package com.tmax.serverless.admin.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConsolePrinter {

  public static void gsonPrettyPrint(Object dto) {
    Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .create();

    System.out.println(gson.toJson(dto) + "\n");
  }

  /* TODO: 테이블 형태 출력 유틸 개발 필요 */
}
