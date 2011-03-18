/*
 * Copyright 2005-2009 The Kuali Foundation
 * 
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
package org.kuali.rice.ksb.messaging;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.config.RunMode;

/**
 * A KSBExporter which will exports the service in the case where the specified
 * run mode is set.  The run mode order proceeds as follows:
 * 
 * <p>remote -> embedded -> local
 * 
 * <p>If any of the earlier run modes in the list is set, then the ones after it
 * will be applied.  So, if remote is set, then it will automatically be exported
 * for embedded and local run modes.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RunModeServiceExporter extends PropertyConditionalKSBExporter {
	
    private String runModePropertyName;
    private RunMode validRunMode;
    private static final List<RunMode> runModeHierarchy = new ArrayList<RunMode>();
    static {
    	runModeHierarchy.add(RunMode.THIN);
    	runModeHierarchy.add(RunMode.REMOTE);
    	runModeHierarchy.add(RunMode.EMBEDDED);
    	runModeHierarchy.add(RunMode.LOCAL);
    }

    @Override
	protected boolean shouldRemoteThisService() throws Exception {
    	if (validRunMode == null) {
    		throw new ConfigurationException("The validRunMode property was not set.");
    	}
    	if (!runModeHierarchy.contains(getValidRunMode())) {
    		throw new ConfigurationException("Given validRunMode is not a valid run mode.  Value was: " + getValidRunMode());
    	}
		RunMode runModePropertyValue = RunMode.valueOf(ConfigContext.getCurrentContextConfig().getProperty(getRunModePropertyName()));

    	if (!runModeHierarchy.contains(runModePropertyValue)) {
    		throw new ConfigurationException("Run mode value set on runModePropertyName of '" + getRunModePropertyName() + "' is not a valid run mode.  Value was: " + runModePropertyValue);
    	}
    	List<RunMode> validRunModeSubList = runModeHierarchy.subList(runModeHierarchy.indexOf(getValidRunMode()), runModeHierarchy.size());
    	return validRunModeSubList.contains(runModePropertyValue) && super.shouldRemoteThisService();
	}

	public String getRunModePropertyName() {
        return this.runModePropertyName;
    }

    public void setRunModePropertyName(String runModePropertyName) {
        this.runModePropertyName = runModePropertyName;
    }

    public RunMode getValidRunMode() {
        return this.validRunMode;
    }

    public void setValidRunMode(RunMode validRunMode) {
        this.validRunMode = validRunMode;
    }
    
}
