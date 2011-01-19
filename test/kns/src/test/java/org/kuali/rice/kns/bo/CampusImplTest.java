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

import org.junit.Test;
import org.kuali.test.KNSTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
}
