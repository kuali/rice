/*
 * Copyright 2007 The Kuali Foundation.
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
package edu.sampleu.travel.rice;

import org.kuali.core.bo.user.KualiModuleUser;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.impl.KualiModuleUserServiceBaseImpl;
import org.kuali.rice.KNSServiceLocator;

public class TravelModuleUserService extends KualiModuleUserServiceBaseImpl {
    
    public static final String MODULE_ID = "tv";

    public KualiModuleUser getModuleUser(String personUniversalIdentifier) throws UserNotFoundException {
        return getModuleUser(KNSServiceLocator.getUniversalUserService().getUniversalUser(personUniversalIdentifier));
    }

    public KualiModuleUser getModuleUser(UniversalUser universalUser) throws UserNotFoundException {
        TravelUser travUser = new TravelUser(MODULE_ID, universalUser);
        travUser.setUniversalUser(universalUser);
        travUser.setActive(true);
        return travUser;
    }

    public void save(KualiModuleUser moduleUser) throws IllegalArgumentException {

    }

}
