/**
 * Copyright 2005-2012 The Kuali Foundation
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.uif.view.ViewPresentationControllerBase;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for all TransactionalDocumentPresentationControllers.
 */
public class TransactionalDocumentPresentationControllerBase extends ViewPresentationControllerBase implements TransactionalDocumentPresentationController {
    private static Log LOG = LogFactory.getLog(TransactionalDocumentPresentationControllerBase.class);

    /**
     *
     * @see DocumentPresentationController#getEditMode(org.kuali.rice.krad.document.Document)
     */
    public Set<String> getEditModes(Document document){
        Set<String> editModes = new HashSet();
        return editModes;
    }

    @Override
    public boolean canInitiate(String documentTypeName) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canEdit(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canAnnotate(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canReload(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canClose(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canSave(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canRoute(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canCancel(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canCopy(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canPerformRouteReport(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canAddAdhocRequests(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canBlanketApprove(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canApprove(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canDisapprove(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canSendAdhocRequests(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canSendNoteFyi(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canEditDocumentOverview(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canFyi(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canAcknowledge(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canComplete(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean canRecall(Document document) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}