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

import org.junit.Test;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.kim.util.KimCommonUtilsInternal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit Tests for KimCommonUtils.java
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class KimCommonUtilsTest extends KIMTestCase {

    @Override
    protected String getModuleName() {
        return "kim";
    }

/*    @Test
    public void testCopyInfoAttributesToGroupAttributes() {
        IdentityManagementService identityManagementService = KimApiServiceLocator.getIdentityManagementService();

        GroupInfo groupInfo = identityManagementService.getGroupByName(KimApiConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "GroupNine");

        Map<String, String> infoAttributes = groupInfo.getAttributes();
        assertTrue(infoAttributes != null);
        assertTrue(infoAttributes.size() > 0);
        assertTrue(infoAttributes.containsKey("documentTypeName"));
        assertTrue(infoAttributes.containsValue("Doc"));
        List<GroupAttributeDataImpl> attributeDataList;
        try {
        attributeDataList = KimCommonUtilsInternal.copyInfoAttributesToGroupAttributes(groupInfo.getAttributes(), groupInfo.getGroupId(), groupInfo.getKimTypeId());
        assertTrue(attributeDataList != null);
        assertTrue(attributeDataList.size() > 0);
        assertTrue(attributeDataList.size() == groupInfo.getAttributes().size());
        boolean hasAttributeValue = true;
        for( GroupAttributeDataImpl attributeData : attributeDataList ) {
        	if( !infoAttributes.containsValue(attributeData.getAttributeValue())) {
        		hasAttributeValue = false;
        	}	
        }
        assertTrue(hasAttributeValue);
        }
    	catch (IllegalArgumentException expectedException) {
    		assertTrue(expectedException.getMessage().contains("not found"));
    		fail("Ingested a group with an unexpected data");
    	}

    }*/

    /*@Test
    public void testCopyInfoToGroup() {
        IdentityManagementService identityManagementService = KimApiServiceLocator.getIdentityManagementService();

        GroupInfo groupInfo = identityManagementService.getGroupByName(KimApiConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "GroupNine");

        GroupImpl groupImpl = new GroupImpl();
        KimCommonUtilsInternal.copyInfoToGroup(groupInfo, groupImpl);
        // Figure out why we do individual checks here, i dont think they are needed
        assertEquals(groupInfo.isActive(), groupImpl.isActive());
    }*/

    /*@Test
    public void testFailedCopyInfoAttributesToGroupAttributes() {
    	IdentityManagementService identityManagementService = KimApiServiceLocator.getIdentityManagementService();

		Group groupInfo = identityManagementService.getGroupByName(KimApiConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "GroupNine");
		Map<String, String> testMap<String, String> = groupInfo.getAttributes();
        testMap<String, String>.put("someDummyKey", "someDummyValue");
        groupInfo.setAttributes(testMap<String, String>);
    	try {
            List<GroupAttributeDataImpl> attributeDataList = KimCommonUtilsInternal.copyInfoAttributesToGroupAttributes(groupInfo.getAttributes(), groupInfo.getGroupId(), groupInfo.getKimTypeId());
    		fail("Ingested a group with an unknown Map<String, String>");
    	}
    	catch (IllegalArgumentException expectedException) {
    		assertTrue(expectedException.getMessage().contains("not found"));
    	}
    }*/

    @Test
    public void testStripEnd() {
    	assertNull(KimCommonUtilsInternal.stripEnd(null, null));
    	assertEquals("", KimCommonUtilsInternal.stripEnd("", null));
    	assertEquals("", KimCommonUtilsInternal.stripEnd("", ""));
    	assertEquals("b", KimCommonUtilsInternal.stripEnd("b", ""));
    	assertEquals("", KimCommonUtilsInternal.stripEnd("b", "b"));
    	assertEquals("b", KimCommonUtilsInternal.stripEnd("b", "bb"));
    	assertEquals("wx", KimCommonUtilsInternal.stripEnd("wxyz", "yz"));
    	assertEquals("wx", KimCommonUtilsInternal.stripEnd("wxyz     ", "yz     "));
    	assertEquals("wxyz", KimCommonUtilsInternal.stripEnd("wxyz", "abc"));


    }
}
