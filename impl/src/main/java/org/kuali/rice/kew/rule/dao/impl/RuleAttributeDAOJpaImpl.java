/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.rice.kew.rule.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.dao.RuleAttributeDAO;


public class RuleAttributeDAOJpaImpl implements RuleAttributeDAO {

    @PersistenceContext
    private EntityManager entityManager;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RuleAttributeDAOJpaImpl.class);

    public void save(RuleAttribute ruleAttribute) {
        if (ruleAttribute.getRuleAttributeId() == null) {
            OrmUtils.populateAutoIncValue(ruleAttribute, entityManager);
            entityManager.persist(ruleAttribute);
        } else {
            entityManager.merge(ruleAttribute);
        }
    }

    public void delete(Long ruleAttributeId) {
        entityManager.remove(findByRuleAttributeId(ruleAttributeId));
    }

    public RuleAttribute findByRuleAttributeId(Long ruleAttributeId) {
        return (RuleAttribute) entityManager.createNamedQuery("RuleAttribute.FindById").setParameter("ruleAttributeId", ruleAttributeId).getSingleResult();
    }

    public List findByRuleAttribute(RuleAttribute ruleAttribute) {
        Criteria crit = new Criteria("RuleAttribute", "ra");

        if (ruleAttribute.getName() != null) {
            crit.rawJpql("UPPER(RULE_ATTRIB_NM) like '" + ruleAttribute.getName().toUpperCase() + "'");
        }

        if (ruleAttribute.getClassName() != null) {
            crit.rawJpql("UPPER(RULE_ATTRIB_CLS_NM) like '" + ruleAttribute.getClassName().toUpperCase() + "'");
        }
        if (ruleAttribute.getType() != null) {
            crit.rawJpql("UPPER(RULE_ATTRIB_TYP) like '" + ruleAttribute.getType().toUpperCase() + "'");
        }
        return new QueryByCriteria(entityManager, crit).toQuery().getResultList();

    }

    public List getAllRuleAttributes() {
        return  entityManager.createNamedQuery("RuleAttribute.GetAllRuleAttributes").getResultList();
    }

    public RuleAttribute findByName(String name) {
        LOG.debug("findByName name=" + name);
        return (RuleAttribute) entityManager.createNamedQuery("RuleAttribute.FindByName").setParameter("name", name).getSingleResult();
    }

    public RuleAttribute findByClassName(String classname) {
        LOG.debug("findByClassName classname=" + classname);
        return (RuleAttribute) entityManager.createNamedQuery("RuleAttribute.FindByClassName").setParameter("className", classname).getSingleResult();
    }

}