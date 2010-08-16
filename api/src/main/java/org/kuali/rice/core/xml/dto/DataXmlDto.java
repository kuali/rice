/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.core.xml.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.xml.CoreNamespaceConstants;
import org.kuali.rice.kim.xml.GroupXmlDto;
import org.kuali.rice.kim.xml.KimNamespaceConstants;

/**
 * This is a description of what this class does - g don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = "data", namespace = CoreNamespaceConstants.CORE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="data",namespace=CoreNamespaceConstants.CORE)
public class DataXmlDto implements Serializable {

	@XmlElementWrapper(name = "groups", namespace=KimNamespaceConstants.GROUP)
	@XmlElement(name = "group", namespace=KimNamespaceConstants.GROUP)
	List<GroupXmlDto> groups = new ArrayList<GroupXmlDto>();

	public List<GroupXmlDto> getGroups() {
		return this.groups;
	}

	public void setGroups(List<GroupXmlDto> groups) {
		this.groups = groups;
	}

		
}