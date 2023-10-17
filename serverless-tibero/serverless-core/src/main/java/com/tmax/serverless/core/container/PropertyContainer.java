package com.tmax.serverless.core.container;

import static org.yaml.snakeyaml.env.EnvScalarConstructor.ENV_FORMAT;
import static org.yaml.snakeyaml.env.EnvScalarConstructor.ENV_TAG;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.env.EnvScalarConstructor;
import org.yaml.snakeyaml.representer.Representer;

@Slf4j
public class PropertyContainer {

  public static final Pattern REP_FORMAT = Pattern.compile("\\$\\{(.*?)\\}");

  private final Map<String, Object> propMap;

  public PropertyContainer() {
    log.info("creating PropertyContainer!");
    LoaderOptions loaderOptions = new LoaderOptions();
    loaderOptions.setAllowDuplicateKeys(false);

    DumperOptions dumperOptions = new DumperOptions();

    Yaml yaml = new Yaml(new EnvScalarConstructor(), new Representer(dumperOptions),
        dumperOptions, loaderOptions);
    yaml.addImplicitResolver(ENV_TAG, ENV_FORMAT, "$");

    propMap = yaml.load(PropertyContainer.class
        .getClassLoader()
        .getResourceAsStream("application.yaml"));

    log.info("go processReplacement");
    processReplacement(propMap);

    if (log.isDebugEnabled()) {
      log.debug(propMap.toString());
    }
  }


  @SuppressWarnings("unchecked")
  private void processReplacement(Map<String, Object> map) {
    map.forEach((k, v) -> {
      if (v instanceof Map) {
        processReplacement((Map<String, Object>) v);
      } else if (v instanceof String) {
        int lastIndex = 0;
        StringBuilder output = new StringBuilder();
        String origin = (String) v;

        Matcher matcher = REP_FORMAT.matcher(origin);
        while (matcher.find()) {
          var g = matcher.group();
          var g1 = matcher.group(1);
          log.info("group:" + g);
          log.info("group(1):" + g1);
          String converted = (String) get(matcher.group(1));

          output.append(origin, lastIndex, matcher.start())
              .append(converted);

          lastIndex = matcher.end();
        }

        if (lastIndex != 0) {
          output.append(origin, lastIndex, origin.length());
          map.put(k, output.toString());

          if (log.isDebugEnabled()) {
            log.debug("{} -> {}", origin, output);
          }
        }
      }
    });
  }

  @SuppressWarnings("unchecked")
  private Object get(String key) {
    Matcher matcher = REP_FORMAT.matcher(key);
    if (matcher.find()) {
      key = matcher.group(1);
    }

    Map<String, Object> map = propMap;
    Object value = null;

    for (String k : key.split("\\.")) {
      if (map == null || !map.containsKey(k)) {
        return null;
      }

      value = map.get(k);
      if (value instanceof Map) {
        map = (Map<String, Object>) value;
      } else {
        map = null;
      }
    }

    return value;
  }

  public Object get(Class<?> type, String key) {
    Object value = get(key);

    if (value == null) {
      throw new RuntimeException("not exited value: " + key);
    } else if (type.isAssignableFrom(String.class)) {
      return value;
    } else if (type.isAssignableFrom(int.class)) {
      return Integer.parseInt((String) value);
    }

    throw new RuntimeException("invalid type: " + key + "=" + value);
  }

  public String getString(String key) {
    return (String) get(String.class, key);
  }

  public Properties getProperties(String prefix) {
    Properties properties = new Properties();
    properties.putAll((Map<?, ?>) get(prefix));

    return properties;
  }

  public boolean isPropertyFormat(String key) {
    return REP_FORMAT.matcher(key).find();
  }
}
