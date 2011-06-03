package org.kuali.rice.kim.api.entity.type;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.kim.api.entity.address.EntityAddress;
import org.kuali.rice.kim.api.entity.address.EntityAddressContract;
import org.kuali.rice.kim.api.entity.email.EntityEmail;
import org.kuali.rice.kim.api.entity.phone.EntityPhone;
import org.kuali.rice.kim.api.entity.phone.EntityPhoneContract;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

@XmlRootElement(name = EntityTypeDataDefault.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityTypeDataDefault.Constants.TYPE_NAME, propOrder = {
    EntityTypeDataDefault.Elements.ENTITY_TYPE_CODE,
    EntityTypeDataDefault.Elements.DEFAULT_ADDRESS,
    EntityTypeDataDefault.Elements.DEFAULT_EMAIL_ADDRESS,
    EntityTypeDataDefault.Elements.DEFAULT_PHONE_NUMBER,

    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityTypeDataDefault
{
    @XmlElement(name = Elements.ENTITY_TYPE_CODE, required = true)
    private final String entityTypeCode;
    @XmlElement(name = Elements.DEFAULT_ADDRESS, required = false)
    private final EntityAddress defaultAddress;
    @XmlElement(name = Elements.DEFAULT_EMAIL_ADDRESS, required = false)
    private final EntityEmail defaultEmailAddress;
    @XmlElement(name = Elements.DEFAULT_PHONE_NUMBER, required = false)
    private final EntityPhone defaultPhoneNumber;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private EntityTypeDataDefault() {
        this.entityTypeCode = null;
        this.defaultAddress = null;
        this.defaultEmailAddress = null;
        this.defaultPhoneNumber = null;

    }

    public EntityTypeDataDefault(String entityTypeCode, EntityAddress defaultAddress, EntityEmail defaultEmailAddress, EntityPhone defaultPhoneNumber) {
        this.entityTypeCode = entityTypeCode;
        this.defaultAddress = defaultAddress;
        this.defaultEmailAddress = defaultEmailAddress;
        this.defaultPhoneNumber = defaultPhoneNumber;
    }

    public String getEntityTypeCode() {
        return this.entityTypeCode;
    }
    public EntityAddressContract getDefaultAddress() {
        return this.defaultAddress;
    }

    public EntityEmail getDefaultEmailAddress() {
        return this.defaultEmailAddress;
    }

    public EntityPhoneContract getDefaultPhoneNumber() {
        return this.defaultPhoneNumber;
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
     * Defines some internal constants used on this class.
     *
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "entityTypeDataDefault";
        final static String TYPE_NAME = "EntityTypeDataDefaultType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] {CoreConstants.CommonElements.FUTURE_ELEMENTS };

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     *
     */
    static class Elements {
        final static String ENTITY_TYPE_CODE = "entityTypeCode";
        final static String DEFAULT_ADDRESS = "defaultAddress";
        final static String DEFAULT_EMAIL_ADDRESS = "defaultEmailAddress";
        final static String DEFAULT_PHONE_NUMBER = "defaultPhoneNumber";

    }

}
