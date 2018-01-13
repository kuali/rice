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
package org.kuali.rice.krad.document;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.PessimisticLockService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.web.form.DocumentFormBase;

import java.util.Map;

/**
 * Extends {@link DocumentViewAuthorizerBase} to add additional authorization behavior to Transactional documents.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TransactionalDocumentViewAuthorizerBase extends DocumentViewAuthorizerBase {

    private static final long serialVersionUID = -6361662425078612737L;

    private PessimisticLockService pessimisticLockService;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canEditView(View view, ViewModel model, Person user) {
        boolean canEditView = super.canEditView(view, model, user);

        Map<String, Object> context = view.getContext();
        DocumentEntry documentEntry = (DocumentEntry) context.get(UifConstants.ContextVariableNames.DOCUMENT_ENTRY);

        if (!documentEntry.getUsePessimisticLocking()) {
            return canEditView;
        }

        DocumentFormBase documentForm = (DocumentFormBase) model;
        Document document = documentForm.getDocument();

        return getPessimisticLockService().establishPessimisticLocks(document, user, canEditView);
    }

    protected PessimisticLockService getPessimisticLockService() {
        if (pessimisticLockService == null) {
            pessimisticLockService = KRADServiceLocatorWeb.getPessimisticLockService();
        }

        return pessimisticLockService;
    }

    protected void setPessimisticLockService(PessimisticLockService pessimisticLockService) {
        this.pessimisticLockService = pessimisticLockService;
    }

}
