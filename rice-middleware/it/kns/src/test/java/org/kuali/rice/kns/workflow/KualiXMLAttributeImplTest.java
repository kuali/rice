/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.workflow;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.kuali.rice.core.api.impex.xml.XmlConstants;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.reflect.ObjectDefinition;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.rule.xmlrouting.XPathHelper;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.kns.workflow.attribute.KualiXmlAttribute;
import org.kuali.rice.kns.workflow.attribute.KualiXmlAttributeHelper;
import org.kuali.rice.krad.test.KRADTestCase;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;

import static org.junit.Assert.assertNotNull;

/**
 * KualiXMLAttributeImplTest tests the {@link KualiXmlAttributeHelper} operations of getting data from the data dictionary for workflow
 * attributes
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * @deprecated KNS test class, convert to KRAD equivalent if applicable.
 */
@Deprecated
public class KualiXMLAttributeImplTest extends KRADTestCase {
    private static Log LOG = LogFactory.getLog(KualiXMLAttributeImplTest.class);

    private static final String RULE_ATTRIBUTE_CONFIG_NODE_NAME = XmlConstants.ROUTING_CONFIG;
    private static final String SEARCH_ATTRIBUTE_CONFIG_NODE_NAME = XmlConstants.SEARCHING_CONFIG;

    XPath myXPath = XPathHelper.newXPath();
    String ruleAttributeXml = "";
    String searchAttributeXml = "";

    @Override
    public void setUp() throws Exception {
        super.setUp();

        InputStream ruleInputStream = null;
        InputStream searchInputStream = null;

        try {
            ruleInputStream = getClass().getResourceAsStream("RuleAttributes.xml");
            assertNotNull(ruleInputStream);
            ruleAttributeXml = IOUtils.toString(ruleInputStream);

            searchInputStream = getClass().getResourceAsStream("SearchAttributes.xml");
            assertNotNull(searchInputStream);
            searchAttributeXml = IOUtils.toString(searchInputStream);
        } finally {
            if (ruleInputStream != null) {
                ruleInputStream.close();
            }

            if (searchInputStream != null) {
                searchInputStream.close();
            }
        }
    }

    /**
     * goes through all of the ruleAttributes in the inputSource and tries to get a label out of the data dictionary
     */
    @Test public void testConfirmLabels() {
        boolean failed = false;
        // test rule xml attributes
        failed |= confirmLabels(KualiXmlAttributeHelper.notFound, ruleAttributeXml, RULE_ATTRIBUTE_CONFIG_NODE_NAME);

        // test search xml attributes
        failed |= confirmLabels(KualiXmlAttributeHelper.notFound, searchAttributeXml, SEARCH_ATTRIBUTE_CONFIG_NODE_NAME);
        
        Assert.assertFalse("At least one label was incorrect", failed);
    }

    /**
     * accepts a Node, and if all goes well, returns the exact same Node with expected changes
     *
     * <p>The node should have the name and title attributes added to
     * the fieldDef element. This exercises the getConfigXML method on the class under test.</p>
     * 
     * @param xmlNode - an input node
     * @return the input node with attributes added
     * @throws TransformerException
     */
    private Node configureRuleAttribute(Node xmlNode, KualiXmlAttribute myAttribute) throws TransformerException {
        ExtensionDefinition.Builder extensionDefinition = ExtensionDefinition.Builder.create("fakeName", "fakeType", "fakeResourceDescriptor");

        StringWriter xmlBuffer = new StringWriter();
        Source source = new DOMSource(xmlNode);
        Result result = new StreamResult(xmlBuffer);
        TransformerFactory.newInstance().newTransformer().transform(source, result);

        extensionDefinition.getConfiguration().put(KewApiConstants.ATTRIBUTE_XML_CONFIG_DATA, new String(xmlBuffer.getBuffer()));

        if (LOG.isDebugEnabled()) {
            LOG.debug("This is the XML that was added to the attribute");
            LOG.debug(new String(xmlBuffer.getBuffer()));
            StringWriter xmlBuffer2 = new StringWriter();
            Source source2 = new DOMSource(xmlNode);
            Result result2 = new StreamResult(xmlBuffer2);
            TransformerFactory.newInstance().newTransformer().transform(source2, result2);
            LOG.debug("This is the XML that was returned from the ruleAttribute");
            LOG.debug(new String(xmlBuffer2.getBuffer()));
        }
        return myAttribute.getConfigXML(extensionDefinition.build());
    }

