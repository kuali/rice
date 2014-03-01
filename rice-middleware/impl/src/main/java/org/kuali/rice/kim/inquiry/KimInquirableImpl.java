/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kim.inquiry;

import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kns.inquiry.KualiInquirableImpl;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

/**
 * Helper class for common needs of the KIM inquiry screens. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class KimInquirableImpl extends KualiInquirableImpl {
    private static final long serialVersionUID = 1L;
    
    protected final String ID = "id";
    protected final String NAME = "name";
    protected final String NAME_TO_DISPLAY = "nameToDisplay";
    protected final String TEMPLATE_NAME = "template.name";
    protected final String NAMESPACE_CODE = "namespaceCode";
    protected final String TEMPLATE_NAMESPACE_CODE = "template.namespaceCode";
    protected final String DETAIL_OBJECTS = "detailObjects";
    protected final String ATTRIBUTE_DATA_ID = "attributeDataId";
    protected final String ASSIGNED_TO_ROLES = "assignedToRolesToDisplay";

    protected String getKimAttributeLabelFromDD(String attributeName){
        return KRADServiceLocatorWeb.getDataDictionaryService().getAttributeLabel(KimAttributes.class, attributeName);
    }
    
}
