package org.kuali.rice.kim.api.identity.personal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.kim.api.util.KualiDateMask;
import org.kuali.rice.kim.util.KimConstants;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Date;
import java.util.Collection;

@XmlRootElement(name = EntityBioDemographics.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityBioDemographics.Constants.TYPE_NAME, propOrder = {
    EntityBioDemographics.Elements.ENTITY_ID,
    EntityBioDemographics.Elements.DECEASED_DATE,
    EntityBioDemographics.Elements.BIRTH_DATE,
    EntityBioDemographics.Elements.GENDER_CODE,
    EntityBioDemographics.Elements.MARITAL_STATUS_CODE,
    EntityBioDemographics.Elements.PRIMARY_LANGUAGE_CODE,
    EntityBioDemographics.Elements.SECONDARY_LANGUAGE_CODE,
    EntityBioDemographics.Elements.COUNTRY_OF_BIRTH_CODE,
    EntityBioDemographics.Elements.BIRTH_STATE_CODE,
    EntityBioDemographics.Elements.CITY_OF_BIRTH,
    EntityBioDemographics.Elements.GEOGRAPHIC_ORIGIN,
    EntityBioDemographics.Elements.BIRTH_DATE_UNMASKED,
    EntityBioDemographics.Elements.GENDER_CODE_UNMASKED,
    EntityBioDemographics.Elements.MARITAL_STATUS_CODE_UNMASKED,
    EntityBioDemographics.Elements.PRIMARY_LANGUAGE_CODE_UNMASKED,
    EntityBioDemographics.Elements.SECONDARY_LANGUAGE_CODE_UNMASKED,
    EntityBioDemographics.Elements.COUNTRY_OF_BIRTH_CODE_UNMASKED,
    EntityBioDemographics.Elements.BIRTH_STATE_CODE_UNMASKED,
    EntityBioDemographics.Elements.CITY_OF_BIRTH_UNMASKED,
    EntityBioDemographics.Elements.GEOGRAPHIC_ORIGIN_UNMASKED,
    EntityBioDemographics.Elements.SUPPRESS_PERSONAL,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityBioDemographics
    implements ModelObjectComplete, EntityBioDemographicsContract
{

    @XmlElement(name = Elements.ENTITY_ID, required = false)
    private final String entityId;
    @XmlElement(name = Elements.DECEASED_DATE, required = false)
    private final Date deceasedDate;
    @XmlElement(name = Elements.BIRTH_DATE, required = false)
    private final Date birthDate;
    @XmlElement(name = Elements.GENDER_CODE, required = false)
    private final String genderCode;
    @XmlElement(name = Elements.MARITAL_STATUS_CODE, required = false)
    private final String maritalStatusCode;
    @XmlElement(name = Elements.PRIMARY_LANGUAGE_CODE, required = false)
    private final String primaryLanguageCode;
    @XmlElement(name = Elements.SECONDARY_LANGUAGE_CODE, required = false)
    private final String secondaryLanguageCode;
    @XmlElement(name = Elements.COUNTRY_OF_BIRTH_CODE, required = false)
    private final String countryOfBirthCode;
    @XmlElement(name = Elements.BIRTH_STATE_CODE, required = false)
    private final String birthStateCode;
    @XmlElement(name = Elements.CITY_OF_BIRTH, required = false)
    private final String cityOfBirth;
    @XmlElement(name = Elements.GEOGRAPHIC_ORIGIN, required = false)
    private final String geographicOrigin;
    @XmlElement(name = Elements.BIRTH_DATE_UNMASKED, required = false)
    private final Date birthDateUnmasked;
    @XmlElement(name = Elements.GENDER_CODE_UNMASKED, required = false)
    private final String genderCodeUnmasked;
    @XmlElement(name = Elements.MARITAL_STATUS_CODE_UNMASKED, required = false)
    private final String maritalStatusCodeUnmasked;
    @XmlElement(name = Elements.PRIMARY_LANGUAGE_CODE_UNMASKED, required = false)
    private final String primaryLanguageCodeUnmasked;
    @XmlElement(name = Elements.SECONDARY_LANGUAGE_CODE_UNMASKED, required = false)
    private final String secondaryLanguageCodeUnmasked;
    @XmlElement(name = Elements.COUNTRY_OF_BIRTH_CODE_UNMASKED, required = false)
    private final String countryOfBirthCodeUnmasked;
    @XmlElement(name = Elements.BIRTH_STATE_CODE_UNMASKED, required = false)
    private final String birthStateCodeUnmasked;
    @XmlElement(name = Elements.CITY_OF_BIRTH_UNMASKED, required = false)
    private final String cityOfBirthUnmasked;
    @XmlElement(name = Elements.GEOGRAPHIC_ORIGIN_UNMASKED, required = false)
    private final String geographicOriginUnmasked;
    @XmlElement(name = Elements.SUPPRESS_PERSONAL, required = false)
    private final boolean suppressPersonal;
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     * 
     */
    private EntityBioDemographics() {
        this.entityId = null;
        this.deceasedDate = null;
        this.birthDate = null;
        this.genderCode = null;
        this.maritalStatusCode = null;
        this.primaryLanguageCode = null;
        this.secondaryLanguageCode = null;
        this.countryOfBirthCode = null;
        this.birthStateCode = null;
        this.cityOfBirth = null;
        this.geographicOrigin = null;
        this.birthDateUnmasked = null;
        this.genderCodeUnmasked = null;
        this.maritalStatusCodeUnmasked = null;
        this.primaryLanguageCodeUnmasked = null;
        this.secondaryLanguageCodeUnmasked = null;
        this.countryOfBirthCodeUnmasked = null;
        this.birthStateCodeUnmasked = null;
        this.cityOfBirthUnmasked = null;
        this.geographicOriginUnmasked = null;
        this.suppressPersonal = false;
        this.versionNumber = null;
        this.objectId = null;
    }

    private EntityBioDemographics(Builder builder) {
        this.entityId = builder.getEntityId();
        this.deceasedDate = builder.getDeceasedDate();
        this.birthDate = builder.getBirthDate();
        this.genderCode = builder.getGenderCode();
        this.maritalStatusCode = builder.getMaritalStatusCode();
        this.primaryLanguageCode = builder.getPrimaryLanguageCode();
        this.secondaryLanguageCode = builder.getSecondaryLanguageCode();
        this.countryOfBirthCode = builder.getCountryOfBirthCode();
        this.birthStateCode = builder.getBirthStateCode();
        this.cityOfBirth = builder.getCityOfBirth();
        this.geographicOrigin = builder.getGeographicOrigin();
        this.birthDateUnmasked = builder.getBirthDateUnmasked();
        this.genderCodeUnmasked = builder.getGenderCodeUnmasked();
        this.maritalStatusCodeUnmasked = builder.getMaritalStatusCodeUnmasked();
        this.primaryLanguageCodeUnmasked = builder.getPrimaryLanguageCodeUnmasked();
        this.secondaryLanguageCodeUnmasked = builder.getSecondaryLanguageCodeUnmasked();
        this.countryOfBirthCodeUnmasked = builder.getCountryOfBirthCodeUnmasked();
        this.birthStateCodeUnmasked = builder.getBirthStateCodeUnmasked();
        this.cityOfBirthUnmasked = builder.getCityOfBirthUnmasked();
        this.geographicOriginUnmasked = builder.getGeographicOriginUnmasked();
        this.suppressPersonal = builder.isSuppressPersonal();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
    }

    @Override
    public String getEntityId() {
        return this.entityId;
    }

    @Override
    public Date getDeceasedDate() {
        return this.deceasedDate;
    }

    @Override
    public Date getBirthDate() {
        return this.birthDate;
    }

    @Override
    public String getGenderCode() {
        return this.genderCode;
    }

    @Override
    public String getMaritalStatusCode() {
        return this.maritalStatusCode;
    }

    @Override
    public String getPrimaryLanguageCode() {
        return this.primaryLanguageCode;
    }

    @Override
    public String getSecondaryLanguageCode() {
        return this.secondaryLanguageCode;
    }

    @Override
    public String getCountryOfBirthCode() {
        return this.countryOfBirthCode;
    }

    @Override
    public String getBirthStateCode() {
        return this.birthStateCode;
    }

    @Override
    public String getCityOfBirth() {
        return this.cityOfBirth;
    }

    @Override
    public String getGeographicOrigin() {
        return this.geographicOrigin;
    }

    @Override
    public Date getBirthDateUnmasked() {
        return this.birthDateUnmasked;
    }

    @Override
    public String getGenderCodeUnmasked() {
        return this.genderCodeUnmasked;
    }

    @Override
    public String getMaritalStatusCodeUnmasked() {
        return this.maritalStatusCodeUnmasked;
    }

    @Override
    public String getPrimaryLanguageCodeUnmasked() {
        return this.primaryLanguageCodeUnmasked;
    }

    @Override
    public String getSecondaryLanguageCodeUnmasked() {
        return this.secondaryLanguageCodeUnmasked;
    }

    @Override
    public String getCountryOfBirthCodeUnmasked() {
        return this.countryOfBirthCodeUnmasked;
    }

    @Override
    public String getBirthStateCodeUnmasked() {
        return this.birthStateCodeUnmasked;
    }

    @Override
    public String getCityOfBirthUnmasked() {
        return this.cityOfBirthUnmasked;
    }

    @Override
    public String getGeographicOriginUnmasked() {
        return this.geographicOriginUnmasked;
    }

    @Override
    public boolean isSuppressPersonal() {
        return this.suppressPersonal;
    }

    @Override
    public Long getVersionNumber() {
        return this.versionNumber;
    }

    @Override
    public String getObjectId() {
        return this.objectId;
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
     * A builder which can be used to construct {@link EntityBioDemographics} instances.  Enforces the constraints of the {@link EntityBioDemographicsContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityBioDemographicsContract
    {

        private String entityId;
        private Date deceasedDate;
        private Date birthDate;
        private String genderCode;
        private String maritalStatusCode;
        private String primaryLanguageCode;
        private String secondaryLanguageCode;
        private String countryOfBirthCode;
        private String birthStateCode;
        private String cityOfBirth;
        private String geographicOrigin;
        private boolean suppressPersonal;
        private Long versionNumber;
        private String objectId;

        private Builder(String entityId, String genderCode) {
            setEntityId(entityId);
            setGenderCode(genderCode);
        }

        public static Builder create(String entityId, String genderCode) {
            // TODO modify as needed to pass any required values and add them to the signature of the 'create' method
            return new Builder(entityId, genderCode);
        }

        public static Builder create(EntityBioDemographicsContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(contract.getEntityId(), contract.getGenderCode());
            builder.setDeceasedDate(contract.getDeceasedDate());
            builder.setBirthDate(contract.getBirthDate());
            builder.setMaritalStatusCode(contract.getMaritalStatusCode());
            builder.setPrimaryLanguageCode(contract.getPrimaryLanguageCode());
            builder.setSecondaryLanguageCode(contract.getSecondaryLanguageCode());
            builder.setCountryOfBirthCode(contract.getCountryOfBirthCode());
            builder.setBirthStateCode(contract.getBirthStateCode());
            builder.setCityOfBirth(contract.getCityOfBirth());
            builder.setGeographicOrigin(contract.getGeographicOrigin());
            builder.setSuppressPersonal(contract.isSuppressPersonal());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            return builder;
        }

        public EntityBioDemographics build() {
            return new EntityBioDemographics(this);
        }

        @Override
        public String getEntityId() {
            return this.entityId;
        }

        @Override
        public Date getDeceasedDate() {
            return this.deceasedDate;
        }

        @Override
        public Date getBirthDate() {
            if (isSuppressPersonal()) {
                return KualiDateMask.getInstance();
            }
            return this.birthDate;
        }

        @Override
        public String getGenderCode() {
            if (isSuppressPersonal()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.genderCode;
        }

        @Override
        public String getMaritalStatusCode() {
            if (isSuppressPersonal()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.maritalStatusCode;
        }

        @Override
        public String getPrimaryLanguageCode() {
            if (isSuppressPersonal()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.primaryLanguageCode;
        }

        @Override
        public String getSecondaryLanguageCode() {
            if (isSuppressPersonal()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.secondaryLanguageCode;
        }

        @Override
        public String getCountryOfBirthCode() {
            if (isSuppressPersonal()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.countryOfBirthCode;
        }

        @Override
        public String getBirthStateCode() {
            if (isSuppressPersonal()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.birthStateCode;
        }

        @Override
        public String getCityOfBirth() {
            if (isSuppressPersonal()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.cityOfBirth;
        }

        @Override
        public String getGeographicOrigin() {
            if (isSuppressPersonal()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.geographicOrigin;
        }

        @Override
        public Date getBirthDateUnmasked() {
            return this.birthDate;
        }

        @Override
        public String getGenderCodeUnmasked() {
            return this.genderCode;
        }

        @Override
        public String getMaritalStatusCodeUnmasked() {
            return this.maritalStatusCode;
        }

        @Override
        public String getPrimaryLanguageCodeUnmasked() {
            return this.primaryLanguageCode;
        }

        @Override
        public String getSecondaryLanguageCodeUnmasked() {
            return this.secondaryLanguageCode;
        }

        @Override
        public String getCountryOfBirthCodeUnmasked() {
            return this.countryOfBirthCode;
        }

        @Override
        public String getBirthStateCodeUnmasked() {
            return this.birthStateCode;
        }

        @Override
        public String getCityOfBirthUnmasked() {
            return this.cityOfBirth;
        }

        @Override
        public String getGeographicOriginUnmasked() {
            return this.geographicOrigin;
        }

        @Override
        public boolean isSuppressPersonal() {
            return this.suppressPersonal;
        }

        @Override
        public Long getVersionNumber() {
            return this.versionNumber;
        }

        @Override
        public String getObjectId() {
            return this.objectId;
        }

        public void setEntityId(String entityId) {
            if (StringUtils.isEmpty(entityId)) {
                throw new IllegalArgumentException("id is empty");
            }
            this.entityId = entityId;
        }

        public void setDeceasedDate(Date deceasedDate) {
            this.deceasedDate = deceasedDate;
        }

        public void setBirthDate(Date birthDate) {
            this.birthDate = birthDate;
        }

        public void setGenderCode(String genderCode) {
            if (StringUtils.isEmpty(genderCode)) {
                throw new IllegalArgumentException("genderCode is empty");
            }
            this.genderCode = genderCode;
        }

        public void setMaritalStatusCode(String maritalStatusCode) {
            this.maritalStatusCode = maritalStatusCode;
        }

        public void setPrimaryLanguageCode(String primaryLanguageCode) {
            this.primaryLanguageCode = primaryLanguageCode;
        }

        public void setSecondaryLanguageCode(String secondaryLanguageCode) {
            this.secondaryLanguageCode = secondaryLanguageCode;
        }

        public void setCountryOfBirthCode(String countryOfBirthCode) {
            this.countryOfBirthCode = countryOfBirthCode;
        }

        public void setBirthStateCode(String birthStateCode) {
            this.birthStateCode = birthStateCode;
        }

        public void setCityOfBirth(String cityOfBirth) {
            this.cityOfBirth = cityOfBirth;
        }

        public void setGeographicOrigin(String geographicOrigin) {
            this.geographicOrigin = geographicOrigin;
        }

        private void setSuppressPersonal(boolean suppressPersonal) {
            this.suppressPersonal = suppressPersonal;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "entityBioDemographics";
        final static String TYPE_NAME = "EntityBioDemographicsType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] {CoreConstants.CommonElements.FUTURE_ELEMENTS };

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String ENTITY_ID = "entityId";
        final static String DECEASED_DATE = "deceasedDate";
        final static String BIRTH_DATE = "birthDate";
        final static String GENDER_CODE = "genderCode";
        final static String MARITAL_STATUS_CODE = "maritalStatusCode";
        final static String PRIMARY_LANGUAGE_CODE = "primaryLanguageCode";
        final static String SECONDARY_LANGUAGE_CODE = "secondaryLanguageCode";
        final static String COUNTRY_OF_BIRTH_CODE = "countryOfBirthCode";
        final static String BIRTH_STATE_CODE = "birthStateCode";
        final static String CITY_OF_BIRTH = "cityOfBirth";
        final static String GEOGRAPHIC_ORIGIN = "geographicOrigin";
        final static String BIRTH_DATE_UNMASKED = "birthDateUnmasked";
        final static String GENDER_CODE_UNMASKED = "genderCodeUnmasked";
        final static String MARITAL_STATUS_CODE_UNMASKED = "maritalStatusCodeUnmasked";
        final static String PRIMARY_LANGUAGE_CODE_UNMASKED = "primaryLanguageCodeUnmasked";
        final static String SECONDARY_LANGUAGE_CODE_UNMASKED = "secondaryLanguageCodeUnmasked";
        final static String COUNTRY_OF_BIRTH_CODE_UNMASKED = "countryOfBirthCodeUnmasked";
        final static String BIRTH_STATE_CODE_UNMASKED = "birthStateCodeUnmasked";
        final static String CITY_OF_BIRTH_UNMASKED = "cityOfBirthUnmasked";
        final static String GEOGRAPHIC_ORIGIN_UNMASKED = "geographicOriginUnmasked";
        final static String SUPPRESS_PERSONAL = "suppressPersonal";

    }

}