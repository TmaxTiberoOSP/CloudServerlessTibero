package com.tmax.serverless.manager.context;

import com.tmax.serverless.core.context.DBServerlessMode;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class DBInstance {
  private static AtomicInteger dbId = new AtomicInteger();

  private Integer id;
  private String dbName;
  private String alias;
  private String ip;
  private int port;
  private String dbUser;
  private String dbPassword;
  private String podName;
  private DBServerlessMode mode;

  public static Integer getNewId() {
    return dbId.incrementAndGet();
  }

  public static Integer decreaseId() {
    return dbId.decrementAndGet();
  }


}
