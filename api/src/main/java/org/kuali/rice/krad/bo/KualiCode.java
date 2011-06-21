/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.bo;

import org.kuali.rice.core.api.mo.common.Coded;

/**
 * Represents a simple code-description business object
 */
public interface KualiCode extends Inactivatable, Coded {

	/**
	 * @param code - Setter for the Code.
	 */
	public void setCode(String code);

	/**
	 * @return code value as string
	 */
	public String getCode();

	/**
	 * @param name - Setter for the name.
	 */
	public void setName(String name);

	/**
	 * @return name value as String
	 */
	public String getName();

	/**
	 * @return Getter for the active field.
	 */
	public boolean isActive();

	/**
	 * @param name - Setter for the active field.
	 */
	public void setActive(boolean a);
}
