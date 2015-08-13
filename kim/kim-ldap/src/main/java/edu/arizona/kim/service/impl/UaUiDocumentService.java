/*
 *
 * Copyright 2009 State of Arizona Board of Regents
 *
 */
package edu.arizona.kim.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.identity.principal.PrincipalContract;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo;
import org.kuali.rice.kim.service.impl.UiDocumentServiceImpl;

import edu.arizona.kim.document.UaIdentityManagementPersonDocument;

//import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 *
 * @author Leo Przybylski (przybyls@arizona.edu)
 */
public class UaUiDocumentService extends UiDocumentServiceImpl {
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(UiDocumentServiceImpl.class);

	/**
	 * Looks up GroupInfo objects for each group id passed in
	 * 
	 * @param groupIds
	 *            the List of group ids to look up GroupInfo records on
	 * @return a List of GroupInfo records
	 */
	protected List<? extends Group> getGroupsByIds(List<String> groupIds) {
		List<Group> groups = new ArrayList<Group>();
		for (String groupId : groupIds) {
			final Group group = getGroupService().getGroup(groupId);
			groups.add(group);
		}
		return groups;
	}

	protected List<UaIdentityManagementPersonDocument> getPrincipalFromPrincipals(List<PrincipalContract> principals) {
		List<UaIdentityManagementPersonDocument> retval = new ArrayList<UaIdentityManagementPersonDocument>();

		for (PrincipalContract principal : principals) {
			PrincipalBo impl = getPrincipalImpl(principal.getPrincipalId());
			impl.setPrincipalId(principal.getPrincipalId());
			impl.setPrincipalName(principal.getPrincipalName());
			impl.setEntityId(principal.getEntityId());
			impl.setActive(principal.isActive());
		}

		return retval;
	}

	protected PrincipalBo getPrincipalImpl(String principalId) {
		Map<String, String> criteria = new HashMap<String, String>(1);
		criteria.put(KIMPropertyConstants.Principal.PRINCIPAL_ID, principalId);
		return (PrincipalBo) getBusinessObjectService().findByPrimaryKey(PrincipalBo.class, criteria);
	}

	/**
	 * Determines if the pending member is a current or future member of the
	 * group
	 * 
	 * @param pendingMember
	 *            the member to check
	 * @param now
	 *            the current date
	 * @return true if the member is a future or current member of the group,
	 *         false otherwise
	 */
	protected boolean isCurrentOrFutureGroupMember(GroupMember member, DateTime today) {
		return member.getActiveToDate() == null || today.compareTo(member.getActiveToDate()) < 0;
	}

}