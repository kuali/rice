/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krms.api.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.jaxb.MapStringStringAdapter;
import org.w3c.dom.Element;

/**
 * A set of criteria for selecting a {@link ContextDefinition}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = ContextSelectionCriteria.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ContextSelectionCriteria.Constants.TYPE_NAME, propOrder = {
		ContextSelectionCriteria.Elements.NAMESPACE_CODE,
		ContextSelectionCriteria.Elements.NAME,
		ContextSelectionCriteria.Elements.CONTEXT_QUALIFIERS,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class ContextSelectionCriteria {

	@XmlElement(name = Elements.NAMESPACE_CODE, required = true)
	private final String namespaceCode;
	
	@XmlElement(name = Elements.NAME, required = false)
	private final String name;
	
	@XmlElement(name = Elements.CONTEXT_QUALIFIERS)
	@XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
	private final Map<String, String> contextQualifiers;
	
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;
	
    /**
     * Only used by JAXB.
     */
    @SuppressWarnings("unused")
	private ContextSelectionCriteria() {
		this.namespaceCode = null;
		this.name = null;
		this.contextQualifiers = null;
	}
	
	private ContextSelectionCriteria(String namespaceCode, String name, Map<String, String> contextQualifiers) {
		this.namespaceCode = namespaceCode;
		this.name = name;
		this.contextQualifiers = new HashMap<String, String>();
		if (contextQualifiers != null) {
			this.contextQualifiers.putAll(contextQualifiers);
		}
	}
	
	public static ContextSelectionCriteria newCriteria(String namespaceCode, String name, Map<String, String> contextQualifiers) {
		return new ContextSelectionCriteria(namespaceCode, name, contextQualifiers);
	}
	
	public static ContextSelectionCriteria newCriteria(String namespaceCode, Map<String, String> contextQualifiers) {
		return newCriteria(namespaceCode, null, contextQualifiers);
	}
	
	public static ContextSelectionCriteria newCriteria(Map<String, String> contextQualifiers) {
		return newCriteria(null, contextQualifiers);
	}
	
	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public String getName() {
		return this.name;
	}

	public Map<String, String> getContextQualifiers() {
		return this.contextQualifiers;
	}

	@Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
	
    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "contextSelectionCriteria";
        final static String TYPE_NAME = "ContextSelectionCriteriaType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }
    
	/**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshaled to XML.
     */
    static class Elements {
        final static String NAMESPACE_CODE = "namespaceCode";
        final static String NAME = "name";
        final static String CONTEXT_QUALIFIERS = "contextQualifiers";
    }
	
}
