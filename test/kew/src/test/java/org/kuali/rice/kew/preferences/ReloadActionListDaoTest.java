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
package org.kuali.rice.kew.preferences;

import org.junit.Test;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.useroptions.dao.ReloadActionListDAO;

import static junit.framework.Assert.*;

/**
 * This is a description of what this class does - gilesp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ReloadActionListDaoTest extends KEWTestCase {
	
	@Test
    public void testReloadActionListDao() {
    	ReloadActionListDAO rald = (ReloadActionListDAO)KEWServiceLocator.getService("reloadActionListDAO");
    	
    	rald.setReloadActionListFlag("admin");
    	assertTrue(rald.checkAndResetReloadActionListFlag("admin"));
    	assertFalse(rald.checkAndResetReloadActionListFlag("admin"));

    	rald.setReloadActionListFlag("admin");
    	assertTrue(rald.checkAndResetReloadActionListFlag("admin"));
    }
    
}
