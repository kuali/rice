/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.util;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Deque;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

/**
 * Performance monitoring log utility.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @version 2.4
 */
public class ProcessLogger {

    private static final Logger LOG = Logger.getLogger(ProcessLogger.class);

    /**
     * Thread local mapping of process status trackers.
     */
    private static final ThreadLocal<Map<String, ProcessStatus>> TL_STAT = new ThreadLocal<Map<String, ProcessStatus>>();

    /**
     * Tracks statistics and execution count for common operations.
     * 
     * <p>
     * ProcessCounter is typically used to clock calls to external systems, such as database and web
     * service requests.
     * </p>
     */
    private static class ProcessCounter {

        /**
         * The name of the counter.
         */
        private final String name;

        /**
         * Stack of the start times for currently active executions.
         */
        private Deque<Long> start = new java.util.LinkedList<Long>();

        /**
         * The total count of all executions.
         */
        private long count;

        /**
         * The minimum execution time, calculated among all executions within the same process.
         */
        private long min;

        /**
         * The maximum execution time, calculated among all executions within the same process.
         */
        private long max;

        /**
         * The average (mean) execution time, calculated among all executions within the same
         * process.
         */
        private long avg;

        /**
         * A detailed description of the execution responsible for the max execution time.
         */
        private String longest;

        /**
         * Create a new counter for use within the current process.
         * 
         * @param name The name of the counter.
         */
        private ProcessCounter(String name) {
            this.name = name;
        }
    }

    /**
     * Tracks the status of one more processes actively being monitoring on the current Thread.
     * 
     * <p>
     * This class represents the internal state of most of ProcessLogger's operations.
     * </p>
     */
    private static class ProcessStatus {

        /**
         * The name of this process trace.
         */
        private final String name;

        /**
         * The start time the process started.
         */
        private final long startTime;

        /**
         * Heap free space, recorded at the start of the process.
         */
        private final long startFree;

        /**
         * Heap total available space, recorded at the start of the process.
         */
        private final long startTot;

        /**
         * Heap max available space, recorded at the start of the process.
         */
        private final long startMax;

        /**
         * The time of the last trace message reported on this process.
         */
        private long lastTime;

        /**
         * Heap free space, recorded at the time the last trace message was reported on this
         * process.
         */
        private long lastFree;

        /**
         * Heap total available space, recorded at the time the last trace message was reported on
         * this process.
         */
        private long lastTot;

        /**
         * Heap max available space, recorded at the time the last trace message was reported on
         * this process.
         */
        private long lastMax;

        /**
         * The time, in milliseconds, elapsed between the last trace message reported and the
         * previous report.
         */
        private long diffTime;

        /**
         * The difference in heap free space between the last trace message reported and the
         * previous report.
         */
        private long diffFree;

        /**
         * The difference in heap total space between the last trace message reported and the
         * previous report.
         */
        private long diffTot;

        /**
         * Internal mapping of counters tracked on this process.
         */
        private Map<String, ProcessCounter> counters = new java.util.LinkedHashMap<String, ProcessCounter>();

        /**
         * Internal mapping of ntrace counters.
         * 
         * <p>
         * These counters are used for detecting excessive execution counts of operations that are
         * typically fast, but may become expensive with a high number of executions. For example,
         * ntrace counts may be used to track excessive object creation.
         * </p>
         */
        private Map<String, Long> ntraceCount = new java.util.TreeMap<String, Long>();

        /**
         * Internal mapping of ntrace thresholds.
         * 
         * <p>
         * Associated with ntraceCount, this map defines thresholds below which not to report counts.
         * </p>
         */
        private Map<String, Long> ntraceThreshold = new java.util.HashMap<String, Long>();

        /**
         * Verbose operation flag.
         * 
         * <p>
         * When true, all trace messages will be included regardless of elapsed time. When false,
         * trace messages will only be included when elapsed time is 1 or more milliseconds.
         * </p>
         * 
         * <p>
         * By default, verbose is true when debugging is enabled via {@link #LOG}.
         * </p>
         */
        private boolean verbose = LOG.isDebugEnabled();

