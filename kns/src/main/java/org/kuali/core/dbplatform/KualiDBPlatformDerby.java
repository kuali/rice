package org.kuali.core.dbplatform;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.core.dao.jdbc.KualiDBPlatformBase;

public class KualiDBPlatformDerby extends KualiDBPlatformBase implements KualiDBPlatform {
    public Long getNextAvailableSequenceNumber(String sequenceName) {
	return null;
    }

    public void applyLimit(Integer limit, Criteria criteria) {
	// derby has no such concept
    }

    public String getCurTimeFunction() {
	return "CURRENT_TIMESTAMP";
    }

    public String getDateFormatString(String dateFormatString) {
	return "'" + dateFormatString + "'";
    }

    public String getUpperCaseFunction() {
	return "UPPER";
    }

    public String getStrToDateFunction() {
	return null;
    }
}
