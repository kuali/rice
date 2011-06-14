package org.kuali.rice.kew.api.document.actions;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.mo.common.Coded;

@XmlRootElement(name = "delegationType")
@XmlType(name = "DelegationTypeType")
@XmlEnum
public enum DelegationType implements Coded {	
	
	@XmlEnumValue("P") PRIMARY("P", "Primary"),
	@XmlEnumValue("S") SECONDARY("S", "Secondary");
	
	private final String code;
	private final String label;
	
	DelegationType(String code, String label) {
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
		
	public static DelegationType fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (DelegationType value : values()) {
			if (value.code.equals(code)) {
				return value;
			}
		}
		throw new IllegalArgumentException("Failed to locate the DelegationType with the given code: " + code);
	}
	
}