    /**
     * compares the label from the test to the expected, or not expected, value for all of the rule attributes in the file
     *
     * <p>The inputSource file should be as close to the production version as possible, as described by the class comments. It
     * accepts the string to test against as a parameter.</p>
     * 
     * @param testString
     * @param attributeXml
     * @param configNodeName
     */
    private boolean confirmLabels(String testString, String attributeXml, String configNodeName) {
        boolean testFailed = false;
        String theTitle = "";
        String theName = "";
        String attributeName = "";
        try {
            NodeList tempList = (NodeList) myXPath.evaluate("//ruleAttribute", new InputSource(new StringReader(attributeXml)), XPathConstants.NODESET);
            for (int i = 0; i < tempList.getLength(); i++) { // loop over ruleattributes
                Node originalNode = tempList.item(i);
                Set ruleAttributeFieldDefNames = new HashSet();
                Set ruleAttributeFieldDefTitles = new HashSet();
                attributeName = (String) myXPath.evaluate(WorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + "name", originalNode, XPathConstants.STRING);
                Node classNameNode = (Node) myXPath.evaluate(WorkflowUtils.XSTREAM_MATCH_RELATIVE_PREFIX + "className", originalNode, XPathConstants.NODE);
                if ((classNameNode != null) && (classNameNode.getFirstChild() != null)) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Checking attribute with name '" + attributeName + "'");
                    }
                    KualiXmlAttribute myAttribute = (KualiXmlAttribute) GlobalResourceLoader.getObject(new ObjectDefinition(classNameNode.getFirstChild().getNodeValue()));
                    Node xmlNode = configureRuleAttribute(originalNode, myAttribute);
                    NamedNodeMap fieldDefAttributes = null;
                    String potentialFailMessage = "";

                    try {
                        NodeList xmlNodeList = (NodeList) myXPath.evaluate("//fieldDef", xmlNode, XPathConstants.NODESET);

                        for (int j = 0; j < xmlNodeList.getLength(); j++) {
                            Node fieldDefXmlNode = xmlNodeList.item(j);
                            fieldDefAttributes = fieldDefXmlNode.getAttributes();

                            theTitle = fieldDefAttributes.getNamedItem("title").getNodeValue();// Making sure they are clean
                            theName = fieldDefAttributes.getNamedItem("name").getNodeValue();
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(attributeName);
                                LOG.debug("name=" + theName + "   title=" + theTitle);
                            }
                            if (ruleAttributeFieldDefNames.contains(theName)) {
                                // names of fieldDefs inside a single attribute must be unique
                                potentialFailMessage = "Each fieldDef name on a single attribute must be unique and the fieldDef name '" + theName + "' already exists on the attribute '" + attributeName + "'";
                                Assert.fail(potentialFailMessage);
                            }
                            else {
                                ruleAttributeFieldDefNames.add(theName);
                            }
                            if (testString.equals(KualiXmlAttributeHelper.notFound)) {
                                potentialFailMessage = "Each fieldDef title should be a valid value and currently the title for attribute '" + attributeName + "' is '" + theTitle + "'";
                                Assert.assertFalse(potentialFailMessage, theTitle.equals(testString));
                                if (ruleAttributeFieldDefTitles.contains(theTitle)) {
                                    /*
                                     * Titles of fieldDefs inside a single attribute should be unique in the normal case. Having two
                                     * fields with the same label would certainly confuse the user. However, due to the way the
                                     * confirmSource test works, all the titles/labels must be the same. So only run this check when
                                     * not in the confirmSource test.
                                     */
                                    potentialFailMessage = "Each fieldDef title on a single attribute must be unique and the fieldDef title '" + theTitle + "' already exists on the attribute '" + attributeName + "'";
                                    Assert.fail(potentialFailMessage);
                                }
                                else {
                                    ruleAttributeFieldDefTitles.add(theTitle);
                                }
                            }
                            else {
                                potentialFailMessage = "For attribute '" + attributeName + "' the title should have been '" + testString + "' but was actually '" + theTitle + "'";
                                Assert.assertEquals(potentialFailMessage, testString, theTitle);
                            }
                        }
                    }
                    catch (AssertionError afe) {
                        LOG.warn("Assertion Failed for attribute '" + attributeName + "' with error " + potentialFailMessage, afe);
                        testFailed = true;
                    }
                    finally {
                        attributeName = "";
                    }
                }
                else {
                    throw new RuntimeException("Could not find class for attribute named '" + attributeName + "'");
                }
            }
        }
        catch (Exception e) {
            LOG.error("General Exception thrown for attribute '" + attributeName + "'", e);
            testFailed = true;
        }
        return testFailed;
    }

    /**
     * confirms that the labels are coming from the data dictionary
     *
     * <p>This is done by modifying all the dictionary values
     * programatically to a nonsense value. It then rebuilds the Hash Table and runs confirmLabels() to make sure the labels have
     * changed.</p>
     */
    @Test public void testLabelSource() {
        DataDictionaryService myDDService = KRADServiceLocatorWeb.getDataDictionaryService();
        String nonsenseString = "BananaRama";
        for (Object tempEntity : myDDService.getDataDictionary().getBusinessObjectEntries().values()) {

            for ( AttributeDefinition attribute : ((BusinessObjectEntry) tempEntity).getAttributes() ) {
                attribute.setLabel(nonsenseString);
                attribute.setShortLabel(nonsenseString);
            }

        }
        for (Object tempEntity : myDDService.getDataDictionary().getDocumentEntries().values()) {

            for ( AttributeDefinition attribute : ((DocumentEntry) tempEntity).getAttributes() ) {
                attribute.setLabel(nonsenseString);
                attribute.setShortLabel(nonsenseString);
            }

        }

        boolean failed = false;
        Assert.assertFalse("At least one label was incorrect", failed);
        // test rule xml attributes
        failed |= confirmLabels(nonsenseString, ruleAttributeXml, RULE_ATTRIBUTE_CONFIG_NODE_NAME);

        // test search xml attributes
        failed |= confirmLabels(nonsenseString, searchAttributeXml, SEARCH_ATTRIBUTE_CONFIG_NODE_NAME);

        Assert.assertFalse("At least one label was incorrect", failed);
    }

}
