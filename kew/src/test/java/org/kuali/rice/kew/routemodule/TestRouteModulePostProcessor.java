/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.rice.kew.routemodule;

import org.kuali.rice.kew.DocumentRouteLevelChange;
import org.kuali.rice.kew.DocumentRouteStatusChange;
import org.kuali.rice.kew.clientapp.DeleteEvent;
import org.kuali.rice.kew.clientapp.IPostProcessorBusinessLogic;
import org.kuali.rice.kew.clientapp.ResourceLocator;
import org.kuali.rice.kew.exception.ResourceUnavailableException;


/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class TestRouteModulePostProcessor implements IPostProcessorBusinessLogic {

    public void setLocator(ResourceLocator locator) {
    }

    public ResourceLocator getLocator() {
        return null;
    }

    public boolean doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent, StringBuffer msg) throws Exception, ResourceUnavailableException {
        return true;
    }

    public boolean doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent, StringBuffer parm2) throws Exception, ResourceUnavailableException {
        return true;
    }

    public boolean doDeleteRouteHeader(DeleteEvent event, StringBuffer message) throws Exception, ResourceUnavailableException {
        return true;
    }

    public String getVersion() throws Exception {
        return "1.0";
    }

}
