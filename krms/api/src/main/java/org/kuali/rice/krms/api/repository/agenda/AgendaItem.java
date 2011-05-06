package org.kuali.rice.krms.api.repository.agenda;

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
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;

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
		AgendaItem.Elements.RULE_ID,
		AgendaItem.Elements.SUB_AGENDA_ID,
		AgendaItem.Elements.WHEN_TRUE_ID,
		AgendaItem.Elements.WHEN_FALSE_ID,
		AgendaItem.Elements.ALWAYS_ID,
		AgendaItem.Elements.RULE,
		AgendaItem.Elements.SUB_AGENDA,
		AgendaItem.Elements.WHEN_TRUE,
		AgendaItem.Elements.WHEN_FALSE,
		AgendaItem.Elements.ALWAYS,
		"_elements"
})
public final class AgendaItem implements AgendaItemContract, ModelObjectComplete{
	private static final long serialVersionUID = 2783959459503209577L;

	@XmlElement(name = Elements.ID, required=true)
	private String id;
	@XmlElement(name = Elements.AGENDA_ID, required=true)
	private String agendaId;
	@XmlElement(name = Elements.RULE_ID, required=false)
	private String ruleId;
	@XmlElement(name = Elements.SUB_AGENDA_ID, required=false)
	private String subAgendaId;
	@XmlElement(name = Elements.WHEN_TRUE_ID, required=false)
	private String whenTrueId;
	@XmlElement(name = Elements.WHEN_FALSE_ID, required=false)
	private String whenFalseId;
	@XmlElement(name = Elements.ALWAYS_ID, required=false)
	private String alwaysId;
	
	@XmlElement(name = Elements.RULE, required=false)
	private RuleDefinition rule;;
	@XmlElement(name = Elements.SUB_AGENDA, required=false)
	private AgendaDefinition subAgenda;
	@XmlElement(name = Elements.WHEN_TRUE, required=false)
	private AgendaItem whenTrue;
	@XmlElement(name = Elements.WHEN_FALSE, required=false)
	private AgendaItem whenFalse;
	@XmlElement(name = Elements.ALWAYS, required=false)
	private AgendaItem always;
	
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
    	this.ruleId = null;
    	this.subAgendaId = null;
    	this.whenTrueId = null;
    	this.whenFalseId = null;
    	this.alwaysId = null;
    	
    	this.rule = null;
    	this.subAgenda = null;
    	
    	this.whenTrue = null;
    	this.whenFalse = null;
    	this.always = null;
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
    	this.ruleId = builder.getRuleId();
    	this.subAgendaId = builder.getSubAgendaId();
    	this.whenTrueId = builder.getWhenTrueId();
    	this.whenFalseId = builder.getWhenFalseId();
    	this.alwaysId = builder.getAlwaysId();

    	this.rule = builder.getRule().build();
    	this.subAgenda = builder.getSubAgenda().build();

    	this.whenTrue  = builder.getWhenTrue().build();
    	this.whenFalse = builder.getWhenFalse().build();
    	this.always = builder.getAlways().build();
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
	public String getRuleId() {
		return this.ruleId;
	}

	@Override
	public String getSubAgendaId() {
		return this.subAgendaId;
	}

	@Override
	public String getWhenTrueId() {
		return this.whenTrueId;
	}

	@Override
	public String getWhenFalseId() {
		return this.whenFalseId;
	}

	@Override
	public String getAlwaysId() {
		return this.alwaysId;
	}

	@Override
	public RuleDefinition getRule() {
		return this.rule; 
	}

	@Override
	public AgendaDefinition getSubAgenda() {
		return this.subAgenda; 
	}

	@Override
	public AgendaItem getWhenTrue() {
		return this.whenTrue; 
	}

	@Override
	public AgendaItem getWhenFalse() {
		return this.whenFalse; 
	}

	@Override
	public AgendaItem getAlways() {
		return this.always; 
	}

	/**
     * This builder is used to construct instances of KRMS Repository AgendaItem.  It enforces the constraints of the {@link AgendaItemContract}.
     */
    public static class Builder implements AgendaItemContract, ModelBuilder, Serializable {
		
