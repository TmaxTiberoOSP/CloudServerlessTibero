package com.tmax.serverless.core.container;

import com.tmax.serverless.core.annotation.ServerlessMessage;
import com.tmax.serverless.core.annotation.ServerlessMessageMapping;
import com.tmax.serverless.core.message.RegularMessage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.reflections.Reflections;

public class ServiceContainer {

  private final Map<Integer, Constructor<?>> messageConstructors = new HashMap<>();
  private final Map<Integer, Method> serverlessServices = new HashMap<>();

  public ServiceContainer(Reflections reflections) {
    // '@ServerlessMessage' 생성자 매핑
    reflections.getTypesAnnotatedWith(ServerlessMessage.class).forEach(clazz -> {
      try {
        messageConstructors.put(clazz.getAnnotation(ServerlessMessage.class).value(),
            clazz.getConstructor(RegularMessage.class));
      } catch (NoSuchMethodException e) {
        throw new RuntimeException("invalid message class: " + clazz.getName());
      }
    });

    // '@ServerlessMessageMapping' 메소드 매핑
    reflections.getMethodsAnnotatedWith(ServerlessMessageMapping.class).forEach(
        method -> serverlessServices.put(
            method.getAnnotation(ServerlessMessageMapping.class).value()
                .getAnnotation(ServerlessMessage.class).value(), method));

  }

  /**
   * '@DBMessage' 생성자 조회
   */
  public Constructor<?> getMessageConstructor(int type) {
    return messageConstructors.get(type);
  }

  public Method getServerlessService(int type) {
    return serverlessServices.get(type);
  }

}
