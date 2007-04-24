/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.rule.event;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.SaveDocumentRule;
import org.kuali.core.service.KualiRuleService;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class represents the save event that is part of an eDoc in Kuali. This could be triggered when a user presses the save
 * button for a given document or it could happen when another piece of code calls the save method in the document service.
 * 
 * 
 */
public final class SaveDocumentEvent extends KualiDocumentEventBase {
    /**
     * Constructs a SaveDocumentEvent with the specified errorPathPrefix and document
     * 
     * @param document
     * @param errorPathPrefix
     */
    public SaveDocumentEvent(String errorPathPrefix, Document document) {
        super("creating save event for document " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * Constructs a SaveDocumentEvent with the given document
     * 
     * @param document
     */
    public SaveDocumentEvent(Document document) {
        this("", document);
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return SaveDocumentRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((SaveDocumentRule) rule).processSaveDocument(document);
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#generateEvents()
     */
    @Override
    public List generateEvents() {
        KualiRuleService ruleService = KNSServiceLocator.getKualiRuleService();

        List events = new ArrayList();
        events.addAll(ruleService.generateAdHocRoutePersonEvents(getDocument()));
        events.addAll(ruleService.generateAdHocRouteWorkgroupEvents(getDocument()));
        
        events.addAll(getDocument().generateSaveEvents());

        /*
        if (getDocument() instanceof CashReceiptDocument) {
            events.addAll(ruleService.generateCheckEvents((CashReceiptDocument) getDocument()));
        }

        if (getDocument() instanceof AccountingDocument) {
            events.addAll(ruleService.generateAccountingLineEvents((AccountingDocument) getDocument()));
        }
        */
        return events;
    }
}