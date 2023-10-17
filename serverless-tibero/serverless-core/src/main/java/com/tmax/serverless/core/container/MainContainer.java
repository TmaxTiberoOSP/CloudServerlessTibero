package com.tmax.serverless.core.container;

import static com.tmax.serverless.core.config.ServerlessConst.PACKAGE_GROUP_NAME;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tmax.serverless.core.util.GsonExcludeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

@Slf4j
public class MainContainer {
  public static final Reflections reflections = new Reflections(PACKAGE_GROUP_NAME,
      Scanners.values());

  private final PropertyContainer propertyContainer = new PropertyContainer();
  private final BeanContainer beanContainer = new BeanContainer(reflections, propertyContainer);
  private final ServiceContainer serviceContainer = new ServiceContainer(reflections);

  private final Gson gson = new GsonBuilder()
      .setExclusionStrategies(new GsonExcludeStrategy())
      .create();

  public static MainContainer getInstance() {
    return LazyHolder.container;
  }

  public static PropertyContainer getPropertyContainer() {
    return getInstance().propertyContainer;
  }

  public static BeanContainer getBeanContainer() {
    return getInstance().beanContainer;
  }

  public static ServiceContainer getServiceContainer() {
    return getInstance().serviceContainer;
  }

  public static Gson getGson() {
    return getInstance().gson;
  }

  private static class LazyHolder {

    private static final MainContainer container;

    static {
      log.info("MainContainer init!");
      container = new MainContainer();
      log.info("after creating MainContainer!");
      log.info("gson of MainContainer: {}", container.gson);
      container.beanContainer.init();
    }
  }
}
