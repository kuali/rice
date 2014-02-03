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

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class WorkFlowDocTypeAftBase extends AdminTmplMthdAftNavBase {

    /**
     * ITUtil.PORTAL + "?channelTitle=Document%20Type&channelUrl=" 
     * + WebDriverUtils.getBaseUrlString() + ITUtil.KNS_LOOKUP_METHOD + "org.kuali.rice.kew.doctype.bo.DocumentType&returnLocation=" +
     * ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK + ITUtil.DOC_FORM_KEY+ "88888888";
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Document%20Type&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KNS_LOOKUP_METHOD +
            "org.kuali.rice.kew.doctype.bo.DocumentType&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK + AutomatedFunctionalTestUtils.DOC_FORM_KEY+ "88888888";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Document Type
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Document Type";
    }

    @Override
    protected void createNewEnterDetails() throws InterruptedException {
        waitAndTypeByName("document.documentHeader.documentDescription", getDescriptionUnique());
        jiraAwareTypeByName("document.newMaintainableObject.name", "name" + uniqueString);
        jiraAwareTypeByName("document.newMaintainableObject.label", "label" + uniqueString);
    }

    public void testWorkFlowDocTypeBookmark(JiraAwareFailable failable) throws Exception {
        testCreateNewCancel();
        driver.navigate().to(WebDriverUtils.getBaseUrlString() + BOOKMARK_URL);
        testSearchEditCancel();
        driver.navigate().to(WebDriverUtils.getBaseUrlString() + BOOKMARK_URL);
        testCreateDocType();
        passed();
    }

    public void testWorkFlowDocTypeNav(JiraAwareFailable failable) throws Exception {
        testCreateNewCancel();
        navigate();
        testSearchEditCancel();
        navigate();
        testCreateDocType();
        passed();
    }
}
