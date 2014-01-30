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
public class ConfigComponentActionListBkMrkAft extends ConfigComponentActionListAftBase {

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Test
    public void testActionListApproveBookmark() throws Exception {
        testActionListApprove();
    }

    @Test
    public void testActionListDisapproveBookmark() throws Exception {
        testActionListDisapprove();
    }

    @Test
    public void testActionListCompleteBookmark() throws Exception {
        testActionListComplete();
    }

    @Test
    public void testActionListAcknowledgeBookmark() throws Exception {
        testActionListAcknowledge();
    }

    @Test
    public void testActionListFyiBookmark() throws Exception {
        testActionListFyi();
    }
}
