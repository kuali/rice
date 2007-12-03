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
package org.kuali.rice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.kuali.rice.util.JSTLConstants;

public class RiceConstants extends JSTLConstants {

    private static final long serialVersionUID = -8828648691393487244L;

    public static final int DEFAULT_TRANSACTION_TIMEOUT_SECONDS = 3600;
    public static final String MESSAGE_ENTITY = "message.entity";
    public static final String MESSAGE_PERSISTENCE = "message.persistence";
    public static final String MESSAGING_OFF = "message.off";
    public static final String MESSAGE_DELIVERY = "message.delivery";
    public static final String MESSAGING_SYNCHRONOUS = "synchronous";
    public static final String DEFAULT_CONFIG_LOCATION_PARAM = "default.config.location";
    public static final String INJECTED_EXCEPTION_MESSAGE_SCHEDULER_KEY = "message.injected.scheduler";
    public static final String FIXED_POOL_SIZE = "ksb.fixedPoolSize";
    public static final String KSB_AUTH_SERVICE = "ksb.authService";

    public static final String ROOT_RESOURCE_LOADER_CONTAINER_NAME = "RootResourceLoaderContainer";
    public static final String DEFAULT_ROOT_RESOURCE_LOADER_NAME = "RootResourceLoader";

    // message queue constants
    public static final String ROUTE_QUEUE_QUEUED = "Q";
    public static final String ROUTE_QUEUE_EXCEPTION = "E";
    public static final String ROUTE_QUEUE_ROUTING = "R";
    public static final String ROUTE_QUEUE_EXCEPTION_LABEL = "EXCEPTION";
    public static final String ROUTE_QUEUE_ROUTING_LABEL = "ROUTING";
    public static final String ROUTE_QUEUE_QUEUED_LABEL = "QUEUED";
    public static final String ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY = "RouteQueue.maxRetryAttempts";
    public static final String ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_OVERRIDE_KEY = "RouteQueue.maxRetryAttemptsOverride";
    public static final String ROUTE_QUEUE_TIME_INCREMENT_KEY = "RouteQueue.timeIncrement";
    public static final String IMMEDIATE_EXCEPTION_ROUTING = "Routing.ImmediateExceptionRouting";
    public static final Integer ROUTE_QUEUE_DEFAULT_PRIORITY = new Integer(5);

    public static final String ROUTE_QUEUE_FILTER_SUFFIX = "Filter";

    public static final String KSB_MESSAGE_DATASOURCE = "ksbMessage.datasource";
    public static final String KSB_REGISTRY_DATASOURCE = "ksbRegistry.datasource";
    public static final String KSB_MESSAGE_DATASOURCE_JNDI = "ksbMessage.datasource.jndi.location";
    public static final String KSB_REGISTRY_DATASOURCE_JNDI = "ksbRegistry.datasource.jndi.location";
    public static final String USE_QUARTZ_DATABASE = "useQuartzDatabase";

    // custom http header keys
    public static final String DIGITAL_SIGNATURE_HEADER = "KEW_DIGITAL_SIGNATURE";
    public static final String KEYSTORE_ALIAS_HEADER = "KEW_KEYSTORE_ALIAS";

    private static final String SIMPLE_DATE_FORMAT_FOR_DATE = "MM/dd/yyyy";
    private static final String SIMPLE_DATE_FORMAT_FOR_TIME = "hh:mm a";
    public static final String DEFAULT_DATE_FORMAT_PATTERN = SIMPLE_DATE_FORMAT_FOR_TIME + " " + SIMPLE_DATE_FORMAT_FOR_DATE;

	public static final String SPRING_TRANSACTION_MANAGER = "SPRING_TRANSACTION_MANAGER";

	public static final String SERVICES_TO_CACHE = "rice.resourceloader.servicesToCache";

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
