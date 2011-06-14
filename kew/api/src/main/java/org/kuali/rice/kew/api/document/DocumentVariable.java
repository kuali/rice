package org.kuali.rice.kew.api.document;

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
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.w3c.dom.Element;

@XmlRootElement(name = DocumentVariable.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentVariable.Constants.TYPE_NAME, propOrder = {
    DocumentVariable.Elements.NAME,
    DocumentVariable.Elements.VALUE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentVariable implements ModelObjectComplete, DocumentVariableContract {

	private static final long serialVersionUID = -7875688623286970510L;

	@XmlElement(name = Elements.NAME, required = true)
    private final String name;
    
    @XmlElement(name = Elements.VALUE, required = true)
    private final String value;
    
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private DocumentVariable() {
        this.name = null;
        this.value = null;
    }

    private DocumentVariable(Builder builder) {
        this.name = builder.getName();
        this.value = builder.getValue();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
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
     * A builder which can be used to construct {@link DocumentVariable} instances.  Enforces the constraints of the {@link DocumentVariableContract}.
     */
    public final static class Builder implements Serializable, ModelBuilder, DocumentVariableContract {

		private static final long serialVersionUID = -2064548119902762503L;

		private String name;
        private String value;

        private Builder(String name, String value) {
        	setName(name);
        	setValue(value);
        }

        public static Builder create(String name, String value) {
            return new Builder(name, value);
        }

        public static Builder create(DocumentVariableContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            return create(contract.getName(), contract.getValue());
        }

        public DocumentVariable build() {
            return new DocumentVariable(this);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getValue() {
            return this.value;
        }

        public void setName(String name) {
            if (StringUtils.isBlank(name)) {
            	throw new IllegalArgumentException("name was null or blank");
            }
            this.name = name;
        }

        public void setValue(String value) {
            if (value == null) {
            	value = "";
            }
            this.value = value;
        }

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "documentVariable";
        final static String TYPE_NAME = "DocumentVariableType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] {CoreConstants.CommonElements.FUTURE_ELEMENTS };

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String NAME = "name";
        final static String VALUE = "value";

    }

}