        /**
         * StringBuilder for constructing the trace message for reporting via log4j.
         */
        private StringBuilder traceBuffer = new StringBuilder();

        /**
         * StringBuilder for collecting extra information to included at the end of the trace.
         */
        private StringBuilder extra = new StringBuilder();

        /**
         * Create a status tracker for a new process.
         * 
         * @param name The name of the process.
         */
        private ProcessStatus(String name) {
            this.name = name;
            this.startTime = System.currentTimeMillis();
            this.startFree = Runtime.getRuntime().freeMemory();
            this.startTot = Runtime.getRuntime().totalMemory();
            this.startMax = Runtime.getRuntime().maxMemory();
            this.lastTime = startTime;
            this.lastFree = startFree;
            this.lastTot = startTot;
            this.lastMax = startMax;
        }

        /**
         * Report that time has elapsed on the process.
         * <p>
         * This method updates the following fields based on the current system runtime.
         * <ul>
         * <li>{@link #diffTime}</li>
         * <li>{@link #diffFree}</li>
         * <li>{@link #diffTot}</li>
         * <li>{@link #lastTime}</li>
         * <li>{@link #lastFree}</li>
         * <li>{@link #lastTot}</li>
         * <li>{@link #lastMax}</li>
         * </ul>
         */
        private void elapse() {
            long nTime = System.currentTimeMillis();
            long nFree = Runtime.getRuntime().freeMemory();
            long nTot = Runtime.getRuntime().totalMemory();
            long nMax = Runtime.getRuntime().maxMemory();
            diffTime = nTime - lastTime;
            diffFree = nFree - lastFree;
            diffTot = nTot - lastTot;
            lastTime = nTime;
            lastFree = nFree;
            lastTot = nTot;
            lastMax = nMax;
        }

        /**
         * Mark the start of a new countable operation in the current process.
         * 
         * @param name The name of the process counter.
         */
        private void countBegin(String name) {
            ProcessCounter pc = counters.get(name);
            if (pc == null) {
                pc = new ProcessCounter(name);
                counters.put(name, pc);
            }
            pc.start.push(new Long(System.currentTimeMillis()));
        }

        /**
         * Mark the end of a countable operation previously reported via {@link #countBegin(String)}
         * .
         * 
         * @param name The name of the process counter.
         * @param detail Details on the operation that just ended.
         * @return The process counter.
         */
        private ProcessCounter countEnd(String name, String detail) {
            ProcessCounter pc = counters.get(name);
            if (pc == null || pc.start.isEmpty()) {
                return null;
            }

            long start = pc.start.pop();
            long elapsed = System.currentTimeMillis() - start;
            
            if (elapsed < pc.min || pc.count == 0L) {
                pc.min = elapsed;
            }

            if (elapsed > pc.max) {
                pc.max = elapsed;
                pc.longest = detail;
            }

            pc.count++;
            pc.avg = (pc.avg * (pc.count - 1) + elapsed) / pc.count;

            return pc;
        }

