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
package org.kuali.rice.kew.transformation;

//Raja Sooriamurthi
//S531 Web application development

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public final class XMLUtil {

    

	private XMLUtil() {
		throw new UnsupportedOperationException("do not call");
	}
	
	// an XPath constructor
	
	static XPath makeXPath() {
		XPathFactory xpfactory = XPathFactory.newInstance();
		return xpfactory.newXPath();
	}

	// Three methods for parsing XML documents based on how they are specified
	//   ... as a file
	//   ... as a URL
	//   ... as the contents of an InputStream
	
	static Document parseFile(String fname) {
		InputStream in = null;
		try {
			in = new FileInputStream(fname);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return parseInputStream(in);
	}

	static Document parseURL(String url) {
		InputStream in = null;
		try {
			in = new URL(url).openStream();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return parseInputStream(in);
	}

	static Document parseInputStream(InputStream in) {
		Document doc = null;
		try {
			DocumentBuilder xml_parser = makeDOMparser();
			doc = xml_parser.parse(in);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (SAXException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return doc;
	}
	
	static DocumentBuilder makeDOMparser() {
		DocumentBuilder parser = null;
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			dbfactory.setIgnoringElementContentWhitespace(true);
			parser = dbfactory.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			System.exit(1);
		}
		return parser;
	}
    
}
