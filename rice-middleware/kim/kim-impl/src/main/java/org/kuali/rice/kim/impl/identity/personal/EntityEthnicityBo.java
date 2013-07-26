package org.kuali.rice.kim.impl.identity.personal;

import org.eclipse.persistence.annotations.Customizer;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.identity.personal.EntityEthnicity;
import org.kuali.rice.kim.api.identity.personal.EntityEthnicityContract;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.provider.jpa.eclipselink.EclipseLinkSequenceCustomizer;

import org.kuali.rice.krad.data.platform.generator.Sequence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Customizer(EclipseLinkSequenceCustomizer.class)
@Sequence(name="KRIM_ENTITY_ETHNIC_ID_S", property = "id")
@Table(name = "KRIM_ENTITY_ETHNIC_T")
public class EntityEthnicityBo extends PersistableBusinessObjectBase implements EntityEthnicityContract {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "ENTITY_ID")
    private String entityId;
    @Column(name = "ETHNCTY_CD")
    private String ethnicityCode;
    @Column(name = "SUB_ETHNCTY_CD")
    private String subEthnicityCode;
    private boolean hispanicOrLatino;
    private String raceEthnicityTypeCode;
    private EntityEthnicityRaceTypeBo raceEthnicityCode;
    private String localRaceEthnicityCode;
    private Double percentage;

    @Transient
    private boolean suppressPersonal;

    public static EntityEthnicity to(EntityEthnicityBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityEthnicity.Builder.create(bo).build();
    }

    /**
     * Creates a EntityEthnicityBo business object from an immutable representation of a EntityEthnicity.
     *
     * @param immutable an immutable EntityEthnicity
     * @return a EntityEthnicityBo
     */
    public static EntityEthnicityBo from(EntityEthnicity immutable) {
        if (immutable == null) {
            return null;
        }

        EntityEthnicityBo bo = new EntityEthnicityBo();
        bo.entityId = immutable.getEntityId();
        bo.id = immutable.getId();
        bo.ethnicityCode = immutable.getEthnicityCodeUnmasked();
        bo.subEthnicityCode = immutable.getSubEthnicityCodeUnmasked();

        //convert list of raceEthnicity types
        if (immutable.getRaceEthnicityCodeUnmasked() != null) {
            bo.raceEthnicityCode = EntityEthnicityRaceTypeBo.from(immutable.getRaceEthnicityCodeUnmasked());
        }

        bo.hispanicOrLatino = immutable.isHispanicOrLatino();
        bo.localRaceEthnicityCode = immutable.getLocalRaceEthnicityCodeUnmasked();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());
        bo.setPercentage(immutable.getPercentage());
        return bo;
    }

    @Override
    public boolean isSuppressPersonal() {
        try {
            EntityPrivacyPreferences privacy = KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(
                    getEntityId());
            if (privacy != null) {
                this.suppressPersonal = privacy.isSuppressPersonal();
            } else {
                this.suppressPersonal = false;
            }
        } catch (NullPointerException e) {
            return false;
        } catch (ClassCastException c) {
            return false;
        }

        return suppressPersonal;
    }

    @Override
    public String getEthnicityCode() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.ethnicityCode;
    }

    @Override
    public String getSubEthnicityCode() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.subEthnicityCode;
    }

    @Override
    public String getEthnicityCodeUnmasked() {
        return this.ethnicityCode;
    }

    @Override
    public String getSubEthnicityCodeUnmasked() {
        return this.subEthnicityCode;
    }

    @Override
    public EntityEthnicityRaceTypeBo getRaceEthnicityCode() {
        if (isSuppressPersonal()) {
            return null;
        }

        return this.raceEthnicityCode;
    }

    @Override
    public EntityEthnicityRaceTypeBo getRaceEthnicityCodeUnmasked() {
        return this.raceEthnicityCode;
    }

    @Override
    public String getLocalRaceEthnicityCode() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.localRaceEthnicityCode;
    }

    @Override
    public String getLocalRaceEthnicityCodeUnmasked() {
        return this.localRaceEthnicityCode;
    }

    @Override
    public Double getPercentage() {
        if (isSuppressPersonal()) {
            return null;
        }
        return this.percentage;
    }

    @Override
    public Double getPercentageUnmasked() {
        return this.percentage;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getRaceEthnicityTypeCode() {
        return raceEthnicityTypeCode;
    }

    public void setRaceEthnicityTypeCode(String raceEthnicityTypeCode) {
        this.raceEthnicityTypeCode = raceEthnicityTypeCode;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setEthnicityCode(String ethnicityCode) {
        this.ethnicityCode = ethnicityCode;
    }

    public void setSubEthnicityCode(String subEthnicityCode) {
        this.subEthnicityCode = subEthnicityCode;
    }

    public boolean getHispanicOrLatino() {
        return hispanicOrLatino;
    }

    @Override
    public boolean isHispanicOrLatino() {
        return hispanicOrLatino;
    }

    public void setHispanicOrLatino(boolean hispanicOrLatino) {
        this.hispanicOrLatino = hispanicOrLatino;
    }

    public void setRaceEthnicityCodes(EntityEthnicityRaceTypeBo raceEthnicityCode) {
        this.raceEthnicityCode = raceEthnicityCode;
    }

    public void setLocalRaceEthnicityCode(String localRaceEthnicityCode) {
        this.localRaceEthnicityCode = localRaceEthnicityCode;
    }

    public boolean getSuppressPersonal() {
        return suppressPersonal;
    }

    public void setSuppressPersonal(boolean suppressPersonal) {
        this.suppressPersonal = suppressPersonal;
    }

}
