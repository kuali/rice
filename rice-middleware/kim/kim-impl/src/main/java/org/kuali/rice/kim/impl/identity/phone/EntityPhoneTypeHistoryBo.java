package org.kuali.rice.kim.impl.identity.phone;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.CodedAttributeHistory;
import org.kuali.rice.kim.impl.identity.CodedAttributeHistoryBoContract;
import org.kuali.rice.kim.impl.identity.CodedAttributeHistoryBoUtil;
import org.kuali.rice.kim.impl.identity.EntityTypeBo;
import org.kuali.rice.kim.impl.identity.name.EntityNameTypeBo;

import javax.persistence.Column;
import java.sql.Timestamp;

public class EntityPhoneTypeHistoryBo extends EntityPhoneTypeBo implements CodedAttributeHistoryBoContract {
    private static final long serialVersionUID = 7546046609268383835L;
    @Column(name ="HIST_ID")
    private Long historyId;
    @Column(name = "ACTV_FRM_DT")
    private Timestamp activeFromDateValue;
    @Column(name = "ACTV_TO_DT")
    private Timestamp activeToDateValue;

    @Override
    public Long getHistoryId() {
        return historyId;
    }

    @Override
    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public Timestamp getActiveFromDateValue() {
        return activeFromDateValue;
    }

    @Override
    public void setActiveFromDateValue(Timestamp activeFromDateValue) {
        this.activeFromDateValue = activeFromDateValue;
    }

    public Timestamp getActiveToDateValue() {
        return activeToDateValue;
    }

    @Override
    public void setActiveToDateValue(Timestamp activeToDateValue) {
        this.activeToDateValue = activeToDateValue;
    }

    public boolean isActive(Timestamp activeAsOfDate) {
        return InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), new DateTime(
                activeAsOfDate.getTime()));
    }

    @Override
    public boolean isActive(DateTime activeAsOfDate) {
        return this.isActive() && InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), activeAsOfDate);
    }

    @Override
    public boolean isActiveNow() {
        return this.isActive() && InactivatableFromToUtils.isActive(getActiveFromDate(), getActiveToDate(), null);
    }

    @Override
    public DateTime getActiveFromDate() {
        return this.activeFromDateValue == null ? null : new DateTime(this.activeFromDateValue.getTime());
    }

    @Override
    public DateTime getActiveToDate() {
        return this.activeToDateValue == null ? null : new DateTime(this.activeToDateValue.getTime());
    }

    public static CodedAttributeHistory to(EntityPhoneTypeHistoryBo bo) {
        if (bo == null) {
            return null;
        }

        return CodedAttributeHistory.Builder.create(bo).build();
    }

    public static EntityPhoneTypeHistoryBo from(CodedAttributeHistory immutable) {
        return CodedAttributeHistoryBoUtil.from(EntityPhoneTypeHistoryBo.class, immutable);
    }

    public static EntityPhoneTypeHistoryBo from(CodedAttribute immutable,
            Timestamp fromDate,
            Timestamp toDate) {
        return CodedAttributeHistoryBoUtil.from(EntityPhoneTypeHistoryBo.class, immutable, fromDate, toDate);
    }
}
