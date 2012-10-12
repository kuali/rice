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

import edu.samplu.common.WebDriverLegacyITBase;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ContraintsLegacyIT extends WebDriverLegacyITBase {

    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page3";
    }

    @Test
    public void testContraintsIT() throws Exception {
        checkForIncidentReport("field9");
        fireEvent("field9","focus");
        waitAndTypeByName("field9", "1");
        fireEvent("field9", "blur");
        Assert.assertTrue(getAttributeByName("field9","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field9","focus");
        clearTextByName("field9");
        waitAndTypeByName("field9", "12345");
        fireEvent("field9", "blur");
        Assert.assertTrue(getAttributeByName("field9","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field10","focus");
        waitAndTypeByName("field10", "2");
        fireEvent("field10", "blur");
        Assert.assertTrue(getAttributeByName("field10","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field10","focus");
        clearTextByName("field10");
        waitAndTypeByName("field10", "51");
        fireEvent("field10", "blur");
        Assert.assertTrue(getAttributeByName("field10","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        fireEvent("field10","focus");
        clearTextByName("field10");
        waitAndTypeByName("field10", "25");
        fireEvent("field10", "blur");
        Assert.assertTrue(getAttributeByName("field10","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("field6","focus");
        waitAndTypeByName("field6", "A");
        fireEvent("field6", "blur");
        waitAndTypeByName("field7", "");
        fireEvent("field7", "blur");
        Assert.assertTrue(getAttributeByName("field7","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndTypeByName("field7", "B");
        fireEvent("field7", "blur");
        Assert.assertTrue(getAttributeByName("field7","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndTypeByName("field8", "");
        fireEvent("field8", "blur");
        Assert.assertTrue(getAttributeByName("field8","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field8");
        waitAndTypeByName("field8", "C");
        fireEvent("field8", "blur");
        Assert.assertTrue(getAttributeByName("field8","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field6");
        waitAndTypeByName("field6", "");
        fireEvent("field6", "blur");
        Assert.assertTrue(getAttributeByName("field6","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field7");
        waitAndTypeByName("field7", "");
        fireEvent("field7", "blur");
        Assert.assertTrue(getAttributeByName("field7","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field8");
        waitAndTypeByName("field8", "");
        fireEvent("field8", "blur");
        Assert.assertTrue(getAttributeByName("field6","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field7","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field8","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field8");
        waitAndTypeByName("field8", "C");
        fireEvent("field8", "blur");
        Assert.assertTrue(getAttributeByName("field6","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field7","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field8","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field6");
        waitAndTypeByName("field6", "A");
        fireEvent("field6", "blur");
        Assert.assertTrue(getAttributeByName("field6","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field7","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field8","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndTypeByName("field14", "A");
        fireEvent("field14", "blur");
        Assert.assertTrue(getAttributeByName("field14","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field11");
        waitAndTypeByName("field11", "A");
        fireEvent("field11", "blur");
        Assert.assertTrue(getAttributeByName("field11","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field14","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field11");
        waitAndTypeByName("field11", "");
        fireEvent("field11", "blur");
        Assert.assertTrue(getAttributeByName("field14","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field12");
        waitAndTypeByName("field12", "A");
        fireEvent("field12", "blur");
        Assert.assertTrue(getAttributeByName("field14","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field13");
        waitAndTypeByName("field13", "A");
        fireEvent("field13", "blur");
        Assert.assertTrue(getAttributeByName("field13","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field14","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field11");
        waitAndTypeByName("field11", "A");
        fireEvent("field11", "blur");
        Assert.assertTrue(getAttributeByName("field11","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field14","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndTypeByName("field18", "A");
        fireEvent("field18", "blur");
        Assert.assertTrue(getAttributeByName("field18","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndTypeByName("field15", "A");
        fireEvent("field15", "blur");
        Assert.assertTrue(getAttributeByName("field15","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field18","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field15");
        waitAndTypeByName("field15", "");
        fireEvent("field15", "blur");
        Assert.assertTrue(getAttributeByName("field18","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field6");
        waitAndTypeByName("field16", "A");
        fireEvent("field16", "blur");
        Assert.assertTrue(getAttributeByName("field18","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field17");
        waitAndTypeByName("field17", "A");
        fireEvent("field17", "blur");
        Assert.assertTrue(getAttributeByName("field17","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttributeByName("field18","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field15");
        waitAndTypeByName("field15", "A");
        fireEvent("field15", "blur");
        Assert.assertTrue(getAttributeByName("field18","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndTypeByName("field23", "A");
        fireEvent("field23", "blur");
        Assert.assertTrue(getAttributeByName("field23","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field19");
        waitAndTypeByName("field19", "A");
        fireEvent("field19", "blur");
        Assert.assertTrue(getAttributeByName("field23","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field19");
        waitAndTypeByName("field19", "");
        fireEvent("field19", "blur");
        waitAndTypeByName("field20", "B");
        fireEvent("field20", "blur");
        Assert.assertTrue(getAttributeByName("field23","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field20");
        waitAndTypeByName("field20", "");
        fireEvent("field20", "blur");
        Assert.assertTrue(getAttributeByName("field23","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field21");
        waitAndTypeByName("field21", "C");
        fireEvent("field21", "blur");
        Assert.assertTrue(getAttributeByName("field23","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field22");
        waitAndTypeByName("field22", "D");
        fireEvent("field22", "blur");
        Assert.assertTrue(getAttributeByName("field23","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field19");
        waitAndTypeByName("field19", "D");
        fireEvent("field19", "blur");
        Assert.assertTrue(getAttributeByName("field23","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field20");
        waitAndTypeByName("field20", "D");
        fireEvent("field20", "blur");
        Assert.assertTrue(getAttributeByName("field23","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case1']");
        clearTextByName("field25");
        waitAndTypeByName("field25", "");
        fireEvent("field25", "blur");
        Assert.assertTrue(getAttributeByName("field25","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case4']");
        fireEvent("field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field25","class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field25","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case1']");
        fireEvent("field24", "blur");
        clearTextByName("field25");
        waitAndTypeByName("field25", "$100");
        fireEvent("field25", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field25","class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field25","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case2']");
        fireEvent("field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field25","class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field25","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field25");
        waitAndTypeByName("field25", "A100");
        fireEvent("field25", "blur");
        Assert.assertTrue(getAttributeByName("field25","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case3']");
        fireEvent("field24", "blur");
        clearTextByName("field26");
        waitAndTypeByName("field26", "6000");
        fireEvent("field26", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field26","class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field26","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field26");
        waitAndTypeByName("field26", "501");
        fireEvent("field26", "blur");
        Assert.assertTrue(getAttributeByName("field26","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field26");
        waitAndTypeByName("field26", "499");
        fireEvent("field26", "blur");
        Assert.assertTrue(getAttributeByName("field26","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field26");
        waitAndTypeByName("field26", "6000");
        fireEvent("field26", "blur");
        checkByXpath("//*[@name='field24' and @value='case3']");
        fireEvent("field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field26","class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field26","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case4']");
        clearTextByName("field27");
        waitAndTypeByName("field27", "A");
        fireEvent("field27", "blur");
        clearTextByName("field28");
        waitAndTypeByName("field28", "");
        fireEvent("field28", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field28","class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field28","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        checkByXpath("//*[@name='field24' and @value='case3']");
        fireEvent("field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field28","class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field28","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field28");
        waitAndTypeByName("field28", "B");
        fireEvent("field28", "blur");
        checkByXpath("//*[@name='field24' and @value='case4']");
        fireEvent("field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttributeByName("field28","class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttributeByName("field28","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field31");
        waitAndTypeByName("field31", "B");
        clearTextByName("field32");
        waitAndTypeByName("field32", "B");
        waitAndTypeByName("field33", "");
        fireEvent("field33", "blur");
        Assert.assertTrue(getAttributeByName("field33","class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        clearTextByName("field33");
        waitAndTypeByName("field33", "B");
        fireEvent("field33", "blur");
        Assert.assertTrue(getAttributeByName("field33","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        clearTextByName("field32");
        waitAndTypeByName("field32", "A");
        clearTextByName("field33");
        waitAndTypeByName("field33", "");
        fireEvent("field33", "blur");
        Assert.assertTrue(getAttributeByName("field33","class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
    }
}
