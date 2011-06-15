package org.kuali.rice.kew.api.action;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.mo.common.Coded;

@XmlRootElement(name = "recipientType")
@XmlType(name = "RecipientTypeType")
@XmlEnum
public enum RecipientType implements Coded {
	
	@XmlEnumValue("U") PRINCIPAL("U", "principal"),
	@XmlEnumValue("W") GROUP("W", "group"),
	@XmlEnumValue("R") ROLE("R", "role");
	
	private final String code;
	private final String label;
	
	RecipientType(String code, String label) {
		this.code = code;
		this.label = label;
	}
	
	@Override
	public String getCode() {
		return code;
	}
	
	public String getLabel() {
		return label;
	}
		
	public static RecipientType fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (RecipientType request : values()) {
			if (request.code.equals(code)) {
				return request;
			}
		}
		throw new IllegalArgumentException("Failed to locate the RecipientType with the given code: " + code);
	}
	
}
