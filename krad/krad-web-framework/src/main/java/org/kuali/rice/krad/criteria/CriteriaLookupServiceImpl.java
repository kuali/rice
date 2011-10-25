package org.kuali.rice.krad.criteria;

import org.kuali.rice.core.api.criteria.CriteriaLookupService;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.LookupCustomizer;
import org.kuali.rice.core.api.criteria.QueryByCriteria;

public class CriteriaLookupServiceImpl implements CriteriaLookupService {
    CriteriaLookupDao criteriaLookupDao;


    public void setCriteriaLookupDao(CriteriaLookupDao criteriaLookupDao) {
        this.criteriaLookupDao = criteriaLookupDao;
    }

    @Override
    public <T> GenericQueryResults<T> lookup(Class<T> queryClass, QueryByCriteria criteria) {
        return criteriaLookupDao.lookup(queryClass, criteria);
    }

    @Override
    public <T> GenericQueryResults<T> lookup(Class<T> queryClass, QueryByCriteria criteria,
            LookupCustomizer<T> customizer) {
        return criteriaLookupDao.lookup(queryClass, criteria, customizer);
    }
}
