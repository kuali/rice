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
package org.kuali.rice.krms.api.repository.proposition;

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.util.jaxb.EnumStringAdapter;

/**
 * TODO... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public enum PropositionType implements Coded {

	COMPOUND("C"),
	SIMPLE("S");
	
	private final String code;
	
	private PropositionType(String code) {
		this.code = code;
	}
	
	/**
	 * Returns the operator code for this evaluation operator.
	 * 
	 * @return the operatorCode
	 */
	@Override
	public String getCode() {
		return code;
	}
	
	public static final Set<String> VALID_TYPE_CODES = new HashSet<String>();
	static {
		for (PropositionType propositionType : values()) {
			VALID_TYPE_CODES.add(propositionType.getCode());
		}
	}
	
	public static PropositionType fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (PropositionType propositionType : values()) {
			if (propositionType.code.equals(code)) {
				return propositionType;
			}
		}
		throw new IllegalArgumentException("Failed to locate the PropositionType with the given code: " + code);
	}
	
	static final class Adapter extends EnumStringAdapter<PropositionType> {
		
		protected Class<PropositionType> getEnumClass() {
			return PropositionType.class;
		}
		
	}
	
}
