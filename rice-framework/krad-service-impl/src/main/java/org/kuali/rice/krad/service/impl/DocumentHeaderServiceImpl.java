/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.service.DocumentHeaderService;

/**
 *
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentHeaderServiceImpl implements DocumentHeaderService {

    protected DataObjectService dataObjectService;

    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentHeader getDocumentHeaderById(String documentHeaderId) {
        return dataObjectService.find(DocumentHeader.class, documentHeaderId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentHeader saveDocumentHeader(DocumentHeader documentHeader) {
        WorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();
        DocumentHeader savedDocumentHeader = dataObjectService.save(documentHeader, PersistenceOption.FLUSH);
        savedDocumentHeader.setWorkflowDocument( workflowDocument );
        return savedDocumentHeader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDocumentHeader(DocumentHeader documentHeader) {
    	dataObjectService.delete(documentHeader);
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

}
