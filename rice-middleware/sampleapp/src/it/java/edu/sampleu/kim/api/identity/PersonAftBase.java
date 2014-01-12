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

import edu.sampleu.admin.AdminTmplMthdAftNavBase;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class PersonAftBase extends AdminTmplMthdAftNavBase {

    /**
     * ITUtil.PORTAL + "?channelTitle=Person&channelUrl=" 
     * + WebDriverUtils.getBaseUrlString() + ITUtil.KNS_LOOKUP_METHOD + "org.kuali.rice.kim.api.identity.Person&docFormKey=88888888&returnLocation=" +
     * ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Person&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KNS_LOOKUP_METHOD +
            "org.kuali.rice.kim.api.identity.Person&docFormKey=88888888&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK ;

    private String docId;
    private String personName;

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
   
    public void testPersonBookmark(JiraAwareFailable failable) throws Exception {
        //Create New Person
        selectFrame("iframeportlet");
        waitAndCreateNew();
        List<String> params;
        params=testCreateNewPerson(docId, personName);
        
        //LookUp Person
        selectTopFrame();
        open(WebDriverUtils.getBaseUrlString()+BOOKMARK_URL);
        selectFrame("iframeportlet");
        params=testLookUpPerson(params.get(0), params.get(1));

        //Verify Person
        testVerifyPerson(params.get(0), params.get(1));
        passed();
    }

    public void testPersonNav(JiraAwareFailable failable) throws Exception {
        //Create New Person
        gotoCreateNew();
        List<String> params;
        params=testCreateNewPerson(docId, personName);
        
        //LookUp Person
        selectTopFrame();
        navigate();
        params=testLookUpPerson(params.get(0), params.get(1));

        //Verify Person
        testVerifyPerson(params.get(0), params.get(1));
        passed();
    }
}
