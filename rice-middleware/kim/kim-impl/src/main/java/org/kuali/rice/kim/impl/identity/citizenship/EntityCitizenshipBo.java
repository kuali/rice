package org.kuali.rice.kim.impl.identity.citizenship;

import org.eclipse.persistence.annotations.Convert;
import org.joda.time.DateTime;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenship;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "KRIM_ENTITY_CTZNSHP_T")
public class EntityCitizenshipBo extends PersistableBusinessObjectBase implements EntityCitizenshipContract {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ENTITY_CTZNSHP_ID")
    private String id;
    @Column(name = "ENTITY_ID")
    private String entityId;
    @Column(name = "POSTAL_CNTRY_CD")
    private String countryCode;
    @Column(name = "CTZNSHP_STAT_CD")
    private String statusCode;
    @Column(name = "strt_dt")
    private Timestamp startDateValue;
    @Column(name = "end_dt")
    private Timestamp endDateValue;
    @ManyToOne(targetEntity = EntityCitizenshipStatusBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(
            name = "CTZNSHP_STAT_CD", insertable = false, updatable = false)
    private EntityCitizenshipStatusBo status;
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;

    @Column(name = "chng_dt")
    private Timestamp changeDateValue;

    @Column(name = "CTZNSHP_CHNG_CD")
    private String changeCode;
    @ManyToOne(targetEntity = EntityCitizenshipChangeTypeBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(
            name = "CTZNSHP_CHNG_CD", insertable = false, updatable = false)
    private EntityCitizenshipChangeTypeBo changeType;




    public static EntityCitizenship to(EntityCitizenshipBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityCitizenship.Builder.create(bo).build();
    }

    /**
     * Creates a EntityCitizenshipBo business object from an immutable representation of a EntityCitizenship.
     *
     * @param immutable an immutable EntityCitizenship
     * @return a EntityCitizenshipBo
     */
    public static EntityCitizenshipBo from(EntityCitizenship immutable) {
        if (immutable == null) {
            return null;
        }

        EntityCitizenshipBo bo = new EntityCitizenshipBo();
        bo.active = immutable.isActive();

        if (immutable.getChangeType() != null) {
            bo.changeCode = immutable.getChangeType().getCode();
            bo.changeType = EntityCitizenshipChangeTypeBo.from(immutable.getChangeType());
        }
        if (immutable.getStatus() != null) {
            bo.setStatusCode(immutable.getStatus().getCode());
            bo.setStatus(EntityCitizenshipStatusBo.from(immutable.getStatus()));
        }

        bo.id = immutable.getId();
        bo.entityId = immutable.getEntityId();
        bo.countryCode = immutable.getCountryCode();
        if (immutable.getStartDate() != null) {
            bo.startDateValue = new Timestamp(immutable.getStartDate().getMillis());
        }

        if (immutable.getEndDate() != null) {
            bo.endDateValue = new Timestamp(immutable.getEndDate().getMillis());
        }
        if (immutable.getChangeDate() != null) {
            bo.changeDateValue = new Timestamp(immutable.getEndDate().getMillis());
        }
        if (immutable.getChangeDate() != null) {
            bo.setChangeDateValue(new Timestamp(immutable.getChangeDate().getMillis()));
        }

        bo.active = immutable.isActive();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());

        return bo;
    }

    @Override
    public DateTime getStartDate() {
        if (this.startDateValue != null) {
            return new DateTime(this.startDateValue);
        }

        return null;
    }

    @Override
    public DateTime getEndDate() {
        if (this.endDateValue != null) {
            return new DateTime(this.endDateValue);
        }

        return null;
    }

    @Override
    public EntityCitizenshipChangeTypeBo getChangeType() {
        return this.changeType;
    }

    public void setChangeType(EntityCitizenshipChangeTypeBo changeType) {
        this.changeType = changeType;
    }

    public String getChangeCode() {
        return changeCode;
    }

    public void setChangeCode(String changeCode) {
        this.changeCode = changeCode;
    }

    @Override
    public DateTime getChangeDate() {
        return this.changeDateValue == null ? null : new DateTime(changeDateValue.getTime());
    }

    public Timestamp getChangeDateValue() {
        return changeDateValue;
    }

    public void setChangeDateValue(Timestamp changeDateValue) {
        this.changeDateValue = changeDateValue;
    }

    @Override
    public EntityCitizenshipStatusBo getStatus() {
        return this.status;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Timestamp getStartDateValue() {
        return startDateValue;
    }

    public void setStartDateValue(Timestamp startDateValue) {
        this.startDateValue = startDateValue;
    }

    public Timestamp getEndDateValue() {
        return endDateValue;
    }

    public void setEndDateValue(Timestamp endDateValue) {
        this.endDateValue = endDateValue;
    }

    public void setStatus(EntityCitizenshipStatusBo status) {
        this.status = status;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


}
