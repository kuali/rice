/*
 * Copyright 2006-2012 The Kuali Foundation
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

package edu.samplu.krad.compview;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;
import org.openqa.selenium.internal.seleniumemulation.GetValue;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

/**
 * tests that a line in a sub collection can be deleted
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DeleteSubCollectionLineWDIT extends WebDriverLegacyITBase{
    @Override
    public String getTestUrl() {
        return  "/kr-krad/uicomponents?viewId=UifCompView_KNS&methodToCall=start&readOnlyFields=field91";
    }

    @Test
    /**
     * tests that a line in a sub collection can be deleted
     */
    public void deleteSubCollectionLine() throws Exception {
       super.deleteSubCollectionLine();
    }
}
