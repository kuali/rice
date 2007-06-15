package org.kuali.core.dbplatform;

import java.util.ArrayList;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.core.dao.jdbc.AbstractDBPlatformDaoJdbc;

public class KualiDBPlatformDerby extends AbstractDBPlatformDaoJdbc {

	@Override
	protected Integer getFetchSize() {
		return null;
	}

	@Override
	public boolean isSequence(String sequenceName) {
		return sequenceName.toUpperCase().startsWith( "SEQ_" ) || sequenceName.toUpperCase().startsWith( "SEQUENCE_" ) || sequenceName.toUpperCase().endsWith( "_SEQ" ) || sequenceName.toUpperCase().endsWith( "_SEQUENCE" );
	}

	public void applyLimit(Integer limit, Criteria criteria) {
		//derby has no such concept
	}

	public String getCurTimeFunction() {
		return "CURRENT_TIMESTAMP";
	}

	public String getDateFormatString(String dateFormatString) {
		return "'" + dateFormatString + "'";
	}

	public String getStrToDateFunction() {
		return null;
	}

	public void createIndex(String ddl) {
		
	}

	public void createSequence(String ddl) {
		
	}

	public void createTable(String ddl) {
		
	}

	public void createView(String ddl) {
		
	}

	public void dropSequence(String sequenceName) {
		
	}

	public void dropTable(String tableName) {
		
	}

	public void dropView(String viewName) {
		
	}

	public void dumpSequence(String sequenceName, String exportDirectory) {
		
	}

	public String escapeSingleQuotes(String value) {
		return null;
	}

	public static long test_counter = 1;
	
	public Long getNextAvailableSequenceNumber(String sequenceName) {
		return test_counter++;
	}

	public List<String> getSequenceNames() {
		return new ArrayList<String>();
	}

	public void setDefaultDateFormatToYYYYMMDD() {
		
	}

	public void setSequenceStart(String sequenceName, Long value) {
		
	}

	
	
}
