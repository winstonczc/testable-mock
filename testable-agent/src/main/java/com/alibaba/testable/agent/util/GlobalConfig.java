package com.alibaba.testable.agent.util;

import com.alibaba.testable.core.model.LogLevel;
import com.alibaba.testable.core.model.MockScope;
import com.alibaba.testable.core.util.LogUtil;

import java.io.File;
import java.net.URL;
import java.util.*;

import static com.alibaba.testable.agent.constant.ConstPool.PROPERTY_TEMP_DIR;
import static com.alibaba.testable.agent.constant.ConstPool.PROPERTY_USER_DIR;
import static com.alibaba.testable.core.constant.ConstPool.*;
import static com.alibaba.testable.core.util.PathUtil.createFolder;

/**
 * @author flin
 */
public class GlobalConfig {

    private static final String MUTE = "mute";
    private static final String DEBUG = "debug";
    private static final String TRACE = "trace";
    private static final String DISABLE_LOG_FILE = "null";
    private static final String TESTABLE_AGENT_LOG = "testable-agent.log";
    private static final String DEFAULT_MAVEN_OUTPUT_FOLDER = "target";
    private static final String DEFAULT_GRADLE_OUTPUT_FOLDER = "gradle";

    private static String logFile = null;
    private static String dumpPath = null;
    private static String[] pkgPrefixWhiteList = null;
    private static String[] pkgPrefixBlackList = null;
    private static Map<String, String> mockPkgMapping = null;

    private static String globalMock = "com.selftest.Mock";
    public static MockScope defaultMockScope = MockScope.GLOBAL;
    public static boolean enhanceFinal = false;
    public static boolean enhanceMock = true;
    public static boolean enhanceOmniConstructor = false;
    public static boolean enhanceThreadLocal = false;
    public static boolean checkMockTargetExistence = false;
    public static String innerMockClassName = "Mock";

    public static void setLogLevel(String level) {
        if (level.equals(MUTE)) {
            LogUtil.setDefaultLevel(LogLevel.DISABLE);
        } else if (level.equals(DEBUG)) {
            LogUtil.setDefaultLevel(LogLevel.DEBUG);
        } else if (level.equals(TRACE)) {
            LogUtil.setDefaultLevel(LogLevel.TRACE);
        }
    }

    public static void setLogFile(String path) {
        logFile = path;
    }

    public static String getDumpPath() {
        return (dumpPath == null || dumpPath.isEmpty() || !new File(dumpPath).isDirectory()) ? null : dumpPath;
    }

    public static void setDumpPath(String path) {
        String fullPath = PathUtil.join(System.getProperty(PROPERTY_USER_DIR), path);
        if (createFolder(fullPath)) {
            dumpPath = fullPath;
        }
    }

    public static String[] getPkgPrefixWhiteList() {
        return pkgPrefixWhiteList;
    }

    public static void setPkgPrefixWhiteList(String prefixes) {
        List<String> pkgs = parsePkgPrefixList(prefixes);
        pkgs.add(ClassUtil.toSlashSeparatedName(globalMock));
        pkgPrefixWhiteList = pkgs.toArray(new String[0]);
    }

    public static String[] getPkgPrefixBlackList() {
        return pkgPrefixBlackList;
    }

    public static void setPkgPrefixBlackList(String prefixes) {
        pkgPrefixBlackList = parsePkgPrefixList(prefixes).toArray(new String[0]);
    }

    public static void setupLogRootPath() {
        if (logFile == null) {
            // Use default log file location
            String baseFolder = getBuildOutputFolder();
            if (!baseFolder.isEmpty()) {
                String logFilePath = PathUtil.join(baseFolder, TESTABLE_AGENT_LOG);
                LogUtil.setGlobalLogPath(logFilePath);
                LogUtil.trace("Generate testable agent log file at: %s", logFilePath);
            }
        } else if (!DISABLE_LOG_FILE.equals(logFile)) {
            // Use custom log file location
            LogUtil.setGlobalLogPath(PathUtil.join(System.getProperty(PROPERTY_USER_DIR), logFile));
        }
    }

    private static String getBuildOutputFolder() {
        String contextFolder = System.getProperty(PROPERTY_USER_DIR);
        URL rootResourceFolder = Object.class.getResource(SLASH);
        if (rootResourceFolder != null) {
            return PathUtil.getFirstLevelFolder(contextFolder, rootResourceFolder.getPath());
        } else if (PathUtil.folderExists(PathUtil.join(contextFolder, DEFAULT_MAVEN_OUTPUT_FOLDER))) {
            return PathUtil.join(contextFolder, DEFAULT_MAVEN_OUTPUT_FOLDER);
        } else if (PathUtil.folderExists(PathUtil.join(contextFolder, DEFAULT_GRADLE_OUTPUT_FOLDER))) {
            return PathUtil.join(contextFolder, DEFAULT_GRADLE_OUTPUT_FOLDER);
        } else {
            return System.getProperty(PROPERTY_TEMP_DIR);
        }
    }

    public static String getGlobalMock() {
        return globalMock;
    }

    public static void setGlobalMock(String globalMock) {
        GlobalConfig.globalMock = globalMock;
    }

    public static void addMockPackageMapping(String originPkg, String mockClassPkg) {
        if (mockPkgMapping == null) {
            mockPkgMapping = new LinkedHashMap<String, String>(5);
        }
        mockPkgMapping.put(originPkg + DOT, mockClassPkg + DOT);
    }

    public static Map<String, String> getMockPackageMapping() {
        return mockPkgMapping;
    }

    private static List<String> parsePkgPrefixList(String prefixes) {
        List<String> whiteList = new ArrayList<String>();
        for (String p : prefixes.split(COMMA)) {
            whiteList.add(ClassUtil.toSlashSeparatedName(p.endsWith(DOT) ? p : p + DOT));
        }
        return whiteList;
    }
}
