package org.kuali.rice.core.api.namespace

import javax.xml.bind.JAXBContext
import org.junit.Assert
import org.junit.Test

class NamespaceTest {
        private static final String XML = """
        <namespace xmlns="http://rice.kuali.org/core/namespace/v1_1">
            <code>PC</code>
            <applicationCode>AC</applicationCode>
            <name>N</name>
            <active>true</active>
        </namespace>
    """

    private static final String CODE = "PC"
    private static final String APP_CODE = "AC"

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_all_null() {
        Namespace.Builder.create(null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_null() {
        Namespace.Builder.create(null, APP_CODE);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_empty() {
        Namespace.Builder.create("", APP_CODE);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_whitespace() {
        Namespace.Builder.create("  ", APP_CODE);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_second_null() {
        Namespace.Builder.create(CODE, null);
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_second_empty() {
        Namespace.Builder.create(CODE, "");
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_second_whitespace() {
        Namespace.Builder.create(CODE, """
        """);
    }

    @Test
    void happy_path() {
        Namespace.Builder.create(CODE, APP_CODE);
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
	  def jc = JAXBContext.newInstance(Namespace.class)
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
		return Namespace.Builder.create(new NamespaceContract() {
				def String code = "PC"
				def String applicationCode = "AC"
                def String name = "N"
                def boolean active = true
			}).build()
	}
}
