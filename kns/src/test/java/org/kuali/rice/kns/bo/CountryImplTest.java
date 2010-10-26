/*
 * Copyright 2010 The Kuali Foundation
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



import java.util.LinkedHashMap;

import org.junit.Test;
import org.kuali.rice.kns.bo.CountryImpl;
import org.kuali.test.KNSTestCase;


/**
 * This class tests CountryImp.java on org.kuali.rice.kns.bo 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class CountryImplTest extends KNSTestCase{

	CountryImpl dummyCountryOne;
	CountryImpl dummyCountrytwo;

	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dummyCountryOne = new CountryImpl();
		dummyCountrytwo = new CountryImpl();
	}

	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		dummyCountryOne = null;
		dummyCountrytwo = null;
	}
	
	@Test
	public void testPostalCountryCode(){
		dummyCountryOne.setPostalCountryCode("USA");
		
		assertEquals("Testing set and get PostalCountryCode for USA", "USA",dummyCountryOne.getPostalCountryCode());
		assertNull("Testing should get null ",dummyCountrytwo.getPostalCountryCode());
	}
	
	@Test
	public void testAlternatePostalCountryCode(){
		dummyCountryOne.setAlternatePostalCountryCode("USA");
		
		assertEquals("Testing set and get AlternatePostalCountryCode for USA", "USA",dummyCountryOne.getAlternatePostalCountryCode());
		assertNull("Testing should get null ",dummyCountrytwo.getAlternatePostalCountryCode());
	}
	
	@Test
	public void testPostalCountryName(){
		dummyCountryOne.setPostalCountryName("America");
		
		assertEquals("Testing set and get PostalCountryName for Amercia", "America",dummyCountryOne.getPostalCountryName());
		assertNull("Testing should get null ", dummyCountrytwo.getPostalCountryName());
	}

	
	/**
	 * boolean default is set to false
	 */
	
	@Test
	public void testPostalCountryRestrictedIndicator(){
		dummyCountryOne.setPostalCountryRestrictedIndicator(true);
			
		assertTrue("Testing set and check PostalCountryRestrictedIndicator ",dummyCountryOne.isPostalCountryRestrictedIndicator());
		assertFalse("Testing get should get default PostalCountryRestrictedIndicator vaue",dummyCountrytwo.isPostalCountryRestrictedIndicator());
	}

	@Test
	public void testActive(){
		dummyCountryOne.setActive(true);
			
		assertTrue("Testing set and check Active ",dummyCountryOne.isActive());
		assertFalse("Testing get should get default Active",dummyCountrytwo.isActive());
	}
	
	@Test
	public void testToStringMapper(){
		dummyCountryOne.setPostalCountryCode("US1101");
		LinkedHashMap dummyMap =  dummyCountryOne.toStringMapper();
		assertEquals("Testing toStringMapper of CountryImpl",dummyCountryOne.getPostalCountryCode() , dummyMap.get("postalCountryCode"));
	}

}
