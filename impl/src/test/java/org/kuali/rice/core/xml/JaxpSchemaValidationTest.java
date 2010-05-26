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
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Test;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtils;
import org.kuali.rice.test.RiceTestCase;

public class JaxpSchemaValidationTest extends RiceTestCase {

	private static final String SCHEMA_DIR = "classpath:schema/";

	private static final String GROUP_SCHEMA = "Group-1.0.3.xsd";
	private static final String BAD_GROUP_SCHEMA1 = "BadGroup-1.0.3a.xsd";
	
	private static int compileErrors = 0;
	
	@Test
	public void testValidateGroupXmlAgainst103Schema() throws Exception {

	}

	@Test
	public void testCompileGroup103Schema() throws Exception {
		Schema schema = null;
		InputStream xmlFile = RiceUtilities.getResourceAsStream(SCHEMA_DIR
				+ GROUP_SCHEMA);
		assertNotNull(xmlFile);

		setCompileErrors(0);
		String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
		SchemaFactory factory = SchemaFactory.newInstance(language);
		factory.setErrorHandler(new TestSchemaValidationErrorHandler());
		factory.setResourceResolver( new TestSchemaLSResourceResolver());
		StreamSource ss = new StreamSource(xmlFile);
		schema = factory.newSchema(ss);

		assertNotNull(schema);
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
	public void testValidateGroupXmlAgainstCompiledSchema() throws Exception {
	}

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
