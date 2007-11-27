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
package org.kuali.rice.kom.service;

import org.junit.Test;
import org.kuali.rice.kom.test.KOMTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

/**
 * Basic test to verify we can access the KOMService through the GRL. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KOMServiceTest extends KOMTestCase {
    private KOMService komService;
    
    /**
     * @throws Exception
     */
	public void setUp() throws Exception {
	    super.setUp();
	    komService = (KOMService)GlobalResourceLoader.getService("komService");
	}

    /**
     * This method tests that the KOMTestHarness is set up appropriately.
     * 
     * @throws Exception
     */
    @Test
    public void testKomTestHarness() throws Exception {
        assertNotNull(komService);
    }
}
