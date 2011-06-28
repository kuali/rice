package org.kuali.rice.kim.api.identity.name

import org.junit.Test
import org.junit.Assert
import org.kuali.rice.kim.api.test.JAXBAssert
import org.kuali.rice.kim.api.identity.Type
import org.kuali.rice.kim.api.identity.TypeContract

class EntityNameTest {
    private static final String ID = "1";
	private static final String ENTITY_TYPE_CODE = "PERSON";
	private static final String ENTITY_ID = "190192";
    private static final String TYPE_CODE = "Home"
    private static final String TYPE_NAME = "Home-y"
    private static final String TYPE_SORT_CODE = "0"
    private static final String TYPE_ACTIVE = "true"
    private static final Long TYPE_VERSION_NUMBER = new Integer(1)
	private static final String TYPE_OBJECT_ID = UUID.randomUUID()
	private static final String TITLE = "Mr"
    private static final String FIRST_NAME = "Bob"
    private static final String MIDDLE_NAME = "Mob"
    private static final String LAST_NAME = "Sob"
    private static final String SUFFIX = "Jr"
    private static final String FORMATTED_NAME = LAST_NAME + ", " + FIRST_NAME + " " + MIDDLE_NAME;
    
    private static final String SUPPRESS = "false"
    private static final String DEFAULT = "true"
    private static final String ACTIVE = "true"
    private static final Long VERSION_NUMBER = new Integer(1);
	private static final String OBJECT_ID = UUID.randomUUID();

    private static final String XML = """
    <entityName xmlns="http://rice.kuali.org/kim/v2_0">
        <id>${ID}</id>
        <entityId>${ENTITY_ID}</entityId>
        <nameType>
            <code>${TYPE_CODE}</code>
            <name>${TYPE_NAME}</name>
            <active>${TYPE_ACTIVE}</active>
            <sortCode>${TYPE_SORT_CODE}</sortCode>
            <versionNumber>${TYPE_VERSION_NUMBER}</versionNumber>
            <objectId>${TYPE_OBJECT_ID}</objectId>
        </nameType>
        <title>${TITLE}</title>
        <firstName>${FIRST_NAME}</firstName>
        <middleName>${MIDDLE_NAME}</middleName>
        <lastName>${LAST_NAME}</lastName>
        <suffix>${SUFFIX}</suffix>
        <formattedName>${FORMATTED_NAME}</formattedName>
        <titleUnmasked>${TITLE}</titleUnmasked>
        <firstNameUnmasked>${FIRST_NAME}</firstNameUnmasked>
        <middleNameUnmasked>${MIDDLE_NAME}</middleNameUnmasked>
        <lastNameUnmasked>${LAST_NAME}</lastNameUnmasked>
        <suffixUnmasked>${SUFFIX}</suffixUnmasked>
        <formattedNameUnmasked>${FORMATTED_NAME}</formattedNameUnmasked>
        <suppressName>${SUPPRESS}</suppressName>
        <defaultValue>${DEFAULT}</defaultValue>
        <active>${ACTIVE}</active>
        <versionNumber>${VERSION_NUMBER}</versionNumber>
        <objectId>${OBJECT_ID}</objectId>
    </entityName>
    """

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_id_whitespace() {
        EntityName.Builder builder = EntityName.Builder.create();
        builder.setId(" ")
    }

    @Test
    void test_copy() {
        def o1 = EntityName.Builder.create().build();
        def o2 = EntityName.Builder.create(o1).build();

        Assert.assertEquals(o1, o2);
    }

    @Test
    void happy_path() {
        EntityName.Builder.create();
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, EntityName.class)
	}

    public static create() {
		return EntityName.Builder.create(new EntityNameContract() {
			def String id = EntityNameTest.ID
            def String entityId = EntityNameTest.ENTITY_ID
			def Type getNameType() { Type.Builder.create(new TypeContract() {
				def String code = EntityNameTest.TYPE_CODE
				def String name = EntityNameTest.TYPE_NAME
				def boolean active = EntityNameTest.TYPE_ACTIVE
                def String sortCode = EntityNameTest.TYPE_SORT_CODE
                def Long versionNumber = EntityNameTest.TYPE_VERSION_NUMBER
				def String objectId = EntityNameTest.TYPE_OBJECT_ID
			}).build()}
            def String title = EntityNameTest.TITLE
            def String firstName = EntityNameTest.FIRST_NAME
            def String middleName = EntityNameTest.MIDDLE_NAME
            def String lastName = EntityNameTest.LAST_NAME
            def String suffix = EntityNameTest.SUFFIX
            def String formattedName = EntityNameTest.FORMATTED_NAME
            def String titleUnmasked = EntityNameTest.TITLE
            def String firstNameUnmasked = EntityNameTest.FIRST_NAME
            def String middleNameUnmasked = EntityNameTest.MIDDLE_NAME
            def String lastNameUnmasked = EntityNameTest.LAST_NAME
            def String suffixUnmasked = EntityNameTest.SUFFIX
            def String formattedNameUnmasked = EntityNameTest.FORMATTED_NAME
            def boolean suppressName = EntityNameTest.SUPPRESS.toBoolean()
            def boolean defaultValue = EntityNameTest.DEFAULT.toBoolean()
            def boolean active = EntityNameTest.ACTIVE.toBoolean()
            def Long versionNumber = EntityNameTest.VERSION_NUMBER;
			def String objectId = EntityNameTest.OBJECT_ID
        }).build()

	}
    
}
