/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.xml;

import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XPathTest extends TestCase {

    private static final String TEST_DOC = "<root name=\"root\">\n" +
                                           "  <child name=\"child1\">\n" +
                                           "    <child_1 name=\"child1_1\">\n" +
                                           "      <closedSimple/>\n" +
                                           "      <emptySimple></emptySimple>\n" +
                                           "      <textSimple>some text 1</textSimple>\n" +
                                           "    </child_1>\n" +
                                           "  </child>\n" +
                                           "  <child name=\"child2\">\n" +
                                           "    <child_2 name=\"child2_1\">\n" +
                                           "      <closedSimple/>\n" +
                                           "      <emptySimple></emptySimple>\n" +
                                           "      <textSimple>some text 2</textSimple>\n" +
                                           "    </child_2>\n" +
                                           "  </child>\n" +
                                           "</root>";

    private static final String TEST_ATTRIBUTE_DOC = "<root name=\"root\">\n" +
                                                     "  <field name=\"one\" type=\"ALL\"/>\n" +
                                                     "  <field name=\"two\" type=\"REPORT\"/>\n" +
                                                     "  <field name=\"three\"/>\n" +
                                                     "</root>";

    private static final XPath XPATH = XPathFactory.newInstance().newXPath();

    private static final InputSource getTestInputSource() {
        return new InputSource(new StringReader(TEST_DOC));
    }

    @Test public void testAttributeAbsence() throws XPathExpressionException {
        NodeList nodes = (NodeList) XPATH.evaluate("/root/child[not(@nonExistentAttribute)]", getTestInputSource(), XPathConstants.NODESET);
        assertEquals(2, nodes.getLength());
        assertEquals("child1", nodes.item(0).getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("child2", nodes.item(1).getAttributes().getNamedItem("name").getNodeValue());

        // now try with an equivalent compound expression
        nodes = (NodeList) XPATH.evaluate("/root/*[local-name(.) = 'child' or (@nonExistentAttribute)]", getTestInputSource(), XPathConstants.NODESET);
        assertEquals(2, nodes.getLength());
        assertEquals("child1", nodes.item(0).getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("child2", nodes.item(1).getAttributes().getNamedItem("name").getNodeValue());

        nodes = (NodeList) XPATH.evaluate("/root/child[not(@name)]", getTestInputSource(), XPathConstants.NODE);
        assertNull(nodes);

        // now use a more specific test document
        nodes = (NodeList) XPATH.evaluate("/root/field[@type='ALL' or not(@type)]", new InputSource(new StringReader(TEST_ATTRIBUTE_DOC)), XPathConstants.NODESET);
        assertEquals(2, nodes.getLength());
        assertEquals("one", nodes.item(0).getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("three", nodes.item(1).getAttributes().getNamedItem("name").getNodeValue());
    }

    @Test public void testSelectJustChilds() throws XPathExpressionException {
        NodeList nodes = (NodeList) XPATH.evaluate("/root/child", getTestInputSource(), XPathConstants.NODESET);
        assertEquals(2, nodes.getLength());
        assertEquals("child1", nodes.item(0).getAttributes().getNamedItem("name").getNodeValue());
        assertEquals("child2", nodes.item(1).getAttributes().getNamedItem("name").getNodeValue());
    }

    @Test public void testSelectAbsoluteChild() throws XPathExpressionException {
        Node node = (Node) XPATH.evaluate("/root/child", getTestInputSource(), XPathConstants.NODE);
        assertEquals("child1", node.getAttributes().getNamedItem("name").getNodeValue());
    }

    @Test public void testSelectAnyChild() throws XPathExpressionException {
        Node anyNode = (Node) XPATH.evaluate("//child", getTestInputSource(), XPathConstants.NODE);
        assertEquals("child1", anyNode.getAttributes().getNamedItem("name").getNodeValue());
    }

    @Test public void testNonexistent()  throws XPathExpressionException {
        final String expr = "//child/child_1/nonExistent";
        Node nonexistent = (Node) XPATH.evaluate(expr, getTestInputSource(), XPathConstants.NODE);
        assertNull(nonexistent);
        String valueOfNonexistentElement = (String) XPATH.evaluate(expr, getTestInputSource(), XPathConstants.STRING);
        // a non-existent element does not have a 'null' text value but a zero-length string
        assertEquals("", valueOfNonexistentElement);
    }

    @Test public void testClosedSimple() throws XPathExpressionException {
        final String expr = "//child/child_1/closedSimple";
        Node closedSimple = (Node) XPATH.evaluate(expr, getTestInputSource(), XPathConstants.NODE);
        assertNotNull(closedSimple);
        assertNull(closedSimple.getFirstChild());
        String valueOfClosedTag = (String) XPATH.evaluate(expr, getTestInputSource(), XPathConstants.STRING);
        // a closed element does not have a 'null' text value but a zero-length string
        assertEquals("", valueOfClosedTag);
    }

    @Test public void testEmptySimple() throws XPathExpressionException {
        final String expr = "//child/child_1/emptySimple";
        Node emptySimple = (Node) XPATH.evaluate(expr, getTestInputSource(), XPathConstants.NODE);
        assertNotNull(emptySimple);
        assertNull(emptySimple.getFirstChild());
        String valueOfEmptyTag = (String) XPATH.evaluate(expr, getTestInputSource(), XPathConstants.STRING);
        // a closed element does not have a 'null' text value but a zero-length string
        assertEquals("", valueOfEmptyTag);
    }

    @Test public void testText() throws XPathExpressionException {
        final String expr = "//child/child_2[@name='child2_1']/textSimple";
        final String expected = "some text 2";
        Node textSimple = (Node) XPATH.evaluate(expr, getTestInputSource(), XPathConstants.NODE);
        assertNotNull(textSimple);
        assertNotNull(textSimple.getFirstChild());
        String valueOfTextTag = (String) XPATH.evaluate(expr, getTestInputSource(), XPathConstants.STRING);
        // a closed element does not have a 'null' text value but a zero-length string
        assertEquals(expected, valueOfTextTag);
    }
}