package org.kuali.rice.core.api.parameter

import javax.xml.bind.JAXBContext
import org.junit.Assert
import org.junit.Test

class ParameterKeyTest {
        private static final String XML = """
        <parameterKey xmlns="http://rice.kuali.org/core/parameter/v1_1">
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
