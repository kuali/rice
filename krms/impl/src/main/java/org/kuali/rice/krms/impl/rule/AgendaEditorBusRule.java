/*
 * Copyright 2011 The Kuali Foundation
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

package org.kuali.rice.krms.impl.rule;

import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.bo.GlobalBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krms.api.KrmsConstants;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.impl.authorization.AgendaAuthorizationService;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.AgendaBoService;
import org.kuali.rice.krms.impl.repository.AgendaItemBo;
import org.kuali.rice.krms.impl.repository.ContextBoService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.repository.RuleBo;
import org.kuali.rice.krms.impl.repository.RuleBoService;
import org.kuali.rice.krms.impl.ui.AgendaEditor;
import org.kuali.rice.krms.impl.util.KRMSPropertyConstants;

import java.util.Map;

/**
 * This class contains the rules for the AgendaEditor.
 */
public class AgendaEditorBusRule extends MaintenanceDocumentRuleBase {

    @Override
    protected boolean primaryKeyCheck(MaintenanceDocument document) {
        // default to success if no failures
        boolean success = true;
        Class<?> dataObjectClass = document.getNewMaintainableObject().getDataObjectClass();

        // Since the dataObject is a wrapper class we need to return the agendaBo instead.
        Object oldBo = ((AgendaEditor) document.getOldMaintainableObject().getDataObject()).getAgenda();
        Object newDataObject = ((AgendaEditor) document.getNewMaintainableObject().getDataObject()).getAgenda();

        // We dont do primaryKeyChecks on Global Business Object maintenance documents. This is
        // because it doesnt really make any sense to do so, given the behavior of Globals. When a
        // Global Document completes, it will update or create a new record for each BO in the list.
        // As a result, there's no problem with having existing BO records in the system, they will
        // simply get updated.
        if (newDataObject instanceof GlobalBusinessObject) {
            return success;
        }

        // fail and complain if the person has changed the primary keys on
        // an EDIT maintenance document.
        if (document.isEdit()) {
            if (!getDataObjectMetaDataService().equalsByPrimaryKeys(oldBo, newDataObject)) {
                // add a complaint to the errors
                putDocumentError(KRADConstants.DOCUMENT_ERRORS,
                        RiceKeyConstants.ERROR_DOCUMENT_MAINTENANCE_PRIMARY_KEYS_CHANGED_ON_EDIT,
                        getHumanReadablePrimaryKeyFieldNames(dataObjectClass));
                success &= false;
            }
        }

        // fail and complain if the person has selected a new object with keys that already exist
        // in the DB.
        else if (document.isNew()) {

            // TODO: when/if we have standard support for DO retrieval, do this check for DO's
            if (newDataObject instanceof PersistableBusinessObject) {

                // get a map of the pk field names and values
                Map<String, ?> newPkFields = getDataObjectMetaDataService().getPrimaryKeyFieldValues(newDataObject);

                // TODO: Good suggestion from Aaron, dont bother checking the DB, if all of the
                // objects PK fields dont have values. If any are null or empty, then
                // we're done. The current way wont fail, but it will make a wasteful
                // DB call that may not be necessary, and we want to minimize these.

                // attempt to do a lookup, see if this object already exists by these Primary Keys
                PersistableBusinessObject testBo = getBoService()
                        .findByPrimaryKey(dataObjectClass.asSubclass(PersistableBusinessObject.class), newPkFields);

                // if the retrieve was successful, then this object already exists, and we need
                // to complain
                if (testBo != null) {
                    putDocumentError(KRADConstants.DOCUMENT_ERRORS,
                            RiceKeyConstants.ERROR_DOCUMENT_MAINTENANCE_KEYS_ALREADY_EXIST_ON_CREATE_NEW,
                            getHumanReadablePrimaryKeyFieldNames(dataObjectClass));
                    success &= false;
                }
            }
        }

        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = true;

        AgendaEditor agenda = (AgendaEditor) document.getNewMaintainableObject().getDataObject();
        isValid &= validContext(agenda);
        isValid &= validAgendaName(agenda);

        return isValid;
    }

