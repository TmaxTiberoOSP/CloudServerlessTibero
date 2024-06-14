package com.tmax.serverless.manager.service.sysmaster;

import com.google.gson.JsonObject;
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
  private String nonDeletable;
  private String userDefinedColor;

  public JsonObject toJsonBody() {
    JsonObject body = new JsonObject();

    body.addProperty("dbName", dbName);
    body.addProperty("dbUser", dbUser);
    body.addProperty("dbPassword", dbPassword);
    body.addProperty("id", id);
    body.addProperty("ip", ip);
    body.addProperty("port", port);
    body.addProperty("name", name);
    body.addProperty("nonDeletable", nonDeletable);
    body.addProperty("type", type);
    body.addProperty("userDefinedColor", userDefinedColor);

    return body;
  }

}
