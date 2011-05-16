package org.kuali.rice.ksb.impl.bus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.core.util.io.SerializationUtils;
import org.kuali.rice.ksb.api.bus.Endpoint;
import org.kuali.rice.ksb.api.bus.ServiceConfiguration;
import org.kuali.rice.ksb.api.bus.ServiceDefinition;
import org.kuali.rice.ksb.api.registry.ServiceDescriptor;
import org.kuali.rice.ksb.api.registry.ServiceEndpoint;
import org.kuali.rice.ksb.api.registry.ServiceEndpointStatus;
import org.kuali.rice.ksb.api.registry.ServiceInfo;

public final class LocalService {
	
	private final ServiceDefinition serviceDefinition;
	private final Endpoint endpoint;
	private final ServiceEndpoint serviceEndpoint;
	
	LocalService(String instanceId, ServiceDefinition serviceDefinition) {
		if (StringUtils.isBlank(instanceId)) {
			throw new IllegalArgumentException("instanceId was blank or null");
		}
		if (serviceDefinition == null) {
			throw new IllegalArgumentException("serviceDefinition was null");
		}
		this.serviceDefinition = serviceDefinition;
		this.endpoint = serviceDefinition.establishEndpoint();
		this.serviceEndpoint = constructServiceEndpoint(instanceId, this.endpoint);
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
		builder.setApplicationNamespace(serviceConfiguration.getApplicationNamespace());
		builder.setChecksum(calculateChecksum(serviceConfiguration));
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
		builder.setDescriptor(SerializationUtils.serializeToBase64(serviceConfiguration));
		return builder;
	}
	
	/**
	 * Creates a checksum for the given ServiceConfiguration.
	 * 
	 * @param serviceConfiguration The configuration for which to calcuate the checksum
	 * @return A checksum value for the object.
	 */
	static String calculateChecksum(ServiceConfiguration serviceConfiguration) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(serviceConfiguration);
        } catch (IOException e) {
            throw new RiceRuntimeException(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {}
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return new String( Base64.encodeBase64( md.digest( bos.toByteArray() ) ), "UTF-8");
        } catch( GeneralSecurityException ex ) {
        	throw new RiceRuntimeException(ex);
        } catch( UnsupportedEncodingException ex ) {
        	throw new RiceRuntimeException(ex);
        }
	}
	
}