/*
 * Copyright 2006-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.jpa.criteria;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria.QueryByCriteriaType;
import org.kuali.rice.core.util.type.KualiDecimal;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.test.document.AccountWithDDAttributesDocument;
import org.kuali.rice.kns.test.document.bo.Account;
import org.kuali.rice.kns.test.document.bo.AccountManager;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.test.KNSTestCase;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This class tests the various features of Rice's JPA Criteria API.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JpaCriteriaTest extends KNSTestCase {
	
	private EntityManager em;

	private static final String ACCOUNT_WITH_DD_ATTRIBUTES_DOCUMENT_NAME = "AccountWithDDAttributes";
	
	/* This is based on the AccountWithDDAttributesDocument creation strategy from DataDictionarySearchableAttributeTest. */
    enum DOCUMENT_FIXTURE {
    	TEST_DOC01("Testing TEST_DOC01", new Integer(1234567890), "John Doe", new KualiDecimal(501.77), createDate(2009, Calendar.OCTOBER, 15), createTimestamp(2009, Calendar.NOVEMBER, 1, 0, 0, 0), "SecondState", true),
    	TEST_DOC02("Testing TEST_DOC02", new Integer(0), "Jane Doe", new KualiDecimal(-100), createDate(2009, Calendar.OCTOBER, 16), createTimestamp(2015, Calendar.NOVEMBER, 2, 0, 0, 0), "FirstState", true),
    	TEST_DOC03("Testing TEST_DOC03", new Integer(987654321), "John D'oh", new KualiDecimal(0.0), createDate(2006, Calendar.OCTOBER, 17), createTimestamp(1900, Calendar.NOVEMBER, 3, 0, 0, 0), "FourthState", false),
    	TEST_DOC04("Testing TEST_DOC04", new Integer(88), "John_Doe", new KualiDecimal(10000051.0), createDate(2009, Calendar.OCTOBER, 18), createTimestamp(2009, Calendar.NOVEMBER, 4, 0, 0, 0), "FourthState", true),
    	TEST_DOC05("Testing TEST_DOC05", new Integer(9000), "Shane Kloe", new KualiDecimal(4.54), createDate(2012, Calendar.OCTOBER, 19), createTimestamp(2007, Calendar.NOVEMBER, 5, 12, 4, 38), "ThirdState", false),
    	TEST_DOC06("Testing TEST_DOC06", new Integer(1234567889), "Anonymous", new KualiDecimal(501), createDate(2009, Calendar.APRIL, 20), createTimestamp(2009, Calendar.NOVEMBER, 6, 12, 59, 59), "ThirdState", true),
    	TEST_DOC07("Testing TEST_DOC07", new Integer(1), "Some%one's_Name%", new KualiDecimal(771.05), createDate(2054, Calendar.OCTOBER, 22), createTimestamp(2008, Calendar.NOVEMBER, 8, 12, 0, 0), "FirstState", true);
    	
    	private String accountDocumentDescription;
    	private Integer accountNumber;
    	private String accountOwner;
    	private KualiDecimal accountBalance;
    	private Date accountOpenDate;
    	private Timestamp accountUpdateDateTime;
    	private String accountState;
    	private boolean accountAwake;
    	
    	private DOCUMENT_FIXTURE(String accountDocumentDescription, Integer accountNumber, String accountOwner, KualiDecimal accountBalance, Date accountOpenDate, Timestamp accountUpdateDateTime, String accountState, boolean accountAwake) {
    		this.accountDocumentDescription = accountDocumentDescription;
    		this.accountNumber = accountNumber;
    		this.accountOwner = accountOwner;
    		this.accountBalance = accountBalance;
    		this.accountOpenDate = accountOpenDate;
    		this.accountUpdateDateTime = accountUpdateDateTime;
    		this.accountState = accountState;
    		this.accountAwake = accountAwake;
    	}
    	
    	public AccountWithDDAttributesDocument getDocument(DocumentService docService) throws WorkflowException {
    		AccountWithDDAttributesDocument acctDoc = (AccountWithDDAttributesDocument) docService.getNewDocument(ACCOUNT_WITH_DD_ATTRIBUTES_DOCUMENT_NAME);
    		acctDoc.getDocumentHeader().setDocumentDescription(this.accountDocumentDescription);
    		acctDoc.setAccountNumber(this.accountNumber);
    		acctDoc.setAccountOwner(this.accountOwner);
    		acctDoc.setAccountBalance(this.accountBalance);
    		acctDoc.setAccountOpenDate(this.accountOpenDate);
    		acctDoc.setAccountUpdateDateTime(this.accountUpdateDateTime);
    		acctDoc.setAccountState(this.accountState);
    		acctDoc.setAccountAwake(this.accountAwake);
    		
    		return acctDoc;
    	}
    }
	
    enum ACCT_MGR_FIXTURE {
    	ACCOUNT_MANAGER01("fo-101"), ACCOUNT_MANAGER02("fo-102"), ACCOUNT_MANAGER03("fo-103"),
    	ACCOUNT_MANAGER04("fo-104"), ACCOUNT_MANAGER05("fo-105");
    	
    	private String userName;
    	
    	private ACCT_MGR_FIXTURE(String userName) {
    		this.userName = userName;
    	}
    	
    	public AccountManager getAccountManager() {
    		AccountManager accountManager = new AccountManager();
    		accountManager.setUserName(userName);
    		return accountManager;
    	}
    }
    
    enum ACCT_FIXTURE {
    	ACCOUNT01("b101", "acct-101", "fo-101"), ACCOUNT02("b102", "acct-102", "fo-101"), ACCOUNT03("b103", "account-103", "fo-101"),
    	ACCOUNT04("b104", "account-104", "fo-102"), ACCOUNT05("b105", "account-105", "fo-102"), ACCOUNT06("b106", "acct-106", "fo-104"),
    	ACCOUNT07("b107", "acct-107", "fo-104"), ACCOUNT08("b108", "account-108", "fo-105"), ACCOUNT09("b109", "account-109", "fo-105"),
    	ACCOUNT10("b110", "acct-110", "fo-105"), ACCOUNT11("b111", "acct-111", null);
    	
    	private String number;
    	private String name;
    	private String amUserName;
    	
    	private ACCT_FIXTURE(String number, String name, String amUserName) {
    		this.number = number;
    		this.name = name;
    		this.amUserName = amUserName;
    	}
    	
    	public Account getAccount(Long amId) {
    		Account account = new Account();
    		account.setNumber(this.number);
    		account.setName(this.name);
    		account.setAmId(amId);
    		return account;
    	}
    }
    
    /**
     * (Based on a similar method from DataDictionarySearchableAttributeTest)
     * Creates a date quickly
     * 
     * @param year the year of the date
     * @param month the month of the date
     * @param day the day of the date
     * @return a new java.sql.Date initialized to the precise date given
     */
    private static Date createDate(int year, int month, int day) {
    	Calendar date = Calendar.getInstance();
    	date.clear();
		date.set(year, month, day, 0, 0, 0);
		return new java.sql.Date(date.getTimeInMillis());
    }
    
    /**
     * (Based on a similar method from DataDictionarySearchableAttributeTest)
     * Utility method to create a timestamp quickly
     * 
     * @param year the year of the timestamp
     * @param month the month of the timestamp
     * @param day the day of the timestamp
     * @param hour the hour of the timestamp
     * @param minute the minute of the timestamp
     * @param second the second of the timestamp
     * @return a new java.sql.Timestamp initialized to the precise time given
     */
    private static Timestamp createTimestamp(int year, int month, int day, int hour, int minute, int second) {
    	Calendar date = Calendar.getInstance();
    	date.clear();
    	date.set(year, month, day, hour, minute, second);
    	return new java.sql.Timestamp(date.getTimeInMillis());
    }
    
	@Override
	public void setUp() throws Exception {
		super.setUp();
		GlobalVariables.setUserSession(new UserSession("quickstart"));
		em = KNSServiceLocator.getApplicationEntityManagerFactory().createEntityManager();
	}
	
	@Override
	public void tearDown() throws Exception {
		if (em != null) {
			try { em.close(); } catch (Exception e) {}
		}
		super.tearDown();
	}
    
	private void makeNewEntityManager() {
		try { em.close(); } catch (Exception e) {}
		em = KNSServiceLocator.getApplicationEntityManagerFactory().createEntityManager();
	}
	
	/*
	 * Construct some AccountWithDDAttributes documents to test with.
	 */
	private void constructAccountWithDDAttributesDocs() throws Exception {
		DocumentService docService = KNSServiceLocatorWeb.getDocumentService();
		for (DOCUMENT_FIXTURE docFixture : DOCUMENT_FIXTURE.values()) {
			em.persist(docFixture.getDocument(docService));
		}
	}
	
	/*
	 * Deletes any existing accounts and account managers, and replaces them with the ones for this test.
	 */
	private void constructAccountsAndAccountManagers() throws Exception {
		Map<String,Long> newIds = new HashMap<String,Long>();
		// Delete the existing accounts and account managers.
		Criteria crit = new Criteria(Account.class.getName());
		new QueryByCriteria(em, crit, QueryByCriteriaType.DELETE).toQuery().executeUpdate();
		crit = new Criteria(AccountManager.class.getName());
		new QueryByCriteria(em, crit, QueryByCriteriaType.DELETE).toQuery().executeUpdate();
		makeNewEntityManager();
		crit = new Criteria(Account.class.getName());
		assertEquals("There should not be any existing Account entities at this time.", 0, new QueryByCriteria(em, crit).toQuery().getResultList().size());
		crit = new Criteria(AccountManager.class.getName());
		assertEquals("There should not be any existing AccountManager entities at this time.", 0, new QueryByCriteria(em, crit).toQuery().getResultList().size());
		// Add the new accounts and account managers.
		for (ACCT_MGR_FIXTURE mgrFixture : ACCT_MGR_FIXTURE.values()) {
			AccountManager newManager = mgrFixture.getAccountManager();
			em.persist(newManager);
			newIds.put(newManager.getUserName(), newManager.getAmId());
		}
		for (ACCT_FIXTURE acctFixture : ACCT_FIXTURE.values()) {
			em.persist(acctFixture.getAccount(newIds.get(acctFixture.amUserName)));
		}
		em.flush();
		makeNewEntityManager();
	}
	
	/**
	 * Tests several non-subquery-related and non-association-related JPA Criteria features.
	 * 
	 * @throws Exception
	 */
	//JPA unit test.  ignoring for the time being for OJB revert
	@Ignore
	@Test
	public void testSimpleQueries() throws Exception {
		String[] strArray = null;
		Object[][] objArray = null;
		Set<Integer> acctNumSet = new HashSet<Integer>();
		List<AccountWithDDAttributesDocument> acctDocs = null;
		List<Object[]> objectList = null;
		Criteria crit = null;
		Criteria crit2 = null;
		constructAccountWithDDAttributesDocs();
		
		// Test that a blank search will retrieve all the records.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, DOCUMENT_FIXTURE.values(), acctDocs);
		
		// Test String equality.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.eq("accountOwner", "Jane Doe");
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC02 }, acctDocs);
		
		// Test integer-plus-String equality along with a single-Criteria-instance AND operator.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.eq("accountNumber", Integer.valueOf(9000));
		crit.eq("accountOwner", "Shane Kloe");
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC05 }, acctDocs);

		// Test number equality along with the OR operator.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit2 = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.eq("accountBalance", new BigDecimal("771.05"));
		crit2.eq("accountBalance", new BigDecimal(0.0));
		crit.or(crit2);
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC03, DOCUMENT_FIXTURE.TEST_DOC07 }, acctDocs);

		// Test date equality and datetime equality along with the AND operator.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit2 = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.eq("accountOpenDate", createDate(2009, Calendar.OCTOBER, 15));
		crit2.eq("accountUpdateDateTime", createTimestamp(2009, Calendar.NOVEMBER, 1, 0, 0, 0));
		crit.and(crit2);
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC01 }, acctDocs);
		
		// Test boolean equality.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.eq("accountAwake", false);
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC03, DOCUMENT_FIXTURE.TEST_DOC05 }, acctDocs);
		
		// Test "NOT (...)" expressions.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit2 = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit2.gt("accountNumber", Integer.valueOf(1));
		crit2.ne("accountOwner", "Anonymous");
		crit.not(crit2);
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC02, DOCUMENT_FIXTURE.TEST_DOC06, DOCUMENT_FIXTURE.TEST_DOC07 }, acctDocs);
		
		// Test "OR NOT (...)" expressions.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit2 = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit2.lte("accountBalance", new BigDecimal("771.05"));
		crit2.ne("accountOwner", "John Doe");
		crit.eq("accountOpenDate", createDate(2012, Calendar.OCTOBER, 19));
		crit.orNot(crit2);
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC01, DOCUMENT_FIXTURE.TEST_DOC04, DOCUMENT_FIXTURE.TEST_DOC05 }, acctDocs);
		
		// Test number inequality.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.ne("accountBalance", new BigDecimal("501.77"));
		crit.ne("accountBalance", new BigDecimal(0.0));
		crit.ne("accountBalance", new BigDecimal("4.54"));
		crit.ne("accountBalance", new BigDecimal("771.05"));
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC02, DOCUMENT_FIXTURE.TEST_DOC04, DOCUMENT_FIXTURE.TEST_DOC06 }, acctDocs);
		
		// Test >= and < on integers.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.gte("accountNumber", Integer.valueOf(987654321));
		crit.lt("accountNumber", Integer.valueOf(1234567890));
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC03, DOCUMENT_FIXTURE.TEST_DOC06 }, acctDocs);
		
		// Test > and <= on integers.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.gt("accountNumber", Integer.valueOf(0));
		crit.lte("accountNumber", Integer.valueOf(9000));
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC04, DOCUMENT_FIXTURE.TEST_DOC05, DOCUMENT_FIXTURE.TEST_DOC07 }, acctDocs);
		
		// Test BETWEEN on dates.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.between("accountOpenDate", createDate(2009, Calendar.OCTOBER, 16), createDate(2012, Calendar.OCTOBER, 19));
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC02, DOCUMENT_FIXTURE.TEST_DOC04, DOCUMENT_FIXTURE.TEST_DOC05 }, acctDocs);
		
		// Test BETWEEN on timestamps.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.between("accountUpdateDateTime", createTimestamp(2009, Calendar.NOVEMBER, 6, 12, 59, 59), createTimestamp(2015, Calendar.NOVEMBER, 2, 0, 0, 0));
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC02, DOCUMENT_FIXTURE.TEST_DOC06 }, acctDocs);
		
		// Test NOT BETWEEN on integers.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.notBetween("accountNumber", Integer.valueOf(1), Integer.valueOf(1234567889));
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC01, DOCUMENT_FIXTURE.TEST_DOC02 }, acctDocs);
		
		// Test LIKE with Strings.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.like("accountOwner", "John_Doe");
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC01, DOCUMENT_FIXTURE.TEST_DOC04 }, acctDocs);
		
		// Test NOT LIKE with Strings.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.notLike("accountState", "Third%");
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet,
				new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC01, DOCUMENT_FIXTURE.TEST_DOC02, DOCUMENT_FIXTURE.TEST_DOC03, DOCUMENT_FIXTURE.TEST_DOC04, DOCUMENT_FIXTURE.TEST_DOC07 }, acctDocs);
		
		// Test NOT LIKE with Strings such that no results are returned.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.notLike("accountOwner", "%o%");
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[0], acctDocs);
		
		// Test LIKE...ESCAPE with Strings.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.likeEscape("accountOwner", "J%n\\_Doe", '\\');
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC04 }, acctDocs);
		
		// Test NOT LIKE...ESCAPE with Strings.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.notLikeEscape("accountOwner", "John%", '\\');
		crit.notLikeEscape("accountOwner", "Some^%one's^_Name^%", '^');
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC02, DOCUMENT_FIXTURE.TEST_DOC05, DOCUMENT_FIXTURE.TEST_DOC06 }, acctDocs);
		
		// Test IN (...) with numbers.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.in("accountBalance", Arrays.asList(new BigDecimal[] { new BigDecimal("501.77"), new BigDecimal(10000051.0), new BigDecimal("771.05") } ));
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC01, DOCUMENT_FIXTURE.TEST_DOC04, DOCUMENT_FIXTURE.TEST_DOC07 }, acctDocs);
		
		// Test IN (...) with Strings.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.in("accountOwner", Arrays.asList(new String[] {"Jane Doe", "John_Doe", "Shane Kloe", "Some%one's_Name%", "Unused Name"} ));
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC02, DOCUMENT_FIXTURE.TEST_DOC04, DOCUMENT_FIXTURE.TEST_DOC05, DOCUMENT_FIXTURE.TEST_DOC07 }, acctDocs);
		
		// Test NOT IN (...) with numbers.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.notIn("accountBalance", Arrays.asList(new BigDecimal[] { new BigDecimal("501.77"), new BigDecimal(10000051.0), new BigDecimal("771.05") } ));
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC02, DOCUMENT_FIXTURE.TEST_DOC03, DOCUMENT_FIXTURE.TEST_DOC05, DOCUMENT_FIXTURE.TEST_DOC06 }, acctDocs);
		
		// Test NOT IN (...) with Strings.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.notIn("accountOwner", Arrays.asList(new String[] {"Jane Doe", "John_Doe", "Shane Kloe", "Some%one's_Name%", "Unused Name"} ));
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC01, DOCUMENT_FIXTURE.TEST_DOC03, DOCUMENT_FIXTURE.TEST_DOC06 }, acctDocs);
		
		// Test an ORDER BY in ascending order.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.in("accountOwner", Arrays.asList(new String[] {"Jane Doe", "Shane Kloe", "John_Doe", "Some%one's_Name%"} ));
		crit.orderBy("accountOwner", true);
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC02, DOCUMENT_FIXTURE.TEST_DOC04, DOCUMENT_FIXTURE.TEST_DOC05, DOCUMENT_FIXTURE.TEST_DOC07 }, acctDocs);
		strArray = new String[] {"Jane Doe", "John_Doe", "Shane Kloe", "Some%one's_Name%"};
		for (int i = 0; i < strArray.length; i++) {
			assertEquals("Found an AccountWithDDAttributesDocument that is out-of-place in the ascending-order-by-owner-name list.", strArray[i], acctDocs.get(i).getAccountOwner());
		}
		
		// Test an ORDER BY in descending order.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.in("accountOwner", Arrays.asList(new String[] {"Jane Doe", "Shane Kloe", "John_Doe", "Some%one's_Name%"} ));
		crit.orderBy("accountOwner", false);
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC02, DOCUMENT_FIXTURE.TEST_DOC04, DOCUMENT_FIXTURE.TEST_DOC05, DOCUMENT_FIXTURE.TEST_DOC07 }, acctDocs);
		strArray = new String[] {"Some%one's_Name%", "Shane Kloe", "John_Doe", "Jane Doe"};
		for (int i = 0; i < strArray.length; i++) {
			assertEquals("Found an AccountWithDDAttributesDocument that is out-of-place in the ascending-order-by-owner-name list.", strArray[i], acctDocs.get(i).getAccountOwner());
		}
		
		// Test a GROUP BY.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName(), false);
		crit.select("accountAwake");
		crit.select("COUNT(__JPA_ALIAS[[0]]__)");
		crit.like("accountOwner", "%o_");
		crit.groupBy("accountAwake");
		objectList = (List<Object[]>) new QueryByCriteria(em, crit).toQuery().getResultList();
		objArray = new Object[][] { {Boolean.TRUE, Long.valueOf(3)}, {Boolean.FALSE, Long.valueOf(2)} };
		assertEquals("Wrong number of aggregate groups returned.", objArray.length, objectList.size());
		for (int i = 0; i < objArray.length; i++) {
			Object tempNumber = objArray[i][0].equals(objectList.get(i)[0]) ? objectList.get(i)[1] : objectList.get((i+1)%2)[1];
			assertEquals("Wrong count(...) result found in group.", objArray[i][1], tempNumber);
		}
		
		// Test a GROUP BY containing a HAVING.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName(), false);
		crit2 = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.select("accountState");
		crit.select("SUM(__JPA_ALIAS[[0]]__.accountBalance)");
		crit.groupBy("accountState");
		crit2.ne("accountState", "FourthState");
		crit.having(crit2);
		crit.orderBy("accountState", true);
		objectList = (List<Object[]>) new QueryByCriteria(em, crit).toQuery().getResultList();
		objArray = new Object[][] { {"FirstState", Double.valueOf("671.05")}, {"SecondState", Double.valueOf("501.77")}, {"ThirdState", Double.valueOf("505.54")} };
		assertEquals("Wrong number of aggregate groups returned.", objArray.length, objectList.size());
		for (int i = 0; i < objArray.length; i++) {
			assertEquals("Wrong or out-of-order aggregate group found in results.", objArray[i][0], objectList.get(i)[0]);
			assertEquals("Wrong sum(...) result found in group.", objArray[i][1], objectList.get(i)[1]);
		}
		
		// Test the toCountQuery() ability of the JPA Criteria.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.like("accountOwner", "%Doe");
		Long resultNum = (Long) new QueryByCriteria(em, crit).toCountQuery().getSingleResult();
		assertEquals("Wrong count(*) value was returned by query.", Long.valueOf(3), resultNum);
		
		// Test an UPDATE query.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.set("accountBalance", new BigDecimal(372));
		crit.like("accountOwner", "%s%");
		int numUpdates = new QueryByCriteria(em, crit, QueryByCriteriaType.UPDATE).toQuery().executeUpdate();
		assertEquals("The wrong number of AccountWithDDAttributesDocument entities were updated.", 2, numUpdates);
		makeNewEntityManager();
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.like("accountOwner", "%s%");
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC06, DOCUMENT_FIXTURE.TEST_DOC07, }, acctDocs);
		for (AccountWithDDAttributesDocument acctDoc : acctDocs) {
			assertEquals("The AccountWithDDAttributesDocument should have an updated account balance.", new KualiDecimal(372), acctDoc.getAccountBalance());
		}
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit2 = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.eq("accountBalance", new BigDecimal(501));
		crit2.eq("accountBalance", new BigDecimal("771.05"));
		crit.or(crit2);
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[0], acctDocs);
		
		// Test a DELETE query.
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.lt("accountBalance", new BigDecimal(372));
		numUpdates = new QueryByCriteria(em, crit, QueryByCriteriaType.DELETE).toQuery().executeUpdate();
		assertEquals("The wrong number of AccountWithDDAttributesDocument entities were deleted.", 3, numUpdates);
		makeNewEntityManager();
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[] { DOCUMENT_FIXTURE.TEST_DOC01, DOCUMENT_FIXTURE.TEST_DOC04, DOCUMENT_FIXTURE.TEST_DOC06, DOCUMENT_FIXTURE.TEST_DOC07 }, acctDocs);
		crit = new Criteria(AccountWithDDAttributesDocument.class.getName());
		crit.in("accountBalance", Arrays.asList(new BigDecimal[] { new BigDecimal(-100), new BigDecimal(0.0), new BigDecimal("4.54") }));
		acctDocs = (List<AccountWithDDAttributesDocument>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectDocumentsWereReturned(acctNumSet, new DOCUMENT_FIXTURE[0], acctDocs);
	}
	
	/**
	 * Tests several subquery-related and association-related JPA Criteria features.
	 * 
	 * @throws Exception
	 */
	//JPA unit test
	@Ignore
	@Test
	public void testSubQueriesAndAssociations() throws Exception {
		Set<String> strSet = new HashSet<String>();
		List<AccountManager> accountManagers = null;
		List<Account> accounts = null;
		List<Object[]> objectList = null;
		Criteria crit = null;
		Criteria crit2 = null;
		
		// Delete any existing accounts and account managers, and replace them with the test ones.
		constructAccountsAndAccountManagers();
		
		// Test a distinct INNER JOIN.
		crit = new Criteria(AccountManager.class.getName());
		crit.join("accounts", "acct", false, true);
		crit.distinct(true);
		accountManagers = (List<AccountManager>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountManagersWereReturned(strSet, new String[] {"fo-101", "fo-102", "fo-104", "fo-105"}, accountManagers);
		
		// Test the use of "IN (...)" in the FROM clause, which is basically identical to an INNER JOIN.
		crit = new Criteria(AccountManager.class.getName());
		crit.fromIn("accounts", "acct", false);
		crit.distinct(true);
		accountManagers = (List<AccountManager>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountManagersWereReturned(strSet, new String[] {"fo-101", "fo-102", "fo-104", "fo-105"}, accountManagers);
		
		// Test a non-distinct INNER JOIN, and grab the Account entities as well.
		crit = new Criteria(AccountManager.class.getName());
		crit.join("accounts", "acct", true, true);
		objectList = (List<Object[]>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountsAndAccountManagersWereReturned(new String[][] { {"fo-101", "b101"}, {"fo-101", "b102"}, {"fo-101", "b103"}, {"fo-102", "b104"},
				{"fo-102", "b105"}, {"fo-104", "b106"}, {"fo-104", "b107"}, {"fo-105", "b108"}, {"fo-105", "b109"}, {"fo-105", "b110"} }, objectList);
		
		// Test an INNER JOIN on a single-valued association.
		crit = new Criteria(Account.class.getName());
		crit.join("accountManager", "mgr", false, true);
		accounts = (List<Account>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountsWereReturned(strSet, new String[] {"b101", "b102", "b103", "b104", "b105", "b106", "b107", "b108", "b109", "b110"}, accounts);
		
		// Test a LEFT (OUTER) JOIN, and grab the Account entities as well.
		crit = new Criteria(AccountManager.class.getName());
		crit.join("accounts", "acct", true, false);
		crit.orderBy("userName", true);
		crit.orderBy("__JPA_ALIAS[['acct']]__.number", true);
		objectList = (List<Object[]>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountsAndAccountManagersWereReturned(new String[][] { {"fo-101", "b101"}, {"fo-101", "b102"}, {"fo-101", "b103"}, {"fo-102", "b104"},
				{"fo-102", "b105"}, {"fo-103", null}, {"fo-104", "b106"}, {"fo-104", "b107"}, {"fo-105", "b108"}, {"fo-105", "b109"}, {"fo-105", "b110"} }, objectList);
		
		// Test an INNER JOIN FETCH, which should auto-retrieve the "accounts" lists on the AccountManagers as well.
		crit = new Criteria(AccountManager.class.getName());
		crit.joinFetch("accounts", true);
		accountManagers = (List<AccountManager>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountManagersWereReturned(strSet,
				new String[] {"fo-101", "fo-101", "fo-101", "fo-102", "fo-102", "fo-104", "fo-104", "fo-105", "fo-105", "fo-105"}, accountManagers);
		
		// Test an INNER JOIN with a condition.
		crit = new Criteria(AccountManager.class.getName());
		crit.join("accounts", "acct", true, true);
		crit.like("__JPA_ALIAS[['acct']]__.name", "account%");
		objectList = (List<Object[]>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountsAndAccountManagersWereReturned(new String[][] { {"fo-101", "b103"}, {"fo-102", "b104"}, {"fo-102", "b105"},
				{"fo-105", "b108"}, {"fo-105", "b109"} }, objectList);
		
		// Test a LEFT (OUTER) JOIN FETCH with a condition.
		crit = new Criteria(AccountManager.class.getName());
		crit.joinFetch("accounts", false);
		crit.in("userName", "fo-102", "fo-103", "fo-105");
		accountManagers = (List<AccountManager>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountManagersWereReturned(strSet,
				new String[] {"fo-102", "fo-102", "fo-103", "fo-105", "fo-105", "fo-105"}, accountManagers);
		
		// Test a LEFT (OUTER) JOIN on a single-valued association, along with a WHERE-clause condition.
		crit = new Criteria(Account.class.getName(), false);
		crit.join("accountManager", "mgr", true, false);
		crit.select("__JPA_ALIAS[[0]]__");
		crit.like("name", "%11%");
		objectList = (List<Object[]>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountsAndAccountManagersWereReturned(new String[][] { {"fo-105", "b110"}, {null, "b111"} }, objectList);
		
		// Test a MEMBER OF condition using another entity as the parameter.
		crit = new Criteria(AccountManager.class.getName());
		crit.memberOf(em.find(Account.class, "b107"), "accounts");
		accountManagers = (List<AccountManager>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountManagersWereReturned(strSet, new String[] {"fo-104"}, accountManagers);
		
		// Test a NOT MEMBER OF condition using another entity as the parameter.
		crit = new Criteria(AccountManager.class.getName());
		crit.notMemberOf(em.find(Account.class, "b110"), "accounts");
		accountManagers = (List<AccountManager>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountManagersWereReturned(strSet, new String[] {"fo-101", "fo-102", "fo-103", "fo-104"}, accountManagers);
		
		// Test an EXISTS condition along with a MEMBER OF condition that uses an expression for the single-valued part.
		crit = new Criteria(AccountManager.class.getName());
		crit2 = new Criteria(Account.class.getName());
		crit2.like("name", "acct%");
		crit2.memberOf("__JPA_ALIAS[[0]]__", "__JPA_ALIAS[[-1]]__.accounts");
		crit.exists(crit2);
		accountManagers = (List<AccountManager>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountManagersWereReturned(strSet, new String[] {"fo-101", "fo-104", "fo-105"}, accountManagers);
		
		// Test a NOT EXISTS condition along with a NOT MEMBER OF condition that uses an expression for the single-valued part.
		crit = new Criteria(AccountManager.class.getName());
		crit2 = new Criteria(Account.class.getName());
		crit2.in("number", "b104", "b105");
		crit2.notMemberOf("__JPA_ALIAS[[0]]__", "__JPA_ALIAS[[-1]]__.accounts");
		crit.notExists(crit2);
		accountManagers = (List<AccountManager>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountManagersWereReturned(strSet, new String[] {"fo-102"}, accountManagers);
		
		// Test an IS EMPTY condition.
		crit = new Criteria(AccountManager.class.getName());
		crit.isEmpty("accounts");
		accountManagers = (List<AccountManager>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountManagersWereReturned(strSet, new String[] {"fo-103"}, accountManagers);
		
		// Test an IS NOT EMPTY condition.
		crit = new Criteria(AccountManager.class.getName());
		crit.in("userName", "fo-101", "fo-103", "fo-105");
		crit.notEmpty("accounts");
		accountManagers = (List<AccountManager>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountManagersWereReturned(strSet, new String[] {"fo-101", "fo-105"}, accountManagers);
		
		// Test an IS NULL condition.
		crit = new Criteria(Account.class.getName());
		crit.isNull("accountManager");
		accounts = (List<Account>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountsWereReturned(strSet, new String[] {"b111"}, accounts);
		
		// Test an IS NOT NULL condition.
		crit = new Criteria(Account.class.getName());
		crit.like("number", "_11_");
		crit.notNull("accountManager");
		accounts = (List<Account>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountsWereReturned(strSet, new String[] {"b110"}, accounts);
		
		// Test an "IN (...)" condition that makes use of a sub-query, along with a FROM-clause "IN (...)" and an "IN (...)" constructed from a list of values.
		crit = new Criteria(Account.class.getName());
		crit2 = new Criteria(AccountManager.class.getName(), false);
		crit2.fromIn("accounts", "acct", true);
		crit2.in("userName", "fo-102", "fo-103", "fo-104");
		crit.in("__JPA_ALIAS[[0]]__", crit2);
		accounts = (List<Account>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountsWereReturned(strSet, new String[] {"b104", "b105", "b106", "b107"}, accounts);
		
		// Test a "NOT IN (...)" condition that makes use of a sub-query, along with a "NOT IN (...)" constructed from a list of values.
		crit = new Criteria(AccountManager.class.getName());
		crit2 = new Criteria(Account.class.getName(), false);
		crit2.select("accountManager");
		crit2.notIn("name", "acct-106", "acct-107", "acct-111");
		crit.notIn("__JPA_ALIAS[[0]]__", crit2);
		accountManagers = (List<AccountManager>) new QueryByCriteria(em, crit).toQuery().getResultList();
		assertCorrectAccountManagersWereReturned(strSet, new String[] {"fo-103", "fo-104"}, accountManagers);
	}
	
	/*
	 * Verifies that all the expected AccountWithDDAttributesDocument objects were retrieved by the query.
	 */
	private void assertCorrectDocumentsWereReturned(Set<Integer> acctNumSet, DOCUMENT_FIXTURE[] expectedDocs,
			Collection<AccountWithDDAttributesDocument> actualDocs) throws Exception {
		assertEquals("The query returned the wrong number of AccountWithDDAttributesDocument entities.", expectedDocs.length, actualDocs.size());
		acctNumSet.clear();
		for (int i = 0; i < expectedDocs.length; i++) {
			acctNumSet.add(expectedDocs[i].accountNumber);
		}
		for (AccountWithDDAttributesDocument actualDoc : actualDocs) {
			if (!acctNumSet.contains(actualDoc.getAccountNumber())) {
				fail("The AccountWithDDAttributesDocument with account number " + actualDoc.getAccountNumber() +
						" should not have been returned by the query");
			}
		}
	}
	
	/*
	 * Verifies that the query retrieved the expected AccountManager-Account pairs.
	 */
	private void assertCorrectAccountsAndAccountManagersWereReturned(String[][] expectedObjects, List<Object[]> actualObjects) throws Exception {
		assertEquals("The query returned the wrong number of AccountManager-Account pairs.", expectedObjects.length, actualObjects.size());
		for (int i = 0; i < expectedObjects.length; i++) {
			assertEquals("The query returned an incorrect or out-of-order AccountManager entity.",
					expectedObjects[i][0], (expectedObjects[i][0] != null) ? ((AccountManager)actualObjects.get(i)[0]).getUserName() : actualObjects.get(i)[0]);
			assertEquals("The query returned an incorrect or out-of-order Account entity.",
					expectedObjects[i][1], (expectedObjects[i][1] != null) ? ((Account)actualObjects.get(i)[1]).getNumber() : actualObjects.get(i)[1]);
		}
	}
	
	/*
	 * Verifies that all the expected AccountManager objects were retrieved by the query.
	 */
	private void assertCorrectAccountManagersWereReturned(Set<String> managerNameSet, String[] expectedManagers,
			Collection<AccountManager> actualManagers) throws Exception {
		assertEquals("The query returned the wrong number of AccountManager entities.", expectedManagers.length, actualManagers.size());
		managerNameSet.clear();
		for (int i = 0; i < expectedManagers.length; i++) {
			managerNameSet.add(expectedManagers[i]);
		}
		for (AccountManager actualManager : actualManagers) {
			if (!managerNameSet.contains(actualManager.getUserName())) {
				fail("The AccountManager with ID " + actualManager.getAmId() + " and name " + actualManager.getUserName() + " should not have been retrieved by the query");
			}
		}
	}
	
	/*
	 * Verifies that all the expected Account objects were retrieved by the query.
	 */
	private void assertCorrectAccountsWereReturned(Set<String> accountNameSet, String[] expectedAccounts, Collection<Account> actualAccounts) throws Exception {
		assertEquals("The query returned the wrong number of Account entities.", expectedAccounts.length, actualAccounts.size());
		accountNameSet.clear();
		for (int i = 0; i < expectedAccounts.length; i++) {
			accountNameSet.add(expectedAccounts[i]);
		}
		for (Account actualAccount : actualAccounts) {
			if (!accountNameSet.contains(actualAccount.getNumber())) {
				fail("The Account with number " + actualAccount.getNumber() + " should not have been retrieved by the query");
			}
		}
	}
}