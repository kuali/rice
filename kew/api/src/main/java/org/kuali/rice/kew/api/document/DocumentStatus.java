/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	
	public String getLabel() {
	    return name();
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
