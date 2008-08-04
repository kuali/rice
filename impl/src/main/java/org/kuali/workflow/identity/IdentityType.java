/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.workflow.identity;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public enum IdentityType {

	USER ("U"),
	GROUP ("G"),
	ROLE ("R"),
	QUALIFIED_ROLE ("Q");

	private final String code;

	IdentityType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static IdentityType fromCode(String code) {
		for (IdentityType type : IdentityType.values()) {
			if (type.getCode().equals(code)) {
				return type;
			}
		}
		return null;
	}

}
