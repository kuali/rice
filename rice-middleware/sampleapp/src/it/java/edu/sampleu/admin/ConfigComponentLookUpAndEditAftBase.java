/**
 * Copyright 2005-2013 The Kuali Foundation
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

import org.kuali.rice.testtools.common.Failable;
import org.kuali.rice.testtools.selenium.ITUtil;
import org.kuali.rice.testtools.selenium.WebDriverUtil;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigComponentLookUpAndEditAftBase extends AdminTmplMthdAftNavBase {

    /**
     * ITUtil.PORTAL+"?channelTitle=Component&channelUrl="+WebDriverUtil.getBaseUrlString()+
     * "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation="+
     * +ITUtil.PORTAL_URL+ ITUtil.HIDE_RETURN_LINK;
     */    
    public static final String BOOKMARK_URL = ITUtil.PORTAL+"?channelTitle=Component&channelUrl="+ WebDriverUtil
            .getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation="+
            ITUtil.PORTAL_URL+ ITUtil.HIDE_RETURN_LINK;

    /**
     * {@inheritDoc}
     * Component
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Component";
    }

    public void testConfigComponentLookUpAndEditBookmark(Failable failable) throws Exception {
        testConfigComponentLookUpAndEdit();
        passed();
    }

    public void testConfigComponentLookUpAndEditNav(Failable failable) throws Exception {
        testConfigComponentLookUpAndEdit();
        passed();
    }    
    
    public void testConfigComponentLookUpAndEdit() throws Exception {
        selectFrameIframePortlet();
        waitAndClickByXpath("(//input[@name='methodToCall.search'])[2]", "Probably KULRICE-10763 500 Error Lookup not defined for business object class org.kuali.rice.coreservice.impl.component.ComponentBo");
        waitAndClickByLinkText("edit");
        waitAndTypeByName("document.documentHeader.documentDescription","Test description of Component");
        waitAndClickByName("methodToCall.route");
        checkForDocError();
        waitAndClickByName("methodToCall.close");
        waitAndClickByName("methodToCall.processAnswer.button1");        
    }
}