        /**
         * Write a trace header on the process trace.
         * 
         * @param name The name of the process trace.
         * @param processDescription A description of the process trace.
         */
        private void appendTraceHeader(String name, String processDescription) {
            // Write a description of the process to the trace buffer.
            traceBuffer.append("KRAD Process Trace (");
            traceBuffer.append(name);
            traceBuffer.append("): ");
            traceBuffer.append(processDescription);

            // Write stack information related to method controlling the callable process.
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            String callerClassName = stackTrace[3].getClassName();
            int indexOfPeriod = callerClassName.lastIndexOf('.');
            String callerPackageName = "";
            if (indexOfPeriod != -1) {
                callerPackageName = callerClassName.substring(0, indexOfPeriod);
            }
            int stackTraceIndex = 3;
            while (stackTraceIndex < stackTrace.length - 1
                    && (stackTrace[stackTraceIndex].getClassName().startsWith("sun.")
                            || stackTrace[stackTraceIndex].getClassName().startsWith("java.")
                            || stackTrace[stackTraceIndex].getClassName().startsWith(
                                    ProcessLogger.class.getPackage().getName()) || stackTrace[stackTraceIndex]
                            .getClassName().startsWith(callerPackageName))) {
                if (!ProcessLogger.class.getName().equals(stackTrace[stackTraceIndex].getClassName())) {
                    traceBuffer.append("\n  at ").append(stackTrace[stackTraceIndex]);
                }
                stackTraceIndex++;
            }
            traceBuffer.append("\n  at ").append(stackTrace[stackTraceIndex]);

            // Write initial heap state and start time.
            traceBuffer.append("\nInitial Memory Usage: ");
            traceBuffer.append(memoryToString(startFree, startTot,
                    startMax));

            if (LOG.isInfoEnabled() && verbose) {
                LOG.debug("Processing Started\n" + traceBuffer.toString());
            }
        }

        /**
         * Write a log message on this process trace.
         * 
         * @param message The message to write.
         */
        public void appendTraceMessage(String message) {
            if (LOG.isDebugEnabled() && verbose) {
                LOG.debug(message
                        + " ("
                        + name
                        + ")\nElapsed Time: "
                        + intervalToString(diffTime)
                        + "\nMemory Usage: "
                        + memoryToString(lastFree, lastTot,
                                lastMax)
                        + "\nMemory Delta: "
                        + memoryToString(diffFree, lastTot,
                                lastMax) + " - tot delta: "
                        + sizeToString(diffTot));
            }

            if (LOG.isInfoEnabled() && (verbose || diffTime > 0L)) {
                traceBuffer.append('\n');

                if (message.length() < 40) {
                    traceBuffer.append(message);
                    for (int i = message.length(); i < 40; i++) {
                        traceBuffer.append('.');
                    }
                } else {
                    traceBuffer.append(message.substring(0, 40));
                }

                traceBuffer.append(intervalToString(diffTime));
                traceBuffer.append(' ');
                traceBuffer.append(intervalToString(lastTime
                        - startTime));
                traceBuffer.append(' ');
                traceBuffer.append(sizeToString(lastFree));
                traceBuffer.append(' ');
                traceBuffer.append(sizeToString(diffFree));
            }
        }

        /**
         * Write a trace footer on the process trace.
         */
        public void appendTraceFooter() {
            traceBuffer.append('\n');
            String message = "Processing Complete";
            traceBuffer.append(message);
            for (int i = message.length(); i < 40; i++) {
                traceBuffer.append('.');
            }
            traceBuffer.append(intervalToString(diffTime));
            traceBuffer.append(' ');
            traceBuffer.append(intervalToString(lastTime
                    - startTime));
            traceBuffer.append(' ');
            traceBuffer.append(sizeToString(lastFree));
            traceBuffer.append(' ');
            traceBuffer.append(sizeToString(diffFree));
            if (!ntraceCount.isEmpty()) {
                traceBuffer.append("\nMonitors:");
                for (Entry<String, Long> ce : ntraceCount.entrySet()) {
                    
                    Long threshold = ntraceThreshold.get(ce.getKey());
                    if (threshold != null && threshold >= ce.getValue()) {
                        continue;
                    }
                    
                    traceBuffer.append("\n  ");
                    StringBuilder sb = new StringBuilder(ce.getKey());
                    int iocc = sb.indexOf("::");
                    if (iocc == -1)
                        sb.append(":" + ce.getValue());
                    else
                        sb.insert(iocc + 1, ce.getValue());
                    traceBuffer.append(sb);
                }
            }
            if (!counters.isEmpty()) {
                traceBuffer.append("\nCounters:");
                for (ProcessCounter pc : counters.values()) {
                    traceBuffer.append("\n  ");
                    traceBuffer.append(pc.name);
                    traceBuffer.append(": ");
                    traceBuffer.append(pc.count);
                    traceBuffer.append(" (");
                    traceBuffer.append(intervalToString(pc.min));
                    traceBuffer.append("/");
                    traceBuffer.append(intervalToString(pc.max));
                    traceBuffer.append("/");
                    traceBuffer.append(intervalToString(pc.avg));
                    traceBuffer.append(")");
                    if (pc.longest != null && !"".equals(pc.longest)) {
                        traceBuffer.append("\n    longest : ");
                        traceBuffer.append(pc.longest);
                    }
                }
            }
            traceBuffer.append("\nElapsed Time: ");
            traceBuffer.append(intervalToString(lastTime
                    - startTime));
            traceBuffer.append("\nMemory Usage: ");
            traceBuffer.append(memoryToString(lastFree, lastTot,
                    lastMax));
            traceBuffer.append("\nMemory Delta: ");
            traceBuffer.append(memoryToString(lastFree
                    - startFree, lastTot, lastMax));
            traceBuffer.append(" - tot delta: ");
            traceBuffer.append(sizeToString(lastTot - startTot));
        }

    }

