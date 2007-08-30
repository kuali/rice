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
package org.kuali.rice.kim.test;

import org.junit.Test;
import org.kuali.rice.kim.bo.AttributeType;
import org.kuali.rice.kim.service.KimService;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

/**
 * Basic Test to verify the test harness is able to start kim. 
 * 
 * @author rkirkend
 *
 */
public class KimSanityCheckTest extends KIMTestCase {

    @Test public void testKimTestHarness() throws Exception {
	
	KimService kimService = (KimService)GlobalResourceLoader.getService("kimService");
	AttributeType attType = new AttributeType();
	attType.setAttributeTypeName("name");
	kimService.saveAttributeType(attType);
	
	attType = kimService.getAttributeType(attType.getId());
	
	
	assertNotNull("should have selected saved att type", attType);
    }
    
}
