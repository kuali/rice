/**
 * Copyright 2005-2017 The Kuali Foundation
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

import edu.sampleu.admin.AdminTmplMthdAftNavBase;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class IdentityPersonLookUpEditAftBase extends AdminTmplMthdAftNavBase {

    /**
     * ITUtil.PORTAL + "?channelTitle=Person&channelUrl=" 
     * + WebDriverUtils.getBaseUrlString() + ITUtil.KNS_LOOKUP_METHOD + "org.kuali.rice.kim.api.identity.Person&docFormKey=88888888&returnLocation=" +
     * ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */    
//    http://env2.rice.kuali.org/portal.do?channelTitle=Person&channelUrl=http://env2.rice.kuali.org/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.api.identity.Person&docFormKey=88888888&returnLocation=http://env2.rice.kuali.org/portal.do&hideReturnLink=true
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Person&channelUrl="
            + WebDriverUtils.getBaseUrlString() + "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.api.identity.Person&docFormKey=88888888&returnLocation="+
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK ;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Person
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Person";
    }
   
    public void testIdentityPersonLookUpEdit() throws Exception {
        String randomSalary = RandomStringUtils.randomNumeric(6);
        String randomMiddleName = RandomStringUtils.randomAlphabetic(6);;
        String randomAddress = RandomStringUtils.randomNumeric(6);;
        String randomExtension = RandomStringUtils.randomNumeric(6);;
        String randomEmail = RandomStringUtils.randomAlphabetic(6);;

        selectFrameIframePortlet();
        waitAndTypeByName("principalName","fran");
        waitAndClickSearchSecond();
        selectFrameIframePortlet();
        waitAndClickByXpath("//a[@title='edit Person withPrincipal ID=fran ']");
        waitAndTypeByName("document.documentHeader.documentDescription", "Test description of person");

        // Add new information if it is not present.  Otherwise, edit the existing information.
        if (isElementPresentByName("document.affiliations[0].campusCode")) {
            clearTextByName("document.affiliations[0].empInfos[0].baseSalaryAmount");
            waitAndTypeByName("document.affiliations[0].empInfos[0].baseSalaryAmount", "1" + randomSalary);
        } else {
            selectByName("newAffln.affiliationTypeCode", "Staff");
            selectByName("newAffln.campusCode", "BL - BLOOMINGTON");
            waitAndClickByName("newAffln.dflt");
            waitAndClickByName("methodToCall.addAffln.anchor");
            waitAndTypeByName("document.affiliations[0].newEmpInfo.employeeId", "9999999999");
            waitAndClickByName("document.affiliations[0].newEmpInfo.primary");
            selectByName("document.affiliations[0].newEmpInfo.employmentStatusCode", "Active");
            selectByName("document.affiliations[0].newEmpInfo.employmentTypeCode", "Professional");
            waitAndTypeByName("document.affiliations[0].newEmpInfo.baseSalaryAmount", "1" + randomSalary);
            waitAndTypeByXpath("//*[@id='document.affiliations[0].newEmpInfo.primaryDepartmentCode']", "BL-BUS");
            waitAndClickByName("methodToCall.addEmpInfo.line0.anchor");
        }

        waitAndClickByName("methodToCall.showAllTabs");

        // Add nick name or edit the existing one
        if (isTextPresent("CrazyNickName")) {
            clearTextByName("document.names[1].middleName");
            waitAndTypeByName("document.names[1].middleName", randomMiddleName);
        } else {
            waitAndTypeByName("newName.firstName", "CrazyNickName");
            waitAndTypeByName("newName.middleName", randomMiddleName);
            waitAndTypeByName("newName.lastName", "fran");
            waitAndClickByName("methodToCall.addName.anchor");
        }

        // Add address or edit the existing one
        if (isTextPresent("CrazyHomeAddress")) {
            clearTextByName("document.addrs[0].line1");
            waitAndTypeByName("document.addrs[0].line1", randomAddress + " Main St");
        } else {
            waitAndTypeByName("newAddress.line1", randomAddress + " Main St");
            waitAndTypeByName("newAddress.line2", "CrazyHomeAddress");
            waitAndTypeByName("newAddress.city", "Bloomington");
            selectByName("newAddress.stateProvinceCode", "ALASKA");
            waitAndTypeByName("newAddress.postalCode", "61821");
            selectByName("newAddress.countryCode", "United States");
            waitAndClickByName("newAddress.dflt");
            waitAndClickByName("methodToCall.addAddress.anchor");
        }

        // Add phone number or edit the existing one
        if (isTextPresent("555-555-5555")) {
            clearTextByName("document.phones[0].extensionNumber");
            waitAndTypeByName("document.phones[0].extensionNumber", randomExtension);
        } else {
            waitAndTypeByName("newPhone.phoneNumber", "555-555-5555");
            waitAndTypeByName("newPhone.extensionNumber", randomExtension);
            selectByName("newPhone.countryCode", "United States");
            waitAndClickByName("newPhone.dflt");
            waitAndClickByName("methodToCall.addPhone.anchor");
        }

        // Add home email or edit the existing one
        if (isTextPresent("@gmailCrazy.com")) {
            clearTextByName("document.emails[1].emailAddress");
            waitAndTypeByName("document.emails[1].emailAddress", randomEmail + "@gmailCrazy.com");
        } else {
            waitAndTypeByName("newEmail.emailAddress", randomEmail + "@gmailCrazy.com");
            waitAndClickByName("methodToCall.addEmail.anchor");
        }

        waitAndClickByName("methodToCall.route");
        waitForTextPresent("Document was successfully submitted.");

        selectTopFrame();
        waitAndClickAdministration();
        waitAndClickByLinkText("Person");
        selectFrameIframePortlet();
        waitAndTypeByName("principalName","fran");
        waitAndClickSearchSecond();
        selectFrameIframePortlet();
        waitAndClickByXpath("//a[@title='edit Person withPrincipal ID=fran ']");
        waitAndClickByName("methodToCall.showAllTabs");

        waitForTextPresent("1," + StringUtils.substring(randomSalary, 0, 3) + ","
                + StringUtils.substring(randomSalary, 3, 6) + ".00");
        assertTextPresent(randomMiddleName);
        assertTextPresent(randomAddress);
        assertTextPresent(randomExtension);
        assertTextPresent(randomEmail);

        testCancelConfirmation();
    }
}
