/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeAttribute;
import edu.iu.uis.eden.doctype.DocumentTypePolicy;
import edu.iu.uis.eden.doctype.DocumentTypePolicyEnum;
import edu.iu.uis.eden.engine.node.ActivationTypeEnum;
import edu.iu.uis.eden.engine.node.BranchPrototype;
import edu.iu.uis.eden.engine.node.Process;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.exception.InvalidWorkgroupException;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * A parser for parsing an XML file into {@link DocumentType}s.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentTypeXmlParser implements XmlConstants {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentTypeXmlParser.class);

    /**
     * Default route node activation type to use if omitted
     */
    private static final String DEFAULT_ACTIVATION_TYPE = "S";

    public List docTypeRouteNodes;
    private Map nodesMap;
    private XPath xpath;
    private Workgroup defaultExceptionWorkgroup;
    private static final String NEXT_NODE_EXP = "./@nextNode";
    private static final String PARENT_NEXT_NODE_EXP = "../@nextNode";
    private static final String PROCESS_NAME_ATTR = "processName";

    public List parseDocumentTypes(InputStream input) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, WorkflowException, TransformerException {

        List documentTypeBeans = new ArrayList();

    	Document routeDocument=XmlHelper.trimXml(input);
        xpath = XPathHelper.newXPath();
        NodeList documentTypeNodes = (NodeList) xpath.evaluate("/data/documentTypes/documentType", routeDocument, XPathConstants.NODESET);
        for (int i = 0; i < documentTypeNodes.getLength(); i++) {
            DocumentType documentType = getDocumentType(documentTypeNodes.item(i));
            NodeList policiesList = (NodeList) xpath.evaluate("./policies", documentTypeNodes.item(i), XPathConstants.NODESET);
            if (policiesList.getLength() > 1) {
                throw new InvalidXmlException("More than one policies node is present in a document type node");
            }
            if (policiesList.getLength() > 0) {
                NodeList policyNodes = (NodeList) xpath.evaluate("./policy", policiesList.item(0), XPathConstants.NODESET);
                documentType.setPolicies(getDocumentTypePolicies(policyNodes, documentType));
            }

            NodeList attributeList = (NodeList) xpath.evaluate("./attributes", documentTypeNodes.item(i), XPathConstants.NODESET);
            if (attributeList.getLength() > 1) {
                throw new InvalidXmlException("More than one attributes node is present in a document type node");
            }

            if (attributeList.getLength() > 0) {
                NodeList attributeNodes = (NodeList) xpath.evaluate("./attribute", attributeList.item(0), XPathConstants.NODESET);
                documentType.setDocumentTypeAttributes(getDocumentTypeAttributes(attributeNodes, documentType));
            }


            NodeList securityList = (NodeList) xpath.evaluate("./security", documentTypeNodes.item(i), XPathConstants.NODESET);
            if (securityList.getLength() > 1) {
                throw new InvalidXmlException("More than one security node is present in a document type node");
            }

            if (securityList.getLength() > 0) {
               try {
                 Node securityNode = securityList.item(0);
                 String securityText = XmlHelper.writeNode(securityNode);
                 documentType.setDocumentTypeSecurityXml(securityText);
               }
               catch (Exception e) {
                 throw new InvalidXmlException(e);
               }
            }

            parseStructure(documentTypeNodes.item(i), routeDocument, documentType, new RoutePathContext());

            LOG.debug("Saving document type " + documentType.getName());
            WorkflowUser user = null;
            routeDocumentType(documentType, user);
            documentTypeBeans.add(documentType);
        }

        return documentTypeBeans;
    }

    private void routeDocumentType(DocumentType documentType, WorkflowUser user) {
        KEWServiceLocator.getDocumentTypeService().versionAndSave(documentType);
    }

    private DocumentType getDocumentType(Node documentTypeNode) throws XPathExpressionException, InvalidWorkgroupException, InvalidXmlException {
        DocumentType documentType = new DocumentType();
        try {
            documentType.setName((String) xpath.evaluate("./name", documentTypeNode, XPathConstants.STRING));
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type name", xpee);
            throw xpee;
        }
        try {
            documentType.setDescription((String) xpath.evaluate("./description", documentTypeNode, XPathConstants.STRING));
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type description", xpee);
            throw xpee;
        }
        try {
            documentType.setLabel((String) xpath.evaluate("./label", documentTypeNode, XPathConstants.STRING));
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type label", xpee);
            throw xpee;
        }
        try {
            if (((Boolean) xpath.evaluate("./postProcessorName", documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
                documentType.setPostProcessorName((String) xpath.evaluate("./postProcessorName", documentTypeNode, XPathConstants.STRING));
            }
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type postProcessorName", xpee);
            throw xpee;
        }
        String docHandler = null;
        try {
            docHandler = (String) xpath.evaluate("./docHandler", documentTypeNode, XPathConstants.STRING);
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type docHandler", xpee);
            throw xpee;
        }

        if (Utilities.isEmpty(docHandler)) {
            throw new InvalidXmlException("docHandler element must be specified");
        }

        documentType.setDocHandlerUrl(docHandler);


        String messageEntity = null; // by default set this to null and let the system sort out what the "default" is
        if (((Boolean) xpath.evaluate("./" + MESSAGE_ENTITY, documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
            try {
                messageEntity = (String) xpath.evaluate("./" + MESSAGE_ENTITY, documentTypeNode, XPathConstants.STRING);
            } catch (XPathExpressionException xpee) {
                LOG.error("Error obtaining document type messageEntity", xpee);
                throw xpee;
            }
        }

        documentType.setMessageEntity(messageEntity);

        try {
        	if (((Boolean) xpath.evaluate("./" + NOTIFICATION_FROM_ADDRESS, documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
                documentType.setNotificationFromAddress((String) xpath.evaluate("./" + NOTIFICATION_FROM_ADDRESS, documentTypeNode, XPathConstants.STRING));
            }
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type " + NOTIFICATION_FROM_ADDRESS, xpee);
            throw xpee;
        }

        try {
            if (XmlHelper.pathExists(xpath, "./" + CUSTOM_EMAIL_STYLESHEET, documentTypeNode)) {
                documentType.setCustomEmailStylesheet((String) xpath.evaluate("./" + CUSTOM_EMAIL_STYLESHEET, documentTypeNode, XPathConstants.STRING));
            }
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type " + CUSTOM_EMAIL_STYLESHEET, xpee);
            throw xpee;
        }

        documentType.setCurrentInd(Boolean.TRUE);

        String exceptionWgName;
        try {
            exceptionWgName = (String) xpath.evaluate("./defaultExceptionWorkgroupName", documentTypeNode, XPathConstants.STRING);
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type defaultExceptionWorkgroupName", xpee);
            throw xpee;
        }

        if (! Utilities.isEmpty(exceptionWgName)) {
            // allow core config parameter replacement in documenttype workgroups
            exceptionWgName = Utilities.substituteConfigParameters(exceptionWgName);
        	Workgroup exceptionWg = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId(exceptionWgName), true);
        	if(exceptionWg == null) {
        		throw new WorkflowRuntimeException("Exception workgroup name " + exceptionWgName + " does not exist");
        	}
            documentType.setDefaultExceptionWorkgroup(exceptionWg);
            defaultExceptionWorkgroup = exceptionWg;
        }

        try {
            if (((Boolean) xpath.evaluate("./active", documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
                documentType.setActiveInd(Boolean.valueOf((String) xpath.evaluate("./active", documentTypeNode, XPathConstants.STRING)));
            } else {
                documentType.setActiveInd(Boolean.TRUE);
            }
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type active flag", xpee);
            throw xpee;
        }
        boolean parentElementExists = false;
        try {
            parentElementExists = XmlHelper.pathExists(xpath, "./parent", documentTypeNode);
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type parent", xpee);
            throw xpee;
        }
        if (parentElementExists) {
            String parentDocumentTypeName = null;
            try {
                parentDocumentTypeName = (String) xpath.evaluate("./parent", documentTypeNode, XPathConstants.STRING);
            } catch (XPathExpressionException xpee) {
                LOG.error("Error obtaining document type parent", xpee);
                throw xpee;
            }
            DocumentType parentDocumentType = KEWServiceLocator.getDocumentTypeService().findByName(parentDocumentTypeName);
            if (parentDocumentType == null) {
                throw new InvalidXmlException("Invalid parent document type: '" + parentDocumentTypeName + "'");
            }
            documentType.setDocTypeParentId(parentDocumentType.getDocumentTypeId());
        }

        try {
            if (((Boolean) xpath.evaluate("./superUserWorkgroupName", documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
                String wg;
                try {
                    wg = (String) xpath.evaluate("./superUserWorkgroupName", documentTypeNode, XPathConstants.STRING);
                } catch (XPathExpressionException xpee) {
                    LOG.error("Error obtaining document type superUserWorkgroupName", xpee);
                    throw xpee;
                }
                // allow core config parameter replacement in documenttype workgroups
                wg = Utilities.substituteConfigParameters(wg);
                Workgroup suWorkgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId(wg), true);
                if (suWorkgroup == null) {
                    throw new InvalidWorkgroupException("Workgroup could not be found: " + wg);
                }
                documentType.setSuperUserWorkgroupNoInheritence(suWorkgroup);
            }
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type superUserWorkgroupName", xpee);
            throw xpee;
        }
        String blanketWorkGroupName = null;
    	String blanketApprovePolicy = null;
        try {
        	if (((Boolean) xpath.evaluate("./blanketApproveWorkgroupName", documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
                blanketWorkGroupName =(String) xpath.evaluate("./blanketApproveWorkgroupName", documentTypeNode, XPathConstants.STRING);
        }
        	if (((Boolean) xpath.evaluate("./blanketApprovePolicy", documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
                    blanketApprovePolicy =(String) xpath.evaluate("./blanketApprovePolicy", documentTypeNode, XPathConstants.STRING);
            }
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type blanketApproveWorkgroupName", xpee);
            throw xpee;
        }
        if (!StringUtils.isBlank(blanketWorkGroupName) && !StringUtils.isBlank(blanketApprovePolicy)){
        	throw new InvalidXmlException("Only one of blanket approve name need to be set");
        } else if (!StringUtils.isBlank(blanketWorkGroupName)){
            // allow core config parameter replacement in documenttype workgroups
            blanketWorkGroupName = Utilities.substituteConfigParameters(blanketWorkGroupName);
        	Workgroup blanketAppWorkgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId(blanketWorkGroupName), true);
        	if (blanketAppWorkgroup == null) {
        		throw new InvalidWorkgroupException("The blanket approve workgroup " + blanketWorkGroupName + " does not exist");
        	}
        	documentType.setBlanketApproveWorkgroup(blanketAppWorkgroup);
        } else if (!StringUtils.isBlank(blanketApprovePolicy)){
        	documentType.setBlanketApprovePolicy(blanketApprovePolicy);
        }
        try {
            if (((Boolean) xpath.evaluate("./routingVersion", documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
                String version;
                try {
                    version = (String) xpath.evaluate("./routingVersion", documentTypeNode, XPathConstants.STRING);
                } catch (XPathExpressionException xpee) {
                    LOG.error("Error obtaining document type routingVersion", xpee);
                    throw xpee;
                }
                if (!(version.equals(EdenConstants.ROUTING_VERSION_ROUTE_LEVEL) || version.equals(EdenConstants.ROUTING_VERSION_NODAL))) {
                    throw new WorkflowRuntimeException("Invalid routing version on document type: " + version);
                }
                documentType.setRoutingVersion(version);
            }
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type routingVersion", xpee);
            throw xpee;
        }

        return documentType;
    }

    private void parseStructure(Node documentTypeNode, Document routeDocument, DocumentType documentType, RoutePathContext context) throws XPathExpressionException, InvalidXmlException, InvalidWorkgroupException, TransformerException {
        // TODO have a validation function that takes an xpath statement and blows chunks if that
        // statement returns false

        NodeList processNodes;

        try {
            if (((Boolean) xpath.evaluate("./routePaths/routePath", documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
                processNodes = (NodeList) xpath.evaluate("./routePaths/routePath", documentTypeNode, XPathConstants.NODESET);
            } else {
                return;
            }
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type routePaths", xpee);
            throw xpee;
        }

        createProcesses(processNodes, documentType);

        NodeList nodeList = null;
        try {
            nodeList = (NodeList) xpath.evaluate("./routePaths/routePath/start", documentTypeNode, XPathConstants.NODESET);
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type routePath start", xpee);
            throw xpee;
        }
        if (nodeList.getLength() > 1) {
            throw new InvalidXmlException("More than one start node is present in route path");
        } else if (nodeList.getLength() == 0) {
            throw new InvalidXmlException("No start node is present in route path");
        }
        try {
            nodeList = (NodeList) xpath.evaluate(".//routeNodes", documentTypeNode, XPathConstants.NODESET);
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type routeNodes", xpee);
            throw xpee;
        }
        if (nodeList.getLength() > 1) {
            throw new InvalidXmlException("More than one routeNodes node is present in documentType node");
        } else if (nodeList.getLength() == 0) {
            throw new InvalidXmlException("No routeNodes node is present in documentType node");
        }
        Node routeNodesNode = nodeList.item(0);
        checkForOrphanedRouteNodes(documentTypeNode, routeNodesNode);

        // passed validation.
        nodesMap = new HashMap();
        for (int index = 0; index < processNodes.getLength(); index++) {
            Node processNode = processNodes.item(index);
            String startName;
            try {
                startName = (String) xpath.evaluate("./start/@name", processNode, XPathConstants.STRING);
            } catch (XPathExpressionException xpee) {
                LOG.error("Error obtaining routePath start name attribute", xpee);
                throw xpee;
            }
            String processName = EdenConstants.PRIMARY_PROCESS_NAME;
            if (Utilities.isEmpty(startName)) {
                try {
                    startName = (String) xpath.evaluate("./@initialNode", processNode, XPathConstants.STRING);
                } catch (XPathExpressionException xpee) {
                    LOG.error("Error obtaining routePath initialNode attribute", xpee);
                    throw xpee;
                }
                try {
                    processName = (String) xpath.evaluate("./@processName", processNode, XPathConstants.STRING);
                } catch (XPathExpressionException xpee) {
                    LOG.error("Error obtaining routePath processName attribute", xpee);
                    throw xpee;
                }
                if (Utilities.isEmpty(startName)) {
                    throw new InvalidXmlException("Invalid routePath: no initialNode attribute defined!");
                }
            }
            RouteNode routeNode = createRouteNode(null, startName, processNode, routeNodesNode, documentType, context);
            if (routeNode != null) {
                Process process = documentType.getNamedProcess(processName);
                process.setInitialRouteNode(routeNode);
            }
        }

    }

    /**
     * Checks for route nodes that are declared but never used and throws an InvalidXmlException if one is discovered.
     */
    private void checkForOrphanedRouteNodes(Node documentTypeNode, Node routeNodesNode) throws XPathExpressionException, InvalidXmlException {
    	NodeList nodesInPath = (NodeList) xpath.evaluate("./routePaths/routePath//*/@name", documentTypeNode, XPathConstants.NODESET);
    	List<String> nodeNamesInPath = new ArrayList<String>(nodesInPath.getLength());
    	for (int index = 0; index < nodesInPath.getLength(); index++) {
    		Node nameNode = nodesInPath.item(index);
    		nodeNamesInPath.add(nameNode.getNodeValue());
    	}

    	NodeList declaredNodes = (NodeList) xpath.evaluate("./*/@name", routeNodesNode, XPathConstants.NODESET);
    	List<String> declaredNodeNames = new ArrayList<String>(declaredNodes.getLength());
    	for (int index = 0; index < declaredNodes.getLength(); index++) {
    		Node nameNode = declaredNodes.item(index);
    		declaredNodeNames.add(nameNode.getNodeValue());
    	}

    	// now compare the declared nodes to the ones actually used
    	List<String> orphanedNodes = new ArrayList<String>();
    	for (String declaredNode : declaredNodeNames) {
    		boolean foundNode = false;
    		for (String nodeInPath : nodeNamesInPath) {
    			if (nodeInPath.equals(declaredNode)) {
    				foundNode = true;
    				break;
    			}
    		}
    		if (!foundNode) {
    			orphanedNodes.add(declaredNode);
    		}
    	}
    	if (!orphanedNodes.isEmpty()) {
    		String message = "The following nodes were declared but never used: ";
    		for (Iterator iterator = orphanedNodes.iterator(); iterator.hasNext();) {
				String orphanedNode = (String) iterator.next();
				message += orphanedNode + (iterator.hasNext() ? ", " : "");
			}
    		throw new InvalidXmlException(message);
    	}
    }

    private void createProcesses(NodeList processNodes, DocumentType documentType) {
        for (int index = 0; index < processNodes.getLength(); index++) {
            Node processNode = processNodes.item(index);
            NamedNodeMap attributes = processNode.getAttributes();
            Node processNameNode = attributes.getNamedItem(PROCESS_NAME_ATTR);
            String processName = (processNameNode == null ? null : processNameNode.getNodeValue());
            Process process = new Process();
            if (Utilities.isEmpty(processName)) {
                process.setInitial(true);
                process.setName(EdenConstants.PRIMARY_PROCESS_NAME);
            } else {
                process.setInitial(false);
                process.setName(processName);
            }
            process.setDocumentType(documentType);
            documentType.addProcess(process);
        }
    }

    private RouteNode createRouteNode(RouteNode previousRouteNode, String nodeName, Node routePathNode, Node routeNodesNode, DocumentType documentType, RoutePathContext context) throws XPathExpressionException, InvalidXmlException, InvalidWorkgroupException, TransformerException {
        if (nodeName != null) {
            Node currentNode;
            try {
                currentNode = (Node) xpath.evaluate(".//*[@name = '" + nodeName + "']", routePathNode, XPathConstants.NODE);
            } catch (XPathExpressionException xpee) {
                LOG.error("Error obtaining routePath for routeNode", xpee);
                throw xpee;
            }
            if (currentNode == null) {
                String message = "Next node '" + nodeName + "' for node '" + previousRouteNode.getRouteNodeName() + "' not found!";
                LOG.error(message);
                throw new InvalidXmlException(message);
            }
            boolean nodeIsABranch;
            try {
                nodeIsABranch = ((Boolean) xpath.evaluate("self::node()[local-name() = 'branch']", currentNode, XPathConstants.BOOLEAN)).booleanValue();
            } catch (XPathExpressionException xpee) {
                LOG.error("Error testing whether node is a branch", xpee);
                throw xpee;
            }
            if (nodeIsABranch) {
                throw new InvalidXmlException("Next node cannot be a branch node");
            }

            String localName;
            try {
                localName = (String) xpath.evaluate("local-name(.)", currentNode, XPathConstants.STRING);
            } catch (XPathExpressionException xpee) {
                LOG.error("Error obtaining node local-name", xpee);
                throw xpee;
            }
            RouteNode currentRouteNode = null;
            if (nodesMap.containsKey(nodeName)) {
                currentRouteNode = (RouteNode) nodesMap.get(nodeName);
            } else {
                String nodeExpression = ".//*[@name='" + nodeName + "']";
                currentRouteNode = makeRouteNodePrototype(nodeName, nodeExpression, routeNodesNode, documentType, context);
            }

            if ("split".equalsIgnoreCase(localName)) {
                getSplitNextNodes(currentNode, routePathNode, currentRouteNode, routeNodesNode, documentType, context);
            }

            if (previousRouteNode != null) {
                previousRouteNode.getNextNodes().add(currentRouteNode);
                nodesMap.put(previousRouteNode.getRouteNodeName(), previousRouteNode);
                currentRouteNode.getPreviousNodes().add(previousRouteNode);
            }

            String nextNodeName = null;
            boolean hasNextNodeAttrib;
            try {
                hasNextNodeAttrib = ((Boolean) xpath.evaluate(NEXT_NODE_EXP, currentNode, XPathConstants.BOOLEAN)).booleanValue();
            } catch (XPathExpressionException xpee) {
                LOG.error("Error obtaining node nextNode attrib", xpee);
                throw xpee;
            }
            if (hasNextNodeAttrib) {
                // if the node has a nextNode but is not a split node, the nextNode is used for its node
                if (!"split".equalsIgnoreCase(localName)) {
                    try {
                        nextNodeName = (String) xpath.evaluate(NEXT_NODE_EXP, currentNode, XPathConstants.STRING);
                    } catch (XPathExpressionException xpee) {
                        LOG.error("Error obtaining node nextNode attrib", xpee);
                        throw xpee;
                    }
                    createRouteNode(currentRouteNode, nextNodeName, routePathNode, routeNodesNode, documentType, context);
                } else {
                    // if the node has a nextNode but is a split node, the nextNode must be used for that split node's join node
                    nodesMap.put(currentRouteNode.getRouteNodeName(), currentRouteNode);
                }
            } else {
                // if the node has no nextNode of its own and is not a join which gets its nextNode from its parent split node
                if (!"join".equalsIgnoreCase(localName)) {
                    nodesMap.put(currentRouteNode.getRouteNodeName(), currentRouteNode);
                    // if join has a parent nextNode (on its split node) and join has not already walked this path
                } else {
                    boolean parentHasNextNodeAttrib;
                    try {
                        parentHasNextNodeAttrib = ((Boolean) xpath.evaluate(PARENT_NEXT_NODE_EXP, currentNode, XPathConstants.BOOLEAN)).booleanValue();
                    } catch (XPathExpressionException xpee) {
                        LOG.error("Error obtaining parent node nextNode attrib", xpee);
                        throw xpee;
                    }
                    if (parentHasNextNodeAttrib && !nodesMap.containsKey(nodeName)) {
                        try {
                            nextNodeName = (String) xpath.evaluate(PARENT_NEXT_NODE_EXP, currentNode, XPathConstants.STRING);
                        } catch (XPathExpressionException xpee) {
                            LOG.error("Error obtaining parent node nextNode attrib", xpee);
                            throw xpee;
                        }
                        createRouteNode(currentRouteNode, nextNodeName, routePathNode, routeNodesNode, documentType, context);
                    } else {
                        // if join's parent split node does not have a nextNode
                        nodesMap.put(currentRouteNode.getRouteNodeName(), currentRouteNode);
                    }
                }
            }
            return currentRouteNode;
        }
        return null;
    }

    private void getSplitNextNodes(Node splitNode, Node routePathNode, RouteNode splitRouteNode, Node routeNodesNode, DocumentType documentType, RoutePathContext context) throws XPathExpressionException, InvalidXmlException, InvalidWorkgroupException, TransformerException {
        NodeList splitBranchNodes;
        try {
            splitBranchNodes = (NodeList) xpath.evaluate("./branch", splitNode, XPathConstants.NODESET);
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining split node branch", xpee);
            throw xpee;
        }
        for (int i = 0; i < splitBranchNodes.getLength(); i++) {
            String branchName;
            try {
                branchName = (String) xpath.evaluate("./@name", splitBranchNodes.item(i), XPathConstants.STRING);
            } catch (XPathExpressionException xpee) {
                LOG.error("Error obtaining branch name attribute", xpee);
                throw xpee;
            }
            String name;
            try {
                name = (String) xpath.evaluate("./*[1]/@name", splitBranchNodes.item(i), XPathConstants.STRING);
            } catch (XPathExpressionException xpee) {
                LOG.error("Error obtaining first split branch node name", xpee);
                throw xpee;
            }
            context.branch = new BranchPrototype();
            context.branch.setName(branchName);

            createRouteNode(splitRouteNode, name, routePathNode, routeNodesNode, documentType, context);
        }
    }

    private RouteNode makeRouteNodePrototype(String nodeName, String nodeExpression, Node routeNodesNode, DocumentType documentType, RoutePathContext context) throws XPathExpressionException, InvalidWorkgroupException, InvalidXmlException, TransformerException {
        NodeList nodeList;
        try {
            nodeList = (NodeList) xpath.evaluate(nodeExpression, routeNodesNode, XPathConstants.NODESET);
        } catch (XPathExpressionException xpee) {
            LOG.error("Error evaluating node expression: '" + nodeExpression + "'");
            throw xpee;
        }
        if (nodeList.getLength() > 1) {
            throw new InvalidXmlException("More than one node under routeNodes has the same name of '" + nodeName + "'");
        }
        if (nodeList.getLength() == 0) {
            throw new InvalidXmlException("No node definition was found with the name '" + nodeName + "'");
        }
        Node node = nodeList.item(0);
        RouteNode routeNode = new RouteNode();
        routeNode.setDocumentType(documentType);
        routeNode.setRouteNodeName((String) xpath.evaluate("./@name", node, XPathConstants.STRING));
        routeNode.setContentFragment(XmlHelper.writeNode(node));
        if (XmlHelper.pathExists(xpath, "./activationType", node)) {
            routeNode.setActivationType(ActivationTypeEnum.parse((String) xpath.evaluate("./activationType", node, XPathConstants.STRING)).getCode());
        } else {
            routeNode.setActivationType(DEFAULT_ACTIVATION_TYPE);
        }

        Workgroup exceptionWorkgroup = defaultExceptionWorkgroup;
        String exceptionWorkgroupName = (String) xpath.evaluate("./exceptionWorkgroupName", node, XPathConstants.STRING);
        if (Utilities.isEmpty(exceptionWorkgroupName)) {
        	// for backward compatibility we also need to be able to support exceptionWorkgroup
        	exceptionWorkgroupName = (String) xpath.evaluate("./exceptionWorkgroup", node, XPathConstants.STRING);
        }
        if (Utilities.isEmpty(exceptionWorkgroupName)) {
        	if (routeNode.getDocumentType().getDefaultExceptionWorkgroup() != null) {
        		exceptionWorkgroupName = routeNode.getDocumentType().getDefaultExceptionWorkgroup().getGroupNameId().getNameId();
        	}
        }
        if (!Utilities.isEmpty(exceptionWorkgroupName)) {
            exceptionWorkgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId(exceptionWorkgroupName), true);
            if (exceptionWorkgroup == null) {
                throw new InvalidWorkgroupException("Could not locate exception workgroup by name " + exceptionWorkgroupName);
            }
        }
        if (exceptionWorkgroup == null) {
            throw new InvalidXmlException("No exception workgroup specified at node " + routeNode.getRouteNodeName() + ".  Either specify an exceptionWorkgroupName for each node or specify the defaultExceptionWorkgroupName.\n" + XmlHelper.jotNode(node));
        }
        routeNode.setExceptionWorkgroupName(exceptionWorkgroup.getGroupNameId().getNameId());
        routeNode.setExceptionWorkgroupId(exceptionWorkgroup.getWorkflowGroupId().getGroupId());
        if (((Boolean) xpath.evaluate("./mandatoryRoute", node, XPathConstants.BOOLEAN)).booleanValue()) {
            routeNode.setMandatoryRouteInd(Boolean.valueOf((String)xpath.evaluate("./mandatoryRoute", node, XPathConstants.STRING)));
        } else {
            routeNode.setMandatoryRouteInd(Boolean.FALSE);
        }
        if (((Boolean) xpath.evaluate("./finalApproval", node, XPathConstants.BOOLEAN)).booleanValue()) {
            routeNode.setFinalApprovalInd(Boolean.valueOf((String)xpath.evaluate("./finalApproval", node, XPathConstants.STRING)));
        } else {
            routeNode.setFinalApprovalInd(Boolean.FALSE);
        }
        if (((Boolean) xpath.evaluate("./ruleTemplate", node, XPathConstants.BOOLEAN)).booleanValue()) {
            String ruleTemplateName = (String) xpath.evaluate("./ruleTemplate", node, XPathConstants.STRING);
            RuleTemplate ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(ruleTemplateName);
            if (ruleTemplate == null) {
                throw new InvalidXmlException("Rule template for node '" + routeNode.getRouteNodeName() + "' not found: " + ruleTemplateName);
            }
            routeNode.setRouteMethodName(ruleTemplateName);
            routeNode.setRouteMethodCode(EdenConstants.ROUTE_LEVEL_FLEX_RM);
        } else if (((Boolean) xpath.evaluate("./routeModule", node, XPathConstants.BOOLEAN)).booleanValue()) {
            routeNode.setRouteMethodName((String) xpath.evaluate("./routeModule", node, XPathConstants.STRING));
            routeNode.setRouteMethodCode(EdenConstants.ROUTE_LEVEL_ROUTE_MODULE);
        }

        String nodeType = null;
        if (((Boolean) xpath.evaluate("./type", node, XPathConstants.BOOLEAN)).booleanValue()) {
            nodeType = (String) xpath.evaluate("./type", node, XPathConstants.STRING);
        } else {
            String localName = (String) xpath.evaluate("local-name(.)", node, XPathConstants.STRING);
            if ("start".equalsIgnoreCase(localName)) {
                nodeType = "edu.iu.uis.eden.engine.node.InitialNode";
            } else if ("split".equalsIgnoreCase(localName)) {
                nodeType = "edu.iu.uis.eden.engine.node.SimpleSplitNode";
            } else if ("join".equalsIgnoreCase(localName)) {
                nodeType = "edu.iu.uis.eden.engine.node.SimpleJoinNode";
            } else if ("requests".equalsIgnoreCase(localName)) {
                nodeType = "edu.iu.uis.eden.engine.node.RequestsNode";
            } else if ("process".equalsIgnoreCase(localName)) {
                nodeType = "edu.iu.uis.eden.engine.node.SimpleSubProcessNode";
            }
        }
        if (Utilities.isEmpty(nodeType)) {
            throw new InvalidXmlException("Could not determine node type for the node named '" + routeNode.getRouteNodeName() + "'");
        }
        routeNode.setNodeType(nodeType);

        String localName = (String) xpath.evaluate("local-name(.)", node, XPathConstants.STRING);
        if ("split".equalsIgnoreCase(localName)) {
            context.splitNodeStack.addFirst(routeNode);
        } else if ("join".equalsIgnoreCase(localName) && context.splitNodeStack.size() != 0) {
            // join node should have same branch prototype as split node
            RouteNode splitNode = (RouteNode)context.splitNodeStack.removeFirst();
            context.branch = splitNode.getBranch();
        }
        routeNode.setBranch(context.branch);

        return routeNode;
    }

    private List getDocumentTypePolicies(NodeList documentTypePolicies, DocumentType documentType) throws XPathExpressionException, InvalidXmlException {
        List policies = new ArrayList();
        Set policyNames = new HashSet();

        for (int i = 0; i < documentTypePolicies.getLength(); i++) {
            DocumentTypePolicy policy = new DocumentTypePolicy();
            policy.setDocumentTypeId(documentType.getDocumentTypeId());
            try {
                String policyName = (String) xpath.evaluate("./name", documentTypePolicies.item(i), XPathConstants.STRING);
                policy.setPolicyName(DocumentTypePolicyEnum.lookup(policyName).getName());
            } catch (XPathExpressionException xpee) {
                LOG.error("Error obtaining document type policy name", xpee);
                throw xpee;
            }
            //don't need this it's determined by the doctype bean based on the data - i think
//            if (((Boolean) xpath.evaluate("./inherited", documentTypePolicies.item(i), XPathConstants.BOOLEAN)).booleanValue()) {
//                policy.setInheritedFlag(new Boolean((String) xpath.evaluate("./inherited", documentTypePolicies.item(i), XPathConstants.STRING)));
//            } else {
//                policy.setInheritedFlag(Boolean.FALSE);
//            }
            try {

                if (((Boolean) xpath.evaluate("./value", documentTypePolicies.item(i), XPathConstants.BOOLEAN)).booleanValue()) {
                    policy.setPolicyValue(Boolean.valueOf((String) xpath.evaluate("./value", documentTypePolicies.item(i), XPathConstants.STRING)));
                } else {
                    policy.setPolicyValue(Boolean.FALSE);
                }
            } catch (XPathExpressionException xpee) {
                LOG.error("Error obtaining document type policy value", xpee);
                throw xpee;
            }
            if (!policyNames.add(policy.getPolicyName())) {
                throw new InvalidXmlException("Policy '" + policy.getPolicyName() + "' has already been defined on this document");
            } else {
                policies.add(policy);
            }
        }

        return policies;
    }

    private List getDocumentTypeAttributes(NodeList documentTypeAttributes, DocumentType documentType) throws XPathExpressionException, WorkflowException {
        List attributes = new ArrayList();

        for (int i = 0; i < documentTypeAttributes.getLength(); i++) {
            DocumentTypeAttribute attribute = new DocumentTypeAttribute();
            attribute.setDocumentType(documentType);
            String ruleAttributeName;
            try {
                ruleAttributeName = (String) xpath.evaluate("./name", documentTypeAttributes.item(i), XPathConstants.STRING);
            } catch (XPathExpressionException xpee) {
                LOG.error("Error obtaining rule attribute name", xpee);
                throw xpee;
            }
            RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(ruleAttributeName);
            if (ruleAttribute == null) {
                throw new WorkflowException("Could not find rule attribute: " + ruleAttributeName);
            }
            attribute.setDocumentType(documentType);
            attribute.setRuleAttribute(ruleAttribute);
            attributes.add(attribute);
        }
        return attributes;
    }

    private class RoutePathContext {
        public BranchPrototype branch;
        public LinkedList splitNodeStack = new LinkedList();
    }

}