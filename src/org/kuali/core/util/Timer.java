/*
 * Copyright 2006 The Kuali Foundation.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Timer {
    private static final Log LOG = LogFactory.getLog(Timer.class);

    private static int indent = 0;

    private long t0;
    private long t1;
    private String name;

    public Timer(String name) {
        indent++;
        if (LOG.isDebugEnabled()) {
            LOG.debug(indent() + name + ": started");
        }
        this.name = name;
        reset();
    }

    public long getElapsed() {
        t1 = System.currentTimeMillis();
        return t1 - t0;
    }

    public void reset() {
        t0 = System.currentTimeMillis();
    }

    public String indent() {
        String whitespace = "                                                                                                    ";
        try {
            return whitespace.substring(whitespace.length() - indent);
        }
        catch (Exception e) {
            return "";
        }
    }

    public void log() {
        if (LOG.isDebugEnabled()) {
            long elapsed = getElapsed();
            LOG.debug(indent() + name + ": " + elapsed + "millisec");
            indent--;
        }
    }

}
