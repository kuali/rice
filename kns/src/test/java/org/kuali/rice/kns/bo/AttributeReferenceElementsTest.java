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
package org.kuali.rice.kns.bo;


import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.test.KNSTestCase;

/**
 * This is a description of what this class does - chang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AttributeReferenceElementsTest extends KNSTestCase {

	AttributeReferenceElements dummyAttributeReferenceElement;
	@Before
	public void setUp() throws Exception {
		super.setUp();
		dummyAttributeReferenceElement = new AttributeReferenceElements();
	}

	/**
	 * This method ...
	 * 
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		dummyAttributeReferenceElement = null;
	}

	@Test
	public void testInfoTextArea(){
		dummyAttributeReferenceElement.setInfoTextArea("dummyAttributeReferenceElement");
		assertEquals("Testing InfoTextArea in AttributeReferenceElements.","dummyAttributeReferenceElement",dummyAttributeReferenceElement.getInfoTextArea());
	}
	
	@Test
	public void testExtendedTextArea(){
		dummyAttributeReferenceElement.setExtendedTextArea("dummyAttributeReferenceElement");
		assertEquals("Testing ExtendedTextArea in AttributeReferenceElements","dummyAttributeReferenceElement",dummyAttributeReferenceElement.getExtendedTextArea());
	}
	

	@Test
	public void testToStringMapper(){
		LinkedHashMap dummyMap = dummyAttributeReferenceElement.toStringMapper();
		assertNotNull("Testing toStringMapper of Attachment in AttachmentTest", dummyMap.get("hashCode"));
	}
	
}
