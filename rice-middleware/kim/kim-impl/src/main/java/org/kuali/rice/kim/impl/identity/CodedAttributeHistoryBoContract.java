package org.kuali.rice.kim.impl.identity;

import org.kuali.rice.kim.api.identity.CodedAttributeHistoryContract;

import java.sql.Timestamp;

public interface CodedAttributeHistoryBoContract extends CodedAttributeHistoryContract {
    void setCode(String code);
    void setName(String name);
    void setActive(boolean active);
    void setSortCode(String sortCode);
    void setHistoryId(Long historyId);
    void setActiveFromDateValue(Timestamp activeFromDateValue);
    void setActiveToDateValue(Timestamp activeToDateValue);
    void setVersionNumber(Long versionNumber);
    void setObjectId(String objectId);
}
