/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.kew.api.action;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.w3c.dom.Element;

@XmlRootElement(name = RequestedActions.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RequestedActions.Constants.TYPE_NAME, propOrder = {
		RequestedActions.Elements.COMPLETE_REQUESTED,
		RequestedActions.Elements.APPROVE_REQUESTED,
		RequestedActions.Elements.ACKNOWLEDGE_REQUESTED,
		RequestedActions.Elements.FYI_REQUESTED,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class RequestedActions extends AbstractDataTransferObject {
    
	private static final long serialVersionUID = -6600754341497697330L;

    @XmlElement(name = Elements.COMPLETE_REQUESTED, required = true)
    private final boolean completeRequested;
	
	@XmlElement(name = Elements.APPROVE_REQUESTED, required = true)
    private final boolean approveRequested;
	
	@XmlElement(name = Elements.ACKNOWLEDGE_REQUESTED, required = true)
	private final boolean acknowledgeRequested;
	
	@XmlElement(name = Elements.FYI_REQUESTED, required = true)
	private final boolean fyiRequested;
    
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    private RequestedActions() {
    	this.completeRequested = false;
    	this.approveRequested = false;
    	this.acknowledgeRequested = false;
    	this.fyiRequested = false;
    }
    
    private RequestedActions(boolean completeRequested, boolean approveRequested, boolean acknowledgeRequested, boolean fyiRequested) {
    	this.completeRequested = completeRequested;
    	this.approveRequested = approveRequested;
    	this.acknowledgeRequested = acknowledgeRequested;
    	this.fyiRequested = fyiRequested;
    }
    
    public static RequestedActions create(boolean completeRequested, boolean approveRequested, boolean acknowledgeRequested, boolean fyiRequested) {
    	return new RequestedActions(completeRequested, approveRequested, acknowledgeRequested, fyiRequested);
    }
    
	public boolean isCompleteRequested() {
		return completeRequested;
	}

	public boolean isApproveRequested() {
		return approveRequested;
	}

	public boolean isAcknowledgeRequested() {
		return acknowledgeRequested;
	}

	public boolean isFyiRequested() {
		return fyiRequested;
    }
    
    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "requestedActions";
        final static String TYPE_NAME = "RequestedActionsType";
    }
    
    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String COMPLETE_REQUESTED = "completeRequested";
        final static String APPROVE_REQUESTED = "approveRequested";
        final static String ACKNOWLEDGE_REQUESTED = "acknowledgeRequested";
        final static String FYI_REQUESTED = "fyiRequested";
    }

}
