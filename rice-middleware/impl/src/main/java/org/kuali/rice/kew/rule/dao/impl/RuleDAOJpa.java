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
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleExtensionBo;
import org.kuali.rice.kew.rule.RuleResponsibilityBo;
import org.kuali.rice.kew.rule.dao.RuleDAO;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

public class RuleDAOJpa implements RuleDAO {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RuleDAOJpa.class);

	private EntityManager entityManager;

    private DataObjectService dataObjectService;

	private static final String OLD_DELEGATIONS_SQL =
		"select oldDel.dlgn_rule_id "+
		"from krew_rule_rsp_t oldRsp, krew_dlgn_rsp_t oldDel "+
		"where oldRsp.rule_id=? and "+
		"oldRsp.rule_rsp_id=oldDel.rule_rsp_id and "+
		"oldDel.dlgn_rule_base_val_id not in "+
		"(select newDel.dlgn_rule_base_val_id from krew_rule_rsp_t newRsp, krew_dlgn_rsp_t newDel "+
		"where newRsp.rule_id=? and "+
		"newRsp.rule_rsp_id=newDel.rule_rsp_id)";

    @Override
    public RuleBaseValues save(RuleBaseValues ruleBaseValues) {
        if ( ruleBaseValues == null ) {
            return null;
        }

        ruleBaseValues = getDataObjectService().save(ruleBaseValues);

        if ( ruleBaseValues.getRoleResponsibilities() != null ) {
            for ( RuleResponsibilityBo resp : ruleBaseValues.getRuleResponsibilities() ) {
                resp.setRuleBaseValues(ruleBaseValues);
                resp.setRuleBaseValuesId(ruleBaseValues.getId());
            }
        }

        if ( ruleBaseValues.getRuleResponsibilities() != null && ruleBaseValues.getRuleResponsibilities().size() > 0 ) {
            return getDataObjectService().save(ruleBaseValues);
        } else {
            return ruleBaseValues;
        }
    }

	@Override
    public List<RuleBaseValues> fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateId, List documentTypes) {
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        List<Predicate> datePredicateList = generateFromToDatePredicate(new Date());
        Predicate[] datePreds = generateFromToDatePredicate(new Date()).
                            toArray(new Predicate[datePredicateList.size()]);
        builder.setPredicates(in("docTypeName", documentTypes),
                               equal("ruleTemplateId",ruleTemplateId),
                               equal("currentInd",Boolean.TRUE),
                               equal("active",Boolean.TRUE),
                               equal("delegateRule",Boolean.FALSE),
                               equal("templateRuleInd",Boolean.FALSE),
                               and(datePreds));

        return getDataObjectService().findMatching(RuleBaseValues.class,builder.build()).getResults();
	}

	@Override
    public List<RuleBaseValues> fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateId, List documentTypes, Timestamp effectiveDate) {
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(equal("ruleTemplateId",ruleTemplateId));
        predicates.add(in("docTypeName", documentTypes));
        predicates.add(equal("active", Boolean.TRUE));
        predicates.add(equal("delegateRule",Boolean.FALSE));
        predicates.add(equal("templateRuleInd",Boolean.FALSE));

        if(effectiveDate != null){
            predicates.add(lessThanOrEqual("activationDate",effectiveDate));
            predicates.add(greaterThanOrEqual("deactivationDate", effectiveDate));
        }
        List<Predicate> datePredicateList = generateFromToDatePredicate(new Date());
        Predicate[] datePreds = generateFromToDatePredicate(new Date()).
                toArray(new Predicate[datePredicateList.size()]);
        predicates.add(and(datePreds));
        Predicate[] preds = predicates.toArray(new Predicate[predicates.size()]);
        builder.setPredicates(preds);
        QueryResults<RuleBaseValues> results = getDataObjectService().findMatching(RuleBaseValues.class,
                                            builder.build());
        return results.getResults();
	}

    public List<Predicate> generateFromToDatePredicate(Date date){
        List<Predicate> datePredicates = new ArrayList<Predicate>();

        Predicate orFromDateValue = or(lessThanOrEqual("fromDateValue",new Timestamp(date.getTime())),
              isNull("fromDateValue"));
        Predicate orToDateValue = or(greaterThanOrEqual("toDateValue",new Timestamp(date.getTime())),
              isNull("toDateValue"));

        datePredicates.add(orFromDateValue);
        datePredicates.add(orToDateValue);

        return datePredicates;
    }

	@Override
    public List<RuleBaseValues> fetchAllRules(boolean currentRules) {
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        builder.setPredicates(equal("currentInd",new Boolean(currentRules)),
                        equal("templateRuleInd",Boolean.FALSE));
        builder.setOrderByFields(OrderByField.Builder.create("activationDate", OrderDirection.DESCENDING).build()) ;
        return getDataObjectService().findMatching(RuleBaseValues.class,builder.build()).getResults();
	}

	@Override
    public void delete(String ruleBaseValuesId) {
        getDataObjectService().delete(getDataObjectService().find(RuleBaseValues.class, ruleBaseValuesId));
	}

	@Override
    public List<RuleBaseValues> findByDocumentId(String documentId) {
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        builder.setPredicates(equal("documentId",documentId));
        return getDataObjectService().findMatching(RuleBaseValues.class,builder.build()).getResults();
	}

    @Override
    public RuleBaseValues findRuleBaseValuesByName(String name) {
        if (name == null) {
        	return null;
        }
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        builder.setPredicates(equal("name",name),equal("currentInd",Boolean.TRUE));

        QueryResults<RuleBaseValues> results = getDataObjectService().findMatching(RuleBaseValues.class,
                builder.build());
        if(results != null && !results.getResults().isEmpty()) {
            return results.getResults().get(0);
        }
        return null;
    }

	@Override
    public RuleBaseValues findRuleBaseValuesById(String ruleBaseValuesId) {
		if (ruleBaseValuesId == null) {
			return null;
		}

        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        builder.setPredicates(equal("id",ruleBaseValuesId));

        QueryResults<RuleBaseValues> results = getDataObjectService().findMatching(
                RuleBaseValues.class,builder.build());
        if(results != null && !results.getResults().isEmpty()) {
            return results.getResults().get(0);
        }
        return null;
	}

	@Override
    public List<RuleBaseValues> findRuleBaseValuesByResponsibilityReviewer(String reviewerName, String type) {
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        builder.setPredicates(equal("ruleResponsibilityName",reviewerName),
                               equal("ruleResponsibilityType",type));

		List responsibilities = getDataObjectService().findMatching(
                                    RuleResponsibilityBo.class,builder.build()).getResults();
		List rules = new ArrayList();

		for (Iterator iter = responsibilities.iterator(); iter.hasNext();) {
			RuleResponsibilityBo responsibility = (RuleResponsibilityBo) iter.next();
			RuleBaseValues rule = responsibility.getRuleBaseValues();
			if (rule != null && rule.getCurrentInd() != null && rule.getCurrentInd().booleanValue()) {
				rules.add(rule);
			}
		}
		return rules;
	}

	@Override
    public List<RuleBaseValues> findRuleBaseValuesByResponsibilityReviewerTemplateDoc(String ruleTemplateName,
            String documentType, String reviewerName, String type) {

        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(equal("ruleResponsibilityName",reviewerName));
        predicates.add(equal("ruleResponsibilityType",type));
        predicates.add(equal("ruleBaseValues.currentInd",Boolean.TRUE));
        if (!StringUtils.isBlank(ruleTemplateName)) {
            predicates.add(like("ruleBaseValues.ruleTemplate.name", ruleTemplateName.replace("*", "%").concat("%")));
        }

        if (!StringUtils.isBlank(documentType)) {
            predicates.add(like("ruleBaseValues.docTypeName", documentType.replace("*", "%").concat("%")));
        }

		List responsibilities = getDataObjectService().findMatching(
                                RuleResponsibilityBo.class,builder.build()).getResults();
		List rules = new ArrayList();

		for (Iterator iter = responsibilities.iterator(); iter.hasNext();) {
			RuleResponsibilityBo responsibility = (RuleResponsibilityBo) iter.next();
			RuleBaseValues rule = responsibility.getRuleBaseValues();
			if (rule != null && rule.getCurrentInd() != null && rule.getCurrentInd().booleanValue()) {
				rules.add(rule);
			}
		}
		return rules;
	}

	@Override
    public RuleResponsibilityBo findRuleResponsibility(String responsibilityId) {
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        builder.setPredicates(equal("responsibilityId",responsibilityId));
		Collection responsibilities = getDataObjectService().findMatching(
                RuleResponsibilityBo.class,builder.build()).getResults();
		for (Iterator iterator = responsibilities.iterator(); iterator.hasNext();) {
			RuleResponsibilityBo responsibility = (RuleResponsibilityBo) iterator.next();
			if (responsibility.getRuleBaseValues().getCurrentInd().booleanValue()) {
				return responsibility;
			}
		}
		return null;
	}

	@Override
    public List<RuleBaseValues> search(String docTypeName, String ruleId, String ruleTemplateId, String ruleDescription, String groupId, String principalId, Boolean delegateRule, Boolean activeInd, Map extensionValues, String workflowIdDirective) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<RuleBaseValues> cq = cb.createQuery(RuleBaseValues.class);
        Root<RuleBaseValues> root = cq.from(RuleBaseValues.class);
        List<javax.persistence.criteria.Predicate> predicates = getSearchCriteria(root,cq,docTypeName, ruleTemplateId, ruleDescription, delegateRule, activeInd, extensionValues);

        if (ruleId != null) {
            predicates.add(cb.equal(root.get("id"),ruleId));
        }
        if (groupId != null) {
            predicates.add(cb.in(root.get("id")).value(getRuleResponsibilitySubQuery(
                    groupId, cq)));
        }
        Collection<String> kimGroupIds = new HashSet<String>();
        Boolean searchUser = Boolean.FALSE;
        Boolean searchUserInWorkgroups = Boolean.FALSE;

        if ("group".equals(workflowIdDirective)) {
            searchUserInWorkgroups = Boolean.TRUE;
        } else if (StringUtils.isBlank(workflowIdDirective)) {
            searchUser = Boolean.TRUE;
            searchUserInWorkgroups = Boolean.TRUE;
        } else {
            searchUser = Boolean.TRUE;
        }

        if (!org.apache.commons.lang.StringUtils.isEmpty(principalId) && searchUserInWorkgroups) {
            Principal principal = null;

            principal = KimApiServiceLocator.getIdentityService().getPrincipal(principalId);

            if (principal == null)
            {
            	throw new RiceRuntimeException("Failed to locate user for the given principal id: " + principalId);
            }
            kimGroupIds = KimApiServiceLocator.getGroupService().getGroupIdsByPrincipalId(principalId);
        }
        Subquery<RuleResponsibilityBo> subquery = addResponsibilityCriteria(cq,kimGroupIds, principalId, searchUser, searchUserInWorkgroups);

        if(subquery != null){
            predicates.add(cb.in(root.get("id")).value(subquery));
        }
        cq.distinct(true);
        javax.persistence.criteria.Predicate[] preds = predicates.toArray(
                new javax.persistence.criteria.Predicate[predicates.size()]);
        cq.where(preds);
        TypedQuery<RuleBaseValues> q = getEntityManager().createQuery(cq);

        return q.getResultList();
	}

    @Override
    public List<RuleBaseValues> search(String docTypeName, String ruleTemplateId, String ruleDescription, Collection<String> workgroupIds, String workflowId, Boolean delegateRule, Boolean activeInd, Map extensionValues, Collection actionRequestCodes) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<RuleBaseValues> cq = cb.createQuery(RuleBaseValues.class);
        Root<RuleBaseValues> root = cq.from(RuleBaseValues.class);
        List<javax.persistence.criteria.Predicate> predicates = getSearchCriteria(root,cq,docTypeName,
                        ruleTemplateId, ruleDescription, delegateRule, activeInd, extensionValues);
        Subquery<RuleResponsibilityBo> subquery = addResponsibilityCriteria(cq,workgroupIds, workflowId, actionRequestCodes,
                        (workflowId != null), ((workgroupIds != null) && !workgroupIds.isEmpty()));
        if (subquery != null){
            predicates.add(cb.in(root.get("id")).value(subquery));
        }
        javax.persistence.criteria.Predicate[] preds = predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]);
        cq.where(preds);
        TypedQuery<RuleBaseValues> q = getEntityManager().createQuery(cq);

        return q.getResultList();
    }

    private Subquery<RuleResponsibilityBo> addResponsibilityCriteria(CriteriaQuery<RuleBaseValues> query, Collection<String> kimGroupIds,
            String principalId, Boolean searchUser, Boolean searchUserInWorkgroups) {
        Collection<String> workgroupIdStrings = new ArrayList<String>();
        for (String workgroupId : kimGroupIds) {
            workgroupIdStrings.add(workgroupId.toString());
        }
        return addResponsibilityCriteria(query, workgroupIdStrings,principalId,new ArrayList<String>(),
                                        searchUser, searchUserInWorkgroups);
    }

    private Subquery<RuleResponsibilityBo> addResponsibilityCriteria(CriteriaQuery<RuleBaseValues> query, Collection<String> workgroupIds, String workflowId, Collection actionRequestCodes, Boolean searchUser, Boolean searchUserInWorkgroups) {

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<RuleResponsibilityBo> subquery = query.subquery(RuleResponsibilityBo.class);
        Root fromResp = subquery.from(RuleResponsibilityBo.class);

        List<javax.persistence.criteria.Predicate> respPredicates = new
                        ArrayList<javax.persistence.criteria.Predicate>();

        List<javax.persistence.criteria.Predicate> ruleRespNamePredicates = new
                ArrayList<javax.persistence.criteria.Predicate>();

        List<javax.persistence.criteria.Predicate> userNamePreds =
                new ArrayList<javax.persistence.criteria.Predicate>();

        List<javax.persistence.criteria.Predicate> workgroupPreds =
                new ArrayList<javax.persistence.criteria.Predicate>();


        if ( (actionRequestCodes != null) && (!actionRequestCodes.isEmpty()) ) {
            Expression<String> exp = fromResp.get("actionRequestedCd");
            javax.persistence.criteria.Predicate actionRequestPredicate = exp.in(actionRequestCodes);

            respPredicates.add(actionRequestPredicate);
        }

        if (!org.apache.commons.lang.StringUtils.isEmpty(workflowId)) {
            // workflow user id exists
            if (searchUser != null && searchUser) {
                // searching user wishes to search for rules specific to user
                userNamePreds.add(cb.like(fromResp.get("ruleResponsibilityName"),workflowId));
                userNamePreds.add(cb.equal(fromResp.get("ruleResponsibilityType"),KewApiConstants.RULE_RESPONSIBILITY_WORKFLOW_ID));

                javax.persistence.criteria.Predicate[] preds = userNamePreds.toArray(new javax.persistence.criteria.Predicate[userNamePreds.size()]);
                ruleRespNamePredicates.add(cb.and(preds));

            }
            if ( (searchUserInWorkgroups != null && searchUserInWorkgroups) && (workgroupIds != null) && (!workgroupIds.isEmpty()) ) {
                // at least one workgroup id exists and user wishes to search on workgroups

                Expression<String> exp = fromResp.get("ruleResponsibilityName");
                javax.persistence.criteria.Predicate groupIdPredicate = exp.in(workgroupIds);
                workgroupPreds.add(groupIdPredicate);
                workgroupPreds.add(cb.equal(fromResp.get("ruleResponsibilityType"),
                        KewApiConstants.RULE_RESPONSIBILITY_GROUP_ID));
                javax.persistence.criteria.Predicate[] preds = workgroupPreds.toArray(new javax.persistence.criteria.Predicate[workgroupPreds.size()]);
                ruleRespNamePredicates.add(cb.and(preds));
            }
        } else if ( (workgroupIds != null) && (workgroupIds.size() == 1) ) {
            // no user and one workgroup id
            workgroupPreds.add(cb.like(fromResp.get("ruleResponsibilityName"),
                                workgroupIds.iterator().next()));
            workgroupPreds.add(cb.equal(fromResp.get("ruleResponsibilityType"),
                        KewApiConstants.RULE_RESPONSIBILITY_GROUP_ID));
            javax.persistence.criteria.Predicate[] preds = workgroupPreds.toArray(new javax.persistence.criteria.Predicate[workgroupPreds.size()]);
            ruleRespNamePredicates.add(cb.and(preds));

        } else if ( (workgroupIds != null) && (workgroupIds.size() > 1) ) {
            // no user and more than one workgroup id

            Expression<String> exp = fromResp.get("ruleResponsibilityName");
            javax.persistence.criteria.Predicate groupIdPredicate = exp.in(workgroupIds);
            workgroupPreds.add(cb.equal(fromResp.get("ruleResponsibilityType"),
                                        KewApiConstants.RULE_RESPONSIBILITY_GROUP_ID));
            javax.persistence.criteria.Predicate[] preds = workgroupPreds.toArray(new javax.persistence.criteria.Predicate[workgroupPreds.size()]);
            ruleRespNamePredicates.add(cb.and(preds));
        }

        if (!ruleRespNamePredicates.isEmpty()) {
            javax.persistence.criteria.Predicate[] preds = ruleRespNamePredicates.toArray(new javax.persistence.criteria.Predicate[ruleRespNamePredicates.size()]);
            respPredicates.add(cb.or(preds));
        }

        if (!respPredicates.isEmpty()) {

            javax.persistence.criteria.Predicate[] preds = respPredicates.toArray(
                    new javax.persistence.criteria.Predicate[respPredicates.size()]);
            subquery.where(preds);
            subquery.select(fromResp.get("ruleBaseValuesId"));
            return subquery;
        }
        return null;
    }

    private List<javax.persistence.criteria.Predicate> getSearchCriteria(Root<RuleBaseValues> root,CriteriaQuery<RuleBaseValues> query,
            String docTypeName, String ruleTemplateId,
            String ruleDescription, Boolean delegateRule, Boolean activeInd, Map extensionValues) {
        List<javax.persistence.criteria.Predicate> predicates = new ArrayList<javax.persistence.criteria.Predicate>();
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        predicates.add(cb.equal(root.get("currentInd"),Boolean.TRUE));
        predicates.add(cb.equal(root.get("templateRuleInd"), Boolean.FALSE));
        if (activeInd != null) {
            predicates.add(cb.equal(root.get("active"),activeInd));
        }
        if (docTypeName != null) {
            predicates.add(cb.like(cb.upper(root.<String>get("docTypeName")), docTypeName.toUpperCase()));
        }
        if (ruleDescription != null && !ruleDescription.trim().equals("")) {
            predicates.add(cb.like(cb.upper(root.<String>get("description")),ruleDescription.toUpperCase()));
        }
        if (ruleTemplateId != null) {
            predicates.add(cb.equal(root.get("ruleTemplateId"),ruleTemplateId));
        }
        if (delegateRule != null) {
            predicates.add(cb.equal(root.get("delegateRule"),delegateRule));
        }
        if (extensionValues != null && !extensionValues.isEmpty()) {
            for (Iterator iter2 = extensionValues.entrySet().iterator(); iter2.hasNext();) {
                Map.Entry entry = (Map.Entry) iter2.next();
                if (!StringUtils.isEmpty((String) entry.getValue())) {
                    Subquery ruleExtSubQuery = query.subquery(RuleExtensionBo.class);
                    Root<RuleExtensionBo> ruleExtRoot = ruleExtSubQuery.from(RuleExtensionBo.class);
                    javax.persistence.criteria.Predicate predAnd = cb.and(
                            cb.equal(ruleExtRoot.get("extensionValues").get("key"),entry.getKey()),
                            cb.like(ruleExtRoot.get("extensionValues").<String>get("value"),
                                    ("%" + (String) entry.getValue() + "%").toUpperCase()));
                    ruleExtSubQuery.where(predAnd);
                    ruleExtSubQuery.select(ruleExtRoot.get("ruleBaseValuesId"));

                    predicates.add(cb.in(root.get("id")).value(ruleExtSubQuery));
                }
            }
        }
        return predicates;
    }

    private Subquery<RuleResponsibilityBo> getRuleResponsibilitySubQuery(String ruleRespName,
                        CriteriaQuery<RuleBaseValues> query){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<RuleResponsibilityBo> subquery = query.subquery(RuleResponsibilityBo.class);
        Root fromResp = subquery.from(RuleResponsibilityBo.class);
        subquery.where(cb.equal(fromResp.get("ruleResponsibilityName"),ruleRespName));
        subquery.select(fromResp.get("ruleBaseValuesId"));

        return subquery;
    }


	@Override
    public List<RuleBaseValues> findByPreviousRuleId(String previousRuleId) {
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        builder.setPredicates(equal("previousRuleId",previousRuleId));
		return getDataObjectService().findMatching(RuleBaseValues.class,builder.build()).getResults();
	}

	@Override
    public RuleBaseValues findDefaultRuleByRuleTemplateId(String ruleTemplateId) {
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        if(StringUtils.isNotBlank(ruleTemplateId)){
            builder.setPredicates(equal("ruleTemplateId",ruleTemplateId),
                    equal("templateRuleInd",Boolean.TRUE));

            List rules = getDataObjectService().findMatching(RuleBaseValues.class,builder.build()).getResults();
            if (rules != null && !rules.isEmpty()) {
                return (RuleBaseValues) rules.get(0);
            }
        }

		return null;
	}

	@Override
    public void retrieveAllReferences(RuleBaseValues rule) {
		// getPersistenceBroker().retrieveAllReferences(rule);
	}

	@Override
    public RuleBaseValues getParentRule(String ruleBaseValuesId) {
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        builder.setPredicates(equal("responsibilities.delegationRules.delegateRuleId",ruleBaseValuesId),
                equal("currentInd",Boolean.TRUE));

		Collection rules = getDataObjectService().findMatching(RuleBaseValues.class,builder.build()).getResults();
		RuleBaseValues rule = null;
		for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
			RuleBaseValues currentRule = (RuleBaseValues) iterator.next();
			if (rule == null || currentRule.getVersionNbr().intValue() > rule.getVersionNbr().intValue()) {
				rule = currentRule;
			}
		}
		return rule;
	}

	@Override
    public List findOldDelegations(final RuleBaseValues oldRule, final RuleBaseValues newRule) {

		Query q = entityManager.createNativeQuery(OLD_DELEGATIONS_SQL);
		q.setParameter(1, oldRule.getId());
		q.setParameter(2, newRule.getId());
		List oldDelegations = new ArrayList();
		for(Object l:q.getResultList()){
			// FIXME: KULRICE-5201 - This used to be a cast by new Long(l) -- assuming that the Object here in result list is actually a string or is castable to string by .toString()
			oldDelegations.add(findRuleBaseValuesById(String.valueOf(l)));
		}
		return oldDelegations;

	}

	@Override
    public String findResponsibilityIdForRule(String ruleName, String ruleResponsibilityName, String ruleResponsibilityType) {
        org.kuali.rice.core.api.criteria.QueryByCriteria.Builder builder =
                org.kuali.rice.core.api.criteria.QueryByCriteria.Builder.create();
        builder.setPredicates(equal("ruleResponsibilityName",ruleResponsibilityName),
                                equal("ruleResponsibilityType",ruleResponsibilityType),
                                equal("ruleBaseValues.currentInd",Boolean.TRUE),
                                equal("ruleBaseValues.name",ruleName));
		Collection responsibilities = getDataObjectService().findMatching(
                            RuleResponsibilityBo.class,builder.build()).getResults();
		if (responsibilities != null) {
			for (Iterator iter = responsibilities.iterator(); iter.hasNext();) {
				RuleResponsibilityBo responsibility = (RuleResponsibilityBo) iter.next();
				return responsibility.getResponsibilityId();
			}
		}
		return null;
	}

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

}
