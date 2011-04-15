/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.rice.krms.api.repository;

/**
 * TODO... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public enum PropositionParameterType {

	CONSTANT("C"),
	TERM("T)"),
	FUNCTION("F"),
	OPERATOR("O");
	
	private final String code;
	
	private PropositionParameterType(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public static PropositionParameterType fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (PropositionParameterType propositionParameterType : values()) {
			if (propositionParameterType.code.equals(code)) {
				return propositionParameterType;
			}
		}
		throw new IllegalArgumentException("Failed to locate the PropositionParameterType with the given code: " + code);
	}
	
}
