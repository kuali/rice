package org.kuali.rice.krms.api.repository

import org.kuali.rice.krms.api.repository.category.CategoryDefinition
import org.kuali.rice.krms.api.test.JAXBAssert
import org.junit.Test
import org.junit.Assert
import org.kuali.rice.krms.api.repository.category.CategoryDefinitionContract


class CategoryDefinitionTest {

    private static final String CTGRY_ID = "1"
    private static final String CTGRY_NAME = "category1"
    private static final String CTGRY_NAMESPACE = "namespace"
    private static final Long VERSION_NUMBER = 1;

    private static final String EXPECTED_XML = """
        <category xmlns="http://rice.kuali.org/krms/repository/v2_0">
            <id>1</id>
	        <name>category1</name>
	        <namespace>namespace</namespace>
	        <versionNumber>1</versionNumber>
	    </category>
    """

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_create_fail_all_null() {
        CategoryDefinition.Builder.create(null, null, null)
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_create_fail_whitespace_id() {
        CategoryDefinition.Builder.create("", CTGRY_NAME, CTGRY_NAMESPACE)
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_create_fail_whitespace_name() {
        CategoryDefinition.Builder.create(CTGRY_ID, "", CTGRY_NAMESPACE)
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_create_fail_whitespace_namespace() {
        CategoryDefinition.Builder.create(CTGRY_ID, CTGRY_NAME, "")
    }

    @Test
    void test_Builder_create_success_null_id() {
        CategoryDefinition categoryDef = CategoryDefinition.Builder.create(null, CTGRY_NAME, CTGRY_NAMESPACE).build()
        Assert.assertEquals(null, categoryDef.getId())
        Assert.assertEquals(CTGRY_NAME, categoryDef.getName())
        Assert.assertEquals(CTGRY_NAMESPACE, categoryDef.getNamespace())
    }

    @Test
    void test_Builder_create_success() {
        CategoryDefinition categoryDef = CategoryDefinition.Builder.create(CTGRY_ID, CTGRY_NAME, CTGRY_NAMESPACE).build()
        Assert.assertEquals(CTGRY_ID, categoryDef.getId())
        Assert.assertEquals(CTGRY_NAME, categoryDef.getName())
        Assert.assertEquals(CTGRY_NAMESPACE, categoryDef.getNamespace())
    }

    @Test
    public void test_Xml_Marshal_Unmarshal() {
        JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), EXPECTED_XML, CategoryDefinition.class)
    }

    /**
     * Ensures that toString executes cleanly.
     */
    @Test
    public void testToString() {
        def CategoryDefinition function = create();
        def toString = function.toString();
        Assert.assertNotNull toString;
        System.out.println(toString);
    }

    private CategoryDefinition.Builder createBuilder() {
        return CategoryDefinition.Builder.create(new CategoryDefinitionContract() {
            String id = CategoryDefinitionTest.CTGRY_ID
            String name = CategoryDefinitionTest.CTGRY_NAME
            String namespace = CategoryDefinitionTest.CTGRY_NAMESPACE
            Long versionNumber = CategoryDefinitionTest.VERSION_NUMBER;
        })
    }

    private CategoryDefinition create() {
        return createBuilder().build();
    }

}
