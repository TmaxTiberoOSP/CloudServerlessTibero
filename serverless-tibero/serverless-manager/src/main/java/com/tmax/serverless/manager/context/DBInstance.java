package com.tmax.serverless.manager.context;

import com.tmax.serverless.core.context.DBServerlessMode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DBInstance {
  private String alias;
  private DBServerlessMode mode;

}
