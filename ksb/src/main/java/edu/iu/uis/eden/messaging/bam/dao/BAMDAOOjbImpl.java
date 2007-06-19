package edu.iu.uis.eden.messaging.bam.dao;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.config.ConfigDAOSupport;
import org.kuali.rice.definition.ObjectDefinition;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.messaging.bam.BAMParam;
import edu.iu.uis.eden.messaging.bam.BAMTargetEntry;

public class BAMDAOOjbImpl extends PersistenceBrokerDaoSupport implements BAMDAO {

	ConfigDAOSupport configSupport = new ConfigDAOSupport();

	public void clearBAMTables() {
		getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(BAMTargetEntry.class));
		getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(BAMParam.class));
	}

	@SuppressWarnings("unchecked")
	public List<BAMTargetEntry> getCallsForService(QName serviceName, String methodName) {
		Criteria crit = new Criteria();
		crit.addEqualTo("serviceName", serviceName.toString());
		crit.addEqualTo("methodName", methodName);
		return (List<BAMTargetEntry>)getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(BAMTargetEntry.class, crit));
	}

	public void save(BAMTargetEntry bamEntry) {
		this.getPersistenceBrokerTemplate().store(bamEntry);
	}

	@SuppressWarnings("unchecked")
	public List<BAMTargetEntry> getCallsForService(QName serviceName) {
		Criteria crit = new Criteria();
		crit.addEqualTo("serviceName", serviceName.toString());
		return (List<BAMTargetEntry>)getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(BAMTargetEntry.class, crit));
	}

	@SuppressWarnings("unchecked")
	public List<BAMTargetEntry> getCallsForRemotedClasses(ObjectDefinition objDef) {
		Criteria crit = new Criteria();
		QName qname = new QName(objDef.getMessageEntity(), objDef.getClassName());
		crit.addLike("serviceName", qname.toString() + "%");
		return (List<BAMTargetEntry>)getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(BAMTargetEntry.class, crit));
	}

	@SuppressWarnings("unchecked")
	public List<BAMTargetEntry> getCallsForRemotedClasses(ObjectDefinition objDef, String methodName) {
		Criteria crit = new Criteria();
		QName qname = new QName(objDef.getMessageEntity(), objDef.getClassName());
		crit.addLike("serviceName", qname.toString() + "%");
		crit.addEqualTo("methodName", methodName);
		return (List<BAMTargetEntry>)getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(BAMTargetEntry.class, crit));
	}
}
