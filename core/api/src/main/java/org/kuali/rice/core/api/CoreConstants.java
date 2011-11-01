/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.core.api;

/**
 * This class contains constants used by the core module. They are public to all and both clients 
 * and other rice modules are allowed to use them. 
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class CoreConstants {

    public static final class Services {
        public static final String DATETIME_SERVICE = "dateTimeService";
        public static final String ENCRYPTION_SERVICE = "encryptionService";

        private Services() {
    		throw new UnsupportedOperationException("do not call");
    	}
	}

    public final static String SERVICE_PATH_SOAP = "soap/" + Namespaces.MODULE_NAME + "/" + Versions.VERSION_2_0;
    
    public static final class Versions {
    	    	
    	public static final String UNSPECIFIED = "unspecifiedVersion";
    	
    	/**
    	 * Name for major version 2.0 of Kuali Rice which should be used in XML namespaces for data elements compatible with Kuali Rice 2.0.x.
    	 * This constant value should never be changed!  If it is changed then it will break compatibility with legacy clients.
    	 */
    	public static final String VERSION_2_0 = "v2_0";
    	
    }
    
    public static final class Namespaces {
    	public static final String ROOT_NAMESPACE_PREFIX = "http://rice.kuali.org";
        public static final String MODULE_NAME = "core";
        public static final String CORE_NAMESPACE_PREFIX = CoreConstants.Namespaces.ROOT_NAMESPACE_PREFIX + "/" + MODULE_NAME;

        public static final String UNSPECIFIED = "unspecifiedNamespace";

    	/**
    	 * Namespace for the core module which is compatible with Kuali Rice 2.0.x.
    	 */
    	public static final String CORE_NAMESPACE_2_0 = CORE_NAMESPACE_PREFIX + "/" + Versions.VERSION_2_0;

        private Namespaces() {
		    throw new UnsupportedOperationException("do not call");
	    }
    }

    public static final class CommonElements {
        public static final String FUTURE_ELEMENTS = "_futureElements";
        public static final String VERSION_NUMBER = "versionNumber";
        public static final String OBJECT_ID = "objectId";
        public static final String ACTIVE = "active";
        public static final String ACTIVE_FROM_DATE = "activeFromDate";
        public static final String ACTIVE_TO_DATE = "activeToDate";
        public static final String ATTRIBUTES = "attributes";

        private CommonElements() {
		    throw new UnsupportedOperationException("do not call");
	    }
    }
	
	public static final String STRING_TO_DATE_FORMATS = "STRING_TO_DATE_FORMATS";
    public static final String STRING_TO_TIMESTAMP_FORMATS = "STRING_TO_TIMESTAMP_FORMATS";
    public static final String DATE_TO_STRING_FORMAT_FOR_USER_INTERFACE = "DATE_TO_STRING_FORMAT_FOR_USER_INTERFACE";
    public static final String TIMESTAMP_TO_STRING_FORMAT_FOR_USER_INTERFACE = "TIMESTAMP_TO_STRING_FORMAT_FOR_USER_INTERFACE";
    public static final String DATE_TO_STRING_FORMAT_FOR_FILE_NAME = "DATE_TO_STRING_FORMAT_FOR_FILE_NAME";
    public static final String TIMESTAMP_TO_STRING_FORMAT_FOR_FILE_NAME = "TIMESTAMP_TO_STRING_FORMAT_FOR_FILE_NAME";

    public static final String DATA_TYPE_STRING = "string";
    public static final String DATA_TYPE_DATE = "datetime";
    public static final String DATA_TYPE_LONG = "long";
    public static final String DATA_TYPE_FLOAT = "float";
    public static final String DATA_TYPE_BOOLEAN = "boolean";
    public static final String EMPTY_STRING = "";
    
    public static final class Config {
    	public static final String APPLICATION_ID = "application.id";
        //FIXME: this should be a core config property
        public static final String INSTANCE_ID = "rice.ksb.bus.instanceId";
    }
    
	private CoreConstants() {
		throw new UnsupportedOperationException("do not call");
	}
}
