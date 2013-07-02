/*
 * Copyright 2006-2013 The Kuali Foundation
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

package edu.samplu.krad.demo.uif.library.controls.checkbox;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryControlCheckboxDefaultSTJUnitBkMrkGen extends DemoLibraryControlCheckboxDefaultSTJUnitBase {

    @Override
    public String getTestUrl() {
        return BOOKMARK_URL;
    }

    @Test
    public void testCheckboxControlDefaultBookmark() throws Exception {
        testCheckboxControlDefaultBookmark(this);
    }

    @Test
    public void testCheckboxControlOptionsFinderBookmark() throws Exception {
        testCheckboxControlOptionsFinderBookmark(this);
    }

    @Test
    public void testCheckboxControlKeyValuePairBookmark() throws Exception {
        testCheckboxControlKeyValuePairBookmark(this);
    }

    @Test
    public void testCheckboxControlDisabledBookmark() throws Exception {
        testCheckboxControlDisabledBookmark(this);
    }

    @Test
    public void testCheckboxControlDelimiterBookmark() throws Exception {
        testCheckboxControlDelimiterBookmark(this);
    }

    @Test
    public void testCheckboxControlDisabledOnKeyEventBookmark() throws Exception {
        testCheckboxControlDisabledOnKeyEventBookmark(this);
    }

    @Test
    public void testCheckboxControlEnableWhenChangedBookmark() throws Exception {
        testCheckboxControlEnableWhenChangedBookmark(this);
    }

    @Test
    public void testCheckboxControlDisableWhenChangedBookmark() throws Exception {
        testCheckboxControlDisableWhenChangedBookmark(this);
    }
}
