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
package org.kuali.rice.kim.test.util;

import org.kuali.rice.kim.bo.AttributeType;

/**
 * This class is a utility class for easy access to re-usable mock objects that can 
 * be used in KIM unit tests. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KIMMockObjectUtil {
    /**
     * This method is a helper to build an AttributeType instance.
     * @param name
     * @param description
     * @return AttributeType
     */
    public static final AttributeType buildTestAttributeType(String attributeTypeName, String description) {
        AttributeType at = new AttributeType();
        at.setAttributeTypeName(attributeTypeName);
        at.setDescription(description);
        at.setVersionNumber(new Long(1));
        return at;
    }		
    
    /**
     * This method returns back a specific test mock object.
     * @return AttributeType
     */
    public static final AttributeType getTestAttributeType_1() {
        return buildTestAttributeType("Test Attribute Type 1", "Test Attribute Type 1 - description");
    }

    /**
     * This method returns back a specific test mock object.
     * @return AttributeType
     */
    public static final AttributeType getTestAttributeType_2() {
        return buildTestAttributeType("Test Attribute Type 2", "Test Attribute Type 2 - description");
    }
}
