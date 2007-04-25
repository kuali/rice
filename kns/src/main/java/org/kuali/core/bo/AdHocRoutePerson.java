/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.bo;

import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.rice.KNSServiceLocator;

/**
 * Ad Hoc Route Person Business Object
 */
public class AdHocRoutePerson extends AdHocRouteRecipient {

    private static final long serialVersionUID = 1L;
    
    private transient UniversalUser universalUser;

    public AdHocRoutePerson() {
        setType(PERSON_TYPE);
    }

    @Override
    public void setType(Integer type) {
        if (!PERSON_TYPE.equals(type)) {
            throw new IllegalArgumentException("cannot change type to " + type);
        }
        super.setType(type);
    }

    @Override
    public String getName() {
        if ( universalUser == null || !universalUser.getPersonUserIdentifier().equalsIgnoreCase( getId() ) ) {
            universalUser = null;
            try {
                universalUser = KNSServiceLocator.getUniversalUserService().getUniversalUser( new AuthenticationUserId( getId() ) );
            } catch ( UserNotFoundException ex ) {
                // do nothing, leave UU as null
            }
        }
        if ( universalUser == null ) {
            return "";
        }
        return universalUser.getPersonName();
    }
    
    
}