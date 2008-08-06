/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.rice.kns.service;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.kns.KNSServiceLocator;
import org.kuali.rice.kns.bo.user.AuthenticationUserId;
import org.kuali.rice.kns.bo.user.KualiGroup;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.test.KNSTestBase;

/**
 * This class tests the KualiGroup service.
 */
public class KualiGroupServiceTest extends KNSTestBase {

    private static final String TEST_GROUP_NAME = "KUALI_ROLE_SUPERVISOR";
    private static final String TEST_GROUP_USER = "admin";

    private UniversalUser universalUser;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        universalUser = KNSServiceLocator.getUniversalUserService().getUniversalUser(new AuthenticationUserId(TEST_GROUP_USER));
    }

    @Test public void testGetByGroupName() throws Exception {
        KualiGroup kualiGroup = KNSServiceLocator.getKualiGroupService().getByGroupName(TEST_GROUP_NAME);
        assertTrue(kualiGroup.hasMember(universalUser));
    }

    @Test public void testGetUsersGroups() throws Exception {
        List groups = KNSServiceLocator.getKualiGroupService().getUsersGroups(universalUser);
        assertNotNull(groups);
        assertTrue(!groups.isEmpty());

        for (Iterator iterator = groups.iterator(); iterator.hasNext();) {
            KualiGroup group = (KualiGroup) iterator.next();
            assertTrue("user not a memeber of group and should be. user=" + universalUser + "; group=" + group.getGroupName(), group.hasMember(universalUser));
        }
    }

}