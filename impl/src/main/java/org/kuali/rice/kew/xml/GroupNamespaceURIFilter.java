/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kew.xml;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.core.xml.CoreNamespaceConstants;


/**
 * This class transforms all the URIs for the group object
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class GroupNamespaceURIFilter extends AbstractTransformationFilter {

	public static final String GROUP_URI_OLD = "ns:workflow/Group";
	public static final String GROUP_URI_NEW = "http://rice.kuali.org/xsd/kim/group";

	/**
	 * This overridden method returns a list of KeyValue objects. the key is the
	 * element name and the value
	 * 
	 * @see org.kuali.rice.kew.xml.AbstractTransformationFilter#getElementTransformationList()
	 */
	@Override
	public List<KeyValue> getElementTransformationList() {
		List<KeyValue> rList = new ArrayList<KeyValue>();

		rList.add(new ConcreteKeyValue("groups", GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group", GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.groupName", GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.namespaceCode", GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.groupDescription", GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.active", GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.type", GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.type.namespace", GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.type.name", GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.attributes", GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.members", GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.members.principalName",GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.members.principalId", GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.members.groupName", GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.members.groupName.name",GROUP_URI_OLD));
		rList.add(new ConcreteKeyValue("groups.group.members.groupName.namespace", GROUP_URI_OLD));

		return rList;
	}

	/**
	 * This overridden method sets the groups starting element.
	 * 
	 * @see org.kuali.rice.kew.xml.AbstractTransformationFilter#getStartingElement()
	 */
	@Override
	public String getStartingElementPath() {
		return "data";
	}

	/**
	 * This method sets the URI
	 * 
	 * @see org.kuali.rice.kew.xml.AbstractTransformationFilter#transformEndElement(org.kuali.rice.kew.xml.AbstractTransformationFilter.CurrentElement)
	 */
	@Override
	public CurrentElement transformEndElement(CurrentElement currentElement) {
		if("groups.group.attributes".equals(this.getTrimmedCurrentElementKey(currentElement.getNameKey()))){
			currentElement.setUri(CoreNamespaceConstants.CORE);
		}else{
			currentElement.setUri(GROUP_URI_NEW);
		}
		return currentElement;
	}

	/**
	 * This method sets the URI of the element
	 * 
	 * @see org.kuali.rice.kew.xml.AbstractTransformationFilter#transformStartElement(org.kuali.rice.kew.xml.AbstractTransformationFilter.CurrentElement)
	 */
	@Override
	public CurrentElement transformStartElement(CurrentElement currentElement) {
		currentElement.setUri(GROUP_URI_NEW);
		return currentElement;
	}
}
