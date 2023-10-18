package com.tmax.serverless.core.message;

import static com.tmax.serverless.core.message.ReturnCode.SUCCESS;

import com.google.gson.Gson;
import com.tmax.serverless.core.annotation.GsonExclude;
import com.tmax.serverless.core.container.MainContainer;
import java.lang.reflect.Field;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@GsonExclude
public class JsonMessage extends RegularMessage {
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

  public void deserialize(Class<?> clazz) {
    log.info("deserialize1");
    Gson gson = MainContainer.getGson();
    log.info("deserialize2");
    Object deserializedObj = gson.fromJson(new String(getPayload()), clazz);
    log.info("deserialize3: {}", deserializedObj);
    if (log.isDebugEnabled()) {
      log.debug("{}", deserializedObj);
    }

    Class<?> currentClazz = this.getClass();
    Class<?> currentDeserializedClazz = deserializedObj.getClass();
    do {
      log.info("deserialize4: " + currentClazz);
      for (Field f : currentClazz.getDeclaredFields()) {
        try {
          Field df = currentDeserializedClazz.getDeclaredField(f.getName());
          final boolean originAccessible = f.canAccess(this);

          df.setAccessible(true);
          final Object value = df.get(deserializedObj);
          log.info("f: " + f.getName() + ", v: " + value);
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
      currentClazz = currentClazz.getSuperclass();
      currentDeserializedClazz = currentDeserializedClazz.getSuperclass();
    } while (currentClazz != JsonMessage.class);
  }

}
