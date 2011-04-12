/*
 * Copyright 2007-2009 The Kuali Foundation
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

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.edl.impl.extract.Dump;
import org.kuali.rice.edl.impl.extract.Fields;
import org.kuali.rice.edl.impl.extract.dao.ExtractDAO;
import org.kuali.rice.kew.notes.Note;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;


public class ExtractDAOOjbImpl extends PersistenceBrokerDaoSupport implements ExtractDAO {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExtractDAOOjbImpl.class);

	public Dump getDumpByRouteHeaderId(Long docId) {
		LOG.debug("finding Document Extract by routeHeaderId " + docId);
        Criteria crit = new Criteria();
        crit.addEqualTo("docId", docId);
        return (Dump) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(Dump.class, crit));
	}

	public List getFieldsByRouteHeaderId(Long docId) {
		LOG.debug("finding Extract Fileds by routeHeaderId " + docId);
	    Criteria crit = new Criteria();
	    crit.addEqualTo("routeHeaderId", docId);
	    QueryByCriteria query = new QueryByCriteria(Fields.class, crit);
	    query.addOrderByAscending("docId");
	    return (List) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

	public void saveDump(Dump dump) {
        LOG.debug("check for null values in Extract document");
        checkNull(dump.getDocId() , "Document ID");
        checkNull(dump.getDocCreationDate(), "Creation Date");
        checkNull(dump.getDocCurrentNodeName(), "Current Node Name");
        checkNull(dump.getDocModificationDate(), "Modification Date");
        checkNull(dump.getDocRouteStatusCode(), "Route Status Code");
        checkNull(dump.getDocInitiatorId(), "Initiator ID");
        checkNull(dump.getDocTypeName(), "Doc Type Name");
        LOG.debug("saving EDocLite document: routeHeader " + dump.getDocId());
        getPersistenceBrokerTemplate().store(dump);
	}

	public void saveField(Fields field) {
        LOG.debug("saving EDocLite Extract fields");
        checkNull(field.getDocId() , "Document ID");
        checkNull(field.getFieldValue(), "Field Value");
        checkNull(field.getFiledName(), "Field Name");
        LOG.debug("saving Fields: routeHeader " + field.getFieldId());
	    getPersistenceBrokerTemplate().store(field);
	}

    private void checkNull(Object value, String valueName) throws RuntimeException {
        if (value == null) {
            throw new RuntimeException("Null value for " + valueName);
        }
    }

	public void deleteDump(Long routeHeaderId) {
        LOG.debug("deleting record form Extract Dump table");
        Criteria crit = new Criteria();
        crit.addEqualTo("docId", routeHeaderId);
        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(Note.class, crit));
	}
}
