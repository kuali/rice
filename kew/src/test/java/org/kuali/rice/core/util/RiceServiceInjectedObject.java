/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.core.util;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.mail.service.ActionListEmailService;
import org.springframework.beans.factory.BeanNameAware;


/**
 * This is an object that is defined as a bean in the TestKewSpringBeans.xml (the test "client") and
 * uses RiceService annotations to have Rice services from the Rice context injected into it instead
 * of Spring wiring.  
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RiceServiceInjectedObject implements BeanNameAware {
    // hack for testing purposes
    public static Map<String, RiceServiceInjectedObject> beans = new HashMap<String, RiceServiceInjectedObject>();
    
    public void setBeanName(String name) {
        beans.put(name, this);
    }

    @RiceService(name="enActionListService")
    public ActionListService als;
    
    @RiceService(name="enActionListEmailService")
    public ActionListEmailService ales;
    
    @RiceService(name="enActionRequestService")
    public ActionRequestService ars;
    
    @RiceService(name="enApplicationConstantsService")
    public Object wireMeInSpring;

    // DON'T WIRE THIS IN SPRING
    @RiceService(resourceLoader="KEW_SPRING+PLUGIN_REGISTRY_CONTAINER_RESOURCE_LOADER", name="enActionListEmailService") // specify the resource loader by name here
    public void setActionListEmailService(ActionListEmailService ales) {
        this.ales = ales;
    }

    public ActionRequestService getActionRequestService() {
        return ars;
    }
    
    public void setActionRequestService(ActionRequestService ars) {
        this.ars = ars;
    }
    
    public void setWireMeInSpring(Object o) {
        this.wireMeInSpring = o;
    }

}
