package org.kuali.rice;

public class RiceConstants {

	public static final int DEFAULT_TRANSACTION_TIMEOUT_SECONDS = 3600;
	public static final String MESSAGE_PERSISTENCE = "message.persistence";
	public static final String MESSAGING_SYNCHRONOUS = "synchronous";
	public static final String DEFAULT_CONFIG_LOCATION_PARAM = "default.config.location";

	public static final String ROOT_RESOURCE_LOADER_CONTAINER_NAME = "RootResourceLoaderContainer";
	public static final String DEFAULT_ROOT_RESOURCE_LOADER_NAME = "RootResourceLoader";

	//message queue constants
	public static final String ROUTE_QUEUE_QUEUED = "Q";
	public static final String ROUTE_QUEUE_EXCEPTION = "E";
	public static final String ROUTE_QUEUE_ROUTING = "R";
	public static final String ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY = "RouteQueue.maxRetryAttempts";
	public static final String ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_OVERRIDE_KEY = "RouteQueue.maxRetryAttemptsOverride";
	public static final String ROUTE_QUEUE_TIME_INCREMENT_KEY = "RouteQueue.timeIncrement";
	public static final String IMMEDIATE_EXCEPTION_ROUTING = "Routing.ImmediateExceptionRouting";
	public static final Integer ROUTE_QUEUE_DEFAULT_PRIORITY = new Integer(5);
	
    // custom http header keys
    public static final String DIGITAL_SIGNATURE_HEADER = "KEW_DIGITAL_SIGNATURE";
	public static final String KEYSTORE_ALIAS_HEADER = "KEW_KEYSTORE_ALIAS";

}
