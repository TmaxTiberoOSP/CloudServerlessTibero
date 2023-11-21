package com.tmax.serverless.manager.service.sysmaster;

import com.tmax.serverless.core.annotation.Service;
import com.tmax.serverless.core.annotation.Value;
import com.tmax.serverless.manager.context.DBInstance;
import java.net.URI;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class SysMasterService {
  @Value("serverless.sysmaster.url")
  private String sysMasterUri;
  @Value("serverless.sysmaster.type")
  private String monitoringType;
  @Value("serverless.sysmaster.color")
  private String monitoringColor;

  public boolean addDBToSysMaster(DBInstance newDB) {
    SysMasterAddDBReq req = SysMasterAddDBReq.builder()
        .dbName(newDB.getDbName())
        .dbUser(newDB.getDbUser())
        .dbPassword(newDB.getDbPassword())
        .id(newDB.getId().toString())
        .ip(newDB.getIp())
        .port(newDB.getPort())
        .name(newDB.getAlias())
        .type(monitoringType)
        .userDefinedColor(monitoringColor)
        .build();

    URI uri = UriComponentsBuilder
        .fromUriString(sysMasterUri)
        .build(false).encode().toUri();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "debug");
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> httpEntity = new HttpEntity<>(req.toJsonBody().toString(), headers);

    ResponseEntity<String> responseEntity = new RestTemplate().exchange(
        uri,
        HttpMethod.POST,
        httpEntity,
        String.class
    );

    log.info("addDBToSysMaster result: {}", responseEntity);

    if (responseEntity.getStatusCode() != HttpStatus.OK)
      return false;
    else
      return true;
  }


  public boolean addGroupToSysMaster(String groupName, ArrayList<String> monitoringList) {
    SysMasterAddGroupReq req = SysMasterAddGroupReq.builder()
        .name(groupName)
        .monitortingList(monitoringList)
        .build();

    URI uri = UriComponentsBuilder
        .fromUriString(sysMasterUri)
        .build(false).encode().toUri();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "debug");
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> httpEntity = new HttpEntity<>(req.toJsonBody().toString(), headers);

    ResponseEntity<String> responseEntity = new RestTemplate().exchange(
        uri,
        HttpMethod.POST,
        httpEntity,
        String.class
    );

    log.info("addGroupToSysMaster result: {}", responseEntity);

    if (responseEntity.getStatusCode() != HttpStatus.OK)
      return false;
    else
      return true;
  }


}
