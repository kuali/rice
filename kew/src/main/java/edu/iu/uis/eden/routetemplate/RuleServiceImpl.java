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

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import edu.iu.uis.eden.EdenConstants;
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
        PerformanceLogger performanceLogger = new PerformanceLogger();
        List rules = findByRouteHeaderId(routeHeaderId);
        boolean isGenerateRuleArs = EdenConstants.YES_RULE_CHANGE_AR_GENERATION_VALUE.equalsIgnoreCase(getApplicationConstantsService().findByName(EdenConstants.RULE_CHANGE_AR_GENERATION_KEY).getApplicationConstantValue());
        boolean isGenerateDelegateArs = EdenConstants.YES_DELEGATE_CHANGE_AR_GENERATION_VALUE.equalsIgnoreCase(getApplicationConstantsService().findByName(EdenConstants.DELEGATE_CHANGE_AR_GENERATION_KEY).getApplicationConstantValue());
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
                    responsibilityIds.addAll(getResponsibilityIdsFromGraph(oldRule, isGenerateRuleArs, isGenerateDelegateArs));
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
                    responsibilityIds.addAll(getResponsibilityIdsFromGraph(delegationRule, isGenerateRuleArs, isGenerateDelegateArs));
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
    	KEWServiceLocator.getCacheAdministrator().putInCache(getRuleCacheKey(ruleTemplateName, documentTypeName), rules, getDocumentTypeRuleCacheGroupName(documentTypeName));
    }

    protected void flushDocumentTypeFromCache(String documentTypeName) {
    	LOG.info("Flushing DocumentType from Cache for the given name: " + documentTypeName);
    	KEWServiceLocator.getCacheAdministrator().flushGroup(getDocumentTypeRuleCacheGroupName(documentTypeName));
    }

    protected void flushListFromCache(String ruleTemplateName, String documentTypeName) {
    	LOG.info("Flushing rules from Cache for ruleTemplate='" + ruleTemplateName + "' and documentType='" + documentTypeName + "'");
    	KEWServiceLocator.getCacheAdministrator().flushEntry(getRuleCacheKey(ruleTemplateName, documentTypeName));
    }

    private Set getResponsibilityIdsFromGraph(RuleBaseValues rule, boolean isRuleCollecting, boolean isDelegationCollecting) {
        Set responsibilityIds = new HashSet();
        for (Iterator iterator = rule.getResponsibilities().iterator(); iterator.hasNext();) {
            RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
            if (isRuleCollecting) {
            	responsibilityIds.add(responsibility.getResponsibilityId());
            }
            // TODO why is this commented out? - Good question.
            //            if (isDelegationCollecting && responsibility.isDelegating()) {
            //                for (Iterator iter =
            // responsibility.getDelegationRules().iterator(); iter.hasNext();)
            // {
            //                    RuleDelegation ruleDelegation = (RuleDelegation) iter.next();
            //                    for (Iterator iterator2 =
            // ruleDelegation.getDelegationRuleBaseValues().getResponsibilities().iterator();
            // iterator2.hasNext();) {
            //                        RuleResponsibility delegationResp = (RuleResponsibility)
            // iterator2.next();
            //                        responsibilityIds.add(delegationResp.getRuleResponsibilityId());
            //                    }
            //                }
            //            }
        }
        return responsibilityIds;
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
        boolean isRoutingParent = parentRule.getRuleBaseValuesId() == null;
        if (isRoutingParent) {
            save2(parentRule, null, true);
        }

        // XXX: added when the RuleValidation stuff was added, basically we just need to get the RuleDelegation
        // that points to our delegate rule, this rule code is scary...
        RuleDelegation ruleDelegation = getRuleDelegation(parentRule, delegateRule);

        save2(delegateRule, ruleDelegation, true);
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
				// they should be the same object in memory...I think...I'm going to try and unit test some
				// of this stuff because I don't like trying to divine what this stuff is doing everytime
				// I look at it
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
        
        for (Iterator iter = ruleBaseValues.getRuleTemplate().getRuleTemplateAttributes().iterator(); iter.hasNext();) {
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
            String roleName, Boolean delegateRule, Boolean activeInd, Map extensionValues) {
        return getRuleDAO().search(docTypeName, ruleId, ruleTemplateId, ruleDescription, workgroupId, workflowId, roleName, delegateRule,
                activeInd, extensionValues);
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