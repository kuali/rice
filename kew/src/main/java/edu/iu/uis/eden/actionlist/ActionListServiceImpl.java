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
package edu.iu.uis.eden.actionlist;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.bus.services.KSBServiceLocator;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionitem.dao.ActionItemDAO;
import edu.iu.uis.eden.actionlist.dao.ActionListDAO;
import edu.iu.uis.eden.actionrequests.ActionRequestService;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.messaging.KEWXMLService;
import edu.iu.uis.eden.messaging.MessageServiceNames;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupMembershipChangeProcessor;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * Default implementation of the {@link ActionListService}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionListServiceImpl implements ActionListService {

	protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());

	private ActionListDAO actionListDAO;

	private ActionItemDAO actionItemDAO;

	public Collection findUserDelegators(WorkflowUser workflowUser, String delegationType) throws EdenUserNotFoundException {
		return findDelegators(workflowUser, delegationType);
	}

	public Collection getActionList(WorkflowUser workflowUser, ActionListFilter filter) {
		return getActionListDAO().getActionList(workflowUser, filter);
	}

	public void setActionListDAO(ActionListDAO actionListDAO) {
		this.actionListDAO = actionListDAO;
	}

	public ActionListDAO getActionListDAO() {
		return actionListDAO;
	}

	public boolean refreshActionList(WorkflowUser user) {
		return KEWServiceLocator.getUserOptionsService().refreshActionList(user);
	}

	public void deleteActionItem(ActionItem actionItem) {
		try {
			KEWServiceLocator.getUserOptionsService().saveRefreshUserOption(actionItem.getUser());
		} catch (EdenUserNotFoundException e) {
			LOG.error("error saving refreshUserOption", e);
		}
		getActionItemDAO().deleteActionItem(actionItem);
	}

	public void deleteActionItems(Long actionRequestId) {
		Collection actionItems = findByActionRequestId(actionRequestId);
		for (Iterator iter = actionItems.iterator(); iter.hasNext();) {
			ActionItem actionItem = (ActionItem) iter.next();
			try {
				KEWServiceLocator.getUserOptionsService().saveRefreshUserOption(actionItem.getUser());
			} catch (EdenUserNotFoundException e) {
				LOG.error("error saving refreshUserOption", e);
			}
		}
		getActionItemDAO().deleteActionItems(actionRequestId);
	}

	public void deleteByRouteHeaderId(Long routeHeaderId) {
		Collection actionItems = findByRouteHeaderId(routeHeaderId);
		for (Iterator iter = actionItems.iterator(); iter.hasNext();) {
			ActionItem actionItem = (ActionItem) iter.next();
			try {
				KEWServiceLocator.getUserOptionsService().saveRefreshUserOption(actionItem.getUser());
			} catch (EdenUserNotFoundException e) {
				LOG.error("error saving refreshUserOption", e);
			}
		}
		getActionItemDAO().deleteByRouteHeaderId(routeHeaderId);
	}

	// public Collection findByWorkgroupId(Long workgroupId) {
	// return getActionItemDAO().findByWorkgroupId(workgroupId);
	// }

	public Collection findByRouteHeaderId(Long routeHeaderId) {
		return getActionItemDAO().findByRouteHeaderId(routeHeaderId);
	}

	public Collection findByActionRequestId(Long actionRequestId) {
		return getActionItemDAO().findByActionRequestId(actionRequestId);
	}

	public Collection findByWorkflowUser(WorkflowUser workflowUser) {
		return getActionItemDAO().findByWorkflowUser(workflowUser);
	}

	public Collection findByWorkflowUserRouteHeaderId(String workflowUserId, Long routeHeaderId) {
		return getActionItemDAO().findByWorkflowUserRouteHeaderId(workflowUserId, routeHeaderId);
	}

	private void loadActionItemFromActionRequest(ActionRequestValue actionRequest, ActionItem actionItem) {

		DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(actionRequest.getRouteHeaderId());
		DocumentType docType = routeHeader.getDocumentType();

		actionItem.setActionRequestCd(actionRequest.getActionRequested());
		actionItem.setActionRequestId(actionRequest.getActionRequestId());
		actionItem.setDocName(docType.getName());
		actionItem.setRoleName(actionRequest.getQualifiedRoleName());
		actionItem.setWorkflowId(actionRequest.getWorkflowId());
		actionItem.setRouteHeaderId(actionRequest.getRouteHeaderId());
		actionItem.setRouteHeader(routeHeader);
		actionItem.setDateAssigned(new Timestamp(new Date().getTime()));
		actionItem.setDocHandlerURL(docType.getDocHandlerUrl());
		actionItem.setDocLabel(docType.getLabel());
		actionItem.setDocTitle(routeHeader.getDocTitle());
		actionItem.setWorkgroupId(actionRequest.getWorkgroupId());
		actionItem.setResponsibilityId(actionRequest.getResponsibilityId());
		actionItem.setDelegationType(actionRequest.getDelegationType());

		ActionRequestValue delegatorActionRequest = getActionRequestService().findDelegatorRequest(actionRequest);
		if (delegatorActionRequest != null) {
			actionItem.setDelegatorWorkflowId(delegatorActionRequest.getWorkflowId());
			actionItem.setDelegatorWorkgroupId(delegatorActionRequest.getWorkgroupId());
		}
	}

	/**
	 * Generates ActionItems for the given ActionRequest and returns the List of
	 * generated Action Items.
	 *
	 * @return the List of generated ActionItems
	 */
	public List generateActionItems(ActionRequestValue actionRequest, boolean simulate) throws EdenUserNotFoundException {
		LOG.debug("generating the action items for request " + actionRequest.getActionRequestId());
		List actionItems = new ArrayList();
		if (!actionRequest.isPrimaryDelegator()) {
			if (EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD.equals(actionRequest.getRecipientTypeCd())) {
				List users = getWorkgroupService().getWorkgroup(new WorkflowGroupId(actionRequest.getWorkgroupId())).getUsers();
				actionItems.addAll(getActionItemsFromUserList(actionRequest, users));
			} else if (EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD.equals(actionRequest.getRecipientTypeCd())) {
				ActionItem actionItem = new ActionItem();
				loadActionItemFromActionRequest(actionRequest, actionItem);
				actionItems.add(actionItem);
			}
		}
		if (!simulate) {
			for (Iterator iterator = actionItems.iterator(); iterator.hasNext();) {
				ActionItem actionItem = (ActionItem) iterator.next();
				saveActionItem(actionItem);
			}
		}
		return actionItems;
	}

	private List getActionItemsFromUserList(ActionRequestValue actionRequest, List users) {
		List actionItems = new ArrayList();
		for (Iterator iterator = users.iterator(); iterator.hasNext();) {
			WorkflowUser user = (WorkflowUser) iterator.next();
			ActionItem actionItem = new ActionItem();
			loadActionItemFromActionRequest(actionRequest, actionItem);
			actionItem.setWorkflowId(user.getWorkflowUserId().getWorkflowId());
			actionItem.setRoleName(actionRequest.getQualifiedRoleName());
			actionItems.add(actionItem);
		}
		return actionItems;
	}

	/**
	 * Determines the difference between the current workgroup membership and
	 * the new workgroup membership. It then schedules the action item updates
	 * to happen asynchronously.
	 */
	public void updateActionItemsForWorkgroupChange(Workgroup oldWorkgroup, Workgroup newWorkgroup) throws EdenUserNotFoundException {
		List oldMembers = oldWorkgroup.getUsers();
		List newMembers = newWorkgroup.getUsers();
		MembersDiff membersDiff = getMembersDiff(oldMembers, newMembers);
		for (Iterator iterator = membersDiff.getRemovedMembers().iterator(); iterator.hasNext();) {
			WorkflowUser removedMember = (WorkflowUser) iterator.next();
			KEWXMLService workgroupMembershipChangeProcessor = (KEWXMLService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(new QName(MessageServiceNames.WORKGROUP_MEMBERSHIP_CHANGE_SERVICE));
			try {
				workgroupMembershipChangeProcessor.invoke(WorkgroupMembershipChangeProcessor.getMemberRemovedMessageContents(removedMember, newWorkgroup));
			} catch (Exception e) {
				throw new WorkflowRuntimeException(e);
			}
		}
		for (Iterator iterator = membersDiff.getAddedMembers().iterator(); iterator.hasNext();) {
			WorkflowUser addedMember = (WorkflowUser) iterator.next();
			KEWXMLService workgroupMembershipChangeProcessor = (KEWXMLService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(new QName(MessageServiceNames.WORKGROUP_MEMBERSHIP_CHANGE_SERVICE));
			try {
				workgroupMembershipChangeProcessor.invoke(WorkgroupMembershipChangeProcessor.getMemberAddedMessageContents(addedMember, oldWorkgroup));
			} catch (Exception e) {
				throw new WorkflowRuntimeException(e);
			}

		}
	}

	/**
	 * Update the user's Action List to reflect their addition to the given
	 * Workgroup.
	 */
	public void updateActionListForUserAddedToWorkgroup(WorkflowUser user, Workgroup workgroup) throws EdenUserNotFoundException {
		// first verify that the user is still a member of the workgroup
		if (workgroup.hasMember(user)) {
			List<ActionRequestValue> actionRequests = new ArrayList<ActionRequestValue>();
			List<Workgroup> allWorkgroupsToCheck = KEWServiceLocator.getWorkgroupService().getWorkgroupsGroups(workgroup);
			allWorkgroupsToCheck.add(0, workgroup);
			for (Workgroup workgroupToCheck : allWorkgroupsToCheck) {
				actionRequests.addAll(getActionRequestService().findActivatedByWorkgroup(workgroupToCheck));
			}
			for (Iterator requestIt = actionRequests.iterator(); requestIt.hasNext();) {
				ActionRequestValue request = (ActionRequestValue) requestIt.next();
				ActionItem item = new ActionItem();
				loadActionItemFromActionRequest(request, item);
				item.setWorkflowId(user.getWorkflowUserId().getWorkflowId());
				saveActionItem(item);
			}
		}
	}

	/**
	 * Update the user's Action List to reflect their removal from the given
	 * Workgroup.
	 */
	public void updateActionListForUserRemovedFromWorkgroup(WorkflowUser user, Workgroup workgroup) throws EdenUserNotFoundException {
		// first verify that the user is no longer a member of the workgroup
		if (!workgroup.hasMember(user)) {
			List<Workgroup> allWorkgroupsToCheck = KEWServiceLocator.getWorkgroupService().getWorkgroupsGroups(workgroup);
			allWorkgroupsToCheck.add(0, workgroup);
			Collection actionItems = findByWorkflowUser(user);
			for (Iterator itemIt = actionItems.iterator(); itemIt.hasNext();) {
				ActionItem item = (ActionItem) itemIt.next();
				if (item.isWorkgroupItem()) {
					for (Workgroup workgroupToCheck : allWorkgroupsToCheck) {
						if (item.getWorkgroupId().equals(workgroupToCheck.getWorkflowGroupId().getGroupId())) {
							deleteActionItem(item);
						}
					}
				}
			}
		}
	}

	public void updateActionItemsForTitleChange(Long routeHeaderId, String newTitle) throws EdenUserNotFoundException {
		Collection items = getActionItemDAO().findByRouteHeaderId(routeHeaderId);
		for (Iterator iterator = items.iterator(); iterator.hasNext();) {
			ActionItem item = (ActionItem) iterator.next();
			item.setDocTitle(newTitle);
			saveActionItem(item);
		}
	}

	private MembersDiff getMembersDiff(Collection oldMembers, Collection newMembers) {
		Map currentMembersMap = createUsersMap(oldMembers);
		Map newMembersMap = createUsersMap(newMembers);
		Map allMembers = mergeMaps(currentMembersMap, newMembersMap);
		Collection addedKeys = CollectionUtils.subtract(newMembersMap.keySet(), currentMembersMap.keySet());
		Collection removedKeys = CollectionUtils.subtract(currentMembersMap.keySet(), newMembersMap.keySet());
		Set addedMembers = getUsersSet(addedKeys, allMembers);
		Set removedMembers = getUsersSet(removedKeys, allMembers);
		return new MembersDiff(addedMembers, removedMembers);
	}

	private Map mergeMaps(Map map1, Map map2) {
		Map newMap = new HashMap();
		newMap.putAll(map1);
		newMap.putAll(map2);
		return newMap;
	}

	private Set getUsersSet(Collection userKeys, Map memberMap) {
		Set resultSet = new HashSet();
		for (Iterator iterator = userKeys.iterator(); iterator.hasNext();) {
			String workflowId = (String) iterator.next();
			WorkflowUser user = (WorkflowUser) memberMap.get(workflowId);
			resultSet.add(user);
		}
		return resultSet;
	}

	private Map createUsersMap(Collection members) {
		Map map = new HashMap();
		for (Iterator iterator = members.iterator(); iterator.hasNext();) {
			WorkflowUser user = (WorkflowUser) iterator.next();
			map.put(user.getWorkflowUserId().getWorkflowId(), user);
		}
		return map;
	}

	public Collection findDelegators(WorkflowUser user, String delegationType) throws EdenUserNotFoundException {
		return getActionItemDAO().findDelegators(user, delegationType);
	}

	public void saveActionItem(ActionItem actionItem) throws EdenUserNotFoundException {
		KEWServiceLocator.getUserOptionsService().saveRefreshUserOption(actionItem.getUser());
		getActionItemDAO().saveActionItem(actionItem);
	}

	public ActionItemDAO getActionItemDAO() {
		return actionItemDAO;
	}

	public ActionRequestService getActionRequestService() {
		return (ActionRequestService) KEWServiceLocator.getActionRequestService();
	}

	public WorkgroupService getWorkgroupService() {
		return (WorkgroupService) KEWServiceLocator.getWorkgroupService();
	}

	public void setActionItemDAO(ActionItemDAO actionItemDAO) {
		this.actionItemDAO = actionItemDAO;
	}

	public UserService getUserService() {
		return (UserService) KEWServiceLocator.getUserService();
	}

	// public ActionListEmailService getActionListEmailService() {
	// return (ActionListEmailService)
	// SpringServiceLocator.getActionListEmailService();
	// }

	private class MembersDiff {
		private final Set addedMembers;

		private final Set removedMembers;

		public MembersDiff(Set addedMembers, Set removedMembers) {
			this.addedMembers = addedMembers;
			this.removedMembers = removedMembers;
		}

		public Set getAddedMembers() {
			return addedMembers;
		}

		public Set getRemovedMembers() {
			return removedMembers;
		}
	}

	public void validateActionItem(ActionItem actionItem) {
		List errors = new ArrayList();
		String workflowId = actionItem.getWorkflowId();
		if (workflowId == null || workflowId.trim().equals("")) {
			errors.add(new WorkflowServiceErrorImpl("ActionItem person null.", "actionitem.personid.empty", actionItem.getActionItemId().toString()));
		} else {
			try {
				getUserService().getWorkflowUser(new WorkflowUserId(workflowId));
			} catch (EdenUserNotFoundException e) {
				errors.add(new WorkflowServiceErrorImpl("ActionItem person invalid.", "actionitem.personid.invalid", actionItem.getActionItemId().toString()));
			}
		}

		if (actionItem.getDateAssigned() == null) {
			errors.add(new WorkflowServiceErrorImpl("ActionItem date assigned empty.", "actionitem.dateassigned.empty", actionItem.getActionItemId().toString()));
		}

		String actionRequestCd = actionItem.getActionRequestCd();
		if (actionRequestCd == null || actionRequestCd.trim().equals("")) {
			errors.add(new WorkflowServiceErrorImpl("ActionItem action request cd empty.", "actionitem.actionrequestcd.empty", actionItem.getActionItemId().toString()));
		} else if (!EdenConstants.ACTION_REQUEST_CD.containsKey(actionRequestCd)) {
			errors.add(new WorkflowServiceErrorImpl("ActionItem action request cd invalid.", "actionitem.actionrequestcd.invalid", actionItem.getActionItemId().toString()));
		}

		if (actionItem.getActionRequestId() == null) {
			errors.add(new WorkflowServiceErrorImpl("ActionItem action request id empty.", "actionitem.actionrequestid.empty", actionItem.getActionItemId().toString()));
		}

		if (actionItem.getRouteHeaderId() == null) {
			errors.add(new WorkflowServiceErrorImpl("ActionItem Document id empty.", "actionitem.routeheaderid.empty", actionItem.getActionItemId().toString()));
		} else if (KEWServiceLocator.getRouteHeaderService().getRouteHeader(actionItem.getRouteHeaderId()) == null) {
			errors.add(new WorkflowServiceErrorImpl("ActionItem Document id invalid.", "actionitem.routeheaderid.invalid", actionItem.getActionItemId().toString()));
		}

		String docTypeName = actionItem.getDocName();
		DocumentType docType = null;
		if (docTypeName == null || docTypeName.trim().equals("")) {
			errors.add(new WorkflowServiceErrorImpl("ActionItem doctypename empty.", "actionitem.doctypename.empty", actionItem.getActionItemId().toString()));
		} else {
			docType = KEWServiceLocator.getDocumentTypeService().findByName(actionItem.getDocName());
			if (docType == null) {
				errors.add(new WorkflowServiceErrorImpl("ActionItem doctypename invalid.", "actionitem.doctypename.invalid", actionItem.getActionItemId().toString()));
			}
		}

		if (actionItem.getDocLabel() == null || actionItem.getDocLabel().trim().equals("")) {
			errors.add(new WorkflowServiceErrorImpl("ActionItem doctypelabel empty.", "actionitem.doctypelabel.empty", actionItem.getActionItemId().toString()));
		} else if (docType != null && !docType.getLabel().equals(actionItem.getDocLabel())) {
			errors.add(new WorkflowServiceErrorImpl("ActionItem doctypelabel no match.", "actionitem.doctypelabel.nomatch", actionItem.getActionItemId().toString()));
		}

		if (actionItem.getDocHandlerURL() == null || actionItem.getDocHandlerURL().trim().equals("")) {
			errors.add(new WorkflowServiceErrorImpl("ActionItem doc handler url empty.", "actionitem.dochdrurl.empty", actionItem.getActionItemId().toString()));
		} else if (docType != null && !docType.getDocHandlerUrl().equals(actionItem.getDocHandlerURL())) {
			errors.add(new WorkflowServiceErrorImpl("ActionItem doc handler url no match.", "actionitem.dochdrurl.nomatch", actionItem.getActionItemId().toString()));
		}

		if (!errors.isEmpty()) {
			throw new WorkflowServiceErrorException("ActionItem Validation Error", errors);
		}
	}

	public ActionItem findByActionItemId(Long actionItemId) {
		return getActionItemDAO().findByActionItemId(actionItemId);
	}

	public int getCount(WorkflowUser user) {
		return getActionListDAO().getCount(user.getWorkflowId());
	}

	public void saveRefreshUserOption(WorkflowUser user) {
	    KEWServiceLocator.getUserOptionsService().saveRefreshUserOption(user);
	}

}
