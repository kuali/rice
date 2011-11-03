/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.authorization;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.rice.krad.document.authorization.DocumentAuthorizerBase;
import org.kuali.rice.krms.api.KrmsConstants;
import org.kuali.rice.krms.impl.repository.AgendaBo;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.ui.AgendaEditor;

import java.util.HashSet;
import java.util.Set;

public class AgendaEditorAuthorizer extends DocumentAuthorizerBase implements MaintenanceDocumentAuthorizer {

    @Override
    public boolean canCreate(Class boClass, Person user) {
        return getAgendaAuthorizationService().isAuthorized(KrmsConstants.MAINTAIN_KRMS_AGENDA, null);
    }

    @Override
    public boolean canMaintain(Object dataObject, Person user) {
        AgendaEditor agendaEditor = (AgendaEditor) dataObject;
        return getAgendaAuthorizationService().isAuthorized(KrmsConstants.MAINTAIN_KRMS_AGENDA, agendaEditor.getAgenda().getContextId());
    }

    @Override
    public boolean canCreateOrMaintain(MaintenanceDocument maintenanceDocument, Person user) {
        AgendaEditor agendaEditor = (AgendaEditor) maintenanceDocument.getOldMaintainableObject().getDataObject();
        return getAgendaAuthorizationService().isAuthorized(KrmsConstants.MAINTAIN_KRMS_AGENDA, agendaEditor.getAgenda().getContextId());
    }

    @Override
    public Set<String> getSecurePotentiallyReadOnlySectionIds() {
        return new HashSet<String>();
    }

    @Override
    public Set<String> getSecurePotentiallyHiddenSectionIds() {
        return new HashSet<String>();
    }

    private AgendaAuthorizationService getAgendaAuthorizationService() {
        return KrmsRepositoryServiceLocator.getAgendaAuthorizationService();
    }
}
