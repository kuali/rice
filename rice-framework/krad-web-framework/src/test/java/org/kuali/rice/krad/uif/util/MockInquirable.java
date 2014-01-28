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
package org.kuali.rice.krad.uif.util;

import java.util.Map;

import org.kuali.rice.krad.inquiry.Inquirable;
import org.kuali.rice.krad.uif.widget.Inquiry;

/**
 * Mock inquirable implementation for UIF unit tests. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MockInquirable implements Inquirable {
    
    private Class<?> dataObjectClass;
    private String viewName;
    
    /**
     * Create a mock inqurable instance based on data object class and view name.
     * 
     * @param dataObjectClass The data object class.
     * @param viewName The view name.
     */
    MockInquirable(Class<?> dataObjectClass, String viewName) {
        this.dataObjectClass = dataObjectClass;
        this.viewName = viewName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getDataObjectClass() {
        return dataObjectClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataObjectClass(Class<?> dataObjectClass) {
        this.dataObjectClass = dataObjectClass;
    }

    /**
     * @see org.kuali.rice.krad.inquiry.Inquirable#retrieveDataObject(java.util.Map)
     */
    @Override
    public Object retrieveDataObject(Map<String, String> fieldValues) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.inquiry.Inquirable#buildInquirableLink(java.lang.Object, java.lang.String, org.kuali.rice.krad.uif.widget.Inquiry)
     */
    @Override
    public void buildInquirableLink(Object dataObject, String propertyName, Inquiry inquiry) {
    }

}
