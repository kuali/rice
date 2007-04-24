/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.util;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Logger;

/**
 * Adapts Exception.print stack trace to print its output to a Logger.
 * 
 * 
 */
public class ExceptionUtils {

    /**
     * Logs the stack trace of the given throwable to the given logger.
     * 
     * @param logger
     * @param t
     */
    public static void logStackTrace(Logger logger, Throwable t) {
        Log log = new Log4JLogger(logger);
        logStackTrace(log, t);
    }

    /**
     * Logs the stack trace of the given throwable to the given logger.
     * 
     * @param log
     * @param t
     */
    public static void logStackTrace(Log log, Throwable t) {
        StackTraceElement[] elements = t.getStackTrace();

        StringBuffer trace = new StringBuffer();
        trace.append(t.getClass().getName());

        if (t.getMessage() != null) {
            trace.append(": ");
            trace.append(t.getMessage());
        }

        trace.append("\n");

        for (int i = 0; i < elements.length; ++i) {
            StackTraceElement element = elements[i];

            trace.append("    at ");
            trace.append(describeStackTraceElement(element));
            trace.append("\n");
        }

        log.error(trace.toString());
    }

    /**
     * @param level
     * @return String containing the name of the method at the given level from the top of the stack
     */
    public static String describeStackLevel(Throwable t, int level) {
        return describeStackLevels(t, level + 1, level + 1);
    }

    /**
     * @param fromLevel
     * @param toLevel
     * @return String containing the names of the methods at the given levels from the top of the stack
     */
    public static String describeStackLevels(Throwable t, int fromLevel, int toLevel) {
        if (fromLevel <= 0) {
            throw new IllegalArgumentException("invalid fromLevel (" + fromLevel + " < 0)");
        }
        if (fromLevel > toLevel) {
            throw new IllegalArgumentException("invalid levels (fromLevel " + fromLevel + " > toLevel " + toLevel + ")");
        }

        StackTraceElement[] elements = t.getStackTrace();
        int stackHeight = elements.length;
        if (toLevel >= elements.length) {
            throw new IllegalArgumentException("invalid toLevel (" + toLevel + " >= " + stackHeight + ")");
        }

        StringBuffer result = new StringBuffer();
        for (int level = fromLevel; level <= toLevel; level++) {
            if (result.length() > 0) {
                result.append(" from ");
            }
            result.append(describeStackTraceElement(elements[level]));
        }
        return result.toString();
    }

    /**
     * @param level
     * @return String containing the name of the method at the given level from the top of the stack (not including this method)
     */
    public static String describeStackLevel(int level) {
        return describeStackLevels(level + 1, level + 1);
    }

    /**
     * @param fromLevel
     * @param toLevel
     * @return String containing the names of the methods at the given levels from the top of the stack (not including this method)
     */
    public static String describeStackLevels(int fromLevel, int toLevel) {
        String description = null;
        try {
            throw new RuntimeException("hack");
        }
        catch (RuntimeException e) {
            description = describeStackLevels(e, fromLevel + 1, toLevel + 1);
        }

        return description;
    }


    /**
     * @param element
     * @return String describing the given StackTraceElement
     */
    private static String describeStackTraceElement(StackTraceElement element) {
        if (element == null) {
            throw new IllegalArgumentException("invalid (null) element");
        }

        StringBuffer description = new StringBuffer();

        description.append(element.getClassName());
        description.append(".");
        description.append(element.getMethodName());
        description.append("(");
        description.append(element.getFileName());
        description.append(":");
        description.append(element.getLineNumber());
        description.append(")");

        return description.toString();
    }

    /**
     * Initializes the JDK 1.4 cause of any ServletExceptions in the given cause chain. This is a work-around for the lack of
     * support in the Servlet 2.4 API and Tomcat 5. It allows anything using Throwable.printStackTrace() (e.g., log4j) to include
     * the cause chain. If a ServletException has a rootCause but it's JDK 1.4 cause has already been initialized to null, then the
     * chain ends there. I think Tomcat's exception logging goes beyond this, to follow cause or rootCause depending on the
     * Exception's type and log each one individually instead of using Throwable.printStackTrace(). But that only helps Tomcat's log
     * file.
     * 
     * @param t the head of the cause chain to initialize
     */
    public void initServletExceptionCauses(Throwable t) {
        while (t != null) {
            if (t instanceof ServletException) {
                ServletException se = (ServletException) t;
                try {
                    // Convert Servlet 2.4 API cause to JDK 1.4 cause.
                    se.initCause(se.getRootCause());
                }
                catch (IllegalStateException e) {
                    // Okay, the cause was already initialized.
                    // (IllegalStateException is the only way to distinguish from being initialized to null.)
                }
            }
            t = t.getCause();
        }
    }
}