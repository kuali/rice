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
package org.kuali.rice.core.api.util.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import java.io.Serializable;
import java.util.Map;

/**
 * Single String-String key-value pair for 
 * marshalling/unmarshalling. Need this rather than
 * general Map.Entry<String, String> to specify
 * cardinality in resulting wsdl's.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "StringMapEntryType")
public final class StringMapEntry implements Serializable {
	
	private static final long serialVersionUID = -9609663434312103L;

	@XmlAttribute (name = "key")
	private final String key;
	
	@XmlValue
	private final String value;
	
	/**
	 * Used only by JAXB.
	 */
	@SuppressWarnings("unused")
	private StringMapEntry() {
		this.key = null;
		this.value = null;
	}
	
	public StringMapEntry(String key, String value) {
	    this.key = key;
	    this.value = value;
	}

	public StringMapEntry(Map.Entry<String, String> e) {
	    this.key = e.getKey();
	    this.value = e.getValue();
	}

	public String getKey() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}
	
}
