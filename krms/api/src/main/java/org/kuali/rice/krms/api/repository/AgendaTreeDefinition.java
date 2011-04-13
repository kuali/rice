package org.kuali.rice.krms.api.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;

/**
 * Concrete model object implementation of KRMS Repository Agenda 
 * immutable. 
 * Instances of Agenda can be (un)marshalled to and from XML.
 *
 * @see AgendaDefinitionContract
 */
@XmlRootElement(name = AgendaTreeDefinition.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AgendaTreeDefinition.Constants.TYPE_NAME, propOrder = {
		AgendaTreeDefinition.Elements.AGENDA_ID,
		AgendaTreeDefinition.Elements.ENTRIES,
		CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class AgendaTreeDefinition implements ModelObjectComplete {
	
	private static final long serialVersionUID = 3355519740298280591L;

	@XmlElement(name = Elements.AGENDA_ID, required = false)
	private final String agendaId;
	
	@XmlElements(value = {
            @XmlElement(name = Elements.RULE, type = AgendaTreeRuleEntry.class, required = false),
            @XmlElement(name = Elements.SUB_AGENDA, type = AgendaTreeSubAgendaEntry.class, required = false)
	})
	private final List<AgendaTreeEntryDefinition> entries;
		
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _futureElements = null;
	
	/** 
     * This constructor should never be called.  
     * It is only present for use during JAXB unmarshalling. 
     */
    private AgendaTreeDefinition() {
    	this.agendaId = null;
    	this.entries = null;
    }
    
    private AgendaTreeDefinition(Builder builder) {
    	this.agendaId = builder.getAgendaId();
        this.entries = builder.getEntries();
    }
    
    public String getAgendaId() {
    	return agendaId;
    }
    
	public List<AgendaTreeEntryDefinition> getEntries() {
		return Collections.unmodifiableList(entries);
	}

    public static class Builder implements ModelBuilder, Serializable {
		        
		private static final long serialVersionUID = 7981215392039022620L;
		
		private String agendaId;
		private List<AgendaTreeEntryDefinition> entries;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder() {
        	this.entries = new ArrayList<AgendaTreeEntryDefinition>();
        }
        
        public static Builder create(){
        	return new Builder();
        }
        
        public void setAgendaId(String agendaId) {
        	this.agendaId = agendaId;
        }
        
        public void addRuleEntry(AgendaTreeRuleEntry ruleEntry) {
        	if (ruleEntry == null) {
        		throw new IllegalArgumentException("ruleEntry was null");
        	}
        	entries.add(ruleEntry);
        }
        
        public void addSubAgendaEntry(AgendaTreeSubAgendaEntry subAgendaEntry) {
        	if (subAgendaEntry == null) {
        		throw new IllegalArgumentException("subAgendaEntry was null");
        	}
        	entries.add(subAgendaEntry);
        }
        
        public String getAgendaId() {
        	return this.agendaId;
        }
        
        public List<AgendaTreeEntryDefinition> getEntries() {
        	return this.entries;
        }

        @Override
        public AgendaTreeDefinition build() {
            return new AgendaTreeDefinition(this);
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
		final static String ROOT_ELEMENT_NAME = "agendaTreeDefinition";
		final static String TYPE_NAME = "AgendaTreeDefinition";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { "_elements" };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	static class Elements {
		final static String AGENDA_ID = "agendaId";
		final static String ENTRIES = "entries";
		final static String RULE = "rule";
		final static String SUB_AGENDA = "subAgenda";
	}

}
