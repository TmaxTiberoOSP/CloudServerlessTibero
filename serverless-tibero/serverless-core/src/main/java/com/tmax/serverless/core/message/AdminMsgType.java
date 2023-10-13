package com.tmax.serverless.core.message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdminMsgType {

  public static final int ADMIN_MSG_ADD_DB = 20221000;
  public static final int ADMIN_MSG_ADD_DB_REPLY = 20221001;
  public static final int ADMIN_MSG_DB_BOOT = 20221002;
  public static final int ADMIN_MSG_DB_BOOT_REPLY = 20221003;
}
