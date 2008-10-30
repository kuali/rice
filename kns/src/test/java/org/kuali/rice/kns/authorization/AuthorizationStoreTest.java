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
package org.kuali.rice.kns.authorization;

import org.junit.Test;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kns.exception.GroupNotFoundException;
import org.kuali.test.KNSTestBase;
import org.kuali.test.KNSWithTestSpringContext;

@KNSWithTestSpringContext
public class AuthorizationStoreTest extends KNSTestBase {
    private static final String NAME_OF_WORKGROUP_WITH_NO_MEMBERS = "KUALI_PED";
    private static final String LEGIT_WORKGROUP_NAME_1 = "KUALI_OPERATIONS";
    private static final String LEGIT_WORKGROUP_NAME_2 = "KUALI_ROLE_MAINTENANCE";

    private static final String NAME_OF_USER_THAT_BELONGS_TO_NO_WORKGROUPS = "quickstart";
    private static final String NAME_OF_USER_THAT_BELONGS_TO_LEGIT_WORKGROUP_NAME_1 = "user4";
    private static final String NAME_OF_USER_THAT_BELONGS_TO_LEGIT_WORKGROUP_NAME_2 = "fred";
    private static final String NAME_OF_USER_THAT_BELONGS_TO_LEGIT_WORKGROUPS_1_AND_2 = "fran";

    private static final String ACTION1 = "action1";
    private static final String ACTION2 = "action2";

    private static final String TARGETTYPE1 = "target1";
    private static final String TARGETTYPE2 = "target2";

    KimGroup workgroupWithNoMembers;
    KimGroup legitWorkgroupOne;
    KimGroup legitWorkgroupTwo;

    Person userThatBelongsToNoWorkgroups;
    Person userThatBelongsToLegitWorkgroupOne;
    Person userThatBelongsToLegitWorkgroupTwo;
    Person userThatBelongsToAllWorkgroups;

    AuthorizationStore authorizationStore;

    @Override
	public void setUp() throws Exception {
        super.setUp();
        
        authorizationStore = new AuthorizationStore();

        workgroupWithNoMembers = buildGroup(NAME_OF_WORKGROUP_WITH_NO_MEMBERS);
        legitWorkgroupOne = buildGroup(LEGIT_WORKGROUP_NAME_1);
        legitWorkgroupTwo = buildGroup(LEGIT_WORKGROUP_NAME_2);

        userThatBelongsToNoWorkgroups = buildUser(NAME_OF_USER_THAT_BELONGS_TO_NO_WORKGROUPS, new KimGroup[] {});
        userThatBelongsToLegitWorkgroupOne = buildUser(NAME_OF_USER_THAT_BELONGS_TO_LEGIT_WORKGROUP_NAME_1, new KimGroup[] { legitWorkgroupOne });
        userThatBelongsToLegitWorkgroupTwo = buildUser(NAME_OF_USER_THAT_BELONGS_TO_LEGIT_WORKGROUP_NAME_2, new KimGroup[] { legitWorkgroupTwo });
        userThatBelongsToAllWorkgroups = buildUser(NAME_OF_USER_THAT_BELONGS_TO_LEGIT_WORKGROUPS_1_AND_2, new KimGroup[] { legitWorkgroupOne, legitWorkgroupTwo });
    }

