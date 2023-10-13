package com.tmax.serverless.admin;

import lombok.Getter;
import lombok.Setter;

public class Global {

  @Getter
  private static final Global instance = new Global();

  @Getter
  @Setter
  private Client client;
}
