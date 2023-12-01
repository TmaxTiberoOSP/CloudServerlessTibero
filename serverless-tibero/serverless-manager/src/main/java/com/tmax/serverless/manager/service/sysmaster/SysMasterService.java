package com.tmax.serverless.manager.service.sysmaster;

import static com.tmax.serverless.core.config.ServerlessConst.SYS_MASTER_DB_COLOR;
import static com.tmax.serverless.core.config.ServerlessConst.SYS_MASTER_DB_TYPE;
import static com.tmax.serverless.core.config.ServerlessConst.SYS_MASTER_URL;

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
  private String sysMasterUri = SYS_MASTER_URL;
  private String monitoringType = SYS_MASTER_DB_TYPE;
  private String monitoringColor = SYS_MASTER_DB_COLOR;

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

    String addDBUri = sysMasterUri + "/resources";
    log.info("addDBUri:" + addDBUri);
    URI uri = UriComponentsBuilder
        .fromUriString(addDBUri)
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

    String addGroupUri = sysMasterUri + "/zeta-tac-groups";
    log.info("addGroupUri:" + addGroupUri);
    URI uri = UriComponentsBuilder
        .fromUriString(addGroupUri)
        .build(false).encode().toUri();

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "debug");
    headers.setContentType(MediaType.APPLICATION_JSON);
    log.info("addGroupToSysMaster req: {}", req);
    log.info("addGroupToSysMaster req json: {}", req.toJsonBody());
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