        private String id;
        private String agendaId;
        private String ruleId;
        private String subAgendaId;
        private String whenTrueId;
        private String whenFalseId;
        private String alwaysId;
        
        private RuleDefinition.Builder rule;
        private AgendaDefinition.Builder subAgenda;
        
        private AgendaItem.Builder whenTrue;
        private AgendaItem.Builder whenFalse;
        private AgendaItem.Builder always;
        

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String id, String agendaId) {
        	setId(id);
        	setAgendaId(agendaId);
        }
        
        public static Builder create(String id, String agendaId){
        	return new Builder(id, agendaId);
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
        	Builder builder =  new Builder(contract.getId(), contract.getAgendaId());
        	builder.setRuleId(contract.getRuleId());
        	builder.setSubAgendaId(contract.getSubAgendaId());
        	builder.setWhenTrueId(contract.getWhenTrueId());
        	builder.setWhenFalseId(contract.getWhenFalseId());
        	builder.setAlwaysId(contract.getAlwaysId());
        	
        	if (contract.getRule() != null){
        		builder.setRule(RuleDefinition.Builder.create( contract.getRule() ));
        	}
        	if (contract.getSubAgenda() != null){
        		builder.setSubAgenda( AgendaDefinition.Builder.create( contract.getSubAgenda()));
        	}
        	if (contract.getWhenTrue() != null){
        		builder.setWhenTrue( AgendaItem.Builder.create( contract.getWhenTrue()));
        	}
        	if (contract.getWhenFalse() != null){
        		builder.setWhenFalse( AgendaItem.Builder.create( contract.getWhenFalse()));
        	}
        	if (contract.getAlways() != null){
        		builder.setAlways( AgendaItem.Builder.create( contract.getAlways()));
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

        public void setId(String id) {
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
		
		public void setRuleId(String ruleId) {
			this.ruleId = ruleId;
		}

		public void setSubAgendaId(String subAgendaId) {
			this.subAgendaId = subAgendaId;
		}

		public void setWhenTrueId(String whenTrueId) {
			this.whenTrueId = whenTrueId;
		}

		public void setWhenFalseId(String whenFalseId) {
			this.whenFalseId = whenFalseId;
		}

		public void setAlwaysId(String alwaysId) {
			this.alwaysId = alwaysId;
		}

		public void setRule(RuleDefinition.Builder rule) {
			this.rule = rule;
		}

		public void setSubAgenda(AgendaDefinition.Builder subAgenda) {
			this.subAgenda = subAgenda;
		}

		public void setWhenTrue(AgendaItem.Builder whenTrue) {
			this.whenTrue = whenTrue;
		}

		public void setWhenFalse(AgendaItem.Builder whenFalse) {
			this.whenTrue = whenFalse;
		}

		public void setAlways(AgendaItem.Builder always) {
			this.always = always;
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
		public String getRuleId() {
			return ruleId;
		}

		@Override
		public String getSubAgendaId() {
			return subAgendaId;
		}

		@Override
		public String getWhenTrueId() {
			return whenTrueId;
		}

		@Override
		public String getWhenFalseId() {
			return whenFalseId;
		}

		@Override
		public String getAlwaysId() {
			return alwaysId;
		}

		@Override
		public RuleDefinition.Builder getRule() {
			return rule;
		}

		@Override
		public AgendaDefinition.Builder getSubAgenda() {
			return subAgenda;
		}

		@Override
		public AgendaItem.Builder getWhenTrue() {
			return whenTrue;
		}

		@Override
		public AgendaItem.Builder getWhenFalse() {
			return whenFalse;
		}

		@Override
		public AgendaItem.Builder getAlways() {
			return always;
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
		final static String RULE_ID = "ruleId";
		final static String SUB_AGENDA_ID = "subAgendaId";
		final static String WHEN_TRUE_ID = "whenTrueId";
		final static String WHEN_FALSE_ID = "whenFalseId";
		final static String ALWAYS_ID = "alwaysId";

		final static String RULE = "rule";
		final static String SUB_AGENDA = "subAgenda";
		final static String WHEN_TRUE = "whenTrue";
		final static String WHEN_FALSE = "whenFalse";
		final static String ALWAYS = "always";
	}

}
