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
package org.kuali.rice.edl.impl.extract.dao.impl;

import java.util.List;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.edl.impl.extract.Dump;
import org.kuali.rice.edl.impl.extract.Fields;
import org.kuali.rice.edl.impl.extract.dao.ExtractDAO;
import org.kuali.rice.kew.notes.Note;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

public class ExtractDAOJpaImpl implements ExtractDAO {

    /** Logger for this class. */
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExtractDAOJpaImpl.class);

    /** Service that persists data to and from the underlying datasource. */
    private DataObjectService dataObjectService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Dump getDumpByDocumentId(String docId) {
        LOG.debug("finding Document Extract by documentId " + docId);
        return this.dataObjectService.find(Dump.class, docId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Fields> getFieldsByDocumentId(String docId) {
        LOG.debug("finding Extract Fileds by documentId " + docId);

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setPredicates(equal("documentId", docId)).setOrderByAscending("docId");

        return this.dataObjectService.findMatching(Fields.class, criteria.build()).getResults();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dump saveDump(Dump dump) {
        LOG.debug("check for null values in Extract document");
        checkNull(dump.getDocId(), "Document ID");
        checkNull(dump.getDocCreationDate(), "Creation Date");
        checkNull(dump.getDocCurrentNodeName(), "Current Node Name");
        checkNull(dump.getDocModificationDate(), "Modification Date");
        checkNull(dump.getDocRouteStatusCode(), "Route Status Code");
        checkNull(dump.getDocInitiatorId(), "Initiator ID");
        checkNull(dump.getDocTypeName(), "Doc Type Name");
        LOG.debug("saving EDocLite document: routeHeader " + dump.getDocId());

        return this.dataObjectService.save(dump, PersistenceOption.FLUSH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Fields saveField(Fields field) {
        LOG.debug("saving EDocLite Extract fields");
        checkNull(field.getDocId(), "Document ID");
        checkNull(field.getFieldValue(), "Field Value");
        checkNull(field.getFieldName(), "Field Name");
        LOG.debug("saving Fields: routeHeader " + field.getFieldId());

        return this.dataObjectService.save(field, PersistenceOption.FLUSH);
    }

    /**
     * Determines if the given value is null and throws a {@link RuntimeException}
     * @param value the value to check if null
     * @param valueName the value name to display in the {@link RuntimeException} message.
     * @throws RuntimeException if the supplied value is null
     */
    private void checkNull(Object value, String valueName) throws RuntimeException {
        if (value == null) {
            throw new RuntimeException("Null value for " + valueName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteDump(String documentId) {
        LOG.debug("deleting record form Extract Dump table");
        this.dataObjectService.delete(this.dataObjectService.find(Note.class, documentId));
    }

    /**
     * Returns the {@link DataObjectService}
     * @return the {@link DataObjectService}
     */
    public DataObjectService getDataObjectService() {
        return this.dataObjectService;
    }

    /**
     *
     * @see #getDataObjectService()
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
