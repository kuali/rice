package org.kuali.rice.kim.api.identity.name;

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
import org.kuali.rice.kim.api.identity.Type;
import org.kuali.rice.kim.api.identity.TypeContract;
import org.kuali.rice.kim.util.KimConstants;
import org.w3c.dom.Element;

@XmlRootElement(name = EntityName.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityName.Constants.TYPE_NAME, propOrder = {
    EntityName.Elements.ID,
    EntityName.Elements.ENTITY_ID,
    EntityName.Elements.NAME_TYPE,
    EntityName.Elements.TITLE,
    EntityName.Elements.FIRST_NAME,
    EntityName.Elements.MIDDLE_NAME,
    EntityName.Elements.LAST_NAME,
    EntityName.Elements.SUFFIX,
    EntityName.Elements.FORMATTED_NAME,
    EntityName.Elements.TITLE_UNMASKED,
    EntityName.Elements.FIRST_NAME_UNMASKED,
    EntityName.Elements.MIDDLE_NAME_UNMASKED,
    EntityName.Elements.LAST_NAME_UNMASKED,
    EntityName.Elements.SUFFIX_UNMASKED,
    EntityName.Elements.FORMATTED_NAME_UNMASKED,
    EntityName.Elements.SUPPRESS_NAME,
    EntityName.Elements.DEFAULT_VALUE,
    EntityName.Elements.ACTIVE,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityName
    implements ModelObjectComplete, EntityNameContract
{

    @XmlElement(name = Elements.SUFFIX, required = false)
    private final String suffix;
    @XmlElement(name = Elements.ENTITY_ID, required = false)
    private final String entityId;
    @XmlElement(name = Elements.NAME_TYPE, required = false)
    private final Type nameType;
    @XmlElement(name = Elements.FIRST_NAME, required = false)
    private final String firstName;
    @XmlElement(name = Elements.FIRST_NAME_UNMASKED, required = false)
    private final String firstNameUnmasked;
    @XmlElement(name = Elements.MIDDLE_NAME, required = false)
    private final String middleName;
    @XmlElement(name = Elements.MIDDLE_NAME_UNMASKED, required = false)
    private final String middleNameUnmasked;
    @XmlElement(name = Elements.LAST_NAME, required = false)
    private final String lastName;
    @XmlElement(name = Elements.LAST_NAME_UNMASKED, required = false)
    private final String lastNameUnmasked;
    @XmlElement(name = Elements.TITLE, required = false)
    private final String title;
    @XmlElement(name = Elements.TITLE_UNMASKED, required = false)
    private final String titleUnmasked;
    @XmlElement(name = Elements.SUFFIX_UNMASKED, required = false)
    private final String suffixUnmasked;
    @XmlElement(name = Elements.FORMATTED_NAME, required = false)
    private final String formattedName;
    @XmlElement(name = Elements.FORMATTED_NAME_UNMASKED, required = false)
    private final String formattedNameUnmasked;
    @XmlElement(name = Elements.SUPPRESS_NAME, required = false)
    private final boolean suppressName;
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;
    @XmlElement(name = Elements.DEFAULT_VALUE, required = false)
    private final boolean defaultValue;
    @XmlElement(name = Elements.ACTIVE, required = false)
    private final boolean active;
    @XmlElement(name = Elements.ID, required = false)
    private final String id;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     * 
     */
    private EntityName() {
        this.suffix = null;
        this.entityId = null;
        this.nameType = null;
        this.firstName = null;
        this.firstNameUnmasked = null;
        this.middleName = null;
        this.middleNameUnmasked = null;
        this.lastName = null;
        this.lastNameUnmasked = null;
        this.title = null;
        this.titleUnmasked = null;
        this.suffixUnmasked = null;
        this.formattedName = null;
        this.formattedNameUnmasked = null;
        this.suppressName = false;
        this.versionNumber = null;
        this.objectId = null;
        this.defaultValue = false;
        this.active = false;
        this.id = null;
    }

    private EntityName(Builder builder) {
        this.suffix = builder.getSuffix();
        this.entityId = builder.getEntityId();
        this.nameType = builder.getNameType() != null ? builder.getNameType().build() : null;
        this.firstName = builder.getFirstName();
        this.firstNameUnmasked = builder.getFirstNameUnmasked();
        this.middleName = builder.getMiddleName();
        this.middleNameUnmasked = builder.getMiddleNameUnmasked();
        this.lastName = builder.getLastName();
        this.lastNameUnmasked = builder.getLastNameUnmasked();
        this.title = builder.getTitle();
        this.titleUnmasked = builder.getTitleUnmasked();
        this.suffixUnmasked = builder.getSuffixUnmasked();
        this.formattedName = builder.getFormattedName();
        this.formattedNameUnmasked = builder.getFormattedNameUnmasked();
        this.suppressName = builder.isSuppressName();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.defaultValue = builder.isDefaultValue();
        this.active = builder.isActive();
        this.id = builder.getId();
    }

    @Override
    public String getSuffix() {
        return this.suffix;
    }

    @Override
    public String getEntityId() {
        return this.entityId;
    }

    @Override
    public TypeContract getNameType() {
        return this.nameType;
    }

    @Override
    public String getFirstName() {
        return this.firstName;
    }

    @Override
    public String getFirstNameUnmasked() {
        return this.firstNameUnmasked;
    }

    @Override
    public String getMiddleName() {
        return this.middleName;
    }

    @Override
    public String getMiddleNameUnmasked() {
        return this.middleNameUnmasked;
    }

    @Override
    public String getLastName() {
        return this.lastName;
    }

    @Override
    public String getLastNameUnmasked() {
        return this.lastNameUnmasked;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getTitleUnmasked() {
        return this.titleUnmasked;
    }

    @Override
    public String getSuffixUnmasked() {
        return this.suffixUnmasked;
    }

    @Override
    public String getFormattedName() {
        return this.formattedName;
    }

    @Override
    public String getFormattedNameUnmasked() {
        return this.formattedNameUnmasked;
    }

    @Override
    public boolean isSuppressName() {
        return this.suppressName;
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
    public boolean isDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public String getId() {
        return this.id;
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
     * A builder which can be used to construct {@link EntityName} instances.  Enforces the constraints of the {@link EntityNameContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityNameContract
    {

        private String suffix;
        private String entityId;
        private Type.Builder nameType;
        private String firstName;
        private String middleName;
        private String lastName;
        private String title;
        private boolean suppressName;
        private Long versionNumber;
        private String objectId;
        private boolean defaultValue;
        private boolean active;
        private String id;

        private Builder() { }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(EntityNameContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setSuffix(contract.getSuffix());
            builder.setEntityId(contract.getEntityId());
            if (contract.getNameType() != null) {
                builder.setNameType(Type.Builder.create(contract.getNameType()));
            }
            builder.setFirstName(contract.getFirstName());
            builder.setMiddleName(contract.getMiddleName());
            builder.setLastName(contract.getLastName());
            builder.setTitle(contract.getTitle());
            builder.setSuppressName(contract.isSuppressName());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setDefaultValue(contract.isDefaultValue());
            builder.setActive(contract.isActive());
            builder.setId(contract.getId());
            return builder;
        }

        public EntityName build() {
            return new EntityName(this);
        }

        @Override
        public String getSuffix() {
            if (isSuppressName()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.suffix;
        }

        @Override
        public String getEntityId() {
            return this.entityId;
        }

        @Override
        public Type.Builder getNameType() {
            return this.nameType;
        }

        @Override
        public String getFirstName() {
            if (isSuppressName()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.firstName;
        }

        @Override
        public String getFirstNameUnmasked() {
            return this.firstName;
        }

        @Override
        public String getMiddleName() {
            if (isSuppressName()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.middleName;
        }

        @Override
        public String getMiddleNameUnmasked() {
            return this.middleName;
        }

        @Override
        public String getLastName() {
            if (isSuppressName()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.lastName;
        }

        @Override
        public String getLastNameUnmasked() {
            return this.lastName;
        }

        @Override
        public String getTitle() {
            if (isSuppressName()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return this.title;
        }

        @Override
        public String getTitleUnmasked() {
            return this.title;
        }

        @Override
        public String getSuffixUnmasked() {
            return this.suffix;
        }

        @Override
        public String getFormattedName() {
            if (isSuppressName()) {
                return KimConstants.RESTRICTED_DATA_MASK;
            }
            return getFormattedNameUnmasked();
        }

        @Override
        public String getFormattedNameUnmasked() {
            return getLastName() + ", " + getFirstName() + (getMiddleName()==null?"":" " + getMiddleName());
        }

        @Override
        public boolean isSuppressName() {
            return this.suppressName;
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
        public boolean isDefaultValue() {
            return this.defaultValue;
        }

        @Override
        public boolean isActive() {
            return this.active;
        }

        @Override
        public String getId() {
            return this.id;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        public void setEntityId(String entityId) {
            this.entityId = entityId;
        }

        public void setNameType(Type.Builder nameType) {
            this.nameType = nameType;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setMiddleName(String middleName) {

            this.middleName = middleName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public void setTitle(String title) {
            this.title = title;
        }


        private void setSuppressName(boolean suppressName) {
            this.suppressName = suppressName;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setDefaultValue(boolean defaultValue) {
            this.defaultValue = defaultValue;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public void setId(String id) {
            if (StringUtils.isWhitespace(id)) {
                throw new IllegalArgumentException("id is blank");
            }
            this.id = id;
        }

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "entityName";
        final static String TYPE_NAME = "EntityNameType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] {CoreConstants.CommonElements.FUTURE_ELEMENTS };

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String SUFFIX = "suffix";
        final static String ENTITY_ID = "entityId";
        final static String NAME_TYPE = "nameType";
        final static String FIRST_NAME = "firstName";
        final static String FIRST_NAME_UNMASKED = "firstNameUnmasked";
        final static String MIDDLE_NAME = "middleName";
        final static String MIDDLE_NAME_UNMASKED = "middleNameUnmasked";
        final static String LAST_NAME = "lastName";
        final static String LAST_NAME_UNMASKED = "lastNameUnmasked";
        final static String TITLE = "title";
        final static String TITLE_UNMASKED = "titleUnmasked";
        final static String SUFFIX_UNMASKED = "suffixUnmasked";
        final static String FORMATTED_NAME = "formattedName";
        final static String FORMATTED_NAME_UNMASKED = "formattedNameUnmasked";
        final static String SUPPRESS_NAME = "suppressName";
        final static String DEFAULT_VALUE = "defaultValue";
        final static String ACTIVE = "active";
        final static String ID = "id";

    }

}