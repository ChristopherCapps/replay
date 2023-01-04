package com.replay.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.logging.*;

public class Persistence {

    private static Logger LOGGER;
    private static Path USER_ROOT;

    public static void initialize() {
        USER_ROOT = User.initialize();
        LOGGER = Logging.initialize();
    }

    public static class User {

        private static Path initialize() {
            final Path userRootPath = Shell.Paths.getUserHomePath().resolve(".replay");

            if (!Files.exists(userRootPath, LinkOption.NOFOLLOW_LINKS)) {
                try {
                    Files.createDirectory(userRootPath,
                            PosixFilePermissions.asFileAttribute(
                                    Set.of(
                                            PosixFilePermission.OWNER_READ,
                                            PosixFilePermission.OWNER_WRITE,
                                            PosixFilePermission.OWNER_EXECUTE,
                                            PosixFilePermission.GROUP_READ,
                                            PosixFilePermission.GROUP_EXECUTE,
                                            PosixFilePermission.OTHERS_READ,
                                            PosixFilePermission.OTHERS_EXECUTE)));
                } catch (Exception e) {
                    throw Console.abend(e,
                            String.format("unable to access user storage: %s", userRootPath));
                }
            }

            return userRootPath;
        }
    }

    public static class Logging {

        public static void debugSection(final String format, Object... args) {
            debug(StringUtils.repeat('-', 5) + " " + format + " " + StringUtils.repeat('-', 5), args);
        }

        public static void debug(final String format, Object... args) {
            LOGGER.fine(String.format(format, args));
        }

        public static void error(final Throwable t, final String format, Object... args) {
            LOGGER.severe(String.format("%s [cause: %s]", String.format(format, args), t.getMessage()));
        }

        public static void error(final String format, Object... args) {
            LOGGER.severe(String.format(format, args));
        }

        private static Logger initialize() {
            LogManager.getLogManager().reset();

            try {
                final Properties logManagerProperties = new Properties();
                logManagerProperties.setProperty(FileHandler.class.getName() + ".formatter",
                        SimpleFormatter.class.getName());
                //"%1$tc %2$s%n%4$s: %5$s%6$s%n"
                logManagerProperties.setProperty("java.util.logging.SimpleFormatter.format",
                        "%1$tc %5$s%6$s%n");
                logManagerProperties.setProperty("org.hibernate.level", "SEVERE");
                final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                logManagerProperties.store(buffer, null);
                LogManager.getLogManager()
                        .readConfiguration(new ByteArrayInputStream(buffer.toByteArray()));
            } catch (Exception e) {
                throw Console.abend(e, String.format("unable to configure logging"));
            }

            final Logger logger = Logger.getLogger("");
            logger.addHandler(createFileHandler());

            setLevel(logger, Level.ALL);

            return logger;
        }

        public static Path getLogPath() {
            return USER_ROOT.resolve("log");
        }

        private static FileHandler createFileHandler() {
            final Path logPath = getLogPath();
            try {
                final FileHandler fileHandler = new FileHandler(
                        logPath.toString(),
                        FileUtils.ONE_MB * 5,
                        1,
                        true);
                return fileHandler;
            } catch (Exception e) {
                throw Console.abend(e,
                        String.format("unable to access log file: %s", logPath));
            }
        }

        private static void setLevel(final Logger logger, final Level targetLevel) {
            logger.setLevel(targetLevel);
            Arrays.stream(logger.getHandlers()).sequential()
                    .forEach(handler -> handler.setLevel(targetLevel));
        }
    }
}

