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
import org.kuali.rice.kns.bo.Campus;
import org.kuali.rice.kns.bo.CampusImpl;
import org.kuali.rice.kns.bo.CampusType;
import org.kuali.rice.kns.bo.CampusTypeImpl;
import org.kuali.test.KNSTestCase;

/**
 * This is a description of what this class does - chang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class CampusImplTest extends KNSTestCase{

	Campus campusOne;
	

	@Override
	public void setUp() throws Exception {
		super.setUp();
		campusOne = new CampusImpl();
		
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		campusOne = null;
	}
	
	@Test
	public void testCampusCode(){
		campusOne.setCampusCode("OSU");
		assertEquals("Testing CampusCode in CampusImplTest", "OSU",campusOne.getCampusCode());	
	}
	@Test
	public void testCampusName(){
		campusOne.setCampusName("Ohio State University-Columbus");
		assertEquals("Testing CampusName in CampusImplTest","Ohio State University-Columbus",campusOne.getCampusName());	
	}
	@Test
	public void testCampusShortName(){
		campusOne.setCampusShortName("OSU");
		assertEquals("Testing CampusShortName in CamplusImplTest","OSU",campusOne.getCampusShortName());	
	}
	@Test
	public void testCampusTypeCode(){
		campusOne.setCampusTypeCode("College");
		assertEquals("Testing CampustypeCode in CampusImplTest","College",campusOne.getCampusTypeCode());	
	}
	@Test
	public void testActive(){
		campusOne.setActive(true);
		assertTrue("Testing Active in CampusImplTest",campusOne.isActive());	
	}
	
	@Test
	public void testCampusType(){
		CampusType dummyCampusType = new CampusTypeImpl();
		
		campusOne.setCampusType(dummyCampusType);
		assertTrue("Testing CampusType in CampusImplTest", campusOne.getCampusType().equals(dummyCampusType));
	}
	
}
