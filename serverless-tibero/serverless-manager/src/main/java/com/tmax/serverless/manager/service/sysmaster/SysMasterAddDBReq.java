package com.tmax.serverless.manager.service.sysmaster;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Builder
@AllArgsConstructor
public class SysMasterAddDBReq {

  private String dbName;
  private String dbUser;
  private String dbPassword;
  private String id;
  private String ip;
  private int port;
  private String name;
  private String type;
  private String userDefinedColor;

  public MultiValueMap<String, String> toMultiValueMap() {
    LinkedMultiValueMap map = new LinkedMultiValueMap<String, String>();

    map.add("dbName", dbName);
    map.add("dbUser", dbUser);
    map.add("dbPassword", dbPassword);
    map.add("id", id);
    map.add("ip", ip);
    map.add("port", port);
    map.add("name", name);
    map.add("type", type);
    map.add("userDefinedColor", userDefinedColor);

    return map;
  }

}
