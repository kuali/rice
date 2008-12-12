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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;
import org.kuali.rice.core.jpa.criteria.QueryByObject;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleExtension;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.dao.RuleDAO;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.user.WorkflowUserId;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;


public class RuleDAOJpaImpl implements RuleDAO {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RuleDAOJpaImpl.class);
	
	@PersistenceContext(unitName="kew-unit")
	private EntityManager entityManager;
	
	private static final String OLD_DELEGATIONS_SQL =
		"select oldDel.dlgn_rule_id "+
		"from krew_rule_rsp_t oldRsp, krew_dlgn_rsp_t oldDel "+
		"where oldRsp.rule_id=? and "+
		"oldRsp.rule_rsp_id=oldDel.rule_rsp_id and "+
		"oldDel.dlgn_rule_base_val_id not in "+
		"(select newDel.dlgn_rule_base_val_id from krew_rule_rsp_t newRsp, krew_dlgn_rsp_t newDel "+
		"where newRsp.rule_id=? and "+
		"newRsp.rule_rsp_id=newDel.rule_rsp_id)";

	public void save(RuleBaseValues ruleBaseValues) {
		if(ruleBaseValues.getRuleBaseValuesId()==null&&!entityManager.contains(ruleBaseValues)){
			entityManager.persist(ruleBaseValues);
		}else{
			entityManager.merge(ruleBaseValues);
		}
	}

	public void saveDeactivationDate(final RuleBaseValues ruleBaseValues) {

		final String sql = "update krew_rule_t set ACTVN_DT = ?, DACTVN_DT = ? where rule_id = ?";
		try{
			Query q = entityManager.createNativeQuery(sql);
			q.setParameter(1, ruleBaseValues.getActivationDate());
			q.setParameter(2, ruleBaseValues.getDeactivationDate());
			q.setParameter(3, ruleBaseValues.getRuleBaseValuesId().longValue());
			entityManager.flush();
			q.executeUpdate();
			entityManager.flush();
		} catch (Exception e) {
			throw new WorkflowRuntimeException("error saving deactivation date", e);
		} 
	}

	public List fetchAllCurrentRulesForTemplateDocCombination(Long ruleTemplateId, List documentTypes) {
		Criteria crit = new Criteria(RuleBaseValues.class.getName());
		crit.in("docTypeName", documentTypes);
		crit.eq("ruleTemplateId", ruleTemplateId);
		crit.eq("currentInd", new Boolean(true));
		crit.eq("activeInd", new Boolean(true));
		crit.eq("delegateRule", new Boolean(false));
		crit.eq("templateRuleInd", new Boolean(false));

		crit.lte("fromDate", new Timestamp(new Date().getTime()));
		crit.gte("toDate", new Timestamp(new Date().getTime()));
		return (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
	}

	public List fetchAllCurrentRulesForTemplateDocCombination(Long ruleTemplateId, List documentTypes, Timestamp effectiveDate) {
		Criteria crit = new Criteria(RuleBaseValues.class.getName());
		crit.in("docTypeName", documentTypes);
		crit.eq("ruleTemplateId", ruleTemplateId);
		crit.eq("activeInd", new Boolean(true));
		crit.eq("delegateRule", new Boolean(false));
		crit.eq("templateRuleInd", new Boolean(false));
		if (effectiveDate != null) {
			crit.lte("activationDate", effectiveDate);
			crit.gte("deactivationDate", effectiveDate);
		}

		crit.lte("fromDate", new Timestamp(new Date().getTime()));
		crit.gte("toDate", new Timestamp(new Date().getTime()));
		return (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
	}

	public List fetchAllRules(boolean currentRules) {
		Criteria crit = new Criteria(RuleBaseValues.class.getName());
		crit.eq("currentInd", new Boolean(currentRules));
		crit.eq("templateRuleInd", new Boolean(false));
		crit.orderBy("activationDate", false);
		
		QueryByCriteria query = new QueryByCriteria(entityManager, crit);
	
		return (List) query.toQuery().getResultList();
	}

	public List findResponsibilitiesByDelegationRuleId(Long delegationRuleId) {
		Criteria crit = new Criteria(RuleBaseValues.class.getName());
		crit.eq("currentInd", new Boolean(true));
		crit.eq("templateRuleInd", new Boolean(false));

		Criteria criteriaDelegationId = new Criteria(RuleBaseValues.class.getName());
		criteriaDelegationId.eq("responsibilities.delegateRuleId", delegationRuleId);
		crit.and(criteriaDelegationId);
		return (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
	}

	public void delete(Long ruleBaseValuesId) {
		entityManager.remove(entityManager.find(RuleBaseValues.class, ruleBaseValuesId));
	}

	public List findByRouteHeaderId(Long routeHeaderId) {
		Criteria crit = new Criteria(RuleBaseValues.class.getName());
		crit.eq("routeHeaderId", routeHeaderId);
		return (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
	}

    public RuleBaseValues findRuleBaseValuesByName(String name) {
        Criteria crit = new Criteria(RuleBaseValues.class.getName());
        crit.eq("name", name);
        crit.eq("currentInd", Boolean.TRUE);
        try{
        	return (RuleBaseValues) new QueryByCriteria(entityManager, crit).toQuery().getSingleResult();
        }catch (javax.persistence.NoResultException e){
        	return null;
        }
        
    }

	public RuleBaseValues findRuleBaseValuesById(Long ruleBaseValuesId) {
		Criteria crit = new Criteria(RuleBaseValues.class.getName());
		crit.eq("ruleBaseValuesId", ruleBaseValuesId);
		try{
			return (RuleBaseValues) new QueryByCriteria(entityManager, crit).toQuery().getSingleResult();
		}catch(javax.persistence.NoResultException e){
			return null;
		}
	}

	public List findRuleBaseValuesByResponsibilityReviewer(String reviewerName, String type) {
		Criteria crit = new Criteria(RuleResponsibility.class.getName());
		crit.eq("ruleResponsibilityName", reviewerName);
		crit.eq("ruleResponsibilityType", type);

		List responsibilities = (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
		List rules = new ArrayList();

		for (Iterator iter = responsibilities.iterator(); iter.hasNext();) {
			RuleResponsibility responsibility = (RuleResponsibility) iter.next();
			RuleBaseValues rule = responsibility.getRuleBaseValues();
			if (rule != null && rule.getCurrentInd() != null && rule.getCurrentInd().booleanValue()) {
				rules.add(rule);
			}
		}
		return rules;
	}

	public List findRuleBaseValuesByResponsibilityReviewerTemplateDoc(String ruleTemplateName, String documentType, String reviewerName, String type) {
	    Criteria crit = new Criteria(RuleResponsibility.class.getName());
		crit.eq("ruleResponsibilityName", reviewerName);
		crit.eq("ruleResponsibilityType", type);
		crit.eq("ruleBaseValues.currentInd", Boolean.TRUE);
		if (!StringUtils.isBlank(ruleTemplateName)) {
		    crit.like("ruleBaseValues.ruleTemplate.name", ruleTemplateName.replace("*", "%").concat("%"));
		}
		if (!StringUtils.isBlank(documentType)) {
		    crit.like("ruleBaseValues.docTypeName", documentType.replace("*", "%").concat("%"));
		}

		List responsibilities = (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
		List rules = new ArrayList();

		for (Iterator iter = responsibilities.iterator(); iter.hasNext();) {
			RuleResponsibility responsibility = (RuleResponsibility) iter.next();
			RuleBaseValues rule = responsibility.getRuleBaseValues();
			if (rule != null && rule.getCurrentInd() != null && rule.getCurrentInd().booleanValue()) {
				rules.add(rule);
			}
		}
		return rules;
	}

	//FIXME nothing uses this, it's not in ruleDAO interface
	public List findRuleBaseValuesByObjectGraph(RuleBaseValues ruleBaseValues) {
		ruleBaseValues.setCurrentInd(new Boolean(true));
		ruleBaseValues.setTemplateRuleInd(Boolean.FALSE);
		return (List) new QueryByObject(entityManager,ruleBaseValues).toQuery().getResultList();
	}

	public RuleResponsibility findRuleResponsibility(Long responsibilityId) {
		Criteria crit = new Criteria(RuleResponsibility.class.getName());
		crit.eq("responsibilityId", responsibilityId);
		Collection responsibilities = new QueryByCriteria(entityManager, crit).toQuery().getResultList();
		for (Iterator iterator = responsibilities.iterator(); iterator.hasNext();) {
			RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
			if (responsibility.getRuleBaseValues().getCurrentInd().booleanValue()) {
				return responsibility;
			}
		}
		return null;
	}

	public List search(String docTypeName, Long ruleId, Long ruleTemplateId, String ruleDescription, Long workgroupId, String workflowId, String roleName, Boolean delegateRule, Boolean activeInd, Map extensionValues, String workflowIdDirective) {
        Criteria crit = getSearchCriteria(docTypeName, ruleTemplateId, ruleDescription, delegateRule, activeInd, extensionValues);
        if (ruleId != null) {
            crit.eq("ruleBaseValuesId", ruleId);
        }
        if (workgroupId != null) {
            crit.in("responsibilities.ruleBaseValuesId", getResponsibilitySubQuery(workgroupId.toString()), "ruleBaseValuesId");
        }
        Set<Long> workgroupIds = new HashSet<Long>();
        Boolean searchUser = Boolean.FALSE;
        Boolean searchUserInWorkgroups = Boolean.FALSE;
        if (!Utilities.isEmpty(workflowIdDirective)) {
            if ("workgroup".equals(workflowIdDirective)) {
                searchUserInWorkgroups = Boolean.TRUE;
            } else if ("both".equals(workflowIdDirective)) {
                searchUser = Boolean.TRUE;
                searchUserInWorkgroups = Boolean.TRUE;
            } else {
                searchUser = Boolean.TRUE;
            }
        }
        if (!Utilities.isEmpty(workflowId) && searchUserInWorkgroups) {
            WorkflowUser user = null;
            try {
        	user = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(workflowId));
            } catch (KEWUserNotFoundException e) {
        	throw new WorkflowRuntimeException(e);
            }
            if (user == null) {
        	throw new WorkflowRuntimeException("Failed to locate user for the given workflow id: " + workflowId);
            }
            workgroupIds = KEWServiceLocator.getWorkgroupService().getUsersGroupIds(user);
        }
        crit.in("responsibilities.ruleBaseValuesId", getResponsibilitySubQuery(workgroupIds, workflowId, roleName, searchUser, searchUserInWorkgroups),"ruleBaseValuesId");
        crit.distinct(true);
		return (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
	}

    public List search(String docTypeName, Long ruleTemplateId, String ruleDescription, Collection<String> workgroupIds, String workflowId, String roleName, Boolean delegateRule, Boolean activeInd, Map extensionValues, Collection actionRequestCodes) {
        Criteria crit = getSearchCriteria(docTypeName, ruleTemplateId, ruleDescription, delegateRule, activeInd, extensionValues);
        crit.in("responsibilities.ruleBaseValuesId", getResponsibilitySubQuery(workgroupIds, workflowId, roleName, actionRequestCodes, (workflowId != null), ((workgroupIds != null) && !workgroupIds.isEmpty())), "ruleBaseValuesId");
        return (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }

    private Criteria getResponsibilitySubQuery(Set<Long> workgroupIds, String workflowId, String roleName, Boolean searchUser, Boolean searchUserInWorkgroups) {
        Collection<String> workgroupIdStrings = new ArrayList<String>();
        for (Long workgroupId : workgroupIds) {
            workgroupIdStrings.add(workgroupId.toString());
        }
        return getResponsibilitySubQuery(workgroupIdStrings,workflowId,roleName,new ArrayList<String>(), searchUser, searchUserInWorkgroups);
    }
    private Criteria getResponsibilitySubQuery(Collection<String> workgroupIds, String workflowId, String roleName, Collection actionRequestCodes, Boolean searchUser, Boolean searchUserInWorkgroups) {
        Criteria responsibilityCrit = new Criteria(RuleResponsibility.class.getName());
        if ( (actionRequestCodes != null) && (!actionRequestCodes.isEmpty()) ) {
            responsibilityCrit.in("actionRequestedCd", new ArrayList(actionRequestCodes));
        }

        Criteria ruleResponsibilityNameCrit = null;
        if (!Utilities.isEmpty(roleName)) {
            // role name exists... nothing else matters
            ruleResponsibilityNameCrit = new Criteria(RuleResponsibility.class.getName());
            ruleResponsibilityNameCrit.like("ruleResponsibilityName", workflowId);
            ruleResponsibilityNameCrit.eq("ruleResponsibilityType", KEWConstants.RULE_RESPONSIBILITY_ROLE_ID);
        } else {
            if (!Utilities.isEmpty(workflowId)) {
                // workflow user id exists
                if (searchUser != null && searchUser) {
                    // searching user wishes to search for rules specific to user
                    ruleResponsibilityNameCrit = new Criteria(RuleResponsibility.class.getName());
                    ruleResponsibilityNameCrit.like("ruleResponsibilityName", workflowId);
                    ruleResponsibilityNameCrit.eq("ruleResponsibilityType", KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
                }
                if ( (searchUserInWorkgroups != null && searchUserInWorkgroups) && (workgroupIds != null) && (!workgroupIds.isEmpty()) ) {
                    // at least one workgroup id exists and user wishes to search on workgroups
                    if (ruleResponsibilityNameCrit == null) {
                        ruleResponsibilityNameCrit = new Criteria(RuleResponsibility.class.getName());
                    }
                    Criteria workgroupCrit = new Criteria(RuleResponsibility.class.getName());
                    workgroupCrit.in("ruleResponsibilityName", new ArrayList<String>(workgroupIds));
                    workgroupCrit.eq("ruleResponsibilityType", KEWConstants.RULE_RESPONSIBILITY_GROUP_ID);
                    ruleResponsibilityNameCrit.or(workgroupCrit);
                }
            } else if ( (workgroupIds != null) && (workgroupIds.size() == 1) ) {
                // no user and one workgroup id
                ruleResponsibilityNameCrit = new Criteria(RuleResponsibility.class.getName());
                ruleResponsibilityNameCrit.like("ruleResponsibilityName", workgroupIds.iterator().next());
                ruleResponsibilityNameCrit.eq("ruleResponsibilityType", KEWConstants.RULE_RESPONSIBILITY_GROUP_ID);
            } else if ( (workgroupIds != null) && (workgroupIds.size() > 1) ) {
                // no user and more than one workgroup id
                ruleResponsibilityNameCrit = new Criteria(RuleResponsibility.class.getName());
                ruleResponsibilityNameCrit.in("ruleResponsibilityName",  new ArrayList<String>(workgroupIds));
                ruleResponsibilityNameCrit.eq("ruleResponsibilityType", KEWConstants.RULE_RESPONSIBILITY_GROUP_ID);
            }
        }
        if (ruleResponsibilityNameCrit != null) {
            responsibilityCrit.and(ruleResponsibilityNameCrit);
        }

        return responsibilityCrit;
    }

    private Criteria getSearchCriteria(String docTypeName, Long ruleTemplateId, String ruleDescription, Boolean delegateRule, Boolean activeInd, Map extensionValues) {
        Criteria crit = new Criteria(RuleBaseValues.class.getName());
        crit.eq("currentInd", new Boolean(true));
        crit.eq("templateRuleInd", new Boolean(false));
        if (activeInd != null) {
            crit.eq("activeInd", activeInd);
        }
        if (docTypeName != null) {
            crit.like("UPPER(docTypeName)", docTypeName.toUpperCase());
        }
        if (ruleDescription != null && !ruleDescription.trim().equals("")) {
            crit.like("UPPER(description)", ruleDescription.toUpperCase());
        }
        if (ruleTemplateId != null) {
            crit.eq("ruleTemplateId", ruleTemplateId);
        }
        if (delegateRule != null) {
            crit.eq("delegateRule", delegateRule);
        }
        if (extensionValues != null && !extensionValues.isEmpty()) {
            for (Iterator iter2 = extensionValues.entrySet().iterator(); iter2.hasNext();) {
                Map.Entry entry = (Map.Entry) iter2.next();
                if (!Utilities.isEmpty((String) entry.getValue())) {
                    // Criteria extensionCrit = new Criteria();
                    // extensionCrit.addEqualTo("extensionValues.key",
                    // entry.getKey());
                    // extensionCrit.addLike("extensionValues.value",
                    // "%"+(String) entry.getValue()+"%");

                    Criteria extensionCrit2 = new Criteria(RuleExtension.class.getName());
                    extensionCrit2.eq("extensionValues.key", entry.getKey());
                    extensionCrit2.like("UPPER(extensionValues.value)", ("%" + (String) entry.getValue() + "%").toUpperCase());

                    // Criteria extensionCrit3 = new Criteria();
                    // extensionCrit3.addEqualTo("extensionValues.key",
                    // entry.getKey());
                    // extensionCrit3.addLike("extensionValues.value",
                    // ("%"+(String) entry.getValue()+"%").toLowerCase());

                    // extensionCrit.addOrCriteria(extensionCrit2);
                    // extensionCrit.addOrCriteria(extensionCrit3);
                    crit.in("ruleExtensions.ruleBaseValuesId", extensionCrit2, "ruleBaseValuesId");
                }
            }
        }
        return crit;
    }

//    private Criteria getWorkgroupOrCriteria(Collection workgroupIds) {
//        Criteria responsibilityCrit = new Criteria(RuleResponsibility.class.getName());
//        for (Iterator iter = workgroupIds.iterator(); iter.hasNext();) {
//            String workgroupIdFromList = (String) iter.next();
//            Criteria orCriteria = new Criteria(RuleResponsibility.class.getName());
//            orCriteria.like("ruleResponsibilityName", workgroupIdFromList);
//            responsibilityCrit.or(orCriteria);
//        }
//
//        Criteria crit = new Criteria();
//        crit.in("responsibilities.ruleBaseValuesId", responsibilityCrit,"ruleBaseValuesId");
//        return crit;
//    }

	private Criteria getResponsibilitySubQuery(String ruleResponsibilityName) {
		Criteria responsibilityCrit = new Criteria(RuleResponsibility.class.getName());
		responsibilityCrit.like("ruleResponsibilityName", ruleResponsibilityName);
		//ReportQueryByCriteria query = QueryFactory.newReportQuery(RuleResponsibility.class, responsibilityCrit);
		//query.setAttributes(new String[] { "ruleBaseValuesId" });
		return responsibilityCrit;
	}

//	private Criteria getWorkgroupResponsibilitySubQuery(Set<Long> workgroupIds) {
//	    	Set<String> workgroupIdStrings = new HashSet<String>();
//	    	for (Long workgroupId : workgroupIds) {
//	    	    workgroupIdStrings.add(workgroupId.toString());
//	    	}
//		Criteria responsibilityCrit = new Criteria(RuleResponsibility.class.getName());
//		responsibilityCrit.in("ruleResponsibilityName", new ArrayList(workgroupIds));
//		responsibilityCrit.eq("ruleResponsibilityType", KEWConstants.RULE_RESPONSIBILITY_GROUP_ID);
////		ReportQueryByCriteria query = QueryFactory.newReportQuery(RuleResponsibility.class, responsibilityCrit);
////		query.setAttributes(new String[] { "ruleBaseValuesId" });
//		return responsibilityCrit;
//	}

	public List findByPreviousVersionId(Long previousVersionId) {
		Criteria crit = new Criteria(RuleBaseValues.class.getName());
		crit.eq("previousVersionId", previousVersionId);
		return (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
	}

	public RuleBaseValues findDefaultRuleByRuleTemplateId(Long ruleTemplateId) {
		Criteria crit = new Criteria(RuleBaseValues.class.getName());
		crit.eq("ruleTemplateId", ruleTemplateId);
		crit.eq("templateRuleInd", new Boolean(true));
		List rules = (List) new QueryByCriteria(entityManager, crit).toQuery().getResultList();
		if (rules != null && !rules.isEmpty()) {
			return (RuleBaseValues) rules.get(0);
		}
		return null;
	}

	public void clearCache() {
		//TODO clear the cache
	}

	public void retrieveAllReferences(RuleBaseValues rule) {
		// getPersistenceBroker().retrieveAllReferences(rule);
	}

	public RuleBaseValues getParentRule(Long ruleBaseValuesId) {
		Criteria criteria = new Criteria(RuleBaseValues.class.getName());
		criteria.eq("currentInd", Boolean.TRUE);
		criteria.eq("responsibilities.delegationRules.delegateRuleId", ruleBaseValuesId);
		Collection rules = new QueryByCriteria(entityManager, criteria).toQuery().getResultList();
		RuleBaseValues rule = null;
		for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
			RuleBaseValues currentRule = (RuleBaseValues) iterator.next();
			if (rule == null || currentRule.getVersionNbr().intValue() > rule.getVersionNbr().intValue()) {
				rule = currentRule;
			}
		}
		return rule;
	}

	public List findOldDelegations(final RuleBaseValues oldRule, final RuleBaseValues newRule) {
		
		Query q = entityManager.createNativeQuery(OLD_DELEGATIONS_SQL);
		q.setParameter(1, oldRule.getRuleBaseValuesId().longValue());
		q.setParameter(2, newRule.getRuleBaseValuesId().longValue());
		List oldDelegations = new ArrayList();
		for(Object l:q.getResultList()){
			oldDelegations.add(findRuleBaseValuesById((Long)l));
		}
		return oldDelegations;
	
	}

}