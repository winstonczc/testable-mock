package com.alibaba.testable.core.util;

import com.alibaba.testable.core.model.LogLevel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author flin
 */
public class LogUtil {

    private static LogLevel defaultLogLevel = LogLevel.INFO;
    private static LogLevel currentLogLevel = LogLevel.INFO;
    private static FileOutputStream logFileStream = null;

    public static void trace(String msg, Object... args) {
        trace(0, msg, args);
    }

    public static void debug(String msg, Object... args) {
        debug(0, msg, args);
    }

    public static void trace(int indent, String msg, Object... args) {
        if (isTraceEnabled()) {
            String text = String.format(space(indent) + msg + "\n", args);
            System.out.print("[VERBOSE] ");
            System.out.print(text);
            write("[TIP] ");
            write(text);
        }
    }

    public static void debug(int indent, String msg, Object... args) {
        String text = String.format(space(indent) + msg + "\n", args);
        if (currentLogLevel.level >= LogLevel.DEBUG.level) {
            System.out.print("[DIAGNOSE] ");
            System.out.print(text);
        }
        write("[INFO] ");
        write(text);
    }

    public static void info(String msg, Object... args) {
        String text = String.format("[INFO] " + msg + "\n", args);
        if (currentLogLevel.level >= LogLevel.INFO.level) {
            System.out.print(text);
        }
        write(text);
    }

    public static void warn(String msg, Object... args) {
        String text = String.format("[WARN] " + msg + "\n", args);
        if (currentLogLevel.level >= LogLevel.WARN.level) {
            System.err.print(text);
        }
        write(text);
    }

    public static void error(String msg, Object... args) {
        String text = String.format("[ERROR] " + msg + "\n", args);
        System.err.print(text);
        write(text);
    }

    private static String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
        return now.format(formatter);
    }

    /**
     * a pre-check method for reduce verbose parameter calculation
     */
    public static boolean isTraceEnabled() {
        return currentLogLevel.level >= LogLevel.TRACE.level;
    }

    public static void setLevel(LogLevel level) {
        currentLogLevel = level;
    }

    public static void setDefaultLevel(LogLevel level) {
        defaultLogLevel = level;
        resetLogLevel();
    }

    public static void resetLogLevel() {
        currentLogLevel = defaultLogLevel;
    }

    public static void setGlobalLogPath(String logFilePath) {
        try {
            if (PathUtil.createFolder(PathUtil.getFolder(logFilePath))) {
                logFileStream = new FileOutputStream(logFilePath, true);
                debug("Start at %s", new Date().toString());
            }
        } catch (FileNotFoundException e) {
            warn("Failed to create log file %s", logFilePath);
        }
    }

    public static void cleanup() {
        try {
            if (logFileStream != null) {
                debug("Completed at %s", new Date().toString());
                logFileStream.flush();
                logFileStream.close();
            }
        } catch (IOException e) {
            warn("Log file is not closed properly");
        }
    }

    private static String space(int indent) {
        return StringUtil.repeat(" ", indent);
    }

    private static void write(String text) {
        try {
            text = getTime() + " " + text;
            if (logFileStream != null) {
                logFileStream.write(text.getBytes());
            }
        } catch (IOException e) {
            // ignore
        }
    }
}
