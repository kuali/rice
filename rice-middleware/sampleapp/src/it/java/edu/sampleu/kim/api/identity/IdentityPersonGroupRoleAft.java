/**
 * Copyright 2005-2014 The Kuali Foundation
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
package edu.sampleu.kim.api.identity;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

import java.util.StringTokenizer;

/**
 * Sets up Person Roles for load-testing
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityPersonGroupRoleAft extends WebDriverLegacyITBase {

    public static final String EDIT_URL = WebDriverUtils.getBaseUrlString() + "/kim/identityManagementPersonDocument.do?&principalId=LTID&docTypeName=IdentityManagementPersonDocument&methodToCall=docHandler&command=initiate";
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Person&channelUrl=" + WebDriverUtils
            .getBaseUrlString() +
            "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.api.identity.Person&docFormKey=88888888&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + "&hideReturnLink=true";
    private int userCnt = Integer.valueOf(System.getProperty("test.role.user.cnt", "2")); // set to 176 for load testing
    private int userCntStart = Integer.valueOf(System.getProperty("test.role.user.cnt.start", "1"));  // set to 0 for load testing
    private String idBase = System.getProperty("test.role.user.base", "testadmin"); // set to lt for load testing
    private static final String ROLE_ID_ADMIN = "63";
    private static final String ROLE_ID_KRMS_ADMIN = "98";
    private static final String ROLE_ID_SAP = "KRSAP10004";
    private static final String[] ROLES = {ROLE_ID_ADMIN, ROLE_ID_KRMS_ADMIN, ROLE_ID_SAP};

    private static final String GROUP_WORKFLOW_ADMIN_ID = "1";
    private static final String GROUP_NOTIFICATION_ADMIN_ID = "2000";
    private static final String[] GROUPS = {GROUP_WORKFLOW_ADMIN_ID, GROUP_NOTIFICATION_ADMIN_ID};

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Test
    public void testPersonRoleBookmark() throws InterruptedException {
        testPersonRole();
        passed();
    }

    @Test
    public void testPersonRoleUserListBookmark() throws InterruptedException {
        testPersonRoleUserList();
        passed();
    }

    private void testPersonRole() throws InterruptedException {
        String id = "";
        String format = "%0" + (userCnt + "").length() + "d";
        for(int i = userCntStart; i < userCnt; i++) {
            id = idBase + i;
// I'ld like to use numbers with leading zeros, but the XmlIngester which creates the users, uses SimpleUserContent.ftl, which doesn't.
// creating a new version which uses UserListIngestion.ftl might be the way to go.  JMeter's user counter config would also need to be formatted.
//            id = idBase + String.format(format, i);
            addPerson(id);
        }
    }

    private void testPersonRoleUserList() throws InterruptedException {
        String usersArg = System.getProperty("xmlingester.user.list", "test1,test2").replace(".", "").replace("-", "").toLowerCase();
        StringTokenizer token = new StringTokenizer(usersArg, ",");
        while (token.hasMoreTokens()) {
            addPerson(token.nextToken());
        }
    }

    private void addPerson(String id) throws InterruptedException {
        open(EDIT_URL.replace("LTID", id));
        waitAndTypeByName("document.documentHeader.documentDescription", "Admin permissions for " + id); // don't make unique

        if (noAffilication()) {
            addAffiliation();
        }

        waitAndClick(By.id("tab-Membership-imageToggle"));
        addGroups();
        addRoles();

        waitAndClickByName("methodToCall.blanketApprove");
        waitForPageToLoad();
    }

    private void addAffiliation() throws InterruptedException {
        selectByName("newAffln.affiliationTypeCode", "Affiliate");
        selectOptionByName("newAffln.campusCode", "BL");
        checkByName("newAffln.dflt");
        waitAndClickByName("methodToCall.addAffln.anchor");
    }

    private void addGroups() throws InterruptedException {
        for (String groupId : GROUPS) {
            waitAndTypeByName("newGroup.groupId", groupId);
            waitAndClickByName("methodToCall.addGroup.anchor");
        }
    }

    private void addRoles() throws InterruptedException {
        for (String roleId : ROLES) {
            waitAndTypeByName("newRole.roleId", roleId);
            waitAndClickByName("methodToCall.addRole.anchor");
        }
    }
}
