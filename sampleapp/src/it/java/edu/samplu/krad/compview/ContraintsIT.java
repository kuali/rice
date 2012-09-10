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

import edu.samplu.common.UpgradedSeleniumITBase;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ContraintsIT extends UpgradedSeleniumITBase {

    @Override
    public String getTestUrl() {
        return "/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page3";
    }

    @Test
    public void testContraintsIT() throws Exception {
        focus("name=field9");
        waitAndType("name=field9", "1");
        fireEvent("name=field9", "blur");
        Assert.assertTrue(getAttribute("name=field9@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        focus("name=field9");
        waitAndType("name=field9", "12345");
        fireEvent("name=field9", "blur");
        Assert.assertTrue(getAttribute("name=field9@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        focus("name=field10");
        waitAndType("name=field10", "2");
        fireEvent("name=field10", "blur");
        Assert.assertTrue(getAttribute("name=field10@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        focus("name=field10");
        waitAndType("name=field10", "51");
        fireEvent("name=field10", "blur");
        Assert.assertTrue(getAttribute("name=field10@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        focus("name=field10");
        waitAndType("name=field10", "25");
        fireEvent("name=field10", "blur");
        Assert.assertTrue(getAttribute("name=field10@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        focus("name=field6");
        waitAndType("name=field6", "A");
        fireEvent("name=field6", "blur");
        fireEvent("name=field7", "blur");
        Assert.assertTrue(getAttribute("name=field7@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field7", "B");
        fireEvent("name=field7", "blur");
        Assert.assertTrue(getAttribute("name=field7@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        fireEvent("name=field8", "blur");
        Assert.assertTrue(getAttribute("name=field8@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field8", "C");
        fireEvent("name=field8", "blur");
        Assert.assertTrue(getAttribute("name=field8@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field6", "");
        fireEvent("name=field6", "blur");
        Assert.assertTrue(getAttribute("name=field6@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field7", "");
        fireEvent("name=field7", "blur");
        Assert.assertTrue(getAttribute("name=field7@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field8", "");
        fireEvent("name=field8", "blur");
        Assert.assertTrue(getAttribute("name=field6@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttribute("name=field7@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttribute("name=field8@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field8", "C");
        fireEvent("name=field8", "blur");
        Assert.assertTrue(getAttribute("name=field6@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(getAttribute("name=field7@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttribute("name=field8@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field6", "A");
        fireEvent("name=field6", "blur");
        Assert.assertTrue(getAttribute("name=field6@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttribute("name=field7@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(getAttribute("name=field8@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field14", "A");
        fireEvent("name=field14", "blur");
        Assert.assertTrue(getAttribute("name=field14@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field11", "A");
        fireEvent("name=field11", "blur");
        Assert.assertTrue(getAttribute("name=field11@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttribute("name=field14@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field11", "");
        fireEvent("name=field11", "blur");
        Assert.assertTrue(getAttribute("name=field14@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field12", "A");
        fireEvent("name=field12", "blur");
        Assert.assertTrue(getAttribute("name=field14@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field13", "A");
        fireEvent("name=field13", "blur");
        Assert.assertTrue(getAttribute("name=field13@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttribute("name=field14@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field11", "A");
        fireEvent("name=field11", "blur");
        Assert.assertTrue(getAttribute("name=field11@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttribute("name=field14@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field18", "A");
        fireEvent("name=field18", "blur");
        Assert.assertTrue(getAttribute("name=field18@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field15", "A");
        fireEvent("name=field15", "blur");
        Assert.assertTrue(getAttribute("name=field15@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttribute("name=field18@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field15", "");
        fireEvent("name=field15", "blur");
        Assert.assertTrue(getAttribute("name=field18@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field16", "A");
        fireEvent("name=field16", "blur");
        Assert.assertTrue(getAttribute("name=field18@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field17", "A");
        fireEvent("name=field17", "blur");
        Assert.assertTrue(getAttribute("name=field17@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(getAttribute("name=field18@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field15", "A");
        fireEvent("name=field15", "blur");
        Assert.assertTrue(getAttribute("name=field18@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field23", "A");
        fireEvent("name=field23", "blur");
        Assert.assertTrue(getAttribute("name=field23@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field19", "A");
        fireEvent("name=field19", "blur");
        Assert.assertTrue(getAttribute("name=field23@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field19", "");
        fireEvent("name=field19", "blur");
        waitAndType("name=field20", "B");
        fireEvent("name=field20", "blur");
        Assert.assertTrue(getAttribute("name=field23@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field20", "");
        fireEvent("name=field20", "blur");
        Assert.assertTrue(getAttribute("name=field23@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field21", "C");
        fireEvent("name=field21", "blur");
        Assert.assertTrue(getAttribute("name=field23@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field22", "D");
        fireEvent("name=field22", "blur");
        Assert.assertTrue(getAttribute("name=field23@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field19", "D");
        fireEvent("name=field19", "blur");
        Assert.assertTrue(getAttribute("name=field23@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field20", "D");
        fireEvent("name=field20", "blur");
        Assert.assertTrue(getAttribute("name=field23@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        check("name=field24 value=case1");
        waitAndType("name=field25", "");
        fireEvent("name=field25", "blur");
        Assert.assertTrue(getAttribute("name=field25@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        check("name=field24 value=case4");
        fireEvent("name=field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttribute("name=field25@class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttribute("name=field25@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        check("name=field24 value=case1");
        fireEvent("name=field24", "blur");
        waitAndType("name=field25", "$100");
        fireEvent("name=field25", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttribute("name=field25@class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttribute("name=field25@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        check("name=field24 value=case2");
        fireEvent("name=field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttribute("name=field25@class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttribute("name=field25@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field25", "A100");
        fireEvent("name=field25", "blur");
        Assert.assertTrue(getAttribute("name=field25@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        check("name=field24 value=case3");
        fireEvent("name=field24", "blur");
        waitAndType("name=field26", "6000");
        fireEvent("name=field26", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttribute("name=field26@class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttribute("name=field26@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field26", "501");
        fireEvent("name=field26", "blur");
        Assert.assertTrue(getAttribute("name=field26@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field26", "499");
        fireEvent("name=field26", "blur");
        Assert.assertTrue(getAttribute("name=field26@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field26", "6000");
        fireEvent("name=field26", "blur");
        check("name=field24 value=case3");
        fireEvent("name=field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttribute("name=field26@class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttribute("name=field26@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        check("name=field24 value=case4");
        waitAndType("name=field27", "A");
        fireEvent("name=field27", "blur");
        waitAndType("name=field28", "");
        fireEvent("name=field28", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttribute("name=field28@class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttribute("name=field28@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        check("name=field24 value=case3");
        fireEvent("name=field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttribute("name=field28@class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttribute("name=field28@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field28", "B");
        fireEvent("name=field28", "blur");
        check("name=field24 value=case4");
        fireEvent("name=field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (getAttribute("name=field28@class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(getAttribute("name=field28@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field31", "B");
        waitAndType("name=field32", "B");
        fireEvent("name=field33", "blur");
        Assert.assertTrue(getAttribute("name=field33@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        waitAndType("name=field33", "B");
        fireEvent("name=field33", "blur");
        Assert.assertTrue(getAttribute("name=field33@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        waitAndType("name=field32", "A");
        waitAndType("name=field33", "");
        fireEvent("name=field33", "blur");
        Assert.assertTrue(getAttribute("name=field33@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
    }
}
