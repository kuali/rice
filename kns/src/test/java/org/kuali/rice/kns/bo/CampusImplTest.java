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

import org.junit.Test;
import org.kuali.rice.kns.bo.CampusImpl;
import org.kuali.rice.kns.bo.CampusType;
import org.kuali.rice.kns.bo.CampusTypeImpl;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.test.KNSTestCase;

/**
 * This is a description of what this class does - chang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class CampusImplTest extends KNSTestCase{

	CampusImpl dummyCampus;
	

	@Override
	public void setUp() throws Exception {
		super.setUp();
		dummyCampus = new CampusImpl();
		
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		dummyCampus = null;
	}
	
	@Test
	public void testCampusCode(){
		dummyCampus.setCampusCode("OSU");
		assertEquals("Testing CampusCode in CampusImplTest", "OSU",dummyCampus.getCampusCode());	
	}
	@Test
	public void testCampusName(){
		dummyCampus.setCampusName("Ohio State University-Columbus");
		assertEquals("Testing CampusName in CampusImplTest","Ohio State University-Columbus",dummyCampus.getCampusName());	
	}
	@Test
	public void testCampusShortName(){
		dummyCampus.setCampusShortName("OSU");
		assertEquals("Testing CampusShortName in CamplusImplTest","OSU",dummyCampus.getCampusShortName());	
	}
	@Test
	public void testCampusTypeCode(){
		dummyCampus.setCampusTypeCode("College");
		assertEquals("Testing CampustypeCode in CampusImplTest","College",dummyCampus.getCampusTypeCode());	
	}
	@Test
	public void testActive(){
		dummyCampus.setActive(true);
		assertTrue("Testing Active in CampusImplTest",dummyCampus.isActive());	
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testCampusType(){
		CampusType dummyCampusType = new CampusTypeImpl();
		
		dummyCampus.setCampusType(dummyCampusType);
		assertTrue("Testing CampusType in CampusImplTest", dummyCampus.getCampusType().equals(dummyCampusType));
	}
	
	@Test
	public void testToStringMapper(){
		dummyCampus.setCampusCode("campusCode");
		LinkedHashMap dummyMap =  dummyCampus.toStringMapper();
		assertEquals("Testing toStringMapper of CampusImpl",dummyCampus.getCampusCode() , dummyMap.get(KNSPropertyConstants.CAMPUS_CODE));
	}
}
