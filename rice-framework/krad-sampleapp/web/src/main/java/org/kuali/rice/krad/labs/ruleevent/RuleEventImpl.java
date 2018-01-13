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
package org.kuali.rice.krad.labs.ruleevent;

import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Provides method for custom rule event invocation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RuleEventImpl extends SaveDocumentEvent {

    public RuleEventImpl( Document document ) {
        super( document );
    }

    @Override
    public boolean invokeRuleMethod( BusinessRule rule ) {
        GlobalVariables.getMessageMap().putInfo(KRADConstants.GLOBAL_MESSAGES, "demo.fakeGrowl",
                "Applied custom business rule class '" + rule.getClass().getName() + "'.");

        return true;
    }

}