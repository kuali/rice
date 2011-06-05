package org.kuali.rice.ksb.api.bus;

import java.io.Serializable;
import java.net.URL;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.security.credentials.CredentialsType;

public interface ServiceConfiguration extends Serializable {

	QName getServiceName();
	URL getEndpointUrl();
	String getApplicationId();
	String getServiceVersion();
	String getType();
	
	boolean isQueue();
	Integer getPriority();
	Integer getRetryAttempts();
	Long getMillisToLive();
	String getMessageExceptionHandler();
	
	Boolean getBusSecurity();
	CredentialsType getCredentialsType();
	
}
