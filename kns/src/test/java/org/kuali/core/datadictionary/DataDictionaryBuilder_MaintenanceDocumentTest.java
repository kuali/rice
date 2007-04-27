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
import org.junit.Before;
import org.junit.Test;
import org.kuali.core.datadictionary.exception.ClassValidationException;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class is used to test the DataDictionaryBuilder Maintenance Document
 * object.
 * 
 * 
 */
@WithTestSpringContext
public class DataDictionaryBuilder_MaintenanceDocumentTest extends KualiTestBase {

	DataDictionaryBuilder builder = null;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		builder = new DataDictionaryBuilder(KNSServiceLocator.getValidationCompletionUtils());

		// quieten things down a bit
		setLogLevel("org.apache.commons.digester", Level.FATAL);
		setLogLevel("org.kuali.bo.datadictionary.XmlErrorHandler", Level.FATAL);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_blankBusinessObjectClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/BlankBusinessObjectClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			failedAsExpected = true;
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_blankFieldName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/BlankFieldName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			failedAsExpected = true;
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_blankMaintainableClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/BlankMaintainableClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			failedAsExpected = true;
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_blankSectionTitle() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/BlankSectionTitle.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			failedAsExpected = true;
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_duplicateEntries() {
		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries("org/kuali/core/bo/datadictionary/AttributeReferenceDummy.xml", true);
			builder.addUniqueEntries("org/kuali/core/bo/datadictionary/AttributeReferenceDummy.xml", true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof DuplicateEntryException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_duplicateFieldName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/DuplicateFieldName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof DuplicateEntryException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_duplicateSectionTitle() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/DuplicateSectionTitle.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof DuplicateEntryException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_invalid() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/InvalidMaintenanceDocument.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof IllegalArgumentException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_invalidBusinessObjectClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/InvalidBusinessObjectClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ClassValidationException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_invalidMaintainableClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/InvalidMaintainableClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ClassValidationException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_unknownBusinessObjectClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/UnknownBusinessObjectClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_unknownMaintainableClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/UnknownMaintainableClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_valid() {
		builder.addUniqueEntries("org/kuali/core/bo/datadictionary/", true);
		builder.addUniqueEntries("org/kuali/core/document/datadictionary/", true);
		builder.completeInitialization();
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_MCBlankCollectionName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/MCBlankCollectionName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_MCBlankBusinessObjectClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/MCBlankBusinessObjectClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_MCBlankFieldName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/MCBlankFieldName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_MCDuplicateCollectionName() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/MCDuplicateCollectionName.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_MCInvalidBusinessObjectClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/MCInvalidBusinessObjectClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_MCMissingFields() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/MCMissingFields.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_MCUnknownBusinessObjectClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/MCUnknownBusinessObjectClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_MCUnknownField() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/MCUnknownField.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_MCInvalidField() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/MCInvalidField.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_businessRulesClass_blank() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/BRBlank.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_businessRulesClass_invalid() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/BRInvalid.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ClassValidationException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_authorization_blankAction() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/AuthBlankAction.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof IllegalArgumentException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_authorization_blankWorkgroup() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/AuthBlankWorkgroup.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof IllegalArgumentException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_authorization_emptyAuthorization() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/AuthEmptyAuthorization.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCaused(e)) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_authorization_emptyAuthorizationsSection() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/AuthEmptyAuthorizationsSection.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCaused(e)) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_authorization_emptyWorkgroupsList() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/AuthEmptyWorkgroupsList.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCaused(e)) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_authorization_missingAction() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/AuthMissingAction.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCaused(e)) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_authorization_missingAuthorizationsSection() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/AuthMissingAuthorizationsSection.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCaused(e)) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_documentAuthorizer_missingAuthorizerClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/DocAuthMissing.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCaused(e)) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_documentAuthorizer_blankAuthorizerClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/DocAuthBlank.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_documentAuthorizer_unknownAuthorizerClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/DocAuthUnknown.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_maintenanceDocument_documentAuthorizer_invalidAuthorizerClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "md/DocAuthInvalid.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.completeInitialization();
		} catch (DataDictionaryException e) {
			if (e instanceof ClassValidationException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}
}
