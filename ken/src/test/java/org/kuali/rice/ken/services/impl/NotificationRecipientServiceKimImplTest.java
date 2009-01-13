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
package org.kuali.rice.ken.services.impl;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.ken.service.impl.NotificationRecipientServiceKimImpl;
import org.kuali.rice.ken.test.NotificationTestCaseBase;
import org.kuali.rice.ken.test.TestConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * This is a description of what this class does - chb don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class NotificationRecipientServiceKimImplTest extends NotificationTestCaseBase
{
    NotificationRecipientServiceKimImpl nrski = new NotificationRecipientServiceKimImpl();
    /**
     * Test method for {@link org.kuali.rice.ken.service.impl.NotificationRecipientServiceKimImpl#getGroupMembers(java.lang.String)}.
     */
    @Test
    public void testGetGroupMembersValid()
    {
        KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroupByName(Utilities.parseGroupNamespaceCode(TestConstants.VALID_KIM_GROUP_NAME_1), Utilities.parseGroupName(TestConstants.VALID_KIM_GROUP_NAME_1));
        assertTrue(nrski.getGroupMembers(group.getGroupId()).length == TestConstants.KIM_GROUP_1_MEMBERS);
    }

    /**
     * Test method for {@link org.kuali.rice.ken.service.impl.NotificationRecipientServiceKimImpl#getUserDisplayName(java.lang.String)}.
     */
    @Test
    @Ignore
    public final void testGetUserDisplayName()
    {
        //hoping gary will take care of this when he does KEW user conversion
        fail("Not yet implemented"); // TODO
    }

    /**
     * Test method for {@link org.kuali.rice.ken.service.impl.NotificationRecipientServiceKimImpl#isGroupRecipientValid(java.lang.String)}.
     */
    @Test
    public final void testIsGroupRecipientValid()
    {
        KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroupByName(Utilities.parseGroupNamespaceCode(TestConstants.VALID_KIM_GROUP_NAME_1), Utilities.parseGroupName(TestConstants.VALID_KIM_GROUP_NAME_1));
        assertTrue(nrski.isGroupRecipientValid(group.getGroupId()));
    }

    /**
     * Test method for {@link org.kuali.rice.ken.service.impl.NotificationRecipientServiceKimImpl#isRecipientValid(java.lang.String, java.lang.String)}.
     */
    @Test
    public final void testIsRecipientValid()
    {
        assertTrue( nrski.isRecipientValid( TestConstants.VALID_KIM_PRINCIPAL_NAME, KimGroupImpl.PRINCIPAL_MEMBER_TYPE));
        assertFalse( nrski.isRecipientValid( "BoogalooShrimp44", KimGroupImpl.PRINCIPAL_MEMBER_TYPE));

        KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroupByName(Utilities.parseGroupNamespaceCode(TestConstants.VALID_KIM_GROUP_NAME_1), Utilities.parseGroupName(TestConstants.VALID_KIM_GROUP_NAME_1));
        assertTrue( nrski.isRecipientValid( group.getGroupId(), KimGroupImpl.GROUP_MEMBER_TYPE));
        assertFalse( nrski.isRecipientValid( "FooSchnickens99", KimGroupImpl.GROUP_MEMBER_TYPE));
    }

    /**
     * Test method for {@link org.kuali.rice.ken.service.impl.NotificationRecipientServiceKimImpl#isUserRecipientValid(java.lang.String)}.
     */
    @Test
    public final void testIsUserRecipientValid()
    {
        assertTrue( nrski.isUserRecipientValid( TestConstants.VALID_KIM_PRINCIPAL_NAME ));
    }

}
