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
public class PostalCodeImplTest extends KNSTestCase {

	PostalCodeImpl dummyPCI;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		dummyPCI = new PostalCodeImpl();
	}

	/**
	 * This method ...
	 * 
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		dummyPCI = null;
	}
	
	@Test
	public void testPostalCode(){
		dummyPCI.setPostalCode("43122");
		assertEquals("Testing PostalCode in PostalCodeImpl", "43122", dummyPCI.getPostalCode());
	}
	
	@Test
	public void testPostalStateCode(){
		dummyPCI.setPostalStateCode("OHIO");
		assertEquals("Testing PostalStateCode in PostalCodeImpl", "OHIO", dummyPCI.getPostalStateCode());
	}
	
	@Test
	public void testPostalCityName(){
		dummyPCI.setPostalCityName("Columbus");
		assertEquals("Testing PostalCityName in PostalCodeImpl", "Columbus", dummyPCI.getPostalCityName());
	}
	
	@Test
	public void testState(){
		State dummyState = new StateImpl();
		
		dummyPCI.setState(dummyState);
		assertEquals("Testing State in PostalCodeImpl", dummyState, dummyPCI.getState());
	}
	
	@Test
	public void testCountry(){
		Country dummyCountry = new CountryImpl();
		
		dummyPCI.setCountry(dummyCountry);
		assertEquals("Testing Country in PostalCodeImpl", dummyCountry, dummyPCI.getCountry());
	}
	
	@Test
	public void testCounty(){
		County dummyCounty = new CountyImpl();
		
		dummyPCI.setCounty(dummyCounty);
		assertEquals("Testing County in PostalCodeImpl", dummyCounty, dummyPCI.getCounty());
	}
	
	@Test
	public void testCountyCode(){
		dummyPCI.setCountyCode("Flan");
		assertEquals("Testing CountyCode in PostalCodeImpl", "Flan", dummyPCI.getCountyCode());
	}
	
	
	@Test
	public void testPostalCountryCode(){
		dummyPCI.setPostalCountryCode("USA");
		assertEquals("Testing PostalCountryCode in PostalCodeImpl", "USA", dummyPCI.getPostalCountryCode());
	}

	@Test
	public void testActive(){
		dummyPCI.setActive(true);
		assertEquals("Testing Active in PostalCodeImpl", true, dummyPCI.isActive());
	}
	
	@Test
	public void testToStringMapper(){
		dummyPCI.setPostalCode("USA");
		LinkedHashMap dummyHashMap = dummyPCI.toStringMapper();
		assertEquals("Testing toStringMapper in PostalCodeImpl", "USA", dummyHashMap.get("postalCode"));
	}
}
