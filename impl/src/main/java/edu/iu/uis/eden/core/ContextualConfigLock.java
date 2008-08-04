/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.core;

import org.apache.log4j.Logger;

/**
 * Synchronization primitive that implements a condition that can be waited upon.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ContextualConfigLock {
    private static final Logger LOG = Logger.getLogger(ContextualConfigLock.class);

    private String name;
    private boolean fired;
    private Object lock = new Object();

    public ContextualConfigLock() {
        this(null);
    }

    public ContextualConfigLock(String name) {
        if (name == null) {
            this.name = "<<anonymous>>";
        } else {
            this.name = name;
        }
    }

    public void await() {
        synchronized (lock) {
            while (!fired) {
                try {
                    lock.wait();
                } catch (InterruptedException ie) {
                    LOG.warn("Interrupted while waiting for condition: " + name, ie);
                }
            }
        }
    }

    public boolean hasFired() {
        synchronized (lock) {
            return fired;
        }
    }

    public void fire() {
        synchronized (lock) {
            if (fired) return;
            fired = true;
            lock.notifyAll();
        }
    }

    public void reset() {
        synchronized (lock) {
            fired = false;
        }
    }

    public String toString() {
        return "[Condition: name=" + name + "]";
    }
}