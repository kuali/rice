package org.kuali.rice.krad.uif;

import org.junit.Test;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.test.KRADTestCase;
import org.kuali.test.TestDictionaryConfig;

import static org.junit.Assert.*;

/**
 * Test methods for the {@link org.kuali.rice.krad.uif.util.UifBeanFactoryPostProcessor} class
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@TestDictionaryConfig(
        dataDictionaryFiles = "classpath:org/kuali/rice/krad/uif/UifBeanFactoryPostProcessorTestBeans.xml")
public class UifBeanFactoryPostProcessorTestNew extends KRADTestCase {

    /**
     * Verifies overriding of a property that exists in the parent bean definition as an expression on
     * a nested bean
     *
     * <p>
     * For example, suppose our parent bean has a nested bean named 'nestedBean' with property foo, that has
     * an expression value. The child bean then sets the property 'nestedBean.foo' to a value. We need to
     * verify the expression is removed from the nested parent bean
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testOverrideOfNestedBeanExpression() throws Exception {
        // one level of nesting
        InputField inputField = (InputField) getTestDictionaryObject("testNestedExpressionOverride");
        assertNotNull("No bean exists with id: testNestedExpressionOverride", inputField);

        assertFalse("Child property did not override", inputField.getInquiry().isRender());
        assertNull("Parent nested bean expression still in property expressions map",
                inputField.getInquiry().getPropertyExpression("render"));

        // one level of nesting, parent with nested notation
        inputField = (InputField) getTestDictionaryObject("testNestedExpressionOverride3");
        assertNotNull("No bean exists with id: testNestedExpressionOverride3", inputField);

        assertTrue("Child property did not override", inputField.getInquiry().isRender());
        assertNull("Parent nested bean expression still in property expressions map",
                inputField.getInquiry().getPropertyExpression("render"));

        // two levels of nesting
        UifTestBeanObject testBean = (UifTestBeanObject) getTestDictionaryObject("testNestedExpressionOverride5");
        assertNotNull("No bean exists with id: testNestedExpressionOverride5", testBean);

        assertEquals("Child property did not override", "old school",
                testBean.getReference1().getReference1().getProperty1());
        assertNull("Parent nested bean expression still in property expressions map",
                testBean.getReference1().getReference1().getPropertyExpression("render"));
    }

    /**
     * Tests merging of maps when the map entries contain expressions
     *
     * @throws Exception
     */
    @Test
    public void testMergingOfMapExpressions() throws Exception {
        UifTestBeanObject testBean = (UifTestBeanObject) getTestDictionaryObject("testExpressionMapMerging2");
        assertNotNull("No bean exists with id: testExpressionMapMerging2", testBean);

        assertTrue("Merged map is not correct size (2)", testBean.getMap1().size() == 2);
        assertTrue("Merged map does not contain key2", testBean.getMap1().containsKey("key2"));
        assertTrue("Merged map does not contain key3)", testBean.getMap1().containsKey("key3"));

        assertTrue("Expression count not correct for merged map", testBean.getPropertyExpressions().size() == 2);
        assertEquals("", "@{expr1}", testBean.getPropertyExpression("map1['key1']"));
        assertEquals("", "@{expr4}", testBean.getPropertyExpression("map1['key4']"));
    }

}
