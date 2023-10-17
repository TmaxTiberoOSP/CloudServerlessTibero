package com.tmax.serverless.core.container;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import com.tmax.serverless.core.annotation.Autowired;
import com.tmax.serverless.core.annotation.Bean;
import com.tmax.serverless.core.annotation.Component;
import com.tmax.serverless.core.annotation.Value;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

@Slf4j
public class BeanContainer {
  private final Reflections reflections;
  private final PropertyContainer propertyContainer;
  private final Map<String, Map<String, Object>> beans = new HashMap<>();
  private final Map<String, Object> namedBeans = new HashMap<>();
  private final Map<String, List<Object>> beansWithLazyAutowired = new HashMap<>();
  private final Map<String, Method> lazyBeans = new HashMap<>();
  private boolean isInit = false;

  public BeanContainer(Reflections reflections, PropertyContainer properties) {
    this.reflections = reflections;
    this.propertyContainer = properties;
  }

  public void init() {
    log.info("BeanContainer init!");
    reflections.get(TypesAnnotated.getAllIncluding(Component.class.getName()).asClass()).stream()
        .filter(clazz -> !clazz.isAnnotation())
        .map(clazz -> {
          String className = clazz.getName();
          try {
            // @Component 객체 초기화
            Object bean = clazz.getDeclaredConstructor().newInstance();

            // @Value 값 주입
            processValue(bean);
            put(className, bean);

            return bean;
          } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Component initialization failed: " + className);
          }
        }).collect(Collectors.toList())
        .forEach(this::processAutowired);

