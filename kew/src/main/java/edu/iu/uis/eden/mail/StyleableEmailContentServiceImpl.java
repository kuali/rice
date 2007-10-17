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
// Created on Mar 21, 2007

package edu.iu.uis.eden.mail;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.springframework.core.io.DefaultResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.edl.StyleService;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.feedback.web.FeedbackForm;
import edu.iu.uis.eden.plugin.attributes.CustomEmailAttribute;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * EmailContentService that serves EmailContent customizable via XSLT style sheets
 * The global email style name is: kew.email.style
 * If this style is not found, the resource 'defaultEmailStyle.xsl' will be retrieved
 * relative to this class.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class StyleableEmailContentServiceImpl extends BaseEmailContentServiceImpl {
    private static final Logger LOG = Logger.getLogger(StyleableEmailContentServiceImpl.class);

    protected final String DEFAULT_EMAIL_STYLESHEET_RESOURCE_LOC = "defaultEmailStyle.xsl";

    protected StyleService styleService;
    protected String globalEmailStyleSheet = EdenConstants.EMAIL_STYLESHEET_NAME;
    
    
    public void setStyleService(StyleService styleService) {
        this.styleService = styleService;
    }
    
    public void setGlobalEmailStyleSheet(String globalEmailStyleSheet) {
        this.globalEmailStyleSheet = globalEmailStyleSheet;
    }

    protected static DocumentBuilder getDocumentBuilder(boolean coalesce) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setCoalescing(coalesce);
            return dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            String message = "Error constructing document builder";
            LOG.error(message, e);
            throw new WorkflowRuntimeException(message, e);
        }
    }

    protected static void addObjectXML(Document doc, Object o, Node node, String name) throws Exception {
        Element element = XmlHelper.propertiesToXml(doc, o, name);

        LOG.info(XmlHelper.jotNode(element));

        if (node == null) {
            node = doc;
        }

        node.appendChild(element);
    }

    protected static void addActionItem(Document doc, ActionItem actionItem, WorkflowUser user, Node node) throws Exception {
        if (node == null) {
            node = doc;
        }

        Element root = doc.createElement("actionItem");

        // append the custom body and subject if they exist
        try {
            CustomEmailAttribute customEmailAttribute = getCustomEmailAttribute(user, actionItem);
            if (customEmailAttribute != null) {
                String customBody = customEmailAttribute.getCustomEmailBody();
                if (!Utilities.isEmpty(customBody)) {
                    Element element = doc.createElement("customBody");
                    element.appendChild(doc.createTextNode(customBody));
                    root.appendChild(element);
                }
                String customEmailSubject = customEmailAttribute.getCustomEmailSubject();
                if (!Utilities.isEmpty(customEmailSubject)) {
                    Element element = doc.createElement("customSubject");
                    element.appendChild(doc.createTextNode(customEmailSubject));
                    root.appendChild(element);
                }
            }
        } catch (Exception e) {
            LOG.error("Error when checking for custom email body and subject.", e);
        }

        // keep adding stuff until we have all the xml we need to formulate the message :/

        addObjectXML(doc, actionItem, root, "actionItem");

        addObjectXML(doc, actionItem.getUser(), root, "actionItemUser");

        addObjectXML(doc, actionItem.getUser().getAuthenticationUserId(), root, "actionItemAuthenticationUserId");

        addObjectXML(doc, actionItem.getRouteHeader(), root, "doc");

        addObjectXML(doc, actionItem.getRouteHeader().getInitiatorUser(), root, "docInitiator");

        DocumentType docType = actionItem.getRouteHeader().getDocumentType();
        addObjectXML(doc, docType, root, "documentType");

        node.appendChild(root);
    }

    protected static String transform(Templates style, Document doc) {
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        try {
            style.newTransformer().transform(new DOMSource(doc), result);
            return writer.toString();
        } catch (TransformerException te) {
            String message = "Error transforming DOM";
            LOG.error(message, te);
            throw new WorkflowRuntimeException(message, te);
        }        
    }

    protected EmailContent generateEmailContent(String styleName, Document doc) {
        Templates style = null;
        try {
            style = styleService.getStyleAsTranslet(styleName);
        } catch (TransformerConfigurationException tce) {
            String message = "Error obtaining style '" + styleName + "', using default"; 
            LOG.error(message, tce);
            // throw new WorkflowRuntimeException("Error obtaining style '" + styleName + "'", tce);
        }

        if (style == null) {
            LOG.warn("Could not find specified style, " + styleName + ", using default");
            try {
        	
                style = TransformerFactory.newInstance().newTemplates(new StreamSource(new DefaultResourceLoader().getResource("classpath:edu/iu/uis/eden/mail/" + DEFAULT_EMAIL_STYLESHEET_RESOURCE_LOC).getInputStream()));
            } catch (Exception tce) {
                String message = "Error obtaining default style from resource: " + DEFAULT_EMAIL_STYLESHEET_RESOURCE_LOC; 
                LOG.error(message, tce);
                throw new WorkflowRuntimeException("Error obtaining style '" + styleName + "'", tce);
            }
        }

        DOMResult result = new DOMResult();

        LOG.debug("Input document: " + XmlHelper.jotNode(doc.getDocumentElement(), true));
        try {
            style.newTransformer().transform(new DOMSource(doc), result);
        } catch (TransformerException te) {
            String message = "Error transforming immediate reminder DOM";
            LOG.error(message, te);
            throw new WorkflowRuntimeException(message, te);
        }

        Node node = result.getNode();
        
        LOG.debug("Email document: " + XmlHelper.jotNode(doc));
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        try {
            String subject = (String) xpath.evaluate("/email/subject", node, XPathConstants.STRING);
            String body = (String) xpath.evaluate("/email/body", node, XPathConstants.STRING);
            // simple heuristic to determine whether content is HTML
            return new EmailContent(subject, body, body.matches("(?msi).*<(\\w+:)?html.*"));
        } catch (XPathExpressionException xpee) {
            throw new WorkflowRuntimeException("Error evaluating generated email content", xpee);
        }
    }

    protected EmailContent generateReminderForActionItems(WorkflowUser user, Collection<ActionItem> actionItems, String name, String style) {
        DocumentBuilder db = getDocumentBuilder(false);
        Document doc = db.newDocument();
        Element element = doc.createElement(name);
        setStandardAttributes(element);
        doc.appendChild(element);

        try {
            addObjectXML(doc, user, element, "user");
            for (ActionItem actionItem: actionItems) {
                try {
                    addActionItem(doc, actionItem, user, element);
                } catch (Exception e) {
                    String message = "Error generating XML for action item: " + actionItem;
                    LOG.error(message, e);
                    throw new WorkflowRuntimeException(e);
                }
            }

        } catch (Exception e) {
            String message = "Error generating XML for action items: " + actionItems;
            LOG.error(message, e);
            throw new WorkflowRuntimeException(e);
        }
        
        return generateEmailContent(style, doc);
    }

    protected void setStandardAttributes(Element e) {
        e.setAttribute("env", getDeploymentEnvironment());
        e.setAttribute("applicationEmailAddress", getApplicationEmailAddress());
        e.setAttribute("actionListUrl", getActionListUrl());
        e.setAttribute("preferencesUrl", getPreferencesUrl());
    }

    public EmailContent generateImmediateReminder(WorkflowUser user, ActionItem actionItem, DocumentType documentType) {
        Collection<ActionItem> actionItems = new ArrayList<ActionItem>(1);
        actionItems.add(actionItem);
        
        // change style name based on documentType when configurable email style on document is implemented...
        String styleSheet = documentType.getCustomEmailStylesheet();
        LOG.error(documentType.getName() + " style: " + styleSheet);
        if (styleSheet == null) {
            styleSheet = globalEmailStyleSheet;
        }

        LOG.error("generateImmediateReminder: style: "+ styleSheet);
        return generateReminderForActionItems(user, actionItems, "immediateReminder", styleSheet);
    }

    public EmailContent generateWeeklyReminder(WorkflowUser user, Collection<ActionItem> actionItems) {
        return generateReminderForActionItems(user, actionItems, "weeklyReminder", globalEmailStyleSheet);
    }

    public EmailContent generateDailyReminder(WorkflowUser user, Collection<ActionItem> actionItems) {
        return generateReminderForActionItems(user, actionItems, "dailyReminder", globalEmailStyleSheet);
    }

    public EmailContent generateFeedback(FeedbackForm form) {
        DocumentBuilder db = getDocumentBuilder(true);
        Document doc = db.newDocument();
        String styleSheet = globalEmailStyleSheet;

        // if the doc type is specified, see if that doc has a custom email stylesheet and use it
        // NOTE: do we need to do this for feedback? presumably feedback will be going back to admins
        /*String docTypeName = form.getDocumentType();
        if (!StringUtils.isBlank(docTypeName)) {
            DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName(docTypeName);
            if (docType == null) {
                LOG.error("User specified document type '" + docTypeName + "' in feedback form, but the document type was not found in the system");
            } else {
                if (docType.getCustomEmailStylesheet() != null) {
                    styleSheet = docType.getCustomEmailStylesheet();
                }
            }
        }*/
        LOG.info("form: " + form.getRouteHeaderId());
        try {
            addObjectXML(doc, form, null, "feedback");
        } catch (Exception e) {
            String message = "Error generating XML for feedback form: " + form;
            LOG.error(message, e);
            throw new WorkflowRuntimeException(message, e);
        }
        setStandardAttributes(doc.getDocumentElement());

        return generateEmailContent(styleSheet, doc);
    }
}