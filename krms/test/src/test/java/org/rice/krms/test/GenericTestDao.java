package org.rice.krms.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

public class GenericTestDao extends PersistenceBrokerDaoSupport {
	
	public <T extends PersistableBusinessObject> T save(T object) {
		this.getPersistenceBrokerTemplate().store(object);
		return object;
	}
	
    @SuppressWarnings("unchecked")
	public <T> T findById(String id, String idFieldName, Class<T> clazz) {
        Criteria crit = new Criteria();
        crit.addEqualTo(idFieldName, id);
        return (T) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(clazz, crit));
    }
    
    @SuppressWarnings("unchecked")
	public <T extends BusinessObject> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues) {
        Criteria crit = new Criteria();
        if (fieldValues != null) for (Entry<String, ?> entry : fieldValues.entrySet()) {
	        crit.addEqualTo(entry.getKey(), entry.getValue());
        }
        return this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(clazz, crit));
    }

    /**
     * @see org.kuali.rice.kns.dao.BusinessObjectDao#deleteMatching(java.lang.Class, java.util.Map)
     */
    public void deleteMatching(Class clazz, Map<String, ?> fieldValues) {
        Criteria criteria = buildCriteria(fieldValues);

        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(clazz, criteria));

        // An ojb delete by query doesn't update the cache so we need to clear the cache for everything to work property.
        // don't believe me? Read the source code to OJB
        getPersistenceBrokerTemplate().clearCache();
    }

    
    private Criteria buildCriteria(Map<String, ?> fieldValues) {
        Criteria criteria = new Criteria();
        for (Iterator i = fieldValues.entrySet().iterator(); i.hasNext();) {
            Map.Entry<String, Object> e = (Map.Entry<String, Object>) i.next();

            String key = e.getKey();
            Object value = e.getValue();
            if (value instanceof Collection) {
                criteria.addIn(key, (Collection) value);
            }
            else {
                criteria.addEqualTo(key, value);
            }
        }

        return criteria;
    }


}
