/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kew.rule.service.impl;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.rice.core.api.impex.xml.XmlConstants;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.core.util.CollectionUtils;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.api.action.ActionRequestPolicy;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.responsibility.service.ResponsibilityIdService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.RuleExtension;
import org.kuali.rice.kew.rule.RuleExtensionValue;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.RuleRoutingDefinition;
import org.kuali.rice.kew.rule.RuleValidationAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.rule.dao.RuleDAO;
import org.kuali.rice.kew.rule.dao.RuleResponsibilityDAO;
import org.kuali.rice.kew.rule.service.RuleCacheProcessor;
import org.kuali.rice.kew.rule.service.RuleDelegationCacheProcessor;
import org.kuali.rice.kew.rule.service.RuleDelegationService;
import org.kuali.rice.kew.rule.service.RuleService;
import org.kuali.rice.kew.rule.service.RuleTemplateService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.kew.validation.RuleValidationContext;
import org.kuali.rice.kew.validation.ValidationResults;
import org.kuali.rice.kew.xml.RuleXmlParser;
import org.kuali.rice.kew.xml.export.RuleXmlExporter;
import org.kuali.rice.kim.api.identity.principal.PrincipalContract;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;


public class RuleServiceImpl implements RuleService {

    private static final String USING_RULE_CACHE_IND = "CACHING_IND";
    private static final String XML_PARSE_ERROR = "general.error.parsexml";
    private static final String RULE_GROUP_CACHE = "org.kuali.workflow.rules.RuleCache";

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RuleServiceImpl.class);

    private RuleDAO ruleDAO;
    private RuleResponsibilityDAO ruleResponsibilityDAO;

    public RuleResponsibilityDAO getRuleResponsibilityDAO() {
        return ruleResponsibilityDAO;
    }

    public RuleBaseValues getRuleByName(String name) {
        return ruleDAO.findRuleBaseValuesByName(name);
    }

    public RuleBaseValues findDefaultRuleByRuleTemplateId(Long ruleTemplateId){
        return this.ruleDAO.findDefaultRuleByRuleTemplateId(ruleTemplateId);
    }
    public void setRuleResponsibilityDAO(RuleResponsibilityDAO ruleResponsibilityDAO) {
        this.ruleResponsibilityDAO = ruleResponsibilityDAO;
    }

    public void save2(RuleBaseValues ruleBaseValues) throws Exception {
        save2(ruleBaseValues, null, true);
    }

    public void save2(RuleBaseValues ruleBaseValues, RuleDelegation ruleDelegation, boolean saveDelegations) throws Exception {
        if (ruleBaseValues.getPreviousVersionId() != null) {
            RuleBaseValues oldRule = findRuleBaseValuesById(ruleBaseValues.getPreviousVersionId());
            ruleBaseValues.setPreviousVersion(oldRule);
            ruleBaseValues.setCurrentInd(Boolean.FALSE);
            ruleBaseValues.setVersionNbr(getNextVersionNumber(oldRule));
        }
        if (ruleBaseValues.getVersionNbr() == null) {
            ruleBaseValues.setVersionNbr(Integer.valueOf(0));
        }
        if (ruleBaseValues.getCurrentInd() == null) {
            ruleBaseValues.setCurrentInd(Boolean.FALSE);
        }
        // iterate through all associated responsibilities, and if they are unsaved (responsibilityId is null)
        // set a new id on them, and recursively save any associated delegation rules
        for (Object element : ruleBaseValues.getResponsibilities()) {
            RuleResponsibility responsibility = (RuleResponsibility) element;
            if (responsibility.getResponsibilityId() == null) {
                responsibility.setResponsibilityId(getResponsibilityIdService().getNewResponsibilityId());
            }
            if (saveDelegations) {
                for (Object element2 : responsibility.getDelegationRules()) {
                    RuleDelegation localRuleDelegation = (RuleDelegation) element2;
                    save2(localRuleDelegation.getDelegationRuleBaseValues(), localRuleDelegation, true);
                }
            }
        }
        validate2(ruleBaseValues, ruleDelegation, null);
        getRuleDAO().save(ruleBaseValues);
    }

    public void makeCurrent(String documentId) {
        makeCurrent(findByDocumentId(documentId));
    }

    public void makeCurrent(List rules) {
        PerformanceLogger performanceLogger = new PerformanceLogger();

        boolean isGenerateRuleArs = true;
        String generateRuleArs = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KEWConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.RULE_DETAIL_TYPE, KEWConstants.RULE_GENERATE_ACTION_REQESTS_IND);
        if (!StringUtils.isBlank(generateRuleArs)) {
            isGenerateRuleArs = KEWConstants.YES_RULE_CHANGE_AR_GENERATION_VALUE.equalsIgnoreCase(generateRuleArs);
        }
        Set responsibilityIds = new HashSet();
        HashMap rulesToSave = new HashMap();

        Collections.sort(rules, new RuleDelegationSorter());
        boolean delegateFirst = false;
        for (Iterator iter = rules.iterator(); iter.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iter.next();

            performanceLogger.log("Preparing rule: " + rule.getDescription());

            rule.setCurrentInd(Boolean.TRUE);
            Timestamp date = new Timestamp(System.currentTimeMillis());
            rule.setActivationDate(date);
            try {
                rule.setDeactivationDate(new Timestamp(RiceConstants.getDefaultDateFormat().parse("01/01/2100").getTime()));
            } catch (Exception e) {
                LOG.error("Parse Exception", e);
            }
            rulesToSave.put(rule.getRuleBaseValuesId(), rule);
            RuleBaseValues oldRule = rule.getPreviousVersion();
            if (oldRule != null) {
                performanceLogger.log("Setting previous rule: " + oldRule.getRuleBaseValuesId() + " to non current.");

                oldRule.setCurrentInd(Boolean.FALSE);
                oldRule.setDeactivationDate(date);
                rulesToSave.put(oldRule.getRuleBaseValuesId(), oldRule);
                if (!delegateFirst) {
                    responsibilityIds.addAll(getResponsibilityIdsFromGraph(oldRule, isGenerateRuleArs));
                }
                //TODO if more than one delegate is edited from the create delegation screen (which currently can not happen), then this logic will not work.
                if (rule.getDelegateRule().booleanValue() && rule.getPreviousVersionId() != null) {
                    delegateFirst = true;
                }

                List oldDelegationRules = findOldDelegationRules(oldRule, rule, performanceLogger);
                for (Iterator iterator = oldDelegationRules.iterator(); iterator.hasNext();) {
                    RuleBaseValues delegationRule = (RuleBaseValues) iterator.next();

                    performanceLogger.log("Setting previous delegation rule: " + delegationRule.getRuleBaseValuesId() + "to non current.");

                    delegationRule.setCurrentInd(Boolean.FALSE);
                    rulesToSave.put(delegationRule.getRuleBaseValuesId(), delegationRule);
                    responsibilityIds.addAll(getResponsibilityIdsFromGraph(delegationRule, isGenerateRuleArs));
                }
            }
            for (Object element : rule.getResponsibilities()) {
                RuleResponsibility responsibility = (RuleResponsibility) element;
                for (Object element2 : responsibility.getDelegationRules()) {
                    RuleDelegation delegation = (RuleDelegation) element2;

                    delegation.getDelegationRuleBaseValues().setCurrentInd(Boolean.TRUE);
                    RuleBaseValues delegatorRule = delegation.getDelegationRuleBaseValues();

                    performanceLogger.log("Setting delegate rule: " + delegatorRule.getDescription() + " to current.");
                    if (delegatorRule.getActivationDate() == null) {
                        delegatorRule.setActivationDate(date);
                    }
                    try {
                        delegatorRule.setDeactivationDate(new Timestamp(RiceConstants.getDefaultDateFormat().parse("01/01/2100").getTime()));
                    } catch (Exception e) {
                        LOG.error("Parse Exception", e);
                    }
                    rulesToSave.put(delegatorRule.getRuleBaseValuesId(), delegatorRule);
                }
            }
        }
        Map<String, Long> notifyMap = new HashMap<String, Long>();
        for (Iterator iterator = rulesToSave.values().iterator(); iterator.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iterator.next();
            getRuleDAO().save(rule);
            performanceLogger.log("Saved rule: " + rule.getRuleBaseValuesId());
            installNotification(rule, notifyMap);
        }
        LOG.info("Notifying rule cache of "+notifyMap.size()+" cache changes.");
        for (Object element : notifyMap.values()) {
            queueRuleCache((Long)element);
        }

        getActionRequestService().updateActionRequestsForResponsibilityChange(responsibilityIds);
        performanceLogger.log("Time to make current");
    }

    /**
     * TODO consolidate this method with makeCurrent.  The reason there's a seperate implementation is because the
     * original makeCurrent(...) could not properly handle versioning a List of multiple rules (including multiple
     * delegates rules for a single parent.  ALso, this work is being done for a patch so we want to mitigate the
     * impact on the existing rule code.
     *
     * <p>This version will only work for remove/replace operations where rules
     * aren't being added or removed.  This is why it doesn't perform some of the functions like checking
     * for delegation rules that were removed from a parent rule.
     */
    public void makeCurrent2(List rules) {
        PerformanceLogger performanceLogger = new PerformanceLogger();

        boolean isGenerateRuleArs = true;
        String generateRuleArs = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KEWConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.RULE_DETAIL_TYPE, KEWConstants.RULE_GENERATE_ACTION_REQESTS_IND);
        if (!StringUtils.isBlank(generateRuleArs)) {
            isGenerateRuleArs = KEWConstants.YES_RULE_CHANGE_AR_GENERATION_VALUE.equalsIgnoreCase(generateRuleArs);
        }
        Set<Long> responsibilityIds = new HashSet<Long>();
        Map<Long, RuleBaseValues> rulesToSave = new HashMap<Long, RuleBaseValues>();

        Collections.sort(rules, new RuleDelegationSorter());
        for (Iterator iter = rules.iterator(); iter.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iter.next();

            performanceLogger.log("Preparing rule: " + rule.getDescription());

            rule.setCurrentInd(Boolean.TRUE);
            Timestamp date = new Timestamp(System.currentTimeMillis());
            rule.setActivationDate(date);
            try {
                rule.setDeactivationDate(new Timestamp(RiceConstants.getDefaultDateFormat().parse("01/01/2100").getTime()));
            } catch (Exception e) {
                LOG.error("Parse Exception", e);
            }
            rulesToSave.put(rule.getRuleBaseValuesId(), rule);
            RuleBaseValues oldRule = rule.getPreviousVersion();
            if (oldRule != null) {
                performanceLogger.log("Setting previous rule: " + oldRule.getRuleBaseValuesId() + " to non current.");
                oldRule.setCurrentInd(Boolean.FALSE);
                oldRule.setDeactivationDate(date);
                rulesToSave.put(oldRule.getRuleBaseValuesId(), oldRule);
                responsibilityIds.addAll(getModifiedResponsibilityIds(oldRule, rule));
            }
            for (Object element : rule.getResponsibilities()) {
                RuleResponsibility responsibility = (RuleResponsibility) element;
                for (Object element2 : responsibility.getDelegationRules()) {
                    RuleDelegation delegation = (RuleDelegation) element2;
                    RuleBaseValues delegateRule = delegation.getDelegationRuleBaseValues();
                    delegateRule.setCurrentInd(Boolean.TRUE);
                    performanceLogger.log("Setting delegate rule: " + delegateRule.getDescription() + " to current.");
                    if (delegateRule.getActivationDate() == null) {
                        delegateRule.setActivationDate(date);
                    }
                    try {
                        delegateRule.setDeactivationDate(new Timestamp(RiceConstants.getDefaultDateFormat().parse("01/01/2100").getTime()));
                    } catch (Exception e) {
                        LOG.error("Parse Exception", e);
                    }
                    rulesToSave.put(delegateRule.getRuleBaseValuesId(), delegateRule);
                }
            }
        }
        Map<String, Long> notifyMap = new HashMap<String, Long>();
        for (RuleBaseValues rule : rulesToSave.values()) {
            getRuleDAO().save(rule);
            performanceLogger.log("Saved rule: " + rule.getRuleBaseValuesId());
            installNotification(rule, notifyMap);
        }
        LOG.info("Notifying rule cache of "+notifyMap.size()+" cache changes.");
        for (Object element : notifyMap.values()) {
            queueRuleCache((Long)element);
        }
        if (isGenerateRuleArs) {
            getActionRequestService().updateActionRequestsForResponsibilityChange(responsibilityIds);
        }
        performanceLogger.log("Time to make current");
    }

    /**
     * makeCurrent(RuleBaseValues) is the version of makeCurrent which is initiated from the new Routing Rule
     * Maintenance document.  Because of the changes in the data model and the front end here,
     * this method can be much less complicated than the previous 2!
     */
    public void makeCurrent(RuleBaseValues rule, boolean isRetroactiveUpdatePermitted) {
    	makeCurrent(null, rule, isRetroactiveUpdatePermitted);
    }

    public void makeCurrent(RuleDelegation ruleDelegation, boolean isRetroactiveUpdatePermitted) {
    	makeCurrent(ruleDelegation, ruleDelegation.getDelegationRuleBaseValues(), isRetroactiveUpdatePermitted);
    }

    protected void makeCurrent(RuleDelegation ruleDelegation, RuleBaseValues rule, boolean isRetroactiveUpdatePermitted) {
        PerformanceLogger performanceLogger = new PerformanceLogger();

        boolean isGenerateRuleArs = false;
        if (isRetroactiveUpdatePermitted) {
        	isGenerateRuleArs = true;
        	String generateRuleArs = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KEWConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.RULE_DETAIL_TYPE, KEWConstants.RULE_GENERATE_ACTION_REQESTS_IND);
        	if (!StringUtils.isBlank(generateRuleArs)) {
        		isGenerateRuleArs = KEWConstants.YES_RULE_CHANGE_AR_GENERATION_VALUE.equalsIgnoreCase(generateRuleArs);
        	}
        }
        Set<Long> responsibilityIds = new HashSet<Long>();


        performanceLogger.log("Preparing rule: " + rule.getDescription());

        Map<Long, RuleBaseValues> rulesToSave = new HashMap<Long, RuleBaseValues>();
        generateRuleNameIfNeeded(rule);
        assignResponsibilityIds(rule);
        rule.setCurrentInd(Boolean.TRUE);
        Timestamp date = new Timestamp(System.currentTimeMillis());
        rule.setActivationDate(date);
        rule.setDeactivationDate(null);

        rulesToSave.put(rule.getRuleBaseValuesId(), rule);
        if (rule.getPreviousVersionId() != null) {
        	RuleBaseValues oldRule = findRuleBaseValuesById(rule.getPreviousVersionId());
        	rule.setPreviousVersion(oldRule);
        }
        rule.setVersionNbr(0);
        RuleBaseValues oldRule = rule.getPreviousVersion();
        if (oldRule != null) {
        	performanceLogger.log("Setting previous rule: " + oldRule.getRuleBaseValuesId() + " to non current.");
        	oldRule.setCurrentInd(Boolean.FALSE);
        	oldRule.setDeactivationDate(date);
        	rulesToSave.put(oldRule.getRuleBaseValuesId(), oldRule);
        	responsibilityIds.addAll(getModifiedResponsibilityIds(oldRule, rule));
        	rule.setVersionNbr(getNextVersionNumber(oldRule));
        }
               

        boolean isRuleDelegation = ruleDelegation != null;
        
        Map<String, Long> notifyMap = new HashMap<String, Long>(); 
        
        for (RuleBaseValues ruleToSave : rulesToSave.values()) {        	
        	getRuleDAO().save(ruleToSave);
        	performanceLogger.log("Saved rule: " + ruleToSave.getRuleBaseValuesId());
            if (!isRuleDelegation) {
                installNotification(ruleToSave, notifyMap);
            } 
        }
        if (isRuleDelegation) {
        	responsibilityIds.add(ruleDelegation.getResponsibilityId());
        	ruleDelegation.setDelegateRuleId(rule.getRuleBaseValuesId());
        	getRuleDelegationService().save(ruleDelegation);
        	installDelegationNotification(ruleDelegation.getResponsibilityId(), notifyMap);
        }
        LOG.info("Notifying rule cache of "+notifyMap.size()+" cache changes.");
        for (Iterator iterator = notifyMap.values().iterator(); iterator.hasNext();) {
            if (isRuleDelegation) {
                queueDelegationRuleCache((Long)iterator.next());
            } else {
                queueRuleCache((Long)iterator.next());
            } 
        }
        

        
        if (isGenerateRuleArs) {
            getActionRequestService().updateActionRequestsForResponsibilityChange(responsibilityIds);
        }
        performanceLogger.log("Time to make current");
    }


    private void queueRuleCache(Long ruleId){
//      PersistedMessageBO ruleCache = new PersistedMessageBO();
//      ruleCache.setQueuePriority(KEWConstants.ROUTE_QUEUE_RULE_CACHE_PRIORITY);
//      ruleCache.setQueueDate(new Timestamp(new Date().getTime()));
//      ruleCache.setQueueStatus(KEWConstants.ROUTE_QUEUE_QUEUED);
//      ruleCache.setRetryCount(new Integer(0));
//      ruleCache.setPayload("" + ruleId);
//      ruleCache.setProcessorClassName("org.kuali.rice.ksb.cache.RuleCacheProcessor");
//      getRouteQueueService().requeueDocument(ruleCache);


        RuleCacheProcessor ruleCacheProcessor = MessageServiceNames.getRuleCacheProcessor();
        ruleCacheProcessor.clearRuleFromCache(ruleId);

    }

