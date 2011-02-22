package org.kuali.rice.core.api.parameter;


import javax.xml.bind.JAXBContext
import org.junit.Assert
import org.junit.Test

public class ParameterTest {

    private static final String XML = """
    <parameter xmlns="http://rice.kuali.org/core/parameter/v1_1">
        <applicationCode>BORG_HUNT</applicationCode>
        <namespaceCode>TNG</namespaceCode>
        <componentCode>C</componentCode>
        <name>SHIELDS_ON</name>
        <value>true</value>
        <description>turn the shields on</description>
        <parameterType>
            <code>PC</code>
            <name>Config</name>
            <active>true</active>
        </parameterType>
        <evaluationOperator>A</evaluationOperator>
    </parameter>
    """

    private static final String APPLICATION_CODE = "BORG_HUNT";
	private static final String NAMESPACE_CODE = "TNG";
	private static final String COMPONENT_CODE = "C";
	private static final String NAME = "SHIELDS_ON";
	private static final String PARAMETER_TYPE_CODE = "PC"

	@Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_all_null() {
        Parameter.Builder.create(null, null, null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_null() {
        Parameter.Builder.create(null, NAMESPACE_CODE, COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE));
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_empty() {
        Parameter.Builder.create("", NAMESPACE_CODE, COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE));
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_whitespace() {
        Parameter.Builder.create("  ", NAMESPACE_CODE, COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE));
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_second_null() {
        Parameter.Builder.create(APPLICATION_CODE, null, COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE));
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_second_empty() {
        Parameter.Builder.create(APPLICATION_CODE, "", COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE));
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_second_whitespace() {
        Parameter.Builder.create(APPLICATION_CODE, " ", COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE));
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_third_null() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, null, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE));
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_third_empty() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, "", NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE));
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_third_whitespace() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, " ", NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE));
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_fourth_null() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, null, ParameterType.Builder.create(PARAMETER_TYPE_CODE));
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_fourth_empty() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, "", ParameterType.Builder.create(PARAMETER_TYPE_CODE));
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_fourth_whitespace() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, " ", ParameterType.Builder.create(PARAMETER_TYPE_CODE));
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_fifth_whitespace() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, NAME, null);
    }

    @Test
    void happy_path() {
        Parameter.Builder.create(APPLICATION_CODE, NAMESPACE_CODE, COMPONENT_CODE, NAME, ParameterType.Builder.create(PARAMETER_TYPE_CODE));
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
			def String name = "SHIELDS_ON"
			def ParameterType getParameterType() { ParameterType.Builder.create(new ParameterTypeContract() {
				def String code ="PC"
				def String name = "Config"
				def boolean active = true
			}).build()
            }
            def String applicationCode = "BORG_HUNT"
            def String namespaceCode = "TNG"
            def String componentCode = "C"
            def String value = "true"
            def String description = "turn the shields on"
            def EvaluationOperator evaluationOperator = EvaluationOperator.ALLOW
        }).build()
	}
}
