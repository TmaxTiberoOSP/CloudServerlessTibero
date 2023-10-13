package com.tmax.serverless.admin.utils;

public class ConsoleColors {

  private static final String ESC = "\033";
  private static final String RESET = ESC + "[0m";

  private static String set(String text) {
    return ESC + text + RESET;
  }

  public static String set(String text, Styles style, Colors color) {
    return set(style.value + ";" + color.value + text);
  }

  public static String set(String text, Colors color) {
    return set(text, Styles.REGULAR, color);
  }

  public static String set(String text, Styles style) {
    return set(style.value + "m" + text);
  }

  public enum Colors {
    BLACK("30m"),
    RED("31m"),
    GREEN("32m"),
    YELLOW("33m"),
    BLUE("34m"),
    PURPLE("35m"),
    CYAN("36m"),
    WHITE("37m");

    private final String value;

    Colors(String value) {
      this.value = value;
    }
  }

  public enum Styles {
    REGULAR("[0"),
    BOLD("[1"),
    UNDERLINE("[4");

    private final String value;

    Styles(String value) {
      this.value = value;
    }
  }
}
