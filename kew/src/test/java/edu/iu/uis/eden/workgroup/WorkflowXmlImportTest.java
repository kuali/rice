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
package edu.iu.uis.eden.workgroup;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;

/**
 * Test workgroup import process. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class WorkflowXmlImportTest extends KEWTestCase {
    
    
    /**
     * 
     * Verify that a workgroup with a bad user in the xml is not going to be put in the db.
     * 
     * @throws Exception
     */
    @Test public void testBadUserInXml() throws Exception {
	
	try {
	    loadXmlFile("WorkflowXmlImportTest_testBadUserInXml.xml");
	} catch (Exception e) {
	    
	}
	
	//verify the workgroup did not get into the db
	Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("BadUserWorkgroup"));
	assertNull(workgroup);
	
	//load xml of an existing workgroup in and verify that we can't put in bad user
	workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("TestWorkgroup"));
	Integer versionNumber = ((BaseWorkgroup)workgroup).getVersionNumber();
	
	try {
	    loadXmlFile("WorkflowXmlImportTest_testBadUserInXml_2.xml");
	} catch (Exception e) {
	}
	
	workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("TestWorkgroup"));
	Integer versionNumber2 = ((BaseWorkgroup)workgroup).getVersionNumber();
	
	assertEquals("Workgroup with a bad user got put in db", versionNumber, versionNumber2);
    }

}
