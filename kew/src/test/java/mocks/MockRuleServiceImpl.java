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
package mocks;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.routetemplate.MyRules2;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleDelegation;
import edu.iu.uis.eden.routetemplate.RuleResponsibility;
import edu.iu.uis.eden.routetemplate.RuleService;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.workgroup.GroupId;


public class MockRuleServiceImpl implements RuleService {

    Map rules = new HashMap();
    Map<String, RuleBaseValues> rulesByName = new HashMap<String, RuleBaseValues>();
    Map responsibilitiesByKey = new HashMap();
    Map responsibilitiesById = new HashMap();
    Map responsibilitiesByReviewer = new HashMap();
    Map rulesByRouteHeaderId = new HashMap();

    public RuleBaseValues getParentRule(Long ruleBaseValuesId) {
        return null;
    }
    public List fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateName, String documentType, boolean ignoreCache) {
        return null;
    }
    public Long isLockedForRouting(Long currentRuleBaseValuesId) {
        return null;
    }
    public Long route2(Long routeHeaderId, MyRules2 myRules, WorkflowUser user, String annotation, boolean blanketApprove) throws Exception {
        return null;
    }
    public Long routeRuleWithDelegate(Long routeHeaderId, RuleBaseValues parentRule, RuleBaseValues delegateRule, WorkflowUser user, String annotation, boolean blanketApprove) throws Exception {
        return null;
    }
    public List search(String docTypeName, Long ruleId, Long ruleTemplateId, String ruleDescription, Long workgroupId, String workflowId, String roleName, Boolean delegateRule, Boolean activeInd, Map extensionValues, String workflowIdDirective) {
        return null;
    }
    public List search(String docTypeName, String ruleTemplateName, String ruleDescription, GroupId workgroupId, UserId userId, String roleName, Boolean workgroupMember, Boolean delegateRule, Boolean activeInd, Map extensionValues, Collection<String> actionRequestCodes) throws EdenUserNotFoundException {
        return null;
    }
    public void notifyCacheOfRuleChange(RuleBaseValues rule, DocumentType documentType) {
    }



    public void flushRuleCache() {
	// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

    }
    public RuleBaseValues getRuleByName(String name) {
        return rulesByName.get(name);
    }
    public void addRule(RuleBaseValues rule) {
        rules.put(rule.getRuleBaseValuesId(), rule);
        if (rule.getName() != null) {
            rulesByName.put(rule.getName(), rule);
        }

        List routeHeaderList = null;
        if(rulesByRouteHeaderId.get(rule.getRouteHeaderId()) != null){
            routeHeaderList = (List)rulesByRouteHeaderId.get(rule.getRouteHeaderId());
        } else {
            routeHeaderList = new ArrayList();
        }
        routeHeaderList.add(rule);
        rulesByRouteHeaderId.put(rule.getRouteHeaderId(), routeHeaderList);

        for (Iterator iter = rule.getResponsibilities().iterator(); iter.hasNext();) {
            RuleResponsibility resp = (RuleResponsibility) iter.next();
            responsibilitiesByKey.put(resp.getRuleResponsibilityKey(), resp);
            responsibilitiesById.put(resp.getResponsibilityId(), resp);

            List respList = null;
            if(responsibilitiesByReviewer.get(resp.getRuleResponsibilityName()) != null){
                respList = (List)responsibilitiesByReviewer.get(resp.getRuleResponsibilityName());
            } else {
                respList = new ArrayList();
            }
            respList.add(resp);
            responsibilitiesByReviewer.put(resp.getRuleResponsibilityName(), respList);
        }
    }

    public Long route2(MyRules2 myRules, WorkflowUser user, String annotation) throws Exception {
        throw new UnsupportedOperationException("not implemented in MockRuleServiceImpl");
    }

    public void delete(Long ruleBaseValuesId) {
        throw new UnsupportedOperationException("not implemented in MockRuleServiceImpl");
    }

    public RuleBaseValues findRuleBaseValuesByName(String name) {
        return null;
    }

    public RuleBaseValues findRuleBaseValuesById(Long ruleBaseValuesId) {
        return (RuleBaseValues) rules.get(ruleBaseValuesId);
    }

    public List search(String docTypeName, Long ruleTemplateId, Long workgroupId, String workflowId, String roleName, Boolean delegateRule, Boolean activeInd, Map extensionValues) {
        throw new UnsupportedOperationException("not implemented in MockRuleServiceImpl");
    }

    public RuleResponsibility findRuleResponsibility(Long responsibilityId) {
        return (RuleResponsibility) responsibilitiesById.get(responsibilityId);
    }

    public void deleteRuleResponsibilityById(Long ruleResponsibilityId) {
        throw new UnsupportedOperationException("not implemented in MockRuleServiceImpl");
    }

    public RuleResponsibility findByRuleResponsibilityId(Long ruleResponsibilityId) {
        return (RuleResponsibility) responsibilitiesByKey.get(ruleResponsibilityId);
    }

    public List fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateName, String documentType) {
        List ruleBaseValues = new ArrayList();

        for (Iterator iter = rules.values().iterator(); iter.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iter.next();
            if(rule.getRuleTemplate().getName().equals(ruleTemplateName) && rule.getDocTypeName().equals(documentType)){
                ruleBaseValues.add(rule);
            }
        }
        return ruleBaseValues;
    }

    public List findByRouteHeaderId(Long routeHeaderId) {
        return (List) rulesByRouteHeaderId.get(routeHeaderId);
    }

    public void makeCurrent(Long routeHeaderId) throws EdenUserNotFoundException {
        throw new UnsupportedOperationException("not implemented in MockRuleServiceImpl");
    }

    public List findRuleBaseValuesByResponsibilityReviewer(String reviewerName, String type) {
        List rules = new ArrayList();
        for (Iterator iter = ((List) responsibilitiesByReviewer.get(reviewerName)).iterator(); iter.hasNext();) {
            RuleResponsibility resp = (RuleResponsibility) iter.next();
            if(resp.getRuleResponsibilityType().equals(type)){
                rules.add(resp.getRuleBaseValues());
            }
        }
        return rules;
    }

    public List fetchAllRules(boolean currentRules) {
        return new ArrayList(rules.values());
    }

    public void saveDeactivationDate(RuleBaseValues rule) {
        // do anything?
    }

    public void validate2(RuleBaseValues ruleBaseValues, RuleDelegation ruleDelegation, List errors) throws Exception {
    	throw new UnsupportedOperationException("not implemented in MockRuleServiceImpl");
    }

    public List fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateName, String documentType, Timestamp effectiveDate) {
        return null;
    }
	public RuleBaseValues findDefaultRuleByRuleTemplateId(Long ruleTemplateId) {
		return null;
	}

    public void save2(RuleBaseValues ruleBaseValues) throws Exception {
        throw new UnsupportedOperationException("not implemented in MockRuleServiceImpl");
    }

    public void loadXml(InputStream inputStream, WorkflowUser user) {
        throw new UnsupportedOperationException("not implemented in MockRuleServiceImpl");
    }
    public Element export(ExportDataSet dataSet) {
        throw new UnsupportedOperationException("not implemented in MockRuleServiceImpl");
    }
	public void notifyCacheOfDocumentTypeChange(DocumentType documentType) {

	}
	public String getRuleDocmentTypeName(List rules) {
		return EdenConstants.DEFAULT_RULE_DOCUMENT_NAME;
	}
	public void removeRuleInvolvement(Id entityToBeRemoved, List<Long> ruleIds, Long documentId) {
	    throw new UnsupportedOperationException("not implemented in MockRuleServiceImpl");
	}
	public void replaceRuleInvolvement(Id entityToBeReplaced, Id newEntity, List<Long> ruleIds, Long documentId) {
	    throw new UnsupportedOperationException("not implemented in MockRuleServiceImpl");
	}
	/**
	 * This overridden method ...
	 *
	 * @see edu.iu.uis.eden.routetemplate.RuleService#findRuleBaseValuesByResponsibilityReviewerTemplateDoc(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public List findRuleBaseValuesByResponsibilityReviewerTemplateDoc(String ruleTemplateName, String documentType, String reviewerName, String type) {
	    throw new UnsupportedOperationException("not implemented in MockRuleServiceImpl");
	}


}
