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
package org.kuali.rice.kim.lookup.valuefinder;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.web.ui.KeyLabelPair;


/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimRoleValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<RoleImpl> roles = (List)KNSServiceLocator.getBusinessObjectService().findAll(RoleImpl.class);
        List<KeyLabelPair> keyValues = new ArrayList<KeyLabelPair>();

        for (RoleImpl role: roles) {
        	keyValues.add(new KeyLabelPair(role.getRoleName(), role.getRoleName()));
        }

        keyValues.add(new KeyLabelPair("", ""));
        return keyValues;
    }

}
