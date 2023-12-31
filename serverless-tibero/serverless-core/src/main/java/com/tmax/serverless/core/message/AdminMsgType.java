package com.tmax.serverless.core.message;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdminMsgType {

  public static final int ADMIN_MSG_ADD_DB = 20221000;
  public static final int ADMIN_MSG_ADD_DB_REPLY = 20221001;
  public static final int ADMIN_MSG_ADD_GROUP = 20221002;
  public static final int ADMIN_MSG_ADD_GROUP_REPLY = 20221003;
  public static final int ADMIN_MSG_BOOT_DB = 20221004;
  public static final int ADMIN_MSG_BOOT_DB_REPLY = 20221005;
  public static final int ADMIN_MSG_DOWN_DB = 20221006;
  public static final int ADMIN_MSG_DOWN_DB_REPLY = 20221007;
  public static final int ADMIN_MSG_SCALE_IN = 20221008;
  public static final int ADMIN_MSG_SCALE_IN_REPLY = 20221009;
  public static final int ADMIN_MSG_SCALE_OUT = 20221010;
  public static final int ADMIN_MSG_SCALE_OUT_REPLY = 20221011;
  public static final int ADMIN_MSG_SCALE_IN_COMPLETE = 20221012;
  public static final int ADMIN_MSG_SCALE_IN_COMPLETE_REPLY = 20221013;
  public static final int ADMIN_MSG_SCALE_OUT_COMPLETE = 20221014;
  public static final int ADMIN_MSG_SCALE_OUT_COMPLETE_REPLY = 20221015;
  public static final int ADMIN_MSG_DOWN_MANAGER = 20221016;
  public static final int ADMIN_MSG_DOWN_MANAGER_REPLY = 20221017;
  public static final int ADMIN_MSG_DELETE_DB = 20221018;
  public static final int ADMIN_MSG_DELETE_DB_REPLY = 20221019;
  public static final int ADMIN_MSG_DELETE_GROUP = 20221020;
  public static final int ADMIN_MSG_DELETE_GROUP_REPLY = 20221021;
}
