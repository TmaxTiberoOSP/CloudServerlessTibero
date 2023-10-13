package com.tmax.serverless.core.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.tmax.serverless.core.annotation.GsonExclude;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GsonExcludeStrategy implements ExclusionStrategy {

  @Override
  public boolean shouldSkipField(FieldAttributes f) {
    /* 필드 자체에 GsonExclude가 설정된 경우 */
    boolean isExcludeField = f.getAnnotation(GsonExclude.class) != null;
    /* 필드 클래스에 GsonExclude가 설정된 경우 */
    boolean isExcludeFieldClass = f.getDeclaringClass().getAnnotation(GsonExclude.class) != null;

    return isExcludeField || isExcludeFieldClass;
  }

  @Override
  public boolean shouldSkipClass(Class<?> clazz) {
    return false;
  }
}
