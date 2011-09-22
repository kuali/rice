package org.kuali.rice.kim.api.identity

import org.junit.Test
import org.kuali.rice.kim.api.test.JAXBAssert


class TypeTest {
    private static final Long VERSION_NUMBER = new Integer(1);
	private static final String OBJECT_ID = UUID.randomUUID();
    private static final String XML = """
        <type xmlns="http://rice.kuali.org/kim/v2_0">
            <code>HOME</code>
            <name>Home</name>
            <active>true</active>
            <sortCode>1</sortCode>
            <versionNumber>${VERSION_NUMBER}</versionNumber>
            <objectId>${OBJECT_ID}</objectId>
        </type>
    """

    private static final String TYPE_CODE = "HOME"

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_empty() {
        CodedAttribute.Builder.create("");
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_first_whitespace() {
        CodedAttribute.Builder.create("  ");
    }

    @Test
    void test_create_only_required() {
        CodedAttribute.Builder.create(CodedAttribute.Builder.create(TYPE_CODE)).build();
    }

    @Test
    void happy_path() {
        CodedAttribute.Builder.create(TYPE_CODE);
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, CodedAttribute.class)
	}

    private create() {
		return CodedAttribute.Builder.create(new CodedAttributeContract() {
				def String code ="HOME"
				def String name = "Home"
				def boolean active = true
                def String sortCode = "1"
                def Long versionNumber = TypeTest.VERSION_NUMBER
				def String objectId = TypeTest.OBJECT_ID
			}).build()
	}
}
