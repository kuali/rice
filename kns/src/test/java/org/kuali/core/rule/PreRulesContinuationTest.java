/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.rule;

import org.junit.Test;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rule.event.PreRulesCheckEvent;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.rules.PreRulesContinuationBase.ContextSession;
import org.kuali.test.KNSTestBase;
import org.kuali.test.KNSWithTestSpringContext;

@KNSWithTestSpringContext
public class PreRulesContinuationTest extends KNSTestBase {

    private class TestPreRules extends PreRulesContinuationBase {

        public boolean doRules(Document document) {
            MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
            return false;
        }

    }

    @Test public void test() {

        TestPreRules preRules = new TestPreRules();

        PreRulesCheckEvent event = new PreRulesCheckEvent("", "", null);

        ContextSession contextSession = preRules.new ContextSession("test", event);

        contextSession.askQuestion("q1", "this is q1");
        contextSession.setAttribute("t1", "test1");
        contextSession.setAttribute("t2", "test2");
        contextSession.setAttribute("t3", "test3");

        assertEquals("testing retrieve", "test1", contextSession.getAttribute("t1"));
        assertEquals("testing retrieve", "test2", contextSession.getAttribute("t2"));
        assertEquals("testing retrieve", "test3", contextSession.getAttribute("t3"));

    }

}
