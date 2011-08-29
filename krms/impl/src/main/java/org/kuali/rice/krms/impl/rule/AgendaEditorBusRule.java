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

import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.impl.repository.AgendaBoService;
import org.kuali.rice.krms.impl.repository.ContextBoService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.ui.AgendaEditor;
import org.kuali.rice.krms.impl.util.KRMSPropertyConstants;

/**
 * This class contains the rules for the AgendaEditor.
 */
public class AgendaEditorBusRule extends MaintenanceDocumentRuleBase {

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = true;

        GlobalVariables.getMessageMap().addToErrorPath("document");
        KRADServiceLocatorWeb.getDictionaryValidationService().validateDocument(document);
        GlobalVariables.getMessageMap().removeFromErrorPath("document");

        AgendaEditor agenda = (AgendaEditor) document.getNewMaintainableObject().getDataObject();
        isValid &= validContext(agenda);
        isValid &= validAgendaName(agenda);

        return isValid;
    }

    /**
     * Check if the context exists.
     * @param agenda
     * @return true if the context exist, false otherwise
     */
    private boolean validContext(AgendaEditor agenda) {
        try {
            if (getContextBoService().getContextByContextId(agenda.getAgenda().getContextId()) == null) {
                this.putFieldError(KRMSPropertyConstants.Agenda.CONTEXT, "error.document.agendaEditor.invalidContext");
                return false;
            }
        }
        catch (IllegalArgumentException e) {
            this.putFieldError(KRMSPropertyConstants.Agenda.CONTEXT, "error.document.agendaEditor.invalidContext");
            return false;
        }

        return true;
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
                this.putFieldError(KRMSPropertyConstants.Agenda.NAME, "error.document.agendaEditor.duplicateName");
                return false;
            }
        }
        catch (IllegalArgumentException e) {
            this.putFieldError(KRMSPropertyConstants.Agenda.NAME, "error.document.agendaEditor.invalidName");
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
}

