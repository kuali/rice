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
package edu.iu.uis.eden.documentoperation.web;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionlist.ActionListService;
import edu.iu.uis.eden.actionrequests.ActionRequestService;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.actionrequests.DocumentRequeuerService;
import edu.iu.uis.eden.actions.asyncservices.ActionInvocation;
import edu.iu.uis.eden.actions.asyncservices.ActionInvocationService;
import edu.iu.uis.eden.actions.asyncservices.BlanketApproveProcessorService;
import edu.iu.uis.eden.actions.asyncservices.MoveDocumentService;
import edu.iu.uis.eden.actiontaken.ActionTakenService;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.docsearch.SearchableAttributeProcessingService;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.engine.node.Branch;
import edu.iu.uis.eden.engine.node.BranchService;
import edu.iu.uis.eden.engine.node.BranchState;
import edu.iu.uis.eden.engine.node.NodeState;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.engine.node.RouteNodeService;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.messaging.KEWXMLService;
import edu.iu.uis.eden.messaging.MessageServiceNames;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.RouteHeaderService;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * Struts Action for doing editing of workflow documents.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentOperationAction extends WorkflowAction {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentOperationAction.class);
	private static final String DEFAULT_LOG_MSG = "Admin change via document operation";

	public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		return mapping.findForward("basic");
	}

	public ActionForward getDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		DocumentOperationForm docForm = (DocumentOperationForm) form;
		Long docId=new Long(docForm.getRouteHeaderId().trim());
		//to clear Document Field first;
        docForm.resetOps();
		DocumentRouteHeaderValue routeHeader = getRouteHeaderService().getRouteHeader(docId);
		List routeNodeInstances=getRouteNodeService().findRouteNodeInstances(docId);
		Map branches1=new HashMap();
		List branches=new ArrayList();

		if (routeHeader == null) {
			throw new WorkflowServiceErrorException("Document Not Found", new WorkflowServiceErrorImpl("Document Not Found", "docoperation.routeheaderid.invalid"));
		} else {
			materializeDocument(routeHeader);
			docForm.setRouteHeader(routeHeader);
			setRouteHeaderTimestampsToString(docForm);
			docForm.setRouteHeaderOp(EdenConstants.NOOP);
			docForm.setRouteHeaderId(docForm.getRouteHeaderId().trim());
			String initials="";
			for(Iterator lInitials=routeHeader.getInitialRouteNodeInstances().iterator();lInitials.hasNext();){
				Long initial=((RouteNodeInstance)lInitials.next()).getRouteNodeInstanceId();
				LOG.debug(initial);
				initials=initials+initial+", ";
			}
			if(initials.trim().length()>1){
				initials=initials.substring(0,initials.lastIndexOf(","));
			}
			docForm.setInitialNodeInstances(initials);
			request.getSession().setAttribute("routeNodeInstances",routeNodeInstances);
			docForm.setRouteNodeInstances(routeNodeInstances);
			if(routeNodeInstances!=null){
				Iterator routeNodeInstanceIter=routeNodeInstances.iterator();
			    while(routeNodeInstanceIter.hasNext()){
				   RouteNodeInstance routeNodeInstance=(RouteNodeInstance) routeNodeInstanceIter.next();
				   Branch branch=routeNodeInstance.getBranch();
				   if (! branches1.containsKey(branch.getName())){
					   branches1.put(branch.getName(),branch);
					   branches.add(branch);
					   LOG.debug(branch.getName()+"; "+branch.getBranchState());
				   }
				}
			    if(branches.size()<1){
			    	branches=null;
			    }
			}
			branches1.clear();
			request.getSession().setAttribute("branches",branches);
			docForm.setBranches(branches);

		}
		return mapping.findForward("basic");
	}

	/**
	 * Sets up various objects on the document which are required for use inside of the Struts and JSP framework.
	 *
	 * Specifically, if a document has action requests with null RouteNodeInstances, it will create empty node instance
	 * objects.
	 */
	private void materializeDocument(DocumentRouteHeaderValue document) {
		for (Iterator iterator = document.getActionRequests().iterator(); iterator.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iterator.next();
			if (request.getNodeInstance() == null) {
				request.setNodeInstance(new RouteNodeInstance());
			}
		}
	}

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		DocumentOperationForm docForm = (DocumentOperationForm) form;
		docForm.setRouteHeader(new DocumentRouteHeaderValue());
		docForm.setRouteHeaderId(null);
		return mapping.findForward("basic");
	}

	public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
		return null;
	}

	public ActionForward flushRuleCache(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	    KEWServiceLocator.getRuleService().flushRuleCache();
	    return mapping.findForward("basic");
	}

	public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentOperationForm docForm = (DocumentOperationForm) form;
		boolean change = false;

		String routeHeaderOp = docForm.getRouteHeaderOp();
		if (!EdenConstants.UPDATE.equals(routeHeaderOp) && !EdenConstants.NOOP.equals(routeHeaderOp)) {
			throw new WorkflowServiceErrorException("Document operation not defined", new WorkflowServiceErrorImpl("Document operation not defined", "docoperation.operation.invalid"));
		}
		if (EdenConstants.UPDATE.equals(routeHeaderOp)) {
			setRouteHeaderTimestamps(docForm);
			DocumentRouteHeaderValue dHeader=docForm.getRouteHeader();
			String initials=docForm.getInitialNodeInstances();
			List lInitials=new ArrayList();
			StringTokenizer tokenInitials=new StringTokenizer(initials,",");
			while (tokenInitials.hasMoreTokens()) {
		         Long instanceId=Long.valueOf(tokenInitials.nextToken().trim());
		         LOG.debug(instanceId);
		         RouteNodeInstance instance=getRouteNodeService().findRouteNodeInstanceById(instanceId);
		         lInitials.add(instance);
		     }
			dHeader.setInitialRouteNodeInstances(lInitials);
			getRouteHeaderService().validateRouteHeader(docForm.getRouteHeader());
			getRouteHeaderService().saveRouteHeader(docForm.getRouteHeader());
			change = true;
		}

		for (Iterator actionRequestIter = docForm.getActionRequestOps().iterator(); actionRequestIter.hasNext();) {
			DocOperationIndexedParameter actionRequestOp = (DocOperationIndexedParameter) actionRequestIter.next();
			int index = actionRequestOp.getIndex().intValue();
			String opValue = actionRequestOp.getValue();
			ActionRequestValue actionRequest = docForm.getRouteHeader().getDocActionRequest(index);
			String createDateParamName = "actionRequestCreateDate" + index;

			if (!EdenConstants.UPDATE.equals(opValue) && !EdenConstants.DELETE.equals(opValue) && !EdenConstants.NOOP.equals(opValue)) {
				throw new WorkflowServiceErrorException("Action request operation not defined", new WorkflowServiceErrorImpl("Action request operation not defined", "docoperation.actionrequest.operation.invalid"));
			}
			if (EdenConstants.UPDATE.equals(opValue)) {
				try {
					actionRequest.setCreateDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse(request.getParameter(createDateParamName)).getTime()));
					actionRequest.setCreateDateString(EdenConstants.getDefaultDateFormat().format(actionRequest.getCreateDate()));
					actionRequest.setRouteHeader(docForm.getRouteHeader());
					actionRequest.setParentActionRequest(getActionRequestService().findByActionRequestId(actionRequest.getParentActionRequestId()));
					actionRequest.setActionTaken(getActionTakenService().findByActionTakenId(actionRequest.getActionTakenId()));
					if (actionRequest.getNodeInstance() != null && actionRequest.getNodeInstance().getRouteNodeInstanceId() == null) {
						actionRequest.setNodeInstance(null);
					} else if (actionRequest.getNodeInstance() != null && actionRequest.getNodeInstance().getRouteNodeInstanceId() != null) {
						actionRequest.setNodeInstance(KEWServiceLocator.getRouteNodeService().findRouteNodeInstanceById(actionRequest.getNodeInstance().getRouteNodeInstanceId()));
					}
					// getActionRequestService().validateActionRequest(actionRequest);
					getActionRequestService().saveActionRequest(actionRequest);
					change = true;
				} catch (ParseException pe) {
					throw new WorkflowServiceErrorException("Action request create date parsing error", new WorkflowServiceErrorImpl("Action request create date parsing error", "docoperation.actionrequests.dateparsing.error", actionRequest.getActionRequestId().toString()));
				}

			}
			if (EdenConstants.DELETE.equals(opValue)) {
				for (Iterator childIter = actionRequest.getChildrenRequests().iterator(); childIter.hasNext();) {
					getActionRequestService().deleteByActionRequestId(((ActionRequestValue) childIter.next()).getActionRequestId());
				}
				getActionListService().deleteActionItems(actionRequest.getActionRequestId());
				if (actionRequest.getActionTakenId() != null) {
					ActionTakenValue actionTaken = getActionTakenService().findByActionTakenId(actionRequest.getActionTakenId());
					getActionTakenService().delete(actionTaken);
				}

				getActionRequestService().deleteByActionRequestId(actionRequest.getActionRequestId());
				change = true;
			}
		}

		for (Iterator actionTakenIter = docForm.getActionTakenOps().iterator(); actionTakenIter.hasNext();) {
			DocOperationIndexedParameter actionTakenOp = (DocOperationIndexedParameter) actionTakenIter.next();
			int index = actionTakenOp.getIndex().intValue();
			String opValue = actionTakenOp.getValue();

			String actionDateParamName = "actionTakenActionDate" + index;
			ActionTakenValue actionTaken = docForm.getRouteHeader().getDocActionTaken(index);
			if (!EdenConstants.UPDATE.equals(opValue) && !EdenConstants.DELETE.equals(opValue) && !EdenConstants.NOOP.equals(opValue)) {
				throw new WorkflowServiceErrorException("Action taken operation not defined", new WorkflowServiceErrorImpl("Action taken operation not defined", "docoperation.actiontaken.operation.invalid"));
			}
			if (EdenConstants.UPDATE.equals(opValue)) {
				try {
					actionTaken.setActionDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse(request.getParameter(actionDateParamName)).getTime()));
					actionTaken.setActionDateString(EdenConstants.getDefaultDateFormat().format(actionTaken.getActionDate()));
					// getActionTakenService().validateActionTaken(actionTaken);
					getActionTakenService().saveActionTaken(actionTaken);
					change = true;
				} catch (ParseException pe) {
					throw new WorkflowServiceErrorException("Action taken action date parsing error", new WorkflowServiceErrorImpl("Action taken action date parse error", "docoperation.actionstaken.dateparsing.error", actionTaken.getActionTakenId().toString()));
				}
			}
			if (EdenConstants.DELETE.equals(opValue)) {
				getActionTakenService().delete(actionTaken);
				change = true;
			}
		}

		for (Iterator actionItemIter = docForm.getActionItemOps().iterator(); actionItemIter.hasNext();) {
			DocOperationIndexedParameter actionItemOp = (DocOperationIndexedParameter) actionItemIter.next();
			int index = actionItemOp.getIndex().intValue();
			String opValue = actionItemOp.getValue();

			String dateAssignedParamName = "actionItemDateAssigned" + index;
			ActionItem actionItem = docForm.getRouteHeader().getDocActionItem(index);
			if (!EdenConstants.UPDATE.equals(opValue) && !EdenConstants.DELETE.equals(opValue) && !EdenConstants.NOOP.equals(opValue)) {
				throw new WorkflowServiceErrorException("Action Item operation not defined", new WorkflowServiceErrorImpl("Action Item operation not defined", "docoperation.operation.invalid"));
			}
			if (EdenConstants.UPDATE.equals(opValue)) {
				try {
					actionItem.setDateAssigned(new Timestamp(EdenConstants.getDefaultDateFormat().parse(request.getParameter(dateAssignedParamName)).getTime()));
					actionItem.setDateAssignedString(EdenConstants.getDefaultDateFormat().format(actionItem.getDateAssigned()));
					actionItem.setRouteHeader(docForm.getRouteHeader());
					// getActionItemService().validateActionItem(actionItem);
					getActionListService().saveActionItem(actionItem);
					change = true;
				} catch (ParseException pe) {
					throw new WorkflowServiceErrorException("Action item date assigned parsing error", new WorkflowServiceErrorImpl("Action item date assigned parse error", "docoperation.actionitem.dateassignedparsing.error", actionItem.getActionItemId().toString()));
				}
			}
			if (EdenConstants.DELETE.equals(opValue)) {
				getActionListService().deleteActionItem(actionItem);
				change = true;
			}
		}

		List routeNodeInstances=(List)(request.getSession().getAttribute("routeNodeInstances"));
		String ids=docForm.getNodeStatesDelete().trim();
		List statesToBeDeleted=new ArrayList();
		if(ids!=null||!ids.equals("")){
		    StringTokenizer idSets=new StringTokenizer(ids);
		    while (idSets.hasMoreTokens()) {
		    	String id=idSets.nextToken().trim();
		    	statesToBeDeleted.add(Long.valueOf(id));
		     }
		}

		for (Iterator routeNodeInstanceIter = docForm.getRouteNodeInstanceOps().iterator(); routeNodeInstanceIter.hasNext();) {
			DocOperationIndexedParameter routeNodeInstanceOp = (DocOperationIndexedParameter) routeNodeInstanceIter.next();
			int index = routeNodeInstanceOp.getIndex().intValue();
			String opValue = routeNodeInstanceOp.getValue();
            LOG.debug(opValue);
			RouteNodeInstance routeNodeInstance = (RouteNodeInstance)(routeNodeInstances.get(index));
			RouteNodeInstance routeNodeInstanceNew = (RouteNodeInstance)(docForm.getRouteNodeInstance(index));
			if (!EdenConstants.UPDATE.equals(opValue) && !EdenConstants.DELETE.equals(opValue) && !EdenConstants.NOOP.equals(opValue)) {
				throw new WorkflowServiceErrorException("Route Node Instance Operation not defined", new WorkflowServiceErrorImpl("Route Node Instance Operation not defined", "docoperation.routenodeinstance.operation.invalid"));
			}
			if (EdenConstants.UPDATE.equals(opValue)) {
				//LOG.debug("saving routeNodeInstance:"+routeNodeInstance.getRouteNodeInstanceId());
				//getRouteNodeService().save(routeNodeInstance);
				routeNodeInstance.setActive(routeNodeInstanceNew.isActive());
				LOG.debug(Boolean.toString(routeNodeInstanceNew.isActive()));
				routeNodeInstance.setComplete(routeNodeInstanceNew.isComplete());
				routeNodeInstance.setInitial(routeNodeInstanceNew.isInitial());
				List nodeStates=routeNodeInstance.getState();
				List nodeStatesNew=routeNodeInstanceNew.getState();

				if(nodeStates!=null){
					for(int i=0;i<nodeStates.size();i++){
					   NodeState nodeState=(NodeState)nodeStates.get(i);
					   NodeState nodeStateNew=(NodeState)nodeStatesNew.get(i);
					   if(nodeStateNew.getKey()!=null && ! nodeStateNew.getKey().trim().equals("")){
					   nodeState.setKey(nodeStateNew.getKey());
					   LOG.debug(nodeState.getKey());
					   nodeState.setValue(nodeStateNew.getValue());
					   LOG.debug(nodeState.getValue());
					   }
				    }
				}
				getRouteNodeService().save(routeNodeInstance);
				LOG.debug("saved");
				change = true;
			}


			if (EdenConstants.DELETE.equals(opValue)) {
				List nodeStates=routeNodeInstance.getState();
				List nodeStatesNew=routeNodeInstanceNew.getState();

				if(nodeStates!=null){
					for(int i=0;i<nodeStates.size();i++){
					   NodeState nodeState=(NodeState)nodeStates.get(i);
					   NodeState nodeStateNew=(NodeState)nodeStatesNew.get(i);
					   if(nodeStateNew.getKey()==null || nodeStateNew.getKey().trim().equals("")){
					     statesToBeDeleted.remove(nodeState.getNodeStateId());
					   }
				    }
				}
				getRouteNodeService().deleteByRouteNodeInstance(routeNodeInstance);
				LOG.debug(routeNodeInstance.getRouteNodeInstanceId()+" is deleted");
				change = true;
				break;
			}

			if (EdenConstants.NOOP.equals(opValue)){
				routeNodeInstanceNew.setActive(routeNodeInstance.isActive());
				routeNodeInstanceNew.setComplete(routeNodeInstance.isComplete());
				routeNodeInstanceNew.setInitial(routeNodeInstance.isInitial());
				List nodeStates=routeNodeInstance.getState();
				List nodeStatesNew=routeNodeInstanceNew.getState();
				if(nodeStates!=null){
				   for(int i=0;i<nodeStates.size();i++){
					   NodeState nodeState=(NodeState)nodeStates.get(i);
					   NodeState nodeStateNew=(NodeState)nodeStatesNew.get(i);
					   if(nodeStateNew.getKey()==null || nodeStateNew.getKey().trim().equals("")){
						     statesToBeDeleted.remove(nodeState.getNodeStateId());
					   }
					   nodeStateNew.setKey(nodeState.getKey());
					   nodeStateNew.setValue(nodeState.getValue());
				   }
				}
			}

			//((DocOperationIndexedParameter)(docForm.getRouteNodeInstanceOps().get(index))).setValue(EdenConstants.NOOP);
		}

		if(statesToBeDeleted!=null && statesToBeDeleted.size()>0){
			getRouteNodeService().deleteNodeStates(statesToBeDeleted);
		}


		List branches=(List)(request.getSession().getAttribute("branches"));
		String branchStateIds=docForm.getBranchStatesDelete().trim();
		List branchStatesToBeDeleted=new ArrayList();
		if(branchStateIds!=null||!branchStateIds.equals("")){
		    StringTokenizer idSets=new StringTokenizer(branchStateIds);
		    while (idSets.hasMoreTokens()) {
		    	String id=idSets.nextToken().trim();
		    	branchStatesToBeDeleted.add(Long.valueOf(id));
		    }
		}

		for (Iterator branchesOpIter = docForm.getBranchOps().iterator(); branchesOpIter.hasNext();) {
			DocOperationIndexedParameter branchesOp = (DocOperationIndexedParameter) branchesOpIter.next();
			int index = branchesOp.getIndex().intValue();
			String opValue = branchesOp.getValue();
            LOG.debug(opValue);
			Branch branch = (Branch)(branches.get(index));
			Branch branchNew = (Branch)(docForm.getBranche(index));
			if (!EdenConstants.UPDATE.equals(opValue) && !EdenConstants.NOOP.equals(opValue)) {
				throw new WorkflowServiceErrorException("Route Node Instance Operation not defined", new WorkflowServiceErrorImpl("Route Node Instance Operation not defined", "docoperation.routenodeinstance.operation.invalid"));
			}
			if (EdenConstants.UPDATE.equals(opValue)) {
				//LOG.debug("saving routeNodeInstance:"+routeNodeInstance.getRouteNodeInstanceId());
				//getRouteNodeService().save(routeNodeInstance);
				branch.setName(branchNew.getName());
				List branchStates=branch.getBranchState();
				List branchStatesNew=branchNew.getBranchState();
				if(branchStates!=null){
				   for(int i=0;i<branchStates.size();i++){
					   BranchState branchState=(BranchState)branchStates.get(i);
					   BranchState branchStateNew=(BranchState)branchStatesNew.get(i);
					   if(branchStateNew.getKey()!=null && ! branchStateNew.getKey().trim().equals("")){
					   branchState.setKey(branchStateNew.getKey());
					   LOG.debug(branchState.getKey());
					   branchState.setValue(branchStateNew.getValue());
					   LOG.debug(branchState.getValue());
					   }
				   }
				}
				getBranchService().save(branch);
				LOG.debug("branch saved");
				change = true;

			}


			if (EdenConstants.NOOP.equals(opValue)){
				branchNew.setName(branch.getName());
				List branchStates=branch.getBranchState();
				List branchStatesNew=branchNew.getBranchState();
				if(branchStates!=null){
				   for(int i=0;i<branchStates.size();i++){
					   BranchState branchState=(BranchState)branchStates.get(i);
					   BranchState branchStateNew=(BranchState)branchStatesNew.get(i);
					   if(branchStateNew.getKey()==null || branchStateNew.getKey().trim().equals("")){
						   branchStatesToBeDeleted.remove(branchState.getBranchStateId());
					   }
					   branchStateNew.setKey(branchState.getKey());
					   LOG.debug(branchState.getKey());
					   branchStateNew.setValue(branchState.getValue());
					   LOG.debug(branchState.getValue());
				   }
				}
			}
			//((DocOperationIndexedParameter)(docForm.getBranchOps().get(index))).setValue(EdenConstants.NOOP);
		}

		if(branchStatesToBeDeleted!=null && branchStatesToBeDeleted.size()>0){
			getBranchService().deleteBranchStates(branchStatesToBeDeleted);
		}


		WorkflowDocument flexDoc = new WorkflowDocument(new NetworkIdVO(getUserSession(request).getWorkflowUser().getAuthenticationUserId().getAuthenticationId()), new Long(docForm.getRouteHeaderId()));
		String annotation = docForm.getAnnotation();
		if (StringUtils.isEmpty(annotation)) {
			annotation = DEFAULT_LOG_MSG;
		}
		flexDoc.logDocumentAction(annotation);

		ActionMessages messages = new ActionMessages();
		String forward = null;
		if (change) {
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("docoperation.operation.saved"));
			docForm.setRouteHeader(getRouteHeaderService().getRouteHeader(docForm.getRouteHeader().getRouteHeaderId()));
			forward = "summary";
		} else {
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("docoperation.operation.noop"));
			forward = "basic";
		}
		saveMessages(request, messages);
		return mapping.findForward(forward);

	}

	private RouteHeaderService getRouteHeaderService() {
		return (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
	}

	private RouteNodeService getRouteNodeService(){
		return (RouteNodeService) KEWServiceLocator.getService(KEWServiceLocator.ROUTE_NODE_SERVICE);
	}

	private ActionRequestService getActionRequestService() {
		return (ActionRequestService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_REQUEST_SRV);
	}

	private ActionTakenService getActionTakenService() {
		return (ActionTakenService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_TAKEN_SRV);
	}

	private ActionListService getActionListService() {
		return (ActionListService) KEWServiceLocator.getActionListService();
	}

	private void setRouteHeaderTimestamps(DocumentOperationForm docForm) {
		if (docForm.getCreateDate() == null || docForm.getCreateDate().trim().equals("")) {
			throw new WorkflowServiceErrorException("Document create date empty", new WorkflowServiceErrorImpl("Document create date empty", "docoperation.routeheader.createdate.empty"));
		} else {
			try {
				docForm.getRouteHeader().setCreateDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse(docForm.getCreateDate()).getTime()));
			} catch (ParseException pe) {
				throw new WorkflowServiceErrorException("RouteHeader create date parsing error", new WorkflowServiceErrorImpl("Date parsing error", "docoperation.routeheader.createdate.invalid"));
			}
		}

		if (docForm.getStatusModDate() == null || docForm.getStatusModDate().trim().equals("")) {
			throw new WorkflowServiceErrorException("Document doc status mod date empty", new WorkflowServiceErrorImpl("Document doc status mod date empty", "docoperation.routeheader.statusmoddate.empty"));
		} else {
			try {
				docForm.getRouteHeader().setStatusModDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse(docForm.getStatusModDate()).getTime()));
			} catch (ParseException pe) {
				throw new WorkflowServiceErrorException("Document doc status date parsing error", new WorkflowServiceErrorImpl("Document doc status mod date parsing error", "docoperation.routeheader.statusmoddate.invalid"));
			}
		}

		if (docForm.getApprovedDate() != null && !docForm.getApprovedDate().trim().equals("")) {
			try {
				docForm.getRouteHeader().setApprovedDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse(docForm.getApprovedDate()).getTime()));
			} catch (ParseException pe) {
				throw new WorkflowServiceErrorException("Document approved date parsing error", new WorkflowServiceErrorImpl("Document approved date parsing error", "docoperation.routeheader.approveddate.invalid"));
			}

		}

		if (docForm.getFinalizedDate() != null && !docForm.getFinalizedDate().trim().equals("")) {
			try {
				docForm.getRouteHeader().setFinalizedDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse(docForm.getFinalizedDate()).getTime()));
			} catch (ParseException pe) {
				throw new WorkflowServiceErrorException("Document finalized date parsing error", new WorkflowServiceErrorImpl("Document finalized date parsing error", "docoperation.routeheader.finalizeddate.invalid"));
			}
		}

		if (docForm.getRouteStatusDate() != null && !docForm.getRouteStatusDate().trim().equals("")) {
			try {
				docForm.getRouteHeader().setRouteStatusDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse(docForm.getRouteStatusDate()).getTime()));
			} catch (ParseException pe) {
				throw new WorkflowServiceErrorException("Document route status date parsing error", new WorkflowServiceErrorImpl("Document route status date parsing error", "docoperation.routeheader.routestatusdate.invalid"));
			}

		}

		if (docForm.getRouteLevelDate() != null && !docForm.getRouteLevelDate().trim().equals("")) {
			try {
				docForm.getRouteHeader().setRouteLevelDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse(docForm.getRouteLevelDate()).getTime()));
			} catch (ParseException pe) {
				throw new WorkflowServiceErrorException("Document route level date parsing error", new WorkflowServiceErrorImpl("Document route level date parsing error", "docoperation.routeheader.routeleveldate.invalid"));
			}
		}
	}

	private void setRouteHeaderTimestampsToString(DocumentOperationForm docForm) {
		try {
			docForm.setCreateDate(EdenConstants.getDefaultDateFormat().format(docForm.getRouteHeader().getCreateDate()));
			docForm.setStatusModDate(EdenConstants.getDefaultDateFormat().format(docForm.getRouteHeader().getStatusModDate()));
			docForm.setApprovedDate(EdenConstants.getDefaultDateFormat().format(docForm.getRouteHeader().getApprovedDate()));
			docForm.setFinalizedDate(EdenConstants.getDefaultDateFormat().format(docForm.getRouteHeader().getFinalizedDate()));
			docForm.setRouteStatusDate(EdenConstants.getDefaultDateFormat().format(docForm.getRouteHeader().getRouteStatusDate()));
			docForm.setRouteLevelDate(EdenConstants.getDefaultDateFormat().format(docForm.getRouteHeader().getRouteLevelDate()));

		} catch (Exception e) {
			LOG.info("One or more of the dates in routeHeader may be null");
		}
	}

	public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentOperationForm docForm = (DocumentOperationForm) form;
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + mapping.getModuleConfig().getPrefix();;
		StringBuffer lookupUrl = new StringBuffer(basePath);

		String lookupType = docForm.getLookupType();
		docForm.setLookupType(null);

		lookupUrl.append("/Lookup.do?methodToCall=start&docFormKey=").append(getUserSession(request).addObject(docForm)).append("&lookupableImplServiceName=");
		lookupUrl.append(request.getParameter("lookupableImplServiceName"));

		if (lookupType != null && !lookupType.equals("")) {
			lookupUrl.append("&conversionFields=");
			WorkflowLookupable workflowLookupable = (WorkflowLookupable) GlobalResourceLoader.getService(request.getParameter("lookupableImplServiceName"));//SpringServiceLocator.getExtensionService().getLookupable(request.getParameter("lookupableImplServiceName"));
			for (Iterator iterator = workflowLookupable.getDefaultReturnType().iterator(); iterator.hasNext();) {
				String returnType = (String) iterator.next();
				lookupUrl.append(returnType).append(":").append(lookupType);
			}
		}

		lookupUrl.append("&returnLocation=").append(basePath).append(mapping.getPath()).append(".do");
		return new ActionForward(lookupUrl.toString(), true);
	}

	public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		DocumentOperationForm docForm = (DocumentOperationForm) form;
		String lookupInvocationModule = docForm.getLookupInvocationModule();
		docForm.getRouteHeader().setRouteHeaderId(new Long(docForm.getRouteHeaderId()));

		if (lookupInvocationModule != null && !lookupInvocationModule.trim().equals("")) {
			String lookupField = docForm.getLookupInvocationField();
			int lookupIndex = new Integer(docForm.getLookupInvocationIndex()).intValue();
			String networkId = request.getParameter("networkId");

			if (lookupInvocationModule.equals("RouteHeader")) {
				DocumentRouteHeaderValue routeHeader = docForm.getRouteHeader();
				if ("initiatorWorkflowId".equals(lookupField)) {
					try {
						routeHeader.setInitiatorWorkflowId(getUserService().getWorkflowUser(new AuthenticationUserId(networkId)).getWorkflowUserId().getWorkflowId());
					} catch (EdenUserNotFoundException e) {
						LOG.info("route header initiator not found");
						routeHeader.setInitiatorWorkflowId(null);
					}
				}
				if ("documentTypeId".equals(lookupField)) {
					DocumentType docType = getDocumentTypeService().findByName(request.getParameter("docTypeFullName"));
					routeHeader.setDocumentTypeId(docType.getDocumentTypeId());
				}
			}

			if (lookupInvocationModule.equals("ActionRequest")) {
				ActionRequestValue actionRequest = docForm.getRouteHeader().getDocActionRequest(lookupIndex);
				if ("routeMethodName".equals(lookupField)) {
//					actionRequest.setRouteMethodName(null);
					String id = request.getParameter("ruleTemplate.ruleTemplateId");
					if (id != null && !"".equals(id.trim())) {
						RuleTemplate ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateId(new Long(id));
						if (ruleTemplate != null) {
//							actionRequest.setRouteMethodName(ruleTemplate.getName());
						}
					}
				}
				if ("workflowId".equals(lookupField)) {
					try {
						actionRequest.setWorkflowId(getUserService().getWorkflowUser(new AuthenticationUserId(networkId)).getWorkflowUserId().getWorkflowId());
					} catch (EdenUserNotFoundException e) {
						LOG.info("action request user not found");
						actionRequest.setWorkflowId(null);
					}
				}
				if ("workgroupId".equals(lookupField)) {
					if (request.getParameter("workgroupId") != null && !"".equals(request.getParameter("workgroupId").trim())) {
						actionRequest.setWorkgroupId(new Long(request.getParameter("workgroupId")));
					} else {
						actionRequest.setWorkgroupId(null);
					}
				}
				if ("roleName".equals(lookupField)) {
					actionRequest.setRoleName(request.getParameter("roleName"));
				}
			}
			if (lookupInvocationModule.equals("ActionTaken")) {
				ActionTakenValue actionTaken = docForm.getRouteHeader().getDocActionTaken(lookupIndex);
				if ("workflowId".equals(lookupField)) {
					try {
						actionTaken.setWorkflowId(getUserService().getWorkflowUser(new AuthenticationUserId(networkId)).getWorkflowUserId().getWorkflowId());
					} catch (EdenUserNotFoundException e) {
						LOG.info("action taken user not found");
						actionTaken.setWorkflowId(null);
					}
				}
				if ("delegatorWorkflowId".equals(lookupField)) {
					try {
						actionTaken.setDelegatorWorkflowId(getUserService().getWorkflowUser(new AuthenticationUserId(networkId)).getWorkflowUserId().getWorkflowId());
					} catch (EdenUserNotFoundException e) {
						LOG.info("action taken delegator user not found");
						actionTaken.setDelegatorWorkflowId(null);
					}
				}
				if ("delegatorWorkgroupId".equals(lookupField)) {
					if (request.getParameter("workgroupId") != null && !"".equals(request.getParameter("workgroupId").trim())) {
						actionTaken.setDelegatorWorkgroupId(new Long(request.getParameter("workgroupId")));
					} else {
						actionTaken.setDelegatorWorkgroupId(null);
					}
				}
			}

			if (lookupInvocationModule.equals("ActionItem")) {
				ActionItem actionItem = docForm.getRouteHeader().getDocActionItem(lookupIndex);
				if ("workflowId".equals(lookupField)) {
					try {
						actionItem.setWorkflowId(getUserService().getWorkflowUser(new AuthenticationUserId(networkId)).getWorkflowUserId().getWorkflowId());
					} catch (EdenUserNotFoundException e) {
						LOG.info("action item user not found");
						actionItem.setWorkflowId(null);
					}
				}

				if ("workgroupId".equals(lookupField)) {
					if (request.getParameter("workgroupId") != null && !"".equals(request.getParameter("workgroupId").trim())) {
						actionItem.setWorkgroupId(new Long(request.getParameter("workgroupId")));
					} else {
						actionItem.setWorkgroupId(null);
					}
				}
				if ("roleName".equals(lookupField)) {
					actionItem.setRoleName(request.getParameter("roleName"));
				}
				if ("delegatorWorkflowId".equals(lookupField)) {
					try {
						actionItem.setDelegatorWorkflowId(getUserService().getWorkflowUser(new AuthenticationUserId(networkId)).getWorkflowUserId().getWorkflowId());
					} catch (EdenUserNotFoundException e) {
						LOG.info("action item delegator user not found");
						actionItem.setDelegatorWorkflowId(null);
					}
				}
				if ("delegatorWorkgroupId".equals(lookupField)) {
					if (request.getParameter("workgroupId") != null && !"".equals(request.getParameter("workgroupId").trim())) {
						actionItem.setDelegatorWorkgroupId(new Long(request.getParameter("workgroupId")));
					} else {
						actionItem.setDelegatorWorkgroupId(null);
					}
				}
				if ("docName".equals(lookupField)) {
					DocumentType docType = getDocumentTypeService().findByName(request.getParameter("docTypeFullName"));
					actionItem.setDocName(docType.getName());
					actionItem.setDocLabel(docType.getLabel());
					actionItem.setDocHandlerURL(docType.getDocHandlerUrl());
				}
			}
		}

		return mapping.findForward("basic");
	}

	public ActionForward queueDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			DocumentOperationForm docForm = (DocumentOperationForm) form;
			KEWXMLService routeDoc = MessageServiceNames.getRouteDocumentMessageService(docForm.getRouteHeader());
			routeDoc.invoke(docForm.getRouteHeaderId());
			ActionMessages messages = new ActionMessages();
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("general.message", "Document was successfully queued"));
			saveMessages(request, messages);
			return mapping.findForward("basic");
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
	}

	public ActionForward indexSearchableAttributes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		DocumentOperationForm docForm = (DocumentOperationForm) form;
		SearchableAttributeProcessingService searchableAttributeService = MessageServiceNames.getSearchableAttributeService(docForm.getRouteHeader());
		searchableAttributeService.indexDocument(docForm.getRouteHeader().getRouteHeaderId());
		ActionMessages messages = new ActionMessages();
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("general.message", "Searchable Attribute Indexing was successfully scheduled"));
		saveMessages(request, messages);
		return mapping.findForward("basic");
	}

	public ActionForward queueDocumentRequeuer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		DocumentOperationForm docForm = (DocumentOperationForm) form;
		DocumentRequeuerService docRequeue = MessageServiceNames.getDocumentRequeuerService(docForm.getRouteHeader().getDocumentType().getMessageEntity(), docForm.getRouteHeader().getRouteHeaderId(), 0);
		docRequeue.requeueDocument(docForm.getRouteHeader().getRouteHeaderId());
		ActionMessages messages = new ActionMessages();
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("general.message", "Document Requeuer was successfully scheduled"));
		saveMessages(request, messages);
		return mapping.findForward("basic");
	}

	public ActionForward blanketApproveDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			DocumentOperationForm docForm = (DocumentOperationForm) form;
			WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(docForm.getBlanketApproveUser()));
			Set<String> nodeNames = new HashSet<String>();
			if (!StringUtils.isBlank(docForm.getBlanketApproveNodes())) {
				String[] nodeNameArray = docForm.getBlanketApproveNodes().split(",");
				for (String nodeName : nodeNameArray) {
					nodeNames.add(nodeName.trim());
				}
			}
			BlanketApproveProcessorService blanketApprove = MessageServiceNames.getBlanketApproveProcessorService(docForm.getRouteHeader());
			blanketApprove.doBlanketApproveWork(docForm.getRouteHeader().getRouteHeaderId(), user, new Long(docForm.getBlanketApproveActionTakenId()), nodeNames);
			ActionMessages messages = new ActionMessages();
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("general.message", "Blanket Approve Processor was successfully scheduled"));
			saveMessages(request, messages);
			return mapping.findForward("basic");
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
	}

	public ActionForward moveDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			DocumentOperationForm docForm = (DocumentOperationForm) form;
			WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(docForm.getBlanketApproveUser()));
			Set<String> nodeNames = new HashSet<String>();
			if (!StringUtils.isBlank(docForm.getBlanketApproveNodes())) {
				String[] nodeNameArray = docForm.getBlanketApproveNodes().split(",");
				for (String nodeName : nodeNameArray) {
					nodeNames.add(nodeName.trim());
				}
			}
			ActionTakenValue actionTaken = KEWServiceLocator.getActionTakenService().findByActionTakenId(new Long(docForm.getBlanketApproveActionTakenId()));
			MoveDocumentService moveService = MessageServiceNames.getMoveDocumentProcessorService(docForm.getRouteHeader());
			moveService.moveDocument(user, docForm.getRouteHeader(), actionTaken, nodeNames);
			ActionMessages messages = new ActionMessages();
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("general.message", "Move Document Processor was successfully scheduled"));
			saveMessages(request, messages);
			return mapping.findForward("basic");
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
	}

	public ActionForward queueActionInvocation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			DocumentOperationForm docForm = (DocumentOperationForm) form;
			WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(docForm.getActionInvocationUser()));
			ActionInvocation invocation = new ActionInvocation(new Long(docForm.getActionInvocationActionItemId()), docForm.getActionInvocationActionCode());
			ActionInvocationService actionInvocationService = MessageServiceNames.getActionInvocationProcessorService(docForm.getRouteHeader());
			actionInvocationService.invokeAction(user, docForm.getRouteHeader().getRouteHeaderId(), invocation);
			ActionMessages messages = new ActionMessages();
			messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("general.message", "Action Invocation Processor was successfully scheduled"));
			saveMessages(request, messages);
			return mapping.findForward("basic");
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
	}


	private UserService getUserService() {
		return (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
	}

	private DocumentTypeService getDocumentTypeService() {
		return (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
	}

	private BranchService getBranchService(){
		return (BranchService) KEWServiceLocator.getService(KEWServiceLocator.BRANCH_SERVICE);
	}
}
