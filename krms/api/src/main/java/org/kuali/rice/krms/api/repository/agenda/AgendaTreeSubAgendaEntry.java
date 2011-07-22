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
package org.kuali.rice.krms.api.repository.agenda;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = AgendaTreeSubAgendaEntry.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AgendaTreeSubAgendaEntry.Constants.TYPE_NAME, propOrder = {
		AgendaTreeSubAgendaEntry.Elements.AGENDA_ITEM_ID,
		AgendaTreeSubAgendaEntry.Elements.SUB_AGENDA_ID,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class AgendaTreeSubAgendaEntry extends AbstractDataTransferObject implements AgendaTreeEntryDefinition {

	private static final long serialVersionUID = 8594116503548506936L;

	@XmlElement(name = Elements.AGENDA_ITEM_ID, required = true)
	private final String agendaItemId;
	
	@XmlElement(name = Elements.SUB_AGENDA_ID, required = true)
	private final String subAgendaId;
		
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	/**
	 * Used only by JAXB.
	 */
	private AgendaTreeSubAgendaEntry() {
		this.agendaItemId = null;
		this.subAgendaId = null;
	}
	
	private AgendaTreeSubAgendaEntry(Builder builder) {
		this.agendaItemId = builder.getAgendaItemId();
		this.subAgendaId = builder.getSubAgendaId();
	}
	
	@Override
	public String getAgendaItemId() {
		return agendaItemId;
	}
	
	public String getSubAgendaId() {
		return this.subAgendaId;
	}

	public static class Builder implements ModelBuilder, Serializable {
        
		private static final long serialVersionUID = 3548736700798501429L;
		
		private String agendaItemId;
		private String subAgendaId;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String agendaItemId, String subAgendaId) {
        	setAgendaItemId(agendaItemId);
        	setSubAgendaId(subAgendaId);
        }
        
        public static Builder create(String agendaItemId, String subAgendaId){
        	return new Builder(agendaItemId, subAgendaId);
        }
        
        public String getAgendaItemId() {
			return this.agendaItemId;
		}

		public String getSubAgendaId() {
			return this.subAgendaId;
		}
		
		public void setAgendaItemId(String agendaItemId) {
			if (agendaItemId == null) {
				throw new IllegalArgumentException("agendaItemId was null");
			}
			this.agendaItemId = agendaItemId;
		}

		public void setSubAgendaId(String subAgendaId) {
			if (subAgendaId == null) {
				throw new IllegalArgumentException("subAgendaId was null");
			}
			this.subAgendaId = subAgendaId;
		}

		@Override
        public AgendaTreeSubAgendaEntry build() {
            return new AgendaTreeSubAgendaEntry(this);
        }
		
    }
	
	/**
	 * Defines some internal constants used on this class.
	 */
	static class Constants {
		final static String ROOT_ELEMENT_NAME = "agendaTreeSubAgendaEntry";
		final static String TYPE_NAME = "AgendaTreeSubAgendaEntryType";
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	static class Elements {
		final static String AGENDA_ITEM_ID = "agendaItemId";
		final static String SUB_AGENDA_ID = "subAgendaId";
	}

}
