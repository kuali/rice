/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.demo.travel.rules;

import org.kuali.rice.krad.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.rules.rule.event.RuleEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Provides method for custom rule event invocation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TravelAccountMaintenanceDocumentRule extends MaintenanceDocumentRuleBase {

    /**
     * Records that the {@code event} has been processed.
     *
     * @param event the rule event to record
     *
     * @return true
     */
    public boolean processRule(RuleEvent event) {
        // using fake growl message due to lack of testing resources
        GlobalVariables.getMessageMap().putInfo(KRADConstants.GLOBAL_MESSAGES, "demo.fakeGrowl",
                "Processed business rule for event '" + event.getName() + "'.");

        return true;
    }
}
