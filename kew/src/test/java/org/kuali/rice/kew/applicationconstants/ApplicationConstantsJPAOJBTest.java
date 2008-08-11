/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Created on Dec 14, 2005
package org.kuali.rice.kew.applicationconstants;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.kew.applicationconstants.ApplicationConstant;
import org.kuali.rice.kew.applicationconstants.dao.ApplicationConstantsDAO;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.KEWTransactionalTest;



/**
 * Tests DB persistence using JPA and OJB.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@KEWTransactionalTest
@Ignore("KULRICE-2090")
public class ApplicationConstantsJPAOJBTest extends KEWTestCase {
	
	private static String CONSTANT_NAME;
	private static String CONSTANT_VALUE;
	private static String CONSTANT_NAME2; 
	private static String CONSTANT_VALUE2;
	private static final String NEW_CONSTANT_NAME = "NewName";
	private static final String NEW_CONSTANT_VALUE = "NewValue";

	private ApplicationConstantsDAO jpaDao;
	private ApplicationConstantsDAO ojbDao;
	
	@Override
	protected void setUpTransactionInternal() throws Exception {
		this.jpaDao = (ApplicationConstantsDAO) KEWServiceLocator.getBean("enApplicationConstantsDAO");
		this.ojbDao = (ApplicationConstantsDAO) KEWServiceLocator.getBean("enApplicationConstantsOJBDAO");
		super.setUpTransactionInternal();
		// load application constants needed for below tests.
		int loopcnt = 0;
		Collection<ApplicationConstant> conList = jpaDao.findAll();
		assertTrue("Application Constant table requires at least two entries", (conList.size() > 1));
		for (ApplicationConstant c : conList) {
			loopcnt++;
			if (loopcnt == 1) {
				CONSTANT_NAME = c.getApplicationConstantName();
				CONSTANT_VALUE = c.getApplicationConstantValue();
				System.out.println("1st app constant name/value " + CONSTANT_NAME + " = " + CONSTANT_VALUE);
			}
			if (loopcnt == 2) {
				CONSTANT_NAME2 = c.getApplicationConstantName();
				CONSTANT_VALUE2 = c.getApplicationConstantValue();
				System.out.println("2nd app constant name/value " + CONSTANT_NAME2 + " = " + CONSTANT_VALUE2);
			}
			if (loopcnt > 2) { break; }; 
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindAll() {
		// ensure both results sets are same size
		assertEquals("List size not the same between ojb and jpa findAll ", jpaDao.findAll().size(), ojbDao.findAll().size());
		System.out.println("results set size for findAll = " + jpaDao.findAll().size());
		// ensure both results sets contain same contents
		assertTrue("A difference in results set exists", jpaDao.findAll().containsAll(ojbDao.findAll()));
		System.out.println("Test testFindAll completed.");
	}
	
	@Test
	public void testFindByName() {
		// ensure both results are identical
		ApplicationConstant apConJdo = ojbDao.findByName(CONSTANT_NAME);
		ApplicationConstant apConJpa = jpaDao.findByName(CONSTANT_NAME);
		assertTrue("Constant names are different between ojb + jpa findByName ", apConJdo.getApplicationConstantName().equals(apConJpa.getApplicationConstantName()));
		assertTrue("Constant values are different between ojb + jpa findByName ",apConJdo.getApplicationConstantValue().equals(apConJpa.getApplicationConstantValue()));
		assertTrue ("Constan locVerNbr are different between ojb + jpa findByName ",apConJdo.getLockVerNbr().equals(apConJpa.getLockVerNbr()));
				
		// ensure both can handle a not found condition by returning null
		assertNull("Expected null value not returned by a non-existing findByName", ojbDao.findByName(NEW_CONSTANT_NAME));
		assertNull("Expected null value not returned by a non-existing findByName", jpaDao.findByName(NEW_CONSTANT_NAME));
		System.out.println("Test testFindByName completed.");
	}
	
	// Test update and insert functionality.
	@Test
	public void testSaveConstantJpa() throws Exception {
		testSaveConstant(jpaDao);
		System.out.println("Test testSaveConstantJpa completed.");
	}
	
	@Test
	public void testSaveConstantOjb() throws Exception {
		testSaveConstant(ojbDao);
		System.out.println("Test testSaveConstantOjb completed.");
	}
	
	private void testSaveConstant(ApplicationConstantsDAO dao) {
		ApplicationConstant constant = dao.findByName(CONSTANT_NAME);
		assertEquals("Incorrect match on findByName in testSaveConstants ", CONSTANT_VALUE, constant.getApplicationConstantValue());
		
		// update the value
		constant.setApplicationConstantValue("abc");
		dao.saveConstant(constant);
		
		// validate that it was updated correctly.
		constant = dao.findByName(CONSTANT_NAME);
		assertEquals("Constant value was not properly updated.", "abc", constant.getApplicationConstantValue());
		
		// insert a new application constant
		// First validate it does not exist
		constant = dao.findByName(NEW_CONSTANT_NAME);
		if (constant != null) {
		    assertEquals("Constant to be inserted already exists.", NEW_CONSTANT_NAME, constant.getApplicationConstantName());
		}
		// insert a value
		constant = new ApplicationConstant();
		constant.setApplicationConstantName(NEW_CONSTANT_NAME);
		constant.setApplicationConstantValue(NEW_CONSTANT_VALUE);
		constant.setLockVerNbr(0);
		dao.saveConstant(constant);
		
		// validate that it was inserted
		constant = dao.findByName(NEW_CONSTANT_NAME);
		System.out.println("inserted constant name = " + constant.getApplicationConstantName() + ", value = " + constant.getApplicationConstantValue());
		assertEquals("Constant name was not found after an insert.", NEW_CONSTANT_NAME, constant.getApplicationConstantName());
		assertEquals("Constant value did not equal expected value after an insert.", NEW_CONSTANT_VALUE, constant.getApplicationConstantValue());
	}
	
	// Test delete functionality.
	@Test
	public void testDeleteConstantJpa() throws Exception {
		System.out.println("Test testDeleteConstantJpa starting.");
		// Attempt to delete a constant that does NOT exist. Differences between OJB and JPA exist. OJB throws an exception, JPA does not.
		// This difference has been noted in Confluence in KULRICE Global Technical Guides named Object-Relational Mapping Library Differences
		
		ApplicationConstant constant = new ApplicationConstant();
		constant.setApplicationConstantName(NEW_CONSTANT_NAME);
		jpaDao.deleteConstant(constant);
		System.out.println("No exception was thrown from an attempt to delete a NON-existing Application Constant using JPA.");
				
		// Delete an existing application constant after finding it first.
		ApplicationConstant constant2 = jpaDao.findByName(CONSTANT_NAME);
		jpaDao.deleteConstant(constant2);
		System.out.println("Applicaton Constant has been deleted. Name = " + CONSTANT_NAME);
		// JPA allows a deleted object that priorly came from the db, to be retrieved again - it's value is null, ( it must have already existed and been retrieved from db first.)
		assertNull("Constant value was not properly deleted.", jpaDao.findByName(CONSTANT_NAME));
		System.out.println("Completed a findByName, then delete, then findByName again in testDeleteConstantJPA successfully.");
		
		// Delete an existing application constant db record by creating a new object instance and loading the existing db record's primary key, then delete it, then reaccess it.
		ApplicationConstant constant3 = new ApplicationConstant();
		constant3.setApplicationConstantName(CONSTANT_NAME2);
		jpaDao.deleteConstant(constant3);
		System.out.println("Deleted an existing application constant db record by creating a new object instance and loading primary key. Name = " + CONSTANT_NAME2);
		// now attempt to access the deleted JPA record (remember, this one was never retrieved from db).
		//ApplicationConstant contstant4 = jpaDao.findByName(CONSTANT_NAME2); // Causes exception!
		//assertNull("Constant value was not properly deleted.", dao.findByName(CONSTANT_NAME2)); // Causes exception!
		// in JPA exception is: javax.persistence.EntityExistsException: org.hibernate.exception.ConstraintViolationException: Could not execute JDBC batch update
			
		System.out.println("Test testDeleteConstantJpa completed.");
	}
	
	@Test
	public void testDeleteConstantOjb() throws Exception {
		System.out.println("Test testDeleteConstantOjb starting.");
		// Attempt to delete a constant that does NOT exist causes an exception in OJB (not in JPA).
		// This difference has been noted in Confluence in KULRICE Global Technical Guides named Object-Relational Mapping Library Differences
		// therefore the test has been bypassed here.
//		ApplicationConstant constant = new ApplicationConstant();
//		constant.setApplicationConstantName(NEW_CONSTANT_NAME);
//		ojbDao.deleteConstant(constant); // Causes exception in JDO!

				
		// Delete an existing application constant after finding it first.
		ApplicationConstant constant2 = ojbDao.findByName(CONSTANT_NAME);
		ojbDao.deleteConstant(constant2);
		System.out.println("Applicaton Constant has been deleted. Name = " + CONSTANT_NAME);
		// OJB does not allow access after a delete until a commit occurs. JPA does when the object was retrieved from db first.
		//assertNull("Constant value was not properly deleted.", dao.findByName(CONSTANT_NAME));
	
		System.out.println("Test testDeleteConstantOjb completed.");
	}
}