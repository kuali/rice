package org.kuali.rice.krms.api.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;

/**
 * Concrete model object implementation of KRMS Repository AgendaItem 
 * immutable. 
 * Instances of AgendaItem can be (un)marshalled to and from XML.
 *
 * @see AgendaItemContract
 */
@XmlRootElement(name = AgendaItem.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AgendaItem.Constants.TYPE_NAME, propOrder = {
		AgendaItem.Elements.ID,
		AgendaItem.Elements.AGENDA_ID,
		AgendaItem.Elements.PRIOR_ITEM_ID,
		AgendaItem.Elements.ENTRY_CONDITION,
		AgendaItem.Elements.RULE_ID,
		AgendaItem.Elements.SUB_AGENDA_ID,
		AgendaItem.Elements.RULE,
		AgendaItem.Elements.SUB_AGENDA,
		AgendaItem.Elements.NEXT_TRUE,
		AgendaItem.Elements.NEXT_FALSE,
		AgendaItem.Elements.NEXT_AFTER,
		"_elements"
})
public final class AgendaItem implements AgendaItemContract, ModelObjectComplete{
	private static final long serialVersionUID = 2783959459503209577L;

	@XmlElement(name = Elements.ID, required=true)
	private String id;
	@XmlElement(name = Elements.AGENDA_ID, required=true)
	private String agendaId;
	@XmlElement(name = Elements.PRIOR_ITEM_ID, required=false)
	private String priorItemId;
	@XmlElement(name = Elements.ENTRY_CONDITION, required=true)
	private String entryCondition;
	@XmlElement(name = Elements.RULE_ID, required=false)
	private String ruleId;
	@XmlElement(name = Elements.SUB_AGENDA_ID, required=false)
	private String subAgendaId;
	
	@XmlElement(name = Elements.RULE, required=false)
	private Rule rule;;
	@XmlElement(name = Elements.SUB_AGENDA, required=false)
	private AgendaDefinition subAgenda;
	@XmlElement(name = Elements.NEXT_TRUE, required=false)
	private AgendaItem nextTrue;
	@XmlElement(name = Elements.NEXT_FALSE, required=false)
	private AgendaItem nextFalse;
	@XmlElement(name = Elements.NEXT_AFTER, required=false)
	private AgendaItem nextAfter;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _elements = null;
	
	
	 /** 
     * This constructor should never be called.  
     * It is only present for use during JAXB unmarshalling. 
     */
    private AgendaItem() {
    	this.id = null;
    	this.agendaId = null;
    	this.priorItemId = null;
    	this.entryCondition = null;
    	this.ruleId = null;
    	this.subAgendaId = null;
    	
    	this.rule = null;
    	this.subAgenda = null;
    	
    	this.nextTrue = null;
    	this.nextFalse = null;
    	this.nextAfter = null;
    }
    
    /**
	 * Constructs a KRMS Repository AgendaItem object from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the AgendaItem
	 */
    private AgendaItem(Builder builder) {
    	this.id = builder.getId();
    	this.agendaId = builder.getAgendaId();
    	this.priorItemId = builder.getPriorItemId();
    	this.entryCondition = builder.getEntryCondition();
    	this.ruleId = builder.getRuleId();
    	this.subAgendaId = builder.getSubAgendaId();

    	this.rule = builder.getRule().build();
    	this.subAgenda = builder.getSubAgenda().build();

    	this.nextTrue  = builder.getNextTrue().build();
    	this.nextFalse = builder.getNextFalse().build();
    	this.nextAfter = builder.getNextAfter().build();
    }

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getAgendaId() {
		return this.agendaId;
	}

	@Override
	public String getPriorItemId() {
		return this.priorItemId;
	}

	@Override
	public String getEntryCondition() {
		return this.entryCondition;
	}

	@Override
	public String getRuleId() {
		return this.ruleId;
	}

	@Override
	public String getSubAgendaId() {
		return this.subAgendaId;
	}

	@Override
	public Rule getRule() {
		return this.rule; 
	}

	@Override
	public AgendaDefinition getSubAgenda() {
		return this.subAgenda; 
	}

	@Override
	public AgendaItem getNextTrue() {
		return this.nextTrue; 
	}

	@Override
	public AgendaItem getNextFalse() {
		return this.nextFalse; 
	}

	@Override
	public AgendaItem getNextAfter() {
		return this.nextFalse; 
	}

	/**
     * This builder is used to construct instances of KRMS Repository AgendaItem.  It enforces the constraints of the {@link AgendaItemContract}.
     */
    public static class Builder implements AgendaItemContract, ModelBuilder, Serializable {
		
        private String id;
        private String agendaId;
        private String priorItemId;
        private String entryCondition;
        private String ruleId;
        private String subAgendaId;
        
        private Rule.Builder rule;
        private AgendaDefinition.Builder subAgenda;
        
        private AgendaItem.Builder nextTrue;
        private AgendaItem.Builder nextFalse;
        private AgendaItem.Builder nextAfter;
        

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String id, String agendaId, String priorItemId, String entryCondition) {
        }
        
