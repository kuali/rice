/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.rice.kew.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.kuali.rice.kew.dto.ActionItemDTO;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.ActionTakenDTO;
import org.kuali.rice.kew.dto.DocumentContentDTO;
import org.kuali.rice.kew.dto.DocumentDetailDTO;
import org.kuali.rice.kew.dto.DocumentSearchCriteriaDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultDTO;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.dto.ReportCriteriaDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.dto.RuleDTO;
import org.kuali.rice.kew.dto.RuleReportCriteriaDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeValidationErrorDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;


/**
 * A remotable service which provides an API for performing various queries
 * and other utilities on KEW.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface WorkflowUtility {
    public RouteHeaderDTO getRouteHeaderWithPrincipal(String principalId, Long documentId) throws WorkflowException;
    public RouteHeaderDTO getRouteHeader(Long documentId) throws WorkflowException;
    public DocumentDetailDTO getDocumentDetail(Long documentId) throws WorkflowException;
    public RouteNodeInstanceDTO getNodeInstance(Long nodeInstanceId) throws WorkflowException;
    public DocumentTypeDTO getDocumentType(Long documentTypeId) throws WorkflowException;
    public DocumentTypeDTO getDocumentTypeByName(String documentTypeName) throws WorkflowException;
    public Long getNewResponsibilityId() throws WorkflowException;
    public Integer getUserActionItemCount(String principalId) throws WorkflowException;
    public ActionItemDTO[] getActionItems(Long routeHeaderId) throws WorkflowException;
    public ActionItemDTO[] getActionItems(Long routeHeaderId, String[] actionRequestedCodes) throws WorkflowException;
    public ActionRequestDTO[] getActionRequests(Long documentId) throws WorkflowException;
    public ActionRequestDTO[] getActionRequests(Long routeHeaderId, String nodeName, String principalId) throws WorkflowException;
    public ActionTakenDTO[] getActionsTaken(Long documentId) throws WorkflowException;
    public WorkflowAttributeValidationErrorDTO[] validateWorkflowAttributeDefinitionVO(WorkflowAttributeDefinitionDTO definition) throws WorkflowException;
    public boolean isUserInRouteLog(Long documentId, String principalId, boolean lookFuture) throws WorkflowException;
    public void reResolveRole(String documentTypeName, String roleName, String qualifiedRoleNameLabel) throws WorkflowException;
    public void reResolveRoleByDocumentId(Long documentId, String roleName, String qualifiedRoleNameLabel) throws WorkflowException;
    public DocumentDetailDTO routingReport(ReportCriteriaDTO reportCriteria) throws WorkflowException;
    //public RouteHeaderDetailVO routingSimulation(RouteHeaderDetailVO detail, ActionTakenVO[] actionsToTake) throws WorkflowException;
    public boolean isFinalApprover(Long documentId, String principalId) throws WorkflowException;
    public boolean isSuperUserForDocumentType(String principalId, Long documentTypeId) throws WorkflowException;
    public DocumentSearchResultDTO performDocumentSearch(DocumentSearchCriteriaDTO criteriaVO) throws WorkflowException;
    public DocumentSearchResultDTO performDocumentSearch(String principalId, DocumentSearchCriteriaDTO criteriaVO) throws WorkflowException;

    // new in 2.3

    public RuleDTO[] ruleReport(RuleReportCriteriaDTO ruleReportCriteria) throws WorkflowException;

    // deprecated as of 2.1

    /**
     * @deprecated use isLastApproverAtNode instead
     */
    public boolean isLastApproverInRouteLevel(Long routeHeaderId, String principalId, Integer routeLevel) throws WorkflowException;

    /**
     * @deprecated use routeNodeHasApproverActionRequest instead
     */
    public boolean routeLevelHasApproverActionRequest(String docType, String docContent, Integer routeLevel) throws WorkflowException;

    // new in 2.1

    public boolean isLastApproverAtNode(Long documentId, String principalId, String nodeName) throws WorkflowException;
    public boolean routeNodeHasApproverActionRequest(String docType, String docContent, String nodeName) throws WorkflowException;
    public RouteNodeInstanceDTO[] getDocumentRouteNodeInstances(Long documentId) throws WorkflowException;
    public RouteNodeInstanceDTO[] getActiveNodeInstances(Long documentId) throws WorkflowException;
    public RouteNodeInstanceDTO[] getTerminalNodeInstances(Long documentId) throws WorkflowException;
    public DocumentContentDTO getDocumentContent(Long routeHeaderId) throws WorkflowException;

    //2.2
    public String[] getPreviousRouteNodeNames(Long documentId) throws WorkflowException;

    // 2.4
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO reportCriteriaDTO, String[] actionRequestedCodes, boolean ignoreCurrentActionRequests);

    /**
     * @deprecated use {@link #documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO, String[], boolean)} instead
     *
     * This method assumes both existing and generated requests should be taken into account
     */
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO reportCriteriaDTO, String[] actionRequestedCodes);

    /**
     * @since 0.9.1
     */
    public String getDocumentStatus(Long documentId) throws WorkflowException;
    public RouteNodeInstanceDTO[] getCurrentNodeInstances(Long documentId) throws WorkflowException;


    // added for KS per Scott
    ActionItemDTO[] getActionItemsForPrincipal(String principalId) throws WorkflowException;

    /**
     *
     * This method gets a list of ids of all principals who have a pending action request for a document.
     *
     * @param actionRequestedCd
     * @param routeHeaderId
     * @return
     * @throws WorkflowException
     */
    public List<String> getPrincipalIdsWithPendingActionRequestByActionRequestedAndDocId(
    		String actionRequestedCd, Long routeHeaderId) throws WorkflowException;

    /**
     * This method gets a list of ids of all principals in the route log -
     * - initiators,
     * - people who have taken action,
     * - people with a pending action request,
	 * - people who will receive an action request for the document in question
     *
     * @param routeHeaderId
     * @param lookFuture
     * @return
     * @throws WorkflowException
     */
    public List<String> getPrincipalIdsInRouteLog(Long routeHeaderId, boolean lookFuture) throws WorkflowException;

    /**
     * Returns the principal ID of the initiator of the given document.  <b>null</b> if the document can not be found.
     *
     * @throws WorkflowException
     */
    public String getDocumentInitiatorPrincipalId( Long routeHeaderId ) throws WorkflowException;

    /**
     * Returns the principal ID of the user who routed the given document.  <b>null</b> if the document can not be found.
     *
     * @throws WorkflowException
     */
    public String getDocumentRoutedByPrincipalId( Long routeHeaderId ) throws WorkflowException;

    public AttributeSet getActionsRequested(String principalId, Long documentId);

    /**
     *
     * This method does a direct search for the searchableAttribute without going through the doc search.
     *
     * @param documentId
     * @param key
     * @return
     */
    public List<String> getSearchableAttributeStringValuesByKey(Long documentId, String key);
    /**
     *
     * This method does a direct search for the searchableAttribute without going through the doc search.
     *
     * @param documentId
     * @param key
     * @return
     */
    public List<Timestamp> getSearchableAttributeDateTimeValuesByKey(Long documentId, String key);
    /**
     *
     * This method does a direct search for the searchableAttribute without going through the doc search.
     *
     * @param documentId
     * @param key
     * @return
     */
    public List<BigDecimal> getSearchableAttributeFloatValuesByKey(Long documentId, String key);
    /**
     *
     * This method does a direct search for the searchableAttribute without going through the doc search.
     *
     * @param documentId
     * @param key
     * @return
     */
    public List<Long> getSearchableAttributeLongValuesByKey(Long documentId, String key);
}