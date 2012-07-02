package org.kuali.rice.krad.uif;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.view.InquiryView;
import org.kuali.test.KRADTestCase;
import org.kuali.test.TestDictionaryConfig;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test methods for the {@link org.kuali.rice.krad.uif.util.UifBeanFactoryPostProcessor} class
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@TestDictionaryConfig(
        dataDictionaryFiles = "classpath:org/kuali/rice/krad/uif/UifBeanFactoryPostProcessorTestBeans.xml")
public class UifBeanFactoryPostProcessorTest extends KRADTestCase {

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
        // test expression is captured
        InputField inputField = (InputField) getTestDictionaryObject("testNestedExpressionOverride2");
        assertNotNull("No bean exists with id: testNestedExpressionOverride2", inputField);

        assertNotNull("Expression not in graph", inputField.getExpressionGraph().get("inquiry.render"));

        // one level of nesting
        inputField = (InputField) getTestDictionaryObject("testNestedExpressionOverride");
        assertNotNull("No bean exists with id: testNestedExpressionOverride", inputField);

        assertFalse("Child property did not override", inputField.getInquiry().isRender());
        assertNull("Parent nested bean expression still in expression graph", inputField.getExpressionGraph().get(
                "inquiry.render"));

        // one level of nesting, parent with nested notation
        inputField = (InputField) getTestDictionaryObject("testNestedExpressionOverride3");
        assertNotNull("No bean exists with id: testNestedExpressionOverride3", inputField);

        assertTrue("Child property did not override", inputField.getInquiry().isRender());
        assertNull("Parent nested bean expression still in expression graph", inputField.getExpressionGraph().get(
                "inquiry.render"));

        // two levels of nesting
        UifTestBeanObject testBean = (UifTestBeanObject) getTestDictionaryObject("testNestedExpressionOverride5");
        assertNotNull("No bean exists with id: testNestedExpressionOverride5", testBean);

        assertEquals("Child property did not override", "old school",
                testBean.getReference1().getReference1().getProperty1());
        assertNull("Parent nested bean expression still in expression graph", inputField.getExpressionGraph().get(
                "reference1.reference1.property1"));
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

        assertTrue("Expression count not correct for merged map", testBean.getExpressionGraph().size() == 2);
        assertEquals("Bean does not contain expression for property key1", "@{expr1}",
                testBean.getExpressionGraph().get("map1['key1']"));
        assertEquals("Bean does not contain expression for property key4", "@{expr4}",
                testBean.getExpressionGraph().get("map1['key4']"));
    }

    /**
     * Tests non merging of maps when the map entries contain expressions
     *
     * @throws Exception
     */
    @Test
    public void testNonMergingOfMapExpressions() throws Exception {
        UifTestBeanObject testBean = (UifTestBeanObject) getTestDictionaryObject("testExpressionMapNonMerging");
        assertNotNull("No bean exists with id: testExpressionMapNonMerging", testBean);

        assertTrue("Non-Merged map is not correct size (1)", testBean.getMap1().size() == 1);
        assertTrue("Non-Merged map does not contain key3)", testBean.getMap1().containsKey("key3"));

        assertTrue("Expression count not correct for non-merged map", testBean.getExpressionGraph().size() == 1);
        assertEquals("Bean does not contain expression for property key4", "@{expr4}",
                testBean.getExpressionGraph().get("map1['key4']"));
    }

    /**
     * Tests merging of maps where the child bean is nested within a list
     *
     * TODO: this test is currently failing due to spring support of nested map merging
     * @throws Exception
     */
    public void testNestedListExpressions() throws Exception {
        UifTestBeanObject testBean = (UifTestBeanObject) getTestDictionaryObject("testListBeanExpressionMerging");
        assertNotNull("No bean exists with id: testListBeanExpressionMerging", testBean);

        Map<String, String> mergedMap = testBean.getListReference1().get(0).getReference1().getMap1();

        assertTrue("Merged map is not correct size (2)", mergedMap.size() == 2);
        assertTrue("Merged map does not contain key2", mergedMap.containsKey("key2"));
        assertTrue("Merged map does not contain key3)", mergedMap.containsKey("key3"));

        UifTestBeanObject rootListBean = testBean.getListReference1().get(0);

        assertTrue("Expression count not correct for merged map", rootListBean.getExpressionGraph().size() == 2);
        assertEquals("Bean does not contain expression for property key1", "@{expr1}",
                rootListBean.getExpressionGraph().get("reference1.map1['key1']"));
        assertEquals("Bean does not contain expression for property key1", "@{expr4}",
                rootListBean.getExpressionGraph().get("reference1.map1['key4']"));
    }

    /**
     * Tests the postProcessBeanFactory method using beans with simple inheritance
     *
     * @throws Exception
     */
    @Test
    public void testPostProcessBeanFactoryWithSimpleInheritanceSucceeds() throws Exception {
        UifTestBeanObject simpleBean1 = (UifTestBeanObject) getTestDictionaryObject("testSimpleBean1");
        UifTestBeanObject simpleBean2 = (UifTestBeanObject) getTestDictionaryObject("testSimpleBean2");

        assertEquals("Bean does not have the correct property3 value", simpleBean1.getExpressionGraph().get("property3"), "@{1 eq 1}");
        assertNull("Bean should not have a property3 value", simpleBean2.getExpressionGraph().get("property3"));
    }

    /**
     * Tests the postProcessBeanFactory method using beans with inheritance and nested properties
     *
     * @throws Exception
     */
    @Test
    public void testPostProcessBeanFactoryWithSimpleNestingSucceeds() throws Exception {
        UifTestBeanObject simpleBean1 = (UifTestBeanObject) getTestDictionaryObject("testSimpleBean1");
        UifTestBeanObject simpleBean4 = (UifTestBeanObject) getTestDictionaryObject("testSimpleBean4");

        assertEquals("Bean does not have the correct property3 value", simpleBean1.getExpressionGraph().get("property3"), "@{1 eq 1}");
        assertNull("Bean should not have a property3 value", simpleBean4.getExpressionGraph().get("property3"));
    }

    /**
     * Tests the postProcessBeanFactory method using the people flow inquiry view
     *
     * @throws Exception
     */
    @Test
    public void testPostProcessBeanFactoryWithPeopleFlowSucceeds() throws Exception {
        InquiryView inquiryView = (InquiryView) getTestDictionaryObject("testPeopleFlow-InquiryView");

        assertNotNull("Bean should have an inquiry property value", ((DataField)inquiryView.getItems().get(0).getItems().get(0)).getInquiry());
        assertFalse("Bean should have an inquiry render value of false", ((DataField)inquiryView.getItems().get(0).getItems().get(0)).getInquiry().isRender());
    }

}
