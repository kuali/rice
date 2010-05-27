/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.core.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;

import org.junit.Test;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtils;
import org.kuali.rice.kim.xml.GroupXmlDto;
import org.kuali.rice.test.RiceTestCase;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class JaxpSchemaValidationTest extends RiceTestCase {

	private static final String SCHEMA_DIR = "classpath:schema/";

	private static final String GROUP_SCHEMA = "Group-1.0.3.xsd";
	private static final String BAD_GROUP_SCHEMA1 = "BadGroup-1.0.3a.xsd";
	
	private static int compileErrors = 0;
	
	private static Schema groupSchema = null; // static to prevent from being reset for each test
	
	@Test
	public void testValidateGroupXmlAgainst103Schema() throws Exception {

	}

	@Test
	public void testCompileGroup103Schema() throws Exception {
		InputStream xmlFile = RiceUtilities.getResourceAsStream(SCHEMA_DIR
				+ GROUP_SCHEMA);
		assertNotNull(xmlFile);

		setCompileErrors(0);
		String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		SchemaFactory factory = SchemaFactory.newInstance(language);
		factory.setErrorHandler(new TestSchemaValidationErrorHandler());
		factory.setResourceResolver( new TestSchemaLSResourceResolver());
		StreamSource ss = new StreamSource(xmlFile);
		groupSchema = factory.newSchema(ss);

		assertNotNull(groupSchema);
		assertTrue(getCompileErrors()==0);
	}

	@Test
	public void testCompileBadGroupSchema() throws Exception {
		Schema schema = null;
		InputStream xmlFile = getClass().getResourceAsStream(BAD_GROUP_SCHEMA1);
		assertNotNull(xmlFile);

		setCompileErrors(0);
		String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		SchemaFactory factory = SchemaFactory.newInstance(language);
		factory.setErrorHandler(new TestSchemaValidationErrorHandler());
		factory.setResourceResolver( new TestSchemaLSResourceResolver());
		StreamSource ss = new StreamSource(xmlFile);
		schema = factory.newSchema(ss);

		assertNotNull(schema);
		assertTrue(getCompileErrors()>0);
	}

	@Test
	/** This test validates an XML file during parsing against 
	 *  an existing compiled schema. This test expects no validation errors.
	 *  
	 *  Assumes that testCompileGroup103Schema() has already run successfully.
	 */
	public void testValidateGroupXmlAgainstCompiledSchemaDuringParse() throws Exception {
		if (groupSchema == null){
			testCompileGroup103Schema();
		}
		setCompileErrors(0);
		InputStream xmlFile = getClass().getResourceAsStream("GroupInstance1.xml");
		assertNotNull(xmlFile);

		GroupXmlDto groupXmlDto = new GroupXmlDto();
		JAXBContext jaxbContext = JAXBContext.newInstance(GroupXmlDto.class);
        ValidatorHandler vh = groupSchema.newValidatorHandler(); 
        vh.setErrorHandler(new TestSchemaValidationErrorHandler());

		SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
		spf.setSchema(groupSchema);
		SAXParser saxParser = spf.newSAXParser();
		XMLReader reader = spf.newSAXParser().getXMLReader();
		reader.setContentHandler(vh);
		reader.parse(new InputSource(xmlFile));
//		XMLFilter myFilter = new XMLFilterImpl();
//		myFilter.setParent(spf.newSAXParser().getXMLReader());
//		myFilter.setContentHandler(handler);
//		myFilter.setErrorHandler(new TestSchemaValidationErrorHandler());
//		myFilter.parse(new InputSource(xmlFile));
//		groupXmlDto = (GroupXmlDto) handler.getResult();
		
		assertTrue(getCompileErrors()==0);
		
	}
 
	@Test
	/** This test validates an XML file during parsing against 
	 *  an existing compiled schema. 
	 *  This test expects validation errors - an expected boolean is set to a not boolean value.
	 *  
	 *  Assumes that testCompileGroup103Schema() has already run successfully.
	 */
	public void testValidateGroupXmlAgainstCompiledSchemaDuringParse2() throws Exception {
		if (groupSchema == null){
			testCompileGroup103Schema();
		}
		setCompileErrors(0);
		InputStream xmlFile = getClass().getResourceAsStream("GroupInstance2.xml");
		assertNotNull(xmlFile);

		GroupXmlDto groupXmlDto = new GroupXmlDto();
		JAXBContext jaxbContext = JAXBContext.newInstance(GroupXmlDto.class);
        ValidatorHandler vh = groupSchema.newValidatorHandler(); 
        vh.setErrorHandler(new TestSchemaValidationErrorHandler());

		SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
		spf.setSchema(groupSchema);
		SAXParser saxParser = spf.newSAXParser();
		XMLReader reader = spf.newSAXParser().getXMLReader();
		reader.setContentHandler(vh);
		reader.parse(new InputSource(xmlFile));
//		XMLFilter myFilter = new XMLFilterImpl();
//		myFilter.setParent(spf.newSAXParser().getXMLReader());
//		myFilter.setContentHandler(handler);
//		myFilter.setErrorHandler(new TestSchemaValidationErrorHandler());
//		myFilter.parse(new InputSource(xmlFile));
//		groupXmlDto = (GroupXmlDto) handler.getResult();
		
		assertTrue(getCompileErrors()>0);
	}
///////// local helper methods //////////////
	
	
/////////////////////////////////////////////
	
	protected String getModuleName() {
		return TestUtils.getModuleName();
	}

	public static int getCompileErrors() {
		return compileErrors;
	}
	
	public static int incrementCompileErrors() {
		compileErrors++;
		return compileErrors;
	}

	public static void setCompileErrors(int compileErrors) {
		JaxpSchemaValidationTest.compileErrors = compileErrors;
	}

}
