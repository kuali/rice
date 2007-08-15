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
package org.kuali.core.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.RiceConstants;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteWorkgroup;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.AddAdHocRoutePersonEvent;
import org.kuali.core.rule.event.AddAdHocRouteWorkgroupEvent;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.MaintenanceDocumentDictionaryService;
import org.kuali.core.service.TransactionalDocumentDictionaryService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class represents a rule evaluator for Kuali. This class is to be used for evaluating business rule checks. The class defines
 * one method right now - applyRules() which takes in a Document and a DocumentEvent and does the proper business rule checks based
 * on the context of the event and the document type.
 */
@Transactional
public class KualiRuleServiceImpl implements KualiRuleService {
    private static final Logger LOG = Logger.getLogger(KualiRuleServiceImpl.class);

    private TransactionalDocumentDictionaryService transactionalDocumentDictionaryService;
    private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    private DictionaryValidationService dictionaryValidationService;
    private DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.core.service.KualiRuleService#applyRules(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    public boolean applyRules(KualiDocumentEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("invalid (null) event");
        }

        event.validate();
        LOG.debug("calling applyRules for event " + event);

        BusinessRule rule = (BusinessRule) getBusinessRulesInstance(event.getDocument(), event.getRuleInterfaceClass());

        boolean success = true;
        if (rule != null) {
            LOG.debug("processing " + event.getName() + " with rule " + rule.getClass().getName());
            increaseErrorPath(event.getErrorPathPrefix());

            // get any child events and apply rules
            List events = event.generateEvents();
            for (Iterator iter = events.iterator(); iter.hasNext();) {
                KualiDocumentEvent element = (KualiDocumentEvent) iter.next();
                success &= applyRules(element);
            }

            // now call the event rule method
            success &= event.invokeRuleMethod(rule);

            decreaseErrorPath(event.getErrorPathPrefix());

            // report failures
            if (!success) {
                LOG.error(event.getName() + " businessRule " + rule.getClass().getName() + " failed");
            }
            else {
                LOG.debug("processed " + event.getName() + " for rule " + rule.getClass().getName());
            }

        }
        return success;
    }

    /**
     * Builds a list containing AddAdHocRoutePersonEvents since the validation done for an AdHocRouteRecipient is the same for all
     * events.
     * 
     * @see org.kuali.core.service.KualiRuleService#generateAdHocRoutePersonEvents(org.kuali.core.document.Document)
     */
    public List generateAdHocRoutePersonEvents(Document document) {
        List adHocRoutePersons = document.getAdHocRoutePersons();

        List events = new ArrayList();

        for (int i = 0; i < adHocRoutePersons.size(); i++) {
            events.add(new AddAdHocRoutePersonEvent(RiceConstants.EXISTING_AD_HOC_ROUTE_PERSON_PROPERTY_NAME + "[" + i + "]", document, (AdHocRoutePerson) adHocRoutePersons.get(i)));
        }

        return events;
    }

    /**
     * Builds a list containing AddAdHocRoutePersonEvents since the validation done for an AdHocRouteRecipient is the same for all
     * events.
     * 
     * @see org.kuali.core.service.KualiRuleService#generateAdHocRouteWorkgroupEvents(org.kuali.core.document.Document)
     */
    public List generateAdHocRouteWorkgroupEvents(Document document) {
        List adHocRouteWorkgroups = document.getAdHocRouteWorkgroups();

        List events = new ArrayList();

        for (int i = 0; i < adHocRouteWorkgroups.size(); i++) {
            events.add(new AddAdHocRouteWorkgroupEvent(RiceConstants.EXISTING_AD_HOC_ROUTE_WORKGROUP_PROPERTY_NAME + "[" + i + "]", document, (AdHocRouteWorkgroup) adHocRouteWorkgroups.get(i)));
        }

        return events;
    }
    





    /**
     * @param document
     * @param ruleInterface
     * @return instance of the businessRulesClass for the given document's type, if that businessRulesClass implements the given
     *         ruleInterface
     */
    public BusinessRule getBusinessRulesInstance(Document document, Class ruleInterface) {
        // get the businessRulesClass
        Class businessRulesClass = null;
        if (document instanceof TransactionalDocument) {
            TransactionalDocument transactionalDocument = (TransactionalDocument) document;

            businessRulesClass = transactionalDocumentDictionaryService.getBusinessRulesClass(transactionalDocument);
        }
//        else if (document instanceof AccountingDocument) {
//            AccountingDocument financialDocument = (AccountingDocument) document;
//
//            businessRulesClass = transactionalDocumentDictionaryService.getBusinessRulesClass(financialDocument);
//        }
        else if (document instanceof MaintenanceDocument) {
            MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;

            businessRulesClass = maintenanceDocumentDictionaryService.getBusinessRulesClass(maintenanceDocument);
        }
        else {
            LOG.error("unable to get businessRulesClass for unknown document type '" + document.getClass().getName() + "'");
        }

        // instantiate and return it if it implements the given ruleInterface
        BusinessRule rule = null;
        if (businessRulesClass != null) {
            try {
                if (ruleInterface.isAssignableFrom(businessRulesClass)) {
                    rule = (BusinessRule) businessRulesClass.newInstance();
                }
            }
            catch (IllegalAccessException e) {
                throw new InfrastructureException("error processing business rules", e);
            }
            catch (InstantiationException e) {
                throw new InfrastructureException("error processing business rules", e);
            }
        }

        return rule;
    }

    /**
     * This method increases the registered error path, so that field highlighting can occur on the appropriate object attribute.
     * 
     * @param errorPathPrefix
     */
    private void increaseErrorPath(String errorPathPrefix) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();

        if (!StringUtils.isBlank(errorPathPrefix)) {
            errorMap.addToErrorPath(errorPathPrefix);
        }
    }

    /**
     * This method decreases the registered error path, so that field highlighting can occur on the appropriate object attribute.
     * 
     * @param errorPathPrefix
     */
    private void decreaseErrorPath(String errorPathPrefix) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();

        if (!StringUtils.isBlank(errorPathPrefix)) {
            errorMap.removeFromErrorPath(errorPathPrefix);
        }
    }

    /* Spring service injection */

    /**
     * @param maintenanceDocumentDictionaryService
     */
    public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
        this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }

    /**
     * @return MaintenanceDocumentDictionaryService
     */
    public MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        return maintenanceDocumentDictionaryService;
    }

    /**
     * @param transactionalDocumentDictionaryService
     */
    public void setTransactionalDocumentDictionaryService(TransactionalDocumentDictionaryService transactionalDocumentDictionaryService) {
        this.transactionalDocumentDictionaryService = transactionalDocumentDictionaryService;
    }

    /**
     * @return TransactionalDocumentDictionaryService
     */
    public TransactionalDocumentDictionaryService getTransactionalDocumentDictionaryService() {
        return transactionalDocumentDictionaryService;
    }

    /**
     * @return DictionaryValidationService
     */
    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    /**
     * @param dictionaryValidationService
     */
    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    /**
     * @return DataDictionaryService
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * @param dataDictionaryService
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}