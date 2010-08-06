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
package org.kuali.rice.kim.test.util;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.impl.GroupAttributeDataImpl;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;

import java.util.List;

/**
 * Unit Tests for KimCommonUtils.java
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class KimCommonUtilsTest extends KIMTestCase {
    private static final Logger LOG = Logger.getLogger(KimCommonUtilsTest.class);

    @Override
    protected String getModuleName() {
        return "kim";
    }

    @Test
    public void testCopyInfoAttributesToGroupAttributes() {
        IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();

        GroupInfo groupInfo = identityManagementService.getGroupByName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "GroupNine");

        assertTrue(groupInfo.getAttributes() != null);
        assertTrue(groupInfo.getAttributes().size() > 0);
        List<GroupAttributeDataImpl> attributeDataList;
        try {
        attributeDataList = KimCommonUtils.copyInfoAttributesToGroupAttributes(groupInfo.getAttributes(), groupInfo.getGroupId(), groupInfo.getKimTypeId());
        assertTrue(attributeDataList != null);
        assertTrue(attributeDataList.size() > 0);
        assertTrue(attributeDataList.size() == groupInfo.getAttributes().size());
        }
    	catch (IllegalArgumentException expectedException) {
    		assertTrue(expectedException.getMessage().contains("not found"));
    		fail("Ingested a group with an unexpected data");
    	}

    }

    @Test
    public void testCopyInfoToGroup() {
        IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();

        GroupInfo groupInfo = identityManagementService.getGroupByName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "GroupNine");

        GroupImpl groupImpl = new GroupImpl();
        KimCommonUtils.copyInfoToGroup(groupInfo, groupImpl);
        // Figure out why we do individual checks here, i dont think they are needed
        assertEquals(groupInfo.isActive(), groupImpl.isActive());
    }

    @Test
    public void testFailedCopyInfoAttributesToGroupAttributes() {
    	IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();

		GroupInfo groupInfo = identityManagementService.getGroupByName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "GroupNine");
		AttributeSet testAttributeSet = groupInfo.getAttributes();
        testAttributeSet.put("someDummyKey", "someDummyValue");
        groupInfo.setAttributes(testAttributeSet);
    	try {
            List<GroupAttributeDataImpl> attributeDataList = KimCommonUtils.copyInfoAttributesToGroupAttributes(groupInfo.getAttributes(), groupInfo.getGroupId(), groupInfo.getKimTypeId());
    		fail("Ingested a group with an unknown attributeSet");
    	}
    	catch (IllegalArgumentException expectedException) {
    		assertTrue(expectedException.getMessage().contains("not found"));
    	}
    }

    @Test
    public void testStripEnd() {
    	assertNull(KimCommonUtils.stripEnd(null, null));
    	assertEquals("", KimCommonUtils.stripEnd("", null));
    	assertEquals("", KimCommonUtils.stripEnd("", ""));
    	assertEquals("b", KimCommonUtils.stripEnd("b", ""));
    	assertEquals("", KimCommonUtils.stripEnd("b", "b"));
    	assertEquals("b", KimCommonUtils.stripEnd("b", "bb"));
    	assertEquals("wx", KimCommonUtils.stripEnd("wxyz", "yz"));
    	assertEquals("wx", KimCommonUtils.stripEnd("wxyz     ", "yz     "));
    	assertEquals("abc", KimCommonUtils.stripEnd("wxyz", "abc"));


    }
}
