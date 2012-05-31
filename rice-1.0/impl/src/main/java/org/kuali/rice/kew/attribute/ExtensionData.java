/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
package org.kuali.rice.kew.attribute;

/**
 * A key-value representation of an extension to a data object.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ExtensionData {

	/**
	 * @return the key of this extension.
     */
	public String getKey();

	/**
	 * @return the value of this extension.
     */
	public String getValue();

	/**
	 * @param value of this extension to the given value.
	 */
	public void setValue(String value);

}
