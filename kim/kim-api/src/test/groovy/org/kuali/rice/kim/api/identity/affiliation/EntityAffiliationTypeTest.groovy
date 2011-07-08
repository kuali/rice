package org.kuali.rice.kim.api.identity.affiliation

import org.junit.Test
import org.kuali.rice.kim.api.test.JAXBAssert
import org.junit.Assert

class EntityAffiliationTypeTest {
    private static final String CODE = "ABC";
    private static final String NAME = "ABC Name";
    private static final String SORT_CODE = "0";
    private static final String EMPLOYEE = "false";
    private static final String ACTIVE = "true"
    private static final Long VERSION_NUMBER = new Integer(1)
	private static final String OBJECT_ID = UUID.randomUUID()

    private static final String XML = """
    <entityAffiliationType xmlns="http://rice.kuali.org/kim/v2_0">
        <code>${CODE}</code>
        <name>${NAME}</name>
        <sortCode>${SORT_CODE}</sortCode>
        <active>${ACTIVE}</active>
        <employmentAffiliationType>${EMPLOYEE}</employmentAffiliationType>
        <versionNumber>${VERSION_NUMBER}</versionNumber>
        <objectId>${OBJECT_ID}</objectId>
    </entityAffiliationType>
    """

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_code_whitespace() {
        EntityAffiliationType.Builder builder = EntityAffiliationType.Builder.create(" ");
    }

    @Test
    void test_copy() {
        def o1 = EntityAffiliationType.Builder.create("ABC").build();
        def o2 = EntityAffiliationType.Builder.create(o1).build();

        Assert.assertEquals(o1, o2);
    }

    @Test
    void happy_path() {
        EntityAffiliationType.Builder.create("ABC");
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, EntityAffiliationType.class)
	}

    public static create() {
		return EntityAffiliationType.Builder.create(new EntityAffiliationTypeContract() {
			def String code = EntityAffiliationTypeTest.CODE
		    def String name = EntityAffiliationTypeTest.NAME
            def String sortCode = EntityAffiliationTypeTest.SORT_CODE
            def boolean active = EntityAffiliationTypeTest.ACTIVE.toBoolean()
            def boolean employmentAffiliationType = EntityAffiliationTypeTest.EMPLOYEE.toBoolean()
            def Long versionNumber = EntityAffiliationTypeTest.VERSION_NUMBER
			def String objectId = EntityAffiliationTypeTest.OBJECT_ID
        }).build()
	}
}
