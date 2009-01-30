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
package org.kuali.rice.kew.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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
import org.kuali.rice.kew.doctype.DocumentTypeAttribute;
import org.kuali.rice.kew.doctype.DocumentTypePolicy;
import org.kuali.rice.kew.doctype.DocumentTypePolicyEnum;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.node.ActivationTypeEnum;
import org.kuali.rice.kew.engine.node.BranchPrototype;
import org.kuali.rice.kew.engine.node.NodeType;
import org.kuali.rice.kew.engine.node.Process;
import org.kuali.rice.kew.engine.node.RoleNode;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.RouteNodeConfigParam;
import org.kuali.rice.kew.exception.InvalidWorkgroupException;
import org.kuali.rice.kew.exception.InvalidXmlException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.export.ExportDataSet;
import org.kuali.rice.kew.role.RoleRouteModule;
import org.kuali.rice.kew.rule.FlexRM;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.xmlrouting.XPathHelper;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.util.XmlHelper;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.util.ObjectUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


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
    private KimGroup defaultExceptionWorkgroup;
    private static final String NEXT_NODE_EXP = "./@nextNode";
    private static final String PARENT_NEXT_NODE_EXP = "../@nextNode";
    private static final String PROCESS_NAME_ATTR = "processName";
    
    protected XPath getXPath() {
        if (this.xpath == null) {
            this.xpath = XPathHelper.newXPath();
        }
        return xpath;
    }
    
    public List parseDocumentTypes(InputStream input) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, WorkflowException, TransformerException {
        Document routeDocument=XmlHelper.trimXml(input);
        Map documentTypesByName = new HashMap();
        for (Iterator iterator = parseStandardDocumentTypes(routeDocument).iterator(); iterator.hasNext();) {
            DocumentType type = (DocumentType) iterator.next();
            documentTypesByName.put(type.getName(), type);
        }
        for (Iterator iterator = parseRoutingDocumentTypes(routeDocument).iterator(); iterator.hasNext();) {
            DocumentType type = (DocumentType) iterator.next();
            documentTypesByName.put(type.getName(), type);
        }
        return new ArrayList(documentTypesByName.values());
    }

    private List parseRoutingDocumentTypes(Document routeDocument) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, WorkflowException, TransformerException {
        List documentTypeBeans = new ArrayList();
//        Document routeDocument=XmlHelper.trimXml(input);
        xpath = XPathHelper.newXPath();
        NodeList documentTypeRoutingNodes = (NodeList) xpath.evaluate("/data/documentTypes/documentTypeRouting", routeDocument, XPathConstants.NODESET);
        for (int i = 0; i < documentTypeRoutingNodes.getLength(); i++) {
            Node documentTypeRoutingNode = documentTypeRoutingNodes.item(i);
            String documentTypeName = getDocumentTypeNameFromNode(documentTypeRoutingNode);
//            // export the document type that exists in the database
//            DocumentType docTypeFromDatabase = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
//            if (ObjectUtils.isNull(docTypeFromDatabase)) {
//                throw new InvalidXmlException("Could not find document type '" + documentTypeName + "' in the database");
//            }
//            ExportDataSet exportDataSet = new ExportDataSet();
//            exportDataSet.getDocumentTypes().add(docTypeFromDatabase);
//            byte[] xmlBytes = KEWServiceLocator.getXmlExporterService().export(exportDataSet);
//            // use the exported document type from the database to generate the new document type
//            Document tempDocument = XmlHelper.trimXml(new BufferedInputStream(new ByteArrayInputStream(xmlBytes)));
//            Node documentTypeNode = (Node) xpath.evaluate("/data/documentTypes/documentType", tempDocument, XPathConstants.NODE);
//            DocumentType newDocumentType = getFullDocumentType(documentTypeNode);
            DocumentType newDocumentType = generateNewDocumentTypeFromExisting(documentTypeName);
            // use the existingDocumentType variable and overwrite the routing information on it before saving it
            setupRoutingVersion(newDocumentType, documentTypeRoutingNode);
            parseStructure(documentTypeRoutingNode, routeDocument, newDocumentType, new RoutePathContext());

            /*   save the newly generated document type which should have both the
             *   new routing data and all the old values of other fields (ex:
             *   description, label, docHandlerUrl) from the database
             */
            LOG.debug("Saving document type " + newDocumentType.getName());
            routeDocumentType(newDocumentType);
            documentTypeBeans.add(newDocumentType);
        }
        return documentTypeBeans;
    }
    
    public DocumentType generateNewDocumentTypeFromExisting(String documentTypeName) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, InvalidWorkgroupException, WorkflowException {
        // export the document type that exists in the database
        DocumentType docTypeFromDatabase = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
        if (ObjectUtils.isNull(docTypeFromDatabase)) {
            throw new InvalidXmlException("Could not find document type '" + documentTypeName + "' in the database");
        }
        ExportDataSet exportDataSet = new ExportDataSet();
        exportDataSet.getDocumentTypes().add(docTypeFromDatabase);
        byte[] xmlBytes = KEWServiceLocator.getXmlExporterService().export(exportDataSet);
        // use the exported document type from the database to generate the new document type
        Document tempDocument = XmlHelper.trimXml(new BufferedInputStream(new ByteArrayInputStream(xmlBytes)));
        Node documentTypeNode = (Node) getXPath().evaluate("/data/documentTypes/documentType", tempDocument, XPathConstants.NODE);
        return getFullDocumentType(documentTypeNode);
    }

    private DocumentType getFullDocumentType(Node documentTypeNode) throws XPathExpressionException, InvalidWorkgroupException, InvalidXmlException, WorkflowException {
        DocumentType documentType = getDocumentType(documentTypeNode);
        NodeList policiesList = (NodeList) xpath.evaluate("./policies", documentTypeNode, XPathConstants.NODESET);
        if (policiesList.getLength() > 1) {
            throw new InvalidXmlException("More than one policies node is present in a document type node");
        }
        if (policiesList.getLength() > 0) {
            NodeList policyNodes = (NodeList) xpath.evaluate("./policy", policiesList.item(0), XPathConstants.NODESET);
            documentType.setPolicies(getDocumentTypePolicies(policyNodes, documentType));
        }

        NodeList attributeList = (NodeList) xpath.evaluate("./attributes", documentTypeNode, XPathConstants.NODESET);
        if (attributeList.getLength() > 1) {
            throw new InvalidXmlException("More than one attributes node is present in a document type node");
        }

        if (attributeList.getLength() > 0) {
            NodeList attributeNodes = (NodeList) xpath.evaluate("./attribute", attributeList.item(0), XPathConstants.NODESET);
            documentType.setDocumentTypeAttributes(getDocumentTypeAttributes(attributeNodes, documentType));
        }


        NodeList securityList = (NodeList) xpath.evaluate("./security", documentTypeNode, XPathConstants.NODESET);
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
        return documentType;
    }

    private List parseStandardDocumentTypes(Document routeDocument) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, WorkflowException, TransformerException {
        List documentTypeBeans = new ArrayList();

//    	Document routeDocument=XmlHelper.trimXml(input);
        xpath = XPathHelper.newXPath();
        NodeList documentTypeNodes = (NodeList) xpath.evaluate("/data/documentTypes/documentType", routeDocument, XPathConstants.NODESET);
        for (int i = 0; i < documentTypeNodes.getLength(); i++) {
            DocumentType documentType = getFullDocumentType(documentTypeNodes.item(i));
//            DocumentType documentType = getDocumentType(documentTypeNodes.item(i));
//            NodeList policiesList = (NodeList) xpath.evaluate("./policies", documentTypeNodes.item(i), XPathConstants.NODESET);
//            if (policiesList.getLength() > 1) {
//                throw new InvalidXmlException("More than one policies node is present in a document type node");
//            }
//            if (policiesList.getLength() > 0) {
//                NodeList policyNodes = (NodeList) xpath.evaluate("./policy", policiesList.item(0), XPathConstants.NODESET);
//                documentType.setPolicies(getDocumentTypePolicies(policyNodes, documentType));
//            }
//
//            NodeList attributeList = (NodeList) xpath.evaluate("./attributes", documentTypeNodes.item(i), XPathConstants.NODESET);
//            if (attributeList.getLength() > 1) {
//                throw new InvalidXmlException("More than one attributes node is present in a document type node");
//            }
//
//            if (attributeList.getLength() > 0) {
//                NodeList attributeNodes = (NodeList) xpath.evaluate("./attribute", attributeList.item(0), XPathConstants.NODESET);
//                documentType.setDocumentTypeAttributes(getDocumentTypeAttributes(attributeNodes, documentType));
//            }
//
//
//            NodeList securityList = (NodeList) xpath.evaluate("./security", documentTypeNodes.item(i), XPathConstants.NODESET);
//            if (securityList.getLength() > 1) {
//                throw new InvalidXmlException("More than one security node is present in a document type node");
//            }
//
//            if (securityList.getLength() > 0) {
//               try {
//                 Node securityNode = securityList.item(0);
//                 String securityText = XmlHelper.writeNode(securityNode);
//                 documentType.setDocumentTypeSecurityXml(securityText);
//               }
//               catch (Exception e) {
//                 throw new InvalidXmlException(e);
//               }
//            }

            parseStructure(documentTypeNodes.item(i), routeDocument, documentType, new RoutePathContext());

            LOG.debug("Saving document type " + documentType.getName());
            routeDocumentType(documentType);
            documentTypeBeans.add(documentType);
        }

        return documentTypeBeans;
    }

    private void routeDocumentType(DocumentType documentType) {
        KEWServiceLocator.getDocumentTypeService().versionAndSave(documentType);
    }
    
    private String getDocumentTypeNameFromNode(Node documentTypeNode) throws XPathExpressionException {
        try {
            return (String) xpath.evaluate("./name", documentTypeNode, XPathConstants.STRING);
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type name", xpee);
            throw xpee;
        }
    }

    private DocumentType getDocumentType(Node documentTypeNode) throws XPathExpressionException, InvalidWorkgroupException, InvalidXmlException {
        DocumentType documentType = new DocumentType();
        String documentTypeName = getDocumentTypeNameFromNode(documentTypeNode); 
        documentType.setName(documentTypeName);
        DocumentType previousDocumentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
//        try {
//            documentType.setName((String) xpath.evaluate("./name", documentTypeNode, XPathConstants.STRING));
//        } catch (XPathExpressionException xpee) {
//            LOG.error("Error obtaining document type name", xpee);
//            throw xpee;
//        }
        try {
            String desc = (String) xpath.evaluate("./" + DESCRIPTION, documentTypeNode, XPathConstants.STRING);
            if (StringUtils.isBlank(desc)) {
                if (previousDocumentType != null && StringUtils.isNotBlank(previousDocumentType.getDescription())) {
                    // keep the same value as before, even if it's not specified here
                    desc = previousDocumentType.getDescription();
                }
            }
            documentType.setDescription(desc);
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type description", xpee);
            throw xpee;
        }
        try {
            String label = (String) xpath.evaluate("./label", documentTypeNode, XPathConstants.STRING);
            if (StringUtils.isBlank(label)) {
            	if (previousDocumentType != null && !StringUtils.isBlank(previousDocumentType.getLabel())) {
            		// keep the same label as before, even if it's not specified here
            		label = previousDocumentType.getLabel();
            	} else {
            		// otherwise set it to undefined
            		label = KEWConstants.DEFAULT_DOCUMENT_TYPE_LABEL;
            	}
            }
            documentType.setLabel(label);
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

        documentType.setDocHandlerUrl(docHandler);


        String serviceNamespace = null; // by default set this to null and let the system sort out what the "default" is
        if (((Boolean) xpath.evaluate("./" + SERVICE_NAMESPACE, documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
            try {
                serviceNamespace = (String) xpath.evaluate("./" + SERVICE_NAMESPACE, documentTypeNode, XPathConstants.STRING);
            } catch (XPathExpressionException xpee) {
                LOG.error("Error obtaining document type ServiceNamespace", xpee);
                throw xpee;
            }
        }

        documentType.setServiceNamespace(serviceNamespace);

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

        String exceptionWg;
        String exceptionWgName;
        String exceptionWgNamespace;
        try {
            exceptionWg = (String) xpath.evaluate("./defaultExceptionWorkgroupName", documentTypeNode, XPathConstants.STRING);
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type defaultExceptionWorkgroupName", xpee);
            throw xpee;
        }


        if (! Utilities.isEmpty(exceptionWg)) {
            // allow core config parameter replacement in documenttype workgroups
            exceptionWg = Utilities.substituteConfigParameters(exceptionWg);
            exceptionWgName = Utilities.parseGroupName(exceptionWg);
            exceptionWgNamespace = Utilities.parseGroupNamespaceCode(exceptionWg);
        	KimGroup exceptionGroup = getIdentityManagementService().getGroupByName(exceptionWgNamespace, exceptionWgName);
        	if(exceptionGroup == null) {
        		throw new WorkflowRuntimeException("Exception workgroup name " + exceptionWgName + " does not exist");
        	}
            documentType.setDefaultExceptionWorkgroup(exceptionGroup);
            defaultExceptionWorkgroup = exceptionGroup;
        }

        try {
            if (((Boolean) xpath.evaluate("./active", documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
                documentType.setActive(Boolean.valueOf((String) xpath.evaluate("./active", documentTypeNode, XPathConstants.STRING)));
            } else {
                documentType.setActive(Boolean.TRUE);
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
                String wgName;
                String wgNamespace;
                try {
                    wg = (String) xpath.evaluate("./superUserWorkgroupName", documentTypeNode, XPathConstants.STRING);
                } catch (XPathExpressionException xpee) {
                    LOG.error("Error obtaining document type superUserWorkgroupName", xpee);
                    throw xpee;
                }

                // allow core config parameter replacement in documenttype workgroups
                wg = Utilities.substituteConfigParameters(wg);
                wgName = Utilities.parseGroupName(wg);
                wgNamespace = Utilities.parseGroupNamespaceCode(wg);
                KimGroup suWorkgroup = getIdentityManagementService().getGroupByName(wgNamespace, wgName);
                if (suWorkgroup == null) {
                    throw new InvalidWorkgroupException("Workgroup could not be found: " + wgName);
                }
                documentType.setSuperUserWorkgroupNoInheritence(suWorkgroup);
            }
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type superUserWorkgroupName", xpee);
            throw xpee;
        }
        String blanketWorkGroup = null;
        String blanketWorkGroupName = null;
        String blanketWorkGroupNamespace = null;
    	String blanketApprovePolicy = null;
        try {
        	if (((Boolean) xpath.evaluate("./blanketApproveWorkgroupName", documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
                blanketWorkGroup =(String) xpath.evaluate("./blanketApproveWorkgroupName", documentTypeNode, XPathConstants.STRING);
        	}
        	if (((Boolean) xpath.evaluate("./blanketApprovePolicy", documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
                    blanketApprovePolicy =(String) xpath.evaluate("./blanketApprovePolicy", documentTypeNode, XPathConstants.STRING);
            }
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type blanketApproveWorkgroupName", xpee);
            throw xpee;
        }
        if (!StringUtils.isBlank(blanketWorkGroup) && !StringUtils.isBlank(blanketApprovePolicy)){
        	throw new InvalidXmlException("Only one of blanket approve name need to be set");
        } else if (!StringUtils.isBlank(blanketWorkGroup)){
            // allow core config parameter replacement in documenttype workgroups
            blanketWorkGroup = Utilities.substituteConfigParameters(blanketWorkGroup);
            blanketWorkGroupName = Utilities.parseGroupName(blanketWorkGroup);
            blanketWorkGroupNamespace = Utilities.parseGroupNamespaceCode(blanketWorkGroup);
            KimGroup blanketAppWorkgroup = getIdentityManagementService().getGroupByName(blanketWorkGroupNamespace, blanketWorkGroupName);
        	if (blanketAppWorkgroup == null) {
        		throw new InvalidWorkgroupException("The blanket approve workgroup " + blanketWorkGroupName + " does not exist");
        	}
        	documentType.setBlanketApproveWorkgroup(blanketAppWorkgroup);
        } else if (!StringUtils.isBlank(blanketApprovePolicy)){
        	documentType.setBlanketApprovePolicy(blanketApprovePolicy);
        }

        try {
            if (((Boolean) xpath.evaluate("./" + REPORTING_WORKGROUP_NAME, documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
                String wg;
                String wgNamespace;
                String wgName;
                try {
                    wg = (String) xpath.evaluate("./" + REPORTING_WORKGROUP_NAME, documentTypeNode, XPathConstants.STRING);
                } catch (XPathExpressionException xpee) {
                    LOG.error("Error obtaining document type " + REPORTING_WORKGROUP_NAME, xpee);
                    throw xpee;
                }
                // allow core config parameter replacement in documenttype workgroups
                wg = Utilities.substituteConfigParameters(wg);
                wgName = Utilities.parseGroupName(wg);
                wgNamespace = Utilities.parseGroupNamespaceCode(wg);
                KimGroup reportingWorkgroup = getIdentityManagementService().getGroupByName(wgNamespace, wgName);
                if (reportingWorkgroup == null) {
                    throw new InvalidWorkgroupException("Reporting workgroup could not be found: " + wgName);
                }
                documentType.setReportingWorkgroup(reportingWorkgroup);
            }
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type " + REPORTING_WORKGROUP_NAME, xpee);
            throw xpee;
        }

        setupRoutingVersion(documentType, documentTypeNode);
//        try {
//            if (((Boolean) xpath.evaluate("./routingVersion", documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
//                String version;
//                try {
//                    version = (String) xpath.evaluate("./routingVersion", documentTypeNode, XPathConstants.STRING);
//                } catch (XPathExpressionException xpee) {
//                    LOG.error("Error obtaining document type routingVersion", xpee);
//                    throw xpee;
//                }
//                if (!(version.equals(KEWConstants.ROUTING_VERSION_ROUTE_LEVEL) || version.equals(KEWConstants.ROUTING_VERSION_NODAL))) {
//                    throw new WorkflowRuntimeException("Invalid routing version on document type: " + version);
//                }
//                documentType.setRoutingVersion(version);
//            }
//        } catch (XPathExpressionException xpee) {
//            LOG.error("Error obtaining document type routingVersion", xpee);
//            throw xpee;
//        }

        return documentType;
    }
    
    private void setupRoutingVersion(DocumentType documentType, Node documentTypeNode) throws XPathExpressionException {
        try {
            if (((Boolean) xpath.evaluate("./routingVersion", documentTypeNode, XPathConstants.BOOLEAN)).booleanValue()) {
                String version;
                try {
                    version = (String) xpath.evaluate("./routingVersion", documentTypeNode, XPathConstants.STRING);
                } catch (XPathExpressionException xpee) {
                    LOG.error("Error obtaining document type routingVersion", xpee);
                    throw xpee;
                }
                if (!(version.equals(KEWConstants.ROUTING_VERSION_ROUTE_LEVEL) || version.equals(KEWConstants.ROUTING_VERSION_NODAL))) {
                    throw new WorkflowRuntimeException("Invalid routing version on document type: " + version);
                }
                documentType.setRoutingVersion(version);
            }
        } catch (XPathExpressionException xpee) {
            LOG.error("Error obtaining document type routingVersion", xpee);
            throw xpee;
        }
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
            String processName = KEWConstants.PRIMARY_PROCESS_NAME;
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
                process.setName(KEWConstants.PRIMARY_PROCESS_NAME);
            } else {
                process.setInitial(false);
                process.setName(processName);
            }
            process.setDocumentType(documentType);
            documentType.addProcess(process);
        }
    }

    private RouteNode createRouteNode(RouteNode previousRouteNode, String nodeName, Node routePathNode, Node routeNodesNode, DocumentType documentType, RoutePathContext context) throws XPathExpressionException, InvalidXmlException, InvalidWorkgroupException, TransformerException {
        if (nodeName == null) return null;

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
            currentRouteNode = makeRouteNodePrototype(localName, nodeName, nodeExpression, routeNodesNode, documentType, context);
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

    private RouteNode makeRouteNodePrototype(String nodeTypeName, String nodeName, String nodeExpression, Node routeNodesNode, DocumentType documentType, RoutePathContext context) throws XPathExpressionException, InvalidWorkgroupException, InvalidXmlException, TransformerException {
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
        // set fields that all route nodes of all types should have defined
        routeNode.setDocumentType(documentType);
        routeNode.setRouteNodeName((String) xpath.evaluate("./@name", node, XPathConstants.STRING));
        routeNode.setContentFragment(XmlHelper.writeNode(node));

        if (XmlHelper.pathExists(xpath, "./activationType", node)) {
            routeNode.setActivationType(ActivationTypeEnum.parse((String) xpath.evaluate("./activationType", node, XPathConstants.STRING)).getCode());
        } else {
            routeNode.setActivationType(DEFAULT_ACTIVATION_TYPE);
        }

        KimGroup exceptionWorkgroup = defaultExceptionWorkgroup;

        String exceptionWg = (String) xpath.evaluate("./exceptionWorkgroupName", node, XPathConstants.STRING);
        String exceptionWorkgroupName = Utilities.parseGroupName(exceptionWg);
        String exceptionWorkgroupNamespace = Utilities.parseGroupNamespaceCode(exceptionWg);

        if (Utilities.isEmpty(exceptionWorkgroupName)) {
        	// for backward compatibility we also need to be able to support exceptionWorkgroup
        	exceptionWg = (String) xpath.evaluate("./exceptionWorkgroup", node, XPathConstants.STRING);
            exceptionWorkgroupName = Utilities.parseGroupName(exceptionWg);
            exceptionWorkgroupNamespace = Utilities.parseGroupNamespaceCode(exceptionWg);
        }
        if (Utilities.isEmpty(exceptionWorkgroupName)) {
        	if (routeNode.getDocumentType().getDefaultExceptionWorkgroup() != null) {
        		exceptionWorkgroupName = routeNode.getDocumentType().getDefaultExceptionWorkgroup().getGroupName();
        		exceptionWorkgroupNamespace = routeNode.getDocumentType().getDefaultExceptionWorkgroup().getNamespaceCode();
        	}
        }
        if (!Utilities.isEmpty(exceptionWorkgroupName)) {
        	exceptionWorkgroup = getIdentityManagementService().getGroupByName(exceptionWorkgroupNamespace, exceptionWorkgroupName);
            if (exceptionWorkgroup == null) {
                throw new InvalidWorkgroupException("Could not locate exception workgroup by name " + exceptionWorkgroupName);
            }
        }
        if (exceptionWorkgroup != null) {
        	routeNode.setExceptionWorkgroupName(exceptionWorkgroup.getGroupName());
            routeNode.setExceptionWorkgroupId(exceptionWorkgroup.getGroupId());
        }

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

        // for every simple child element of the node, store a config parameter of the element name and text content
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n instanceof Element) {
                Element e = (Element) n;
                String name = e.getNodeName();
                String content = XmlHelper.getTextContent(e);
                routeNode.getConfigParams().add(new RouteNodeConfigParam(routeNode, name, content));
            }
        }

        // make sure a default rule selector is set
        Map<String, String> cfgMap = Utilities.getKeyValueCollectionAsMap(routeNode.getConfigParams());
        if (!cfgMap.containsKey(RouteNode.RULE_SELECTOR_CFG_KEY)) {
            routeNode.getConfigParams().add(new RouteNodeConfigParam(routeNode, RouteNode.RULE_SELECTOR_CFG_KEY, FlexRM.DEFAULT_RULE_SELECTOR));
        }

        if (((Boolean) xpath.evaluate("./ruleTemplate", node, XPathConstants.BOOLEAN)).booleanValue()) {
            String ruleTemplateName = (String) xpath.evaluate("./ruleTemplate", node, XPathConstants.STRING);
            RuleTemplate ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(ruleTemplateName);
            if (ruleTemplate == null) {
                throw new InvalidXmlException("Rule template for node '" + routeNode.getRouteNodeName() + "' not found: " + ruleTemplateName);
            }
            routeNode.setRouteMethodName(ruleTemplateName);
            routeNode.setRouteMethodCode(KEWConstants.ROUTE_LEVEL_FLEX_RM);
        } else if (((Boolean) xpath.evaluate("./routeModule", node, XPathConstants.BOOLEAN)).booleanValue()) {
            routeNode.setRouteMethodName((String) xpath.evaluate("./routeModule", node, XPathConstants.STRING));
            routeNode.setRouteMethodCode(KEWConstants.ROUTE_LEVEL_ROUTE_MODULE);
        }

        String nodeType = null;
        if (((Boolean) xpath.evaluate("./type", node, XPathConstants.BOOLEAN)).booleanValue()) {
            nodeType = (String) xpath.evaluate("./type", node, XPathConstants.STRING);
        } else {
            String localName = (String) xpath.evaluate("local-name(.)", node, XPathConstants.STRING);
            if ("start".equalsIgnoreCase(localName)) {
                nodeType = "org.kuali.rice.kew.engine.node.InitialNode";
            } else if ("split".equalsIgnoreCase(localName)) {
                nodeType = "org.kuali.rice.kew.engine.node.SimpleSplitNode";
            } else if ("join".equalsIgnoreCase(localName)) {
                nodeType = "org.kuali.rice.kew.engine.node.SimpleJoinNode";
            } else if ("requests".equalsIgnoreCase(localName)) {
                nodeType = "org.kuali.rice.kew.engine.node.RequestsNode";
            } else if ("process".equalsIgnoreCase(localName)) {
                nodeType = "org.kuali.rice.kew.engine.node.SimpleSubProcessNode";
            } else if (NodeType.ROLE.getName().equalsIgnoreCase(localName)) {
            	nodeType = RoleNode.class.getName();
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
        } else if (NodeType.ROLE.getName().equalsIgnoreCase(localName)) {
        	routeNode.setRouteMethodName(RoleRouteModule.class.getName());
        	routeNode.setRouteMethodCode(KEWConstants.ROUTE_LEVEL_ROUTE_MODULE);
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
            attribute.setOrderIndex(i+1);
            attributes.add(attribute);
        }
        return attributes;
    }

    private class RoutePathContext {
        public BranchPrototype branch;
        public LinkedList splitNodeStack = new LinkedList();
    }

    protected IdentityManagementService getIdentityManagementService() {
    	return KIMServiceLocator.getIdentityManagementService();
    }

}