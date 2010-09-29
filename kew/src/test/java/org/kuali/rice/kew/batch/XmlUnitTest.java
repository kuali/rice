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
package org.kuali.rice.kew.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.Diff;
import org.junit.Test;
import org.kuali.rice.kew.test.TestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

/**
 * This class is used for trying to figure out the xmlunit stuff.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class XmlUnitTest extends TestCase {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testXmlUnitTest(){
		
		// Define the path for the test environment
        String relativeFolder = "/src/test/resources/org/kuali/rice/kew/batch/data/";
        String ingestedFilePath = TestUtils.getBaseDir() + relativeFolder + "test.xml";
        String reingestedFilePath = TestUtils.getBaseDir() + relativeFolder + "test.xml";
        File ingestedFile = new File(ingestedFilePath);
        File reingestedFile = new File(reingestedFilePath);
        // Need to get InputStream 
        InputStream ingestedStream = null;
        InputStream reingestedStream = null;
        Document ingestedDocument = null;
        Document reingestedDocument = null;
		try {
			ingestedStream = new FileInputStream(ingestedFile);
			reingestedStream = new FileInputStream(reingestedFile);
		
			ingestedDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ingestedStream);
			reingestedDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(reingestedStream);
	        
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Element root = ingestedDocument.getDocumentElement();
		
        Diff d = new Diff(ingestedDocument, reingestedDocument);
		assertTrue(true);	
	}

}
