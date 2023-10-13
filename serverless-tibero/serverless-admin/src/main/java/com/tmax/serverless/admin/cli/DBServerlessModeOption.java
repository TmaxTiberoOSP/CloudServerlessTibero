package com.tmax.serverless.admin.cli;

import com.tmax.serverless.core.context.DBServerlessMode;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

public class DBServerlessModeOption {

  public static String getCandidates() {
    return String.join(", ", new Candidates());
  }

  public static class Candidates extends ArrayList<String> {

    public Candidates() {
      super(Stream.of(DBServerlessMode.values()).map(Enum::name).collect(Collectors.toList()));
    }
  }

  public static class Converter implements ITypeConverter<DBServerlessMode> {

    @Override
    public DBServerlessMode convert(String value) throws Exception {
      try {
        return DBServerlessMode.valueOf(value);
      } catch (IllegalArgumentException e) {
        throw new TypeConversionException(
            String.format("expected one of [%s] but was '%s'",
                getCandidates(), value));
      }
    }
  }
}
