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
 * Concrete model object implementation of KRMS Repository Action 
 * immutable. 
 * Instances of Action can be (un)marshalled to and from XML.
 *
 * @see ActionContract
 */
@XmlRootElement(name = Action.Constants.ROOT_ELEMENT_NAME, namespace = Action.Constants.KRMSNAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Action.Constants.TYPE_NAME, propOrder = {
		Action.Elements.ID,
		Action.Elements.NAME,
		Action.Elements.NAMESPACE,
		Action.Elements.DESC,
		Action.Elements.TYPE_ID,
		Action.Elements.ATTRIBUTES,
		"_elements"
})
public final class Action implements ActionContract, ModelObjectComplete{
	private static final long serialVersionUID = 2783959459503209577L;

	@XmlElement(name = Elements.ID, required=true, namespace = Action.Constants.KRMSNAMESPACE)
	private String actionId;
	@XmlElement(name = Elements.NAME, required=true, namespace = Action.Constants.KRMSNAMESPACE)
	private String name;
	@XmlElement(name = Elements.NAMESPACE, required=true, namespace = Action.Constants.KRMSNAMESPACE)
	private String namespace;
	@XmlElement(name = Elements.DESC, required=true, namespace = Action.Constants.KRMSNAMESPACE)
	private String description;
	@XmlElement(name = Elements.TYPE_ID, required=true, namespace = Action.Constants.KRMSNAMESPACE)
	private String typeId;
	@XmlElement(name = Elements.ATTRIBUTES, required=false, namespace = Action.Constants.KRMSNAMESPACE)
	private List<ActionAttribute> attributes;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<org.w3c.dom.Element> _elements = null;
	
	
	 /** 
     * This constructor should never be called.  
     * It is only present for use during JAXB unmarshalling. 
     */
    private Action() {
    	this.actionId = null;
    	this.name = null;
    	this.namespace = null;
    	this.description = null;
    	this.typeId = null;
    	this.attributes = null;
    }
    
    /**
	 * Constructs a KRMS Repository Action object from the given builder.  
	 * This constructor is private and should only ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the Action
	 */
    private Action(Builder builder) {
        this.actionId = builder.getActionId();
        this.name = builder.getName();
        this.namespace = builder.getNamespace();
        this.description = builder.getDescription();
        this.typeId = builder.getTypeId();
        List<ActionAttribute> attrList = new ArrayList<ActionAttribute>();
        for (ActionAttribute.Builder b : builder.attributes){
        	attrList.add(b.build());
        }
        this.attributes = Collections.unmodifiableList(attrList);
    }
    
	@Override
	public String getActionId() {
		return this.actionId;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getNamespace() {
		return this.namespace;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getTypeId() {
		return this.typeId;
	}

	@Override
	public List<ActionAttribute> getAttributes() {
		return this.attributes; 
	}

	/**
     * This builder is used to construct instances of KRMS Repository Action.  It enforces the constraints of the {@link ActionContract}.
     */
    public static class Builder implements ActionContract, ModelBuilder, Serializable {
		
        private String actionId;
        private String name;
        private String namespace;
        private String description;
        private String typeId;
        private List<ActionAttribute.Builder> attributes;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String actionId, String name, String namespace, String typeId) {
            setActionId(actionId);
            setName(name);
            setNamespace(namespace);
            setTypeId(typeId);
        }
        
        public Builder description (String description){
        	setDescription(description);
        	return this;
        }
        public Builder attributes (List<ActionAttribute.Builder> attributes){
        	setAttributes(attributes);
        	return this;
        }
 
        public static Builder create(String actionId, String name, String namespace, String typeId){
        	return new Builder(actionId, name, namespace, typeId);
        }
        /**
         * Creates a builder by populating it with data from the given {@link ActionContract}.
         * 
         * @param contract the contract from which to populate this builder
         * @return an instance of the builder populated with data from the contract
         */
        public static Builder create(ActionContract contract) {
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
        	List <ActionAttribute.Builder> attrBuilderList = new ArrayList<ActionAttribute.Builder>();
        	if (contract.getAttributes() != null){
        		for (ActionAttributeContract attrContract : contract.getAttributes()){
        			ActionAttribute.Builder myBuilder = ActionAttribute.Builder.create(attrContract);
        			attrBuilderList.add(myBuilder);
        		}
        	}
            Builder builder =  new Builder(contract.getActionId(), contract.getName(),
            		contract.getNamespace(), contract.getTypeId())
            			.description(contract.getDescription())
            			.attributes(attrBuilderList);
            return builder;
        }

		/**
		 * Sets the value of the id on this builder to the given value.
		 * 
		 * @param id the id value to set, must not be null or blank
		 * @throws IllegalArgumentException if the id is null or blank
		 */

        public void setActionId(String actionId) {
            if (StringUtils.isBlank(actionId)) {
                throw new IllegalArgumentException("actionId is blank");
            }
			this.actionId = actionId;
		}

     
        public void setName(String name) {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("name is blank");
            }
			this.name = name;
		}

     
        public void setNamespace(String namespace) {
            if (StringUtils.isBlank(namespace)) {
                throw new IllegalArgumentException("namespace is blank");
            }
			this.namespace = namespace;
		}

     
		public void setDescription(String desc) {
			this.description = desc;
		}
		
		public void setTypeId(String typeId) {
			if (StringUtils.isBlank(typeId)) {
	                throw new IllegalArgumentException("KRMS type id is blank");
			}
			this.typeId = typeId;
		}
		
		public void setAttributes(List<ActionAttribute.Builder> attributes){
			if (attributes == null){
				this.attributes = Collections.unmodifiableList(new ArrayList<ActionAttribute.Builder>());
				return;
			}
			this.attributes = Collections.unmodifiableList(attributes);
		}
		
		@Override
		public String getActionId() {
			return actionId;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getNamespace() {
			return namespace;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public String getTypeId() {
			return typeId;
		}

		@Override
		public List<ActionAttribute.Builder> getAttributes() {
			return attributes;
		}

		/**
		 * Builds an instance of a Action based on the current state of the builder.
		 * 
		 * @return the fully-constructed Action
		 */
        @Override
        public Action build() {
            return new Action(this);
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
		final static String KRMSNAMESPACE = "http://rice.kuali.org/schema/krms";		
		final static String ROOT_ELEMENT_NAME = "Action";
		final static String TYPE_NAME = "ActionType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { "_elements" };
	}
	
	/**
	 * A private class which exposes constants which define the XML element names to use
	 * when this object is marshalled to XML.
	 */
	public static class Elements {
		final static String ID = "actionId";
		final static String NAME = "name";
		final static String NAMESPACE = "namespace";
		final static String DESC = "description";
		final static String TYPE_ID = "typeId";
		final static String ATTRIBUTES = "attribute";
	}

}
