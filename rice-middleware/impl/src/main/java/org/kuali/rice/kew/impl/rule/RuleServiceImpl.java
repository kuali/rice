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
package org.kuali.rice.kew.impl.rule;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.rule.Rule;
import org.kuali.rice.kew.api.rule.RuleDelegation;
import org.kuali.rice.kew.api.rule.RuleQueryResults;
import org.kuali.rice.kew.api.rule.RuleReportCriteria;
import org.kuali.rice.kew.api.rule.RuleResponsibility;
import org.kuali.rice.kew.api.rule.RuleService;
import org.kuali.rice.kew.api.rule.RuleTemplate;
import org.kuali.rice.kew.api.rule.RuleTemplateQueryResults;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegationBo;
import org.kuali.rice.kew.rule.RuleResponsibilityBo;
import org.kuali.rice.kew.rule.bo.RuleTemplateBo;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.impl.common.attribute.AttributeTransform;
import org.kuali.rice.krad.data.DataObjectService;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;


/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RuleServiceImpl implements RuleService {

    private static final Logger LOG = Logger.getLogger(RuleServiceImpl.class);

    private DataObjectService dataObjectService;

    @Override
    public Rule getRule(String id) throws RiceIllegalArgumentException, RiceIllegalStateException{
        incomingParamCheck("id", id);
        RuleBaseValues rbv = getDataObjectService().find(RuleBaseValues.class, id);
        if (rbv == null) {
            throw new RiceIllegalStateException("Rule with specified id: " + id + " does not exist");
        }
        return RuleBaseValues.to(rbv);
    }

    @Override
    public Rule getRuleByName(String name) {
        incomingParamCheck("name", name);
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(
                equal("name", name),
                equal("currentInd", Boolean.TRUE)
        );
        QueryResults<RuleBaseValues> results = getDataObjectService().findMatching(RuleBaseValues.class, builder.build());
        if (results.getResults().isEmpty()) {
            throw new RiceIllegalStateException("Rule with specified name: " + name + " does not exist");
        }
        if (results.getResults().size() > 1) {
            throw new RiceIllegalStateException("Found more than one current rule with specified name " + name);
        }
        return RuleBaseValues.to(results.getResults().get(0));
    }

    @Override
    public List<Rule> getRulesByTemplateId(
            @WebParam(name = "templateId") String templateId) throws RiceIllegalArgumentException {
        incomingParamCheck("templateId", templateId);
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(equal("ruleTemplateId", templateId), equal("currentInd", Boolean.TRUE));
        QueryResults<RuleBaseValues> results = getDataObjectService().findMatching(RuleBaseValues.class, builder.build());
        final List<Rule> rules = new ArrayList<Rule>();
        for (RuleBaseValues bo : results.getResults()) {
            rules.add(Rule.Builder.create(bo).build());
        }
        return rules;
    }

    @Override
    public List<Rule> getRulesByTemplateNameAndDocumentTypeName(String templateName, String documentTypeName) {
        return getRulesByTemplateNameAndDocumentTypeNameAndEffectiveDate(templateName, documentTypeName, null);
    }

    @Override
    public List<Rule> getRulesByTemplateNameAndDocumentTypeNameAndEffectiveDate(String templateName, String documentTypeName,
            DateTime effectiveDate)
            throws RiceIllegalArgumentException {
        QueryByCriteria.Builder query = QueryByCriteria.Builder.create();
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(equal("ruleTemplate.name", templateName));

        // Check all document types in ancestry
        DocumentTypeService documentTypeService = KewApiServiceLocator.getDocumentTypeService();
        org.kuali.rice.kew.api.doctype.DocumentType dt = documentTypeService.getDocumentTypeByName(documentTypeName);
        List<String> documentTypeAncestryNames = new ArrayList<String>();
        while (dt != null) {
            documentTypeAncestryNames.add(dt.getName());
            dt = dt.getParentId() == null ? null : documentTypeService.getDocumentTypeById(dt.getParentId());
        }
        predicates.add(in("docTypeName", documentTypeAncestryNames.toArray(
                new String[documentTypeAncestryNames.size()])));
        DateTime currentTime = new DateTime();
        predicates.add(and(
                           or(isNull("fromDateValue"), lessThanOrEqual("fromDateValue", currentTime)),
                           or(isNull("toDateValue"), greaterThan("toDateValue", currentTime))
                      ));
        predicates.add(equal("active", Boolean.TRUE));
        predicates.add(equal("delegateRule", Boolean.FALSE));
        predicates.add(equal("templateRuleInd", Boolean.FALSE));
        if (effectiveDate != null) {
            predicates.add(
                    and(
                        or(isNull("activationDate"), lessThanOrEqual("activationDate", effectiveDate)),
                        or(isNull("deactivationDate"), greaterThan("deactivationDate", effectiveDate))
                    ));
        } else {
            predicates.add(equal("currentInd", Boolean.TRUE));
        }
        Predicate p = and(predicates.toArray(new Predicate[]{}));
        query.setPredicates(p);
        return KewApiServiceLocator.getRuleService().findRules(query.build()).getResults();
    }

    @Override
    public RuleQueryResults findRules(QueryByCriteria queryByCriteria) {
        if (queryByCriteria == null) {
            throw new RiceIllegalArgumentException("queryByCriteria is null");
        }

        QueryResults<RuleBaseValues> results = dataObjectService.findMatching(RuleBaseValues.class,
                AttributeTransform.getInstance().apply(queryByCriteria));

        RuleQueryResults.Builder builder = RuleQueryResults.Builder.create();
        builder.setMoreResultsAvailable(results.isMoreResultsAvailable());
        builder.setTotalRowCount(results.getTotalRowCount());

        final List<Rule.Builder> ims = new ArrayList<Rule.Builder>();
        for (RuleBaseValues bo : results.getResults()) {
            ims.add(Rule.Builder.create(RuleBaseValues.to(bo)));
        }

        builder.setResults(ims);
        return builder.build();
    }

    @Override
    public List<Rule> ruleReport(RuleReportCriteria ruleReportCriteria) {
        incomingParamCheck(ruleReportCriteria, "ruleReportCriteria");
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Executing rule report [responsibleUser=" + ruleReportCriteria.getResponsiblePrincipalId() + ", responsibleWorkgroup=" +
                    ruleReportCriteria.getResponsibleGroupId() + "]");
        }
        Collection<RuleBaseValues> rulesFound = KEWServiceLocator.getRuleService().searchByTemplate(
                ruleReportCriteria.getDocumentTypeName(), ruleReportCriteria.getRuleTemplateName(),
                ruleReportCriteria.getRuleDescription(), ruleReportCriteria.getResponsibleGroupId(),
                ruleReportCriteria.getResponsiblePrincipalId(), Boolean.valueOf(ruleReportCriteria.isConsiderGroupMembership()),
                Boolean.valueOf(ruleReportCriteria.isIncludeDelegations()), Boolean.valueOf(ruleReportCriteria.isActive()), ruleReportCriteria.getRuleExtensions(),
                ruleReportCriteria.getActionRequestCodes());
        List<org.kuali.rice.kew.api.rule.Rule> returnableRules = new ArrayList<Rule>(rulesFound.size());
        for (RuleBaseValues rule : rulesFound) {
            returnableRules.add(RuleBaseValues.to(rule));
        }
        return returnableRules;
    }

    @Override
    public RuleTemplate getRuleTemplate(@WebParam(name = "id") String id) {
        incomingParamCheck("id", id);
        RuleTemplateBo template = dataObjectService.find(RuleTemplateBo.class, id);
        if (template == null) {
            throw new RiceIllegalStateException("RuleTemplate with specified id: " + id + " does not exist");
        }
        return RuleTemplateBo.to(template);
    }

    @Override
    public RuleTemplate getRuleTemplateByName(@WebParam(name = "name") String name) {
        incomingParamCheck("name", name);
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(equal("name", name));
        QueryResults<RuleTemplateBo> results = dataObjectService.findMatching(RuleTemplateBo.class, builder.build());
        if (results.getResults().isEmpty()) {
            throw new RiceIllegalStateException("Rule Template with specified name: " + name + " does not exist");
        }
        if (results.getResults().size() > 1) {
            throw new RiceIllegalStateException("Found more than one rule template with specified name " + name);
        }
        return RuleTemplateBo.to(results.getResults().get(0));
    }

    @Override
    public RuleTemplateQueryResults findRuleTemplates(
            @WebParam(name = "query") QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException {
        if (queryByCriteria == null) {
            throw new RiceIllegalArgumentException("queryByCriteria is null");
        }

        QueryResults<RuleTemplateBo> results = dataObjectService.findMatching(RuleTemplateBo.class,
                AttributeTransform.getInstance().apply(queryByCriteria));

        RuleTemplateQueryResults.Builder builder = RuleTemplateQueryResults.Builder.create();
        builder.setMoreResultsAvailable(results.isMoreResultsAvailable());
        builder.setTotalRowCount(results.getTotalRowCount());

        final List<RuleTemplate.Builder> ims = new ArrayList<RuleTemplate.Builder>();
        for (RuleTemplateBo bo : results.getResults()) {
            ims.add(RuleTemplate.Builder.create(RuleTemplateBo.to(bo)));
        }

        builder.setResults(ims);
        return builder.build();
    }

    @Override
    public RuleResponsibility getRuleResponsibility(String responsibilityId) {
        incomingParamCheck("responsibilityId", responsibilityId);
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(equal("responsibilityId", responsibilityId));
        QueryResults<RuleResponsibilityBo> results = dataObjectService.findMatching(RuleResponsibilityBo.class, builder.build());
        if (results.getResults().isEmpty()) {
            throw new RiceIllegalStateException("RuleResponsibility with specified id: " + responsibilityId + " does not exist");
        }
        if (results.getResults().size() > 1) {
            throw new RiceIllegalStateException("Found more than one rule responsibility with responsibility id: " + responsibilityId);
        }
        return RuleResponsibilityBo.to(results.getResults().get(0));
    }

    @Override
    public List<RuleDelegation> getRuleDelegationsByResponsibiltityId(
            @WebParam(name = "id") String id) throws RiceIllegalArgumentException, RiceIllegalStateException {
        incomingParamCheck("id", id);
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(
                equal("responsibilityId", id),
                equal("delegationRule.currentInd", Boolean.TRUE)
        );
        QueryResults<RuleDelegationBo> results = dataObjectService.findMatching(RuleDelegationBo.class, builder.build());
        List<RuleDelegation> ruleDelegations = new ArrayList<RuleDelegation>();
        for (RuleDelegationBo bo : results.getResults()) {
            ruleDelegations.add(RuleDelegationBo.to(bo));
        }
    	return ruleDelegations;
    }

    private void incomingParamCheck(Object object, String name) {
        if (object == null) {
            throw new RiceIllegalArgumentException(name + " was null");
        } else if (object instanceof String
                && StringUtils.isBlank((String) object)) {
            throw new RiceIllegalArgumentException(name + " was blank");
        }
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

}
