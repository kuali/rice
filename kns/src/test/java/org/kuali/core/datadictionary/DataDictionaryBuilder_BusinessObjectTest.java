/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.core.datadictionary;

import org.apache.commons.beanutils.ConversionException;
import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class is used to test the DataDictionaryBuilder Business Object.
 * 
 * 
 */
@WithTestSpringContext
public class DataDictionaryBuilder_BusinessObjectTest extends KualiTestBase {

	DataDictionaryBuilder builder = null;

	Level oldDigesterLogLevel;

	Level oldXmlLogLevel;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// String INPUT_FILE = "classpath:org/kuali/core/bo/datadictionary/" +
		// "AttributeReferenceDummy.xml";
		builder = new DataDictionaryBuilder(KNSServiceLocator.getValidationCompletionUtils());
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/AdHocRoutePerson.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/AdHocRouteWorkgroup.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/ApplicationConstant.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/Attachment.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/AttributeReferenceDummy.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/BusinessObjectAttributeEntry.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/Campus.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/CampusType.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/DocumentGroup.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/DocumentHeader.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/DocumentStatus.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/DocumentType.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/EmployeeStatus.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/EmployeeType.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/FinancialSystemParameter.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/FinancialSystemParameterSecurity.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/Note.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/NoteType.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/bo/datadictionary/UniversalUser.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/BusinessRuleMaintenanceDocument.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/BusinessRuleSecurityMaintenanceDocument.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/CampusMaintenanceDocument.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/CampusTypeMaintenanceDocument.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/DocumentTypeMaintenanceDocument.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/EmployeeStatusMaintenanceDocument.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/EmployeeTypeMaintenanceDocument.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/FinancialSystemParameterSecurityMaintenanceDocument.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/FinancialSystemParameterMaintenanceDocument.xml", true);
		builder.addUniqueEntries("classpath:org/kuali/core/document/datadictionary/KualiUniversityUserMaintenanceDocument.xml", true);
		
		// quieten things down a bit
		setLogLevel("org.apache.commons.digester", Level.FATAL);
		setLogLevel("org.kuali.core.datadictionary.XmlErrorHandler", Level.FATAL);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		builder = null;
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attribute_blankFormatterClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/BlankFormatterClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("BlankFormatterClass", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attribute_blankValidatingRegex() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/BlankValidatingRegex.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("BlankValidatingRegex", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attribute_invalidFormatterClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/InvalidFormatterClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("InvalidFormatterClass", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attribute_invalidValidatingRegex() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/InvalidValidatingRegex.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("InvalidValidatingRegex", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attribute_unknownFormatterClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/UnknownFormatterClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("UnknownFormatterClass", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attributeReference_blankAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/ARBlankAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("ARBlankAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attributeReference_blankSourceAttribute() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/ARBlankSourceAttribute.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("ARBlankSourceAttribute", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attributeReference_blankSourceClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/ARBlankSourceClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("ARBlankSourceClass", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attributeReference_duplicateAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/ARDuplicateAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("ARDuplicateAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attributeReference_invalidAttributeReference() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/ARInvalidAttributeReference.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("ARInvalidAttributeReference", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attributeReference_missingSourceAttribute() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/ARMissingSourceAttribute.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("ARMissingSourceAttribute", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attributeReference_missingSourceClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/ARMissingSourceClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("ARMissingSourceClass", true);
		} catch (DataDictionaryException e) {
			failedAsExpected = true;
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attributeReference_unknownAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/ARUnknownAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("ARUnknownAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attributeReference_unknownSourceAttribute() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/ARUnknownSourceAttribute.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("ARUnknownSourceAttribute", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_attributeReference_unknownSourceClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/ARUnknownSourceClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("ARUnknownSourceClass", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_blankAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/BlankAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("BlankAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_blankBusinessObjectClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/BlankBusinessObjectClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("BlankBusinessObjectClass", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_blankCollectionName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/BlankCollectionName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("BlankCollectionName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_defaultSort_blankAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/DefaultSortBlankAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("DefaultSortBlankAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_defaultSort_unknownAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/DefaultSortUnknownAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("DefaultSortUnknownAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_duplicateAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/DuplicateAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("DuplicateAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_inquiryField_blankAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/IFBlankAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("IFBlankAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_inquiryField_duplicateAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/IFDuplicateAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("IFDuplicateAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_inquiryField_unknownAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/IFUnknownAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("IFUnknownAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_invalid() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/InvalidBusinessObject.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("InvalidBusinessObject", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}
		
		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_lookupField_blankAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/LFBlankAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("LFBlankAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_lookupField_duplicateAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/LFDuplicateAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("LFDuplicateAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_lookupField_unknownAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/LFUnknownAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("LFUnknownAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_returnKey_blankAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/RKBlankAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("RKBlankAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_returnKey_duplicateAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/RKDuplicateAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("RKDuplicateAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_returnKey_unknownAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/RKUnknownAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("RKUnknownAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_unknownAttributeName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/UnknownAttributeName.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("UnknownAttributeName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_unknownBusinessObjectClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/UnknownBusinessObjectClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseBO("UnknownBusinessObjectClass", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_businessObject_unknownCollectionName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "bo/UnknownCollectionName.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseBO("UnknownCollectionName", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}
}