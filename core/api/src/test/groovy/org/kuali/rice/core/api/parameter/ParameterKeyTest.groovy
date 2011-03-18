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

package org.kuali.rice.core.api.parameter

import javax.xml.bind.JAXBContext
import org.junit.Assert
import org.junit.Test

class ParameterKeyTest {
        private static final String XML = """
        <parameterKey xmlns="http://rice.kuali.org/core/v1_1">
            <applicationCode>AC</applicationCode>
            <namespaceCode>NC</namespaceCode>
            <componentCode>CC</componentCode>
            <name>N</name>
        </parameterKey>
    """

    private static final String APPLICATION_CODE = "AC"
    private static final String NAMESPACE_CODE = "NC"
    private static final String COMPONENT_CODE = "CC"
    private static final String NAME = "N"

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_all_null() {
        ParameterKey.create(null, null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_first_null() {
        ParameterKey.create(null, NAMESPACE_CODE, COMPONENT_CODE, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_first_empty() {
        ParameterKey.create("", NAMESPACE_CODE, COMPONENT_CODE, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_first_whitespace() {
        ParameterKey.create(" ", NAMESPACE_CODE, COMPONENT_CODE, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_second_null() {
        ParameterKey.create(APPLICATION_CODE, null, COMPONENT_CODE, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_second_empty() {
        ParameterKey.create(APPLICATION_CODE, "", COMPONENT_CODE, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_second_whitespace() {
        ParameterKey.create(APPLICATION_CODE, " ", COMPONENT_CODE, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_third_null() {
        ParameterKey.create(APPLICATION_CODE, NAMESPACE_CODE, null, NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_third_empty() {
        ParameterKey.create(APPLICATION_CODE, NAMESPACE_CODE, "", NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_third_whitespace() {
        ParameterKey.create(APPLICATION_CODE, NAMESPACE_CODE, " ", NAME);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_fourth_null() {
        ParameterKey.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, null);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_fourth_empty() {
        ParameterKey.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, "");
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Key_fail_fourth_whitespace() {
        ParameterKey.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, " ");
    }

    @Test
    void happy_path() {
        ParameterKey.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, NAME);
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
	  def jc = JAXBContext.newInstance(ParameterKey.class)
	  def marshaller = jc.createMarshaller()
	  def sw = new StringWriter()

	  def param = this.create()
	  marshaller.marshal(param,sw)

	  def unmarshaller = jc.createUnmarshaller();
	  def actual = unmarshaller.unmarshal(new StringReader(sw.toString()))
	  def expected = unmarshaller.unmarshal(new StringReader(XML))

	  Assert.assertEquals(expected,actual)
	}

    private create() {
		return ParameterKey.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, NAME);
	}
}
