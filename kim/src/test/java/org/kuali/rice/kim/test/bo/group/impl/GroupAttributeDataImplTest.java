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
package org.kuali.rice.kim.test.bo.group.impl;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.service.impl.GroupServiceImpl;
import org.kuali.rice.kim.service.impl.GroupUpdateServiceImpl;
import org.kuali.rice.kim.test.KIMTestCase;

/**
 * This is a description of what this class does - Isha don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupAttributeDataImplTest extends KIMTestCase {

	GroupAttributeDataImpl grpAttDataImpl = new GroupAttributeDataImpl();
	
	public void setUp() throws Exception {
		super.setUp();
		grpAttDataImpl = (GroupAttributeDataImpl)GlobalResourceLoader.getService(new QName("KIM", "kimGroupService"));
		//groupUpdateService = (GroupUpdateServiceImpl)GlobalResourceLoader.getService(new QName("KIM", "kimGroupUpdateService"));
	}
	@Override
    protected String getModuleName() {
        return "kim";
    }
	
	@Test
	public void testGroupAttDataImpl(){		
		String groupId = "Test";		
		grpAttDataImpl.setGroupId(groupId);		
		assertEquals("Checking the getter and setter for groupID",grpAttDataImpl.getGroupId(), groupId);
	}
	
	@Test
	public void testToStringMapper(){
		//Test the protected toStringMapper method here 
	}
	
	
}
