package edu.iu.uis.eden.messaging.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;
import org.kuali.rice.util.RiceUtilities;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.messaging.PersistedMessage;

public class MessageQueueDAOOjbImpl extends PersistenceBrokerDaoSupport implements MessageQueueDAO {
    
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MessageQueueDAOOjbImpl.class);
	
    public void remove(PersistedMessage routeQueue) {
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("Removing message " + routeQueue);
    	}
        Criteria crit = new Criteria();
        crit.addEqualTo("routeQueueId", routeQueue.getRouteQueueId());
        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(PersistedMessage.class, crit));
    }
    
    public void save(PersistedMessage routeQueue) {
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("Persisting message " + routeQueue);
    	}
    	getPersistenceBrokerTemplate().store(routeQueue);
    }

    @SuppressWarnings("unchecked")
	public List<PersistedMessage> findAll() {
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("Returning all persisted messages");
    	}
        return (List<PersistedMessage>) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PersistedMessage.class));
    }
    
    @SuppressWarnings("unchecked")
	public List<PersistedMessage> findAll(int maxRows) {
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("Finding next " + maxRows + " messages");
    	}
        QueryByCriteria query = new QueryByCriteria(PersistedMessage.class);
        query.setStartAtIndex(0);
        query.setEndAtIndex(maxRows);
        return (List<PersistedMessage>) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    
    @SuppressWarnings("unchecked")
    public List<PersistedMessage> findByValues(Map<String,String> criteriaValues) {
      Criteria crit = new Criteria();
      String value = null;
      for (String key : criteriaValues.keySet()) {
        value = criteriaValues.get(key);
        if (StringUtils.isBlank(key) && StringUtils.isBlank(value)) {
          throw new IllegalArgumentException("Either the key or value was blank in criteriaValues (" + key + "=" + value + ")");
        }
        
        //  auto-wildcard the statement
        if (value.contains("*")) {
          value = value.replace("*", "%");
        }
        else {
          value = value.concat("%");
        }
        crit.addLike(key, value);
      }
      return (List<PersistedMessage>) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PersistedMessage.class, crit));
    }
    
    public PersistedMessage findByRouteQueueId(Long routeQueueId) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("routeQueueId", routeQueueId);
        return (PersistedMessage) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(PersistedMessage.class, criteria));
    }
    
    public List<PersistedMessage> getNextDocument() {
        return getNextDocuments(null);
    }
    
    @SuppressWarnings("unchecked")
	public List<PersistedMessage> getNextDocuments(Integer maxDocuments) {
        Criteria crit = new Criteria();
        String messagingEntity = Core.getCurrentContextConfig().getMessageEntity();
        crit.addEqualTo("messageEntity", messagingEntity);
        crit.addEqualTo("queueStatus", RiceConstants.ROUTE_QUEUE_QUEUED);
        crit.addEqualTo("ipNumber", RiceUtilities.getIpNumber());

        //Date fetching does not seem to work with derby...
        if (Core.getCurrentContextConfig().getProperty("datasource.ojb.platform").indexOf("Derby") == -1) {
        	Timestamp currentDate = new Timestamp(new Date().getTime());
            crit.addLessThan("queueDate", currentDate);	
        }
        
        QueryByCriteria query = new QueryByCriteria(PersistedMessage.class, crit);
        query.addOrderByAscending("queuePriority");
        query.addOrderByAscending("routeQueueId");
        query.addOrderByAscending("queueDate");
        if (maxDocuments != null) query.setEndAtIndex(maxDocuments.intValue());
        return (List) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }


	@SuppressWarnings("unchecked")
	public List<PersistedMessage> findByServiceName(QName serviceName, String methodName) {
		if (LOG.isDebugEnabled()) {
    		LOG.debug("Finding messages for service name " + serviceName);
    	}
		Criteria crit = new Criteria();
		crit.addEqualTo("serviceName", serviceName.toString());
		crit.addEqualTo("methodName", methodName);
		return (List<PersistedMessage>) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PersistedMessage.class, crit));
	}
    
}