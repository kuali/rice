package org.kuali.rice.kew.api.document;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.mo.common.Coded;

/**
 * TODO...
 * 
 * @author ewestfal
 *
 */
@XmlRootElement(name = "documentStatus")
@XmlType(name = "DocumentStatusType")
@XmlEnum
public enum DocumentStatus implements Coded {

	@XmlEnumValue("I") INITIATED("I"),
	@XmlEnumValue("S") SAVED("S"),
	@XmlEnumValue("R") ENROUTE("R"),
	@XmlEnumValue("P") PROCESSED("P"),
	@XmlEnumValue("F") FINAL("F"),
	@XmlEnumValue("X") CANCELED("X"),
	@XmlEnumValue("D") DISAPPROVED("D"),
	@XmlEnumValue("E") EXCEPTION("E");
	
	private final String code;
	
	private DocumentStatus(String code) {
		this.code = code;
	}
	
	@Override
	public String getCode() {
		return code;
	}
	
	public static DocumentStatus fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (DocumentStatus status : values()) {
			if (status.code.equals(code)) {
				return status;
			}
		}
		throw new IllegalArgumentException("Failed to locate the DocumentStatus with the given code: " + code);
	}
	
}
