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
package edu.samplu;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;
import edu.samplu.common.WebDriverUtil;
import org.junit.Assert;
import org.junit.Test;

import static com.thoughtworks.selenium.SeleneseTestCase.assertEquals;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InvalidUserNameWDIT extends WebDriverLegacyITBase {

    @Override
    public void fail(String message) {
        Assert.fail(message);
    }

    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    @Override
    public void testSetUp()  {
        System.setProperty(ITUtil.REMOTE_AUTOLOGIN_PROPERTY, "notnull");
        super.testSetUp();
    }

    /**
     * Invalid user name test
     * @throws InterruptedException
     */
    @Test
    public void testInvalidUserName() throws InterruptedException {
        try {
            WebDriverUtil.login(driver, ITUtil.DTS_TWO, this);
        } catch (Exception e) {
            assertEquals(ITUtil.DTS_TWO, "Invalid username " + ITUtil.DTS_TWO, e.getMessage());
            passed();
        }
    }
}
