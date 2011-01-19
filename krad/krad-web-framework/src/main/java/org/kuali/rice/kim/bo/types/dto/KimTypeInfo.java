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
package org.kuali.rice.kim.bo.types.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.KimType;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimTypeInfo extends TransientBusinessObjectBase implements KimType, Serializable {

	private static final long serialVersionUID = 4229466320569714756L;
	
	protected String kimTypeId;
	protected String name;
	protected String namespaceCode;
	protected String kimTypeServiceName;

	protected List<KimTypeAttributeInfo> attributeDefinitions;

	public KimTypeInfo() {
		attributeDefinitions = new ArrayList<KimTypeAttributeInfo> ();
	}

	public List<KimTypeAttributeInfo> getAttributeDefinitions() {
		return attributeDefinitions;
	}

	public String getKimTypeId() {
		return kimTypeId;
	}

	public String getKimTypeServiceName() {
		return kimTypeServiceName;
	}

	public String getName() {
		return name;
	}

	public void setAttributeDefinitions(List<KimTypeAttributeInfo> attributeDefinitions) {
		this.attributeDefinitions = attributeDefinitions;
	}
	
	public KimTypeAttributeInfo getAttributeDefinition( String kimAttributeId ) {
		if ( kimAttributeId == null || attributeDefinitions == null ) {
			return null;
		}
		for ( KimTypeAttributeInfo def : attributeDefinitions ) {
			if ( def.kimAttributeId.equals( kimAttributeId ) ) {
				return def;
			}
		}
		return null;
	}

	public KimTypeAttributeInfo getAttributeDefinitionByName( String attributeName ) {
		if ( attributeName == null || attributeDefinitions == null ) {
			return null;
		}
		for ( KimTypeAttributeInfo def : attributeDefinitions ) {
			if ( StringUtils.equals(def.attributeName, attributeName) ) {
				return def;
			}
		}
		return null;
	}
	
	public void setKimTypeServiceName(String kimTypeServiceName) {
		this.kimTypeServiceName = kimTypeServiceName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}
}
