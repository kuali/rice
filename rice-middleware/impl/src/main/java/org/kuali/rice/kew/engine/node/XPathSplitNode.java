/*
 * Copyright 2006-2015 The Kuali Foundation
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

package org.kuali.rice.kew.engine.node;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.RouteHelper;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A SplitNode {@link SplitNode} implementation which chooses branches to execute using XPath.
 *
 * This implementation of SplitNode expects zero or more xpath nodes which specify a branch name and an
 * XPath expression which should resolve to a boolean value. It checks each xpath node independently and if
 * it resolves to true the document will route down that branch in the workflow.  If the xpath resolves to false
 * the branch will be skipped.  This allows for routing to multiple branches if the xpath expressions allow it.
 * If none of the xpath nodes are matched then it routes to one or more default branches specified in the
 * configuration.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class XPathSplitNode implements SplitNode {

    private static final Logger LOG = Logger.getLogger(XPathSplitNode.class);

    private XPath xPath;
    private NodeList xpathDecisions;
    private NodeList defaultDecisions;

    @Override
    public SplitResult process(RouteContext context, RouteHelper helper) throws Exception {
        loadConfiguration(context);
        List<String> branchNames = new ArrayList<String>();
        if(xpathDecisions != null) {
            for(int i = 0; i < xpathDecisions.getLength(); i++) {
                Node xpathDecision = xpathDecisions.item(i);
                String xpathExpression = xpathDecision.getAttributes().getNamedItem("expression").getNodeValue();
                String branchName = xpathDecision.getAttributes().getNamedItem("branchName").getNodeValue();
                if((Boolean)getXPath().evaluate(xpathExpression, context.getDocumentContent().getDocument(), XPathConstants.BOOLEAN)) {
                    branchNames.add(branchName);
                }
            }
        }

        if(CollectionUtils.isEmpty(branchNames) && defaultDecisions != null) {
            for(int i = 0; i < defaultDecisions.getLength(); i++) {
                Node xpathDecision = defaultDecisions.item(i);
                String branchName = xpathDecision.getAttributes().getNamedItem("branchName").getNodeValue();
                branchNames.add(branchName);
            }
        }
        return new SplitResult(branchNames);
    }

    private void loadConfiguration(RouteContext context) {
        try {
            String contentFragment = context.getNodeInstance().getRouteNode().getContentFragment();
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document nodeContent = db.parse(new InputSource(new StringReader(contentFragment)));

            this.xpathDecisions = (NodeList)getXPath().evaluate("//split/branchDecisions/xpath", nodeContent, XPathConstants.NODESET);
            this.defaultDecisions = (NodeList)getXPath().evaluate("//split/branchDecisions/default", nodeContent, XPathConstants.NODESET);
        } catch (ParserConfigurationException e) {
            LOG.error("Caught parser exception processing XPathSplitNode configuration", e);
        } catch (SAXException e) {
            LOG.error("Caught SAX exception processing XPathSplitNode configuration", e);
        } catch (IOException e) {
            LOG.error("Caught IO exception processing XPathSplitNode configuration", e);
        } catch (XPathExpressionException e) {
            LOG.error("Caught XPath exception processing XPathSplitNode configuration", e);
        }
    }

    public XPath getXPath() {
        if(this.xPath == null) {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            this.xPath = xPathFactory.newXPath();
        }
        return xPath;
    }
}