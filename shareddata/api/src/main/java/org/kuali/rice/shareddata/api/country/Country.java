package org.kuali.rice.shareddata.api.country;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.rice.core.mo.ModelBuilder;
import org.kuali.rice.core.mo.ModelObjectComplete;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Collection;

/**
 * POJO implementation of CountryContract that is immutable. Instances of Country can be (un)marshalled to and from XML.
 *
 * @see CountryContract
 */
@XmlRootElement(name = Country.Elements.ROOT_ELEMENT_NAME, namespace = Country.Elements.NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Country.Elements.COUNTRY_TYPE_NAME, propOrder = {
        Country.Elements.POSTAL_COUNTRY_CODE,
        Country.Elements.ALTERNATE_POSTAL_COUNTRY_CODE,
        Country.Elements.POSTAL_COUNTRY_NAME,
        Country.Elements.POSTAL_COUNTRY_RESTRICTED,
        Country.Elements.ACTIVE,
        "_elements"
})
public final class Country implements CountryContract, ModelObjectComplete {
    private static final long serialVersionUID = -8975392777320033940L;

    @XmlElement(name = Elements.POSTAL_COUNTRY_CODE, required = true, namespace = Country.Elements.NAMESPACE)
    private final String postalCountryCode;

    @XmlElement(name = Elements.ALTERNATE_POSTAL_COUNTRY_CODE, required = false, namespace = Country.Elements.NAMESPACE)
    private final String alternatePostalCountryCode;

    @XmlElement(name = Elements.POSTAL_COUNTRY_NAME, required = false, namespace = Country.Elements.NAMESPACE)
    private final String postalCountryName;

    @XmlElement(name = Elements.POSTAL_COUNTRY_RESTRICTED, required = true, namespace = Country.Elements.NAMESPACE)
    private final boolean postalCountryRestricted;

    @XmlElement(name = Elements.ACTIVE, required = true, namespace = Country.Elements.NAMESPACE)
    private final boolean active;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _elements = null;

    /**
     * This constructor should never be called except during JAXB unmarshalling.
     */
    @SuppressWarnings("unused")
    private Country() {
        this.postalCountryCode = null;
        this.alternatePostalCountryCode = null;
        this.postalCountryName = null;
        this.postalCountryRestricted = false;
        this.active = false;
    }

    private Country(Builder builder) {
        this.postalCountryCode = builder.getPostalCountryCode();
        this.alternatePostalCountryCode = builder.getAlternatePostalCountryCode();
        this.postalCountryName = builder.getPostalCountryName();
        this.postalCountryRestricted = builder.isPostalCountryRestricted();
        this.active = builder.isActive();
    }

    @Override
    public String getPostalCountryCode() {
        return this.postalCountryCode;
    }

    @Override
    public String getAlternatePostalCountryCode() {
        return this.alternatePostalCountryCode;
    }

    @Override
    public String getPostalCountryName() {
        return this.postalCountryName;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public boolean isPostalCountryRestricted() {
        return this.postalCountryRestricted;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof CountryContract)) {
            return false;
        }
        CountryContract other = (CountryContract) o;
        return new EqualsBuilder().append(this.active, other.isActive())
                .append(this.postalCountryRestricted, other.isPostalCountryRestricted())
                .append(this.alternatePostalCountryCode, other.getAlternatePostalCountryCode())
                .append(this.postalCountryCode, other.getPostalCountryCode())
                .append(this.postalCountryName, other.getPostalCountryName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(9, 55)
                .append(this.active)
                .append(this.postalCountryRestricted)
                .append(this.alternatePostalCountryCode)
                .append(this.postalCountryCode)
                .append(this.postalCountryName)
                .toHashCode();
    }

    /**
     * Builder for immutable Country objects.
     */
    public static class Builder implements CountryContract, ModelBuilder, Serializable {
        private static final long serialVersionUID = -4786917485397379322L;

        private String postalCountryCode;
        private String alternatePostalCountryCode;
        private String postalCountryName;
        private boolean postalCountryRestricted;
        private boolean active;

        private Builder(String postalCountryCode, String alternatePostalCode, String postalCountryName,
                        boolean postalCountryRestricted, boolean active) {
            this.setPostalCountryCode(postalCountryCode);
            this.setAlternatePostalCode(alternatePostalCode);
            this.setPostalCountryName(postalCountryName);
            this.setPostalCountryRestricted(postalCountryRestricted);
            this.setActive(active);
        }

        public static Builder create(String postalCountryCode, String alternatePostalCode, String postalCountryName,
                                     boolean postalCountryRestricted, boolean active) {
            return new Builder(postalCountryCode, alternatePostalCode, postalCountryName, postalCountryRestricted, active);
        }

        public static Builder create(CountryContract cc) {
            return new Builder(cc.getPostalCountryCode(), cc.getAlternatePostalCountryCode(),
                    cc.getPostalCountryName(), cc.isPostalCountryRestricted(), cc.isActive());
        }

        @Override
        public Country build() {
            return new Country(this);
        }

        /**
         * Sets postalCountryCode property.
         *
         * @param postalCountryCode required to be not null and not empty.
         */
        public void setPostalCountryCode(String postalCountryCode) {
            if (StringUtils.isBlank(postalCountryCode)) {
                throw new IllegalArgumentException("postalCountryCode cannot be blank or null");
            }
            this.postalCountryCode = postalCountryCode;
        }

        @Override
        public String getPostalCountryCode() {
            return this.postalCountryCode;
        }

        /**
         * Sets the optional alternatePostalCode property
         *
         * @param alternatePostalCode
         */
        public void setAlternatePostalCode(String alternatePostalCode) {
            this.alternatePostalCountryCode = alternatePostalCode;
        }

        @Override
        public String getAlternatePostalCountryCode() {
            return this.alternatePostalCountryCode;
        }

        /**
         * Sets the optional postalCountryName property.
         *
         * @param postalCountryName
         */
        public void setPostalCountryName(String postalCountryName) {
            this.postalCountryName = postalCountryName;
        }

        @Override
        public String getPostalCountryName() {
            return this.postalCountryName;
        }

        /**
         * Sets the active property.
         *
         * @param active
         */
        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public boolean isActive() {
            return this.active;
        }

        /**
         * Sets the postalCountryRestrictedProperty
         *
         * @param postalCountryRestricted
         */
        public void setPostalCountryRestricted(boolean postalCountryRestricted) {
            this.postalCountryRestricted = postalCountryRestricted;
        }

        @Override
        public boolean isPostalCountryRestricted() {
            return this.postalCountryRestricted;
        }
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String NAMESPACE = "http://rice.kuali.org/schema/shareddata";
        final static String ROOT_ELEMENT_NAME = "country";
        final static String COUNTRY_TYPE_NAME = "CountryType";

        final static String POSTAL_COUNTRY_CODE = "postalCountryCode";
        final static String ALTERNATE_POSTAL_COUNTRY_CODE = "alternatePostalCountryCode";
        final static String POSTAL_COUNTRY_NAME = "postalCountryName";
        final static String POSTAL_COUNTRY_RESTRICTED = "postalCountryRestricted";
        final static String ACTIVE = "active";
    }
}