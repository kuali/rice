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

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class AdminTmplMthdAftNavCreateNewBase extends AdminTmplMthdAftNavBase {

    @Test
    public void testCreateNewCancelNav() throws Exception {
        testCreateNewCancel();
        passed();
    }

    @Test
    public void testCreateNewCancelBookmark() throws Exception {
        testCreateNewCancel();
        passed();
    }

    @Test
    public void testCreateNewSaveBookmark() throws Exception {
        testCreateNewSave();
        passed();
    }

    @Test
    public void testCreateNewSaveNav() throws Exception {
        testCreateNewSave();
        passed();
    }

    @Test
    public void testCreateNewSaveSubmitBookmark() throws Exception {
        testCreateNewSaveSubmit();
        passed();
    }

    @Test
    public void testCreateNewSaveSubmitNav() throws Exception {
        testCreateNewSaveSubmit();
        passed();
    }

    @Test
    public void testCreateNewSubmitBookmark() throws Exception {
        testCreateNewSubmit();
        passed();
    }

    @Test
    public void testCreateNewSubmitNav() throws Exception {
        testCreateNewSubmit();
        passed();
    }
}
