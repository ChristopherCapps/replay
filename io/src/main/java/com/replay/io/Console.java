package com.replay.io;

import com.replay.io.Persistence.Logging;

import java.io.PrintStream;
import java.util.Formatter;

public class Console {

  public static final String REPLAY_PREFIX = "replay";
  public static final String FATAL_PREFIX = "fatal";
  public static final String CAUSE_PREFIX = "cause";

  private static PrintStream console;

  public static void initialize() {
    console = System.out;
  }

  public static String print(final String format, final Object... args) {
    final String text = (new Formatter().format(format, args)).toString();
    console.printf(text);
    return text;
  }

  public static String println(final String prefix, final String format, final Object... args) {
    final String text = buildPrefixedMessage(prefix, format, args);
    console.println(text);
    return text;
  }

  public static String print(final String prefix, final String format, final Object... args) {
    return print(buildPrefixedMessage(prefix, format, args));
  }

  public static String replay(final String format, final Object... args) {
    return print(REPLAY_PREFIX, format, args);
  }

  public static String replayln(final String format, final Object... args) {
    return println(REPLAY_PREFIX, format, args);
  }

  private static String buildPrefixedMessage(final String prefix, final String format,
      final Object... args) {
    return String.format("%s: %s", prefix, String.format(format, args));
  }

  private static String buildFatalMessage(final String format, final Object... args) {
    return buildPrefixedMessage(FATAL_PREFIX, format, args);
  }

  public static String fatal(final String format, final Object... args) {
    final String message = buildFatalMessage(format, args);
    console.printf(message);
    return message;
  }

  public static String cause(final Throwable t) {
    return print(CAUSE_PREFIX, "%s", t.getMessage());
  }

  public static RuntimeException abend(final String format, final Object... args) {
    Logging.error(fatal(format, args));
    return abend(new RuntimeException("internal tool error"));
  }

  public static RuntimeException abend(final Throwable t, final String format,
      final Object... args) {
    Logging.error(fatal(format + System.lineSeparator(), args));
    Logging.error(cause(t));
    return abend(t);
  }

  public static RuntimeException abend(final Throwable t) {
    Logging.error("<abend>");
    print("\n");
    Shell.Process.terminate();
    return new RuntimeException(t);
  }

}
