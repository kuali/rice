/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.bus.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * This is a description of what this class does - rkirkend don't forget to fill this in. 
 * 
 * @author Full Name (email at address dot com)
 *
 */
public class TestJob implements Job {

    public static boolean EXECUTED = false;
    
    public static Object LOCK = new Object();
    
    /**
     * This overridden method ...
     * 
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
	EXECUTED = true;
	synchronized (LOCK) {
	    TestJob.LOCK.notifyAll();    
	}
	
    }

}
