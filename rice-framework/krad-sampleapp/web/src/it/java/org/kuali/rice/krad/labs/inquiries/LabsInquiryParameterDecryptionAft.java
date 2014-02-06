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
package org.kuali.rice.krad.labs.inquiries;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsInquiryParameterDecryptionAft extends LabsInquiryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=KradInquirySample-PageR5C3
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=KradInquirySample-PageR5C3";
    
    /**
     * MkNJjQMvX6PWHLJdDgVyJ9RA9durPueTKPQ5P+KdoKY=
     */
    private static final String TRAVEL_ACCOUNT_TYPE_CODE_ENCRYPTED="*********";
    
    /**
     * bmkm0hqKp30=
     */
    private static final String TRAVEL_ACCOUNT_TYPE_CODE_ENCRYPTED_LIGHTBOX="*********";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    	navigateToInquiry("Inquiry Parameter Decryption");
    }

    protected void testInquiryParameterDecryption() throws InterruptedException {
    	 waitAndClickByLinkText("Link to Inquiry with an encrypted parameter that will be decrypted");
    	 waitAndClickByLinkText(TRAVEL_ACCOUNT_TYPE_CODE_ENCRYPTED);
         gotoLightBox();
    	 String[][] LabeledTextLightBox = {{"Travel Account Type Code:", TRAVEL_ACCOUNT_TYPE_CODE_ENCRYPTED_LIGHTBOX},
        		 {"Account Type Name:","Clearing"},
                 {"Account Type:", "CAT - Clearing"}
                 };
         assertLabeledTextPresent(LabeledTextLightBox);
         clickCollapseAll();
         assertLabeledTextNotPresent(LabeledTextLightBox);
         clickExpandAll();
         assertLabeledTextPresent(LabeledTextLightBox);  
         waitAndClickButtonByText(CLOSE);
         selectTopFrame();
         String[][] LabeledText = {{"Travel Account Type Code:", TRAVEL_ACCOUNT_TYPE_CODE_ENCRYPTED},
        		 {"Account Type Name:","Clearing"},
                 {"Account Type:", "CAT - Clearing"}
                 };
         assertLabeledTextPresent(LabeledText);
         clickCollapseAll();
         assertLabeledTextNotPresent(LabeledText);
         clickExpandAll();
         assertLabeledTextPresent(LabeledText);      
    }

    @Test
    public void testInquiryParameterDecryptionBookmark() throws Exception {
    	testInquiryParameterDecryption();
        passed();
    }

    @Test
    public void testInquiryParameterDecryptionNav() throws Exception {
    	testInquiryParameterDecryption();
        passed();
    }
}
