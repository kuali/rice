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
package org.kuali.notification.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.notification.bo.Notification;
import org.kuali.notification.bo.NotificationChannel;
import org.kuali.notification.bo.NotificationContentType;
import org.kuali.notification.bo.NotificationPriority;
import org.kuali.notification.bo.NotificationProducer;
import org.kuali.notification.bo.NotificationRecipient;
import org.kuali.notification.bo.NotificationSender;
import org.kuali.notification.dao.BusinessObjectDao;
import org.kuali.notification.service.NotificationContentTypeService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.org.apache.xerces.internal.jaxp.JAXPConstants;

/**
 * A general Utility class for the Notification system.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public final class Util {
    private static final Logger LOG = Logger.getLogger(Util.class);

    //public static final EntityResolver ENTITY_RESOLVER = new ClassLoaderEntityResolver("schema", "notification");
    public static final NamespaceContext NOTIFICATION_NAMESPACE_CONTEXT;

    //  XSD Zulu (UTC) date format necessary for XML request messages
    private static final DateFormat DATEFORMAT_ZULU = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    // Server current date time
    private static final DateFormat DATEFORMAT_CURR_TZ = new SimpleDateFormat(
    "MM/dd/yyyy hh:mm a");
    
    static {
        Map<String, String> prefixToNS = new HashMap<String, String>();
        prefixToNS.put("nreq", "ns:notification/NotificationRequest");
        NOTIFICATION_NAMESPACE_CONTEXT = new ConfiguredNamespaceContext(prefixToNS);

        // set the timezone to Zulu for the XML/XSD formatter
        DATEFORMAT_ZULU.setTimeZone(TimeZone.getTimeZone("UTC"));
        // set the timezone for current time
         
    }

    /**
     * Parses a date/time string under XSD dateTime type syntax
     * @see #DATEFORMAT_ZULU
     * @param dateTimeString an XSD dateTime-formatted String
     * @return a Date representing the time value of the String parameter 
     * @throws ParseException if an error occurs during parsing 
     */
    public static Date parseXSDDateTime(String dateTimeString) throws ParseException {
        synchronized (DATEFORMAT_ZULU) {
            return DATEFORMAT_ZULU.parse(dateTimeString);
        }
    }

    /**
     * Formats a Date into XSD dateTime format
     * @param d the date value to format
     * @return date value formatted into XSD dateTime format
     */
    public static String toXSDDateTimeString(Date d) {
        synchronized (DATEFORMAT_ZULU) {
            return DATEFORMAT_ZULU.format(d);
        }
    }
    
    /**
     * Returns the current date formatted for the UI
     * @return the current date formatted for the UI
     */
    public static String getCurrentDateTime() {
        return toUIDateTimeString(new Date());
    }
    
    /**
     * Returns the specified date formatted for the UI
     * @return the specified date formatted for the UI
     */
    public static String toUIDateTimeString(Date d) {
        synchronized (DATEFORMAT_CURR_TZ) {
           return DATEFORMAT_CURR_TZ.format(d);
        }
    }

    /**
     * Parses the string in UI date time format
     * @return the date parsed from UI date time format
     */
    public static Date parseUIDateTime(String s) throws ParseException {
        synchronized (DATEFORMAT_CURR_TZ) {
           return DATEFORMAT_CURR_TZ.parse(s);
        }
    }

    /**
     * Returns a compound NamespaceContext that defers to the preconfigured notification namespace context
     * first, then delegates to the document prefix/namespace definitions second.
     * @param doc the Document to use for prefix/namespace resolution
     * @return  compound NamespaceContext
     */
    public static NamespaceContext getNotificationNamespaceContext(Document doc) {
        return new CompoundNamespaceContext(NOTIFICATION_NAMESPACE_CONTEXT, new DocumentNamespaceContext(doc));
    }

    /**
     * Returns an EntityResolver to resolve XML entities (namely schema resources) in the notification system
     * @param notificationContentTypeService the NotificationContentTypeService
     * @return an EntityResolver to resolve XML entities (namely schema resources) in the notification system
     */
    public static EntityResolver getNotificationEntityResolver(NotificationContentTypeService notificationContentTypeService) {
        return new CompoundEntityResolver(new ClassLoaderEntityResolver("schema", "notification"),
                                          new ContentTypeEntityResolver(notificationContentTypeService));
    }

    /**
     * transformContent - transforms xml content in notification to a string
     * using the xsl in the datastore for a given documentType
     * @param notification
     * @return
     */
    public static String transformContent(Notification notification) {
        NotificationContentType contentType = notification.getContentType();
        String xsl = contentType.getXsl();
        
        LOG.info("xsl: "+xsl);
        
        XslSourceResolver xslresolver = new XslSourceResolver();
        //StreamSource xslsource = xslresolver.resolveXslFromFile(xslpath);
        StreamSource xslsource = xslresolver.resolveXslFromString(xsl);
        String content = notification.getContent();
        LOG.info("xslsource:"+xslsource.toString());
        
        String contenthtml = new String();
        try {
          ContentTransformer transformer = new ContentTransformer(xslsource);
          contenthtml = transformer.transform(content);
          LOG.info("html: "+contenthtml);
        } catch (IOException ex) {
            LOG.error("IOException transforming document",ex);
        } catch (Exception ex) {
            LOG.error("Exception transforming document",ex);
        } 
        return contenthtml;
    }

    /**
     * This method uses DOM to parse the input source of XML.
     * @param source the input source
     * @param validate whether to turn on validation
     * @param namespaceAware whether to turn on namespace awareness
     * @return Document the parsed (possibly validated) document
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Document parse(final InputSource source, boolean validate, boolean namespaceAware, EntityResolver entityResolver) throws ParserConfigurationException, IOException, SAXException {
        // TODO: optimize this
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(validate);
        dbf.setNamespaceAware(namespaceAware);
        dbf.setAttribute(JAXPConstants.JAXP_SCHEMA_LANGUAGE, JAXPConstants.W3C_XML_SCHEMA);
        DocumentBuilder db = dbf.newDocumentBuilder();
        if (entityResolver != null) {
            db.setEntityResolver(entityResolver);
        }
        db.setErrorHandler(new ErrorHandler() {
            public void warning(SAXParseException se) {
                LOG.warn("Warning parsing xml doc " + source, se);
            }
            public void error(SAXParseException se) throws SAXException {
                LOG.error("Error parsing xml doc " + source, se);
                throw se;
            }
            public void fatalError(SAXParseException se) throws SAXException {
                LOG.error("Fatal error parsing xml doc " + source, se);
                throw se;
            }
        });
        return db.parse(source);
    }

    /**
     * This method uses DOM to parse the input source of XML, supplying a notification-system-specific
     * entity resolver.
     * @param source the input source
     * @param validate whether to turn on validation
     * @param namespaceAware whether to turn on namespace awareness
     * @return Document the parsed (possibly validated) document
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Document parseWithNotificationEntityResolver(final InputSource source, boolean validate, boolean namespaceAware, NotificationContentTypeService notificationContentTypeService) throws ParserConfigurationException, IOException, SAXException {
        return parse(source, validate, namespaceAware, getNotificationEntityResolver(notificationContentTypeService));
    }

    /**
     * This method returns the value of the first chile of the element node.
     * @param element
     * @return String
     */
    public static String getTextContent(org.w3c.dom.Element element) {
        NodeList children = element.getChildNodes();
        Node node = children.item(0);
        return node.getNodeValue();
    }

    /**
     * Returns a node child with the specified tag name of the specified parent node,
     * or null if no such child node is found. 
     * @param parent the parent node
     * @param name the name of the child node
     * @return child node if found, null otherwise
     */
    public static Element getChildElement(Node parent, String name) {
        NodeList childList = parent.getChildNodes();
        for (int i = 0; i < childList.getLength(); i++) {
            Node node = childList.item(i);
            // we must test against NodeName, not just LocalName
            // LocalName seems to be null - I am guessing this is because
            // the DocumentBuilderFactory is not "namespace aware"
            // although I would have expected LocalName to default to
            // NodeName
            if (node.getNodeType() == Node.ELEMENT_NODE
                && (name.equals(node.getLocalName())
                   || name.equals(node.getNodeName()))) {
                return (Element) node;
            }
        }
        return null;
    }
    
    /**
     * Returns the text value of a child element with the given name, of the given parent element,
     * or null if the child does not exist or does not have a child text node
     * @param parent parent element
     * @param name name of child element
     * @return the text value of a child element with the given name, of the given parent element,
     * or null if the child does not exist or does not have a child text node
     */
    public static String getChildElementTextValue(Node parent, String name) {
        Element child = getChildElement(parent, name);
        if (child == null) {
            return null;
        }
        return child.getTextContent();
    }

    /**
     * Reads the entire contents of a stream and returns a byte array
     * 
     * @param stream
     *            the stream to read fully
     * @return a byte array containing the contents of the stream
     * @throws IOException
     */
    public static byte[] readFully(InputStream stream) throws IOException {
        byte[] buf = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int read;
        while ((read = stream.read(buf)) != -1) {
            baos.write(buf, 0, read);
        }
        return baos.toByteArray();
    }

    /**
     * Serializes a node to XML (without indentation)
     * @param node the node to serialize
     * @return the serialized node
     * @throws TransformerException if transformation fails
     */
    public static String writeNode(org.w3c.dom.Node node) throws TransformerException {
        return writeNode(node, false);
    }

    /**
     * Serializes a node to XML
     * @param node the node to serialize
     * @param indent whether to apply indentation to the output
     * @return the serialized node
     * @throws TransformerException if transformation fails
     */
    public static String writeNode(org.w3c.dom.Node node, boolean indent) throws TransformerException {
        Source source = new DOMSource(node);
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        if (indent) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        }
        transformer.transform(source, result);
        return writer.toString();
    }
    
    /**
     * This method will clone a given Notification object, one level deep, returning a fresh new instance 
     * without any references.
     * @param notification the object to clone
     * @return Notification a fresh instance
     */
    public static final Notification cloneNotificationWithoutObjectReferences(Notification notification) {
	Notification clone = new Notification();
	
	// handle simple data types first
        if(notification.getCreationDateTime() != null) {
            clone.setCreationDateTime(new Timestamp(notification.getCreationDateTime().getTime()));
        }
	if(notification.getAutoRemoveDateTime() != null) {
	    clone.setAutoRemoveDateTime(new Timestamp(notification.getAutoRemoveDateTime().getTime()));
	}
	clone.setContent(new String(notification.getContent()));
	clone.setDeliveryType(new String(notification.getDeliveryType()));
	if(notification.getId() != null) {
	    clone.setId(new Long(notification.getId()));
	}
	clone.setProcessingFlag(new String(notification.getProcessingFlag()));
	if(notification.getSendDateTime() != null) {
	    clone.setSendDateTime(new Timestamp(notification.getSendDateTime().getTime()));
	}
	
        clone.setTitle(notification.getTitle());
        
	// now take care of the channel
	NotificationChannel channel = new NotificationChannel();
	channel.setId(new Long(notification.getChannel().getId()));
	channel.setName(new String(notification.getChannel().getName()));
	channel.setDescription(new String(notification.getChannel().getDescription()));
	channel.setSubscribable(new Boolean(notification.getChannel().isSubscribable()).booleanValue());
	clone.setChannel(channel);
	
	// handle the content type
	NotificationContentType contentType = new NotificationContentType();
	contentType.setId(new Long(notification.getContentType().getId()));
	contentType.setDescription(new String(notification.getContentType().getDescription()));
	contentType.setName(new String(notification.getContentType().getName()));
	contentType.setNamespace(new String(notification.getContentType().getNamespace()));
	clone.setContentType(contentType);
	
	// take care of the prioirity
	NotificationPriority priority = new NotificationPriority();
	priority.setDescription(new String(notification.getPriority().getDescription()));
	priority.setId(new Long(notification.getPriority().getId()));
	priority.setName(new String(notification.getPriority().getName()));
	priority.setOrder(new Integer(notification.getPriority().getOrder()));
	clone.setPriority(priority);
	
	// take care of the producer
	NotificationProducer producer = new NotificationProducer();
	producer.setDescription(new String(notification.getProducer().getDescription()));
	producer.setId(new Long(notification.getProducer().getId()));
	producer.setName(new String(notification.getProducer().getName()));
	producer.setContactInfo(new String(notification.getProducer().getContactInfo()));
	clone.setProducer(producer);
	
	// process the list of recipients now
	ArrayList<NotificationRecipient> recipients = new ArrayList<NotificationRecipient>();
	for(int i = 0; i < notification.getRecipients().size(); i++) {
	    NotificationRecipient recipient = notification.getRecipient(i);
	    NotificationRecipient cloneRecipient = new NotificationRecipient();
	    cloneRecipient.setRecipientId(new String(recipient.getRecipientId()));
	    cloneRecipient.setRecipientType(new String(recipient.getRecipientType()));
	    
	    recipients.add(cloneRecipient);
	}
	clone.setRecipients(recipients);
	
	// process the list of senders now
	ArrayList<NotificationSender> senders = new ArrayList<NotificationSender>();
	for(int i = 0; i < notification.getSenders().size(); i++) {
	    NotificationSender sender = notification.getSender(i);
	    NotificationSender cloneSender = new NotificationSender();
	    cloneSender.setSenderName(new String(sender.getSenderName()));
	    
	    senders.add(cloneSender);
	}
	clone.setSenders(senders);
	
	return clone;
    }
    
    /**
     * This method generically retrieves a reference to foreign key objects that are part of the content, to get 
     * at the reference objects' pk fields so that those values can be used to store the notification with proper 
     * foreign key relationships in the database.
     * @param <T>
     * @param fieldName
     * @param keyName
     * @param keyValue
     * @param clazz
     * @param boDao
     * @return T
     * @throws IllegalArgumentException
     */
    public static <T> T retrieveFieldReference(String fieldName, String keyName, String keyValue, Class clazz, BusinessObjectDao boDao) throws IllegalArgumentException {
        LOG.info(fieldName + " key value: " + keyValue);
        if (StringUtils.isBlank(keyValue)) {
            throw new IllegalArgumentException(fieldName + " must be specified in notification");
        }
        Map<String, Object> keys = new HashMap<String, Object>(1);
        keys.put(keyName, keyValue);
        T reference = (T) boDao.findByPrimaryKey(clazz, keys);
        if (reference == null) {
            throw new IllegalArgumentException(fieldName + " '" + keyValue + "' not found");
        }
        return reference;
    }
    
    /**
     * This method searches for an exception of the specified type in the stack trace of the given
     * exception.
     * @param topLevelException the exception whose stack to traverse
     * @param exceptionClass the exception class to look for
     * @return the first instance of an exception of the specified class if found, or null otherwise
     */
    public static <T extends Throwable> T findExceptionInStack(Throwable topLevelException, Class<T> exceptionClass) {
        Throwable t = topLevelException;
        while (t != null) {
            if (exceptionClass.isAssignableFrom(t.getClass())) return (T) t;
            t = t.getCause();
        }
        return null;
    }
}