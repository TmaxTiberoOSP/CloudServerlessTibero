package com.tmax.serverless.manager.service.k8s;

import com.tmax.serverless.core.annotation.Service;
import com.tmax.serverless.core.context.DBExecuteCommand;
import com.tmax.serverless.core.context.LBExecuteCommand;
import com.tmax.serverless.manager.context.DBContactInfo;
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

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class KubernetesManagementService {
    @Getter
    private final ConcurrentHashMap<String, DBContactInfo> dbContactPool = new ConcurrentHashMap<>();
    public void init() throws IOException, ApiException {
        //setDBList();
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

    public void executeDBCommand(String alias, DBExecuteCommand command) throws IOException {
        DBContactInfo dbContactInfo = dbContactPool.get(alias);
        String podName = dbContactInfo.getPodName();
        String tbCommand;

        if(command == DBExecuteCommand.Boot) {
            tbCommand = "tbboot";
        }
        else if(command == DBExecuteCommand.Down)
            tbCommand = "tbdown";
        else {
            log.info("DB Command : not valid ");
            return ;
        }

        log.info("DB Command : "+tbCommand);

        String[] cmd = {"kubectl", "exec", "-it", podName, "-n", "tibero", "--", "/bin/bash","-c", "export TB_HOME=/tibero;export TB_SID="+alias+";"+tbCommand};
        final Process proc = Runtime.getRuntime().exec(cmd);
    }

    public void executeLBCommand(String alias, LBExecuteCommand command) throws IOException {
        DBContactInfo dbContactInfo = dbContactPool.get(alias);
        String podName = dbContactInfo.getPodName();
        String lbCommand;

        if(command == LBExecuteCommand.ActiveDB) {
            lbCommand = "active-db";
        }
        else if(command == LBExecuteCommand.StandbyDB)
            lbCommand = "standby-db";
        else {
            log.info("LB Command : not valid ");
            return ;
        }

        log.info("LB Command : "+lbCommand);

        String[] cmd = {"kubectl", "label", "pod", podName, "-n", "tibero", "app="+lbCommand, "--overwrite"};
        final Process proc = Runtime.getRuntime().exec(cmd);
    }

}