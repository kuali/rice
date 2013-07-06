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
package edu.samplu.admin.config.namespace.pending.fyi;

import org.apache.commons.lang.RandomStringUtils;

import edu.samplu.admin.test.AdminTmplMthdSTNavBase;
import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class PersonPendingFYIAbstractSmokeTestBase extends AdminTmplMthdSTNavBase{

    /**
     * ITUtil.PORTAL+"?channelTitle=Namespace&channelUrl="+ITUtil.getBaseUrlString()+ITUtil..KNS_LOOKUP_METHOD
     * +"org.kuali.rice.coreservice.impl.namespace.NamespaceBo&docFormKey=88888888&returnLocation="
     * +ITUtil.PORTAL_URL+ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = ITUtil.PORTAL+"?channelTitle=Namespace&channelUrl="
            + ITUtil.getBaseUrlString() + ITUtil.KNS_LOOKUP_METHOD
            + "org.kuali.rice.coreservice.impl.namespace.NamespaceBo" + "&docFormKey=88888888&returnLocation="
            + ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;

    /**
     * methodToCall.insertAdHocRoutePerson
     */
    private static final String ADD_PERSON_ELEMENT_NAME = "methodToCall.insertAdHocRoutePerson";
    
    /**
     * newAdHocRoutePerson.id
     */
    private static final String ADD_PERSON_TEXT_ELEMENT_NAME = "newAdHocRoutePerson.id";
    
    /**
     * document.documentHeader.documentDescription
     */
    private static final String DOCUMENT_DESCRIPTION_NAME = "document.documentHeader.documentDescription";
    
    /**
     * document.newMaintainableObject.code
     */
    private static final String DOCUMENT_CODE_NAME = "document.newMaintainableObject.code";
    
    /**
     * document.newMaintainableObject.name
     */
    private static final String DOCUMENT_NAME = "document.newMaintainableObject.name";
    
    /**
     * document.newMaintainableObject.applicationId
     */
    private static final String DOCUMENT_APPLICATIONID_NAME = "document.newMaintainableObject.applicationId";
    
    /**
     * (//input[@name='methodToCall.search'])[2]
     */
    private static final String SEARCH_XPATH = "(//input[@name='methodToCall.search'])[2]";       
    
    /**
     * methodToCall.route
     */
    private static final String SUBMIT_NAME = "methodToCall.route";
    
    /**
     * {@inheritDoc}
     * Namespace
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Namespace";
    }

    public void testPersonPendingFYIBookmark(Failable failable) throws Exception {
        testPersonPendingFYI();
        passed();
    }

    public void testPersonPendingFYINav(Failable failable) throws Exception {
        testPersonPendingFYI();
        passed();
    }

    protected void testPersonPendingFYI() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        waitAndTypeByName(DOCUMENT_DESCRIPTION_NAME,"Test Namespace2 " + ITUtil.createUniqueDtsPlusTwoRandomCharsNot9Digits());
        String randomFour = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        waitAndTypeByName(DOCUMENT_CODE_NAME,"SUACTION2" + randomFour);
        waitAndTypeByName(DOCUMENT_NAME,"SUACTION2" + randomFour);
        waitAndTypeByName(DOCUMENT_APPLICATIONID_NAME,"KUALI");
        waitAndClickByName("methodToCall.toggleTab.tabAdHocRecipients");
        selectByName("newAdHocRoutePerson.actionRequested", "FYI");
        waitAndTypeByName(ADD_PERSON_TEXT_ELEMENT_NAME,"frank");
        waitAndClickByName(ADD_PERSON_ELEMENT_NAME);
        selectByName("newAdHocRoutePerson.actionRequested", "ACKNOWLEDGE");
        waitAndTypeByName(ADD_PERSON_TEXT_ELEMENT_NAME,"eric");
        waitAndClickByName(ADD_PERSON_ELEMENT_NAME);
        selectByName("newAdHocRoutePerson.actionRequested", "APPROVE");
        waitAndTypeByName(ADD_PERSON_TEXT_ELEMENT_NAME,"fran");
        waitAndClickByName(ADD_PERSON_ELEMENT_NAME);
        waitAndClickByName(SUBMIT_NAME);
        String docId= waitForDocId();
        switchToWindow("Kuali Portal Index");
        waitAndClickDocSearch();
        selectFrameIframePortlet();
        waitAndTypeByName("documentId",docId);
        waitAndClickByXpath(SEARCH_XPATH);
        waitAndClickByLinkText(docId);
        switchToWindow("Kuali :: Namespace");
        waitAndClickByName("selectedActionRequests");
        waitAndTypeByName("superUserAnnotation","test suaction");
        waitAndClickByName("methodToCall.takeSuperUserActions");
        if(!isTextPresent("frank"))
        {
            assertTextPresent("superuser approved in Document "+docId);
        }
        else
        {
            fail("Super User Approve functionality may not be working.");
        }
        
    }
}
