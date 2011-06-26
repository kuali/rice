/*

 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.kew.api.action;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.kew.api.document.Document;
import org.w3c.dom.Element;

@XmlRootElement(name = DocumentActionResult.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentActionResult.Constants.TYPE_NAME, propOrder = {
		DocumentActionResult.Elements.DOCUMENT,
		DocumentActionResult.Elements.VALID_ACTIONS,
		DocumentActionResult.Elements.REQUESTED_ACTIONS,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentActionResult implements Serializable {
    
	private static final long serialVersionUID = -3916503634900791018L;

	@XmlElement(name = Elements.DOCUMENT, required = true)
    private final Document document;
	
	@XmlElement(name = Elements.VALID_ACTIONS, required = false)
	private final ValidActions validActions;
	
	@XmlElement(name = Elements.REQUESTED_ACTIONS, required = false)
	private final RequestedActions requestedActions;
	    
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    private DocumentActionResult() {
    	this.document = null;
    	this.validActions = null;
    	this.requestedActions = null;
    }
    
    private DocumentActionResult(Document document, ValidActions validActions, RequestedActions requestedActions) {
    	if (document == null) {
    		throw new IllegalArgumentException("document was null");
    	}
        this.document = document;
        this.validActions = validActions;
        this.requestedActions = requestedActions;
    }
    
    public static DocumentActionResult create(Document document, ValidActions validActions, RequestedActions requestedActions) {
    	return new DocumentActionResult(document, validActions, requestedActions);
    }
    
    public Document getDocument() {
        return document;
    }
    
    public ValidActions getValidActions() {
    	return validActions;
    }
    
    public RequestedActions getRequestedActions() {
    	return requestedActions;
    }
    
	@Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(object, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentActionResult";
        final static String TYPE_NAME = "DocumentActionResultType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] { CoreConstants.CommonElements.FUTURE_ELEMENTS };
    }
    
    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String DOCUMENT = "document";
        final static String VALID_ACTIONS = "validActions";
        final static String REQUESTED_ACTIONS = "requestedActions";
    }

}