//  private RouteQueueService getRouteQueueService() {
//  return (RouteQueueService)SpringServiceLocator.getService(SpringServiceLocator.ROUTE_QUEUE_SRV);
//  }

    /**
     * Ensure that we don't have any notification duplication.
     */
    private void installNotification(RuleBaseValues rule, Map<String, Long> notifyMap) {
    	// don't notify the cache if it's a "template" rule!
    	if (!rule.getTemplateRuleInd()) {
    		String key = getRuleCacheKey(rule.getRuleTemplateName(), rule.getDocTypeName());
    		if (!notifyMap.containsKey(key)) {
    			notifyMap.put(key, rule.getRuleBaseValuesId());
    		}
    	}
    }

    private void queueDelegationRuleCache(Long responsibilityId) {
        RuleDelegationCacheProcessor ruleDelegationCacheProcessor = (RuleDelegationCacheProcessor) KsbApiServiceLocator.getMessageHelper().getServiceAsynchronously(new QName("RuleDelegationCacheProcessorService"), null, null, null, null);
        ruleDelegationCacheProcessor.clearRuleDelegationFromCache(responsibilityId);    	
    }
    
    private void installDelegationNotification(Long responsibilityId, Map<String, Long> notifyMap) {
    	// don't notify the cache if it's a "template" rule!
    	String key = getRuleDlgnCacheKey(responsibilityId);
    	if (!notifyMap.containsKey(key)) {
    		notifyMap.put(key, responsibilityId);
    	}
    }

	protected String getRuleDlgnCacheKey(Long responsibilityId) {
		return "RuleDlgnCache:" + responsibilityId;
	}
	
    public RuleBaseValues getParentRule(Long ruleBaseValuesId) {
        return getRuleDAO().getParentRule(ruleBaseValuesId);
    }

    public void notifyCacheOfDocumentTypeChange(DocumentType documentType) {
        DocumentType rootDocumentType = KEWServiceLocator.getDocumentTypeService().findRootDocumentType(documentType);
        notifyCacheOfDocumentTypeChangeFromRoot(rootDocumentType, documentType);
        notifyCacheOfDocumentTypeChangeFromParent(documentType);
    }

    /**
     * Flushes rules cached for the given DocumentType and then recursivley flushes rules cached
     * for all children DocumentTypes.
     */
    protected void notifyCacheOfDocumentTypeChangeFromParent(DocumentType documentType) {
        flushDocumentTypeFromCache(documentType.getName());
        for (Iterator iter = documentType.getChildrenDocTypes().iterator(); iter.hasNext();) {
            notifyCacheOfDocumentTypeChangeFromParent((DocumentType) iter.next());
        }
    }

    /**
     * Flushes rules cached from the root of the DocumentType hierarchy (at the given root DocumentType).  Stops
     * when it hits the given DocumentType.  This method exists because of the nature of
     * DocumentTypeService.findRootDocumentType(...).  Whenever we have a modification to child document type
     * and we call findRootDocumentType(...) on it, we end up getting back a version of the root document type
     * which is cached in the OJB transaction cache and doesn't have the appropriate child document type attached.
     * A better way to handle this would be to go into the DocumentType service and fix how it versions document
     * types in versionAndSave to prevent this issue from occurring.
     *
     * <p>If such a fix was made then we could simply pass the root DocumentType into notifyCacheOfDocumentTypeChange
     * and be gauranteed that we will see all appropriate children as we call getChildrenDocTypes().
     *
     * <p>One last note, we don't necesarily have to stop this cache flusing at the given DocumentType but there's
     * no reason to duplicate the work that is going to be done in notifyCacheOfDocumentTypeChangeFromParent.
     */
    protected void notifyCacheOfDocumentTypeChangeFromRoot(DocumentType rootDocumentType, DocumentType documentType) {
        if (rootDocumentType.getName().equals(documentType.getName())) {
            return;
        }
        flushDocumentTypeFromCache(rootDocumentType.getName());
        for (Iterator iter = rootDocumentType.getChildrenDocTypes().iterator(); iter.hasNext();) {
            notifyCacheOfDocumentTypeChangeFromRoot((DocumentType) iter.next(), documentType);
        }
    }

    public void notifyCacheOfRuleChange(RuleBaseValues rule, DocumentType documentType) {
        Boolean cachingRules = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(KEWConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.RULE_DETAIL_TYPE, USING_RULE_CACHE_IND);

        LOG.info("Entering notifyCacheOfRuleChange.  CachingRules: " + cachingRules + " ; ruleID: " + rule.getRuleBaseValuesId());
        if (!cachingRules.booleanValue()) {
            return;
        }
        // named rules may be templateless, and therefore cannot be mapped into the cache by templatename/doctype key
        // but we still need to make sure that if there was a previous version with a template, that the cache is blown away
        String ruleTemplateName = null;
        if (rule.getRuleTemplate() == null) {
            if (rule.getPreviousVersion() != null) {
                RuleBaseValues prev = rule.getPreviousVersion();
                if (prev.getRuleTemplate() != null) {
                    ruleTemplateName = prev.getRuleTemplate().getName();
                }
            }
        } else {
            ruleTemplateName = rule.getRuleTemplate().getName();
        }

        if (documentType == null) {
            documentType = getDocumentTypeService().findByName(rule.getDocTypeName());
            // if it's a delegate rule, we need to look at the parent's template
            if (Boolean.TRUE.equals(rule.getDelegateRule())) {
                List delegations = getRuleDelegationService().findByDelegateRuleId(rule.getRuleBaseValuesId());
                for (Iterator iterator = delegations.iterator(); iterator.hasNext();) {
                    RuleDelegation ruleDelegation = (RuleDelegation) iterator.next();
                    RuleBaseValues parentRule = ruleDelegation.getRuleResponsibility().getRuleBaseValues();
                    if (Boolean.TRUE.equals(parentRule.getCurrentInd())) {
                        ruleTemplateName = parentRule.getRuleTemplate().getName();
                        break;
                    }
                }
            }
        }
        flushListFromCache(ruleTemplateName, documentType.getName());

//      if (getListFromCache(ruleTemplateName, documentType.getName()) != null) {
//      eventGenerator.notify(new CacheDataModifiedEvent(RULE_CACHE_NAME, getRuleCacheKey(ruleTemplateName, documentType.getName())));
//      }
        //}
        //walk the down the document hierarchy when refreshing the cache. The
        // rule could be cached through more than one path
        //HREDOC could be cached under HREDOC.child and HREDOC.child1
        for (Iterator iter = documentType.getChildrenDocTypes().iterator(); iter.hasNext();) {
            DocumentType childDocumentType = (DocumentType) iter.next();
            //SpringServiceLocator.getCacheAdministrator().flushEntry(getRuleCacheKey(ruleTemplateName, childDocumentType.getName()));
//          if (getListFromCache(rule.getRuleTemplate().getName(), childDocumentType.getName()) != null) {
//          eventGenerator.notify(new CacheDataModifiedEvent(RULE_CACHE_NAME, getRuleCacheKey(rule.getRuleTemplate().getName(), childDocumentType.getName())));
//          }
            notifyCacheOfRuleChange(rule, childDocumentType);
        }
        LOG.info("Leaving notifyCacheOfRuleChange.  CachingRules: " + cachingRules + " ; ruleID: " + rule.getRuleBaseValuesId() + " ; documentType: " + documentType.getDocumentTypeId());



    }

    /**
     * Returns the key of the rule cache.
     */
    protected String getRuleCacheKey(String ruleTemplateName, String docTypeName) {
        return "RuleCache:" + ruleTemplateName + "_" + docTypeName;
    }

    /*
     * Return the cache group name for the given DocumentType
     */
    protected String getDocumentTypeRuleCacheGroupName(String documentTypeName) {
        return "DocumentTypeRuleCache:"+documentTypeName;
    }

    protected List<RuleBaseValues> getListFromCache(String ruleTemplateName, String documentTypeName) {
        LOG.debug("Retrieving List of Rules from cache for ruleTemplate='" + ruleTemplateName + "' and documentType='" + documentTypeName + "'");
        return (List) KsbApiServiceLocator.getCacheAdministrator().getFromCache(getRuleCacheKey(ruleTemplateName, documentTypeName));
        //return (List) SpringServiceLocator.getCache().getCachedObjectById(RULE_CACHE_NAME, getRuleCacheKey(ruleTemplateName, documentTypeName));
    }

    protected void putListInCache(String ruleTemplateName, String documentTypeName, List<RuleBaseValues> rules) {
        assert(ruleTemplateName != null) : "putListInCache was called with a null ruleTemplateName";
        LOG.info("Caching " + rules.size() + " rules for ruleTemplate='" + ruleTemplateName + "' and documentType='" + documentTypeName + "'");
        String groups[] = new String[] { getDocumentTypeRuleCacheGroupName(documentTypeName), RULE_GROUP_CACHE };
        KsbApiServiceLocator.getCacheAdministrator().putInCache(getRuleCacheKey(ruleTemplateName, documentTypeName), rules, groups);
    }

    protected void flushDocumentTypeFromCache(String documentTypeName) {
        LOG.info("Flushing DocumentType from Cache for the given name: " + documentTypeName);
        KsbApiServiceLocator.getCacheAdministrator().flushGroup(getDocumentTypeRuleCacheGroupName(documentTypeName));
    }

    protected void flushListFromCache(String ruleTemplateName, String documentTypeName) {
        LOG.info("Flushing rules from Cache for ruleTemplate='" + ruleTemplateName + "' and documentType='" + documentTypeName + "'");
        KsbApiServiceLocator.getCacheAdministrator().flushEntry(getRuleCacheKey(ruleTemplateName, documentTypeName));
    }

    public void flushRuleCache() {
        LOG.info("Flushing entire Rule Cache.");
        KsbApiServiceLocator.getCacheAdministrator().flushGroup(RULE_GROUP_CACHE);
    }

    private Set getResponsibilityIdsFromGraph(RuleBaseValues rule, boolean isRuleCollecting) {
        Set responsibilityIds = new HashSet();
        for (Object element : rule.getResponsibilities()) {
            RuleResponsibility responsibility = (RuleResponsibility) element;
            if (isRuleCollecting) {
                responsibilityIds.add(responsibility.getResponsibilityId());
            }
        }
        return responsibilityIds;
    }

    /**
     * Returns the responsibility IDs that were modified between the 2 given versions of the rule.  Any added
     * or removed responsibilities are also included in the returned Set.
     */
    private Set<Long> getModifiedResponsibilityIds(RuleBaseValues oldRule, RuleBaseValues newRule) {
        Map<Long, RuleResponsibility> modifiedResponsibilityMap = new HashMap<Long, RuleResponsibility>();
        for (Object element : oldRule.getResponsibilities()) {
            RuleResponsibility responsibility = (RuleResponsibility) element;
            modifiedResponsibilityMap.put(responsibility.getResponsibilityId(), responsibility);
        }
        for (Object element : newRule.getResponsibilities()) {
            RuleResponsibility responsibility = (RuleResponsibility) element;
            RuleResponsibility oldResponsibility = modifiedResponsibilityMap.get(responsibility.getResponsibilityId());
            if (oldResponsibility == null) {
                // if there's no old responsibility then it's a new responsibility, add it
                modifiedResponsibilityMap.put(responsibility.getResponsibilityId(), responsibility);
            } else if (!hasResponsibilityChanged(oldResponsibility, responsibility)) {
                // if it hasn't been modified, remove it from the collection of modified ids
                modifiedResponsibilityMap.remove(responsibility.getResponsibilityId());
            }
        }
        return modifiedResponsibilityMap.keySet();
    }

    /**
     * Determines if the given responsibilities are different or not.
     */
    private boolean hasResponsibilityChanged(RuleResponsibility oldResponsibility, RuleResponsibility newResponsibility) {
        return !ObjectUtils.equals(oldResponsibility.getActionRequestedCd(), newResponsibility.getActionRequestedCd()) ||
        !ObjectUtils.equals(oldResponsibility.getApprovePolicy(), newResponsibility.getActionRequestedCd()) ||
        !ObjectUtils.equals(oldResponsibility.getPriority(), newResponsibility.getPriority()) ||
        !ObjectUtils.equals(oldResponsibility.getRole(), newResponsibility.getRole()) ||
        !ObjectUtils.equals(oldResponsibility.getRuleResponsibilityName(), newResponsibility.getRuleResponsibilityName()) ||
        !ObjectUtils.equals(oldResponsibility.getRuleResponsibilityType(), newResponsibility.getRuleResponsibilityType());
    }

    /*
     private Map findOldDelegationRules(RuleBaseValues oldRule, RuleBaseValues newRule, PerformanceLogger performanceLogger) {
        Map oldRules = new HashMap();
        performanceLogger.log("Begin to get delegation rules.");
        for (Iterator iterator = oldRule.getResponsibilities().iterator(); iterator.hasNext();) {
            RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
            for (Iterator delIterator = responsibility.getDelegationRules().iterator(); delIterator.hasNext();) {
                RuleDelegation ruleDelegation = (RuleDelegation) delIterator.next();
                RuleBaseValues delegateRule = ruleDelegation.getDelegationRuleBaseValues();
                performanceLogger.log("Found delegate rule: "+ delegateRule.getRuleBaseValuesId());
                oldRules.put(ruleDelegation.getDelegateRuleId(), delegateRule);
            }
        }
        performanceLogger.log("Begin removing rule delegations from new rule.");
        for (Iterator iterator = newRule.getResponsibilities().iterator(); iterator.hasNext();) {
            RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
            for (Iterator delIterator = responsibility.getDelegationRules().iterator(); delIterator.hasNext();) {
                RuleDelegation ruleDelegation = (RuleDelegation) delIterator.next();
                performanceLogger.log("Removing rule delegation: "+ ruleDelegation.getDelegateRuleId()+" from new rule.");
                oldRules.remove(ruleDelegation.getDelegateRuleId());
            }
        }
        return oldRules;
    }
     */
    /**
     * This method will find any old delegation rules on the previous version of the parent rule which are not on the
     * new version of the rule so that they can be marked non-current.
     */
    private List findOldDelegationRules(RuleBaseValues oldRule, RuleBaseValues newRule, PerformanceLogger performanceLogger) {
        performanceLogger.log("Begin to get delegation rules.");
        List oldDelegations = getRuleDAO().findOldDelegations(oldRule, newRule);
        performanceLogger.log("Located "+oldDelegations.size()+" old delegation rules.");
        return oldDelegations;
    }

    public String routeRuleWithDelegate(String documentId, RuleBaseValues parentRule, RuleBaseValues delegateRule, PrincipalContract principal, String annotation, boolean blanketApprove) throws Exception {
        if (parentRule == null) {
            throw new IllegalArgumentException("Cannot route a delegate without a parent rule.");
        }
        if (parentRule.getDelegateRule().booleanValue()) {
            throw new IllegalArgumentException("Parent rule cannot be a delegate.");
        }
        if (parentRule.getPreviousVersionId() == null && delegateRule.getPreviousVersionId() == null) {
            throw new IllegalArgumentException("Previous rule version required.");
        }

        // if the parent rule is new, unsaved, then save it
//      boolean isRoutingParent = parentRule.getRuleBaseValuesId() == null;
//      if (isRoutingParent) {
//      // it's very important that we do not save delegations here (that's what the false parameter is for)
//      // this is because, if we save the delegations, the existing delegations on our parent rule will become
//      // saved as "non current" before the rule is approved!!!
//      save2(parentRule, null, false);
//      //save2(parentRule, null, true);
//      }

        // XXX: added when the RuleValidation stuff was added, basically we just need to get the RuleDelegation
        // that points to our delegate rule, this rule code is scary...
        RuleDelegation ruleDelegation = getRuleDelegation(parentRule, delegateRule);

        save2(delegateRule, ruleDelegation, true);

//      if the parent rule is new, unsaved, then save it
        // It's important to save the parent rule after the delegate rule is saved, that way we can ensure that any new rule
        // delegations have a valid, saved, delegation rule to point to (otherwise we end up with a null constraint violation)
        boolean isRoutingParent = parentRule.getRuleBaseValuesId() == null;
        if (isRoutingParent) {
            // it's very important that we do not save delegations here (that's what the false parameter is for)
            // this is because, if we save the delegations, the existing delegations on our parent rule will become
            // saved as "non current" before the rule is approved!!!
            save2(parentRule, null, false);
            //save2(parentRule, null, true);
        }

        WorkflowDocument workflowDocument = null;
        if (documentId != null) {
            workflowDocument = WorkflowDocument.loadDocument(principal.getPrincipalId(), documentId);
        } else {
            List rules = new ArrayList();
            rules.add(delegateRule);
            rules.add(parentRule);
            workflowDocument = WorkflowDocument.createDocument(principal.getPrincipalId(), getRuleDocmentTypeName(rules));
        }
        workflowDocument.setTitle(generateTitle(parentRule, delegateRule));
        delegateRule.setDocumentId(workflowDocument.getDocumentId());
        workflowDocument.addAttributeDefinition(new RuleRoutingDefinition(parentRule.getDocTypeName()));
        getRuleDAO().save(delegateRule);
        if (isRoutingParent) {
            parentRule.setDocumentId(workflowDocument.getDocumentId());
            getRuleDAO().save(parentRule);
        }
        if (blanketApprove) {
            workflowDocument.blanketApprove(annotation);
        } else {
            workflowDocument.routeDocument(annotation);
        }
        return workflowDocument.getDocumentId();
    }

    /**
     * Gets the RuleDelegation object from the parentRule that points to the delegateRule.
     */
    private RuleDelegation getRuleDelegation(RuleBaseValues parentRule, RuleBaseValues delegateRule) throws Exception {
        for (Object element : parentRule.getResponsibilities()) {
            RuleResponsibility responsibility = (RuleResponsibility) element;
            for (Object element2 : responsibility.getDelegationRules()) {
                RuleDelegation ruleDelegation = (RuleDelegation) element2;
                // they should be the same object in memory
                if (ruleDelegation.getDelegationRuleBaseValues().equals(delegateRule)) {
                    return ruleDelegation;
                }
            }
        }
        return null;
    }

    private String generateTitle(RuleBaseValues parentRule, RuleBaseValues delegateRule) {
        StringBuffer title = new StringBuffer();
        if (delegateRule.getPreviousVersionId() != null) {
            title.append("Editing Delegation Rule '").append(delegateRule.getDescription()).append("' on '");
        } else {
            title.append("Adding Delegation Rule '").append(delegateRule.getDescription()).append("' to '");
        }
        title.append(parentRule.getDescription()).append("'");
        return title.toString();
    }

    public void validate(RuleBaseValues ruleBaseValues, List errors) {
        if (errors == null) {
            errors = new ArrayList();
        }
        if (getDocumentTypeService().findByName(ruleBaseValues.getDocTypeName()) == null) {
            errors.add(new WorkflowServiceErrorImpl("Document Type Invalid", "doctype.documenttypeservice.doctypename.required"));
        }
        if (ruleBaseValues.getToDate().before(ruleBaseValues.getFromDate())) {
            errors.add(new WorkflowServiceErrorImpl("From Date is later than to date", "routetemplate.ruleservice.daterange.fromafterto"));
        }
        if (ruleBaseValues.getActiveInd() == null) {
            errors.add(new WorkflowServiceErrorImpl("Active Indicator is required", "routetemplate.ruleservice.activeind.required"));
        }
        if (ruleBaseValues.getDescription() == null || ruleBaseValues.getDescription().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("Description is required", "routetemplate.ruleservice.description.required"));
        }
        if (ruleBaseValues.getForceAction() == null) {
            errors.add(new WorkflowServiceErrorImpl("Force Action is required", "routetemplate.ruleservice.forceAction.required"));
        }
        if (ruleBaseValues.getResponsibilities().isEmpty()) {
            errors.add(new WorkflowServiceErrorImpl("A responsibility is required", "routetemplate.ruleservice.responsibility.required"));
        } else {
            for (Object element : ruleBaseValues.getResponsibilities()) {
                RuleResponsibility responsibility = (RuleResponsibility) element;
                if (responsibility.getRuleResponsibilityName() != null && KEWConstants.RULE_RESPONSIBILITY_GROUP_ID.equals(responsibility.getRuleResponsibilityType())) {
                    if (getIdentityManagementService().getGroup(responsibility.getRuleResponsibilityName()) == null) {
                        errors.add(new WorkflowServiceErrorImpl("Workgroup is invalid", "routetemplate.ruleservice.workgroup.invalid"));
                    }
                } else if (responsibility.getPrincipal() == null && responsibility.getRole() == null) {
                    errors.add(new WorkflowServiceErrorImpl("User is invalid", "routetemplate.ruleservice.user.invalid"));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("RuleBaseValues validation errors", errors);
        }
    }

    public void validate2(RuleBaseValues ruleBaseValues, RuleDelegation ruleDelegation, List errors) {
        if (errors == null) {
            errors = new ArrayList();
        }
        if (getDocumentTypeService().findByName(ruleBaseValues.getDocTypeName()) == null) {
            errors.add(new WorkflowServiceErrorImpl("Document Type Invalid", "doctype.documenttypeservice.doctypename.required"));
            LOG.error("Document Type Invalid");
        }
        if (ruleBaseValues.getToDate() == null) {
            try {
                ruleBaseValues.setToDate(new Timestamp(RiceConstants.getDefaultDateFormat().parse("01/01/2100").getTime()));
            } catch (ParseException e) {
                LOG.error("Error date-parsing default date");
                throw new WorkflowServiceErrorException("Error parsing default date.", e);
            }
        }
        if (ruleBaseValues.getFromDate() == null) {
            ruleBaseValues.setFromDate(new Timestamp(System.currentTimeMillis()));
        }
        if (ruleBaseValues.getToDate().before(ruleBaseValues.getFromDate())) {
            errors.add(new WorkflowServiceErrorImpl("From Date is later than to date", "routetemplate.ruleservice.daterange.fromafterto"));
            LOG.error("From Date is later than to date");
        }
        if (ruleBaseValues.getActiveInd() == null) {
            errors.add(new WorkflowServiceErrorImpl("Active Indicator is required", "routetemplate.ruleservice.activeind.required"));
            LOG.error("Active Indicator is missing");
        }
        if (ruleBaseValues.getDescription() == null || ruleBaseValues.getDescription().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("Description is required", "routetemplate.ruleservice.description.required"));
            LOG.error("Description is missing");
        }
        if (ruleBaseValues.getForceAction() == null) {
            errors.add(new WorkflowServiceErrorImpl("Force Action is required", "routetemplate.ruleservice.forceAction.required"));
            LOG.error("Force Action is missing");
        }

        for (Object element : ruleBaseValues.getResponsibilities()) {
            RuleResponsibility responsibility = (RuleResponsibility) element;
            if (responsibility.getRuleResponsibilityName() != null && KEWConstants.RULE_RESPONSIBILITY_GROUP_ID.equals(responsibility.getRuleResponsibilityType())) {
                if (getIdentityManagementService().getGroup(responsibility.getRuleResponsibilityName()) == null) {
                    errors.add(new WorkflowServiceErrorImpl("Workgroup is invalid", "routetemplate.ruleservice.workgroup.invalid"));
                    LOG.error("Workgroup is invalid");
                }
            } else if (responsibility.getPrincipal() == null && responsibility.getRole() == null) {
                errors.add(new WorkflowServiceErrorImpl("User is invalid", "routetemplate.ruleservice.user.invalid"));
                LOG.error("User is invalid");
            } else if (responsibility.isUsingRole()) {
                if (responsibility.getApprovePolicy() == null || !(responsibility.getApprovePolicy().equals(ActionRequestPolicy.ALL.getCode()) || responsibility.getApprovePolicy().equals(ActionRequestPolicy.FIRST.getCode()))) {
                    errors.add(new WorkflowServiceErrorImpl("Approve Policy is Invalid", "routetemplate.ruleservice.approve.policy.invalid"));
                    LOG.error("Approve Policy is Invalid");
                }
            }
        }

        if (ruleBaseValues.getRuleTemplate() != null) {
            for (Object element : ruleBaseValues.getRuleTemplate().getActiveRuleTemplateAttributes()) {
                RuleTemplateAttribute templateAttribute = (RuleTemplateAttribute) element;
                if (!templateAttribute.isRuleValidationAttribute()) {
                    continue;
                }
                RuleValidationAttribute attribute = templateAttribute.getRuleValidationAttribute();
                UserSession userSession = GlobalVariables.getUserSession();
                try {
                    RuleValidationContext validationContext = new RuleValidationContext(ruleBaseValues, ruleDelegation, userSession);
                    ValidationResults results = attribute.validate(validationContext);
                    if (results != null && !results.getValidationResults().isEmpty()) {
                        errors.add(results);
                    }
                } catch (Exception e) {
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException)e;
                    }
                    throw new RuntimeException("Problem validation rule.", e);
                }

            }
        }
        if (ruleBaseValues.getRuleExpressionDef() != null) {
            // rule expressions do not require parse-/save-time validation
        }

        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("RuleBaseValues validation errors", errors);
        }
    }

    public List findByDocumentId(String documentId) {
        return getRuleDAO().findByDocumentId(documentId);
    }

    public List search(String docTypeName, Long ruleId, Long ruleTemplateId, String ruleDescription, String groupId, String principalId,
            Boolean delegateRule, Boolean activeInd, Map extensionValues, String workflowIdDirective) {
        return getRuleDAO().search(docTypeName, ruleId, ruleTemplateId, ruleDescription, groupId, principalId, delegateRule,
                activeInd, extensionValues, workflowIdDirective);
    }

    public List search(String docTypeName, String ruleTemplateName, String ruleDescription, String groupId, String principalId,
            Boolean workgroupMember, Boolean delegateRule, Boolean activeInd, Map extensionValues, Collection<String> actionRequestCodes) {

        if ( (StringUtils.isEmpty(docTypeName)) &&
                (StringUtils.isEmpty(ruleTemplateName)) &&
                (StringUtils.isEmpty(ruleDescription)) &&
                (StringUtils.isEmpty(groupId)) &&
                (StringUtils.isEmpty(principalId)) &&
                (extensionValues.isEmpty()) &&
                (actionRequestCodes.isEmpty()) ) {
            // all fields are empty
            throw new IllegalArgumentException("At least one criterion must be sent");
        }

        RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateName(ruleTemplateName);
        Long ruleTemplateId = null;
        if (ruleTemplate != null) {
            ruleTemplateId = ruleTemplate.getRuleTemplateId();
        }

        if ( ( (extensionValues != null) && (!extensionValues.isEmpty()) ) &&
                (ruleTemplateId == null) ) {
            // cannot have extensions without a correct template
            throw new IllegalArgumentException("A Rule Template Name must be given if using Rule Extension values");
        }

        Collection<String> workgroupIds = new ArrayList<String>();
        if (principalId != null) {
            KEWServiceLocator.getIdentityHelperService().validatePrincipalId(principalId);
            if ( (workgroupMember == null) || (workgroupMember.booleanValue()) ) {
        		workgroupIds = getIdentityManagementService().getGroupIdsForPrincipal(principalId);
        	} else {
        		// user was passed but workgroups should not be parsed... do nothing
        	}
        } else if (groupId != null) {
        	Group group = KEWServiceLocator.getIdentityHelperService().getGroup(groupId);
        	if (group == null) {
        		throw new IllegalArgumentException("Group does not exist in for given group id: " + groupId);
        	} else  {
        		workgroupIds.add(group.getId());
        	}
        }

        return getRuleDAO().search(docTypeName, ruleTemplateId, ruleDescription, workgroupIds, principalId,
                delegateRule,activeInd, extensionValues, actionRequestCodes);
    }

    public void delete(Long ruleBaseValuesId) {
        getRuleDAO().delete(ruleBaseValuesId);
    }

    public RuleBaseValues findRuleBaseValuesById(Long ruleBaseValuesId) {
        return getRuleDAO().findRuleBaseValuesById(ruleBaseValuesId);
    }

    public RuleResponsibility findRuleResponsibility(Long responsibilityId) {
        return getRuleDAO().findRuleResponsibility(responsibilityId);
    }

    public List fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateName, String documentType, boolean ignoreCache) {
        PerformanceLogger performanceLogger = new PerformanceLogger();
        Boolean cachingRules = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(KEWConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.RULE_DETAIL_TYPE, USING_RULE_CACHE_IND);
        if (cachingRules.booleanValue()) {
            //Cache cache = SpringServiceLocator.getCache();
            List<RuleBaseValues> rules = getListFromCache(ruleTemplateName, documentType);
            if (rules != null && !ignoreCache) {
                performanceLogger.log("Time to fetchRules by template " + ruleTemplateName + " cached.");
                return rules;
            }
            RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateName(ruleTemplateName);
            if (ruleTemplate == null) {
                return Collections.EMPTY_LIST;
            }
            Long ruleTemplateId = ruleTemplate.getRuleTemplateId();
            //RuleListCache translatedRules = new RuleListCache();
            //translatedRules.setId(getRuleCacheKey(ruleTemplateName, documentType));
            rules = getRuleDAO().fetchAllCurrentRulesForTemplateDocCombination(ruleTemplateId, getDocGroupAndTypeList(documentType));
            //translatedRules.addAll(rules);
            putListInCache(ruleTemplateName, documentType, rules);
            //cache.add(RULE_CACHE_NAME, translatedRules);
            performanceLogger.log("Time to fetchRules by template " + ruleTemplateName + " cache refreshed.");
            return rules;
        } else {
            Long ruleTemplateId = getRuleTemplateService().findByRuleTemplateName(ruleTemplateName).getRuleTemplateId();
            performanceLogger.log("Time to fetchRules by template " + ruleTemplateName + " not caching.");
            return getRuleDAO().fetchAllCurrentRulesForTemplateDocCombination(ruleTemplateId, getDocGroupAndTypeList(documentType));
        }
    }

    public List fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateName, String documentType) {
        return fetchAllCurrentRulesForTemplateDocCombination(ruleTemplateName, documentType, false);
    }

    public List fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateName, String documentType, Timestamp effectiveDate){
        Long ruleTemplateId = getRuleTemplateService().findByRuleTemplateName(ruleTemplateName).getRuleTemplateId();
        PerformanceLogger performanceLogger = new PerformanceLogger();
        performanceLogger.log("Time to fetchRules by template " + ruleTemplateName + " not caching.");
        return getRuleDAO().fetchAllCurrentRulesForTemplateDocCombination(ruleTemplateId, getDocGroupAndTypeList(documentType), effectiveDate);
    }
    public List fetchAllRules(boolean currentRules) {
        return getRuleDAO().fetchAllRules(currentRules);
    }

    private List getDocGroupAndTypeList(String documentType) {
        List docTypeList = new ArrayList();
        DocumentTypeService docTypeService = (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
        DocumentType docType = docTypeService.findByName(documentType);
        while (docType != null) {
            docTypeList.add(docType.getName());
            docType = docType.getParentDocType();
        }
        return docTypeList;
    }

    private Integer getNextVersionNumber(RuleBaseValues currentRule) {
        List candidates = new ArrayList();
        candidates.add(currentRule.getVersionNbr());
        List pendingRules = ruleDAO.findByPreviousVersionId(currentRule.getRuleBaseValuesId());
        for (Iterator iterator = pendingRules.iterator(); iterator.hasNext();) {
            RuleBaseValues pendingRule = (RuleBaseValues) iterator.next();
            candidates.add(pendingRule.getVersionNbr());
        }
        Collections.sort(candidates);
        Integer maxVersionNumber = (Integer) candidates.get(candidates.size() - 1);
        if (maxVersionNumber == null) {
            return Integer.valueOf(0);
        }
        return Integer.valueOf(maxVersionNumber.intValue() + 1);
    }

    /**
     * Determines if the given rule is locked for routing.
     *
     * In the case of a root rule edit, this method will take the rule id of the rule being edited.
     *
     * In the case of a new delegate rule or a delegate rule edit, this method will take the id of it's parent.
     */
    public String isLockedForRouting(Long currentRuleBaseValuesId) {
        // checks for any other versions of the given rule, essentially, if this is a rule edit we want to see how many other
        // pending edits are out there
        List pendingRules = ruleDAO.findByPreviousVersionId(currentRuleBaseValuesId);
        boolean isDead = true;
        for (Iterator iterator = pendingRules.iterator(); iterator.hasNext();) {
            RuleBaseValues pendingRule = (RuleBaseValues) iterator.next();

            if (pendingRule.getDocumentId() != null && StringUtils.isNotBlank(pendingRule.getDocumentId())) {
                DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(pendingRule.getDocumentId());
                // the pending edit is considered dead if it's been disapproved or cancelled and we are allowed to proceed with our own edit
                isDead = routeHeader.isDisaproved() || routeHeader.isCanceled();
                if (!isDead) {
                    return pendingRule.getDocumentId();
                }
            }

            for (Object element : pendingRule.getResponsibilities()) {
                RuleResponsibility responsibility = (RuleResponsibility) element;
                for (Object element2 : responsibility.getDelegationRules()) {
                    RuleDelegation delegation = (RuleDelegation) element2;
                    List pendingDelegateRules = ruleDAO.findByPreviousVersionId(delegation.getDelegationRuleBaseValues().getRuleBaseValuesId());
                    for (Iterator iterator3 = pendingDelegateRules.iterator(); iterator3.hasNext();) {
                        RuleBaseValues pendingDelegateRule = (RuleBaseValues) iterator3.next();
                        if (pendingDelegateRule.getDocumentId() != null && StringUtils.isNotBlank(pendingDelegateRule.getDocumentId())) {
                            DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(pendingDelegateRule.getDocumentId());
                            isDead = routeHeader.isDisaproved() || routeHeader.isCanceled();
                            if (!isDead) {
                                return pendingDelegateRule.getDocumentId();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public RuleBaseValues getParentRule(RuleBaseValues rule) {
        if (rule == null || rule.getRuleBaseValuesId() == null) {
            throw new IllegalArgumentException("Rule must be non-null with non-null id: " + rule);
        }
        if (!Boolean.TRUE.equals(rule.getDelegateRule())) {
            return null;
        }
        return getRuleDAO().getParentRule(rule.getRuleBaseValuesId());
    }

    /**
     * This configuration is currently stored in a system parameter named "CUSTOM_DOCUMENT_TYPES ",
     * long term we should come up with a better solution.  The format of this constant is a comma-separated
     * list of entries of the following form:
     *
     * <<name of doc type on rule>>:<<rule template name on rule>>:<<type of rule>>:<<name of document type to use for rule routing>>
     *
     * Rule type indicates either main or delegation rules.  A main rule is indicated by the character 'M' and a
     * delegate rule is indicated by the character 'D'.
     *
     * So, if you wanted to route "main" rules made for the "MyDocType" document with the rule template name
     * "MyRuleTemplate" using the "MyMainRuleDocType" doc type, it would be specified as follows:
     *
     * MyDocType:MyRuleTemplate:M:MyMainRuleDocType
     *
     * If you also wanted to route "delegate" rules made for the "MyDocType" document with rule template name
     * "MyDelegateTemplate" using the "MyDelegateRuleDocType", you would then set the constant as follows:
     *
     * MyDocType:MyRuleTemplate:M:MyMainRuleDocType,MyDocType:MyDelegateTemplate:D:MyDelegateRuleDocType
     *
     * TODO this method ended up being a mess, we should get rid of this as soon as we can
     */
    public String getRuleDocmentTypeName(List rules) {
        if (rules.size() == 0) {
            throw new IllegalArgumentException("Cannot determine rule DocumentType for an empty list of rules.");
        }
        String ruleDocTypeName = null;
        RuleRoutingConfig config = RuleRoutingConfig.parse();
        // There are 2 cases here
        RuleBaseValues firstRule = (RuleBaseValues)rules.get(0);
        if (Boolean.TRUE.equals(firstRule.getDelegateRule())) {
            // if it's a delegate rule then the list will contain only 2 elements, the first is the delegate rule,
            // the second is the parent rule.  In this case just look at the custom routing process for the delegate rule.
            ruleDocTypeName = config.getDocumentTypeName(firstRule);
        } else {
            // if this is a list of parent rules being routed, look at all configued routing types and verify that they are
            // all the same, if not throw an exception
            String parentRulesDocTypeName = null;
            for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
                RuleBaseValues rule = (RuleBaseValues) iterator.next();
                // if it's a delegate rule just skip it
                if  (Boolean.TRUE.equals(rule.getDelegateRule())) {
                    continue;
                }
                String currentDocTypeName = config.getDocumentTypeName(rule);
                if (parentRulesDocTypeName == null) {
                    parentRulesDocTypeName = currentDocTypeName;
                } else {
                    if (!ObjectUtils.equals(currentDocTypeName, parentRulesDocTypeName)) {
                        throw new RuntimeException("There are multiple rules being routed and they have different document type definitions!  " + parentRulesDocTypeName + " and " + currentDocTypeName);
                    }
                }
            }
            ruleDocTypeName = parentRulesDocTypeName;
        }
        if (ruleDocTypeName == null) {
            ruleDocTypeName = KEWConstants.DEFAULT_RULE_DOCUMENT_NAME;
        }
        return ruleDocTypeName;
    }

    public void setRuleDAO(RuleDAO ruleDAO) {
        this.ruleDAO = ruleDAO;
    }

    public RuleDAO getRuleDAO() {
        return ruleDAO;
    }

    public void deleteRuleResponsibilityById(Long ruleResponsibilityId) {
        getRuleResponsibilityDAO().delete(ruleResponsibilityId);
    }

    public RuleResponsibility findByRuleResponsibilityId(Long ruleResponsibilityId) {
        return getRuleResponsibilityDAO().findByRuleResponsibilityId(ruleResponsibilityId);
    }

    public List findRuleBaseValuesByResponsibilityReviewer(String reviewerName, String type) {
        return getRuleDAO().findRuleBaseValuesByResponsibilityReviewer(reviewerName, type);
    }

    public List findRuleBaseValuesByResponsibilityReviewerTemplateDoc(String ruleTemplateName, String documentType, String reviewerName, String type) {
        return getRuleDAO().findRuleBaseValuesByResponsibilityReviewerTemplateDoc(ruleTemplateName, documentType, reviewerName, type);
    }

    public RuleTemplateService getRuleTemplateService() {
        return (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
    }

    public DocumentTypeService getDocumentTypeService() {
        return (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
    }

    public IdentityManagementService getIdentityManagementService() {
        return KimApiServiceLocator.getIdentityManagementService();
    }

    public ActionRequestService getActionRequestService() {
        return (ActionRequestService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_REQUEST_SRV);
    }

    private ResponsibilityIdService getResponsibilityIdService() {
        return (ResponsibilityIdService) KEWServiceLocator.getService(KEWServiceLocator.RESPONSIBILITY_ID_SERVICE);
    }

    private RuleDelegationService getRuleDelegationService() {
        return (RuleDelegationService) KEWServiceLocator.getService(KEWServiceLocator.RULE_DELEGATION_SERVICE);
    }

    private RouteHeaderService getRouteHeaderService() {
        return (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
    }

    /**
     * A comparator implementation which compares RuleBaseValues and puts all delegate rules first.
     */
    public class RuleDelegationSorter implements Comparator {
        public int compare(Object arg0, Object arg1) {
            RuleBaseValues rule1 = (RuleBaseValues) arg0;
            RuleBaseValues rule2 = (RuleBaseValues) arg1;

            Integer rule1Value = new Integer((rule1.getDelegateRule().booleanValue() ? 0 : 1));
            Integer rule2Value = new Integer((rule2.getDelegateRule().booleanValue() ? 0 : 1));
            int value = rule1Value.compareTo(rule2Value);
            return value;
        }
    }


    public void loadXml(InputStream inputStream, String principalId) {
        RuleXmlParser parser = new RuleXmlParser();
        try {
            parser.parseRules(inputStream);
        } catch (Exception e) { //any other exception
            LOG.error("Error loading xml file", e);
            WorkflowServiceErrorException wsee = new WorkflowServiceErrorException("Error loading xml file", new WorkflowServiceErrorImpl("Error loading xml file", XML_PARSE_ERROR));
            wsee.initCause(e);
            throw wsee;
        }
    }

    public Element export(ExportDataSet dataSet) {
        RuleXmlExporter exporter = new RuleXmlExporter(XmlConstants.RULE_NAMESPACE);
        return exporter.export(dataSet);
    }
    
    @Override
	public boolean supportPrettyPrint() {
		return true;
	}

    protected List<RuleBaseValues> loadRules(List<Long> ruleIds) {
        List<RuleBaseValues> rules = new ArrayList<RuleBaseValues>();
        for (Long ruleId : ruleIds) {
            RuleBaseValues rule = KEWServiceLocator.getRuleService().findRuleBaseValuesById(ruleId);
            rules.add(rule);
        }
        return rules;
    }

    /**
     * If a rule has been modified and is no longer current since the original request was made, we need to
     * be sure to NOT update the rule.
     */
    protected boolean shouldChangeRuleInvolvement(String documentId, RuleBaseValues rule) {
        if (!rule.getCurrentInd()) {
            LOG.warn("Rule requested for rule involvement change by document " + documentId + " is no longer current.  Change will not be executed!  Rule id is: " + rule.getRuleBaseValuesId());
            return false;
        }
        String lockingDocumentId = KEWServiceLocator.getRuleService().isLockedForRouting(rule.getRuleBaseValuesId());
        if (lockingDocumentId != null) {
            LOG.warn("Rule requested for rule involvement change by document " + documentId + " is locked by document " + lockingDocumentId + " and cannot be modified.  " +
                    "Change will not be executed!  Rule id is: " + rule.getRuleBaseValuesId());
            return false;
        }
        return true;
    }

    protected RuleDelegation getRuleDelegationForDelegateRule(RuleBaseValues rule) {
        if (Boolean.TRUE.equals(rule.getDelegateRule())) {
            List delegations = getRuleDelegationService().findByDelegateRuleId(rule.getRuleBaseValuesId());
            for (Iterator iterator = delegations.iterator(); iterator.hasNext();) {
                RuleDelegation ruleDelegation = (RuleDelegation) iterator.next();
                RuleBaseValues parentRule = ruleDelegation.getRuleResponsibility().getRuleBaseValues();
                if (Boolean.TRUE.equals(parentRule.getCurrentInd())) {
                    return ruleDelegation;
                }
            }
        }
        return null;
    }

    protected void hookUpDelegateRuleToParentRule(RuleBaseValues newParentRule, RuleBaseValues newDelegationRule, RuleDelegation existingRuleDelegation) {
        // hook up parent rule to new rule delegation
        boolean foundDelegation = false;
        outer:for (RuleResponsibility responsibility : (List<RuleResponsibility>)newParentRule.getResponsibilities()) {
            for (RuleDelegation ruleDelegation : (List<RuleDelegation>)responsibility.getDelegationRules()) {
                if (ruleDelegation.getDelegationRuleBaseValues().getRuleBaseValuesId().equals(existingRuleDelegation.getDelegationRuleBaseValues().getRuleBaseValuesId())) {
                    ruleDelegation.setDelegationRuleBaseValues(newDelegationRule);
                    foundDelegation = true;
                    break outer;
                }
            }
        }
        if (!foundDelegation) {
            throw new WorkflowRuntimeException("Failed to locate the existing rule delegation with id: " + existingRuleDelegation.getDelegationRuleBaseValues().getRuleBaseValuesId());
        }

    }

    protected RuleBaseValues createNewRuleVersion(RuleBaseValues existingRule, String documentId) throws Exception {
        RuleBaseValues rule = new RuleBaseValues();
        PropertyUtils.copyProperties(rule, existingRule);
        rule.setPreviousVersion(existingRule);
        rule.setPreviousVersionId(existingRule.getRuleBaseValuesId());
        rule.setRuleBaseValuesId(null);
        rule.setActivationDate(null);
        rule.setDeactivationDate(null);
        rule.setVersionNumber(0L);
        rule.setDocumentId(documentId);

        // TODO: FIXME: need to copy the rule expression here too?

        rule.setResponsibilities(new ArrayList());
        for (RuleResponsibility existingResponsibility : (List<RuleResponsibility>)existingRule.getResponsibilities()) {
            RuleResponsibility responsibility = new RuleResponsibility();
            PropertyUtils.copyProperties(responsibility, existingResponsibility);
            responsibility.setRuleBaseValues(rule);
            responsibility.setRuleBaseValuesId(null);
            responsibility.setRuleResponsibilityKey(null);
            responsibility.setVersionNumber(0L);
            rule.getResponsibilities().add(responsibility);
//            responsibility.setDelegationRules(new ArrayList());
//            for (RuleDelegation existingDelegation : (List<RuleDelegation>)existingResponsibility.getDelegationRules()) {
//                RuleDelegation delegation = new RuleDelegation();
//                PropertyUtils.copyProperties(delegation, existingDelegation);
//                delegation.setRuleDelegationId(null);
//                delegation.setRuleResponsibility(responsibility);
//                delegation.setRuleResponsibilityId(null);
//                delegation.setVersionNumber(0L);
//                // it's very important that we do NOT recurse down into the delegation rules and reversion those,
//                // this is important to how rule versioning works
//                responsibility.getDelegationRules().add(delegation);
//            }
        }
        rule.setRuleExtensions(new ArrayList());
        for (RuleExtension existingExtension : (List<RuleExtension>)existingRule.getRuleExtensions()) {
            RuleExtension extension = new RuleExtension();
            PropertyUtils.copyProperties(extension, existingExtension);
            extension.setLockVerNbr(0);
            extension.setRuleBaseValues(rule);
            extension.setRuleBaseValuesId(null);
            extension.setRuleExtensionId(null);
            rule.getRuleExtensions().add(extension);
            extension.setExtensionValues(new ArrayList<RuleExtensionValue>());
            for (RuleExtensionValue existingExtensionValue : extension.getExtensionValues()) {
                RuleExtensionValue extensionValue = new RuleExtensionValue();
                PropertyUtils.copyProperties(extensionValue, existingExtensionValue);
                extensionValue.setExtension(extension);
                extensionValue.setRuleExtensionId(null);
                extensionValue.setLockVerNbr(0);
                extensionValue.setRuleExtensionValueId(null);
                extension.getExtensionValues().add(extensionValue);
            }
        }
        return rule;
    }

    private static class RuleVersion {
        public RuleBaseValues rule;
        public RuleBaseValues parent;
        public RuleDelegation delegation;
    }

    private static class RuleRoutingConfig {
        private List configs = new ArrayList();
        public static RuleRoutingConfig parse() {
            RuleRoutingConfig config = new RuleRoutingConfig();
            String constant = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KEWConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.RULE_DETAIL_TYPE, KEWConstants.RULE_CUSTOM_DOC_TYPES);
            if (!StringUtils.isEmpty(constant)) {
                String[] ruleConfigs = constant.split(",");
                for (String ruleConfig : ruleConfigs) {
                    String[] configElements = ruleConfig.split(":");
                    if (configElements.length != 4) {
                        throw new RuntimeException("Found incorrect number of config elements within a section of the custom rule document types config.  There should have been four ':' delimited sections!  " + ruleConfig);
                    }
                    config.configs.add(configElements);
                }
            }
            return config;
        }
        public String getDocumentTypeName(RuleBaseValues rule) {
            for (Iterator iterator = configs.iterator(); iterator.hasNext();) {
                String[] configElements = (String[]) iterator.next();
                String docTypeName = configElements[0];
                String ruleTemplateName = configElements[1];
                String type = configElements[2];
                String ruleDocTypeName = configElements[3];
                if (rule.getDocTypeName().equals(docTypeName) && rule.getRuleTemplateName().equals(ruleTemplateName)) {
                    if (type.equals("M")) {
                        if (Boolean.FALSE.equals(rule.getDelegateRule())) {
                            return ruleDocTypeName;
                        }
                    } else if (type.equals("D")) {
                        if (Boolean.TRUE.equals(rule.getDelegateRule())) {
                            return ruleDocTypeName;
                        }
                    } else {
                        throw new RuntimeException("Bad rule type '" + type + "' in rule doc type routing config.");
                    }
                }
            }
            return null;
        }
    }

    public Long getDuplicateRuleId(RuleBaseValues rule) {

    	// TODO: this method is extremely slow, if we could implement a more optimized query here, that would help tremendously

    	List responsibilities = rule.getResponsibilities();
    	List extensions = rule.getRuleExtensions();
    	String docTypeName = rule.getDocTypeName();
    	String ruleTemplateName = rule.getRuleTemplateName();
        List rules = fetchAllCurrentRulesForTemplateDocCombination(rule.getRuleTemplateName(), rule.getDocTypeName(), false);
        Iterator it = rules.iterator();
        while (it.hasNext()) {
            RuleBaseValues r = (RuleBaseValues) it.next();
            if (ObjectUtils.equals(rule.getActiveInd(), r.getActiveInd()) &&
        	ObjectUtils.equals(docTypeName, r.getDocTypeName()) &&
                    ObjectUtils.equals(ruleTemplateName, r.getRuleTemplateName()) &&
                    ObjectUtils.equals(rule.getRuleExpressionDef(), r.getRuleExpressionDef()) &&
                    CollectionUtils.collectionsEquivalent(responsibilities, r.getResponsibilities()) &&
                    CollectionUtils.collectionsEquivalent(extensions, r.getRuleExtensions())) {
                // we have a duplicate
                return r.getRuleBaseValuesId();
            }
        }
        return null;
    }

    private void generateRuleNameIfNeeded(RuleBaseValues rule) {
        if (StringUtils.isBlank(rule.getName())) {
        	rule.setName(UUID.randomUUID().toString());
        }
    }

    private void assignResponsibilityIds(RuleBaseValues rule) {
    	for (RuleResponsibility responsibility : rule.getResponsibilities()) {
    		if (responsibility.getResponsibilityId() == null) {
    			responsibility.setResponsibilityId(KEWServiceLocator.getResponsibilityIdService().getNewResponsibilityId());
    		}
    	}
    }

    public RuleBaseValues saveRule(RuleBaseValues rule, boolean isRetroactiveUpdatePermitted) {
    	rule.setPreviousVersionId(rule.getRuleBaseValuesId());
		rule.setPreviousVersion(null);
		rule.setRuleBaseValuesId(null);
		makeCurrent(rule, isRetroactiveUpdatePermitted);
		return rule;
    }

    public List<RuleBaseValues> saveRules(List<RuleBaseValues> rulesToSave, boolean isRetroactiveUpdatePermitted) {
    	List<RuleBaseValues> savedRules = new ArrayList<RuleBaseValues>();
    	for (RuleBaseValues rule : rulesToSave) {
    		rule = saveRule(rule, isRetroactiveUpdatePermitted);
    		savedRules.add(rule);
    	}
    	return savedRules;
    }

    public RuleDelegation saveRuleDelegation(RuleDelegation ruleDelegation, boolean isRetroactiveUpdatePermitted) {
    	RuleBaseValues rule = ruleDelegation.getDelegationRuleBaseValues();
		rule.setPreviousVersionId(rule.getRuleBaseValuesId());
		rule.setPreviousVersion(null);
		rule.setRuleBaseValuesId(null);
		ruleDelegation.setRuleDelegationId(null);
		makeCurrent(ruleDelegation, isRetroactiveUpdatePermitted);
		return ruleDelegation;
    }

    public List<RuleDelegation> saveRuleDelegations(List<RuleDelegation> ruleDelegationsToSave, boolean isRetroactiveUpdatePermitted) {
    	List<RuleDelegation> savedRuleDelegations = new ArrayList<RuleDelegation>();
    	for (RuleDelegation ruleDelegation : ruleDelegationsToSave) {
    		ruleDelegation = saveRuleDelegation(ruleDelegation, isRetroactiveUpdatePermitted);
    		savedRuleDelegations.add(ruleDelegation);
    	}
    	return savedRuleDelegations;
    }

    public Long findResponsibilityIdForRule(String ruleName, String ruleResponsibilityName, String ruleResponsibilityType) {
    	return getRuleDAO().findResponsibilityIdForRule(ruleName, ruleResponsibilityName, ruleResponsibilityType);
    }

}
