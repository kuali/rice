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

package org.kuali.rice.core.api.namespace

import org.junit.Test
import org.kuali.rice.core.test.JAXBAssert

class NamespaceTest {
        private static final String XML = """
        <namespace xmlns="http://rice.kuali.org/core/v2_0">
            <code>PC</code>
            <applicationCode>AC</applicationCode>
            <name>N</name>
            <active>true</active>
            <versionNumber>1</versionNumber>
        </namespace>
    """

    private static final String CODE = "PC"
    private static final String APP_CODE = "AC"

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_all_null() {
        Namespace.Builder.create(null, null, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_null() {
        Namespace.Builder.create(null, APP_CODE, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_empty() {
        Namespace.Builder.create("", APP_CODE, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_whitespace() {
        Namespace.Builder.create("  ", APP_CODE, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_second_null() {
        Namespace.Builder.create(CODE, null, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_second_empty() {
        Namespace.Builder.create(CODE, "", 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_second_whitespace() {
        Namespace.Builder.create(CODE, """
        """, 1);
    }

    @Test
    void happy_path() {
        Namespace.Builder.create(CODE, APP_CODE, 1);
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, Namespace.class)
	}

    private create() {
		return Namespace.Builder.create(new NamespaceContract() {
				def String code = "PC"
				def String applicationCode = "AC"
                def String name = "N"
                def boolean active = true
                def Long versionNumber = 1
			}).build()
	}
}
