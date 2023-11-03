package com.tmax.serverless.manager.k8s;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class k8sTest {

  @Test
  public void testK8sApi() throws IOException, ApiException {
    String kubeConfigPath = "src/test/resources/config";
    ApiClient client = ClientBuilder.kubeconfig(
        KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();

    Configuration.setDefaultApiClient(client);
    CoreV1Api api = new CoreV1Api();

    String fs = "metadata.namespace=tibero";
    V1PodList list = api.listPodForAllNamespaces(
        null, null, fs, null, null, null,
        null, null, null, null
        );

    for (V1Pod pod : list.getItems()) {
      System.out.println(pod.getMetadata().getName());
    }
  }

}
