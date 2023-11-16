package com.tmax.serverless.manager.service;

import com.tmax.serverless.core.annotation.Autowired;
import com.tmax.serverless.core.annotation.Service;
import com.tmax.serverless.core.container.PropertyContainer;
import com.tmax.serverless.manager.context.DBContactInfo;
import com.tmax.serverless.manager.context.DBInstance;
import com.tmax.serverless.manager.context.DBInstancePool;
import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Streams;

import javax.print.URIException;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class KubernetesManagementService {
    @Getter
    private final ConcurrentHashMap<String, DBContactInfo> dbContactPool = new ConcurrentHashMap<>();
    public void init() throws IOException, ApiException {
        setDBList();
    }
    public void setDBList() throws IOException, ApiException {
        int aliasIndex = 0;
        InputStream kubeConfigIn = KubernetesManagementService.class
                .getClassLoader()
                .getResourceAsStream(("config"));
        BufferedReader kubeConfigBuf = new BufferedReader((new InputStreamReader(kubeConfigIn)));

        ApiClient client = ClientBuilder.kubeconfig(
                KubeConfig.loadKubeConfig(kubeConfigBuf)).build();

        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();

        String filesSelector = "metadata.namespace=tibero";

        V1PodList list = api.listPodForAllNamespaces(
                null, null, filesSelector, null, null, null,
                null, null, null, null
        );

        for (V1Pod pod : list.getItems()) {
            String alias = "tac"+ aliasIndex;
            DBContactInfo dbContactInfo = new DBContactInfo(alias, pod.getMetadata().getName(), pod.getStatus().getPodIP(), pod.getStatus().getHostIP());
            dbContactPool.put(alias, dbContactInfo);
            System.out.println(dbContactInfo.getAlias() + " " + dbContactInfo.getPodName() + " " + dbContactInfo.getPodIP() + " " + dbContactInfo.getHostIP());
            aliasIndex++;
        }
    }

    public void executeDBCommand(String alias, String command) throws IOException {
        DBContactInfo dbContactInfo = dbContactPool.get(alias);
        String podName = dbContactInfo.getPodName();
        /*
        command 제한 두기
         */
        String[] cmd = {"kubectl", "exec", "-it", podName, "-n", "tibero", "--", "/bin/bash","-c", "export TB_HOME=/tibero;export TB_SID="+alias+";"+command};
        final Process proc = Runtime.getRuntime().exec(cmd);
    }

    public void addDBtoLB(String alias) throws IOException {
        DBContactInfo dbContactInfo = dbContactPool.get(alias);
        String podName = dbContactInfo.getPodName();
        /*
        command 제한 두기
         */
        String[] cmd = {"kubectl", "label", "pod", podName, "-n", "tibero", "app=zeta-dbc", "--overwrite"};
        final Process proc = Runtime.getRuntime().exec(cmd);
    }

    public void removeDBfromLB(String alias) {

    }
}