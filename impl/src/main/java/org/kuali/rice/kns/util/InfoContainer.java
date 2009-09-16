/*
 * Copyright 2007-2009 The Kuali Foundation
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

import java.util.List;
import java.util.Set;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class InfoContainer extends MessageContainer {
	public InfoContainer(MessageMap errorMap) {
		super(errorMap);
	}
	
	/**
	 * @see org.kuali.rice.kns.util.MessageContainer#getMessageCount()
	 */
	@Override
	public int getMessageCount() {
		return getMessageMap().getInfoCount();
	}

	/**
	 * @see org.kuali.rice.kns.util.MessageContainer#getMessagePropertyList()
	 */
	@Override
	public List<String> getMessagePropertyList() {
		return getMessageMap().getPropertiesWithInfo();
	}

	/**
	 * @see org.kuali.rice.kns.util.MessageContainer#getMessagePropertyNames()
	 */
	@Override
	protected Set<String> getMessagePropertyNames() {
		return getMessageMap().getAllPropertiesWithInfo();
	}

	/**
	 * @see org.kuali.rice.kns.util.MessageContainer#getMessagesForProperty(java.lang.String)
	 */
	@Override
	protected List getMessagesForProperty(String propertyName) {
		return getMessageMap().getInfoMessagesForProperty(propertyName);
	}

}
