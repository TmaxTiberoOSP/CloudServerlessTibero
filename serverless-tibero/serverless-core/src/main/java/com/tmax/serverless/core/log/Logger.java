package com.tmax.serverless.core.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

@Slf4j
public class Logger {

  public static final String DEFAULT_DATE_PATTERN = "%d{yyyy_MM_dd}";
  public static final String DEFAULT_LOG_PATTERN = "[%d{HH:mm:ss.SSS}] %-5level [%thread] %C{100} %M - %msg%n";

  public static void initRollingFileLogger(Level level, String name, String filename,
      String filePattern) {
    ConfigurationBuilder<BuiltConfiguration> builder
        = ConfigurationBuilderFactory.newConfigurationBuilder();

    /* Appenders > RollingFile */
    AppenderComponentBuilder rolling = builder.newAppender(name, "RollingFile");
    rolling.addAttribute("fileName", filename);
    rolling.addAttribute("filePattern", filePattern);

    /* Appenders > RollingFile > PatternLayout */
    LayoutComponentBuilder layout = builder.newLayout("PatternLayout");
    layout.addAttribute("pattern", DEFAULT_LOG_PATTERN);
    rolling.add(layout);

    /* Appenders > RollingFile >  Policies > TimeBasedTriggeringPolicy */
    ComponentBuilder<?> triggeringPolicies = builder.newComponent("Policies")
        .addComponent(builder.newComponent("TimeBasedTriggeringPolicy"));
    rolling.addComponent(triggeringPolicies);

    builder.add(rolling);

    /* Loggers > Root > Appender */
    RootLoggerComponentBuilder rootLogger = builder.newRootLogger(level);
    rootLogger.add(builder.newAppenderRef(name));
    builder.add(rootLogger);

    Configurator.reconfigure(builder.build());
  }
}
