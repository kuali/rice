package org.kuali.rice.kew.api.document;

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
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.w3c.dom.Element;

@XmlRootElement(name = RouteNodeInstanceState.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RouteNodeInstanceState.Constants.TYPE_NAME, propOrder = {
    RouteNodeInstanceState.Elements.VALUE,
    RouteNodeInstanceState.Elements.KEY,
    RouteNodeInstanceState.Elements.ID,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class RouteNodeInstanceState
    implements ModelObjectComplete, RouteNodeInstanceStateContract
{

	@XmlElement(name = Elements.ID, required = false)
    private final String id;
    @XmlElement(name = Elements.VALUE, required = false)
    private final String value;
    @XmlElement(name = Elements.KEY, required = false)
    private final String key;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     * 
     */
    private RouteNodeInstanceState() {
    	this.id = null;
        this.value = null;
        this.key = null;
    }

    private RouteNodeInstanceState(Builder builder) {
    	this.id = builder.getId();
        this.value = builder.getValue();
        this.key = builder.getKey();
    }

    @Override
    public String getId() {
        return this.id;
    }
    
    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String getKey() {
        return this.key;
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
     * A builder which can be used to construct {@link RouteNodeInstanceState} instances.  Enforces the constraints of the {@link RouteNodeInstanceStateContract}.
     */
    public final static class Builder
        implements Serializable, ModelBuilder, RouteNodeInstanceStateContract
    {

        private String id;
        private String value;
        private String key;

        private Builder() {
            // TODO modify this constructor as needed to pass any required values and invoke the appropriate 'setter' methods
        }

        public static Builder create() {
            // TODO modify as needed to pass any required values and add them to the signature of the 'create' method
            return new Builder();
        }

        public static Builder create(RouteNodeInstanceStateContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            // TODO if create() is modified to accept required parameters, this will need to be modified
            Builder builder = create();
            builder.setId(contract.getId());
            builder.setValue(contract.getValue());
            builder.setKey(contract.getKey());
            return builder;
        }

        public RouteNodeInstanceState build() {
            return new RouteNodeInstanceState(this);
        }

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getId() {
            return this.id;
        }

        public void setValue(String value) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.value = value;
        }

        public void setKey(String key) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.key = key;
        }

        public void setId(String id) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.id = id;
        }

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "routeNodeInstanceState";
        final static String TYPE_NAME = "RouteNodeInstanceStateType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] {CoreConstants.CommonElements.FUTURE_ELEMENTS };

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String VALUE = "value";
        final static String KEY = "key";
        final static String ID = "id";

    }

}

