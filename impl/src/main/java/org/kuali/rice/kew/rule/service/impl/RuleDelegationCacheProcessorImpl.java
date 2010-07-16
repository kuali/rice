package org.kuali.rice.kew.rule.service.impl;

import org.kuali.rice.kew.rule.service.RuleDelegationCacheProcessor;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.PerformanceLogger;

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
        KEWServiceLocator.getCacheAdministrator().flushEntry(getRuleDlgnCacheKey(responsibilityIdStr));
    }
	
	protected String getRuleDlgnCacheKey(String responsibilityIdStr) {
        return "RuleDlgnCache:" + responsibilityIdStr;
    }
	
}
