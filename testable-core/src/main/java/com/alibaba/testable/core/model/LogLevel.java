package com.alibaba.testable.core.model;

/**
 * @author flin
 */
public enum LogLevel {
    /**
     * Mute
     */
    DISABLE(0),
    /**
     * Warn only
     */
    WARN(1),
    /**
     * Info only
     */
    INFO(2),
    /**
     * Show diagnose messages
     */
    DEBUG(3),
    /**
     * Show detail progress logs
     */
    TRACE(4);

    public int level;

    LogLevel(int level) {
        this.level = level;
    }
}
