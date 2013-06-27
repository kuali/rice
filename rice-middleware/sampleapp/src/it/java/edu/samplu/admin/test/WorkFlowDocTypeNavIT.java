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
package edu.samplu.admin.test;

import org.junit.Assert;
import org.junit.Test;

/**
 * tests creating and cancelling new and edit Document Type maintenance screens
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowDocTypeNavIT extends AdminTmplMthdSTNavBase {

    @Override
    public void fail(String message) {
        Assert.fail(message);
    }

    @Override
    public String getLinkLocator() {
        return "Document Type";
    }

    @Test
    /**
     * tests that a new Document Type maintenance document can be cancelled
     */
    public void testCreateNew() throws Exception {
        super.testCreateNewCancel();
    }

    @Test
    /**
     * tests that a Document Type maintenance document is created for an edit operation originating from a lookup screen
     */
    public void testEditDocType() throws Exception {
        gotoMenuLinkLocator();
        super.testSearchEditCancel();
    }

    //Test to validate the requirement of Document Type Label field while submitting a document.
    @Test
    public void testCreateDocType() throws Exception {
        gotoMenuLinkLocator();
        super.testCreateDocType();
    }
}
