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
package org.kuali.rice.util;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom.output.XMLOutputter;

/**
 *
 * @author rkirkend
 */
public class XmlJotter {

	
	public static String jotNode(org.jdom.Element element) {
		XMLOutputter outputer = new XMLOutputter();
		StringWriter writer = new StringWriter();
		try {
			outputer.output(element, writer);
		} catch (IOException e) {
			throw new RuntimeException("Could not write XML data export.", e);
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
			return RiceUtilities.collectStackTrace(te);
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
	
}
