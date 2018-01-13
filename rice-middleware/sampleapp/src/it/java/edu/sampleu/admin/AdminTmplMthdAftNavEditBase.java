/**
 * Copyright 2005-2018 The Kuali Foundation
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

import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class AdminTmplMthdAftNavEditBase extends AdminTmplMthdAftNavBlanketAppBase {

    public void testLookUpEditSave() throws Exception {
        String docId = testCreateNewSubmit(); // create a new doc we are going to copy

        lookupDocByWildcardedUniqueStringName();

        waitAndClickByLinkText("edit");

        testEditCopy(docId);
    }

    @Test
    public void testLookUpEditSaveBookmark() throws Exception {
        testLookUpEditSave();
        passed();
    }

    @Test
    public void testLookUpEditSaveNav() throws Exception {
        testLookUpEditSave();
        passed();
    }
}