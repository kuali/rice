/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.document.IdentityManagementKimDocument;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.AdHocRouteWorkgroup;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.exception.ClassValidationException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.DocumentBase;
import org.kuali.rice.krad.maintenance.MaintenanceDocumentBase;
import org.kuali.rice.krad.document.TransactionalDocumentBase;
import org.kuali.rice.krad.impls.RiceTestTransactionalDocument2;
import org.kuali.rice.krad.impls.RiceTestTransactionalDocument2Parent;
import org.kuali.rice.krad.test.document.AccountRequestDocument;
import org.kuali.rice.krad.test.document.bo.AccountType2;
import org.kuali.rice.krad.test.document.bo.AccountType2Parent;
import org.kuali.test.KRADTestCase;

import static org.junit.Assert.*;

/**
 * This is a test case for the data dictionary's ability to index entries by a "base" superclass. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BaseBOClassAndBaseDocumentClassTest extends KRADTestCase {

	DataDictionary dd = null;

	/**
	 * Performs setup tasks similar to those in ExtensionAttributeTest.
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();

		dd = new DataDictionary();

        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../impl/src/main/resources/org/kuali/rice/krad/uif/UifControlDefinitions.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../impl/src/main/resources/org/kuali/rice/krad/uif/UifFieldDefinitions.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../impl/src/main/resources/org/kuali/rice/krad/uif/UifGroupDefinitions.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../impl/src/main/resources/org/kuali/rice/krad/uif/UifHeaderFooterDefinitions.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../impl/src/main/resources/org/kuali/rice/krad/uif/UifLayoutManagerDefinitions.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../impl/src/main/resources/org/kuali/rice/krad/uif/UifViewPageDefinitions.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../impl/src/main/resources/org/kuali/rice/krad/uif/UifWidgetDefinitions.xml");
		dd.addConfigFileLocation("file:" + getBaseDir() + "/../../impl/src/main/resources/org/kuali/rice/krad/bo/datadictionary");
		dd.addConfigFileLocation("classpath:org/kuali/rice/kns/bo/datadictionary/DataDictionaryBaseTypes.xml");
		dd.addConfigFileLocation("classpath:org/kuali/rice/kim/bo/datadictionary/EmploymentStatus.xml");
		dd.addConfigFileLocation("classpath:org/kuali/rice/kim/bo/datadictionary/EmploymentType.xml");
		dd.addConfigFileLocation("classpath:org/kuali/rice/kim/impl/identity/PersonImpl.xml");
		dd.addConfigFileLocation("classpath:org/kuali/rice/kim/bo/datadictionary/KimBaseBeans.xml");
		dd.addConfigFileLocation("classpath:org/kuali/rice/kim/impl/group/Group.xml");
		dd.addConfigFileLocation("classpath:org/kuali/rice/kim/impl/role/RoleBo.xml");
		dd.addConfigFileLocation("classpath:org/kuali/rice/kim/impl/type/KimType.xml");
		dd.addConfigFileLocation("classpath:org/kuali/rice/krad/test/document/");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../location/web/src/main/resources/org/kuali/rice/location/web/campus/Campus.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../location/web/src/main/resources/org/kuali/rice/location/web/campus/CampusType.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../location/web/src/main/resources/org/kuali/rice/location/web/country/Country.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../location/web/src/main/resources/org/kuali/rice/location/web/state/State.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../location/web/src/main/resources/org/kuali/rice/location/web/county/County.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../location/web/src/main/resources/org/kuali/rice/location/web/postalcode/PostalCode.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../core/web/src/main/resources/org/kuali/rice/core/web/parameter/Parameter.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../core/web/src/main/resources/org/kuali/rice/core/web/parameter/ParameterType.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../core/web/src/main/resources/org/kuali/rice/core/web/namespace/Namespace.xml");
        dd.addConfigFileLocation("file:" + getBaseDir() + "/../../core/web/src/main/resources/org/kuali/rice/core/web/component/Component.xml");
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
		// Ensure that we cannot specify a base class that is not the superclass of the document/businessObject class.
		assertExpectedOutcomeOfBOEntryConstruction(AdHocRoutePerson.class, DocumentBase.class, false);
		assertExpectedOutcomeOfDocEntryConstruction(TransactionalDocumentBase.class, MaintenanceDocumentBase.class, false);
		assertExpectedOutcomeOfBOEntryConstruction(TransactionalDocumentBase.class, AdHocRouteRecipient.class, false);
		assertExpectedOutcomeOfDocEntryConstruction(AccountRequestDocument.class, IdentityManagementKimDocument.class, false);
		
		// Ensure that we can specify a base class that is the superclass of the document/businessObject class.
		assertExpectedOutcomeOfBOEntryConstruction(AdHocRoutePerson.class, AdHocRouteRecipient.class, true);
		assertExpectedOutcomeOfDocEntryConstruction(TransactionalDocumentBase.class, DocumentBase.class, true);
		assertExpectedOutcomeOfBOEntryConstruction(AdHocRouteWorkgroup.class, AdHocRouteRecipient.class, true);
		assertExpectedOutcomeOfDocEntryConstruction(IdentityManagementPersonDocument.class, IdentityManagementKimDocument.class, true);
		
		// Ensure that we cannot specify a document/businessObject class that is a superclass of the base class.
		assertExpectedOutcomeOfBOEntryConstruction(AdHocRouteRecipient.class, AdHocRoutePerson.class, false);
		assertExpectedOutcomeOfDocEntryConstruction(IdentityManagementKimDocument.class, IdentityManagementGroupDocument.class, false);
		
		// Ensure that we can specify the same class for both the document/businessObject class and the base class
		// (as permitted by the use of Class.isAssignableFrom in the BO and doc entry validations).
		assertExpectedOutcomeOfBOEntryConstruction(AdHocRoutePerson.class, AdHocRoutePerson.class, true);
		assertExpectedOutcomeOfDocEntryConstruction(MaintenanceDocumentBase.class, MaintenanceDocumentBase.class, true);
	}
	
	/**
	 * This method tests the DataDictionary's ability to grab entries based on a "base" class.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRetrieveDDEntriesByBaseClass() throws Exception {
		// Test the ability to retrieve a BusinessObjectEntry by "base" class, using both the full name and the simple name.
		String[] baseClassNames = {"AccountType2Parent", "AccountType2Parent"};
		String[] actualClassNames = {"AccountType2", "AccountType2"};
		for (int i = 0; i < baseClassNames.length; i++) {
			// Attempt to retrieve a BusinessObjectEntry that is indexed under AccountType2Parent, the "base" class of AccountType2.
			assertBusinessObjectEntryIsAccountType2(dd.getBusinessObjectEntry(baseClassNames[i]));
			// Now check to ensure that the same BusinessObjectEntry can still be retrieved by specifying the actual BO class name.
			assertBusinessObjectEntryIsAccountType2(dd.getBusinessObjectEntry(actualClassNames[i]));
		}
		
		// Test the ability to retrieve a DocumentEntry by "base" class, using both the full name and the simple name.
		baseClassNames = new String[] {"org.kuali.rice.krad.impls.RiceTestTransactionalDocument2Parent", "org.kuali.rice.krad.impls.RiceTestTransactionalDocument2Parent"};
		actualClassNames = new String[] {"org.kuali.rice.krad.impls.RiceTestTransactionalDocument2", "org.kuali.rice.krad.impls.RiceTestTransactionalDocument2"};
		for (int i = 0; i < baseClassNames.length; i++) {
			// Attempt to retrieve a DocumentEntry indexed under RiceTestTransactionalDocument2Parent, the "base" class of RiceTestTransactionalDocument2.
			assertDocumentEntryIsRiceTestTransactionalDocument2(dd.getDocumentEntry(baseClassNames[i]));
			// Now check to ensure that the same DocumentEntry can still be retrieved by specifying the actual document class name.
			assertDocumentEntryIsRiceTestTransactionalDocument2(dd.getDocumentEntry(actualClassNames[i]));
		}
		
		// Test the ability to retrieve a BusinessObjectEntry by JSTL key, where the "base" class's simple name is the JSTL key if it is defined. However,
		// the getDictionaryObjectEntry could still find the object even if the JSTL key map doesn't have it, since it checks the other maps for the
		// entry if it cannot be found in the entriesByJstlKey map.
		DataDictionaryEntry ddEntry = dd.getDictionaryObjectEntry("AccountType2");
		assertNotNull("The AccountType2 DD entry from the entriesByJstlKey map should not be null", ddEntry);
		assertTrue("The DD entry should have been a BusinessObjectEntry", ddEntry instanceof BusinessObjectEntry);
		assertBusinessObjectEntryIsAccountType2((BusinessObjectEntry) ddEntry);
	}
	
	/**
	 * A convenience method for testing the construction of a BusinessObjectEntry that has the given BO class and "base" class.
	 * 
	 * @param boClass The businessObjectClass of the entry.
	 * @param boBaseClass The "base" class of the entry.
	 * @param shouldSucceed Indicates whether the construction task should succeed or fail.
	 * @throws Exception
	 */
	private void assertExpectedOutcomeOfBOEntryConstruction(Class<? extends BusinessObject> boClass,
			Class<? extends BusinessObject> boBaseClass, boolean shouldSucceed) throws Exception {
		// Construct the entry and set the necessary properties.
		BusinessObjectEntry boEntry = new BusinessObjectEntry();
		boEntry.setBusinessObjectClass(boClass);
		boEntry.setBaseBusinessObjectClass(boBaseClass);
		boEntry.setObjectLabel(boClass.getSimpleName());
		// Now attempt to validate these properties.
		try {
			boEntry.completeValidation();
			// If the above operation succeeds, check whether or not the operation was meant to succeed.
			if (!shouldSucceed) {
				fail("The BO entry should have thrown a ClassValidationException during the validation process");
			}
		}
		catch (ClassValidationException e) {
			// If the above operation fails, check whether or not the operation was meant to fail.
			if (shouldSucceed) {
				fail("The BO entry should not have thrown a ClassValidationException during the validation process");
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
			Class<? extends Document> docBaseClass, boolean shouldSucceed) throws Exception {
		// Construct the entry and set the necessary properties.
		DocumentEntry docEntry = new TransactionalDocumentEntry();
		docEntry.setDocumentClass(docClass);
		docEntry.setBaseDocumentClass(docBaseClass);
		// Now attempt to validate these properties.
		try {
			docEntry.completeValidation();
			// If the above operation succeeds, check whether or not the operation was meant to succeed.
			if (!shouldSucceed) {
				fail("The doc entry should have thrown a ClassValidationException during the validation process");
			}
		}
		catch (ClassValidationException e) {
			// If the above operation fails, check whether or not the operation was meant to fail.
			if (shouldSucceed) {
				fail("The doc entry should not have thrown a ClassValidationException during the validation process");
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
