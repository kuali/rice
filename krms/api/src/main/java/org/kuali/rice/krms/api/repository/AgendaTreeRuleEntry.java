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
import org.kuali.rice.core.api.mo.ModelBuilder;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = AgendaTreeRuleEntry.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AgendaTreeRuleEntry.Constants.TYPE_NAME, propOrder = {
		AgendaTreeRuleEntry.Elements.AGENDA_ITEM_ID,
		AgendaTreeRuleEntry.Elements.RULE_ID,
		AgendaTreeRuleEntry.Elements.IF_TRUE,
		AgendaTreeRuleEntry.Elements.IF_FALSE,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class AgendaTreeRuleEntry implements AgendaTreeEntryDefinition {

	private static final long serialVersionUID = 8594116503548506936L;

	@XmlElement(name = Elements.AGENDA_ITEM_ID, required = true)
	private final String agendaItemId;
	
	@XmlElement(name = Elements.RULE_ID, required = true)
	private final String ruleId;
	
	@XmlElement(name = Elements.IF_TRUE, required = false)
	private final AgendaTreeDefinition ifTrue;
	
	@XmlElement(name = Elements.IF_FALSE, required = false)
	private final AgendaTreeDefinition ifFalse;
		
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	/**
	 * Used only by JAXB.
	 */
	private AgendaTreeRuleEntry() {
		this.agendaItemId = null;
		this.ruleId = null;
		this.ifTrue = null;
		this.ifFalse = null;
	}
	
	private AgendaTreeRuleEntry(Builder builder) {
		this.agendaItemId = builder.getAgendaItemId();
		this.ruleId = builder.getRuleId();
		this.ifTrue = builder.getIfTrue() == null ? null : builder.getIfTrue().build();
		this.ifFalse = builder.getIfFalse() == null ? null : builder.getIfFalse().build();
	}
	
	@Override
	public String getAgendaItemId() {
		return agendaItemId;
	}
	
	public String getRuleId() {
		return this.ruleId;
	}

	public AgendaTreeDefinition getIfTrue() {
		return this.ifTrue;
	}

	public AgendaTreeDefinition getIfFalse() {
		return this.ifFalse;
	}

	public static class Builder implements ModelBuilder, Serializable {
        
		private static final long serialVersionUID = 3548736700798501429L;
		
		private String agendaItemId;
		private String ruleId;
		private AgendaTreeDefinition.Builder ifTrue;
		private AgendaTreeDefinition.Builder ifFalse;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String agendaItemId, String ruleId) {
        	setAgendaItemId(agendaItemId);
        	setRuleId(ruleId);
        }
        
        public static Builder create(String agendaItemId, String ruleId){
        	return new Builder(agendaItemId, ruleId);
        }
        
        public String getAgendaItemId() {
			return this.agendaItemId;
		}

		public String getRuleId() {
			return this.ruleId;
		}

		public AgendaTreeDefinition.Builder getIfTrue() {
			return this.ifTrue;
		}

		public AgendaTreeDefinition.Builder getIfFalse() {
			return this.ifFalse;
		}
		
		public void setAgendaItemId(String agendaItemId) {
			if (agendaItemId == null) {
				throw new IllegalArgumentException("agendaItemId was null");
			}
			this.agendaItemId = agendaItemId;
		}

		public void setRuleId(String ruleId) {
			if (ruleId == null) {
				throw new IllegalArgumentException("ruleId was null");
			}
			this.ruleId = ruleId;
		}

		public void setIfTrue(AgendaTreeDefinition.Builder ifTrue) {
			this.ifTrue = ifTrue;
		}

		public void setIfFalse(AgendaTreeDefinition.Builder ifFalse) {
			this.ifFalse = ifFalse;
		}

		@Override
        public AgendaTreeRuleEntry build() {
            return new AgendaTreeRuleEntry(this);
        }
		
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
		final static String ROOT_ELEMENT_NAME = "agendaTreeRuleEntry";
		final static String TYPE_NAME = "AgendaTreeRuleEntryType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { "_futureElements" };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	static class Elements {
		final static String AGENDA_ITEM_ID = "agendaItemId";
		final static String RULE_ID = "ruleId";
		final static String IF_TRUE = "ifTrue";
		final static String IF_FALSE = "ifFalse";
	}

}
