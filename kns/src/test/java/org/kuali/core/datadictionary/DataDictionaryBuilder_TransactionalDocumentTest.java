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
import org.junit.Test;
import org.kuali.core.datadictionary.exception.ClassValidationException;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.test.KNSTestBase;
import org.kuali.test.KNSWithTestSpringContext;
import org.xml.sax.SAXParseException;

/**
 * This class is used to test the DataDictionaryBuilder Transactional Document
 * object.
 */
@KNSWithTestSpringContext
public class DataDictionaryBuilder_TransactionalDocumentTest extends KNSTestBase {

	DataDictionaryBuilder builder = null;

	static final public String IB_DD_XML = "classpath:org/kuali/module/financial/datadictionary/docs/KualiInternalBillingDocument.xml";

	@Override
	public void setUp() throws Exception {
		super.setUp();
		builder = new DataDictionaryBuilder(KNSServiceLocator.getValidationCompletionUtils());
		builder.addUniqueEntries(DataDictionaryBuilderTest.PACKAGE_CORE_BO, true);
		builder.addUniqueEntries(DataDictionaryBuilderTest.PACKAGE_CORE_DOCUMENT, true);
		setLogLevel("org.apache.commons.digester", Level.FATAL);
		setLogLevel("org.kuali.core.datadictionary.XmlErrorHandler", Level.FATAL);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_blankDocumentClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/BlankDocumentClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseDocument("BlankDocumentClass", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_invalid() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/InvalidTransactionalDocument.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseDocument("InvalidTransactionalDocument", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof IllegalStateException) {
				failedAsExpected = true;
			} else {
				throw e;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_invalidDocumentClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/InvalidDocumentClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseDocument("InvalidDocumentClass", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ClassValidationException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_unknownDocumentClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/UnknownDocumentClass.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseDocument("UnknownDocumentClass", true);
		} catch (DataDictionaryException e) {
//			if (DataDictionaryUtils.saxCause(e) instanceof ClassValidationException) {
				failedAsExpected = true;
//			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_businessRulesClass_blank() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/BRBlank.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseDocument("BRBlank", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_businessRulesClass_invalid() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/BRInvalid.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseDocument("BRInvalid", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ClassValidationException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

//	@Test
//	public final void testDataDictionaryBuilder_transactionalDocument_authorization_blankAction() {
//		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/AuthBlankAction.xml";
//
//		boolean failedAsExpected = false;
//
//		try {
//			builder.addUniqueEntries(INPUT_FILE, true);
//			builder.parseDocument("AuthBlankAction", true);
//		} catch (DataDictionaryException e) {
//			if (DataDictionaryUtils.saxCause(e) instanceof ClassValidationException) {
//				failedAsExpected = true;
//			}
//		}
//
//		assertTrue(failedAsExpected);
//	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_authorization_blankWorkgroup() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/AuthBlankWorkgroup.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseDocument("AuthBlankWorkgroup", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof IllegalArgumentException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_authorization_emptyAuthorization() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/AuthEmptyAuthorization.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseDocument("AuthEmptyAuthorization", true);
		} catch (DataDictionaryException e) {
			if (e.getCause() instanceof SAXParseException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_authorization_emptyAuthorizationsSection() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/AuthEmptyAuthorizationsSection.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseDocument("AuthEmptyAuthorizationsSection", true);
		} catch (DataDictionaryException e) {
			if (e.getCause() instanceof SAXParseException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_authorization_emptyWorkgroupsList() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/AuthEmptyWorkgroupsList.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseDocument("AuthEmptyWorkgroupsList", true);
		} catch (DataDictionaryException e) {
			if (e.getCause() instanceof SAXParseException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_authorization_missingAction() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/AuthMissingAction.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseDocument("AuthMissingAction", true);
		} catch (DataDictionaryException e) {
			if (e.getCause() instanceof SAXParseException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_authorization_missingAuthorizationsSection() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/AuthMissingAuthorizationsSection.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseDocument("AuthMissingAuthorizationsSection", true);
		} catch (DataDictionaryException e) {
//			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
//			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_documentAuthorizer_missingAuthorizerClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/DocAuthMissing.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseDocument("DocAuthMissing", true);
		} catch (DataDictionaryException e) {
//			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
//			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_documentAuthorizer_blankAuthorizerClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/DocAuthBlank.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseDocument("DocAuthBlank", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ConversionException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_documentAuthorizer_unknownAuthorizerClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/DocAuthUnknown.xml";

		boolean failedAsExpected = false;

		try {
			builder.addUniqueEntries(INPUT_FILE, true);
			builder.parseDocument("DocAuthUnknown", true);
		} catch (DataDictionaryException e) {
//			if (DataDictionaryUtils.saxCause(e) instanceof ClassValidationException) {
				failedAsExpected = true;
//			}
		}

		assertTrue(failedAsExpected);
	}

	@Test
	public final void testDataDictionaryBuilder_transactionalDocument_documentAuthorizer_invalidAuthorizerClass() {
		String INPUT_FILE = DataDictionaryBuilderTest.TESTPACKAGE_INVALID + "td/DocAuthInvalid.xml";

		boolean failedAsExpected = false;

		try {
			builder.getDataDictionary().setAllowOverrides(true);
			builder.addOverrideEntries(INPUT_FILE, true);
			builder.parseDocument("DocAuthInvalid", true);
		} catch (DataDictionaryException e) {
			if (DataDictionaryUtils.saxCause(e) instanceof ClassValidationException) {
				failedAsExpected = true;
			}
		}

		assertTrue(failedAsExpected);
	}
}
