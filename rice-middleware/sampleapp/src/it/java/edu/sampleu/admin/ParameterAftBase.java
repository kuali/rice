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
package edu.sampleu.admin;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ParameterAftBase extends AdminTmplMthdAftNavBase {

    /**
     * ITUtil.PORTAL + "?channelTitle=Parameter&channelUrl=" 
     * + WebDriverUtils.getBaseUrlString() + ITUtil.KNS_LOOKUP_METHOD + "org.kuali.rice.coreservice.impl.parameter.ParameterBo&docFormKey=88888888&returnLocation=" +
     * ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Parameter&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KNS_LOOKUP_METHOD +
            "org.kuali.rice.coreservice.impl.parameter.ParameterBo&docFormKey=88888888&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK ;

    private String docId;
    private String parameterName;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Parameter
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Parameter";
    }
   
    public void testParameterBookmark(JiraAwareFailable failable) throws Exception {
        // Create New
        selectFrame("iframeportlet");
        waitAndCreateNew();
        List<String> params;
        params=testCreateNewParameter(docId, parameterName);
       
        //Lookup
        open(WebDriverUtils.getBaseUrlString()+BOOKMARK_URL);
        selectFrame("iframeportlet");
        params=testLookUpParameter(params.get(0), params.get(1));
   
        //edit
        params=testEditParameter(params.get(0), params.get(1));
        
        //Verify if its edited
        open(WebDriverUtils.getBaseUrlString()+BOOKMARK_URL);
        selectFrame("iframeportlet");
        params=testVerifyModifiedParameter(params.get(0), params.get(1));
  
        //copy
        params=testCopyParameter(params.get(0), params.get(1));
        
        //Verify if its copied
        open(WebDriverUtils.getBaseUrlString()+BOOKMARK_URL);
        selectFrame("iframeportlet");
        testVerifyModifiedParameter(params.get(0), params.get(1));
        passed();
    }

    public void testParameterNav(JiraAwareFailable failable) throws Exception {
        //Create New
        gotoCreateNew();
        List<String> params;
        params=testCreateNewParameter(docId, parameterName);
    
        //Lookup
        navigate();
        selectFrame("iframeportlet");
        params=testLookUpParameter(params.get(0), params.get(1));

        //edit
        params=testEditParameter(params.get(0), params.get(1));
        
        //Verify if its edited
        navigate();
        params=testVerifyModifiedParameter(params.get(0), params.get(1));

        //copy
        params=testCopyParameter(params.get(0), params.get(1));
        
        //Verify if its copied
        navigate();
        testVerifyModifiedParameter(params.get(0), params.get(1));
        passed();
    }
}
