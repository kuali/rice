package org.kuali.rice.kew.api.action;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.mo.common.Coded;

@XmlRootElement(name = "actionRequestPolicy")
@XmlType(name = "ActionRequestPolicyType")
@XmlEnum
public enum ActionRequestPolicy implements Coded {	
	
	@XmlEnumValue("F") FIRST("F", "FIRST"),
	@XmlEnumValue("A") ALL("A", "ALL");
	
	private final String code;
	private final String label;
	
	ActionRequestPolicy(String code, String label) {
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
		
	public static ActionRequestPolicy fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (ActionRequestPolicy value : values()) {
			if (value.code.equals(code)) {
				return value;
			}
		}
		throw new IllegalArgumentException("Failed to locate the ActionRequestPolicy with the given code: " + code);
	}
	
}
