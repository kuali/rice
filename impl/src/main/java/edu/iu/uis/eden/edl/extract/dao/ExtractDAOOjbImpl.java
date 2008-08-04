package edu.iu.uis.eden.edl.extract.dao;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.edl.extract.Dump;
import edu.iu.uis.eden.edl.extract.Fields;
import edu.iu.uis.eden.notes.Note;

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
