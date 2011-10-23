package org.kuali.rice.ksb.impl.bus;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.util.ChecksumUtils;
import org.kuali.rice.core.api.util.RiceUtilities;
import org.kuali.rice.ksb.api.bus.Endpoint;
import org.kuali.rice.ksb.api.bus.ServiceConfiguration;
import org.kuali.rice.ksb.api.bus.ServiceDefinition;
import org.kuali.rice.ksb.api.registry.ServiceDescriptor;
import org.kuali.rice.ksb.api.registry.ServiceEndpoint;
import org.kuali.rice.ksb.api.registry.ServiceEndpointStatus;
import org.kuali.rice.ksb.api.registry.ServiceInfo;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public final class LocalService {
	
	private final ServiceDefinition serviceDefinition;
	private final Endpoint endpoint;
	private final ServiceEndpoint serviceEndpoint;
	
	LocalService(String instanceId, ServiceDefinition serviceDefinition) {
		this(instanceId, serviceDefinition, null);
	}
	
	LocalService(String instanceId, ServiceDefinition serviceDefinition, ServiceEndpoint serviceEndpoint) {
		if (StringUtils.isBlank(instanceId)) {
			throw new IllegalArgumentException("instanceId was blank or null");
		}
		if (serviceDefinition == null) {
			throw new IllegalArgumentException("serviceDefinition was null");
		}
		this.serviceDefinition = serviceDefinition;
		this.endpoint = serviceDefinition.establishEndpoint();
		if (serviceEndpoint != null) {
			this.serviceEndpoint = serviceEndpoint;
		} else {
			this.serviceEndpoint = constructServiceEndpoint(instanceId, this.endpoint);
		}
	}
	
	LocalService(LocalService currentLocalService, ServiceEndpoint newServiceEndpoint) {
		this(newServiceEndpoint.getInfo().getInstanceId(), currentLocalService.getServiceDefinition(), newServiceEndpoint);
	}
	
	public QName getServiceName() {
		return endpoint.getServiceConfiguration().getServiceName();
	}
		
	public ServiceDefinition getServiceDefinition() {
		return serviceDefinition;
	}
	
	public Endpoint getEndpoint() {
		return endpoint;
	}
	
	public ServiceEndpoint getServiceEndpoint() {
		return this.serviceEndpoint;
	}
		
	static ServiceEndpoint constructServiceEndpoint(String instanceId, Endpoint endpoint) {
		ServiceInfo.Builder serviceInfo = constructServiceInfo(instanceId, endpoint.getServiceConfiguration());
		ServiceDescriptor.Builder serviceDescriptor = constructDescriptor(endpoint.getServiceConfiguration());
		ServiceEndpoint.Builder builder = ServiceEndpoint.Builder.create(serviceInfo, serviceDescriptor);
		return builder.build();
	}
	
	static ServiceInfo.Builder constructServiceInfo(String instanceId, ServiceConfiguration serviceConfiguration) {
		ServiceInfo.Builder builder = ServiceInfo.Builder.create();
		builder.setInstanceId(instanceId);
		builder.setApplicationId(serviceConfiguration.getApplicationId());
		builder.setChecksum(ChecksumUtils.calculateChecksum(serviceConfiguration));
		builder.setEndpointUrl(serviceConfiguration.getEndpointUrl().toExternalForm());
		builder.setServerIpAddress(RiceUtilities.getIpNumber());
		builder.setServiceName(serviceConfiguration.getServiceName());
		builder.setServiceVersion(serviceConfiguration.getServiceVersion());
		builder.setStatus(ServiceEndpointStatus.ONLINE);
		builder.setType(serviceConfiguration.getType());
		return builder;
	}
	
	static ServiceDescriptor.Builder constructDescriptor(ServiceConfiguration serviceConfiguration) {
		ServiceDescriptor.Builder builder = ServiceDescriptor.Builder.create();
		builder.setDescriptor(ServiceConfigurationSerializationHandler.marshallToXml(serviceConfiguration));
		return builder;
	}
	
}