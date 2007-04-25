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
package org.kuali.core.dao.ojb;

import java.sql.Date;
import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.dao.DocumentHeaderDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * This class is the OJB implementation of the DocumentHeaderDao interface.
 * 
 * 
 */
public class DocumentHeaderDaoOjb extends PlatformAwareDaoBaseOjb implements DocumentHeaderDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentHeaderDaoOjb.class);

    /**
     * 
     */
    public DocumentHeaderDaoOjb() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.dao.DocumentHeaderDao#getByDocumentHeaderId(java.lang.Long)
     */
    public DocumentHeader getByDocumentHeaderId(String id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("FDOC_NBR", id);

        return (DocumentHeader) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(DocumentHeader.class, criteria));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.dao.DocumentHeaderDao#getCorrectingDocumentHeader(java.lang.Long)
     */
    public DocumentHeader getCorrectingDocumentHeader(String documentId) {
        Criteria correctedByCriteria = new Criteria();
        correctedByCriteria.addEqualTo("financialDocumentInErrorNumber", documentId);

        return (DocumentHeader) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(DocumentHeader.class, correctedByCriteria));
    }

    /**
     * @see org.kuali.core.dao.DocumentHeaderDao#getByDocumentFinalDate(Date documentFinalDate)
     */
    public Collection getByDocumentFinalDate(Date documentFinalDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.DOCUMENT_FINAL_DATE, documentFinalDate);
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(DocumentHeader.class, criteria));
    }
}