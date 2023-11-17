package com.tmax.serverless.manager.context;

import com.tmax.serverless.core.context.DBServerlessMode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DBInstance {
  private String dbName;
  private String alias;
  private String ip;
  private int port;
  private String dbUser;
  private String dbPassword;
  private DBServerlessMode mode;

}
