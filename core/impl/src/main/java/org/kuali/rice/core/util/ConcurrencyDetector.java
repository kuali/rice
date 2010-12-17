/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.core.util;

import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

/**
 * Utility class that can be used to diagnose concurrency issues.  When concurrency is detected
 * the default {@link #onConcurrencyDetected()} implementation logs an error and optionally the
 * stack traces (if trackStacktraces is enabled).
 * 
 * E.g.
 * 
 * private static final ConcurrencyDetector detector = new ConcurrencyDetector("Concurrency in Foo class");
 * 
 * <tt>
 * public void questionableMethod() {
 *   detector.enter();
 *   try {
 *     // method impl
 *   } finally {
 *     detector.exit();
 *   }
 * }
 * </tt>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConcurrencyDetector {
    // keeps track of whether the last enter (in the thread) was able to acquire
    // the semaphore (in which case the next exit should release it)
    private ThreadLocal<Boolean> ACQUIRED_SEMAPHORE = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };
    private final Logger log;
    private final String name;
    private final boolean trackStacktraces;
    private final Semaphore semaphore = new Semaphore(1);

    private Throwable entryPoint;

    public ConcurrencyDetector() {
        this(ConcurrencyDetector.class.getName());
    }
    
    public ConcurrencyDetector(String name) {
        this(name, true);
    }
    
    public ConcurrencyDetector(String name, boolean trackStacktraces) {
        this.log = Logger.getLogger(name);
        this.name = name;
        this.trackStacktraces = trackStacktraces;
    }
    
    public synchronized boolean enter() {
        boolean acquired = semaphore.tryAcquire();
        ACQUIRED_SEMAPHORE.set(Boolean.valueOf(acquired));
        if (!acquired) {
            onConcurrencyDetected();
        } else {
            if (trackStacktraces) {
                entryPoint = new Throwable("Initial entry");
            }
        }
        return acquired;
    }

    public synchronized void exit() {
        if (ACQUIRED_SEMAPHORE.get()) {
            if (trackStacktraces) {
                entryPoint = null;
            }
            semaphore.release();
        }
    }

    /**
     * Logs an error (and optionally stack traces) when concurrency is detected.
     * Subclasses may override for custom behavior.
     */
    protected void onConcurrencyDetected() {
        log.error("Concurrency was detected");
        if (trackStacktraces) {
            entryPoint.printStackTrace();
            new Throwable("Second entry").printStackTrace();
        }
    }
}
