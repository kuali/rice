package org.kuali.rice.ksb.api.registry;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.util.jaxb.EnumStringAdapter;

/**
 * Defines the possible statuses for a service endpoint in the KSB registry.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public enum ServiceEndpointStatus implements Coded {

	/**
	 * Indicates the service is online and available to receieve requests.
	 */
	ONLINE("A"),
	
	/**
	 * Indicates the service has been taken offline, most likely as the
	 * result of the instance hosting the service being taken offline
	 * (i.e. for maintenance or otherwise)
	 */
	OFFLINE("I"),
	
	/**
	 * Indicates the service has been disabled because the registry has
	 * detected that it, or it's host instance is defective or not
	 * processing requests properly.
	 */
	DISABLED("D");
	
	private final String code;
	
	ServiceEndpointStatus(final String code) {
		this.code = code;
	}
	
	@Override
	public String getCode() {
		return this.code;
	}
	
	/**
	 * Returns the endpoint status for the given status code.  This method will
	 * return null if the given code value is null.
	 * 
	 * @param code the code for which to locate the {@link ServiceEndpointStatus}
	 * @return the {@link ServiceEndpointStatus} which has the given code, or null
	 * if the given code value was null
	 * @throws IllegalArgumentException if an endpoint status does not match the given code
	 */
	public static ServiceEndpointStatus fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (ServiceEndpointStatus status : values()) {
			if (status.code.equals(code)) {
				return status;
			}
		}
		throw new IllegalArgumentException("Failed to locate the ServiceEndpointStatus with the given code: " + code);
	}
	
	/**
	 * Adapts the ServiceEndpointStatus to and from a string during JAXB operations.
	 */
	static final class Adapter extends EnumStringAdapter<ServiceEndpointStatus> {
		
		protected Class<ServiceEndpointStatus> getEnumClass() {
			return ServiceEndpointStatus.class;
		}
		
	}
	
}
