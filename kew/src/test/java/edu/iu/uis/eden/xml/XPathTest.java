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

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XPathTest extends TestCase {
    private static final Logger LOG = Logger.getLogger(XPathTest.class);

    private static final String STYLESHEET_RESOURCE = "edu/iu/uis/eden/edoclite/DefaultStyle.xsl";
    private static final String INITIAL_EDOC_XML = "initial_edldoc.xml";
    private static final String SAMPLE_EDOC_XML = "sample_edldoc.xml";

    @Ignore("The tests in this test case need to be fixed???")
    @Test public void testTheTestsInThisTestCaseNeedToBeFixed() {
        
    }
    
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

    /*public void testTransformInitialDoc() throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source styleSheet = new StreamSource(this.getClass().getClassLoader().getResourceAsStream(STYLESHEET_RESOURCE));
        Templates templates = templates = factory.newTemplates(styleSheet);
        Transformer transformer = templates.newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.setParameter("readOnly", "false");
        //transformer.setParameter("docType", docType);
        //transformer.setParameter("schema", schema);

        Source input = new StreamSource(this.getClass().getResourceAsStream(INITIAL_EDOC_XML));
        transformer.transform(input, new StreamResult(System.out));
    }

    public void testFieldHasMatchingUserValues() throws Exception {
        LOG.info("testFieldHasMatchingUserValues");
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        XPath xpath = XPathFactory.newInstance().newXPath();
        Document doc = db.parse(this.getClass().getResourceAsStream(SAMPLE_EDOC_XML));
        // enumerate all fields
        final String fieldDefs = "/edlContent/edl/field/display/values";
        NodeList nodes = (NodeList) xpath.evaluate(fieldDefs, doc, XPathConstants.NODESET);

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String name = (String) xpath.evaluate("../../@name", node, XPathConstants.STRING);
            LOG.debug("Name: " + name);
            LOG.debug("Value: " + node.getFirstChild().getNodeValue());
            final String expr = "/edlContent/data/version[@current='true']/fieldEntry[@name=current()/../../@name and value=current()]";
            NodeList matchingUserValues = (NodeList) xpath.evaluate(expr, node, XPathConstants.NODESET);
            LOG.debug(matchingUserValues + "");
            LOG.debug(matchingUserValues.getLength() + "");
            if ("gender".equals(name)) {
                assertTrue("Matching values > 0", matchingUserValues.getLength() > 0);
            }
            for (int j = 0; j < matchingUserValues.getLength(); j++) {
                LOG.debug(matchingUserValues.item(j).getFirstChild().getNodeValue());    
            }
        }
    }

    public void testUpdateEDLDocument() throws Exception {
        final Map params = new HashMap();
        params.put("givenname", new String[] { "Frank" });
        params.put("surname", new String[] { "Miller" });
        params.put("email", new String[] { "frank@bogus.blah.asdlajsd.co.uk" });
        params.put("gender", new String[] { "male" });
        params.put("color", new String[] { "blue" });
        params.put("food", new String[] { "sandwiches", "soup" });

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        XPath xpath = XPathFactory.newInstance().newXPath();
        String versionsExpression = "/edlContent/data/version";

        // try an initial empty doc
        EDLDocument edlDoc = new EDLDocument(db.parse(this.getClass().getResourceAsStream(INITIAL_EDOC_XML))); 
        int numVersionsBefore = ((NodeList) xpath.evaluate(versionsExpression, edlDoc.getDocument(), XPathConstants.NODESET)).getLength();
        LOG.debug("Initial before:");
        LOG.debug(edlDoc);
        edlDoc.update(null, params);
        LOG.debug("Initial after:");
        LOG.debug(edlDoc);
        int numVersionsAfter = ((NodeList) xpath.evaluate(versionsExpression, edlDoc.getDocument(), XPathConstants.NODESET)).getLength();
        assertEquals(numVersionsBefore + 1, numVersionsAfter);

        numVersionsBefore = ((NodeList) xpath.evaluate(versionsExpression, edlDoc.getDocument(), XPathConstants.NODESET)).getLength();
        LOG.debug("Initial 2nd time before:");
        LOG.debug(edlDoc);
        edlDoc.update(null, params);
        LOG.debug("Initial 2nd time after:");
        LOG.debug(edlDoc);
        numVersionsAfter = ((NodeList) xpath.evaluate(versionsExpression, edlDoc.getDocument(), XPathConstants.NODESET)).getLength();
        assertEquals(numVersionsBefore + 1, numVersionsAfter);
        
        // try a sample doc
        edlDoc = new EDLDocument(db.parse(this.getClass().getResourceAsStream(SAMPLE_EDOC_XML)));
        numVersionsBefore = ((NodeList) xpath.evaluate(versionsExpression, edlDoc.getDocument(), XPathConstants.NODESET)).getLength();
        LOG.debug("Sample before:");
        LOG.debug(edlDoc);
        edlDoc.update(null, params);
        LOG.debug("Sample after:");
        LOG.debug(edlDoc);
        numVersionsAfter = ((NodeList) xpath.evaluate(versionsExpression, edlDoc.getDocument(), XPathConstants.NODESET)).getLength();
        assertEquals(numVersionsBefore + 1, numVersionsAfter);
    }

    public void testXPathStuff() throws Exception {
        InputStream edlDocContent = new TestUtilities().loadResource(this.getClass(), "edldoccontent.xml");
        org.w3c.dom.Document w3cDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(edlDocContent);
        // Document document = new DOMBuilder().build(w3cDocument);
        // DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        // Document routeDocument = builder.parse(new File("ParallelRouting.xml"));

        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.getXPathFunctionResolver();
        // String expression = "//version[@current='true']/fieldEntry[@name='name']/value";
        // xpath.getXPathFunctionResolver().resolveFunction();s
        String expression = "//version[@current='true']/fieldEntry[@name=concat('n', 'ame')]/value";
        String expression2 = "local-name(//field[@name='name']/@name)";
        String expression3 = "//version[@current='true']/fieldEntry[@name=local-name(//field[@name='name']/@name)]/value";
        Node node = (Node) xpath.evaluate(expression3, w3cDocument, XPathConstants.NODE);
        xpath.evaluate(expression3, w3cDocument);
        node.getNodeValue();
        node.getNodeType();
        ((Text)node.getFirstChild()).getNodeValue();
        int i =1;
    }*/
}