    /**
     * Print a human readable time duration.
     * 
     * @param millis The number of milliseconds.
     * @return A human readable representation of the time interval represented by millis.
     */
    public static String intervalToString(long millis) {
        DecimalFormat df = new DecimalFormat("000");
        StringBuilder sb = new StringBuilder();
        sb.append('.');
        sb.append(df.format(millis % 1000));
        df.applyPattern("00");
        long sec = millis / 1000;
        sb.insert(0, df.format(sec % 60));
        long min = sec / 60;
        sb.insert(0, ':');
        sb.insert(0, df.format(min % 60));
        long hours = min / 60;
        if (hours > 0) {
            sb.insert(0, ':');
            sb.insert(0, df.format(hours % 24));
        }
        long days = hours / 24;
        if (days > 0) {
            sb.insert(0, " days, ");
            sb.insert(0, days);
        }

        return sb.toString();
    }

    /**
     * The steps for printing sizes.
     */
    private static final String[] SIZE_INTERVALS = new String[]{"k", "M",
            "G", "T", "E",};

    /**
     * Print a human readable size.
     * 
     * @param bytes The number of bytes.
     * @return A human readable representation of the size.
     */
    public static String sizeToString(long bytes) {
        DecimalFormat df = new DecimalFormat("000");
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int mod = 0;

        if (bytes < 0) {
            sb.append('-');
            bytes = Math.abs(bytes);
        }

        while (bytes / 1024 > 0 && i < SIZE_INTERVALS.length) {
            i++;
            mod = (int) (bytes % 1024);
            bytes /= 1024;
        }
        sb.append(bytes);

        if (mod > 0) {
            sb.append('.');
            sb.append(df.format(mod * 1000 / 1024));
        }

        if (i >= 0) {
            sb.append(SIZE_INTERVALS[i]);
        }

        return sb.toString();
    }

    /**
     * Get a human readable representation of the system memory.
     * 
     * @param free free heap memory, in bytes
     * @param tot total heap memory, in bytes
     * @param max maximum total heap memory, in bytes
     * 
     * @return A human readable representation of the system memory.
     */
    public static String memoryToString(long free, long tot, long max) {
        StringBuilder sb = new StringBuilder();
        sb.append(sizeToString(free));
        sb.append('/');
        sb.append(sizeToString(tot));
        sb.append('/');
        sb.append(sizeToString(max));
        sb.append(" - ");
        sb.append(free * 100 / tot);
        sb.append("% free");
        return sb.toString();
    }

