/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.routeheader;

import org.kuali.rice.core.util.xml.XmlException;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.util.KEWConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;


/**
 * Standard implementation of {@link DocumentContent} which nows hows to parse a
 * String that it's constructed with into content with the application,
 * attribute, and searchable content sections.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StandardDocumentContent implements DocumentContent, Serializable {

	private static final long serialVersionUID = -3189330007364191220L;
	
	private static final String LEGACY_FLEXDOC_ELEMENT = "flexdoc";

	private String docContent;

	private transient Document document;

	private transient Element applicationContent;

	private transient Element attributeContent;

	private transient Element searchableContent;

	private RouteContext routeContext;

	public StandardDocumentContent(String docContent) throws XmlException {
		this(docContent, null);
	}

	public StandardDocumentContent(String docContent, RouteContext routeContext) throws XmlException {
		this.routeContext = routeContext;
		initialize(docContent, routeContext);
	}

	private void initialize(String docContent, RouteContext routeContext) throws XmlException {
		if (org.apache.commons.lang.StringUtils.isEmpty(docContent)) {
			this.docContent = "";
			this.document = null;
		} else {
			try {
				this.docContent = docContent;
				this.document = parseDocContent(docContent);
				extractElements(this.document);
			} catch (IOException e) {
				throw new XmlException("I/O Error when attempting to parse document content.", e);
			} catch (SAXException e) {
				throw new XmlException("XML parse error when attempting to parse document content.", e);
			} catch (ParserConfigurationException e) {
				throw new XmlException("XML parser configuration error when attempting to parse document content.", e);
			}
		}
	}

	private Document parseDocContent(String docContent) throws IOException, SAXException, ParserConfigurationException {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return documentBuilder.parse(new InputSource(new BufferedReader(new StringReader(docContent))));
	}

	private void extractElements(Document document) {
		// this handles backward compatibility in document content
		if (!document.getDocumentElement().getNodeName().equals(KEWConstants.DOCUMENT_CONTENT_ELEMENT)) {
			// if the root element is the flexdoc element (pre Workflow 2.0)
			// then designate that as attribute content
			if (document.getDocumentElement().getNodeName().equals(LEGACY_FLEXDOC_ELEMENT)) {
				attributeContent = document.getDocumentElement();
			} else {
				applicationContent = document.getDocumentElement();
			}
		} else {
			NodeList nodes = document.getDocumentElement().getChildNodes();
			for (int index = 0; index < nodes.getLength(); index++) {
				Node node = nodes.item(index);
				if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(KEWConstants.APPLICATION_CONTENT_ELEMENT)) {
					int numChildElements = 0;
					for (int childIndex = 0; childIndex < node.getChildNodes().getLength(); childIndex++) {
						Node child = (Node) node.getChildNodes().item(childIndex);
						if (child.getNodeType() == Node.ELEMENT_NODE) {
							applicationContent = (Element) child;
							numChildElements++;
						}
					}
					// TODO can we have application content without a root node?
					if (numChildElements > 1) {
						applicationContent = (Element) node;
					}
				} else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(KEWConstants.ATTRIBUTE_CONTENT_ELEMENT)) {
					attributeContent = (Element) node;
				} else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(KEWConstants.SEARCHABLE_CONTENT_ELEMENT)) {
					searchableContent = (Element) node;
				}
			}
		}
	}

	public Element getApplicationContent() {
		return applicationContent;
	}

	public Element getAttributeContent() {
		return attributeContent;
	}

	public String getDocContent() {
		return docContent;
	}

	public Document getDocument() {
		return document;
	}

	public Element getSearchableContent() {
		return searchableContent;
	}

	public RouteContext getRouteContext() {
		return this.routeContext;
	}

	private void readObject(ObjectInputStream ais) throws IOException, ClassNotFoundException {
		ais.defaultReadObject();
		try {
			initialize(this.docContent, this.routeContext);
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
	}

}
