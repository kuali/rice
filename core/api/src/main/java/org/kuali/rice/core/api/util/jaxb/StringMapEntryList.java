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
package org.kuali.rice.core.api.util.jaxb;

import org.kuali.rice.core.api.mo.AbstractJaxbModelObject;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StringMapEntryListType")
public class StringMapEntryList extends AbstractJaxbModelObject {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "entry")
	private final List<StringMapEntry> entries;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

	@SuppressWarnings("unused")
	private StringMapEntryList() {
		this.entries = null;
	}
	
	public StringMapEntryList(List<StringMapEntry> entries) {
		this.entries = new ArrayList<StringMapEntry>(entries);
	}
	
	/**
	 * @return the attribute
	 */
	public List<StringMapEntry> getEntries() {
		if (this.entries == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(entries);
	}
}
