package org.kuali.rice.shareddata.api.campus;


import java.io.Serializable;
import java.util.Collection;

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
import org.kuali.rice.core.mo.ModelBuilder;
import org.kuali.rice.core.mo.ModelObjectComplete;
import org.w3c.dom.Element;

@XmlRootElement(name = Campus.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Campus.Constants.TYPE_NAME, propOrder = {
        Campus.Elements.CODE,
        Campus.Elements.NAME,
        Campus.Elements.SHORT_NAME,
        Campus.Elements.CAMPUS_TYPE,
        Campus.Elements.ACTIVE,
        "_elements"
})
public final class Campus implements CampusContract, ModelObjectComplete {
	private static final long serialVersionUID = 2288194493838509380L;

	@XmlElement(name = Elements.CODE, required=true)
	private String code;
	@XmlElement(name = Elements.NAME, required=false)
	private String name;
	@XmlElement(name = Elements.SHORT_NAME, required=false)
	private String shortName;
	@XmlElement(name = Elements.CAMPUS_TYPE, required=false)
	private CampusType campusType;
	@XmlElement(name = Elements.ACTIVE, required=false)
	private boolean active;
	
	@SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _elements = null;
	
	
	 /** 
     * This constructor should never be called.  It is only present for use during JAXB unmarshalling. 
     */
    private Campus() {
    	this.code = null;
    	this.name = null;
    	this.shortName = null;
    	this.campusType = null;
    	this.active = false;
    }
    
    /**
	 * Constructs a Campus from the given builder.  This constructor is private and should only
	 * ever be invoked from the builder.
	 * 
	 * @param builder the Builder from which to construct the campus
	 */
    private Campus(Builder builder) {
        this.code = builder.getCode();
        this.name = builder.getName();
        this.shortName = builder.getShortName();
        if (builder.campusType != null) {
        	this.campusType = builder.campusType.build();
        }
        this.active = builder.isActive();
    }
    
/*	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepareForWorkflow() {
		// TODO Auto-generated method stub
		
	}*/

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getShortName() {
		return this.shortName;
	}

	@Override
	public CampusType getCampusType() {
		return this.campusType;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	/**
     * This builder is used to construct instances of Campus.  It enforces the constraints of the {@link CampusContract}.
     */
    public static class Builder implements CampusContract, ModelBuilder, Serializable {
		private static final long serialVersionUID = -3130728718673871762L;
		private String code;
        private String name;
        private String shortName;
        private CampusType.Builder campusType;
        private boolean active;

		/**
		 * Private constructor for creating a builder with all of it's required attributes.
		 */
        private Builder(String code) {
            setCode(code);
			setActive(true);
        }

        /**
         * Creates a builder from the given campus code.
         * 
         * @param code the campus code
         * @return an instance of the builder with the code already populated
         * @throws IllegalArgumentException if the code is null or blank
         */
        public static Builder create(String code) {
            return new Builder(code);
        }

        /**
         * Creates a builder by populating it with data from the given {@link CampusContract}.
         * 
         * @param contract the contract from which to populate this builder
         * @return an instance of the builder populated with data from the contract
         */
        public static Builder create(CampusContract contract) {
        	if (contract == null) {
                throw new IllegalArgumentException("contract is null");
            }
            Builder builder =  new Builder(contract.getCode());
            builder.setName(contract.getName());
            builder.setShortName(contract.getShortName());
            if (contract.getCampusType() != null) {
            	builder.setCampusType(CampusType.Builder.create(contract.getCampusType()));
            }
            builder.setActive(contract.isActive());
            return builder;
        }

		/**
		 * Sets the value of the code on this builder to the given value.
		 * 
		 * @param code the code value to set, must not be null or blank
		 * @throws IllegalArgumentException if the code is null or blank
		 */
        public void setCode(String code) {
            if (StringUtils.isBlank(code)) {
                throw new IllegalArgumentException("code is blank");
            }
            this.code = code;
        }

		public void setName(String name) {
			this.name = name;
		}
		
		public void setShortName(String shortName) {
			this.shortName = shortName;
		}
		
		public void setCampusType(CampusType.Builder campusType) {
			this.campusType = campusType;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		@Override
		public String getCode() {
			return code;
		}

		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public String getShortName() {
			return shortName;
		}
		
		@Override 
		public CampusType.Builder getCampusType() {
			return campusType;
		}
		
		@Override
		public boolean isActive() {
			return active;
		}

		/**
		 * Builds an instance of a Campus based on the current state of the builder.
		 * 
		 * @return the fully-constructed Campus
		 */
        @Override
        public Campus build() {
            return new Campus(this);
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
		final static String ROOT_ELEMENT_NAME = "campus";
		final static String TYPE_NAME = "CampusType";
		final static String[] HASH_CODE_EQUALS_EXCLUDE = { "_elements" };
	}
	
	/**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String CODE = "code";
        final static String NAME = "name";
        final static String SHORT_NAME = "shortName";
        final static String CAMPUS_TYPE = "campusType";
        final static String ACTIVE = "active";
    }
}
