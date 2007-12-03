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
package edu.iu.uis.eden.routetemplate.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.springmodules.orm.ojb.PersistenceBrokerCallback;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleResponsibility;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.Utilities;

public class RuleDAOOjbImpl extends PersistenceBrokerDaoSupport implements RuleDAO {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RuleDAOOjbImpl.class);

	private static final String OLD_DELEGATIONS_SQL =
		"select oldDel.dlgn_rule_base_val_id "+
		"from en_rule_rsp_t oldRsp, en_dlgn_rsp_t oldDel "+
		"where oldRsp.rule_base_val_id=? and "+
		"oldRsp.rule_rsp_id=oldDel.rule_rsp_id and "+
		"oldDel.dlgn_rule_base_val_id not in "+
		"(select newDel.dlgn_rule_base_val_id from en_rule_rsp_t newRsp, en_dlgn_rsp_t newDel "+
		"where newRsp.rule_base_val_id=? and "+
		"newRsp.rule_rsp_id=newDel.rule_rsp_id)";

	public void save(RuleBaseValues ruleBaseValues) {
		this.getPersistenceBrokerTemplate().store(ruleBaseValues);
	}

	public void saveDeactivationDate(final RuleBaseValues ruleBaseValues) {

		final String sql = "update en_rule_base_val_t set RULE_BASE_VAL_ACTVN_DT = ?, RULE_BASE_VAL_DACTVN_DT = ? where rule_base_val_id = ?";
		this.getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
			public Object doInPersistenceBroker(PersistenceBroker pb) {
				PreparedStatement ps = null;
				try {
					ps = pb.serviceConnectionManager().getConnection().prepareStatement(sql);
					ps.setTimestamp(1, ruleBaseValues.getActivationDate());
					ps.setTimestamp(2, ruleBaseValues.getDeactivationDate());
					ps.setLong(3, ruleBaseValues.getRuleBaseValuesId().longValue());
					ps.executeUpdate();
				} catch (Exception e) {
					throw new WorkflowRuntimeException("error saving deactivation date", e);
				} finally {
					if (ps != null) {
						try {
							ps.close();
						} catch (Exception e) {
							LOG.error("error closing preparedstatement", e);
						}
					}
				}
				return null;
			}
		});

	}

	public List fetchAllCurrentRulesForTemplateDocCombination(Long ruleTemplateId, List documentTypes) {
		Criteria crit = new Criteria();
		crit.addIn("docTypeName", documentTypes);
		crit.addEqualTo("ruleTemplateId", ruleTemplateId);
		crit.addEqualTo("currentInd", new Boolean(true));
		crit.addEqualTo("activeInd", new Boolean(true));
		crit.addEqualTo("delegateRule", new Boolean(false));
		crit.addEqualTo("templateRuleInd", new Boolean(false));

		crit.addLessOrEqualThan("fromDate", new Timestamp(new Date().getTime()));
		crit.addGreaterOrEqualThan("toDate", new Timestamp(new Date().getTime()));
		return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleBaseValues.class, crit));
	}

	public List fetchAllCurrentRulesForTemplateDocCombination(Long ruleTemplateId, List documentTypes, Timestamp effectiveDate) {
		Criteria crit = new Criteria();
		crit.addIn("docTypeName", documentTypes);
		crit.addEqualTo("ruleTemplateId", ruleTemplateId);
		crit.addEqualTo("activeInd", new Boolean(true));
		crit.addEqualTo("delegateRule", new Boolean(false));
		crit.addEqualTo("templateRuleInd", new Boolean(false));
		if (effectiveDate != null) {
			crit.addLessOrEqualThan("activationDate", effectiveDate);
			crit.addGreaterThan("deactivationDate", effectiveDate);
		}

		crit.addLessOrEqualThan("fromDate", new Timestamp(new Date().getTime()));
		crit.addGreaterOrEqualThan("toDate", new Timestamp(new Date().getTime()));
		return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleBaseValues.class, crit));
	}

	public List fetchAllRules(boolean currentRules) {
		Criteria crit = new Criteria();
		crit.addEqualTo("currentInd", new Boolean(currentRules));
		crit.addEqualTo("templateRuleInd", new Boolean(false));
		// crit.addEqualTo("delegateRule", new Boolean(false));

		QueryByCriteria query = new QueryByCriteria(RuleBaseValues.class, crit);
		query.addOrderByDescending("activationDate");

		return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(query);
	}

	public List findResponsibilitiesByDelegationRuleId(Long delegationRuleId) {
		Criteria crit = new Criteria();
		crit.addEqualTo("currentInd", new Boolean(true));
		crit.addEqualTo("templateRuleInd", new Boolean(false));

		Criteria criteriaDelegationId = new Criteria();
		criteriaDelegationId.addEqualTo("responsibilities.delegateRuleId", delegationRuleId);
		crit.addAndCriteria(criteriaDelegationId);
		return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleBaseValues.class, crit));
	}

	public void delete(Long ruleBaseValuesId) {
		this.getPersistenceBrokerTemplate().delete(findRuleBaseValuesById(ruleBaseValuesId));
	}

	public List findByRouteHeaderId(Long routeHeaderId) {
		Criteria crit = new Criteria();
		crit.addEqualTo("routeHeaderId", routeHeaderId);
		return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleBaseValues.class, crit));
	}

    public RuleBaseValues findRuleBaseValuesByName(String name) {
        Criteria crit = new Criteria();
        crit.addEqualTo("name", name);
        crit.addEqualTo("currentInd", Boolean.TRUE);
        return (RuleBaseValues) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(RuleBaseValues.class, crit));
    }

	public RuleBaseValues findRuleBaseValuesById(Long ruleBaseValuesId) {
		Criteria crit = new Criteria();
		crit.addEqualTo("ruleBaseValuesId", ruleBaseValuesId);
		// crit.addEqualTo("currentInd", new Boolean(true));
		return (RuleBaseValues) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(RuleBaseValues.class, crit));
	}

	public List findRuleBaseValuesByResponsibilityReviewer(String reviewerName, String type) {
		Criteria crit = new Criteria();
		crit.addEqualTo("ruleResponsibilityName", reviewerName);
		crit.addEqualTo("ruleResponsibilityType", type);

		List responsibilities = (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleResponsibility.class, crit));
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
	    Criteria crit = new Criteria();
		crit.addEqualTo("ruleResponsibilityName", reviewerName);
		crit.addEqualTo("ruleResponsibilityType", type);
		crit.addEqualTo("ruleBaseValues.currentInd", Boolean.TRUE);
		if (!StringUtils.isBlank(ruleTemplateName)) {
		    crit.addLike("ruleBaseValues.ruleTemplate.name", ruleTemplateName.replace("*", "%").concat("%"));
		}
		if (!StringUtils.isBlank(documentType)) {
		    crit.addLike("ruleBaseValues.docTypeName", documentType.replace("*", "%").concat("%"));
		}

		List responsibilities = (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleResponsibility.class, crit));
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

	public List findRuleBaseValuesByObjectGraph(RuleBaseValues ruleBaseValues) {
		ruleBaseValues.setCurrentInd(new Boolean(true));
		ruleBaseValues.setTemplateRuleInd(Boolean.FALSE);
		return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(ruleBaseValues));
	}

	public RuleResponsibility findRuleResponsibility(Long responsibilityId) {
		Criteria crit = new Criteria();
		crit.addEqualTo("responsibilityId", responsibilityId);
		Collection responsibilities = this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleResponsibility.class, crit));
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
            crit.addEqualTo("ruleBaseValuesId", ruleId);
        }
        if (workgroupId != null) {
            crit.addIn("responsibilities.ruleBaseValuesId", getResponsibilitySubQuery(workgroupId.toString()));
        }
        boolean searchUser = false;
        boolean searchUserInWorkgroups = false;
        if ("workgroup".equals(workflowIdDirective)) {
            searchUserInWorkgroups = true;
        } else if ("both".equals(workflowIdDirective)) {
            searchUser = true;
            searchUserInWorkgroups = true;
        } else {
            searchUser = true;
        }
        Criteria orCriteria = new Criteria();
        if (!Utilities.isEmpty(workflowId) && searchUser) {
            Criteria userCrit = new Criteria();
            userCrit.addIn("responsibilities.ruleBaseValuesId", getResponsibilitySubQuery(workflowId));
            orCriteria.addOrCriteria(userCrit);
        }
        if (!Utilities.isEmpty(workflowId) && searchUserInWorkgroups) {
            Criteria workgroupCrit = new Criteria();
            WorkflowUser user = null;
            try {
        	user = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(workflowId));
            } catch (EdenUserNotFoundException e) {
        	throw new WorkflowRuntimeException(e);
            }
            if (user == null) {
        	throw new WorkflowRuntimeException("Failed to locate user for the given workflow id: " + workflowId);
            }
            Set<Long> workgroupIds = KEWServiceLocator.getWorkgroupService().getUsersGroupIds(user);
            workgroupCrit.addIn("responsibilities.ruleBaseValuesId", getWorkgroupResponsibilitySubQuery(workgroupIds));
            orCriteria.addOrCriteria(workgroupCrit);
        }
        if (!orCriteria.isEmpty()) {
            crit.addAndCriteria(orCriteria);
        }
        if (!Utilities.isEmpty(roleName)) {
            crit.addIn("responsibilities.ruleBaseValuesId", getResponsibilitySubQuery(roleName));
        }
        try {
		return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleBaseValues.class, crit, true));
        } catch (Exception e) {
            LOG.error("Caught error performing search using criteria " + crit.toString(),e);
            throw new WorkflowRuntimeException(e);
        }
	}

    public List search(String docTypeName, Long ruleTemplateId, String ruleDescription, Collection<String> workgroupIds, String workflowId, String roleName, Boolean delegateRule, Boolean activeInd, Map extensionValues, Collection actionRequestCodes) {
        Criteria crit = getSearchCriteria(docTypeName, ruleTemplateId, ruleDescription, delegateRule, activeInd, extensionValues);
//        if (!Utilities.isEmpty(roleName)) {
//            crit.addIn("responsibilities.ruleBaseValuesId", getResponsibilitySubQuery(roleName));
//        }
//        if ( (actionRequestCodes != null) && (!actionRequestCodes.isEmpty()) ) {
//            crit.addIn("responsibilities.actionRequestedCd", actionRequestCodes);
//        }
//        // user id -- no workgroups
//        // user id -- workgroups
//        // no user id -- workgroups
//        Criteria userResponsibilityCrit = null;
//        if ( (!Utilities.isEmpty(workflowId)) &&
//             ( (workgroupIds != null) && (!workgroupIds.isEmpty()) ) ) {
//            // have user id and at least one workgroup id
//            userResponsibilityCrit = new Criteria();
//            userResponsibilityCrit.addIn("responsibilities.ruleBaseValuesId", getResponsibilitySubQuery(workflowId));
//            Criteria workgroupCrit = this.getWorkgroupOrCriteria(workgroupIds);
//            userResponsibilityCrit.addOrCriteria(workgroupCrit);
//        } else if ( (!Utilities.isEmpty(workflowId)) &&
//                    ( (workgroupIds == null) || (workgroupIds.isEmpty()) ) ) {
//            // have user id and no workgroup ids
//            userResponsibilityCrit = new Criteria();
//            userResponsibilityCrit.addIn("responsibilities.ruleBaseValuesId", getResponsibilitySubQuery(workflowId));
//        } else if ( (Utilities.isEmpty(workflowId)) &&
//                    ( (workgroupIds != null) && (!workgroupIds.isEmpty()) ) ) {
//            // have no user id and at least one workgroup id
//            userResponsibilityCrit = this.getWorkgroupOrCriteria(workgroupIds);
//        }
//        if (userResponsibilityCrit != null) {
//            crit.addAndCriteria(userResponsibilityCrit);
//        }
        crit.addIn("responsibilities.ruleBaseValuesId", getResponsibilitySubQuery(workgroupIds, workflowId, roleName, actionRequestCodes));

        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleBaseValues.class, crit, true));
    }

    private ReportQueryByCriteria getResponsibilitySubQuery(Collection<String> workgroupIds, String workflowId, String roleName, Collection actionRequestCodes) {
        Criteria responsibilityCrit = new Criteria();
        if ( (actionRequestCodes != null) && (!actionRequestCodes.isEmpty()) ) {
            responsibilityCrit.addIn("actionRequestedCd", actionRequestCodes);
        }

        Criteria ruleResponsibilityNameCrit = null;
        if (!Utilities.isEmpty(roleName)) {
            // role name exists... nothing else matters
            ruleResponsibilityNameCrit = new Criteria();
            ruleResponsibilityNameCrit.addLike("ruleResponsibilityName", workflowId);
            ruleResponsibilityNameCrit.addEqualTo("ruleResponsibilityType", EdenConstants.RULE_RESPONSIBILITY_ROLE_ID);
        } else {
            if (!Utilities.isEmpty(workflowId)) {
                // workflow user id exists
                ruleResponsibilityNameCrit = new Criteria();
                ruleResponsibilityNameCrit.addLike("ruleResponsibilityName", workflowId);
                ruleResponsibilityNameCrit.addEqualTo("ruleResponsibilityType", EdenConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
                if ( (workgroupIds != null) && (!workgroupIds.isEmpty()) ) {
                    // at least one workgroup id exists
                    Criteria workgroupCrit = new Criteria();
                    workgroupCrit.addIn("ruleResponsibilityName", workgroupIds);
                    workgroupCrit.addEqualTo("ruleResponsibilityType", EdenConstants.RULE_RESPONSIBILITY_WORKGROUP_ID);
                    ruleResponsibilityNameCrit.addOrCriteria(workgroupCrit);
                }
            } else if ( (workgroupIds != null) && (workgroupIds.size() == 1) ) {
                // no user and one workgroup id
                ruleResponsibilityNameCrit = new Criteria();
                ruleResponsibilityNameCrit.addLike("ruleResponsibilityName", workgroupIds.iterator().next());
                ruleResponsibilityNameCrit.addEqualTo("ruleResponsibilityType", EdenConstants.RULE_RESPONSIBILITY_WORKGROUP_ID);
            } else if ( (workgroupIds != null) && (workgroupIds.size() > 1) ) {
                // no user and more than one workgroup id
                ruleResponsibilityNameCrit = new Criteria();
                ruleResponsibilityNameCrit.addIn("ruleResponsibilityName", workgroupIds);
                ruleResponsibilityNameCrit.addEqualTo("ruleResponsibilityType", EdenConstants.RULE_RESPONSIBILITY_WORKGROUP_ID);
            }
        }
        if (ruleResponsibilityNameCrit != null) {
            responsibilityCrit.addAndCriteria(ruleResponsibilityNameCrit);
        }

        ReportQueryByCriteria query = QueryFactory.newReportQuery(RuleResponsibility.class, responsibilityCrit);
        query.setAttributes(new String[] { "ruleBaseValuesId" });
        return query;
    }

    private Criteria getSearchCriteria(String docTypeName, Long ruleTemplateId, String ruleDescription, Boolean delegateRule, Boolean activeInd, Map extensionValues) {
        Criteria crit = new Criteria();
        crit.addEqualTo("currentInd", new Boolean(true));
        crit.addEqualTo("templateRuleInd", new Boolean(false));
        if (activeInd != null) {
            crit.addEqualTo("activeInd", activeInd);
        }
        if (docTypeName != null) {
            crit.addLike("UPPER(docTypeName)", docTypeName.toUpperCase());
        }
        if (ruleDescription != null && !ruleDescription.trim().equals("")) {
            crit.addLike("UPPER(description)", ruleDescription.toUpperCase());
        }
        if (ruleTemplateId != null) {
            crit.addEqualTo("ruleTemplateId", ruleTemplateId);
        }
        if (delegateRule != null) {
            crit.addEqualTo("delegateRule", delegateRule);
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

                    Criteria extensionCrit2 = new Criteria();
                    extensionCrit2.addEqualTo("extensionValues.key", entry.getKey());
                    extensionCrit2.addLike("UPPER(extensionValues.value)", ("%" + (String) entry.getValue() + "%").toUpperCase());

                    // Criteria extensionCrit3 = new Criteria();
                    // extensionCrit3.addEqualTo("extensionValues.key",
                    // entry.getKey());
                    // extensionCrit3.addLike("extensionValues.value",
                    // ("%"+(String) entry.getValue()+"%").toLowerCase());

                    // extensionCrit.addOrCriteria(extensionCrit2);
                    // extensionCrit.addOrCriteria(extensionCrit3);
                    ReportQueryByCriteria query = QueryFactory.newReportQuery(RuleExtension.class, extensionCrit2);
                    query.setAttributes(new String[] { "ruleBaseValuesId" });
                    crit.addIn("ruleExtensions.ruleBaseValuesId", query);
                }
            }
        }
        return crit;
    }

    private Criteria getWorkgroupOrCriteria(Collection workgroupIds) {
        Criteria responsibilityCrit = new Criteria();
        for (Iterator iter = workgroupIds.iterator(); iter.hasNext();) {
            String workgroupIdFromList = (String) iter.next();
            Criteria orCriteria = new Criteria();
            orCriteria.addLike("ruleResponsibilityName", workgroupIdFromList);
            responsibilityCrit.addOrCriteria(orCriteria);
        }
        ReportQueryByCriteria query = QueryFactory.newReportQuery(RuleResponsibility.class, responsibilityCrit);
        query.setAttributes(new String[] { "ruleBaseValuesId" });
        Criteria crit = new Criteria();
        crit.addIn("responsibilities.ruleBaseValuesId", query);
        return crit;
    }

	private ReportQueryByCriteria getResponsibilitySubQuery(String ruleResponsibilityName) {
		Criteria responsibilityCrit = new Criteria();
		responsibilityCrit.addLike("ruleResponsibilityName", ruleResponsibilityName);
		ReportQueryByCriteria query = QueryFactory.newReportQuery(RuleResponsibility.class, responsibilityCrit);
		query.setAttributes(new String[] { "ruleBaseValuesId" });
		return query;
	}

	private ReportQueryByCriteria getWorkgroupResponsibilitySubQuery(Set<Long> workgroupIds) {
	    	Set<String> workgroupIdStrings = new HashSet<String>();
	    	for (Long workgroupId : workgroupIds) {
	    	    workgroupIdStrings.add(workgroupId.toString());
	    	}
		Criteria responsibilityCrit = new Criteria();
		responsibilityCrit.addIn("ruleResponsibilityName", workgroupIds);
		responsibilityCrit.addEqualTo("ruleResponsibilityType", EdenConstants.RULE_RESPONSIBILITY_WORKGROUP_ID);
		ReportQueryByCriteria query = QueryFactory.newReportQuery(RuleResponsibility.class, responsibilityCrit);
		query.setAttributes(new String[] { "ruleBaseValuesId" });
		return query;
	}

	public List findByPreviousVersionId(Long previousVersionId) {
		Criteria crit = new Criteria();
		crit.addEqualTo("previousVersionId", previousVersionId);
		return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleBaseValues.class, crit));
	}

	public RuleBaseValues findDefaultRuleByRuleTemplateId(Long ruleTemplateId) {
		Criteria crit = new Criteria();
		crit.addEqualTo("ruleTemplateId", ruleTemplateId);
		crit.addEqualTo("templateRuleInd", new Boolean(true));
		List rules = (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleBaseValues.class, crit));
		if (rules != null && !rules.isEmpty()) {
			return (RuleBaseValues) rules.get(0);
		}
		return null;
	}

	public void clearCache() {
		this.getPersistenceBroker(false).clearCache();
	}

	public void retrieveAllReferences(RuleBaseValues rule) {
		// getPersistenceBroker().retrieveAllReferences(rule);
	}

	public RuleBaseValues getParentRule(Long ruleBaseValuesId) {
		Criteria criteria = new Criteria();
		criteria.addEqualTo("currentInd", Boolean.TRUE);
		criteria.addEqualTo("responsibilities.delegationRules.delegateRuleId", ruleBaseValuesId);
		Collection rules = this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(RuleBaseValues.class, criteria));
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
		return (List)this.getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
			public Object doInPersistenceBroker(PersistenceBroker pb) {
				List oldDelegations = new ArrayList();
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					ps = pb.serviceConnectionManager().getConnection().prepareStatement(OLD_DELEGATIONS_SQL);
					ps.setLong(1, oldRule.getRuleBaseValuesId().longValue());
					ps.setLong(2, newRule.getRuleBaseValuesId().longValue());
					rs = ps.executeQuery();
					while (rs.next()) {
						oldDelegations.add(findRuleBaseValuesById(new Long(rs.getLong(1))));
					}
				} catch (Exception e) {
					throw new WorkflowRuntimeException("error saving deactivation date", e);
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (Exception e) {
							LOG.error("error closing result set", e);
						}
					}
					if (ps != null) {
						try {
							ps.close();
						} catch (Exception e) {
							LOG.error("error closing preparedstatement", e);
						}
					}
				}
				return oldDelegations;
			}
		});
	}

}