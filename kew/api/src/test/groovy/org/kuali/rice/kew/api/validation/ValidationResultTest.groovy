/*
 * Copyright 2011 The Kuali Foundation
 *
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
package org.kuali.rice.kew.api.validation

import org.kuali.rice.core.test.JAXBAssert
import org.junit.Assert
import org.junit.Test

/**
 * Unit test for ValidationResult object
 */
class ValidationResultTest {
    private static final String FIELD_NAME= "field_1"
 	private static final String ERROR_MESSAGE = "field_1 validation error"

    private static final String FIELD_AND_ERRORMESSAGE = """
        <validationResult xmlns="http://rice.kuali.org/kew/v2_0">
 	 	    <fieldName>${FIELD_NAME}</fieldName>
 	 	 	<errorMessage>${ERROR_MESSAGE}</errorMessage>
 	 	</validationResult>
 	 	"""

    private static final String FIELD_WITHOUT_ERRORMESSAGE = """
        <validationResult xmlns="http://rice.kuali.org/kew/v2_0">
 	 	    <fieldName>${FIELD_NAME}</fieldName>
 	 	</validationResult>
 	 	"""

    @Test(expected=IllegalArgumentException.class)
 	void test_Builder_create_fail_null_fieldname() {
 	    ValidationResult.Builder.create(null, "anything")
 	}

    @Test(expected=IllegalArgumentException.class)
 	void test_Builder_create_fail_null_source() {
 	    ValidationResult.Builder.create(null)
 	}

    @Test
    void test_Builder_create_success_null_errormessage() {
 	  ValidationResult validationResult= ValidationResult.Builder.create(FIELD_NAME, null).build()
 	  Assert.assertEquals(null, validationResult.getErrorMessage())
 	  Assert.assertEquals(FIELD_NAME, validationResult.getFieldName())
 	}

    @Test
    void test_Builder_create_success() {
 	  ValidationResult validationResult= ValidationResult.Builder.create(FIELD_NAME, ERROR_MESSAGE).build()
 	  Assert.assertEquals(ERROR_MESSAGE, validationResult.getErrorMessage())
 	  Assert.assertEquals(FIELD_NAME, validationResult.getFieldName())
 	}

    @Test
    void test_Builder_create_copy_success() {
      def source = ValidationResult.Builder.create(FIELD_NAME, ERROR_MESSAGE)
 	  ValidationResult validationResult= ValidationResult.Builder.create(source).build()
 	  Assert.assertEquals(ERROR_MESSAGE, validationResult.getErrorMessage())
 	  Assert.assertEquals(FIELD_NAME, validationResult.getFieldName())
 	}

    @Test
 	void test_Xml_Marshal_Unmarshal() {
 	  JAXBAssert.assertEqualXmlMarshalUnmarshal(ValidationResult.Builder.create(FIELD_NAME, ERROR_MESSAGE).build(), FIELD_AND_ERRORMESSAGE, ValidationResult.class)
 	}

    @Test
 	void test_Xml_Marshal_Unmarshal_without_errormessage() {
 	  JAXBAssert.assertEqualXmlMarshalUnmarshal(ValidationResult.Builder.create(FIELD_NAME, null).build(), FIELD_WITHOUT_ERRORMESSAGE, ValidationResult.class)
 	}
}