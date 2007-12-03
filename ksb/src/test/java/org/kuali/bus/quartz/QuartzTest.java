/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.bus.quartz;

import java.util.Date;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

/**
 * Test basic sanity check of quartz implementation
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class QuartzTest extends KSBTestCase {

    @Test
    public void testSchedulingJob() throws Exception {
	Scheduler scheduler = KSBServiceLocator.getScheduler();
	JobDataMap datMap = new JobDataMap();
	datMap.put("yo", "yo");
	JobDetail jobDetail = new JobDetail("myJob", null, TestJob.class);
	jobDetail.setJobDataMap(datMap);
	
	Trigger trigger = TriggerUtils.makeImmediateTrigger(1, 1);
	trigger.setStartTime(new Date()); 
	trigger.setName("i'm a trigger puller");
	trigger.setJobDataMap(datMap);

	scheduler.scheduleJob(jobDetail, trigger);
	
	
	synchronized (TestJob.LOCK) {
	    TestJob.LOCK.wait(30 * 1000);    
	}
	
	assertTrue("job never fired", TestJob.EXECUTED);
    }

}