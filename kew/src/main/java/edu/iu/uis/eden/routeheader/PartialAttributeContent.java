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
package edu.iu.uis.eden.routeheader;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 * Allows the construction of DocumentContent from fragments of XML.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PartialAttributeContent implements DocumentContent {

	private static final long serialVersionUID = -7710201192800150123L;
	
	private Document document;
	private Element attributeContent;
	private RouteContext routeContext;
	
	public PartialAttributeContent(List attributeContents) throws InvalidXmlException {
	    this(attributeContents, null);
	}
	
	public PartialAttributeContent(List attributeContents, RouteContext routeContext) throws InvalidXmlException {
		try {
            this.routeContext = routeContext;
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.document = documentBuilder.newDocument();
			Element rootElement = document.createElement(EdenConstants.DOCUMENT_CONTENT_ELEMENT);
			this.attributeContent = document.createElement(EdenConstants.ATTRIBUTE_CONTENT_ELEMENT);
			rootElement.appendChild(attributeContent);
			for (Iterator iterator = attributeContents.iterator(); iterator.hasNext();) {
				Element element = (Element) iterator.next();
				element = (Element)document.importNode(element, true);
				attributeContent.appendChild(element);
			}
			document.appendChild(rootElement);
		} catch (Exception e) {
			throw new InvalidXmlException(e);
		}
	}
		
	public Document getDocument() {
		return document;
	}

	public Element getApplicationContent() {
		return null;
	}

	public Element getAttributeContent() {
		return attributeContent;
	}

	public Element getSearchableContent() {
		return null;
	}

	public String getDocContent() {
		try {
            Source source = new DOMSource(document);
            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(source, result);
            return writer.toString();
        } catch (TransformerConfigurationException e) {
            throw new WorkflowRuntimeException("Error configuring transformer to write doc content.", e);
        } catch (TransformerException e) {
            throw new WorkflowRuntimeException("Error transforming DOM into doc content.", e);
        }
	}

    public RouteContext getRouteContext() {
        return this.routeContext;
    }
    
}
