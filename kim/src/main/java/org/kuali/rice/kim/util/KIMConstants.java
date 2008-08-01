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
    public static final class ATTRIBUTE_TYPE {
    	public static final String TEXT_ATTRIBUTE_TYPE = "Text";
    	public static final Long TEXT = new Long(1);  //this needs to stay in sync with the Attribute Type bootstrap SQL in KIMBootstrap.sql
    }
    
    /**
     * This class houses constants that represent the property names for the business objects.  This should 
     * be used for building database queries and property names for error generation.
     * @author Kuali Rice Team (kuali-rice@googlegroups.com)
     */
    public static final class BO_PROPERTY_NAMES {
        public static final String ID = "id";
        public static final String ATTRIBUTE_TYPE_NAME = "attributeTypeName";
        public static final String DESCRIPTION = "description";
        public static final String NAME = "name";
        public static final String PERMISSIONS = "permissions";
    }

	public static final class GROUP_TYPE {
		public static final Long DEFAULT_GROUP_TYPE = new Long(1);  //this needs to stay in sync with the Group Type bootstrap SQL in KIMBootstrap.sql
	}

	public static final class ENTITY_TYPE {
		public static final Long PERSON_ENTITY_TYPE = new Long(1);  //this needs to stay in sync with the Entity Type bootstrap SQL in KIMBootstrap.sql
	}

	public static final class NAMESPACE {
		public static final Long KIM_NAMESPACE = new Long(1);  //this needs to stay in sync with the Namespace bootstrap SQL in KIMBootstrap.sql
	}
	
	public static final String NAMESPACE_DEFAULT_ATTRIBUTE_PREFIX_TOKEN = "NAMESPACE_DFLT_ATTRIBUTE-"; 
}