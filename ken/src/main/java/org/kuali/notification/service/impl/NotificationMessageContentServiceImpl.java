/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.notification.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.notification.bo.Notification;
import org.kuali.notification.bo.NotificationChannel;
import org.kuali.notification.bo.NotificationContentType;
import org.kuali.notification.bo.NotificationPriority;
import org.kuali.notification.bo.NotificationProducer;
import org.kuali.notification.bo.NotificationRecipient;
import org.kuali.notification.bo.NotificationResponse;
import org.kuali.notification.bo.NotificationSender;
import org.kuali.notification.dao.BusinessObjectDao;
import org.kuali.notification.exception.InvalidXMLException;
import org.kuali.notification.service.NotificationContentTypeService;
import org.kuali.notification.service.NotificationMessageContentService;
import org.kuali.notification.util.CompoundNamespaceContext;
import org.kuali.notification.util.ConfiguredNamespaceContext;
import org.kuali.notification.util.NotificationConstants;
import org.kuali.notification.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * NotificationMessageContentService implementation - uses both Xalan and XStream in various places to manage the marshalling/unmarshalling of 
 * Notification data for processing by various components in the system.
 * @see NotificationMessageContentService
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationMessageContentServiceImpl implements NotificationMessageContentService {
    private static final Logger LOG = Logger.getLogger(NotificationMessageContentServiceImpl.class);

    /**
     * Prefix that content type schemas should start with
     */
    static final String CONTENT_TYPE_NAMESPACE_PREFIX = "ns:notification/ContentType";

    // Date format of current timezone necessary for intra-system XML parsing via send form
    private static final DateFormat DATEFORMAT_CURR_TZ = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

    /**
     * Our BusinessObjectDao persistence layer
     */
    private BusinessObjectDao boDao;
    /**
     * NotificationContentTypeService impl
     */
    private NotificationContentTypeService notificationContentTypeService;

    /**
     * Constructor which takes a BusinessObjectDao
     * Constructs a NotificationMessageContentServiceImpl.java.
     * @param boDao
     */
    public NotificationMessageContentServiceImpl(BusinessObjectDao boDao,  NotificationContentTypeService notificationContentTypeService) {
        this.boDao = boDao;
        this.notificationContentTypeService = notificationContentTypeService;
    }
    
    /**
     * This method implements by taking in a String and then converting that to a byte[];
     * @see org.kuali.notification.service.NotificationMessageContentService#parseNotificationRequestMessage(java.lang.String)
     */
    public Notification parseNotificationRequestMessage(String notificationMessageAsXml) throws IOException, InvalidXMLException {
	// this is sort of redundant...but DOM does not perform validation
        // so we have to read all the bytes and then hand them to DOM
        // after our first-pass validation, for a second parse
	byte[] bytes = notificationMessageAsXml.getBytes();
	
	return parseNotificationRequestMessage(bytes);
    }

    /**
     * This method implements by taking in an InputStream and then coverting that to a byte[].
     * @see org.kuali.notification.service.NotificationMessageContentService#parseNotificationRequestMessage(java.io.InputStream)
     */
    public Notification parseNotificationRequestMessage(InputStream stream) throws IOException, InvalidXMLException {
        // this is sort of redundant...but DOM does not perform validation
        // so we have to read all the bytes and then hand them to DOM
        // after our first-pass validation, for a second parse
        byte[] bytes = Util.readFully(stream);
        
        return parseNotificationRequestMessage(bytes); 
    }

    /**
     * This method is the meat of the notification message parsing.  It uses DOM to parse out the notification 
     * message XML and into a Notification BO.  It handles lookup of reference objects' primary keys so that it 
     * can properly populate the notification object.
     * @param bytes
     * @return Notification
     * @throws IOException
     * @throws InvalidXMLException
     */
    private Notification parseNotificationRequestMessage(byte[] bytes) throws IOException, InvalidXMLException {
        /* First we'll fully parse the DOM with validation turned on */
        Document doc;
        try {
            doc = Util.parseWithNotificationEntityResolver(new InputSource(new ByteArrayInputStream(bytes)), true, true, notificationContentTypeService);
        } catch (ParserConfigurationException pce) {
            throw new InvalidXMLException("Error obtaining XML parser", pce);
        } catch (SAXException se) {
            throw new InvalidXMLException("Error validating notification request", se);
        }

        Element root = doc.getDocumentElement();
        /* XPath is namespace-aware, so if the DOM that XPath will be evaluating has fully qualified elements
           (because, e.g., it has been parsed with a validating DOM parser as above, then we need to set a
           "NamespaceContext" which essentially declares the defined namespace mappings to XPath.
           
           Unfortunately there is no EASY way (that I have found at least) to automatically expose the namespaces
           that have been discovered in the XML document parsed into DOM to XPath (an oversight in my opinion as
           this requires duplicate footwork to re-expose known definitions).
           
           So what we do is create a set of helper classes that will expose both the "known" core Notification system
           namespaces, as well as those that can be derived from the DOM Document (Document exposes these but through a
           different API than XPath NamespaceContext).  We create CompoundNamespaceContext that consists of both of these
           constituent namespace contexts (so that our core NamespaceContext takes precedent...nobody should be redefining
           these!).
           
           We can *then* use fully qualified XPath expressions like: /nreq:notification/nreq:channel ...
           
           (Another alternative would be to REPARSE the incoming XML with validation turned off so we can have simpler XPath
           expresssions.  This is less correct, but also not ideal as we will want to use qualified XPath expressions with
           notification content type also)
         */
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(Util.getNotificationNamespaceContext(doc));

        /* First parse immediate/primitive Notification member data */
        LOG.debug("URI: " + xpath.getNamespaceContext().getNamespaceURI("nreq"));
        try {
            String channelName = (String) xpath.evaluate("/nreq:notification/nreq:channel", root);
            LOG.debug("CHANNELNAME: "+ channelName);
            String producerName = xpath.evaluate("/nreq:notification/nreq:producer", root);
    
            List<String> senders = new ArrayList<String>();
            NodeList nodes = (NodeList) xpath.evaluate("/nreq:notification/nreq:senders/nreq:sender", root, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                LOG.debug("sender node: " + nodes.item(i));
                LOG.debug("sender node VALUE: " + nodes.item(i).getTextContent());
                senders.add(nodes.item(i).getTextContent());   
            }
            nodes = (NodeList) xpath.evaluate("/nreq:notification/nreq:recipients/nreq:group|/nreq:notification/nreq:recipients/nreq:user", root, XPathConstants.NODESET);
            List<NotificationRecipient> recipients = new ArrayList<NotificationRecipient>();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                NotificationRecipient recipient = new NotificationRecipient();
                // NOTE: assumes validation has occurred; does not check validity of element name
                if (NotificationConstants.RECIPIENT_TYPES.GROUP.equalsIgnoreCase(node.getLocalName())) {
                    recipient.setRecipientType(NotificationConstants.RECIPIENT_TYPES.GROUP);
                } else if (NotificationConstants.RECIPIENT_TYPES.USER.equalsIgnoreCase(node.getLocalName())){
                    recipient.setRecipientType(NotificationConstants.RECIPIENT_TYPES.USER);
                } else {
                    throw new InvalidXMLException("Invalid 'recipientType' value: '" + node.getLocalName() + 
                	    "'.  Needs to either be 'user' or 'group'");
                }
                recipient.setRecipientId(node.getTextContent());
                recipients.add(recipient);   
            }

            String deliveryType = xpath.evaluate("/nreq:notification/nreq:deliveryType", root);
            String sendDateTime = xpath.evaluate("/nreq:notification/nreq:sendDateTime", root);
            String autoRemoveDateTime = xpath.evaluate("/nreq:notification/nreq:autoRemoveDateTime", root);

            String priorityName = xpath.evaluate("/nreq:notification/nreq:priority", root);
            String title = xpath.evaluate("/nreq:notification/nreq:title", root);
            String contentTypeName = xpath.evaluate("/nreq:notification/nreq:contentType", root);

            /* Construct the Notification business object */

            Notification notification = new Notification();
            
            if (!StringUtils.isBlank(title)) {
                notification.setTitle(title);
            }

            /* channel and producer require lookups in the system (i.e. we can't just create new instances out of whole cloth), so
               we call a helper method to retrieve references to the respective objects
             */ 
            NotificationChannel channel = Util.retrieveFieldReference("channel", "name", channelName, NotificationChannel.class, boDao);
            notification.setChannel(channel);

            NotificationProducer producer = Util.retrieveFieldReference("producer", "name", producerName, NotificationProducer.class, boDao);
            notification.setProducer(producer);

            for (String sender: senders) {
                NotificationSender ns = new NotificationSender();
                LOG.debug("Setting sender: " + sender);
                ns.setSenderName(sender);
                notification.addSender(ns);
            }
    
            for (NotificationRecipient recipient: recipients) {
                LOG.debug("Setting recipient id: "+ recipient.getRecipientId());
                notification.addRecipient(recipient);
            }
    
            /* validate the delivery type */
            if(!NotificationConstants.DELIVERY_TYPES.ACK.equalsIgnoreCase(deliveryType) && 
               !NotificationConstants.DELIVERY_TYPES.FYI.equalsIgnoreCase(deliveryType)) {
                throw new InvalidXMLException("Invalid 'deliveryType' value: '" + deliveryType + 
                    "'.  Must be either 'ACK' or 'FYI'.");
            }
            notification.setDeliveryType(deliveryType);
    
            /* If we have gotten this far, then these dates have obviously already passed XML schema validation,
               but as that may be volatile we make sure to validate programmatically.
             */
            Date d;
            if(StringUtils.isNotBlank(sendDateTime)) {
                try {
                    d = Util.parseXSDDateTime(sendDateTime);
                } catch (ParseException pe) {
                    throw new InvalidXMLException("Invalid 'sendDateTime' value: " + sendDateTime, pe);
                }
                notification.setSendDateTime(new Timestamp(d.getTime()));
            }
            if(StringUtils.isNotBlank(autoRemoveDateTime)) {
                try {
                    d = Util.parseXSDDateTime(autoRemoveDateTime);
                } catch (ParseException pe) {
                    throw new InvalidXMLException("Invalid 'autoRemoveDateTime' value: " + autoRemoveDateTime, pe);
                }
                notification.setAutoRemoveDateTime(new Timestamp(d.getTime()));
            }
            
    
            /* we have to look up priority and content type in the system also */
            NotificationPriority priority = Util.retrieveFieldReference("priority", "name", priorityName, NotificationPriority.class, boDao);
            notification.setPriority(priority);
     
            NotificationContentType contentType = Util.retrieveFieldReference("contentType", "name", contentTypeName, NotificationContentType.class, boDao);
            notification.setContentType(contentType);
    
            /* Now handle and validate actual notification content.  This is a tricky part.
               Our job is to validate the incoming content xml blob.  However that content could be under ANY namespace
               (since we have pluggable content types).  So how can we construct an XPath expression, that refers to
               node names that are fully qualified with the correct namespace/uri, when we don't KNOW at this point what that
               correct namespace URI is?
               
               The solution is to use a namespace naming convention coupled with the defined content type name in order to generate
               the canonical namespace uri for any custom content type.
               
               ns:notification/Content<Content Type name>
               
               e.g. ns:notification/ContentSimple, or ns:notification/ContentEvent
               
               We then construct an "ephemeral" namespace prefix to use in the NamespaceContext/XPath expressions to refer to this namespace URI.
               
               e.g. contentNS_<unique number>
               
               It doesn't (shouldn't!) matter what this ephemeral namespace is.
               
               We then define a temporary NamespaceContext that consists only of this ephemeral namespace mapping, and wrap the existing
               XPath NamespaceContext (the nice one we set up above to do our original qualifizzizing) with it.  Then we are off and on our
               way and can use XPath to parse the content type of arbitrary namespace.
             */
            Map<String, String> contentTypeNamespace = new HashMap<String, String>();
            String ephemeralNamespace = "contentNS_" + System.currentTimeMillis();
            contentTypeNamespace.put(ephemeralNamespace, CONTENT_TYPE_NAMESPACE_PREFIX + contentType.getName());
            xpath.setNamespaceContext(new CompoundNamespaceContext(new ConfiguredNamespaceContext(contentTypeNamespace), xpath.getNamespaceContext()));
            Node contentNode = (Node) xpath.evaluate("/nreq:notification/" + ephemeralNamespace + ":content", root, XPathConstants.NODE);
            Element contentElement = null;
            String content = "";
            /* Since we have had to use <any processContents="lax" minOccurs="1" maxOccurs="1"/> for the content element
             * (since there is no way to specify a mandatory element of specified name, but unspecified type), we need to
             * make sure to *programmatically* enforce its existence, since schema won't (the above statement says any
             * element occuring once, but we don't want "any" element, we want an element named 'content').
             */ 
            if (contentNode == null) {
                throw new InvalidXMLException("The 'content' element is mandatory.");
            }
            if (contentNode != null) {
                if (!(contentNode instanceof Element)) {
                    // don't know what could possibly cause this
                    throw new InvalidXMLException("The 'content' node is not an Element! (???).");
                }
                contentElement = (Element) contentNode;
                /* Take the literal XML content value of the DOM node.
                   This should be symmetric/reversable */
                try {
                    content = Util.writeNode(contentNode, true);
                } catch (TransformerException te) {
                    throw new InvalidXMLException("Error serializing notification request content.", te);
                }
            }

            notification.setContent(content);
    
            LOG.debug("Content type: " + contentType.getName());
            LOG.debug("Content: " + content);

            /* double check that we got content of the type that was declared, not just any valid
               content type! (e.g., can't send valid Event content for a Simple notification type)
             */
            validateContent(notification, contentType.getName(), contentElement, content);

            return notification;
        } catch (XPathExpressionException xpee) {
            throw new InvalidXMLException("Error parsing request", xpee);
        }
    }

    

    /**
     * This method validates the content of a notification message by matching up the namespace of the expected content type 
     * to the actual namespace that is passed in as part of the XML message.
     *
     * This is possibly redundant because we are using qualified XPath expressions to obtain content under the correct namespace.
     *
     * @param notification
     * @param contentType
     * @param contentElement
     * @param content
     * @throws IOException
     * @throws InvalidXMLException
     */
    private void validateContent(Notification notification, String contentType, Element contentElement, String content) throws IOException, InvalidXMLException {
        // this debugging relies on a DOM 3 API that is only available with Xerces 2.7.1+ (TypeInfo)
        // commented out for now
        /*LOG.debug(contentElement.getSchemaTypeInfo());
        LOG.debug(contentElement.getSchemaTypeInfo().getTypeName());
        LOG.debug(contentElement.getSchemaTypeInfo().getTypeNamespace());
        LOG.debug(contentElement.getNamespaceURI());
        LOG.debug(contentElement.getLocalName());
        LOG.debug(contentElement.getNodeName());*/

        String contentTypeTitleCase = Character.toTitleCase(contentType.charAt(0)) + contentType.substring(1);
        String expectedNamespaceURI = CONTENT_TYPE_NAMESPACE_PREFIX + contentTypeTitleCase;
        String actualNamespaceURI = contentElement.getNamespaceURI();
        if (!actualNamespaceURI.equals(expectedNamespaceURI)) {
            throw new InvalidXMLException("Namespace URI of 'content' node, '" + actualNamespaceURI + "', does not match expected namespace URI, '" + expectedNamespaceURI + "', for content type '" + contentType + "'");
        }
    }

    /**
     * This method will marshall out the NotificationResponse object as a String of XML, using XStream.
     * @see org.kuali.notification.service.NotificationMessageContentService#generateNotificationResponseMessage(org.kuali.notification.bo.NotificationResponse)
     */
    public String generateNotificationResponseMessage(NotificationResponse response) {
	XStream xstream = new XStream(new DomDriver());
	xstream.alias("response", NotificationResponse.class);
	xstream.alias("status", String.class);
	xstream.alias("message", String.class);
        xstream.alias("notificationId", Long.class);
	String xml = xstream.toXML(response);			
	return xml;
    }

    /**
     * This method will marshall out the Notification object as a String of XML, using XStream and replaces the 
     * full recipient list with just a single recipient.
     * @see org.kuali.notification.service.NotificationMessageContentService#generateNotificationMessage(org.kuali.notification.bo.Notification, java.lang.String)
     */
    public String generateNotificationMessage(Notification notification, String userRecipientId) {
	// create a new fresh instance so we don't screw up any references
	Notification clone = Util.cloneNotificationWithoutObjectReferences(notification);

        /* TODO: modify clone recipient list so that:
             1. only the specified user is listed as a recipient (no other users or groups)
             2. if the specified user was resolved from a group, make sure to include
                that group in the list so it can be searched against for this
                particular per-user notification
        
           Group1 --> TestUser1 --> "Group1 TestUser1"
                  --> TestUser2 --> "Group1 TestUser2"

        */
	
	// inject only the single specified recipient
	if(StringUtils.isNotBlank(userRecipientId)) {
	    clone.getRecipients().clear();
	    
	    NotificationRecipient recipient = new NotificationRecipient();
	    recipient.setRecipientId(userRecipientId);
	    recipient.setRecipientType(NotificationConstants.RECIPIENT_TYPES.USER);
	    
	    clone.getRecipients().add(recipient);
	}
        
	// now marshall out to XML
	XStream xstream = new XStream(new DomDriver());
	xstream.alias("notification", Notification.class);
	xstream.alias("channel", NotificationChannel.class);
	xstream.alias("contentType", NotificationContentType.class);
        xstream.alias("title", String.class);
	xstream.alias("priority", NotificationPriority.class);
	xstream.alias("producer", NotificationProducer.class);
	xstream.alias("recipient", NotificationRecipient.class);
	xstream.alias("sender", NotificationSender.class);
	String xml = xstream.toXML(clone);
	return xml;
    }
    
    /**
     * This method will marshall out the Notification object as a String of XML, using XStream.
     * @see org.kuali.notification.service.NotificationMessageContentService#generateNotificationMessage(org.kuali.notification.bo.Notification)
     */
    public String generateNotificationMessage(Notification notification) {
	return generateNotificationMessage(notification, null);
    }
    
    /**
     * Uses XPath to parse out the serialized Notification xml into a Notification instance.
     * Warning: this method does NOT validate the payload content XML
     * @see org.kuali.notification.service.NotificationMessageContentService#parseNotificationXml(byte[])
     */
    public Notification parseSerializedNotificationXml(byte[] xmlAsBytes) throws Exception {
        Document doc;
        Notification notification = new Notification();
        
        try {
            doc = Util.parse(new InputSource(new ByteArrayInputStream(xmlAsBytes)), false, false, null);
        } catch (Exception pce) {
            throw new InvalidXMLException("Error obtaining XML parser", pce);
        }

        Element root = doc.getDocumentElement();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(Util.getNotificationNamespaceContext(doc));

        try {
            // pull data out of the application content
            String title = ((String) xpath.evaluate("//notification/title", root)).trim();
            
            String channelName = ((String) xpath.evaluate("//notification/channel/name", root)).trim();
            
            String contentTypeName = ((String) xpath.evaluate("//notification/contentType/name", root)).trim();

            String priorityName = ((String) xpath.evaluate("//notification/priority/name", root)).trim();
            
            List<String> senders = new ArrayList<String>();
            NodeList senderNodes = (NodeList) xpath.evaluate("//notification/senders/sender/senderName", root, XPathConstants.NODESET);
            for (int i = 0; i < senderNodes.getLength(); i++) {
                senders.add(senderNodes.item(i).getTextContent().trim());   
            }
            
            String deliveryType = ((String) xpath.evaluate("//notification/deliveryType", root)).trim();
            if(deliveryType.equalsIgnoreCase(NotificationConstants.DELIVERY_TYPES.FYI)) {
        	deliveryType = NotificationConstants.DELIVERY_TYPES.FYI;
            } else {
        	deliveryType = NotificationConstants.DELIVERY_TYPES.ACK;
            }
            
            String sendDateTime = ((String) xpath.evaluate("//notification/sendDateTime", root)).trim();
            
            String autoRemoveDateTime = ((String) xpath.evaluate("//notification/autoRemoveDateTime", root)).trim();
            
            List<String> userRecipients = new ArrayList<String>();
            List<String> workgroupRecipients = new ArrayList<String>();
            
            NodeList recipientIds = (NodeList) xpath.evaluate("//notification/recipients/recipient/recipientId", root, XPathConstants.NODESET);
            NodeList recipientTypes = (NodeList) xpath.evaluate("//notification/recipients/recipient/recipientType", root, XPathConstants.NODESET);
            
            for (int i = 0; i < recipientIds.getLength(); i++) {
        	if(NotificationConstants.RECIPIENT_TYPES.USER.equalsIgnoreCase(recipientTypes.item(i).getTextContent().trim())) {
        	    userRecipients.add(recipientIds.item(i).getTextContent().trim());
        	} else {
        	    workgroupRecipients.add(recipientIds.item(i).getTextContent().trim());
        	}
            }
            
            String content = ((String) xpath.evaluate("//notification/content", root)).trim();
            
            // now populate the notification BO instance
            NotificationChannel channel = Util.retrieveFieldReference("channel", "name", channelName, NotificationChannel.class, boDao);
            notification.setChannel(channel);

            NotificationPriority priority = Util.retrieveFieldReference("priority", "name", priorityName, NotificationPriority.class, boDao);
            notification.setPriority(priority);
     
            NotificationContentType contentType = Util.retrieveFieldReference("contentType", "name", contentTypeName, NotificationContentType.class, boDao);
            notification.setContentType(contentType);

            NotificationProducer producer = Util.retrieveFieldReference("producer", "name", NotificationConstants.KEW_CONSTANTS.NOTIFICATION_SYSTEM_USER_NAME, 
        	    NotificationProducer.class, boDao);
            notification.setProducer(producer);
            
            for (String senderName: senders) {
                NotificationSender ns = new NotificationSender();
                ns.setSenderName(senderName);
                notification.addSender(ns);
            }
            
            for (String userRecipientId: userRecipients) {
                NotificationRecipient recipient = new NotificationRecipient();
                recipient.setRecipientType(NotificationConstants.RECIPIENT_TYPES.USER);
                recipient.setRecipientId(userRecipientId);
                notification.addRecipient(recipient);   
            }

            for (String workgroupRecipientId: workgroupRecipients) {
                NotificationRecipient recipient = new NotificationRecipient();
                recipient.setRecipientType(NotificationConstants.RECIPIENT_TYPES.GROUP);
                recipient.setRecipientId(workgroupRecipientId);
                notification.addRecipient(recipient);   
            }
            
            if (!StringUtils.isBlank(title)) {
                notification.setTitle(title);
            }

            notification.setDeliveryType(deliveryType);
    
            // simpledateformat is not threadsafe, have to sync and validate
            synchronized (DATEFORMAT_CURR_TZ) {
                Date d = null;
                if(StringUtils.isNotBlank(sendDateTime)) {
                    try {
                        d = DATEFORMAT_CURR_TZ.parse(sendDateTime);
                    } catch (ParseException pe) {
                        LOG.warn("Invalid 'sendDateTime' value: " + sendDateTime, pe);
                    }
                    notification.setSendDateTime(new Timestamp(d.getTime()));
                }
                
                Date d2 = null;
                if(StringUtils.isNotBlank(autoRemoveDateTime)) {
                    try {
                        d2 = DATEFORMAT_CURR_TZ.parse(autoRemoveDateTime);
                    } catch (ParseException pe) {
                	LOG.warn("Invalid 'autoRemoveDateTime' value: " + autoRemoveDateTime, pe);
                    }
                    notification.setAutoRemoveDateTime(new Timestamp(d2.getTime()));
                }
            }
            
            notification.setContent(content);
            
            return notification;
        } catch (XPathExpressionException xpee) {
            throw new InvalidXMLException("Error parsing request", xpee);
        }
    }
}