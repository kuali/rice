package org.kuali.rice.shareddata.api.state;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.mo.ModelBuilder;
import org.kuali.rice.core.mo.ModelObjectComplete;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Collection;

@XmlRootElement(name = State.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = State.Constants.TYPE_NAME, propOrder = {
        State.Elements.POSTAL_CODE,
        State.Elements.POSTAL_NAME,
        State.Elements.POSTAL_COUNTRY_CODE,
        State.Elements.ACTIVE,
        "_elements"
})
public final class State implements StateContract, ModelObjectComplete {

    private static final long serialVersionUID = 6097498602725305353L;

    @XmlElement(name = Elements.POSTAL_CODE, required = true)
    private final String postalCode;

    @XmlElement(name = Elements.POSTAL_NAME, required = true)
    private final String postalName;

    @XmlElement(name = Elements.POSTAL_COUNTRY_CODE, required = true)
    private final String postalCountryCode;

    @XmlElement(name = Elements.ACTIVE, required = true)
    private final boolean active;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _elements = null;

    /**
     * This constructor should never be called except during JAXB unmarshalling.
     */
    private State() {
        this.postalCode = null;
        this.postalName = null;
        this.postalCountryCode = null;
        this.active = false;
    }

    private State(Builder builder) {
        postalCode = builder.getPostalCode();
        postalName = builder.getPostalName();
        postalCountryCode = builder.getPostalCountryCode();
        active = builder.isActive();
    }

    @Override
    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public String getPostalName() {
        return postalName;
    }

    @Override
    public String getPostalCountryCode() {
        return postalCountryCode;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * This builder constructs an Parameter enforcing the constraints of the {@link StateContract}.
     */
    public static class Builder implements StateContract, ModelBuilder, Serializable {

        private static final long serialVersionUID = 7077484401017765844L;

        private String postalCode;

        private String postalName;

        private String postalCountryCode;

        private boolean active;

        private Builder(String postalCode, String postalName, String postalCountryCode) {
            setPostalCode(postalCode);
            setPostalName(postalName);
            setPostalCountryCode(postalCountryCode);
        }

        /**
         * creates a State with the required fields.
         */
        public static Builder create(String postalCode, String postalName, String postalCountryCode) {
            final Builder builder = new Builder(postalCode, postalName, postalCountryCode);
            builder.setActive(true);
            return builder;
        }

        /**
         * creates a Parameter from an existing {@link StateContract}.
         */
        public static Builder create(StateContract contract) {
            final Builder builder = new Builder(contract.getPostalCode(), contract.getPostalName(), contract.getPostalCountryCode());
            builder.setActive(contract.isActive());
            return builder;
        }

        @Override
        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            if (StringUtils.isBlank(postalCode)) {
                throw new IllegalArgumentException("postalCode is blank");
            }

            this.postalCode = postalCode;
        }

        @Override
        public String getPostalName() {
            return postalName;
        }

        public void setPostalName(String postalName) {
            if (StringUtils.isBlank(postalName)) {
                throw new IllegalArgumentException("postalName is blank");
            }

            this.postalName = postalName;
        }

        @Override
        public String getPostalCountryCode() {
            return postalCountryCode;
        }

        public void setPostalCountryCode(String postalCountryCode) {
            if (StringUtils.isBlank(postalCountryCode)) {
                throw new IllegalArgumentException("postalCountryCode is blank");
            }

            this.postalCountryCode = postalCountryCode;
        }

        @Override
        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public State build() {
            return new State(this);
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
        final static String ROOT_ELEMENT_NAME = "state";
        final static String TYPE_NAME = "StateType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {"_elements"};
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String POSTAL_CODE = "postalCode";
        final static String POSTAL_NAME = "postalName";
        final static String POSTAL_COUNTRY_CODE = "postalCountryCode";
        final static String ACTIVE = "active";
    }

}