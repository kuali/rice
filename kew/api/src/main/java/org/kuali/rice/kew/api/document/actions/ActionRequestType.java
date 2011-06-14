package org.kuali.rice.kew.api.document.actions;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.mo.common.Coded;

@XmlRootElement(name = "actionRequestType")
@XmlType(name = "ActionRequestTypeType")
@XmlEnum
public enum ActionRequestType implements Coded {

	@XmlEnumValue("C") COMPLETE("C"),
	@XmlEnumValue("A") APPROVE("A"),
	@XmlEnumValue("K") ACKNOWLEDGE("K"),
	@XmlEnumValue("F") FYI("F");
	
	private final String code;
	
	ActionRequestType(String code) {
		this.code = code;
	}
	
	@Override
	public String getCode() {
		return code;
	}
	
	public String getLabel() {
		return name();
	}
	
	public static ActionRequestType fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (ActionRequestType request : values()) {
			if (request.code.equals(code)) {
				return request;
			}
		}
		throw new IllegalArgumentException("Failed to locate the ActionRequestType with the given code: " + code);
	}
	
}
