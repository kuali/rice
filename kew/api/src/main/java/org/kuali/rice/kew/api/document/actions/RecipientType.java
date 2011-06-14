package org.kuali.rice.kew.api.document.actions;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.mo.common.Coded;

@XmlRootElement(name = "recipientType")
@XmlType(name = "RecipientTypeType")
@XmlEnum
public enum RecipientType implements Coded {

	@XmlEnumValue("W") GROUP("W"),
	@XmlEnumValue("U") PRINCIPAL("U"),
	@XmlEnumValue("R") ROLE("R");
	
	private final String code;
	
	RecipientType(String code) {
		this.code = code;
	}
	
	@Override
	public String getCode() {
		return code;
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
