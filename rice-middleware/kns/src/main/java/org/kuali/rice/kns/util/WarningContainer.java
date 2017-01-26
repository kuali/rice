/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kns.util;

import org.kuali.rice.krad.util.MessageMap;

import java.util.List;
import java.util.Set;

/**
 * An implementation of {@link MessageContainer} that makes warning messages accessible by the JSP layer
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * @deprecated Only used in KNS classes, use KRAD.
 */
@Deprecated
public class WarningContainer extends MessageContainer {

	public WarningContainer(MessageMap errorMap) {
		super(errorMap);
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see MessageContainer#getMessageCount()
	 */
	@Override
	public int getMessageCount() {
		return getMessageMap().getWarningCount();
	}

	
	/**
	 * @see MessageContainer#getMessagePropertyNames()
	 */
	@Override
	protected Set<String> getMessagePropertyNames() {
		return getMessageMap().getAllPropertiesWithWarnings();
	}

	/**
	 * @see MessageContainer#getMessagePropertyList()
	 */
	@Override
	public List<String> getMessagePropertyList() {
		return getMessageMap().getPropertiesWithWarnings();
	}
	
	/**
	 * @see MessageContainer#getMessagesForProperty(java.lang.String)
	 */
	@Override
	protected List getMessagesForProperty(String propertyName) {
		return getMessageMap().getWarningMessagesForProperty(propertyName);
	}
}
