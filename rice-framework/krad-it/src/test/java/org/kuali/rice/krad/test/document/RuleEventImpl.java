/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.krad.test.document;

import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * setup a custom save document event method to process the default business rule class's rule method
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RuleEventImpl extends SaveDocumentEvent {

    public RuleEventImpl( Document document ) {
        super( document );
    }

    @Override
    public void validate() {
    }

    /**
     * set a global variable to verify invocation of the default method's processing of the custom save document event
     *
     * @param rule - the custom business rule to process
     * @return
     */
    @Override
    public boolean invokeRuleMethod( BusinessRule rule ) {
        GlobalVariables.getMessageMap().putInfo( this.getClass().getName(), rule.getClass().getName() );
        return true;
    }
}
