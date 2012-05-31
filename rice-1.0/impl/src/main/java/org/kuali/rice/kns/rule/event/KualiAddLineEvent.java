/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.rice.kns.rule.event;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.rule.AddCollectionLineRule;
import org.kuali.rice.kns.rule.BusinessRule;

public class KualiAddLineEvent extends KualiDocumentEventBase {
    private static final Logger LOG = Logger.getLogger(KualiAddLineEvent.class);

    private PersistableBusinessObject bo;
    private String collectionName;

    public KualiAddLineEvent( Document document, String collectionName, PersistableBusinessObject addLine ) {
        super("adding bo to document collection " + getDocumentId(document), "", document);
        
        this.bo = addLine;//(BusinessObject)ObjectUtils.deepCopy( addLine );
        this.collectionName = collectionName;
    }

    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddCollectionLineRule)rule).processAddCollectionLineBusinessRules( (MaintenanceDocument)getDocument(), collectionName, bo );
    }

    /**
     * Logs the event type and some information about the associated accountingLine
     */
    private void logEvent() {
        if ( LOG.isDebugEnabled() ) {
            StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
            logMessage.append(" with ");
    
            // vary logging detail as needed
            if (bo == null) {
                logMessage.append("null new bo");
            } else {
                logMessage.append( StringUtils.substringAfterLast(bo.getObjectId(), ".") );
            }
    
            LOG.debug(logMessage);
        }
    }

    public Class getRuleInterfaceClass() {
        return AddCollectionLineRule.class;
    }
}
