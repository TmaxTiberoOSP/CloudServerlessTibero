package com.tmax.serverless.admin.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.Level;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

public class LogLevelOption {

  public static String getCandidates() {
    return String.join(", ", new Candidates());
  }

  public static class Candidates extends ArrayList<String> {

    public Candidates() {
      super(Arrays.stream(Level.values()).map(Level::name).collect(Collectors.toList()));
    }
  }

  public static class Converter implements ITypeConverter<Level> {

    @Override
    public Level convert(String value) throws Exception {
      return Optional.ofNullable(Level.getLevel(value)).orElseThrow(() ->
          new TypeConversionException(
              String.format("expected one of [%s] but was '%s'",
                  getCandidates(), value)));
    }
  }
}
