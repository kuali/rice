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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.kim.bo.Entity;
import org.kuali.rice.kim.bo.NamespaceDefaultAttribute;
import org.kuali.rice.kim.service.NamespaceAttributeService;
import org.kuali.rice.kns.util.KNSPropertyConstants;


public class NamespaceAttributeServiceImpl implements NamespaceAttributeService {

    protected List<String> propertyList = Collections.EMPTY_LIST;
    
    private String namespaceName;
     
    public NamespaceAttributeServiceImpl() {
        super();
    }

    public NamespaceAttributeServiceImpl(String namespaceName){
        this.namespaceName = namespaceName;
        List<String> properties = new ArrayList<String>();   
        properties.add("attributeName");
        properties.add(KNSPropertyConstants.ACTIVE);
        properties.add("required");   
        setPropertyList(properties);
    }
    
   
    public List<String> getPropertyList() {
        return propertyList;
    }
    
    public void setPropertyList(List<String> propertyList) {
        this.propertyList = propertyList;
    }

}
