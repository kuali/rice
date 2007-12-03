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
package edu.iu.uis.eden.util;

/**
 * Records and logs performance information about an elapsed time period.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PerformanceLogger {

    private static final org.apache.log4j.Logger LOG =
        org.apache.log4j.Logger.getLogger(PerformanceLogger.class);
    private long startTime;
    private Long routeHeaderId;
    
    public PerformanceLogger() {
        recordStartTime();
    }
    
    public PerformanceLogger(Long routeHeaderId) {
        this();
        this.routeHeaderId = routeHeaderId;
    }
    
    private void recordStartTime() {
        this.startTime = System.currentTimeMillis();
    }
    
    public void log(String message) {
        log(message, false);
    }

    public void log(String message, boolean terminalPoint) {
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        String logMessage = "Time: "+totalTime+" ms, ";
        if (routeHeaderId != null) {
            logMessage+="docId="+routeHeaderId+", ";
        }
        logMessage += message;
        if (terminalPoint) {
            logMessage += "\n";
        }
        LOG.info(logMessage);
    }
    
}
