package org.kuali.rice.kns.dbplatform;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.rice.kns.dao.jdbc.KualiDBPlatformBase;

/**
 * @deprecated
 * @see org.kuali.rice.core.database.platform.DerbyPlatform
 */
public class KualiDBPlatformDerby extends KualiDBPlatformBase implements KualiDBPlatform {
	public Long getNextAvailableSequenceNumber(String sequenceName) {
		return null;
	}

	public void applyLimit(Integer limit, Criteria criteria) {
		// derby has no such concept
	}

	//not copied -- no references
	public String getCurTimeFunction() {
		return "CURRENT_TIMESTAMP";
	}

	//not copied -- no references
	/**
	 * @see org.kuali.rice.core.database.platform.ANSISqlPlatform#getDateSQL(String, String)
	 */
	public String getDateFormatString(String dateFormatString) {
		return "'" + dateFormatString + "'";
	}

	/**
	 * @see org.kuali.rice.core.database.platform.ANSISqlPlatform#getUpperCaseFunction()
	 */
	public String getUpperCaseFunction() {
		return "UPPER";
	}

	public String getStrToDateFunction() {
		return null;
	}
}
