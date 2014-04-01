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
package org.kuali.rice.kew.rule.dao.impl;

import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.dao.RuleAttributeDAO;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.likeIgnoreCase;

public class RuleAttributeDAOJpa implements RuleAttributeDAO {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RuleAttributeDAOJpa.class);

    private EntityManager entityManager;
    private DataObjectService dataObjectService;


	public void save(RuleAttribute ruleAttribute) {
        getDataObjectService().save(ruleAttribute);
    }

    public void delete(String ruleAttributeId) {
        RuleAttribute ruleAttribute = findByRuleAttributeId(ruleAttributeId);
        getDataObjectService().delete(ruleAttribute);
    }

    public RuleAttribute findByRuleAttributeId(String ruleAttributeId) {
        return getDataObjectService().find(RuleAttribute.class,ruleAttributeId);
    }

    public List<RuleAttribute> findByRuleAttribute(RuleAttribute ruleAttribute) {
        QueryByCriteria.Builder builder =
                    QueryByCriteria.Builder.create();

        List<Predicate> predicates = new ArrayList<Predicate>();
        if (ruleAttribute.getName() != null) {
            predicates.add(likeIgnoreCase("name",ruleAttribute.getName()));
        }

        if (ruleAttribute.getResourceDescriptor() != null) {
            predicates.add(likeIgnoreCase("resourceDescriptor",ruleAttribute.getResourceDescriptor()));
        }
        if (ruleAttribute.getType() != null) {
            predicates.add(likeIgnoreCase("type",ruleAttribute.getType()));
        }
        Predicate[] preds = predicates.toArray(new Predicate[predicates.size()]);
        builder.setPredicates(preds);
        QueryResults<RuleAttribute> results = getDataObjectService().findMatching(RuleAttribute.class, builder.build());
        return results.getResults();

    }

    public List<RuleAttribute> getAllRuleAttributes() {
        QueryByCriteria.Builder builder =
                QueryByCriteria.Builder.create();
        QueryResults<RuleAttribute> results = getDataObjectService().findMatching(RuleAttribute.class, builder.build());
        return results.getResults();
    }

    public RuleAttribute findByName(String name) {
        LOG.debug("findByName name=" + name);
        QueryByCriteria.Builder builder =
                QueryByCriteria.Builder.create();
        builder.setPredicates(equal("name",name));
        QueryResults<RuleAttribute> ruleAttributeQueryResults = getDataObjectService().
                findMatching(RuleAttribute.class, builder.build());
        if(ruleAttributeQueryResults != null && ruleAttributeQueryResults.getResults().size() > 0){
            return ruleAttributeQueryResults.getResults().get(0);
        }
        return null;
    }

    public List<RuleAttribute> findByClassName(String classname) {
        LOG.debug("findByClassName classname=" + classname);

        QueryByCriteria.Builder builder =
                QueryByCriteria.Builder.create();
        builder.setPredicates(equal("resourceDescriptor",classname));
        QueryResults<RuleAttribute> ruleAttributeQueryResults = getDataObjectService().
                findMatching(RuleAttribute.class,builder.build());
        return ruleAttributeQueryResults.getResults();

    }


    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }


    /**
     * @return the entityManager
     */
    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    /**
     * @param entityManager the entityManager to set
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