        public static Builder create(String id, String agendaId, String priorItemId, String entryCondition){
        	return new Builder(id, agendaId, priorItemId, entryCondition);
        }
        /**
         * Creates a builder by populating it with data from the given {@link AgendaItemContract}.
         * 
         * @param contract the contract from which to populate this builder
         * @return an instance of the builder populated with data from the contract
         */
        public static Builder create(AgendaItemContract contract) {
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
        	}
        	Builder builder =  new Builder(contract.getId(), contract.getAgendaId(),
        			contract.getPriorItemId(), contract.getEntryCondition());
        	if (contract.getRule() != null){
        		builder.setRule(Rule.Builder.create( contract.getRule() ));
        	}
        	if (contract.getSubAgenda() != null){
        		builder.setSubAgenda( AgendaDefinition.Builder.create( contract.getSubAgenda()));
        	}
        	if (contract.getNextTrue() != null){
        		builder.setNextTrue( AgendaItem.Builder.create( contract.getNextTrue()));
        	}
        	if (contract.getNextFalse() != null){
        		builder.setNextFalse( AgendaItem.Builder.create( contract.getNextFalse()));
        	}
        	if (contract.getNextAfter() != null){
        		builder.setNextAfter( AgendaItem.Builder.create( contract.getNextAfter()));
        	}
        	return builder;
        }

		/**
		 * Sets the value of the id on this builder to the given value.
		 * 
		 * @param id the primary id. (may be null), must not be or blank.
		 * </p><p>This value is generated by the system.
		 * For new items (not yet persisted) this field is null. 
		 * For existing items this field is the generated id. 
		 * @throws IllegalArgumentException if the id is blank
		 */

        public void setid(String id) {
            if (id != null && StringUtils.isBlank(id)) {
                throw new IllegalArgumentException("agendaItemId is empty or whitespace.");
            }
			this.id = id;
		}


        /**
         * 
         * This method ...
         * 
         * @param agendaId
         */
        public void setAgendaId(String agendaId) {
            if (StringUtils.isBlank(agendaId)) {
                throw new IllegalArgumentException("agendaId is blank");
            }
			this.agendaId = agendaId;
		}

        /**
         * 
         * This method ...
         * 
         * @param priorItemId - the item at the top level of the agenda tree
         * 			has a null ID, all other items in the tree have a priorItemId
         */
        public void setPriorItemId(String priorItemId) {
			this.priorItemId = priorItemId;
		}

        /**
         * 
         * This method ...
         * 
         * @param EntryCondition - required.
         */
		public void setEntryCondition(String EntryCondition) {
			this.entryCondition = entryCondition;
		}
		
		public void setRuleId(String ruleId) {
			this.ruleId = ruleId;
		}

		public void setSubAgendaId(String subAgendaId) {
			this.subAgendaId = subAgendaId;
		}

		public void setRule(Rule.Builder rule) {
			this.rule = rule;
		}

		public void setSubAgenda(AgendaDefinition.Builder subAgenda) {
			this.subAgenda = subAgenda;
		}

		public void setNextTrue(AgendaItem.Builder nextTrue) {
			this.nextTrue = nextTrue;
		}

		public void setNextFalse(AgendaItem.Builder nextFalse) {
			this.nextTrue = nextFalse;
		}

		public void setNextAfter(AgendaItem.Builder nextAfter) {
			this.nextTrue = nextAfter;
		}

			
		
		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getAgendaId() {
			return agendaId;
		}

		@Override
		public String getPriorItemId() {
			return priorItemId;
		}

		@Override
		public String getEntryCondition() {
			return entryCondition;
		}

		@Override
		public String getRuleId() {
			return ruleId;
		}

		@Override
		public String getSubAgendaId() {
			return subAgendaId;
		}

		@Override
		public Rule.Builder getRule() {
			return rule;
		}

		@Override
		public AgendaDefinition.Builder getSubAgenda() {
			return subAgenda;
		}

		@Override
		public AgendaItem.Builder getNextTrue() {
			return nextTrue;
		}

		@Override
		public AgendaItem.Builder getNextFalse() {
			return nextFalse;
		}

		@Override
		public AgendaItem.Builder getNextAfter() {
			return nextAfter;
		}

		/**
		 * Builds an instance of a AgendaItem based on the current state of the builder.
		 * 
		 * @return the fully-constructed AgendaItem
		 */
        @Override
        public AgendaItem build() {
            return new AgendaItem(this);
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
		final static String ROOT_ELEMENT_NAME = "AgendaItem";
		final static String TYPE_NAME = "AgendaItemType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { "_elements" };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String ID = "id";
		final static String AGENDA_ID = "agendaId";
		final static String PRIOR_ITEM_ID = "priorItemId";
		final static String ENTRY_CONDITION = "entryCondition";
		final static String RULE_ID = "ruleId";
		final static String SUB_AGENDA_ID = "subAgendaId";

		final static String RULE = "rule";
		final static String SUB_AGENDA = "subAgenda";
		final static String NEXT_TRUE = "nextTrue";
		final static String NEXT_FALSE = "nextFalse";
		final static String NEXT_AFTER = "nextAfter";
	}

}
