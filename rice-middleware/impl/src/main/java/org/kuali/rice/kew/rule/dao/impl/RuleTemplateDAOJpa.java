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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.OrderByField;
import org.kuali.rice.core.api.criteria.OrderDirection;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.kew.rule.bo.RuleTemplateBo;
import org.kuali.rice.kew.rule.dao.RuleTemplateDAO;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.likeIgnoreCase;


public class RuleTemplateDAOJpa implements RuleTemplateDAO {

    private static final String SEQUENCE_NAME = "KREW_RTE_TMPL_S";

	private EntityManager entityManager;
    private DataObjectService dataObjectService;

    private DataSource dataSource;
	
    public List<RuleTemplateBo> findAll() {
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();

        return getDataObjectService().findMatching(RuleTemplateBo.class,builder.build()).getResults();
    }

    public RuleTemplateBo findByRuleTemplateName(String ruleTemplateName) {
        if (StringUtils.isBlank(ruleTemplateName)) {
        	return null;
        }

        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        builder.setPredicates(equal("name",ruleTemplateName));
        builder.setOrderByFields(OrderByField.Builder.create("name", OrderDirection.DESCENDING).build());

        QueryResults<RuleTemplateBo> ruleTemplates = getDataObjectService().findMatching(RuleTemplateBo.class,builder.build());

        if(ruleTemplates==null || ruleTemplates.getResults() == null || ruleTemplates.getResults().isEmpty()){
        	return null;
        }
        return (RuleTemplateBo) ruleTemplates.getResults().get(0);
    }

    public List<RuleTemplateBo> findByRuleTemplate(RuleTemplateBo ruleTemplate) {
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (ruleTemplate.getName() != null) {
          predicates.add(likeIgnoreCase("name",ruleTemplate.getName()));
        }
        if (ruleTemplate.getDescription() != null) {
            predicates.add(likeIgnoreCase("description",ruleTemplate.getDescription()));
        }
        Predicate[] preds = predicates.toArray(new Predicate[predicates.size()]);
        builder.setPredicates(preds);
        QueryResults<RuleTemplateBo> results = getDataObjectService().
                    findMatching(RuleTemplateBo.class, builder.build());
        return results.getResults();
    }

    public void delete(String ruleTemplateId) {
        getDataObjectService().delete(findByRuleTemplateId(ruleTemplateId));
    }

    public RuleTemplateBo findByRuleTemplateId(String ruleTemplateId) {
        return getDataObjectService().find(RuleTemplateBo.class, ruleTemplateId);
     }

    public RuleTemplateBo save(RuleTemplateBo ruleTemplate) {
    	return getDataObjectService().save(ruleTemplate, PersistenceOption.FLUSH);
    }

    public String getNextRuleTemplateId() {
        DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(
                getDataSource(), SEQUENCE_NAME);
        return incrementer.nextStringValue();
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
