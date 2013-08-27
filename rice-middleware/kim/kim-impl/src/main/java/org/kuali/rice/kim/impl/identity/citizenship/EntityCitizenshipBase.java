package org.kuali.rice.kim.impl.identity.citizenship;

import org.joda.time.DateTime;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@MappedSuperclass
public abstract class EntityCitizenshipBase extends PersistableBusinessObjectBase implements EntityCitizenshipContract {
    private static final long serialVersionUID = 1L;

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

    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;

    @Column(name = "chng_dt")
    private Timestamp changeDateValue;

    @Column(name = "CTZNSHP_CHNG_CD")
    private String changeCode;


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
