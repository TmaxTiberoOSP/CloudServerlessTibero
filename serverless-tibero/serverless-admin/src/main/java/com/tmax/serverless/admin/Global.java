package com.tmax.serverless.admin;

import com.tmax.serverless.core.Client;
import lombok.Getter;
import lombok.Setter;

public class Global {

  @Getter
  private static final Global instance = new Global();

  @Getter
  @Setter
  private Client client;
}
