package com.tmax.serverless.core.message;

import static com.tmax.serverless.core.message.JsonMessage.ResponseResult.SUCCESS;

import com.google.gson.Gson;
import com.tmax.serverless.core.annotation.GsonExclude;
import com.tmax.serverless.core.container.MainContainer;
import java.lang.reflect.Field;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@GsonExclude
public class JsonMessage extends RegularMessage {
  private ResponseResult result = SUCCESS;

  /**
   * Decoding 에 필요한 생성자
   */
  public JsonMessage(RegularMessage header) {
    super(header);
  }

  /**
   * Encoding 에 필요한 생성자
   */
  public JsonMessage(int msgType) {
    super(msgType);
  }

  public enum ResponseResult {
    SUCCESS,
    FAIL
  }

  public void deserialize(Class<?> clazz) {
    Gson gson = MainContainer.getGson();
    Object deserializedObj = gson.fromJson(new String(getPayload()), clazz);

    if (log.isDebugEnabled()) {
      log.debug("{}", deserializedObj);
    }

    for (Field f : this.getClass().getDeclaredFields()) {
      try {
        Field df = deserializedObj.getClass().getDeclaredField(f.getName());
        final boolean originAccessible = f.canAccess(this);

        df.setAccessible(true);
        final Object value = df.get(deserializedObj);

        f.setAccessible(true);
        f.set(this, value);
        f.setAccessible(originAccessible);

        if (log.isDebugEnabled()) {
          log.debug("{}: {}", f.getName(), value);
        }
      } catch (NoSuchFieldException | IllegalAccessException e) {
        log.warn("", e);
        // TODO: 커스텀 에러 핸들링 필요
        throw new RuntimeException(e);
      }
    }
  }

  public boolean isSuccess() {
    return result == SUCCESS;
  }
}
