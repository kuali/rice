package org.kuali.rice.kim.api.identity.personal;

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
import org.kuali.rice.core.api.mo.AbstractJaxbModelObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.kim.api.KimConstants;
import org.w3c.dom.Element;

@XmlRootElement(name = EntityEthnicity.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityEthnicity.Constants.TYPE_NAME, propOrder = {
    EntityEthnicity.Elements.ID,
    EntityEthnicity.Elements.ENTITY_ID,
    EntityEthnicity.Elements.ETHNICITY_CODE,
    EntityEthnicity.Elements.ETHNICITY_CODE_UNMASKED,
    EntityEthnicity.Elements.SUB_ETHNICITY_CODE,
    EntityEthnicity.Elements.SUB_ETHNICITY_CODE_UNMASKED,
    EntityEthnicity.Elements.SUPPRESS_PERSONAL,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityEthnicity extends AbstractJaxbModelObject
    implements EntityEthnicityContract
{

    @XmlElement(name = Elements.ENTITY_ID, required = false)
    private final String entityId;
    @XmlElement(name = Elements.ETHNICITY_CODE, required = false)
    private final String ethnicityCode;
    @XmlElement(name = Elements.ETHNICITY_CODE_UNMASKED, required = false)
    private final String ethnicityCodeUnmasked;
    @XmlElement(name = Elements.SUB_ETHNICITY_CODE, required = false)
    private final String subEthnicityCode;
    @XmlElement(name = Elements.SUB_ETHNICITY_CODE_UNMASKED, required = false)
    private final String subEthnicityCodeUnmasked;
    @XmlElement(name = Elements.SUPPRESS_PERSONAL, required = false)
    private final boolean suppressPersonal;
    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;
    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;
    @XmlElement(name = Elements.ID, required = false)
    private final String id;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     * 
     */
    private EntityEthnicity() {
        this.entityId = null;
        this.ethnicityCode = null;
        this.ethnicityCodeUnmasked = null;
        this.subEthnicityCode = null;
        this.subEthnicityCodeUnmasked = null;
        this.suppressPersonal = false;
        this.versionNumber = null;
        this.objectId = null;
        this.id = null;
    }

    private EntityEthnicity(Builder builder) {
        this.entityId = builder.getEntityId();
        this.ethnicityCode = builder.getEthnicityCode();
        this.ethnicityCodeUnmasked = builder.getEthnicityCodeUnmasked();
        this.subEthnicityCode = builder.getSubEthnicityCode();
        this.subEthnicityCodeUnmasked = builder.getSubEthnicityCodeUnmasked();
        this.suppressPersonal = builder.isSuppressPersonal();
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.id = builder.getId();
    }

    @Override
    public String getEntityId() {
        return this.entityId;
    }

    @Override
    public String getEthnicityCode() {
        return this.ethnicityCode;
    }

    @Override
    public String getEthnicityCodeUnmasked() {
        return this.ethnicityCodeUnmasked;
    }

    @Override
    public String getSubEthnicityCode() {
        return this.subEthnicityCode;
    }

    @Override
    public String getSubEthnicityCodeUnmasked() {
        return this.subEthnicityCodeUnmasked;
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
    public String getId() {
        return this.id;
    }

    /**
     * A builder which can be used to construct {@link EntityEthnicity} instances.  Enforces the constraints of the {@link EntityEthnicityContract}.
     * 
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityEthnicityContract
    {

        private String entityId;
        private String ethnicityCode;
        private String subEthnicityCode;
        private boolean suppressPersonal;
        private Long versionNumber;
        private String objectId;
        private String id;

        private Builder() { }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(EntityEthnicityContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setEntityId(contract.getEntityId());
            builder.setEthnicityCode(contract.getEthnicityCode());
            builder.setSubEthnicityCode(contract.getSubEthnicityCode());
            builder.setSuppressPersonal(contract.isSuppressPersonal());
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setId(contract.getId());
            return builder;
        }

        public EntityEthnicity build() {
            return new EntityEthnicity(this);
        }

        @Override
        public String getEntityId() {
            return this.entityId;
        }

        @Override
        public String getEthnicityCode() {
            if (isSuppressPersonal()) {
                return KimConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
            }
            return this.ethnicityCode;
        }

        @Override
        public String getEthnicityCodeUnmasked() {
            return this.ethnicityCode;
        }

        @Override
        public String getSubEthnicityCode() {
            if (isSuppressPersonal()) {
                return KimConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
            }
            return this.subEthnicityCode;
        }

        @Override
        public String getSubEthnicityCodeUnmasked() {
            return this.subEthnicityCode;
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
        public String getId() {
            return this.id;
        }

        public void setEntityId(String entityId) {
            // TODO add validation of input value if required and throw IllegalArgumentException if needed
            this.entityId = entityId;
        }

        public void setEthnicityCode(String ethnicityCode) {
            this.ethnicityCode = ethnicityCode;
        }

        public void setSubEthnicityCode(String subEthnicityCode) {
            this.subEthnicityCode = subEthnicityCode;
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

        final static String ROOT_ELEMENT_NAME = "entityEthnicity";
        final static String TYPE_NAME = "EntityEthnicityType";
    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     * 
     */
    static class Elements {

        final static String ENTITY_ID = "entityId";
        final static String ETHNICITY_CODE = "ethnicityCode";
        final static String ETHNICITY_CODE_UNMASKED = "ethnicityCodeUnmasked";
        final static String SUB_ETHNICITY_CODE = "subEthnicityCode";
        final static String SUB_ETHNICITY_CODE_UNMASKED = "subEthnicityCodeUnmasked";
        final static String SUPPRESS_PERSONAL = "suppressPersonal";
        final static String ID = "id";

    }

}