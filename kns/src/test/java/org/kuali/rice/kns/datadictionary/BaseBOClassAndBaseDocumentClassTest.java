/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.document.IdentityManagementKimDocument;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.bo.AdHocRouteWorkgroup;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.DocumentBase;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.document.TransactionalDocumentBase;
import org.kuali.rice.kns.impls.RiceTestTransactionalDocument2;
import org.kuali.rice.kns.impls.RiceTestTransactionalDocument2Parent;
import org.kuali.rice.kns.test.document.AccountRequestDocument;
import org.kuali.rice.kns.test.document.bo.AccountType2;
import org.kuali.rice.kns.test.document.bo.AccountType2Parent;
import org.kuali.test.KNSTestCase;

/**
 * This is a test case for the data dictionary's ability to index entries by a "base" superclass. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BaseBOClassAndBaseDocumentClassTest extends KNSTestCase {

	DataDictionary dd = null;

	/**
	 * Performs setup tasks similar to those in ExtensionAttributeTest.
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();

		dd = new DataDictionary();
		dd.addConfigFileLocation("file:" + getBaseDir() + "/../impl/src/main/resources/org/kuali/rice/kns/bo/datadictionary");
		dd.addConfigFileLocation("file:" + getBaseDir() + "/../impl/src/main/resources/org/kuali/rice/kns/document/datadictionary");
		dd.addConfigFileLocation("file:" + getBaseDir() + "/../impl/src/main/resources/org/kuali/rice/kim/bo/datadictionary/EmploymentStatus.xml");
		dd.addConfigFileLocation("file:" + getBaseDir() + "/../impl/src/main/resources/org/kuali/rice/kim/bo/datadictionary/EmploymentType.xml");
		dd.addConfigFileLocation("file:" + getBaseDir() + "/../impl/src/main/resources/org/kuali/rice/kim/bo/datadictionary/PersonImpl.xml");
		dd.addConfigFileLocation("file:" + getBaseDir() + "/../impl/src/main/resources/org/kuali/rice/kim/bo/datadictionary/KimBaseBeans.xml");
		dd.addConfigFileLocation("file:" + getBaseDir() + "/../impl/src/main/resources/org/kuali/rice/kim/bo/datadictionary/GroupImpl.xml");
		dd.addConfigFileLocation("file:" + getBaseDir() + "/../impl/src/main/resources/org/kuali/rice/kim/bo/datadictionary/RoleImpl.xml");
		dd.addConfigFileLocation("file:" + getBaseDir() + "/../impl/src/main/resources/org/kuali/rice/kim/bo/datadictionary/KimTypeImpl.xml");
		dd.addConfigFileLocation("classpath:org/kuali/rice/kns/test/document");
        dd.parseDataDictionaryConfigurationFiles( false );
	}

	/**
	 * Performs tearDown tasks similar to those in ExtensionAttributeTest.
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		dd = null;
	}
	
	/**
	 * This method tests to make sure that business object entries and document entries can only define regular and "base" classes that are compatible.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testValidAndInvalidDDEntries() throws Exception {
		// Ensure that we cannot specify a base class that is not the superclass of the currently-defined document/businessObject class.
		assertExpectedOutcomeOfBOEntryConstruction(AdHocRoutePerson.class, DocumentBase.class, true, false);
		assertExpectedOutcomeOfDocEntryConstruction(TransactionalDocumentBase.class, MaintenanceDocumentBase.class, true, false);
		
		// Ensure that we cannot specify a document/businessObject class that is not the subclass of the currently-defined base class.
		assertExpectedOutcomeOfBOEntryConstruction(TransactionalDocumentBase.class, AdHocRouteRecipient.class, false, false);
		assertExpectedOutcomeOfDocEntryConstruction(AccountRequestDocument.class, IdentityManagementKimDocument.class, false, false);
		
		// Ensure that we can specify a valid base class after the document/businessObject class has been set.
		assertExpectedOutcomeOfBOEntryConstruction(AdHocRoutePerson.class, AdHocRouteRecipient.class, true, true);
		assertExpectedOutcomeOfDocEntryConstruction(TransactionalDocumentBase.class, DocumentBase.class, true, true);
		
		// Ensure that we can specify a valid document/businessObject class after the base class has been set.
		assertExpectedOutcomeOfBOEntryConstruction(AdHocRouteWorkgroup.class, AdHocRouteRecipient.class, false, true);
		assertExpectedOutcomeOfDocEntryConstruction(IdentityManagementPersonDocument.class, IdentityManagementKimDocument.class, false, true);
		
		// Ensure that we cannot specify a document/businessObject class that is a superclass of the base class.
		assertExpectedOutcomeOfBOEntryConstruction(AdHocRouteRecipient.class, AdHocRoutePerson.class, true, false);
		assertExpectedOutcomeOfDocEntryConstruction(IdentityManagementKimDocument.class, IdentityManagementGroupDocument.class, true, false);
		
		// Ensure that we can specify the same class for both the document/businessObject class and the base class
		// (as permitted by the use of Class.isAssignableFrom in the BO and doc entry validations).
		assertExpectedOutcomeOfBOEntryConstruction(AdHocRoutePerson.class, AdHocRoutePerson.class, true, true);
		assertExpectedOutcomeOfDocEntryConstruction(MaintenanceDocumentBase.class, MaintenanceDocumentBase.class, true, true);
	}
	
	/**
	 * This method tests the DataDictionary's ability to grab entries based on a "base" class.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRetrieveDDEntriesByBaseClass() throws Exception {
		// Test the ability to retrieve a BusinessObjectEntry by "base" class, using both the full name and the simple name.
		String[] baseClassNames = {"AccountType2Parent", "org.kuali.rice.kns.test.document.bo.AccountType2Parent"};
		String[] actualClassNames = {"AccountType2", "org.kuali.rice.kns.test.document.bo.AccountType2"};
		for (int i = 0; i < baseClassNames.length; i++) {
			// Attempt to retrieve a BusinessObjectEntry that is indexed under AccountType2Parent, the "base" class of AccountType2.
			assertBusinessObjectEntryIsAccountType2(dd.getBusinessObjectEntry(baseClassNames[i]));
			// Now check to ensure that the same BusinessObjectEntry can still be retrieved by specifying the actual BO class name.
			assertBusinessObjectEntryIsAccountType2(dd.getBusinessObjectEntry(actualClassNames[i]));
		}
		
		// Test the ability to retrieve a DocumentEntry by "base" class, using both the full name and the simple name.
		baseClassNames = new String[] {"RiceTestTransactionalDocument2Parent", "org.kuali.rice.kns.impls.RiceTestTransactionalDocument2Parent"};
		actualClassNames = new String[] {"RiceTestTransactionalDocument2", "org.kuali.rice.kns.impls.RiceTestTransactionalDocument2"};
		for (int i = 0; i < baseClassNames.length; i++) {
			// Attempt to retrieve a DocumentEntry indexed under RiceTestTransactionalDocument2Parent, the "base" class of RiceTestTransactionalDocument2.
			assertDocumentEntryIsRiceTestTransactionalDocument2(dd.getDocumentEntry(baseClassNames[i]));
			// Now check to ensure that the same DocumentEntry can still be retrieved by specifying the actual document class name.
			assertDocumentEntryIsRiceTestTransactionalDocument2(dd.getDocumentEntry(actualClassNames[i]));
		}
		
		// Test the ability to retrieve a BusinessObjectEntry by JSTL key, where the "base" class's simple name is the JSTL key if it is defined. However,
		// the getDictionaryObjectEntry could still find the object even if the JSTL key map doesn't have it, since it checks the other maps for the
		// entry if it cannot be found in the entriesByJstlKey map.
		DataDictionaryEntry ddEntry = dd.getDictionaryObjectEntry("AccountType2Parent");
		assertNotNull("The AccountType2 DD entry from the entriesByJstlKey map should not be null", ddEntry);
		assertTrue("The DD entry should have been a BusinessObjectEntry", ddEntry instanceof BusinessObjectEntry);
		assertBusinessObjectEntryIsAccountType2((BusinessObjectEntry) ddEntry);
	}
	
	/**
	 * A convenience method for testing the construction of a BusinessObjectEntry that has the given BO class and "base" class.
	 * 
	 * @param boClass The businessObjectClass of the entry.
	 * @param boBaseClass The "base" class of the entry.
	 * @param boClassFirst Indicates if either the BO class or the "base" class should be set first.
	 * @param shouldSucceed Indicates whether the construction task should succeed or fail.
	 * @throws Exception
	 */
	private void assertExpectedOutcomeOfBOEntryConstruction(Class<? extends BusinessObject> boClass,
			Class<? extends BusinessObject> boBaseClass, boolean boClassFirst, boolean shouldSucceed) throws Exception {
		// Construct the entry and set one of the BO class properties.
		BusinessObjectEntry boEntry = new BusinessObjectEntry();
		if (boClassFirst) { boEntry.setBusinessObjectClass(boClass); } else { boEntry.setBaseBusinessObjectClass(boBaseClass); }
		// Now set the other BO class property.
		try {
			if (!boClassFirst) { boEntry.setBusinessObjectClass(boClass); } else { boEntry.setBaseBusinessObjectClass(boBaseClass); }
			// If the above operation succeeds, check whether or not the operation was meant to succeed.
			if (!shouldSucceed) {
				fail("The BO entry should have thrown an IllegalArgumentException when setting the " + ((!boClassFirst) ? "subclass" : "superclass"));
			}
		}
		catch (IllegalArgumentException e) {
			// If the above operation fails, check whether or not the operation was meant to fail.
			if (shouldSucceed) {
				fail("The BO entry should not have thrown an IllegalArgumentException when setting the " + ((!boClassFirst) ? "subclass" : "superclass"));
			}
		}
	}
	
	/**
	 * A convenience method for testing the construction of a DocumentEntry that has the given doc class and "base" class.
	 * 
	 * @param docClass The documentClass of the entry.
	 * @param docBaseClass The "base" class of the entry.
	 * @param docClassFirst Indicates if either the doc class or the "base" class should be set first.
	 * @param shouldSucceed Indicates whether the construction task should succeed or fail.
	 * @throws Exception
	 */
	private void assertExpectedOutcomeOfDocEntryConstruction(Class<? extends Document> docClass,
			Class<? extends Document> docBaseClass, boolean docClassFirst, boolean shouldSucceed) throws Exception {
		// Construct the entry and set one of the doc class properties.
		DocumentEntry docEntry = new TransactionalDocumentEntry();
		if (docClassFirst) { docEntry.setDocumentClass(docClass); } else { docEntry.setBaseDocumentClass(docBaseClass); }
		// Now set the other doc class property.
		try {
			if (!docClassFirst) { docEntry.setDocumentClass(docClass); } else { docEntry.setBaseDocumentClass(docBaseClass); }
			// If the above operation succeeds, check whether or not the operation was meant to succeed.
			if (!shouldSucceed) {
				fail("The doc entry should have thrown an IllegalArgumentException when setting the " + ((!docClassFirst) ? "subclass" : "superclass"));
			}
		}
		catch (IllegalArgumentException e) {
			// If the above operation fails, check whether or not the operation was meant to fail.
			if (shouldSucceed) {
				fail("The doc entry should not have thrown an IllegalArgumentException when setting the " + ((!docClassFirst) ? "subclass" : "superclass"));
			}
		}
	}
	
	/**
	 * A convenience method for checking if a BusinessObjectEntry represents the AccountType2 DD entry.
	 * 
	 * @param boEntry The BusinessObjectEntry to test.
	 * @throws Exception
	 */
	private void assertBusinessObjectEntryIsAccountType2(BusinessObjectEntry boEntry) throws Exception {
		assertNotNull("The AccountType2 DD entry should not be null", boEntry);
		assertEquals("The DD entry does not represent the AccountType2 entry", AccountType2.class, boEntry.getBusinessObjectClass());
		assertEquals("The DD entry does not have the expected base class", AccountType2Parent.class, boEntry.getBaseBusinessObjectClass());
		assertEquals("The DD entry does not have the expected title attribute", "accountTypeCode2", boEntry.getTitleAttribute());
		assertEquals("The DD entry does not have the expected object label", "Account Type 2", boEntry.getObjectLabel());
	}
	
	/**
	 * A convenience method for checking if a DocumentEntry represents the RiceTestTransactionalDocument2 DD entry.
	 * 
	 * @param docEntry The DocumentEntry to test.
	 * @throws Exception
	 */
	private void assertDocumentEntryIsRiceTestTransactionalDocument2(DocumentEntry docEntry) throws Exception {
		assertNotNull("The RiceTestTransactionalDocument2 DD entry should not be null", docEntry);
		assertEquals("The DD entry does not represent the RiceTestTransactionalDocument2 entry",
				RiceTestTransactionalDocument2.class, docEntry.getDocumentClass());
		assertEquals("The DD entry does not have the expected base class", RiceTestTransactionalDocument2Parent.class, docEntry.getBaseDocumentClass());
	}
}