    /**
     * Check if the context exists and if user has authorization to edit agendas under this context.
     * @param agenda
     * @return true if the context exist and has authorization, false otherwise
     */
    private boolean validContext(AgendaEditor agenda) {
        boolean isValid = true;

        try {
            if (getContextBoService().getContextByContextId(agenda.getAgenda().getContextId()) == null) {
                this.putFieldError(KRMSPropertyConstants.Agenda.CONTEXT, "error.agenda.invalidContext");
                isValid = false;
            } else {
                if (!getAgendaAuthorizationService().isAuthorized(KrmsConstants.MAINTAIN_KRMS_AGENDA,
                        agenda.getAgenda().getContextId())) {
                    this.putFieldError(KRMSPropertyConstants.Agenda.CONTEXT, "error.agenda.unauthorizedContext");
                    isValid = false;
                }
            }
        }
        catch (IllegalArgumentException e) {
            this.putFieldError(KRMSPropertyConstants.Agenda.CONTEXT, "error.agenda.invalidContext");
            isValid = false;
        }

        return isValid;
    }

    /**
     * Check if an agenda with that name exists already in the context.
     * @param agenda
     * @return true if agenda name is unique, false otherwise
     */
    private boolean validAgendaName(AgendaEditor agenda) {
        try {
            AgendaDefinition agendaFromDataBase = getAgendaBoService().getAgendaByNameAndContextId(agenda.getAgenda().getName(),
                    agenda.getAgenda().getContextId());
            if ((agendaFromDataBase != null) && !agendaFromDataBase.getId().equals(agenda.getAgenda().getId())) {
                this.putFieldError(KRMSPropertyConstants.Agenda.NAME, "error.agenda.duplicateName");
                return false;
            }
        }
        catch (IllegalArgumentException e) {
            this.putFieldError(KRMSPropertyConstants.Agenda.NAME, "error.agenda.invalidName");
            return false;
        }
        return true;
    }

    public boolean processAddAgendaItemBusinessRules(AgendaItemBo agendaItem, AgendaBo agenda) {
        boolean isValid = true;

        RuleBo rule = agendaItem.getRule();
        isValid &= validateRuleName(rule, agenda);

        return isValid;
    }

    /**
     * Check if a rule with that name exists already in the namespace.
     * @param rule
     * @parm agenda
     * @return true if rule name is unique, false otherwise
     */
    private boolean validateRuleName(RuleBo rule, AgendaBo agenda) {
        // check current bo for rules (including ones that aren't persisted to the database)
        for (AgendaItemBo agendaItem : agenda.getItems()) {
            if (agendaItem.getRule().getName().equals(rule.getName()) && agendaItem.getRule().getNamespace().equals(rule.getNamespace())) {
                this.putFieldError(KRMSPropertyConstants.Rule.NAME, "error.rule.duplicateName");
                return false;
            }
        }

        // check database for rules used with other agendas
        RuleDefinition ruleFromDatabase = getRuleBoService().getRuleByNameAndNamespace(rule.getName(), rule.getNamespace());
        try {
            if ((ruleFromDatabase != null) && ruleFromDatabase.getId().equals(rule.getId())) {
                this.putFieldError(KRMSPropertyConstants.Rule.NAME, "error.rule.duplicateName");
                return false;
            }
        }
        catch (IllegalArgumentException e) {
            this.putFieldError(KRMSPropertyConstants.Rule.NAME, "error.rule.invalidName");
            return false;
        }
        return true;
    }

    public ContextBoService getContextBoService() {
        return KrmsRepositoryServiceLocator.getContextBoService();
    }

    public AgendaBoService getAgendaBoService() {
        return KrmsRepositoryServiceLocator.getAgendaBoService();
    }

    public RuleBoService getRuleBoService() {
        return KrmsRepositoryServiceLocator.getRuleBoService();
    }

    private AgendaAuthorizationService getAgendaAuthorizationService() {
        return KrmsRepositoryServiceLocator.getAgendaAuthorizationService();
    }

}

