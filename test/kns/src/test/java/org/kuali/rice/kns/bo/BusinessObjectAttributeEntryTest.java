/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kns.bo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.test.KNSTestCase;

import static org.junit.Assert.assertEquals;


/**
 * This is a description of what this class does - chang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class BusinessObjectAttributeEntryTest extends KNSTestCase {

	BusinessObjectAttributeEntry dummyBOAE;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		dummyBOAE = new BusinessObjectAttributeEntry();		
	}

	/**
	 * This method ...
	 * 
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		dummyBOAE = null;
	}
	
	@Test
	public void testAttributeControlType(){
		dummyBOAE.setAttributeControlType("ControlType");
		assertEquals("Testing AttributeControlType in BusiessObjectAtributeEntry","ControlType",dummyBOAE.getAttributeControlType());
	}
	
	@Test
	public void testAttributeDescription(){
		dummyBOAE.setAttributeDescription("attributeDescription");
		assertEquals("Testing AttributeDescription in BusiessObjectAtributeEntry","attributeDescription",dummyBOAE.getAttributeDescription());
	}
	
	@Test
	public void testAttributeFormatterClassName(){
		dummyBOAE.setAttributeFormatterClassName("attributeFormatterClassName");
		assertEquals("Testing AttributeFormatterClassName in BusiessObjectAtributeEntry", "attributeFormatterClassName",dummyBOAE.getAttributeFormatterClassName());
	}
	
	@Test
	public void testAttributeLabel(){
		dummyBOAE.setAttributeLabel("attributeLabel");
		assertEquals("Testing AttributeLabel in BusiessObjectAtributeEntry","attributeLabel",dummyBOAE.getAttributeLabel());
	}
	
	@Test
	public void testAttributeMaxLength(){
		dummyBOAE.setAttributeMaxLength("100");
		assertEquals("Testing AttributeMaxLength in BusiessObjectAtributeEntry","100",dummyBOAE.getAttributeMaxLength());
	}
	
	@Test
	public void testAttributeName(){
		dummyBOAE.setAttributeName("AttributeName");
		assertEquals("Testing AttributeName in BusiessObjectAtributeEntry","AttributeName",dummyBOAE.getAttributeName());
	}
	
	@Test
	public void testAttributeShortLabel(){
		dummyBOAE.setAttributeShortLabel("AttributeShortLabel");
		assertEquals("Testing AttributeShortLabel in BusiessObjectAtributeEntry","AttributeShortLabel",dummyBOAE.getAttributeShortLabel());
	}
	
	@Test
	public void testAttributeSummary(){
		dummyBOAE.setAttributeSummary("AttributeSummary");
		assertEquals("Testing AttributeSummary in BusiessObjectAtributeEntry","AttributeSummary",dummyBOAE.getAttributeSummary());
	}
	

	@Test
	public void testAttributeValidatingExpression(){
		dummyBOAE.setAttributeValidatingExpression("AttributeValidatingExpression");
		assertEquals("Testing AttributeValidatingExpression in BusiessObjectAtributeEntry","AttributeValidatingExpression",dummyBOAE.getAttributeValidatingExpression());
	}
	
	@Test
	public void testDictionaryBusinessObjectName(){
		dummyBOAE.setDictionaryBusinessObjectName("DictionaryBusinessObjectName");
		assertEquals("Testing DictionaryBusinessObjectName in BusiessObjectAtributeEntry","DictionaryBusinessObjectName",dummyBOAE.getDictionaryBusinessObjectName());
	}
}
