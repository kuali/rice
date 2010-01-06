/*
 * Copyright 2005-2007 The Kuali Foundation
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

import java.util.List;

/**
 * Represents an extension to a data object.  An Extension is associated with an Attribute which
 * defines the properties of the Extension.  An Extension has a list of ExtensionData which
 * store key-values pairs that represent the extension data.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Extension {

	/**
	 * Return the name of the attribute which defines the properties of this Extension.
	 */
	public String getAttributeName();

	/**
	 * Return the data associated with this Extension.
	 */
	public List<ExtensionData> getData();

	/**
	 * Returns the value of the ExtensionData with the specified key.
	 */
	public String getDataValue(String key);

}