    @Test public final void testAddAuthorization_emptyGroupName() {
        
        boolean failedAsExpected = false;

        try {
            authorizationStore.addAuthorization(null, ACTION1, TARGETTYPE1);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public final void testAddAuthorization_emptyAction() {
        boolean failedAsExpected = false;

        try {
            authorizationStore.addAuthorization(LEGIT_WORKGROUP_NAME_1, "", TARGETTYPE1);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public final void testAddAuthorization_emptyTargetType() {
        boolean failedAsExpected = false;

        try {
            authorizationStore.addAuthorization(LEGIT_WORKGROUP_NAME_1, ACTION1, "     ");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public final void testAddAuthorization() {
        authorizationStore.addAuthorization(LEGIT_WORKGROUP_NAME_1, ACTION1, TARGETTYPE1);
        assertTrue(authorizationStore.isAuthorized(userThatBelongsToLegitWorkgroupOne, ACTION1, TARGETTYPE1));
    }

    @Test public final void testIsAuthorized_nullUser() {
        boolean failedAsExpected = false;

        try {
            authorizationStore.isAuthorized(null, ACTION1, TARGETTYPE1);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public final void testIsAuthorized_blankAction() {
        boolean failedAsExpected = false;

        try {
            authorizationStore.isAuthorized(userThatBelongsToLegitWorkgroupOne, "    ", TARGETTYPE1);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public final void testIsAuthorized_nullTarget() {
        boolean failedAsExpected = false;

        try {
            authorizationStore.isAuthorized(userThatBelongsToLegitWorkgroupOne, ACTION1, null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @Test public final void testIsAuthorized_noAuthorizations() {
        assertFalse(authorizationStore.isAuthorized(userThatBelongsToNoWorkgroups, ACTION1, TARGETTYPE1));
    }

    @Test public final void testIsAuthorized_unauthorizedGroup() {
        authorizationStore.addAuthorization(LEGIT_WORKGROUP_NAME_1, ACTION1, TARGETTYPE1);

        assertFalse(authorizationStore.isAuthorized(userThatBelongsToLegitWorkgroupTwo, ACTION1, TARGETTYPE1));
    }

    @Test public final void testIsAuthorized_authorizedGroup_wrongAction() {
        authorizationStore.addAuthorization(LEGIT_WORKGROUP_NAME_1, ACTION1, TARGETTYPE1);

        assertFalse(authorizationStore.isAuthorized(userThatBelongsToLegitWorkgroupOne, ACTION2, TARGETTYPE1));
    }

    @Test public final void testIsAuthorized_authorizedGroup_wrongTarget() {
        authorizationStore.addAuthorization(LEGIT_WORKGROUP_NAME_1, ACTION1, TARGETTYPE1);

        assertFalse(authorizationStore.isAuthorized(userThatBelongsToLegitWorkgroupOne, ACTION1, TARGETTYPE2));
    }

    @Test public final void testIsAuthorized_singleGroupAuth_singleGroupUser() {
        authorizationStore.addAuthorization(LEGIT_WORKGROUP_NAME_1, ACTION1, TARGETTYPE1);

        assertTrue(authorizationStore.isAuthorized(userThatBelongsToLegitWorkgroupOne, ACTION1, TARGETTYPE1));
    }

    @Test public final void testIsAuthorized_multiGroupAuth_singleGroupUser() {
        authorizationStore.addAuthorization(LEGIT_WORKGROUP_NAME_1, ACTION1, TARGETTYPE1);
        authorizationStore.addAuthorization(LEGIT_WORKGROUP_NAME_2, ACTION1, TARGETTYPE1);

        assertTrue(authorizationStore.isAuthorized(userThatBelongsToLegitWorkgroupOne, ACTION1, TARGETTYPE1));
    }

    @Test public final void testIsAuthorized_singleGroupAuth_multiGroupUser() {
        authorizationStore.addAuthorization(LEGIT_WORKGROUP_NAME_1, ACTION1, TARGETTYPE1);

        assertTrue(authorizationStore.isAuthorized(userThatBelongsToAllWorkgroups, ACTION1, TARGETTYPE1));
    }

    @Test public final void testIsAuthorized_multiGroupAuth_multiGroupUser() {
        authorizationStore.addAuthorization(LEGIT_WORKGROUP_NAME_1, ACTION1, TARGETTYPE1);
        authorizationStore.addAuthorization(LEGIT_WORKGROUP_NAME_2, ACTION1, TARGETTYPE1);

        assertTrue(authorizationStore.isAuthorized(userThatBelongsToAllWorkgroups, ACTION1, TARGETTYPE1));
    }

    private KimGroup buildGroup(String groupName) throws GroupNotFoundException {
        KimGroup group = org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().getGroupByName("KFS", groupName);
        if(null == group) {
            return group;
        }
        //((KimGroupImpl)group).setGroupName(groupName);
        return group;
    }

    private Person buildUser(String userName, KimGroup[] groups) {
        Person user = org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPersonByPrincipalName(userName);
        if(null == user) {
            return user;
        }
        //user.setGroups(Arrays.asList(groups));
        return user;
    }
}

