/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.krad.uif.view;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.authorization.PessimisticLock;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.PessimisticLockService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.TransactionalDocumentFormBase;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * View type for Transactional documents.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "transactionalDocumentView", parent = "Uif-TransactionalDocumentView")
public class TransactionalDocumentView extends DocumentView {

    private static final long serialVersionUID = 4375336878804984171L;

    private PessimisticLockService pessimisticLockService;

    public TransactionalDocumentView() {
        super();

        setViewTypeName(UifConstants.ViewType.TRANSACTIONAL);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Locks the document if pessimistic locking is turned on.
     * </p>
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (getDocumentEntryForView().getUsePessimisticLocking()) {
            TransactionalDocumentFormBase form = (TransactionalDocumentFormBase) model;

            generatePessimisticLockMessages(form);
            setupPessimisticLockingTimeout(form);
        }
    }

    /**
     * Generates the messages that warn users that the document has been locked for editing by another user.
     *
     * @param form form instance containing the transactional document data
     */
    protected void generatePessimisticLockMessages(TransactionalDocumentFormBase form) {
        Document document = form.getDocument();
        Person user = GlobalVariables.getUserSession().getPerson();

        for (PessimisticLock lock : document.getPessimisticLocks()) {
            if (!lock.isOwnedByUser(user)) {
                String lockDescriptor = StringUtils.defaultIfBlank(lock.getLockDescriptor(), "full");
                String lockOwner = lock.getOwnedByUser().getName();
                String lockTime = RiceConstants.getDefaultTimeFormat().format(lock.getGeneratedTimestamp());
                String lockDate = RiceConstants.getDefaultDateFormat().format(lock.getGeneratedTimestamp());

                GlobalVariables.getMessageMap().putError(KRADConstants.GLOBAL_ERRORS,
                        RiceKeyConstants.ERROR_TRANSACTIONAL_LOCKED, lockDescriptor, lockOwner, lockTime, lockDate);
            }
        }
    }

    /**
     * Enables the session timeout warning if any pessimistic locks exist.
     *
     * @param form form instance containing the transactional document data
     */
    protected void setupPessimisticLockingTimeout(TransactionalDocumentFormBase form) {
        Document document = form.getDocument();

        if (!document.getPessimisticLocks().isEmpty()) {
            form.getView().getSessionPolicy().setEnableTimeoutWarning(true);
        }
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