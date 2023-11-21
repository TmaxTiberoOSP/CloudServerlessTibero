package com.tmax.serverless.manager.service.sysmaster;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class SysMasterAddGroupReq {
  private String name;
  private ArrayList<String> monitortingList;

  public JsonObject toJsonBody() {
    JsonObject body = new JsonObject();
    JsonArray list = new JsonArray();
    for (String dbId : monitortingList)
      list.add(dbId);

    body.addProperty("name", name);
    body.add("resources", list);

    return body;
  }
}
