package com.replay.io;

import com.replay.io.Persistence.Logging;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Shell {

  public static class Process {

    public static void terminate() {
      System.exit(1);
    }
  }

  public static class Paths {

    public static Path getHomePath() {
      final String envHomeVar = SystemUtils.getEnvironmentVariable("PHOTO_VAULT_HOME",
          "user.dir");
      return Path.of(envHomeVar);
    }

    public static Path getPathForFile(final String filepath) {
      return (new File(filepath)).toPath();
    }

    public static Path getAbsolutePathForFile(final String filepath) {
      return getPathForFile(filepath).toAbsolutePath();
    }

    public static Path getAbsolutePathForExistingFile(final String filepath) {
      final Path path = getAbsolutePathForFile(filepath);
      if (Files.exists(path)) {
        return path;
      } else {
        throw new RuntimeException(
            String.format("File '%s' is required but does not exist", path));
      }
    }

    public static Path getWorkingDirectoryPath() {
      return getAbsolutePathForFile(SystemUtils.USER_DIR);
    }

    public static Path getUserHomePath() {
      return getAbsolutePathForFile(SystemUtils.USER_HOME);
    }
  }

  public static class Environment {

    public static String getVariable(final String name, final String defaultValue) {
      final String value = SystemUtils.getEnvironmentVariable(name, defaultValue);
      Logging.debug("env: %s=[%s]|'%s'", name, defaultValue, value);
      return value;
    }

    public static void setVariable(final String name, final String value) {
      System.setProperty(name, value);
    }
  }

  public static Platform getPlatform() {
    if (SystemUtils.IS_OS_MAC) {
      return Platform.MACOS;
    } else if (SystemUtils.IS_OS_LINUX) {
      return Platform.LINUX;
    } else if (SystemUtils.IS_OS_WINDOWS) {
      return Platform.WINDOWS;
    } else {
      throw new RuntimeException("Unrecognized platform: " + SystemUtils.OS_NAME);
    }
  }

  public enum Platform {
    LINUX,
    MACOS,
    WINDOWS
  }

}
