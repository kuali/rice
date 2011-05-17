package org.kuali.rice.ksb.api.registry;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.util.jaxb.EnumStringAdapter;

// TODO review statuses and annotate for JAXB
@XmlType(name = "EvaluationOperatorType")
@XmlJavaTypeAdapter(ServiceEndpointStatus.Adapter.class)
public enum ServiceEndpointStatus implements Coded {

	ONLINE("A"),
	OFFLINE("I"),
	DISABLED("D");
	
	private final String code;
	
	ServiceEndpointStatus(final String code) {
		this.code = code;
	}
	
	@Override
	public String getCode() {
		return this.code;
	}
	
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
	
	static final class Adapter extends EnumStringAdapter<ServiceEndpointStatus> {
		
		protected Class<ServiceEndpointStatus> getEnumClass() {
			return ServiceEndpointStatus.class;
		}
		
	}
	
}
