/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.rice.kew.rule.service.impl;

import org.kuali.rice.kew.rule.service.RuleDelegationCacheProcessor;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;

public class RuleDelegationCacheProcessorImpl implements RuleDelegationCacheProcessor {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RuleDelegationCacheProcessorImpl.class);
	
	public void clearRuleDelegationFromCache(Long responsibilityId) {
		PerformanceLogger logger = new PerformanceLogger();
		if (responsibilityId != null) {
			flushListFromCache(responsibilityId);
		}
		logger.log("Time to notify cache of rule delegation change for responsibility id=" + responsibilityId);
	}
	
	protected void flushListFromCache(Long responsibilityId) {
    	String responsibilityIdStr = responsibilityId.toString();
        LOG.info("Flushing delegation rules from Cache for responsibilityId='" + responsibilityIdStr );
        KsbApiServiceLocator.getCacheAdministrator().flushEntry(getRuleDlgnCacheKey(responsibilityIdStr));
    }
	
	protected String getRuleDlgnCacheKey(String responsibilityIdStr) {
        return "RuleDlgnCache:" + responsibilityIdStr;
    }
	
}