    /**
     * Follow a callable process using the system default verbose setting and checked exception
     * handling.
     * 
     * @param <T> callable return type
     * @param name The name of the process.
     * @param processDescription A message describing the process to report at the top of the trace.
     * @param callableProcess The callable process.
     * @return The result of calling the process.
     * @throws IllegalStateException If checked exception occurs within the callable process.
     */
    public static <T> T safeFollow(final String name, final String processDescription,
            final Callable<T> callableProcess) {
        try {
            return follow(name, processDescription, null, callableProcess);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new IllegalStateException("Error in followed process " + name + " - " + processDescription, e);
            }
        }
    }

    /**
     * Follow a callable process with checked exception handling.
     * 
     * @param <T> callable return type
     * @param name The name of the process.
     * @param processDescription A message describing the process to report at the top of the trace.
     * @param verbose Verbose operation flag, see {@link ProcessStatus#verbose}.
     * @param callableProcess The callable process.
     * @return The result of calling the process.
     * @throws IllegalStateException If checked exception occurs within the callable process.
     */
    public static <T> T safeFollow(final String name, final String processDescription,
            final Boolean verbose, final Callable<T> callableProcess) {
        try {
            return follow(name, processDescription, verbose, callableProcess);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new IllegalStateException("Error in followed process " + name + " - " + processDescription, e);
            }
        }
    }

    /**
     * Follow a callable process using the system default verbose setting.
     * 
     * @param <T> callable return type
     * @param name The name of the process.
     * @param processDescription A message describing the process to report at the top of the trace.
     * @param callableProcess The callable process.
     * @return The result of calling the process.
     * @throws Exception from {@link Callable#call()}
     */
    public static <T> T follow(String name, String processDescription, Callable<T> callableProcess)
            throws Exception {
        return follow(name, processDescription, null, callableProcess);
    }

    /**
     * Follow a callable process.
     * 
     * @param <T> callable return type
     * @param name The name of the process.
     * @param processDescription A message describing the process to report at the top of the trace.
     * @param verbose True to note every trace entry, false to only note those that take longer than 1ms.
     * @param callableProcess The callable process.
     * @return The result of calling the process.
     * @throws Exception from {@link Callable#call()}
     */
    public static <T> T follow(String name, String processDescription, Boolean verbose,
            Callable<T> callableProcess) throws Exception {
        // When logging is not at least info enabled, process tracing has no effect - short circuit.
        if (!LOG.isInfoEnabled()) {
            return callableProcess.call();
        }

        // Ensure that statistics are not already active for a process with the same name.
        assert TL_STAT.get() == null || TL_STAT.get().get(name) == null;

        // Bind a new status tracking map to the current thread, if not already bound.
        if (TL_STAT.get() == null) {
            TL_STAT.set(new java.util.HashMap<String, ProcessStatus>());
        }

        // Create a new status tracker for monitoring this process.
        ProcessStatus processStatus = new ProcessStatus(name);
        if (verbose != null) {
            processStatus.verbose = verbose;
        }

        try {
            // Bind the status tracker to the current thread.
            TL_STAT.get().put(name, processStatus);

            processStatus.appendTraceHeader(name, processDescription);

            // Call the process.
            return callableProcess.call();

        } finally {
            // Clear the thread state to prevent memory growth.
            if (TL_STAT.get() != null) {
                TL_STAT.get().remove(name);
                if (TL_STAT.get().isEmpty())
                    TL_STAT.remove();
            }

            // Calculate time and heap utilization since the last trace message.
            processStatus.elapse();
            processStatus.appendTraceFooter();

            // Write process trace at INFO level
            LOG.info(processStatus.traceBuffer.toString());
        }
    }

    /**
     * Determine if any process traces are active on the current thread.
     * 
     * @return True if any process traces are active on the current thread, false if not.
     */
    public static boolean isTraceActive() {
        return TL_STAT.get() != null && !TL_STAT.get().isEmpty();
    }

    /**
     * Determine if a process trace is active on the current thread.
     * 
     * @param name The name of the process trace.
     * @return True if the named process trace is active on the current thread, false if not.
     */
    public static boolean isTraceActive(String name) {
        return TL_STAT.get() != null && TL_STAT.get().containsKey(name);
    }

    /**
     * Determine if the named process is active on the current thread with the verbose flag set to
     * true.
     * 
     * @param name The name of the process trace.
     * @return True if the named process trace is active on the current thread with the verbose flag
     *         set to true, false if not.
     */
    public static boolean isVerbose(String name) {
        ProcessStatus processStatus = TL_STAT.get() == null ? null : TL_STAT.get().get(
                name);
        return processStatus != null && processStatus.verbose;
    }

    /**
     * Modify the verbose flag on a process trace active on the current thread.
     * 
     * <p>
     * This method has no impact if a process trace with the given name is not active.
     * </p>
     * 
     * @param name The name of the process trace.
     * @param verbose The verbose flag setting to apply to the named process trace.
     */
    public static void setVerbose(String name, boolean verbose) {
        ProcessStatus ps = TL_STAT.get() == null ? null : TL_STAT.get().get(
                name);
        if (ps != null) {
            ps.verbose = verbose;
        }
    }

    /**
     * Report a trace message on all process traces active on the current thread.
     * 
     * <p>
     * The first 40 characters of the message will be printed on the traces along with timing and
     * heap utilization statistics.
     * </p>
     * 
     * <p>
     * When debug logging is enabled, the entire message will be printed via log4j at the DEBUG
     * level.
     * </p>
     * 
     * @param message The message to report on the trace.
     */
    public static void trace(String message) {
        if (TL_STAT.get() != null) {
            for (String k : TL_STAT.get().keySet()) {
                trace(k, message);
            }
        }
    }

    /**
     * Report a trace message on a process trace, if active on the current thread.
     * 
     * <p>
     * The first 40 characters of the message will be printed on the trace along with timing and
     * heap utilization statistics.
     * </p>
     * 
     * <p>
     * When debug logging is enabled, the entire message will be printed via log4j at the DEBUG
     * level.
     * </p>
     * 
     * @param name The name of the process trace.
     * @param message The message to report on the trace.
     */
    public static void trace(String name, String message) {
        ProcessStatus processStatus = TL_STAT.get() == null ? null : TL_STAT.get().get(
                name);
        if (processStatus != null) {
            processStatus.elapse();
            processStatus.appendTraceMessage(message);
        }
    }

    /**
     * Count instances of a typically fast operation that may become expensive given a high number
     * of executions.
     * 
     * <p>
     * When the specified number of instances of the same operation have been counted, then a
     * message indicating the execution count will be added to the process trace.
     * </p>
     * 
     * @param prefix The message to report before the count.
     * @param suffix The message to report after the count.
     * @param interval The number of instances to count between reports on the process trace.
     * @return The execution count of the operation on trace with the highest number of executions.
     */
    public static long ntrace(String prefix, String suffix, long interval) {
        return ntrace(prefix, suffix, interval, 0L);
    }
    
    /**
     * Count instances of a typically fast operation that may become expensive given a high number
     * of executions.
     * 
     * <p>
     * When the specified number of instances of the same operation have been counted, then a
     * message indicating the execution count will be added to the process trace.
     * </p>
     * 
     * @param prefix The message to report before the count.
     * @param suffix The message to report after the count.
     * @param interval The number of instances to count between reports on the process trace.
     * @param threshold The number of instances below which not to report monitored counts.
     * @return The execution count of the operation on trace with the highest number of executions.
     */
    public static long ntrace(String prefix, String suffix, long interval, long threshold) {
        long rv = 0L;
        if (TL_STAT.get() != null) {
            for (String k : TL_STAT.get().keySet()) {
                rv = Math.max(rv, ntrace(k, prefix, suffix, interval, threshold));
            }
        }

        return rv;
    }

    /**
     * Count instances of a typically fast operation that may become expensive given a high number
     * of executions.
     * 
     * <p>
     * When the specified number of instances of the same operation have been counted, then a
     * message indicating the execution count will be added to the process trace.
     * </p>
     * 
     * @param name The name of the trace.
     * @param prefix The message to report before the count.
     * @param suffix The message to report after the count.
     * @param interval The number of instances to count between reports on the process trace.
     * @return The execution count of the operation on the named trace.
     */
    public static long ntrace(String name, String prefix, String suffix,
            long interval) {
        return ntrace(name, prefix, suffix, interval, 0L);
    }

    /**
     * Count instances of a typically fast operation that may become expensive given a high number
     * of executions.
     * 
     * <p>
     * When the specified number of instances of the same operation have been counted, then a
     * message indicating the execution count will be added to the process trace.
     * </p>
     * 
     * @param name The name of the trace.
     * @param prefix The message to report before the count.
     * @param suffix The message to report after the count.
     * @param interval The number of instances to count between reports on the process trace.
     * @param threshold The number of instances below which not to report monitored counts.
     * @return The execution count of the operation on the named trace.
     */
    public static long ntrace(String name, String prefix, String suffix,
            long interval, long threshold) {
        ProcessStatus processStatus = TL_STAT.get() == null ? null : TL_STAT.get().get(
                name);
        String nTraceCountKey = prefix + suffix;
        Long nTraceCount = processStatus.ntraceCount.get(nTraceCountKey);
        
        if (nTraceCount == null) {
            nTraceCount = 0L;
        }
        
        processStatus.ntraceCount.put(nTraceCountKey, ++nTraceCount);
        
        if (threshold > 0) {
            processStatus.ntraceThreshold.put(nTraceCountKey, threshold);
        }
        
        if (nTraceCount % interval == 0) {
            String msg = prefix + nTraceCount + suffix;
            trace(msg);
            if (LOG.isDebugEnabled()) {
                LOG.debug(msg, new Throwable());
            }
        }

        return nTraceCount;
    }

    /**
     * Mark the start of a new countable operation on all active process traces.
     * 
     * @param name The name of the process counter.
     */
    public static void countBegin(String name) {
        if (TL_STAT.get() != null) {
            for (String k : TL_STAT.get().keySet()) {
                countBegin(k, name);
            }
        }
    }

    /**
     * Mark the start of a new countable operation on an active process trace.
     * 
     * @param traceName The name of the process trace.
     * @param name The name of the process counter.
     */
    public static void countBegin(String traceName, String name) {
        ProcessStatus ps = TL_STAT.get() == null ? null : TL_STAT.get().get(
                traceName);
        if (ps != null) {
            ps.countBegin(name);
        }
    }

    /**
     * Mark the end of a countable operation previously reported via {@link #countBegin(String)} .
     * 
     * @param name The name of the process counter.
     * @param detail Details on the operation that just ended.
     */
    public static void countEnd(String name, String detail) {
        if (TL_STAT.get() != null) {
            for (String k : TL_STAT.get().keySet()) {
                countEnd(k, name, detail);
            }
        }
    }

    /**
     * Mark the end of a countable operation previously reported via
     * {@link #countBegin(String, String)} .
     * 
     * @param traceName The name of the process trace.
     * @param name The name of the process counter.
     * @param detail Details on the operation that just ended.
     */
    public static void countEnd(String traceName, String name,
            String detail) {
        ProcessStatus processStatus = TL_STAT.get() == null ? null : TL_STAT.get().get(
                traceName);
        if (processStatus != null) {
            processStatus.countEnd(name, detail);
        }
    }

    /**
     * Append an informational message to a process trace.
     * 
     * <p>
     * The message will additionally be logged at the INFO level.
     * </p>
     * 
     * @param traceName The name of the process trace.
     * @param message The information message.
     */
    public static void addExtra(String traceName, Object message) {
        ProcessStatus processStatus = TL_STAT.get() == null ? null : TL_STAT.get().get(
                traceName);
        if (processStatus == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Information Message Reported at ");
        sb.append(new Date());
        sb.append(":\n");
        sb.append(message);
        LOG.info(sb.toString());
        processStatus.extra.append(sb);
        processStatus.extra.append("\n\n");
    }

}
