package edu.iu.uis.eden.messaging.dao;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.messaging.ServiceInfo;

public class ServiceInfoDAOOjbImpl extends PersistenceBrokerDaoSupport implements ServiceInfoDAO {

	public void addEntry(ServiceInfo entry) {
		getPersistenceBrokerTemplate().store(entry);
	}

	@SuppressWarnings("unchecked")
	public List<ServiceInfo> fetchAll() {
		return (List<ServiceInfo>)getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ServiceInfo.class));
	}
	
	@SuppressWarnings("unchecked")
	public List<ServiceInfo> findLocallyPublishedServices(String ipNumber, String messageEntity) {
		Criteria crit = new Criteria();
		crit.addEqualTo("serverIp", ipNumber);
		crit.addEqualTo("messageEntity", messageEntity);
		return (List<ServiceInfo>)getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ServiceInfo.class, crit));
	}
	
	public void removeEntry(ServiceInfo entry) {
		Criteria crit = new Criteria();
		crit.addEqualTo("messageEntryId", entry.getMessageEntryId());
		getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(ServiceInfo.class, crit));
	}
	
	public void removeEntry(String serviceName, String ipNumber) {
		// TODO remove entry
	}
	
	public ServiceInfo findServiceInfo(Long serviceInfoId) {
		return (ServiceInfo) getPersistenceBrokerTemplate().getObjectById(ServiceInfo.class, serviceInfoId);
	}

	public void removeLocallyPublishedServices(String ipNumber, String messageEntity) {
		Criteria crit = new Criteria();
		crit.addEqualTo("serverIp", ipNumber);
		crit.addEqualTo("messageEntity", messageEntity);
		getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(ServiceInfo.class, crit));
	}
	

}
