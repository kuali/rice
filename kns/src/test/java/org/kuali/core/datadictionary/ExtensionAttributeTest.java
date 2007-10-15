/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.core.datadictionary;

import java.util.ArrayList;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.core.UserSession;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.BusinessObjectRelationship;
import org.kuali.core.bo.PersistableBusinessObjectExtension;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.rule.event.RouteDocumentEvent;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.ui.Field;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.test.KNSTestBase;

import edu.sampleu.travel.bo.TravelAccount;
import edu.sampleu.travel.bo.TravelAccountExtension;
import edu.sampleu.travel.bo.TravelAccountType;

public class ExtensionAttributeTest extends KNSTestBase {

    DataDictionaryBuilder builder = null;

    @Before
    public void setUp() throws Exception {
	super.setUp();

	builder = new DataDictionaryBuilder(KNSServiceLocator.getValidationCompletionUtils());
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/AdHocRoutePerson.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/AdHocRouteWorkgroup.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/Attachment.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/AttributeReferenceDummy.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/BusinessObjectAttributeEntry.xml", true);
	// builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/BusinessRule.xml", true);
	// builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/BusinessRuleSecurity.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/Campus.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/CampusType.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/DocumentGroup.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/DocumentHeader.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/DocumentStatus.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/DocumentType.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/EmployeeStatus.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/EmployeeType.xml", true);
	// builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/FinancialSystemParameter.xml", true);
	// builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/FinancialSystemParameterSecurity.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/Note.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/NoteType.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/UniversalUser.xml", true);
	// builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/BusinessRuleMaintenanceDocument.xml",
        // true);
	// builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/BusinessRuleSecurityMaintenanceDocument.xml",
        // true);
	builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/CampusMaintenanceDocument.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/CampusTypeMaintenanceDocument.xml", true);
	builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/DocumentTypeMaintenanceDocument.xml",
		true);
	builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/EmployeeStatusMaintenanceDocument.xml",
		true);
	builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/EmployeeTypeMaintenanceDocument.xml",
		true);
	// builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/FinancialSystemParameterSecurityMaintenanceDocument.xml",
        // true);
	// builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/FinancialSystemParameterMaintenanceDocument.xml",
        // true);
	builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/UniversalUserMaintenanceDocument.xml",
		true);
	builder.addUniqueEntries("classpath:edu/sampleu/travel/datadictionary/TravelAccount.xml", true);
	builder.addUniqueEntries("classpath:edu/sampleu/travel/datadictionary/TravelAccountType.xml", true);
	builder.addUniqueEntries("classpath:edu/sampleu/travel/datadictionary/TravelAccountMaintenanceDocument.xml", true);
	builder.addUniqueEntries("classpath:edu/sampleu/travel/datadictionary/TravelAccountExtension.xml", true);
	builder.addUniqueEntries("classpath:edu/sampleu/travel/datadictionary/FiscalOfficer.xml", true);
	builder.addUniqueEntries("classpath:edu/sampleu/travel/datadictionary/FiscalOfficerMaintenanceDocument.xml", true);

	// quieten things down a bit
	setLogLevel("org.apache.commons.digester", Level.ERROR);
	setLogLevel("org.kuali.core.datadictionary.XmlErrorHandler", Level.ERROR);
    }

    @After
    public void tearDown() throws Exception {
	super.tearDown();
	builder = null;
    }

    @Test
    public void testExtensionAttributeType() throws Exception {
	BusinessObjectEntry boe = builder.getDataDictionary().getBusinessObjectEntry("TravelAccount");
	assertNotNull("BusinessObjectEntry for TravelAccount should not be null", boe);
	AttributeDefinition extAttrib = boe.getAttributeDefinition("extension.accountTypeCode");
	assertNotNull("AttributeDefinition for 'extension.accountType' should not be null", extAttrib);
	extAttrib = boe.getAttributeDefinition("extension.accountType.codeAndDescription");
	assertNotNull("AttributeDefinition for 'extension.accountType.codeAndDescription' should not be null", extAttrib);
    }

    @Test
    public void testObjectUtils_getPropertyType() throws Exception {
	TravelAccount ta = new TravelAccount();
	assertEquals("physical property type mismatch", PersistableBusinessObjectExtension.class, PropertyUtils
		.getPropertyType(ta, "extension"));
	assertEquals("DD property type mismatch", TravelAccountExtension.class, ObjectUtils.getPropertyType(ta, "extension",
		KNSServiceLocator.getPersistenceStructureService()));
	assertEquals("extension.accountType attribute class mismatch", TravelAccountType.class, ObjectUtils.getPropertyType(
		ta, "extension.accountType", KNSServiceLocator.getPersistenceStructureService()));
	assertEquals("extension.accountType.codeAndDescription attribute class mismatch", String.class, ObjectUtils
		.getPropertyType(ta, "extension.accountType.codeAndDescription", KNSServiceLocator
			.getPersistenceStructureService()));
    }

    @Test
    public void testBOMetaDataService() throws Exception {
	TravelAccount ta = new TravelAccount();
	BusinessObjectRelationship br = KNSServiceLocator.getBusinessObjectMetaDataService().getBusinessObjectRelationship(
		ta, "extension.accountType");
	assertEquals("mismatch on parent class", TravelAccount.class, br.getParentClass());
	assertEquals("mismatch on related class", TravelAccountType.class, br.getRelatedClass());
	System.out.println(br.getParentToChildReferences());
	assertEquals("parent/child key not correct - should be extension.accountTypeCode/accountTypeCode",
		"accountTypeCode", br.getParentToChildReferences().get("extension.accountTypeCode"));
	br = KNSServiceLocator.getBusinessObjectMetaDataService().getBusinessObjectRelationship(ta, "extension");
	assertNull("extension is not lookupable, should have returned null", br);
    }

    @Test
    public void testQuickFinder() throws Exception {
	TravelAccount ta = new TravelAccount();
	ArrayList<String> lookupFieldAttributeList = new ArrayList<String>();
	lookupFieldAttributeList.add("extension.accountTypeCode");

	Field field = FieldUtils.getPropertyField(ta.getClass(), "extension.accountTypeCode", true);

	field = LookupUtils.setFieldQuickfinder((BusinessObject) ta, "extension.accountTypeCode", field,
		lookupFieldAttributeList);

	assertEquals("lookup class not correct", TravelAccountType.class.getName(), field.getQuickFinderClassNameImpl());
	assertEquals("field lookup params not correct", "extension.accountTypeCode:accountTypeCode", field
		.getLookupParameters());
	assertEquals("lookup field conversions not correct", "accountTypeCode:extension.accountTypeCode", field
		.getFieldConversions());
    }

    @Test
    public void testExistenceChecks() throws Exception {
	TravelAccount ta = new TravelAccount();
	((TravelAccountExtension) ta.getExtension()).setAccountTypeCode("XYZ"); // invalid account type
	ta.setName("Test Name");
	ta.setNumber("1234567");
	GlobalVariables.setUserSession(new UserSession("quickstart"));
	MaintenanceDocument document = (MaintenanceDocument) KNSServiceLocator.getDocumentService().getNewDocument(
		"TravelAccountMaintenanceDocument");
	assertNotNull("new document must not be null", document);
	document.getDocumentHeader().setFinancialDocumentDescription(getClass().getSimpleName() + "test");
	document.getOldMaintainableObject().setBusinessObject(null);
	document.getOldMaintainableObject().setBoClass(ta.getClass());
	document.getNewMaintainableObject().setBusinessObject(ta);
	document.getNewMaintainableObject().setBoClass(ta.getClass());

	boolean failedAsExpected = false;
	try {
	    document.validateBusinessRules(new RouteDocumentEvent(document));
	} catch (ValidationException expected) {
	    failedAsExpected = true;
	}
	assertTrue("validation should have failed", failedAsExpected);
	System.out.println("document errors: " + GlobalVariables.getErrorMap());
	assertTrue("there should be errors", GlobalVariables.getErrorMap().getErrorCount() > 0);
	assertTrue("should be an error on the account type code", GlobalVariables.getErrorMap().containsKey(
		"document.newMaintainableObject.extension.accountTypeCode"));
	assertTrue("account type code should have an existence error", GlobalVariables.getErrorMap().fieldHasMessage(
		"document.newMaintainableObject.extension.accountTypeCode", "error.existence"));
    }
}
