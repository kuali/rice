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
package org.kuali.rice.core.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * This is a description of what this class does - delyea don't forget to fill this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RiceConstants {

    private static final long serialVersionUID = 3625085403351858951L;

	public static final String DB_PLATFORM = "enDbPlatform";

    public static final String RICE_JPA_ENABLED = "rice.jpa.enabled";
    
    public static final String SERVICES_TO_CACHE = "rice.resourceloader.servicesToCache";

    public static final String SPRING_TRANSACTION_MANAGER = "SPRING_TRANSACTION_MANAGER";

    public static final String ROOT_RESOURCE_LOADER_CONTAINER_NAME = "RootResourceLoaderContainer";
    public static final String DEFAULT_ROOT_RESOURCE_LOADER_NAME = "RootResourceLoader";

    // Default struts mapping forward key
    public static final String MAPPING_BASIC = "basic";

    // Default date formatting
    public static final String SIMPLE_DATE_FORMAT_FOR_DATE = "MM/dd/yyyy";
    public static final String SIMPLE_DATE_FORMAT_FOR_TIME = "hh:mm a";
    public static final String DEFAULT_DATE_FORMAT_PATTERN = SIMPLE_DATE_FORMAT_FOR_TIME + " " + SIMPLE_DATE_FORMAT_FOR_DATE;

    public static DateFormat getDefaultDateFormat() {
        return new SimpleDateFormat(SIMPLE_DATE_FORMAT_FOR_DATE);
    }

    public static DateFormat getDefaultTimeFormat() {
        return new SimpleDateFormat(SIMPLE_DATE_FORMAT_FOR_TIME);
    }

    public static DateFormat getDefaultDateAndTimeFormat() {
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT_PATTERN);
    }
}
