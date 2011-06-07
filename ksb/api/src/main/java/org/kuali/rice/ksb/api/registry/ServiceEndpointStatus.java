package org.kuali.rice.ksb.api.registry;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.util.jaxb.EnumStringAdapter;

/**
 * Defines the possible statuses for a service endpoint in the KSB registry.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = "serviceEndpointStatus")
@XmlType(name = "ServiceEndpointStatusType")
@XmlEnum
public enum ServiceEndpointStatus implements Coded {

	/**
	 * Indicates the service is online and available to receieve requests.
	 */
	@XmlEnumValue("A") ONLINE("A"),
	
	/**
	 * Indicates the service has been taken offline, most likely as the
	 * result of the instance hosting the service being taken offline
	 * (i.e. for maintenance or otherwise)
	 */
	@XmlEnumValue("I") OFFLINE("I"),
	
	/**
	 * Indicates the service has been disabled because the registry has
	 * detected that it, or it's host instance is defective or not
	 * processing requests properly.
	 */
	@XmlEnumValue("D") DISABLED("D");
	
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
