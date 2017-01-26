/**
 * Copyright 2005-2017 The Kuali Foundation
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
import org.kuali.rice.krad.document.authorization.PessimisticLock;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

/**
 * Base class for all Transactional Document authorizers.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TransactionalDocumentAuthorizerBase extends DocumentAuthorizerBase implements TransactionalDocumentAuthorizer {

    private static final long serialVersionUID = 3255133642834256283L;

    private DataDictionaryService dataDictionaryService;

    /**
     * {@inheritDoc}
     *
     * <p>
     * The {@code user} can only close the {@code document} if it is a transactional document.
     * </p>
     */
    @Override
    public boolean canClose(Document document, Person user) {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The {@code user} can only save the {@code document} if they have permission and, if pessimistic locking is turned
     * on for the {@code document}, they can establish a pessimistic lock.
     * </p>
     */
    @Override
    public boolean canSave(Document document, Person user) {
        boolean canSave = super.canSave(document, user);

        if (!isUsingPessimisticLocking(document)) {
            return canSave;
        }

        return canSave && canEstablishPessimisticLock(document, user);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The {@code user} can only route the {@code document} if they have permission and, if pessimistic locking is
     * turned on for the {@code document}, they can establish a pessimistic lock.
     * </p>
     */
    @Override
    public boolean canRoute(Document document, Person user) {
        boolean canRoute = super.canRoute(document, user);

        if (!isUsingPessimisticLocking(document)) {
            return canRoute;
        }

        return canRoute && canEstablishPessimisticLock(document, user);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The {@code user} can only cancel the {@code document} if they have permission and, if pessimistic locking is
     * turned on for the {@code document}, they can establish a pessimistic lock.
     * </p>
     */
    @Override
    public boolean canCancel(Document document, Person user) {
        boolean canCancel = super.canCancel(document, user);

        if (!isUsingPessimisticLocking(document)) {
            return canCancel;
        }

        return canCancel && canEstablishPessimisticLock(document, user);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The {@code user} can only blanket approve the {@code document} if they have permission and, if pessimistic
     * locking is turned on for the {@code document}, they can establish a pessimistic lock.
     * </p>
     */
    @Override
    public boolean canBlanketApprove(Document document, Person user) {
        boolean canBlanketApprove = super.canBlanketApprove(document, user);

        if (!isUsingPessimisticLocking(document)) {
            return canBlanketApprove;
        }

        return canBlanketApprove && canEstablishPessimisticLock(document, user);
    }

    /**
     * Returns whether the {@code document} is using pessimistic locking.
     *
     * @param document the document to check for using pessimistic locking
     *
     * @return true if the {@code document} is using pessimistic locking, false otherwise.
     */
    protected boolean isUsingPessimisticLocking(Document document) {
        String documentClassName = document.getClass().getName();
        DocumentEntry documentEntry = getDataDictionaryService().getDataDictionary().getDocumentEntry(documentClassName);

        return documentEntry.getUsePessimisticLocking();
    }

    /**
     * Returns whether {@code user} can establish a pessimistic lock on the document.
     *
     * <p>
     * The {@code user} can only establish a pessimistic lock on the document {@code document} if there are no existing
     * locks or if they already have a lock on the {@code document}.
     * </p>
     *
     * @param document the document to check for pessimistic locks
     * @param user the user to check for pessimistic locks
     *
     * @return true if the {@code user} can establish a pessimistic lock on the document, false otherwise
     */
    protected boolean canEstablishPessimisticLock(Document document, Person user) {
        if (document.getPessimisticLocks().isEmpty()) {
            return true;
        }

        for (PessimisticLock pessimisticLock : document.getPessimisticLocks()) {
            if (pessimisticLock.isOwnedByUser(user)) {
                return true;
            }
        }

        return false;
    }

    protected DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }

        return dataDictionaryService;
    }

    protected void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}