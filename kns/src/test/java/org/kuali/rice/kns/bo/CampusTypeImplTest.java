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
import org.kuali.rice.kns.bo.CampusType;
import org.kuali.rice.kns.bo.CampusTypeImpl;
import org.kuali.test.KNSTestCase;


/**
 * This is a description of what this class does - chang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class CampusTypeImplTest extends KNSTestCase{
	
	CampusType dummyCampusType;
	
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		dummyCampusType = new CampusTypeImpl();
		
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		dummyCampusType = null;
	}
	
	@Test
	public void testCampusTypeCode(){
		dummyCampusType.setCampusTypeCode("Columbus");
		assertEquals("Testing CampusTypeCode in CampusTypeImplTest","Columbus",dummyCampusType.getCampusTypeCode());
	}
	
	@Test
	public void testCampusTypeName(){
		dummyCampusType.setCampusTypeName("Main");
		assertEquals("Testing CampusTypeName in CampusTypeImplTest","Main",dummyCampusType.getCampusTypeName());
	}
	
	@Test
	public void testCampusTypeActive(){
		dummyCampusType.setActive(true);
		assertTrue("Testing CampusTypeActive in CampusTypeImplTest",dummyCampusType.isActive());
	}
	
	@Test
	public void testDataObjectMaintenanceCodeActiveIndicator(){
		assertTrue("Testing DataObjectMaintenanceCodeActiveIndicator in CampusTypeImplTest",dummyCampusType.getDataObjectMaintenanceCodeActiveIndicator());
	}
}
