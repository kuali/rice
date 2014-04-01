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
import org.kuali.rice.kew.rule.RuleDelegationBo;
import org.kuali.rice.kew.rule.dao.RuleDelegationDAO;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

public class RuleDelegationDAOJpa implements RuleDelegationDAO {

	private EntityManager entityManager;
    private DataObjectService dataObjectService;

    public List<RuleDelegationBo> findByDelegateRuleId(String ruleId) {
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        builder.setPredicates(equal("ruleDelegationId",ruleId));
        return getDataObjectService().findMatching(RuleDelegationBo.class,builder.build()).getResults();
    }

    public void save(RuleDelegationBo ruleDelegation) {
    	getDataObjectService().save(ruleDelegation);
    }

    public List<RuleDelegationBo> findAllCurrentRuleDelegations(){
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        builder.setPredicates(equal("delegationRule.currentInd",true));
        return getDataObjectService().findMatching(RuleDelegationBo.class,builder.build()).getResults();
    }

    public RuleDelegationBo findByRuleDelegationId(String ruleDelegationId){
        return getDataObjectService().find(RuleDelegationBo.class, ruleDelegationId);

    }
    public void delete(String ruleDelegationId){
        getDataObjectService().delete(findByRuleDelegationId(ruleDelegationId));
    }


    public List<RuleDelegationBo> findByResponsibilityIdWithCurrentRule(String responsibilityId) {
        if (StringUtils.isBlank(responsibilityId)){
            return null;
        }

        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                    org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        builder.setPredicates(equal("delegationRule.currentInd",true),
                    equal("responsibilityId",responsibilityId));
        return getDataObjectService().findMatching(RuleDelegationBo.class,builder.build()).getResults();
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kew.rule.dao.RuleDelegationDAO#search(java.lang.String, java.lang.Long, java.lang.Long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean, java.util.Map, java.lang.String)
     */
    public List<RuleDelegationBo> search(String parentRuleBaseVaueId, String parentResponsibilityId, String docTypeName, String ruleId,
            String ruleTemplateId, String ruleDescription, String workgroupId,
            String workflowId, String delegationType, Boolean activeInd,
            Map extensionValues, String workflowIdDirective) {
        // TODO jjhanso - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kew.rule.dao.RuleDelegationDAO#search(java.lang.String, java.lang.Long, java.lang.String, java.util.Collection, java.lang.String, java.lang.String, java.lang.Boolean, java.util.Map, java.util.Collection)
     */
    public List<RuleDelegationBo> search(String parentRuleBaseVaueId, String parentResponsibilityId, String docTypeName, String ruleTemplateId,
            String ruleDescription, Collection<String> workgroupIds,
            String workflowId, String delegationType, Boolean activeInd,
            Map extensionValues, Collection actionRequestCodes) {
        // TODO jjhanso - THIS METHOD NEEDS JAVADOCS
        return null;
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


}
