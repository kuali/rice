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
package edu.iu.uis.eden.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.xml.ClassLoaderEntityResolver;
import edu.iu.uis.eden.xml.XmlConstants;

/**
 * Provides a set of utilities for XML-related operations.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class XmlHelper {
	protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(XmlHelper.class);

    private static final EntityResolver ENTITY_RESOLVER = new ClassLoaderEntityResolver();

	public XmlHelper() {
	}

	public static org.jdom.Document buildJDocument(StringReader xmlStream) throws InvalidXmlException {
		// use SAX Builder
		// don't verify for speed reasons
		SAXBuilder builder = new SAXBuilder(false);
		org.jdom.Document doc = null;

		try {
			doc = builder.build(xmlStream);
		} /*
			 * catch (IOException e) { LOG.error("error building jdom
			 * document"); throw new InvalidXmlException("Invalid xml string. " +
			 * e.getMessage()); }
			 */catch (Exception e) {
			LOG.error("error building jdom document");
			throw new InvalidXmlException("Invalid xml string. " + e.getMessage());
		}

		return doc;
	}

	public static Element getAttributeRootElement(Document document) throws InvalidXmlException {
		return findElement(document.getRootElement(), EdenConstants.ATTRIBUTE_CONTENT_ELEMENT);
	}

	public static Element getAttributeElement(Document document, String elementName) throws InvalidXmlException {
		return findElement(getAttributeRootElement(document), elementName);
	}

	public static org.jdom.Document buildJDocument(org.w3c.dom.Document document) {
		return new DOMBuilder().build(document);
	}

	/**
	 * Same as above but can specify whether validation is to be performed or
	 * not. Would have liked to have added a property that these methods could
	 * have used but would force instantiate on an otherwise static class
	 *
	 * @param xmlSream
	 * @param validateXML
	 * @return JDOM Document
	 * @throws InvalidXMLException
	 */
	public static org.jdom.Document buildJDocument(StringReader xmlStream, boolean validateXML) throws InvalidXmlException {
		SAXBuilder builder = new SAXBuilder(false);
		org.jdom.Document doc = null;

		try {
			doc = builder.build(xmlStream);
		} /*
			 * catch (IOException e) { LOG.error("error building jdom
			 * document"); throw new InvalidXmlException("Invalid xml string. " +
			 * e.getMessage()); }
			 */catch (Exception e) {
			LOG.error("error building jdom document");
			throw new InvalidXmlException("Invalid xml string. " + e.getMessage());
		}

		return doc;
	}

	/**
	 * readerToString: read entire content of a Reader into a String
	 *
	 * @param is
	 * @return String
	 * @throws IOException
	 */
	public static String readerToString(Reader is) throws IOException {
		// local variables
		StringBuffer sb = new StringBuffer();
		char[] b = new char[2000];
		int n;

		// Read a block. If it gets any chars, append them.
		while ((n = is.read(b)) > 0) {
			sb.append(b, 0, n);
		}

		// Only construct the String object once, here.
		return sb.toString();
	}

	// end ==> readerToString()

	/**
	 * Find all Elements in document of a particular name
	 *
	 * @param root -
	 *            the starting Element to scan
	 * @param elementName -
	 *            name of the Element to scan for
	 * @return Vector - a list of the Elements found - return null if none found
	 */
	public static Vector findElements(Element root, String elementName) {
		Vector elementList = new Vector();

		if (root == null) {
			return elementList;
		}

		XmlHelper.findElements(root, elementName, elementList);

		return elementList;
	}

	// end ==> findElements()

	/**
	 * returns the one element in root according to elementName. If more than
	 * one or none of element exists InvalidXmlException is thrown. This method
	 * is based on the assumption you have validated your xml and know the
	 * element is there. It provides a way to make code independent of the xml
	 * structure i.e. it digs for the element.
	 *
	 * @param root
	 * @param elementName
	 * @return Element
	 * @throws InvalidXmlException
	 */
	public static Element findElement(Element root, String elementName) throws InvalidXmlException {
		Vector elementList = XmlHelper.findElements(root, elementName);

		if (elementList.size() < 1) {
			return null;
		}

		if (elementList.size() > 1) {
			throw new InvalidXmlException("More than one element in root");
		}

		return (Element) elementList.get(0);
	}

	/**
	 * makes a properly formed element in the manner of <elementName value=""/>
	 * if value is null value attribute is given an empty value
	 *
	 * @param elementName
	 * @param value
	 * @return Element
	 * @throws Exception
	 */
	public static Element makeElement(String elementName, String value) throws Exception {
		if ((elementName == null) || elementName.trim().equals("")) {
			throw new Exception("Programmatic error:  Element Name passed in null or blank");
		}

		Element element = new Element(elementName);

		if ((value == null) || value.trim().equals("")) {
			element.setAttribute("value", "");
		} else {
			element.setAttribute("value", value);
		}

		return element;
	}

	/**
	 * Returns the value of the given element names given attribute tag based
	 * upon the root element passed in.
	 *
	 * @param root
	 * @param elementName
	 * @param attributeName
	 * @return value of the Element's attribute who's name matches the passed
	 *         element name and attribute matches the attribute name passes
	 * @throws InvalidXmlException
	 *             if element or attribute are not present
	 */
	public static String getElementAttributeValue(Element root, String elementName, String attributeName) throws InvalidXmlException {
		Element element = XmlHelper.findElement(root, elementName);
		Attribute attribute = element.getAttribute(attributeName);

		if (attribute == null) {
			throw new InvalidXmlException("The Attribute name given is not present in the element " + element.getName());
		}

		return attribute.getValue();
	}

	/**
	 * This function is tail-recursive and just adds the root to the list if it
	 * matches and checks the children.
	 *
	 * @param root
	 * @param elementName
	 * @param list
	 */
	private static void findElements(Element root, String elementName, List list) {
		if (root != null) {
			if (root.getName().equals(elementName)) {
				list.add(root);
			}

			Iterator iter = root.getChildren().iterator();

			while (iter.hasNext()) {
				Element item = (Element) iter.next();

				if (item != null) {
					XmlHelper.findElements(item, elementName, list);
				}
			}
		}
	}

	public static String getTextContent(org.w3c.dom.Element element) {
		NodeList children = element.getChildNodes();
		Node node = children.item(0);
		return node.getNodeValue();
	}

	public static String jotDocument(org.jdom.Document document) {
		XMLOutputter outputer = new XMLOutputter(Format.getPrettyFormat());
		StringWriter writer = new StringWriter();
		try {
			outputer.output(document, writer);
		} catch (IOException e) {
			throw new WorkflowRuntimeException("Could not write XML data export.", e);
		}
		return writer.toString();
	}

	public static String jotNode(org.jdom.Element element) {
		XMLOutputter outputer = new XMLOutputter(Format.getPrettyFormat());
		StringWriter writer = new StringWriter();
		try {
			outputer.output(element, writer);
		} catch (IOException e) {
			throw new WorkflowRuntimeException("Could not write XML data export.", e);
		}
		return writer.toString();
	}

	public static String jotNode(org.w3c.dom.Node node) {
		// default to true since this is used mostly for debugging
		return jotNode(node, true);
	}

	public static String jotNode(org.w3c.dom.Node node, boolean indent) {
		try {
			return writeNode(node, indent);
		} catch (TransformerException te) {
			return Utilities.collectStackTrace(te);
		}
	}

	public static String writeNode(org.w3c.dom.Node node) throws TransformerException {
		return writeNode(node, false);
	}

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

	public static void appendXml(Node parentNode, String xml) throws TransformerException {
		Source source = new StreamSource(new BufferedReader(new StringReader(xml)));
		DOMResult result = new DOMResult(parentNode);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(source, result);
	}

	public static org.w3c.dom.Document readXml(String xml) throws TransformerException {
		Source source = new StreamSource(new BufferedReader(new StringReader(xml)));
		DOMResult result = new DOMResult();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(source, result);
		return (org.w3c.dom.Document) result.getNode();
	}

	public static void propogateNamespace(Element element, Namespace namespace) {
		element.setNamespace(namespace);
		for (Iterator iterator = element.getChildren().iterator(); iterator.hasNext();) {
			Element childElement = (Element) iterator.next();
			propogateNamespace(childElement, namespace);
		}
	}

	public static org.w3c.dom.Document trimXml(InputStream input) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		org.w3c.dom.Document oldDocument = builder.parse(input);
		org.w3c.dom.Element naviElement = oldDocument.getDocumentElement();
		trimElement(naviElement);
		return oldDocument;
	}

	public static void trimElement(Node node) throws SAXException, IOException, ParserConfigurationException {

		if (node.hasChildNodes()) {
			// System.out.println(node.getNodeType()+"; "+node.getNodeName()+";
			// "+node.getNodeValue());
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child != null) {
					// System.out.println(child.getNodeType()+";
					// "+child.getNodeName()+"; "+child.getNodeValue());
					trimElement(child);
				}
			}
		} else {
			// System.out.println(node.getNodeType()+"; "+node.getNodeName()+";
			// "+node.getNodeValue());
			if (node.getNodeType() == Node.TEXT_NODE) {
				String text = node.getNodeValue();
				if (Utilities.isEmpty(text)) {
					text = "";
				} else {
					text = text.trim();
				}
				node.setNodeValue(text);
			}
		}
	}

	public static void printDocumentStructure(org.w3c.dom.Document doc) {
		org.w3c.dom.Element naviElement = doc.getDocumentElement();
		printNode(naviElement, 0);
	}

	public static void printNode(Node node, int level) {
		if (node.getNodeType() == Node.TEXT_NODE) {
			System.out.print(node.getNodeValue());
			return;
		} else {
			/*
			 * System.out.println("\n"); for(int i=0;i<level;i++){
			 * System.out.print(" "); }
			 */
			System.out.print("<" + node.getNodeName());
			if (node.hasAttributes()) {
				NamedNodeMap attrMap = node.getAttributes();
				for (int i = 0; i < attrMap.getLength(); i++) {
					org.w3c.dom.Attr attribute = (org.w3c.dom.Attr) attrMap.item(i);
					System.out.print(" " + attribute.getName().trim() + "=\"" + attribute.getValue() + "\"");
				}
			}
			System.out.print(">");
			if (node.hasChildNodes()) {
				NodeList children = node.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);
					if (child != null) {
						// System.out.println(child.getNodeType()+";
						// "+child.getNodeName()+"; "+child.getNodeValue());
						printNode(child, level + 1);
					}
				}
			}
			System.out.print("</" + node.getNodeName() + ">");

		}
	}

	public static Document trimSAXXml(InputStream input) throws JDOMException, SAXException, IOException, ParserConfigurationException {
		SAXBuilder builder = new SAXBuilder(false);
		Document oldDocument = builder.build(input);
		Element naviElement = oldDocument.getRootElement();
		trimSAXElement(naviElement);
		return oldDocument;
	}

	public static void trimSAXElement(Element element) throws SAXException, IOException, ParserConfigurationException {

		if (! element.getChildren().isEmpty()) {

			java.util.List children = element.getChildren();
			for (int i = 0; i < children.size(); i++) {
				Element child = (Element) children.get(i);
				if (child != null) {
					// System.out.println(child.getNodeType()+";
					// "+child.getNodeName()+"; "+child.getNodeValue());
					trimSAXElement(child);
				}
			}
		} else {
			// System.out.println(node.getNodeType()+"; "+node.getNodeName()+";
			// "+node.getNodeValue());
			// System.out.println(element.getName());
			String text = element.getTextTrim();
			if (Utilities.isEmpty(text)) {
				text = "";
			}
			element.setText(text);

		}
	}

	public static void printSAXDocumentStructure(Document doc) {
		Element naviElement = doc.getRootElement();
		printSAXNode(naviElement, 0);
	}

	public static void printSAXNode(Element element, int level) {
		if (element.getChildren().isEmpty()) {
			System.out.print("<" + element.getName().trim() + ">" + element.getText() + "</" + element.getName().trim() + ">");
			return;
		} else {
			/*
			 * System.out.println("\n"); for(int i=0;i<level;i++){
			 * System.out.print(" "); }
			 */
			System.out.print("<" + element.getName());
			org.jdom.Namespace ns = element.getNamespace();
			if (ns != null) {
				System.out.print(" xmlns=\"" + ns.getURI() + "\"");

			}
			ns = element.getNamespace("xsi");
			if (ns != null) {
				System.out.print(" xmlns:" + ns.getPrefix() + "=\"" + ns.getURI() + "\"");
			}
			if (element.getAttributes() != null && element.getAttributes().size() > 0) {
				List attrMap = element.getAttributes();
				for (int i = 0; i < attrMap.size(); i++) {
					Attribute attribute = (Attribute) attrMap.get(i);
					ns = attribute.getNamespace();
					System.out.print(" ");
					if (ns != null) {
						System.out.print(ns.getPrefix() + ":");
					}
					System.out.print(attribute.getName().trim() + "=\"" + attribute.getValue() + "\"");
				}
			}
			System.out.print(">");
			List children = element.getChildren();
			for (int i = 0; i < children.size(); i++) {
				Element child = (Element) children.get(i);
				if (child != null) {
					// System.out.println(child.getNodeType()+";
					// "+child.getNodeName()+"; "+child.getNodeValue());
					printSAXNode(child, level + 1);
				}
			}

			System.out.print("</" + element.getName() + ">");

		}
	}

    /**
     * Convenience method that performs an xpath evaluation to determine whether the expression
     * evaluates to true (a node exists).
     * This is method exists only to disambiguate the cases of determining the *presence* of a node
     * and determining the *boolean value of the node as converted from a string*, as the syntaxes
     * are very similar and could be misleading.
     * @param xpath the XPath object
     * @param expression the XPath expression
     * @param object the object on which to evaluate the expression as required by the XPath API, typically a Node
     * @return whether the result of the expression evaluation, which is whether or not a node was present
     * @throws XPathExpressionException
     */
    public static boolean pathExists(XPath xpath, String expression, Object object) throws XPathExpressionException {
        return ((Boolean) xpath.evaluate(expression, object, XPathConstants.BOOLEAN)).booleanValue();
    }

    public static void validate(final InputSource source) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);
        dbf.setNamespaceAware( true );
        dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", XMLConstants.W3C_XML_SCHEMA_NS_URI);
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(ENTITY_RESOLVER);
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
        db.parse(source);
    }

    public static org.w3c.dom.Element propertiesToXml(org.w3c.dom.Document doc, Object o, String elementName) throws Exception {
        Class c = o.getClass();
        org.w3c.dom.Element wrapper = doc.createElement(elementName);
        Method[] methods = c.getMethods();
        for (int i = 0; i < methods.length; i++) {
            String name = methods[i].getName();
            if ("getClass".equals(name)) continue;
            if (!name.startsWith("get") ||
                methods[i].getParameterTypes().length > 0) continue;
            name = name.substring("get".length());
            name = StringUtils.uncapitalize(name);
            String value = null;
            try {
                Object result = methods[i].invoke(o, null);
                if (result == null) {
                    LOG.debug("value of " + name + " method on object " + o.getClass() + " is null");
                    value = "";
                } else {
                    value = result.toString();
                }
                org.w3c.dom.Element fieldE = doc.createElement(name);
                fieldE.appendChild(doc.createTextNode(value));
                wrapper.appendChild(fieldE);
            } catch (RuntimeException e) {
                LOG.error("Error accessing method '" + methods[i].getName() + " of instance of " + c);
                throw e;
            } catch (Exception e) {
                LOG.error("Error accessing method '" + methods[i].getName() + " of instance of " + c);
            }
        }
        return wrapper;
    }

    public static String getChildElementText(org.w3c.dom.Element parent, String childElementName) {
	NodeList childNodes = parent.getChildNodes();
	for (int index = 0; index < childNodes.getLength(); index++) {
	    org.w3c.dom.Node node = childNodes.item(index);
	    if (Node.ELEMENT_NODE == node.getNodeType()) {
		org.w3c.dom.Element element = (org.w3c.dom.Element)node;
		if (XmlConstants.DOCUMENT_TYPE.equals(element.getNodeName())) {
		    return element.getTextContent();
		}
	    }
	}
	return null;
    }
}