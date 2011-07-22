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
package org.kuali.rice.kim.api.jaxb;

import org.kuali.rice.core.api.mo.AbstractJaxbModelObject;
import org.kuali.rice.kim.api.identity.principal.EntityNamePrincipalName;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.Collection;

public class StringEntNmPrncpInfoMapEntry extends AbstractJaxbModelObject {
	
	private static final long serialVersionUID = 1L;
	
	@XmlAttribute
	private final String key;
	
	@XmlElement(required=true)
    private final EntityNamePrincipalName value;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
	private StringEntNmPrncpInfoMapEntry() {
	    key = null;
        value = null;
	}

	public StringEntNmPrncpInfoMapEntry(String key, EntityNamePrincipalName value) {
	    super();
	    
	    this.key = key;
	    this.value = value;
	}

    public String getKey() {
        return key;
    }

    public EntityNamePrincipalName getValue() {
        return value;
    }
}
