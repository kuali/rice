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
package org.kuali.rice.kim.util;

/**
 * This class houses all constants for KIM.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KIMConstants {
    /**
     * Different content types for the Notification System.  These are static out of the box content types that have specific UIs built for them.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
     */
    public static final class ATTRIBUTE_TYPES {
	public static final String TEXT_ATTRIBUTE_TYPE = "Text";
    }
    
    /**
     * This class houses constants that represent the property names for the business objects.  This should 
     * be used for building database queries.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
     */
    public static final class BO_PROPERTY_NAMES {
        public static final String ID = "id";
        public static final String ATTRIBUTE_TYPE_NAME = "attributeTypeName";
        public static final String DESCRIPTION = "description";
    }
}