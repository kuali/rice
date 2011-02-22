package org.kuali.rice.core.api.parameter

import javax.xml.bind.JAXBContext
import org.junit.Assert
import org.junit.Test

class ParameterTypeTest {

    private static final String XML = """
        <parameterType xmlns="http://rice.kuali.org/core/parameter/v1_1">
            <code>PC</code>
            <name>Config</name>
            <active>true</active>
        </parameterType>
    """

    private static final String PARAMETER_TYPE_CODE = "PC"

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_null() {
        ParameterType.Builder.create((String) null);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_empty() {
        ParameterType.Builder.create("");
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_whitespace() {
        ParameterType.Builder.create("  ");
    }

        @Test
    void happy_path() {
        ParameterType.Builder.create(PARAMETER_TYPE_CODE);
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
	  def jc = JAXBContext.newInstance(ParameterType.class)
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
		return ParameterType.Builder.create(new ParameterTypeContract() {
				def String code ="PC"
				def String name = "Config"
				def boolean active = true
			}).build()
	}
}
