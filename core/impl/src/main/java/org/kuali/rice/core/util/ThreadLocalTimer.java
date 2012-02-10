package org.kuali.rice.core.util;

/**
 * Thread Local Timer for Tread Safe Request Logging {@see RequestLoggingFilter}
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ThreadLocalTimer {
    /**
     * startTimeThreadLocal ThreadLocal<Long>
     */
    public static ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<Long>();

    /**
     * Returns startTimeThreadLocal as long since epoch
     * @return long since epoch
     */
    public static long getStartTime() {
        return (Long)startTimeThreadLocal.get();
    }

    /**
     * Sets startTimeThreadLocal to the given long since epoch.
     * @param dateTime long since epoch
     */
    public static void setStartTime(long dateTime) {
        startTimeThreadLocal.set(Long.valueOf(dateTime));
    }

    /**
     * Cleanup, startTimeThreadLocal.remove()
     */
    public static void unset() {
        startTimeThreadLocal.remove();
    }
}
