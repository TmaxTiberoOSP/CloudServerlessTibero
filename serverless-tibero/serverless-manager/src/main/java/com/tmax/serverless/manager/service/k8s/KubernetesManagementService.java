package com.tmax.serverless.manager.service.k8s;

import com.tmax.serverless.core.annotation.Service;
import com.tmax.serverless.core.context.DBExecuteCommand;
import com.tmax.serverless.core.context.LBExecuteCommand;
import com.tmax.serverless.manager.context.DBContactInfo;
/*
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
*/
import com.tmax.serverless.manager.context.DBInstance;
import lombok.Getter;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class KubernetesManagementService {
    public void init() throws IOException{
        //setDBList();
    }
    /*
    @Deprecated
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
    */
    public boolean executeDBCommand(DBInstance dbInstance, DBExecuteCommand command) {
        String podName = dbInstance.getPodName();
        String tbCommand;

        if (command == DBExecuteCommand.Boot) {
            tbCommand = "tbboot";
        }
        else if (command == DBExecuteCommand.Down)
            tbCommand = "tbdown immediate";
        else {
            log.info("DB Command : not valid ");
            return false;
        }

        log.info("DB Command : " + tbCommand);

        String[] cmd = {"kubectl", "exec", "-it", podName, "-n", "tibero3", "--", "/bin/bash",
                "-c", "export TB_HOME=/tibero;export TB_SID=" + dbInstance.getAlias() + ";" + tbCommand};
        if (executeCommand(cmd))
            log.info ("Success to " + tbCommand + " DB");
        else {
            log.info ("Fail to " + tbCommand + " DB");
            return false;
        }

        return true;
    }

    public boolean executeLBCommand(DBInstance dbInstance, LBExecuteCommand command) {
        String podName = dbInstance.getPodName();
        String lbCommand;

        if (command == LBExecuteCommand.ActiveDB) {
            lbCommand = "active2-db";
        }
        else if (command == LBExecuteCommand.StandbyDB)
            lbCommand = "standby2-db";
        else {
            log.info("LB Command : not valid ");
            return false;
        }

        log.info("LB Command : " + lbCommand);

        String[] cmd = {"kubectl", "label", "pod", podName, "-n", "tibero3", "app="+lbCommand, "--overwrite"};

        if (executeCommand(cmd))
            log.info ("Success to made " + lbCommand);
        else {
            log.info ("Fail to made " + lbCommand);
            return false;
        }

        return true;
    }

    public boolean executeCommand(String[] cmd) {
        Process proc = null;
        boolean success = true;

        try {
            proc = Runtime.getRuntime().exec(cmd);
        }
        catch (IOException e) {
            log.error("Error on exec() method");
            e.printStackTrace();
            success = false;
        }
        finally {
        }
        return success;
    }

}