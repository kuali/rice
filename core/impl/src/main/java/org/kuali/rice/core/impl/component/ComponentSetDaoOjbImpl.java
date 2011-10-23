package org.kuali.rice.core.impl.component;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.core.framework.persistence.ojb.DataAccessUtils;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * JDBC-based implementation of the {@code ComponentSetDao}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentSetDaoOjbImpl extends PersistenceBrokerDaoSupport implements ComponentSetDao {

    @Override
    public ComponentSetBo getComponentSet(String componentSetId) {
        Criteria criteria = new Criteria();
	    criteria.addEqualTo("componentSetId", componentSetId);
	    return (ComponentSetBo) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(ComponentSetBo.class, criteria));
    }

    @Override
    public boolean saveIgnoreLockingFailure(ComponentSetBo componentSetBo) {
        try {
            getPersistenceBrokerTemplate().store(componentSetBo);
        } catch (RuntimeException e) {
            if (DataAccessUtils.isOptimisticLockFailure(e)) {
                return false;
            }
            throw e;
        }
        return true;
    }
}
