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



import org.junit.Test;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.bo.CountryImpl;
import org.kuali.test.KNSTestCase;


/**
 * This class tests CountryImp.java on org.kuali.rice.kns.bo 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class CountryImpTest extends KNSTestCase{

	Country countryOne;
	Country countryTwo;

	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		countryOne = new CountryImpl();
		countryTwo = new CountryImpl();
	}

	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		countryOne = null;
		countryTwo = null;
	}
	
	@Test
	public void testPostalCountryCode(){
		countryOne.setPostalCountryCode("USA");
		
		assertEquals("Testing set and get PostalCountryCode for USA", "USA",countryOne.getPostalCountryCode());
		assertNull("Testing should get null ",countryTwo.getPostalCountryCode());
	}
	
	@Test
	public void testPostalCountryName(){
		countryOne.setPostalCountryName("America");
		
		assertEquals("Testing set and get PostalCountryName for Amercia", "America",countryOne.getPostalCountryName());
		assertNull("Testing should get null ", countryTwo.getPostalCountryName());
	}

	
	/**
	 * boolean default is set to false
	 */
	
	@Test
	public void testPostalCountryRestrictedIndicator(){
		countryOne.setPostalCountryRestrictedIndicator(true);
			
		assertTrue("Testing set and check PostalCountryRestrictedIndicator ",countryOne.isPostalCountryRestrictedIndicator());
		assertFalse("Testing get should get default PostalCountryRestrictedIndicator vaue",countryTwo.isPostalCountryRestrictedIndicator());
	}

	@Test
	public void testActive(){
		countryOne.setActive(true);
			
		assertTrue("Testing set and check Active ",countryOne.isActive());
		assertFalse("Testing get should get default Active",countryTwo.isActive());
	}
	

}
