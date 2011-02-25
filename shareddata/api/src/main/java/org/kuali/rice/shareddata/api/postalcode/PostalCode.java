package org.kuali.rice.shareddata.api.postalcode;


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

@XmlRootElement(name = PostalCode.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = PostalCode.Constants.TYPE_NAME, propOrder = {
        PostalCode.Elements.CODE,
        PostalCode.Elements.CITY_NAME,
        PostalCode.Elements.COUNTRY_CODE,
        PostalCode.Elements.STATE_CODE,
        PostalCode.Elements.ACTIVE,
        PostalCode.Elements.COUNTY_CODE,
        "_elements"
})
public final class PostalCode implements PostalCodeContract, ModelObjectComplete {

    private static final long serialVersionUID = 6097498602725305353L;

    @XmlElement(name = Elements.CODE, required = true)
    private final String code;

    @XmlElement(name = Elements.CITY_NAME, required = false)
    private final String cityName;

    @XmlElement(name = Elements.COUNTRY_CODE, required = true)
    private final String countryCode;

    @XmlElement(name = Elements.STATE_CODE, required = false)
    private final String stateCode;

    @XmlElement(name = Elements.COUNTY_CODE, required = false)
    private final String countyCode;

    @XmlElement(name = Elements.ACTIVE, required = true)
    private final boolean active;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _elements = null;

    /**
     * This constructor should never be called except during JAXB unmarshalling.
     */
    private PostalCode() {
        this.code = null;
        this.cityName = null;
        this.countryCode = null;
        this.stateCode = null;
        countyCode = null;
        this.active = false;
    }

    private PostalCode(Builder builder) {
        code = builder.getCode();
        cityName = builder.getCityName();
        countryCode = builder.getCountryCode();
        stateCode = builder.getStateCode();
        countyCode = builder.getCountyCode();
        active = builder.isActive();
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getCityName() {
        return cityName;
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String getStateCode() {
        return stateCode;
    }

    @Override
    public String getCountyCode() {
        return countyCode;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * This builder constructs an Parameter enforcing the constraints of the {@link PostalCodeContract}.
     */
    public static class Builder implements PostalCodeContract, ModelBuilder, Serializable {

        private static final long serialVersionUID = 7077484401017765844L;

        private String code;
        private String cityName;
        private String countryCode;
        private String stateCode;
        private String countyCode;
        private boolean active;

        private Builder(String code, String countryCode) {
            setCode(code);
            setCountryCode(countryCode);
        }

        /**
         * creates a State with the required fields.
         */
        public static Builder create(String code, String countryCode) {
            final Builder builder = new Builder(code, countryCode);
            builder.setActive(true);
            return builder;
        }

        /**
         * creates a Parameter from an existing {@link PostalCodeContract}.
         */
        public static Builder create(PostalCodeContract contract) {
            final Builder builder = new Builder(contract.getCode(), contract.getCountryCode());
            builder.setActive(contract.isActive());
            builder.setCountyCode(contract.getCountyCode());
            builder.setCityName(contract.getCityName());
            builder.setStateCode(contract.getStateCode());
            return builder;
        }

        @Override
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            if (StringUtils.isBlank(code)) {
                throw new IllegalArgumentException("code is blank");
            }

            this.code = code;
        }

        @Override
        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            if (StringUtils.isBlank(cityName)) {
                throw new IllegalArgumentException("cityName is blank");
            }

            this.cityName = cityName;
        }

        @Override
        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            if (StringUtils.isBlank(countryCode)) {
                throw new IllegalArgumentException("countryCode is blank");
            }

            this.countryCode = countryCode;
        }

        @Override
        public String getStateCode() {
            return stateCode;
        }

        public void setStateCode(String stateCode) {
            if (StringUtils.isBlank(stateCode)) {
                throw new IllegalArgumentException("stateCode is blank");
            }

            this.stateCode = stateCode;
        }

        @Override
        public String getCountyCode() {
            return countyCode;
        }

        public void setCountyCode(String countyCode) {
            if (StringUtils.isBlank(countyCode)) {
                throw new IllegalArgumentException("countyCode is blank");
            }

            this.countyCode = countyCode;
        }

        @Override
        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public PostalCode build() {
            return new PostalCode(this);
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
        final static String ROOT_ELEMENT_NAME = "county";
        final static String TYPE_NAME = "CountyType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {"_elements"};
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String CODE = "code";
        final static String CITY_NAME = "cityName";
        final static String COUNTRY_CODE = "countryCode";
        final static String STATE_CODE = "stateCode";
        final static String COUNTY_CODE = "countyCode";
        final static String ACTIVE = "active";
    }
}
