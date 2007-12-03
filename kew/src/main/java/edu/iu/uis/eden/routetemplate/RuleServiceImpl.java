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
package edu.iu.uis.eden.routetemplate;

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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.actionrequests.ActionRequestService;
import edu.iu.uis.eden.applicationconstants.ApplicationConstantsService;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.messaging.MessageServiceNames;
import edu.iu.uis.eden.responsibility.ResponsibilityIdService;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.RouteHeaderService;
import edu.iu.uis.eden.routetemplate.dao.RuleDAO;
import edu.iu.uis.eden.routetemplate.dao.RuleResponsibilityDAO;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.PerformanceLogger;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.validation.RuleValidationContext;
import edu.iu.uis.eden.validation.ValidationResults;
import edu.iu.uis.eden.web.session.UserSession;
import edu.iu.uis.eden.workgroup.GroupId;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupService;
import edu.iu.uis.eden.xml.RuleXmlParser;
import edu.iu.uis.eden.xml.export.RuleXmlExporter;

public class RuleServiceImpl implements RuleService {

    private static final String USING_RULE_CACHE_KEY = "RuleService.IsCaching";
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
            ruleBaseValues.setCurrentInd(new Boolean(false));
            ruleBaseValues.setVersionNbr(getNextVersionNumber(oldRule));
        }
        if (ruleBaseValues.getVersionNbr() == null) {
            ruleBaseValues.setVersionNbr(new Integer(0));
        }
        if (ruleBaseValues.getCurrentInd() == null) {
            ruleBaseValues.setCurrentInd(new Boolean(false));
        }
        // iterate through all associated responsibilities, and if they are unsaved (responsibilityId is null)
        // set a new id on them, and recursively save any associated delegation rules
        for (Iterator iterator = ruleBaseValues.getResponsibilities().iterator(); iterator.hasNext();) {
            RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
            if (responsibility.getResponsibilityId() == null) {
                responsibility.setResponsibilityId(getResponsibilityIdService().getNewResponsibilityId());
            }
            if (saveDelegations) {
                for (Iterator iter = responsibility.getDelegationRules().iterator(); iter.hasNext();) {
                    RuleDelegation localRuleDelegation = (RuleDelegation) iter.next();
                    save2(localRuleDelegation.getDelegationRuleBaseValues(), localRuleDelegation, true);
                }
            }
        }
        validate2(ruleBaseValues, ruleDelegation, null);
        getRuleDAO().save(ruleBaseValues);
    }

    public void makeCurrent(Long routeHeaderId) throws EdenUserNotFoundException {
	makeCurrent(findByRouteHeaderId(routeHeaderId));
    }

    public void makeCurrent(List rules) throws EdenUserNotFoundException {
        PerformanceLogger performanceLogger = new PerformanceLogger();

        boolean isGenerateRuleArs = true;
        String generateRuleArs = Utilities.getApplicationConstant(EdenConstants.RULE_CHANGE_AR_GENERATION_KEY);
        if (!StringUtils.isBlank(generateRuleArs)) {
            isGenerateRuleArs = EdenConstants.YES_RULE_CHANGE_AR_GENERATION_VALUE.equalsIgnoreCase(generateRuleArs);
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
                rule.setDeactivationDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse("01/01/2100").getTime()));
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
            for (Iterator iterator = rule.getResponsibilities().iterator(); iterator.hasNext();) {
                RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
                for (Iterator delIterator = responsibility.getDelegationRules().iterator(); delIterator.hasNext();) {
                    RuleDelegation delegation = (RuleDelegation) delIterator.next();

                    delegation.getDelegationRuleBaseValues().setCurrentInd(Boolean.TRUE);
                    RuleBaseValues delegatorRule = delegation.getDelegationRuleBaseValues();

                    performanceLogger.log("Setting delegate rule: " + delegatorRule.getDescription() + " to current.");
                    if (delegatorRule.getActivationDate() == null) {
                        delegatorRule.setActivationDate(date);
                    }
                    try {
                        delegatorRule.setDeactivationDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse("01/01/2100").getTime()));
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
        for (Iterator iterator = notifyMap.values().iterator(); iterator.hasNext();) {
			queueRuleCache((Long)iterator.next());
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
    public void makeCurrent2(List rules) throws EdenUserNotFoundException {
        PerformanceLogger performanceLogger = new PerformanceLogger();

        boolean isGenerateRuleArs = true;
        String generateRuleArs = Utilities.getApplicationConstant(EdenConstants.RULE_CHANGE_AR_GENERATION_KEY);
        if (!StringUtils.isBlank(generateRuleArs)) {
            isGenerateRuleArs = EdenConstants.YES_RULE_CHANGE_AR_GENERATION_VALUE.equalsIgnoreCase(generateRuleArs);
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
                rule.setDeactivationDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse("01/01/2100").getTime()));
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
            for (Iterator iterator = rule.getResponsibilities().iterator(); iterator.hasNext();) {
                RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
                for (Iterator delIterator = responsibility.getDelegationRules().iterator(); delIterator.hasNext();) {
                    RuleDelegation delegation = (RuleDelegation) delIterator.next();
                    RuleBaseValues delegateRule = delegation.getDelegationRuleBaseValues();
                    delegateRule.setCurrentInd(Boolean.TRUE);
                    performanceLogger.log("Setting delegate rule: " + delegateRule.getDescription() + " to current.");
                    if (delegateRule.getActivationDate() == null) {
                        delegateRule.setActivationDate(date);
                    }
                    try {
                        delegateRule.setDeactivationDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse("01/01/2100").getTime()));
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
        for (Iterator iterator = notifyMap.values().iterator(); iterator.hasNext();) {
            queueRuleCache((Long)iterator.next());
        }
        if (isGenerateRuleArs) {
            getActionRequestService().updateActionRequestsForResponsibilityChange(responsibilityIds);
        }
        performanceLogger.log("Time to make current");
    }

	private void queueRuleCache(Long ruleId){
//        PersistedMessage ruleCache = new PersistedMessage();
//        ruleCache.setQueuePriority(EdenConstants.ROUTE_QUEUE_RULE_CACHE_PRIORITY);
//        ruleCache.setQueueDate(new Timestamp(new Date().getTime()));
//        ruleCache.setQueueStatus(EdenConstants.ROUTE_QUEUE_QUEUED);
//        ruleCache.setRetryCount(new Integer(0));
//        ruleCache.setPayload("" + ruleId);
//        ruleCache.setProcessorClassName("edu.iu.uis.eden.cache.RuleCacheProcessor");
//	    getRouteQueueService().requeueDocument(ruleCache);


	    RuleCacheProcessor ruleCacheProcessor = MessageServiceNames.getRuleCacheProcessor();
	    ruleCacheProcessor.clearRuleFromCache(ruleId);

    }

//	private RouteQueueService getRouteQueueService() {
//        return (RouteQueueService)SpringServiceLocator.getService(SpringServiceLocator.ROUTE_QUEUE_SRV);
//    }

    /**
     * Ensure that we don't have any notification duplication.
     */
    private void installNotification(RuleBaseValues rule, Map<String, Long> notifyMap) {
        String key = getRuleCacheKey(rule.getRuleTemplateName(), rule.getDocTypeName());
        if (!notifyMap.containsKey(key)) {
        	notifyMap.put(key, rule.getRuleBaseValuesId());
        }
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
        Boolean cachingRules = new Boolean(Utilities.getApplicationConstant(USING_RULE_CACHE_KEY));
        if (!cachingRules.booleanValue()) {
            return;
        }
        String ruleTemplateName = rule.getRuleTemplate().getName();
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
//            if (getListFromCache(ruleTemplateName, documentType.getName()) != null) {
//                eventGenerator.notify(new CacheDataModifiedEvent(RULE_CACHE_NAME, getRuleCacheKey(ruleTemplateName, documentType.getName())));
//            }
        //}
        //walk the down the document hierarchy when refreshing the cache. The
        // rule could be cached through more than one path
        //HREDOC could be cached under HREDOC.child and HREDOC.child1
        for (Iterator iter = documentType.getChildrenDocTypes().iterator(); iter.hasNext();) {
            DocumentType childDocumentType = (DocumentType) iter.next();
            //SpringServiceLocator.getCacheAdministrator().flushEntry(getRuleCacheKey(ruleTemplateName, childDocumentType.getName()));
//            if (getListFromCache(rule.getRuleTemplate().getName(), childDocumentType.getName()) != null) {
//                eventGenerator.notify(new CacheDataModifiedEvent(RULE_CACHE_NAME, getRuleCacheKey(rule.getRuleTemplate().getName(), childDocumentType.getName())));
//            }
            notifyCacheOfRuleChange(rule, childDocumentType);
        }
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
    	return (List) KEWServiceLocator.getCacheAdministrator().getFromCache(getRuleCacheKey(ruleTemplateName, documentTypeName));
    	//return (List) SpringServiceLocator.getCache().getCachedObjectById(RULE_CACHE_NAME, getRuleCacheKey(ruleTemplateName, documentTypeName));
    }

    protected void putListInCache(String ruleTemplateName, String documentTypeName, List<RuleBaseValues> rules) {
    	LOG.info("Caching " + rules.size() + " rules for ruleTemplate='" + ruleTemplateName + "' and documentType='" + documentTypeName + "'");
    	String groups[] = new String[] { getDocumentTypeRuleCacheGroupName(documentTypeName), RULE_GROUP_CACHE };
    	KEWServiceLocator.getCacheAdministrator().putInCache(getRuleCacheKey(ruleTemplateName, documentTypeName), rules, groups);
    }

    protected void flushDocumentTypeFromCache(String documentTypeName) {
    	LOG.info("Flushing DocumentType from Cache for the given name: " + documentTypeName);
    	KEWServiceLocator.getCacheAdministrator().flushGroup(getDocumentTypeRuleCacheGroupName(documentTypeName));
    }

    protected void flushListFromCache(String ruleTemplateName, String documentTypeName) {
    	LOG.info("Flushing rules from Cache for ruleTemplate='" + ruleTemplateName + "' and documentType='" + documentTypeName + "'");
    	KEWServiceLocator.getCacheAdministrator().flushEntry(getRuleCacheKey(ruleTemplateName, documentTypeName));
    }

    public void flushRuleCache() {
	LOG.info("Flushing entire Rule Cache.");
	KEWServiceLocator.getCacheAdministrator().flushGroup(RULE_GROUP_CACHE);
    }

    private Set getResponsibilityIdsFromGraph(RuleBaseValues rule, boolean isRuleCollecting) {
        Set responsibilityIds = new HashSet();
        for (Iterator iterator = rule.getResponsibilities().iterator(); iterator.hasNext();) {
            RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
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
        for (Iterator iterator = oldRule.getResponsibilities().iterator(); iterator.hasNext();) {
            RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
            modifiedResponsibilityMap.put(responsibility.getResponsibilityId(), responsibility);
        }
        for (Iterator iterator = newRule.getResponsibilities().iterator(); iterator.hasNext();) {
            RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
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

    public Long route2(Long routeHeaderId, MyRules2 myRules, WorkflowUser user, String annotation, boolean blanketApprove) throws Exception {
        List errors = new ArrayList();
        if (myRules.getRules().isEmpty()) {
            errors.add(new WorkflowServiceErrorImpl("Rule required", "rule.required"));
        }
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("RuleBaseValues validation errors", errors);
        }
        WorkflowDocument flexDoc = null;
        if (routeHeaderId == null) {
           flexDoc = new WorkflowDocument(new WorkflowIdVO(user.getWorkflowId()), getRuleDocmentTypeName(myRules.getRules()));
        } else {
            flexDoc = new WorkflowDocument(new WorkflowIdVO(user.getWorkflowId()), routeHeaderId);
        }

        for (Iterator iter = myRules.getRules().iterator(); iter.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iter.next();
            rule.setRouteHeaderId(flexDoc.getRouteHeaderId());

            flexDoc.addAttributeDefinition(new RuleRoutingDefinition(rule.getDocTypeName()));
            getRuleDAO().retrieveAllReferences(rule);
            save2(rule);
        }

        flexDoc.setTitle(generateTitle(myRules));
        if (blanketApprove) {
            flexDoc.blanketApprove(annotation);
        } else {
            flexDoc.routeDocument(annotation);
        }
        return flexDoc.getRouteHeaderId();
    }

    public Long routeRuleWithDelegate(Long routeHeaderId, RuleBaseValues parentRule, RuleBaseValues delegateRule, WorkflowUser user, String annotation, boolean blanketApprove) throws Exception {
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
//        boolean isRoutingParent = parentRule.getRuleBaseValuesId() == null;
//        if (isRoutingParent) {
//            // it's very important that we do not save delegations here (that's what the false parameter is for)
//            // this is because, if we save the delegations, the existing delegations on our parent rule will become
//            // saved as "non current" before the rule is approved!!!
//            save2(parentRule, null, false);
//            //save2(parentRule, null, true);
//        }

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

        WorkflowDocument flexDoc = null;
        if (routeHeaderId != null) {
            flexDoc = new WorkflowDocument(new WorkflowIdVO(user.getWorkflowId()), routeHeaderId);
        } else {
        	List rules = new ArrayList();
        	rules.add(delegateRule);
        	rules.add(parentRule);
            flexDoc = new WorkflowDocument(new WorkflowIdVO(user.getWorkflowId()), getRuleDocmentTypeName(rules));
        }
        flexDoc.setTitle(generateTitle(parentRule, delegateRule));
        delegateRule.setRouteHeaderId(flexDoc.getRouteHeaderId());
        flexDoc.addAttributeDefinition(new RuleRoutingDefinition(parentRule.getDocTypeName()));
        getRuleDAO().save(delegateRule);
        if (isRoutingParent) {
            parentRule.setRouteHeaderId(flexDoc.getRouteHeaderId());
            getRuleDAO().save(parentRule);
        }
        if (blanketApprove) {
            flexDoc.blanketApprove(annotation);
        } else {
            flexDoc.routeDocument(annotation);
        }
        return flexDoc.getRouteHeaderId();
    }

    /**
     * Gets the RuleDelegation object from the parentRule that points to the delegateRule.
     */
    private RuleDelegation getRuleDelegation(RuleBaseValues parentRule, RuleBaseValues delegateRule) throws Exception {
    	for (Iterator iterator = parentRule.getResponsibilities().iterator(); iterator.hasNext(); ) {
			RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
			for (Iterator respIt = responsibility.getDelegationRules().iterator(); respIt.hasNext(); ) {
				RuleDelegation ruleDelegation = (RuleDelegation) respIt.next();
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

    private String generateTitle(MyRules2 myRules) {
        StringBuffer title = new StringBuffer();
        RuleBaseValues firstRule = myRules.getRule(0);
        if (myRules.getRules().size() > 1) {
            title.append("Routing ").append(myRules.getSize()).append(" Rules, '");
            title.append(firstRule.getDescription()).append("',...");
        } else if (firstRule.getPreviousVersionId() != null) {
            title.append("Editing Rule '").append(firstRule.getDescription()).append("'");
        } else {
            title.append("Adding Rule '").append(firstRule.getDescription()).append("'");
        }
        return title.toString();
    }

    public void validate(RuleBaseValues ruleBaseValues, List errors) throws EdenUserNotFoundException {
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
        if (ruleBaseValues.getIgnorePrevious() == null) {
            errors.add(new WorkflowServiceErrorImpl("Ignore Previous is required", "routetemplate.ruleservice.ignoreprevious.required"));
        }
        if (ruleBaseValues.getResponsibilities().isEmpty()) {
            errors.add(new WorkflowServiceErrorImpl("A responsibility is required", "routetemplate.ruleservice.responsibility.required"));
        } else {
            for (Iterator iter = ruleBaseValues.getResponsibilities().iterator(); iter.hasNext();) {
                RuleResponsibility responsibility = (RuleResponsibility) iter.next();
                if (responsibility.getRuleResponsibilityName() != null && EdenConstants.RULE_RESPONSIBILITY_WORKGROUP_ID.equals(responsibility.getRuleResponsibilityType())) {
                    if (getWorkgroupService().getWorkgroup(new WorkflowGroupId(new Long(responsibility.getRuleResponsibilityName()))) == null) {
                        errors.add(new WorkflowServiceErrorImpl("Workgroup is invalid", "routetemplate.ruleservice.workgroup.invalid"));
                    }
                } else if (responsibility.getWorkflowUser() == null && responsibility.getRole() == null) {
                    errors.add(new WorkflowServiceErrorImpl("User is invalid", "routetemplate.ruleservice.user.invalid"));
                }
            }
        }
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("RuleBaseValues validation errors", errors);
        }
    }

    public void validate2(RuleBaseValues ruleBaseValues, RuleDelegation ruleDelegation, List errors) throws EdenUserNotFoundException {
        if (errors == null) {
            errors = new ArrayList();
        }
        if (getDocumentTypeService().findByName(ruleBaseValues.getDocTypeName()) == null) {
            errors.add(new WorkflowServiceErrorImpl("Document Type Invalid", "doctype.documenttypeservice.doctypename.required"));
            LOG.error("Document Type Invalid");
        }
        if (ruleBaseValues.getToDate() == null) {
            try {
                ruleBaseValues.setToDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse("01/01/2100").getTime()));
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
        if (ruleBaseValues.getIgnorePrevious() == null) {
            errors.add(new WorkflowServiceErrorImpl("Ignore Previous is required", "routetemplate.ruleservice.ignoreprevious.required"));
            LOG.error("Ignore Previous is missing");
        }
        if (ruleBaseValues.getResponsibilities().isEmpty()) {
            errors.add(new WorkflowServiceErrorImpl("A responsibility is required", "routetemplate.ruleservice.responsibility.required"));
            LOG.error("Rule does not have a responsibility");
        } else {
            for (Iterator iter = ruleBaseValues.getResponsibilities().iterator(); iter.hasNext();) {
                RuleResponsibility responsibility = (RuleResponsibility) iter.next();
                if (responsibility.getRuleResponsibilityName() != null && EdenConstants.RULE_RESPONSIBILITY_WORKGROUP_ID.equals(responsibility.getRuleResponsibilityType())) {
                    if (getWorkgroupService().getWorkgroup(new WorkflowGroupId(new Long(responsibility.getRuleResponsibilityName()))) == null) {
                        errors.add(new WorkflowServiceErrorImpl("Workgroup is invalid", "routetemplate.ruleservice.workgroup.invalid"));
                        LOG.error("Workgroup is invalid");
                    }
                } else if (responsibility.getWorkflowUser() == null && responsibility.getRole() == null) {
                    errors.add(new WorkflowServiceErrorImpl("User is invalid", "routetemplate.ruleservice.user.invalid"));
                    LOG.error("User is invalid");
                } else if (responsibility.isUsingRole()) {
                    if (responsibility.getApprovePolicy() == null || !(responsibility.getApprovePolicy().equals(EdenConstants.APPROVE_POLICY_ALL_APPROVE) || responsibility.getApprovePolicy().equals(EdenConstants.APPROVE_POLICY_FIRST_APPROVE))) {
                        errors.add(new WorkflowServiceErrorImpl("Approve Policy is Invalid", "routetemplate.ruleservice.approve.policy.invalid"));
                        LOG.error("Approve Policy is Invalid");
                    }
                }
            }
        }

        for (Iterator iter = ruleBaseValues.getRuleTemplate().getActiveRuleTemplateAttributes().iterator(); iter.hasNext();) {
			RuleTemplateAttribute templateAttribute = (RuleTemplateAttribute) iter.next();
			if (!templateAttribute.isRuleValidationAttribute()) {
				continue;
			}
			RuleValidationAttribute attribute = templateAttribute.getRuleValidationAttribute();
			UserSession userSession = UserSession.getAuthenticatedUser();
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
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("RuleBaseValues validation errors", errors);
        }
    }

    public List findByRouteHeaderId(Long routeHeaderId) {
        return getRuleDAO().findByRouteHeaderId(routeHeaderId);
    }

    public List search(String docTypeName, Long ruleId, Long ruleTemplateId, String ruleDescription, Long workgroupId, String workflowId,
            String roleName, Boolean delegateRule, Boolean activeInd, Map extensionValues, String workflowIdDirective) {
        return getRuleDAO().search(docTypeName, ruleId, ruleTemplateId, ruleDescription, workgroupId, workflowId, roleName, delegateRule,
                activeInd, extensionValues, workflowIdDirective);
    }

    public List search(String docTypeName, String ruleTemplateName, String ruleDescription, GroupId workgroupId, UserId userId, String roleName,
            Boolean workgroupMember, Boolean delegateRule, Boolean activeInd, Map extensionValues, Collection<String> actionRequestCodes) throws EdenUserNotFoundException {

        if ( (StringUtils.isEmpty(docTypeName)) &&
             (StringUtils.isEmpty(ruleTemplateName)) &&
             (StringUtils.isEmpty(ruleDescription)) &&
             (workgroupId.isEmpty()) &&
             (userId.isEmpty()) &&
             (StringUtils.isEmpty(roleName)) &&
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

        WorkflowUser user = null;
        if (userId != null) {
            // below will throw EdenUserNotFoundException
            user = getUserService().getWorkflowUser(userId);
        }

        Collection<String> workgroupIds = new ArrayList();
        if (user != null) {
            if ( (workgroupMember == null) || (workgroupMember.booleanValue()) ) {
                // user is found from DB and we need to parse workgroups
                List userWorkgroups = getWorkgroupService().getUsersGroups(user);
                for (Iterator iter = userWorkgroups.iterator(); iter.hasNext();) {
                    Workgroup workgroup = (Workgroup) iter.next();
                    workgroupIds.add(workgroup.getWorkflowGroupId().getGroupId().toString());
                }
            } else {
                // user was passed but workgroups should not be parsed... do nothing
            }
        } else {
            if (workgroupId != null) {
                Workgroup group = getWorkgroupService().getWorkgroup(workgroupId);
                if ( (group == null) && (!workgroupId.isEmpty()) ) {
                    throw new IllegalArgumentException("Workgroup name given does not exist in Workflow");
                } else if (group != null) {
                    workgroupIds.add(group.getWorkflowGroupId().getGroupId().toString());
                }
            }
        }

        return getRuleDAO().search(docTypeName, ruleTemplateId, ruleDescription, workgroupIds, (user != null) ? user.getWorkflowUserId().getWorkflowId() : null,
                roleName, delegateRule,activeInd, extensionValues, actionRequestCodes);
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

    public void saveDeactivationDate(RuleBaseValues rule) {
        getRuleDAO().saveDeactivationDate(rule);
    }

    public List fetchAllCurrentRulesForTemplateDocCombination(String ruleTemplateName, String documentType, boolean ignoreCache) {
        PerformanceLogger performanceLogger = new PerformanceLogger();
        Boolean cachingRules = new Boolean(Utilities.getApplicationConstant(USING_RULE_CACHE_KEY));
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
            return new Integer(0);
        }
        return new Integer(maxVersionNumber.intValue() + 1);
    }

    /**
     * Determines if the given rule is locked for routing.
     *
     * In the case of a root rule edit, this method will take the rule id of the rule being edited.
     *
     * In the case of a new delegate rule or a delegate rule edit, this method will take the id of it's parent.
     */
    public Long isLockedForRouting(Long currentRuleBaseValuesId) {
    	// checks for any other versions of the given rule, essentially, if this is a rule edit we want to see how many other
    	// pending edits are out there
        List pendingRules = ruleDAO.findByPreviousVersionId(currentRuleBaseValuesId);
        boolean isDead = true;
        for (Iterator iterator = pendingRules.iterator(); iterator.hasNext();) {
            RuleBaseValues pendingRule = (RuleBaseValues) iterator.next();

            if (pendingRule.getRouteHeaderId() != null && pendingRule.getRouteHeaderId().longValue() != 0) {
                DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(pendingRule.getRouteHeaderId());
                // the pending edit is considered dead if it's been disapproved or cancelled and we are allowed to proceed with our own edit
                isDead = routeHeader.isDisaproved() || routeHeader.isCanceled();
                if (!isDead) {
                    return pendingRule.getRouteHeaderId();
                }
            }
            for (Iterator iter = pendingRule.getResponsibilities().iterator(); iter.hasNext();) {
                RuleResponsibility responsibility = (RuleResponsibility) iter.next();
                for (Iterator iterator2 = responsibility.getDelegationRules().iterator(); iterator2.hasNext();) {
                    RuleDelegation delegation = (RuleDelegation) iterator2.next();
                    List pendingDelegateRules = ruleDAO.findByPreviousVersionId(delegation.getDelegationRuleBaseValues().getRuleBaseValuesId());
                    for (Iterator iterator3 = pendingDelegateRules.iterator(); iterator3.hasNext();) {
                        RuleBaseValues pendingDelegateRule = (RuleBaseValues) iterator3.next();
                        if (pendingDelegateRule.getRouteHeaderId() != null && pendingDelegateRule.getRouteHeaderId().longValue() != 0) {
                            DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(pendingDelegateRule.getRouteHeaderId());
                            isDead = routeHeader.isDisaproved() || routeHeader.isCanceled();
                            if (!isDead) {
                                return pendingDelegateRule.getRouteHeaderId();
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
     * This configuration is currently stored in a application constant named "Rule.Config.CustomDocTypes",
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
					if (!Utilities.equals(currentDocTypeName, parentRulesDocTypeName)) {
						throw new RuntimeException("There are multiple rules being routed and they have different document type definitions!  " + parentRulesDocTypeName + " and " + currentDocTypeName);
					}
				}
			}
    		ruleDocTypeName = parentRulesDocTypeName;
    	}
    	if (ruleDocTypeName == null) {
    		ruleDocTypeName = EdenConstants.DEFAULT_RULE_DOCUMENT_NAME;
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

    public WorkgroupService getWorkgroupService() {
        return (WorkgroupService) KEWServiceLocator.getService(KEWServiceLocator.WORKGROUP_SRV);
    }

    public ActionRequestService getActionRequestService() {
        return (ActionRequestService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_REQUEST_SRV);
    }

    private ResponsibilityIdService getResponsibilityIdService() {
        return (ResponsibilityIdService) KEWServiceLocator.getService(KEWServiceLocator.RESPONSIBILITY_ID_SERVICE);
    }

    private ApplicationConstantsService getApplicationConstantsService() {
        return (ApplicationConstantsService) KEWServiceLocator.getService(KEWServiceLocator.APPLICATION_CONSTANTS_SRV);
    }

    private RuleDelegationService getRuleDelegationService() {
        return (RuleDelegationService) KEWServiceLocator.getService(KEWServiceLocator.RULE_DELEGATION_SERVICE);
    }

    private RouteHeaderService getRouteHeaderService() {
        return (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
    }

    private UserService getUserService() {
        return (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
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


    public void loadXml(InputStream inputStream, WorkflowUser user) {
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
        RuleXmlExporter exporter = new RuleXmlExporter();
        return exporter.export(dataSet);
    }

    public void removeRuleInvolvement(Id entityToBeRemoved, List<Long> ruleIds, Long documentId) throws WorkflowException {
	WorkflowUser userToRemove = null;
	Workgroup workgroupToRemove = null;
	if (entityToBeRemoved instanceof UserId) {
	    userToRemove = KEWServiceLocator.getUserService().getWorkflowUser((UserId)entityToBeRemoved);
	} else if (entityToBeRemoved instanceof GroupId) {
	    workgroupToRemove = KEWServiceLocator.getWorkgroupService().getWorkgroup((GroupId)entityToBeRemoved);
	} else {
	    throw new WorkflowRuntimeException("Invalid entity ID for removal was passed, type was: " + entityToBeRemoved);
	}
	if (userToRemove == null && workgroupToRemove == null) {
	    throw new WorkflowRuntimeException("Could not resolve entity to be removed with id: " + entityToBeRemoved);
	}
	List<RuleBaseValues> existingRules = loadRules(ruleIds);
	// sort the rules so that delegations are last, very important in order to deal with parent-child versioning properly
	Collections.sort(existingRules, ComparatorUtils.reversedComparator(new RuleDelegationSorter()));
	// we maintain the old-new mapping so we can associate versioned delegate rules with their appropriate re-versioned parents when applicable
	Map<Long, RuleBaseValues> oldIdNewRuleMapping = new HashMap<Long, RuleBaseValues>();
	Map<Long, RuleBaseValues> rulesToVersion = new HashMap<Long, RuleBaseValues>();
	for (RuleBaseValues existingRule : existingRules) {
	    if (!shouldChangeRuleInvolvement(documentId, existingRule)) {
		continue;
	    }
	    List<RuleResponsibility> finalResponsibilities = new ArrayList<RuleResponsibility>();
	    RuleVersion ruleVersion = createNewRemoveReplaceVersion(existingRule, oldIdNewRuleMapping, documentId);
	    boolean modified = false;
	    for (RuleResponsibility responsibility : (List<RuleResponsibility>)ruleVersion.rule.getResponsibilities()) {
		if (responsibility.isUsingWorkflowUser()) {
		    if (userToRemove != null && responsibility.getRuleResponsibilityName().equals(userToRemove.getWorkflowId())) {
			modified = true;
			continue;
		    }
		} else if (responsibility.isUsingWorkgroup()) {
		    if (workgroupToRemove != null && responsibility.getRuleResponsibilityName().equals(workgroupToRemove.getWorkflowGroupId().getGroupId().toString())) {
			modified = true;
			continue;
		    }
		}
		finalResponsibilities.add(responsibility);
	    }
	    if (modified) {
		// if this is a delegation rule, we need to hook it up to the parent rule
		if (ruleVersion.parent != null && ruleVersion.delegation != null) {
		    hookUpDelegateRuleToParentRule(ruleVersion.parent, ruleVersion.rule, ruleVersion.delegation);
		}
		if (finalResponsibilities.isEmpty()) {
		    // deactivate the rule instead
		    ruleVersion.rule.setActiveInd(false);
		} else {
		    ruleVersion.rule.setResponsibilities(finalResponsibilities);
		}
		try {
		    save2(ruleVersion.rule, ruleVersion.delegation, false);
		    if (ruleVersion.delegation != null) {
			KEWServiceLocator.getRuleDelegationService().save(ruleVersion.delegation);
		    }
		    rulesToVersion.put(ruleVersion.rule.getRuleBaseValuesId(), ruleVersion.rule);
		    if (ruleVersion.parent != null) {
			save2(ruleVersion.parent, null, false);
			rulesToVersion.put(ruleVersion.parent.getRuleBaseValuesId(), ruleVersion.parent);
		    }
		} catch (Exception e) {
		    throw new WorkflowRuntimeException(e);
		}
	    }
	}
	makeCurrent2(new ArrayList<RuleBaseValues>(rulesToVersion.values()));
    }

    protected List<RuleBaseValues> loadRules(List<Long> ruleIds) {
	List<RuleBaseValues> rules = new ArrayList<RuleBaseValues>();
	for (Long ruleId : ruleIds) {
	    RuleBaseValues rule = KEWServiceLocator.getRuleService().findRuleBaseValuesById(ruleId);
	    rules.add(rule);
	}
	return rules;
    }

    public void replaceRuleInvolvement(Id entityToBeReplaced, Id newEntity, List<Long> ruleIds, Long documentId) throws WorkflowException {
	WorkflowUser userToReplace = null;
	Workgroup workgroupToReplace = null;
	if (entityToBeReplaced instanceof UserId) {
	    userToReplace = KEWServiceLocator.getUserService().getWorkflowUser((UserId)entityToBeReplaced);
	} else if (entityToBeReplaced instanceof GroupId) {
	    workgroupToReplace = KEWServiceLocator.getWorkgroupService().getWorkgroup((GroupId)entityToBeReplaced);
	} else {
	    throw new WorkflowRuntimeException("Invalid ID for entity to be replaced was passed, type was: " + entityToBeReplaced);
	}
	if (userToReplace == null && workgroupToReplace == null) {
	    throw new WorkflowRuntimeException("Could not resolve entity to be replaced with id: " + entityToBeReplaced);
	}
	WorkflowUser newUser = null;
	Workgroup newWorkgroup = null;
	if (newEntity instanceof UserId) {
	    newUser = KEWServiceLocator.getUserService().getWorkflowUser((UserId)newEntity);
	} else if (newEntity instanceof GroupId) {
	    newWorkgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup((GroupId)newEntity);
	} else {
	    throw new WorkflowRuntimeException("Invalid ID for new replacement entity was passed, type was: " + newEntity);
	}
	if (newUser == null && newWorkgroup == null) {
	    throw new WorkflowRuntimeException("Could not resolve new replacement entity with id: " + newEntity);
	}
	List<RuleBaseValues> existingRules = loadRules(ruleIds);
	// sort the rules so that delegations are last, very important in order to deal with parent-child versioning properly
	Collections.sort(existingRules, ComparatorUtils.reversedComparator(new RuleDelegationSorter()));
	// we maintain the old-new mapping so we can associate versioned delegate rules with their appropriate re-versioned parents when applicable
	Map<Long, RuleBaseValues> oldIdNewRuleMapping = new HashMap<Long, RuleBaseValues>();
	Map<Long, RuleBaseValues> rulesToVersion = new HashMap<Long, RuleBaseValues>();
	for (RuleBaseValues existingRule : existingRules) {
	    if (!shouldChangeRuleInvolvement(documentId, existingRule)) {
		continue;
	    }
	    RuleVersion ruleVersion = createNewRemoveReplaceVersion(existingRule, oldIdNewRuleMapping, documentId);
	    RuleBaseValues rule = ruleVersion.rule;
	    boolean modified = false;
	    for (RuleResponsibility responsibility : (List<RuleResponsibility>)rule.getResponsibilities()) {
		if (responsibility.isUsingWorkflowUser()) {
		    if (userToReplace != null && responsibility.getRuleResponsibilityName().equals(userToReplace.getWorkflowId())) {
			if (newUser != null) {
			    responsibility.setRuleResponsibilityType(EdenConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
			    responsibility.setRuleResponsibilityName(newUser.getWorkflowId());
			    modified = true;
			} else if (newWorkgroup != null) {
			    responsibility.setRuleResponsibilityType(EdenConstants.RULE_RESPONSIBILITY_WORKGROUP_ID);
			    responsibility.setRuleResponsibilityName(newWorkgroup.getWorkflowGroupId().getGroupId().toString());
			    modified = true;
			}
		    }
		} else if (responsibility.isUsingWorkgroup()) {
		    if (workgroupToReplace != null && responsibility.getRuleResponsibilityName().equals(workgroupToReplace.getWorkflowGroupId().getGroupId().toString())) {
			if (newUser != null) {
			    responsibility.setRuleResponsibilityType(EdenConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
			    responsibility.setRuleResponsibilityName(newUser.getWorkflowId());
			    modified = true;
			} else if (newWorkgroup != null) {
			    responsibility.setRuleResponsibilityType(EdenConstants.RULE_RESPONSIBILITY_WORKGROUP_ID);
			    responsibility.setRuleResponsibilityName(newWorkgroup.getWorkflowGroupId().getGroupId().toString());
			    modified = true;
			}
		    }
		}
	    }
	    if (modified) {
		try {
		    // if this is a delegation rule, we need to hook it up to the parent rule
		    if (ruleVersion.parent != null && ruleVersion.delegation != null) {
			hookUpDelegateRuleToParentRule(ruleVersion.parent, ruleVersion.rule, ruleVersion.delegation);
		    }
		    save2(ruleVersion.rule, ruleVersion.delegation, false);
		    if (ruleVersion.delegation != null) {
			KEWServiceLocator.getRuleDelegationService().save(ruleVersion.delegation);
		    }
		    rulesToVersion.put(ruleVersion.rule.getRuleBaseValuesId(), ruleVersion.rule);
		    if (ruleVersion.parent != null) {
			save2(ruleVersion.parent, null, false);
			rulesToVersion.put(ruleVersion.parent.getRuleBaseValuesId(), ruleVersion.parent);
		    }

		} catch (Exception e) {
		    throw new WorkflowRuntimeException(e);
		}
	    }
	}

	makeCurrent2(new ArrayList<RuleBaseValues>(rulesToVersion.values()));

    }

    /**
     * If a rule has been modified and is no longer current since the original request was made, we need to
     * be sure to NOT update the rule.
     */
    protected boolean shouldChangeRuleInvolvement(Long documentId, RuleBaseValues rule) {
	if (!rule.getCurrentInd()) {
	    LOG.warn("Rule requested for rule involvement change by document " + documentId + " is no longer current.  Change will not be executed!  Rule id is: " + rule.getRuleBaseValuesId());
	    return false;
	}
	Long lockingDocumentId = KEWServiceLocator.getRuleService().isLockedForRouting(rule.getRuleBaseValuesId());
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

    /**
     * This is essentially the code required to create a new version of a rule.  While this is being done in the Rule
     * GUI It's not sufficiently abstracted at the web-tier so we must essentially re-implement it here :(
     * This would be a good place to do some work to clean this up.
     *
     * <p>The oldNewMapping which is passed in allows us to attach the existing rule that gets reversioned to an already re-versioned
     * parent rule in the case where that is applicable.  This case would occur whenever both a parent and one or more of it's delegate rules are changed
     * as part of the same transaction.
     */
    protected RuleVersion createNewRemoveReplaceVersion(RuleBaseValues existingRule, Map<Long, RuleBaseValues> originalIdNewRuleMapping, Long documentId) {
	try {
	    // first check if the rule has already been re-versioned
	    RuleBaseValues rule = null;
	    if (originalIdNewRuleMapping.containsKey(existingRule.getRuleBaseValuesId())) {
		rule = originalIdNewRuleMapping.get(existingRule.getRuleBaseValuesId());
	    } else {
		rule = createNewRuleVersion(existingRule, documentId);
	    }

	    RuleVersion ruleVersion = new RuleVersion();
	    RuleBaseValues existingParentRule = null;
	    RuleBaseValues newParentRule = null;
	    // if it's a delegate rule, we need to find the appropriate parent and attach it
	    if (rule.getDelegateRule()) {
		RuleDelegation existingBaseDelegation = getRuleDelegationForDelegateRule(existingRule);
		ruleVersion.delegation = existingBaseDelegation;
		existingParentRule = existingBaseDelegation.getRuleResponsibility().getRuleBaseValues();
		if (originalIdNewRuleMapping.containsKey(existingParentRule.getRuleBaseValuesId())) {
		    newParentRule = originalIdNewRuleMapping.get(existingParentRule.getRuleBaseValuesId());
		} else {
		    // re-version the parent rule
		    newParentRule = createNewRuleVersion(existingParentRule, documentId);
		}
	    }

	    // put our newly created rules into the map
	    originalIdNewRuleMapping.put(existingRule.getRuleBaseValuesId(), rule);
	    if (existingParentRule != null && !originalIdNewRuleMapping.containsKey(existingParentRule.getRuleBaseValuesId())) {
		originalIdNewRuleMapping.put(existingParentRule.getRuleBaseValuesId(), newParentRule);
	    }

	    ruleVersion.rule = rule;
	    ruleVersion.parent = newParentRule;
	    return ruleVersion;
	} catch (Exception e) {
	    if (e instanceof RuntimeException) {
		throw (RuntimeException)e;
	    }
	    throw new WorkflowRuntimeException(e);
	}
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

    protected RuleBaseValues createNewRuleVersion(RuleBaseValues existingRule, Long documentId) throws Exception {
	RuleBaseValues rule = new RuleBaseValues();
	    PropertyUtils.copyProperties(rule, existingRule);
	    rule.setPreviousVersion(existingRule);
	    rule.setPreviousVersionId(existingRule.getRuleBaseValuesId());
	    rule.setRuleBaseValuesId(null);
	    rule.setActivationDate(null);
	    rule.setDeactivationDate(null);
	    rule.setLockVerNbr(0);
	    rule.setRouteHeaderId(documentId);
	    rule.setResponsibilities(new ArrayList());
	    for (RuleResponsibility existingResponsibility : (List<RuleResponsibility>)existingRule.getResponsibilities()) {
		RuleResponsibility responsibility = new RuleResponsibility();
		PropertyUtils.copyProperties(responsibility, existingResponsibility);
		responsibility.setRuleBaseValues(rule);
		responsibility.setRuleBaseValuesId(null);
		responsibility.setRuleResponsibilityKey(null);
		responsibility.setLockVerNbr(0);
		rule.getResponsibilities().add(responsibility);
		responsibility.setDelegationRules(new ArrayList());
		for (RuleDelegation existingDelegation : (List<RuleDelegation>)existingResponsibility.getDelegationRules()) {
		    RuleDelegation delegation = new RuleDelegation();
		    PropertyUtils.copyProperties(delegation, existingDelegation);
		    delegation.setRuleDelegationId(null);
		    delegation.setRuleResponsibility(responsibility);
		    delegation.setRuleResponsibilityId(null);
		    delegation.setLockVerNbr(0);
		    // it's very important that we do NOT recurse down into the delegation rules and reversion those,
		    // this is important to how rule versioning works
		    responsibility.getDelegationRules().add(delegation);
		}
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
    		String constant = Utilities.getApplicationConstant(EdenConstants.RULE_CUSTOM_DOC_TYPES_KEY);
    		if (!StringUtils.isEmpty(constant)) {
    			String[] ruleConfigs = constant.split(",");
    			for (int index = 0; index < ruleConfigs.length; index++) {
    				String[] configElements = ruleConfigs[index].split(":");
    				if (configElements.length != 4) {
    					throw new RuntimeException("Found incorrect number of config elements within a section of the custom rule document types config.  There should have been four ':' delimited sections!  " + ruleConfigs[index]);
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

}