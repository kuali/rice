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
package org.kuali.rice.kim.test.bo.group.impl;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kim.impl.group.GroupAttributeBo;
import org.kuali.rice.kim.test.KIMTestCase;

import static org.junit.Assert.assertEquals;

/**
 * This is a very simple unit test to make the code coverage tool happy :)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupAttributeDataImplTest extends KIMTestCase {

	GroupAttributeBo grpAttDataImpl;

    @Before
    public void setUp() throws Exception {
		super.setUp();
        grpAttDataImpl = new GroupAttributeBo();
	}

    public void tearDown() throws Exception {
        super.tearDown();
        grpAttDataImpl = null;
    }

    @Test
    public void testGroupId() {
        grpAttDataImpl.setAssignedToId("Test");
		assertEquals("Checking the getter and setter for groupID", grpAttDataImpl.getAssignedToId(), "Test");

    }
}
