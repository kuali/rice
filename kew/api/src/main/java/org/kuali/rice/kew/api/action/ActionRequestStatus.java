package org.kuali.rice.kew.api.action;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.mo.common.Coded;

@XmlRootElement(name = "actionRequestStatus")
@XmlType(name = "ActionRequestStatusType")
@XmlEnum
public enum ActionRequestStatus implements Coded {

	/**
	 * Code to indicate the request has been satisfied
	 */
    @XmlEnumValue("D") DONE("D", "DONE"),
    
    /**
     * Code to indicate the request is currently active
     */
    @XmlEnumValue("A") ACTIVATED("A", "ACTIVATED"),
    
    /**
     * Code to indicate the request has not been activated
     */
    @XmlEnumValue("I") INITIALIZED("I", "INITIALIZED");
	
	private final String code;
	private final String label;
	
	ActionRequestStatus(String code, String label) {
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
	
	public static ActionRequestStatus fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (ActionRequestStatus request : values()) {
			if (request.code.equals(code)) {
				return request;
			}
		}
		throw new IllegalArgumentException("Failed to locate the ActionRequestStatus with the given code: " + code);
	}
	
}
