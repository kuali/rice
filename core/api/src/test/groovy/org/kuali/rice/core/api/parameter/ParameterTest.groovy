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





package org.kuali.rice.core.api.parameter;


import javax.xml.bind.JAXBContext
import org.junit.Assert
import org.junit.Test

public class ParameterTest {

    private static final String APPLICATION_CODE = "BORG_HUNT";
	private static final String NAMESPACE_CODE = "TNG";
	private static final String COMPONENT_CODE = "C";
	private static final String NAME = "SHIELDS_ON";
    private static final String VALUE = "true";
	private static final String DESC = "turn the shields on";
    private static final EvaluationOperator EVALUATION_OP = EvaluationOperator.ALLOW;
	private static final String PARAMETER_TYPE_CODE = "PC"
    private static final String PARAMETER_TYPE_NAME = "Config"
    private static final String PARAMETER_TYPE_ACTIVE = "true"
    private static final Long VERSION_NUMBER = new Integer(1);

    private static final String XML = """
    <parameter xmlns="http://rice.kuali.org/core/v1_1">
        <applicationCode>${APPLICATION_CODE}</applicationCode>
        <namespaceCode>${NAMESPACE_CODE}</namespaceCode>
        <componentCode>${COMPONENT_CODE}</componentCode>
        <name>${NAME}</name>
        <value>${VALUE}</value>
        <description>${DESC}</description>
        <parameterType>
            <code>${PARAMETER_TYPE_CODE}</code>
            <name>${PARAMETER_TYPE_NAME}</name>
            <active>${PARAMETER_TYPE_ACTIVE}</active>
            <versionNumber>1</versionNumber>
        </parameterType>
        <evaluationOperator>${EVALUATION_OP.operatorCode}</evaluationOperator>
        <versionNumber>1</versionNumber>
    </parameter>
    """

	@Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_all_null() {
        Parameter.Builder.create(null, null, null, null, null, 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_null() {
        Parameter.Builder.create(null, NAMESPACE_CODE, COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_empty() {
        Parameter.Builder.create("", NAMESPACE_CODE, COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_whitespace() {
        Parameter.Builder.create("  ", NAMESPACE_CODE, COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_second_null() {
        Parameter.Builder.create(APPLICATION_CODE, null, COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_second_empty() {
        Parameter.Builder.create(APPLICATION_CODE, "", COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_second_whitespace() {
        Parameter.Builder.create(APPLICATION_CODE, " ", COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_third_null() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, null, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_third_empty() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, "", NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_third_whitespace() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, " ", NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_fourth_null() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, null, ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_fourth_empty() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, "", ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_fourth_whitespace() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, " ", ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_fifth_whitespace() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, NAME, null, 1);
    }

    @Test
    void test_create_only_required() {
        Parameter.Builder.create(Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1)).build();
    }

    @Test
    void happy_path() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE, 1), 1);
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
	  def jc = JAXBContext.newInstance(Parameter.class)
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
		return Parameter.Builder.create(new ParameterContract() {
			def String name = ParameterTest.NAME
			def ParameterType getParameterType() { ParameterType.Builder.create(new ParameterTypeContract() {
				def String code = ParameterTest.PARAMETER_TYPE_CODE
				def String name = ParameterTest.PARAMETER_TYPE_NAME
				def boolean active = ParameterTest.PARAMETER_TYPE_ACTIVE.toBoolean()
                def Long versionNumber = ParameterTest.VERSION_NUMBER
			}).build()
            }
            def String applicationCode = ParameterTest.APPLICATION_CODE
            def String namespaceCode = ParameterTest.NAMESPACE_CODE
            def String componentCode = ParameterTest.COMPONENT_CODE
            def String value = ParameterTest.VALUE
            def String description = ParameterTest.DESC
            def EvaluationOperator evaluationOperator = ParameterTest.EVALUATION_OP;
            def Long versionNumber = ParameterTest.VERSION_NUMBER;
        }).build()
	}
}
