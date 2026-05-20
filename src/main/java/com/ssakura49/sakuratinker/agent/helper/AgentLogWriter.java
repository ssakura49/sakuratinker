package com.ssakura49.sakuratinker.agent.helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class AgentLogWriter {
    private static BufferedWriter writer = null;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static boolean initialized = false;
    private static final String LOG_FILE_NAME = "SakuraAgent.log";

    public static synchronized void resetForNewSession() {
        try {
            Path logsDir = Paths.get("logs");
            if (!Files.exists(logsDir, new LinkOption[0])) {
                Files.createDirectories(logsDir);
            }

            Path logFile = logsDir.resolve("SakuraAgent.log");
            Files.deleteIfExists(logFile);
        } catch (IOException ignored) {
        }

    }

    private static synchronized void initialize() {
        if (!initialized) {
            initialized = true;

            try {
                Path logsDir = Paths.get("logs");
                if (!Files.exists(logsDir, new LinkOption[0])) {
                    Files.createDirectories(logsDir);
                }

                Path logFile = logsDir.resolve("SakuraAgent.log");
                boolean isNewSession = !Files.exists(logFile, new LinkOption[0]) || Files.size(logFile) == 0L;
                writer = new BufferedWriter(new FileWriter(logFile.toFile(), true));
                if (isNewSession) {
                    writer.write("========================================\n");
                    writer.write("SakuraTinker - Agent Log\n");
                    BufferedWriter var10000 = writer;
                    LocalDateTime var10001 = LocalDateTime.now();
                    var10000.write("Started at: " + var10001.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n");
                    writer.write("========================================\n\n");
                } else {
                    BufferedWriter var4 = writer;
                    LocalDateTime var5 = LocalDateTime.now();
                    var4.write("\n--- ClassLoader session joined at: " + var5.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " ---\n\n");
                }

                writer.flush();
            } catch (IOException var3) {
                writer = null;
            }

        }
    }

    public static synchronized void log(String level, String message) {
        initialize();
        if (writer != null) {
            try {
                String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
                writer.write(String.format("[%s] [%s] %s\n", timestamp, level, message));
                writer.flush();
            } catch (IOException var3) {
            }

        }
    }

    public static synchronized void log(String level, String message, Throwable throwable) {
        log(level, message);
        if (writer != null && throwable != null) {
            try {
                BufferedWriter var10000 = writer;
                String var10001 = throwable.getClass().getName();
                var10000.write("  Exception: " + var10001 + ": " + throwable.getMessage() + "\n");
                StackTraceElement[] stackTrace = throwable.getStackTrace();
                int limit = Math.min(stackTrace.length, 10);

                for(int i = 0; i < limit; ++i) {
                    writer.write("    at " + stackTrace[i].toString() + "\n");
                }

                if (stackTrace.length > 10) {
                    writer.write("    ... " + (stackTrace.length - 10) + " more\n");
                }

                writer.flush();
            } catch (IOException var6) {
            }

        }
    }

    public static void info(String message) {
        log("INFO", message);
    }

    public static void warn(String message) {
        log("WARN", message);
    }

    public static void error(String message) {
        log("ERROR", message);
    }

    public static void error(String message, Throwable throwable) {
        log("ERROR", message, throwable);
    }

    public static void debug(String message) {
        log("DEBUG", message);
    }

    public static synchronized void close() {
        if (writer != null) {
            try {
                writer.write("\n========================================\n");
                BufferedWriter var10000 = writer;
                LocalDateTime var10001 = LocalDateTime.now();
                var10000.write("Agent log closed at: " + var10001.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n");
                writer.write("========================================\n");
                writer.close();
            } catch (IOException ignored) {
            }

            writer = null;
        }

    }

    private AgentLogWriter() {
    }
}
