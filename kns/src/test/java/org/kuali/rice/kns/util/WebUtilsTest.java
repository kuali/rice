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
package org.kuali.rice.kns.util;

import org.junit.Test;
import org.kuali.test.KNSTestCase;

/**
 * Unit tests for the KNS WebUtils
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class WebUtilsTest extends KNSTestCase {
	
	/**
	 * Tests WebUtils.getButtonImageUrl()
	 *
	 */
	@Test
	public void testButtonImageUrl() {
		final String test1 = "test1";
		final String test2 = "test2";
		
		final String test2Image = WebUtils.getButtonImageUrl(test2);
		final String test2DefaultImage = WebUtils.getDefaultButtonImageUrl(test2);
		assertEquals("test2 image did not equal default for test2", test2Image, test2DefaultImage);
		
		final String test1Image = WebUtils.getButtonImageUrl(test1);
		final String test1DefaultImage = WebUtils.getDefaultButtonImageUrl(test1);
		assertNotSame("the test1 image should not be the default", test1Image, test1DefaultImage);
		assertEquals("/test/images/test1.png", test1Image);
	}
}
