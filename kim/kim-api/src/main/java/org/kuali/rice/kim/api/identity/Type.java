package org.kuali.rice.kim.api.identity;

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

@XmlRootElement(name = Type.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Type.Constants.TYPE_NAME, propOrder = {
    Type.Elements.CODE,
    Type.Elements.NAME,
    Type.Elements.SORT_CODE,
    Type.Elements.ACTIVE,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class Type
    implements ModelObjectComplete, TypeContract
{
    @XmlElement(name = Elements.CODE, required = true)
    private final String code;
    @XmlElement(name = Elements.NAME, required = false)
    private final String name;
    @XmlElement(name = Elements.SORT_CODE, required = false)
    private final String sortCode;
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;
    @XmlElement(name = Elements.ACTIVE, required = false)
    private final boolean active;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     * 
     */
    private Type() {
        this.name = null;
        this.code = null;
        this.sortCode = null;
        this.versionNumber = null;
        this.objectId = null;
        this.active = false;
    }

    private Type(Builder builder) {
        this.name = builder.getName();
        this.code = builder.getCode();
        this.sortCode = builder.getSortCode();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.active = builder.isActive();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getSortCode() {
        return this.sortCode;
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
    public boolean isActive() {
        return this.active;
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
     * A builder which can be used to construct {@link Type} instances.  Enforces the constraints of the {@link TypeContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, TypeContract
    {

        private String name;
        private String code;
        private String sortCode;
        private Long versionNumber;
        private String objectId;
        private boolean active;

        private Builder(String code) {
            setCode(code);
        }

        public static Builder create(String code) {
            return new Builder(code);
        }

        public static Builder create(TypeContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            // TODO if create() is modified to accept required parameters, this will need to be modified
            Builder builder = create(contract.getCode());
            builder.setName(contract.getName());
            builder.setSortCode(contract.getSortCode());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setActive(contract.isActive());
            return builder;
        }

        public Type build() {
            return new Type(this);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getCode() {
            return this.code;
        }

        @Override
        public String getSortCode() {
            return this.sortCode;
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
        public boolean isActive() {
            return this.active;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCode(String code) {
            if (StringUtils.isWhitespace(code)) {
                throw new IllegalArgumentException("code is empty");
            }
            this.code = code;
        }

        public void setSortCode(String sortCode) {
            this.sortCode = sortCode;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

    }


    /**
     * Defines some internal constants used on this class.
     * 
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "type";
        final static String TYPE_NAME = "typeType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] {CoreConstants.CommonElements.FUTURE_ELEMENTS };

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String NAME = "name";
        final static String CODE = "code";
        final static String SORT_CODE = "sortCode";
        final static String ACTIVE = "active";

    }

}