    isInit = true;
    if (log.isDebugEnabled()) {
      log.debug(beans.toString());
    }
  }

  /**
   * '@Value' 필드 값 주입
   *
   * @param bean
   */
  @SneakyThrows
  public void processValue(Object bean) {
    // Value 주입
    for (Field field : bean.getClass().getDeclaredFields()) {
      Value aValue = field.getAnnotation(Value.class);

      if (aValue != null) {
        field.setAccessible(true);
        String key = field.getAnnotation(Value.class).value();

        try {
          Object value = propertyContainer.get(field.getType(), key);
          field.set(bean, value);
          field.setAccessible(false);

          if (log.isDebugEnabled()) {
            log.debug("{}:{}", field, value);
          }
        } catch (Exception e) {
          throw new RuntimeException("Value field injection failed: " + key);
        }
      }
    }
  }

  /**
   * '@Autowired' 필드 값 주입
   *
   * @param bean
   */
  @SneakyThrows
  public void processAutowired(Object bean) {
    for (Field field : bean.getClass().getDeclaredFields()) {
      Autowired autowired = field.getAnnotation(Autowired.class);

      if (autowired != null) {
        field.setAccessible(true);

        String className = field.getType().getName();
        String fieldName = autowired.nameMatching() ? field.getName()
            : autowired.value().isEmpty() ? className
                : autowired.value();
        Object value;

        if (!has(className, fieldName)) {
          log.info("no has bean: " + className + ", " + fieldName);
          initializeBean(className, fieldName);
        }

        if (!lazyBeans.containsKey(fieldName)) {
          value = get(className, fieldName);

          field.set(bean, value);
          field.setAccessible(false);
          if (log.isDebugEnabled()) {
            log.debug("{}:{}", field, value);
          }
        } else {
          putInBeansWithLazyAutowired(fieldName, bean);
        }
      }
    }
  }

  private void putInBeansWithLazyAutowired(String fieldName, Object bean) {
    List<Object> beanList = getFromBeansWithLazyAutowired(fieldName);
    if (beanList != null) {
      beanList.add(bean);
    } else {
      List<Object> newBeanList = new ArrayList<>();
      newBeanList.add(bean);
      beansWithLazyAutowired.put(fieldName, newBeanList);
    }
  }

  private List<Object> getFromBeansWithLazyAutowired(String fieldName) {
    return beansWithLazyAutowired.get(fieldName);
  }

  /**
   * '@Bean' 객체 실제 초기화 할 때 사용
   *
   * @param method
   */
  private void initializeBean(Method method) {
    Object instance = get(method.getDeclaringClass().getName());

    Bean b = method.getAnnotation(Bean.class);
    String className = method.getReturnType().getName();
    String fieldName = b.nameMatching() ? method.getName()
        : b.name().isEmpty() ? className
            : b.name();

    if (has(className, fieldName)
        || (!isInit && b.lazy())) {
      lazyBeans.put(fieldName, method);
      return;
    }

    try {
      boolean originAccessible = method.isAccessible();
      Parameter[] methodParams = method.getParameters();
      Object bean;

      if (!originAccessible) {
        method.setAccessible(true);
      }

      if (methodParams.length > 0) {
        Object[] params = Arrays.stream(methodParams).map(p -> {
          if (!p.isAnnotationPresent(Value.class)) {
            throw new RuntimeException("Bean invalid param. need @Value");
          }

          Value v = p.getAnnotation(Value.class);
          String typeName = p.getType().getName();
          String value = v.value().isEmpty() ? typeName : v.value();

          // property 경우
          if (propertyContainer.isPropertyFormat(value)) {
            return propertyContainer.get(p.getType(), value);
          }
          // bean 경우
          else {
            if (!has(typeName, value)) {
              initializeBean(p.getType().getName(), value);
            }
            return get(typeName, value);
          }
        }).toArray();

        bean = method.invoke(instance, params);
      } else {
        bean = method.invoke(instance);
      }

      method.setAccessible(originAccessible);

      put(className, fieldName, bean);
      if (!b.name().isEmpty()) {
        namedBeans.put(fieldName, bean);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Bean initialization failed: " + className + " " + fieldName);
    }
  }

  /**
   * 특정 Bean 초기화하려 할 때 사용 (초기화 되지 않은 Bean 주입받으려 할 때 필요)
   *
   * @param className
   * @param fieldName
   */
  private void initializeBean(String className, String fieldName) {
    reflections.getMethodsAnnotatedWith(Bean.class).stream()
        .filter(method -> {
          Bean b = method.getAnnotation(Bean.class);
          String returnTypeName = method.getReturnType().getName();
          String name = b.nameMatching() ? method.getName()
              : b.name().isEmpty() ? returnTypeName
                  : b.name();

          return returnTypeName.equals(className) && name.equals(fieldName);
        }).forEach(this::initializeBean);
  }

  /**
   * Lazy load 설정된 Bean 객체 초기화 후 매핑
   *
   * @param name
   */
  public void initializeLazyBean(String name) {
    Method method = lazyBeans.get(name);
    if (method != null) {
      initializeBean(method);
      lazyBeans.remove(name);
    }

    List<Object> beanList = beansWithLazyAutowired.get(name);
    if (beanList != null) {
      Iterator<Object> beansIterator = beanList.iterator();
      while (beansIterator.hasNext()) {
        Object bean = beansIterator.next();
        processAutowired(bean);
        beansIterator.remove();
      }
      beansWithLazyAutowired.remove(name);
    }
  }

  public void put(String className, Object value) {
    put(className, className, value);

    if (log.isDebugEnabled()) {
      log.debug("component - {} = {}", className, value);
    }
  }

  private void put(String className, String fieldName, Object value) {
    Map<String, Object> map;

    if (!beans.containsKey(className)) {
      map = new HashMap<>();
      beans.put(className, map);
    } else {
      map = beans.get(className);
    }

    map.put(fieldName, value);

    if (log.isDebugEnabled() && !className.equals(fieldName)) {
      log.debug("bean - {}:{} = {}", className, fieldName, value);
    }
  }

  private boolean has(String className, String fieldName) {
    if (!beans.containsKey(className)) {
      return false;
    }

    return beans.get(className).containsKey(fieldName);
  }

  public Object get(String className) {
    if (!beans.containsKey(className)) {
      throw new RuntimeException("bean of that type does not exist.");
    }

    return beans.get(className).values().stream().findFirst()
        .orElseThrow(() -> new RuntimeException("bean does not exist."));
  }

  public Object get(String className, String fieldName) {
    if (!beans.containsKey(className)) {
      throw new RuntimeException("bean of that type does not exist: " + className);
    }

    if (!beans.get(className).containsKey(fieldName)) {
      throw new RuntimeException(
          "bean of that field does not exist: " + className + " " + fieldName);
    }

    return beans.get(className).get(fieldName);
  }

  public <T> T get(Class<T> clazz) {
    String className = clazz.getName();

    if (!beans.containsKey(className)) {
      throw new RuntimeException("bean of that type does not exist.");
    }

    Map<String, Object> map = beans.get(className);
    Optional<Object> bean = map.values().stream().findFirst();

    return bean.map(clazz::cast)
        .orElseThrow(() -> new RuntimeException("bean does not exist."));
  }
}
