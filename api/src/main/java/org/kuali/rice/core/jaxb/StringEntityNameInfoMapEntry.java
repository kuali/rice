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
package org.kuali.rice.core.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.kuali.rice.kim.bo.entity.dto.KimEntityNameInfo;

/**
 * This is a description of what this class does - jim7 don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class StringEntityNameInfoMapEntry {
	
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute
	String key;
	
	@XmlElement(required=true) // maxoccurs == minoccurs == 1
	KimEntityNameInfo value;
	
	/**
	 * 
	 */
	public StringEntityNameInfoMapEntry() {
	    super();
	}
	
	/**
	 * @param name
	 * @param value
	 */
	public StringEntityNameInfoMapEntry(String key, KimEntityNameInfo value) {
	    super();
	    
	    this.key = key;
	    this.value = value;
	}

}
