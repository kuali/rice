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
package org.kuali.rice.core;

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
        synchronized (this.lock) {
            while (!this.fired) {
                try {
                    this.lock.wait();
                } catch (InterruptedException ie) {
                    LOG.warn("Interrupted while waiting for condition: " + this.name, ie);
                }
            }
        }
    }

    public boolean hasFired() {
        synchronized (this.lock) {
            return this.fired;
        }
    }

    public void fire() {
        synchronized (this.lock) {
            if (this.fired) return;
            this.fired = true;
            this.lock.notifyAll();
        }
    }

    public void reset() {
        synchronized (this.lock) {
            this.fired = false;
        }
    }

    public String toString() {
        return "[Condition: name=" + this.name + "]";
    }
}