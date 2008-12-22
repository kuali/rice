/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;
import org.kuali.rice.kew.attribute.Extension;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.dto.WorkgroupIdDTO;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.export.ExportDataSet;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.workgroup.BaseWorkgroup;
import org.kuali.rice.kew.workgroup.GroupId;
import org.kuali.rice.kew.workgroup.GroupNameId;
import org.kuali.rice.kew.workgroup.WorkflowGroupId;
import org.kuali.rice.kew.workgroup.Workgroup;
import org.kuali.rice.kew.workgroup.WorkgroupCapabilities;
import org.kuali.rice.kew.workgroup.WorkgroupService;
import org.kuali.rice.kew.xml.WorkgroupXmlHandler;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimWorkgroupServiceImpl implements WorkgroupService {

	protected PersonService<Person> personService;
	protected IdentityManagementService identityManagementService;

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#getUsersGroupIds(org.kuali.rice.kew.user.WorkflowUser)
	 */
	public Set<Long> getUsersGroupIds(String principalId) {
		Set<Long> ids = new HashSet<Long>();
		List<? extends KimGroup> groups = getIdentityManagementService().getGroupsForPrincipal(principalId);
		for (KimGroup kimGroup : groups) {
			ids.add(new Long(kimGroup.getGroupId()));
		}
		return ids;
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#getUsersGroupNames(org.kuali.rice.kew.user.WorkflowUser)
	 */
	public Set<String> getUsersGroupNames(Person member) {
		return this.getUsersGroupNames(member.getPrincipalId());
	}

	/**
	 *
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#getUsersGroupNames(java.lang.String)
	 */
	public Set<String> getUsersGroupNames(String principalId) {
		Set<String> names = new HashSet<String>();
		List<? extends KimGroup> groups = getIdentityManagementService().getGroupsForPrincipal(principalId);
		for (KimGroup kimGroup : groups) {
			names.add(kimGroup.getGroupName());
		}
		return names;
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#getUsersGroups(org.kuali.rice.kew.user.WorkflowUser)
	 */
	public List<Workgroup> getUsersGroups(String principalId) throws KEWUserNotFoundException {
		List<Workgroup> workgroups = new ArrayList<Workgroup>();
		List<? extends KimGroup> groups = getIdentityManagementService().getGroupsForPrincipal(principalId);
		for (KimGroup kimGroup : groups) {
			workgroups.add(convertToWorkgroup(kimGroup));
		}
		return workgroups;
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#getWorkgroup(org.kuali.rice.kew.workgroup.GroupId)
	 */
	public Workgroup getWorkgroup(GroupId groupId) {
		if (groupId == null || groupId.isEmpty()) {
			return null;
		}
		if (groupId instanceof WorkflowGroupId) {
			KimGroup group = getIdentityManagementService().getGroup(""+((WorkflowGroupId)groupId).getGroupId());
			return convertToWorkgroup(group);
		} else {
			KimGroup group = getIdentityManagementService().getGroupByName(org.kuali.rice.kim.util.KimConstants.TEMP_GROUP_NAMESPACE, ((GroupNameId)groupId).getNameId());
			return convertToWorkgroup(group);
		}
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#getWorkgroup(org.kuali.rice.kew.workgroup.GroupId, boolean)
	 */
	public Workgroup getWorkgroup(GroupId groupId, boolean loadWorkgroupExtensions) {
		return getWorkgroup(groupId);
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#getWorkgroup(org.kuali.rice.kew.dto.WorkgroupIdDTO)
	 */
	public Workgroup getWorkgroup(WorkgroupIdDTO groupIdVO) {
		return getWorkgroup(DTOConverter.convertWorkgroupIdVO(groupIdVO));
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#isUserMemberOfGroup(org.kuali.rice.kew.workgroup.GroupId, org.kuali.rice.kew.user.WorkflowUser)
	 */
	public boolean isUserMemberOfGroup(GroupId groupId, String principalId) throws KEWUserNotFoundException {
		String id = null;
		if (groupId instanceof WorkflowGroupId) {
			KimGroup group = getIdentityManagementService().getGroup(""+((WorkflowGroupId)groupId).getGroupId());
			id = group.getGroupId();
		} else {
			KimGroup group = getIdentityManagementService().getGroupByName(org.kuali.rice.kim.util.KimConstants.TEMP_GROUP_NAMESPACE, ((GroupNameId)groupId).getNameId());
			id = group.getGroupId();
		}
		return getIdentityManagementService().isMemberOfGroup(principalId, id);
	}

	private Workgroup convertToWorkgroup(KimGroup kimGroup) {
		if (kimGroup == null) {
			return null;
		}
		BaseWorkgroup workgroup = new BaseWorkgroup();
		workgroup.setActiveInd(kimGroup.isActive());
		workgroup.setCurrentInd(true);
		workgroup.setDescription(kimGroup.getGroupDescription());
		workgroup.setDocumentId(-1L);
		workgroup.setExtensions(new ArrayList<Extension>());
		workgroup.setGroupNameId(new GroupNameId(kimGroup.getGroupName()));
		workgroup.setLockVerNbr(0);
		List<Recipient> members = new ArrayList<Recipient>();
		for (String id : getIdentityManagementService().getMemberGroupIds(kimGroup.getGroupId())) {
			members.add(convertToWorkgroup(getIdentityManagementService().getGroup(id)));
		}
		for (String id: getIdentityManagementService().getGroupMemberPrincipalIds(kimGroup.getGroupId())) {
			Person person = getPersonService().getPerson(id);
			WorkflowUser u = KimUserServiceImpl.convertPersonToWorkflowUser(person);
			if (u != null) {
				members.add(u);
			}
		}
		workgroup.setMembers(members);
		workgroup.setVersionNumber(0);
		workgroup.setWorkgroupId(new Long(kimGroup.getGroupId()));
		return workgroup;
	}

	// Below are all old methods that KIM will not support

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#getWorkgroupsGroups(org.kuali.rice.kew.workgroup.Workgroup)
	 */
	public List<Workgroup> getWorkgroupsGroups(Workgroup workgroup) {
		throw new UnsupportedOperationException("Kim does not suppoert this method");
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#copy(org.kuali.rice.kew.workgroup.Workgroup)
	 */
	public Workgroup copy(Workgroup workgroup) {
		throw new UnsupportedOperationException("Kim does not suppoert this method");
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#getBlankWorkgroup()
	 */
	public Workgroup getBlankWorkgroup() {
		return new BaseWorkgroup();
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#getBlankWorkgroupExtension()
	 */
	public Extension getBlankWorkgroupExtension() {
		throw new UnsupportedOperationException("Kim does not suppoert this method");
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#getCapabilities()
	 */
	public WorkgroupCapabilities getCapabilities() {
		throw new UnsupportedOperationException("Kim does not suppoert this method");
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#removeFromCacheById(org.kuali.rice.kew.workgroup.WorkflowGroupId)
	 */
	public void removeFromCacheById(WorkflowGroupId id) {
		throw new UnsupportedOperationException("Kim does not suppoert this method");
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#removeNameFromCache(org.kuali.rice.kew.workgroup.GroupNameId)
	 */
	public void removeNameFromCache(GroupNameId name) {
		throw new UnsupportedOperationException("Kim does not suppoert this method");
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#save(org.kuali.rice.kew.workgroup.Workgroup)
	 */
	@SuppressWarnings("unchecked")
	public void save(Workgroup workgroup) {
		KimGroupImpl group = new KimGroupImpl();
		if (workgroup.getWorkflowGroupId() != null && !workgroup.getWorkflowGroupId().isEmpty()) {
			group.setGroupId("" + workgroup.getWorkflowGroupId().getGroupId());
		}
		group.setGroupName(workgroup.getGroupNameId().getNameId());
		group.setNamespaceCode(org.kuali.rice.kim.util.KimConstants.TEMP_GROUP_NAMESPACE);
		group.setGroupDescription(workgroup.getDescription());
		group.setKimTypeId(workgroup.getWorkgroupType());
		group.setActive(true);

		group.setMembers(new ArrayList<GroupMemberImpl>());
		for (Iterator iterator = workgroup.getMembers().iterator(); iterator.hasNext();) {
			Recipient recipient = (Recipient) iterator.next();
			if (recipient instanceof Person) {
				Person user = (Person)recipient;
				GroupMemberImpl member = new GroupMemberImpl();
				member.setMemberTypeCode( KimGroupImpl.PRINCIPAL_MEMBER_TYPE );
				member.setMemberId(user.getPrincipalId());
				group.getMembers().add(member);
			} else if (recipient instanceof Workgroup) {
				Workgroup groupMember = (Workgroup)recipient;
				GroupMemberImpl member = new GroupMemberImpl();
				member.setMemberTypeCode( KimGroupImpl.PRINCIPAL_MEMBER_TYPE );
				member.setMemberId("" + groupMember.getWorkflowGroupId().getGroupId());
				group.getMembers().add(member);
			}
		}
		KNSServiceLocator.getBusinessObjectService().save(group);
		workgroup.setWorkflowGroupId(new WorkflowGroupId(new Long(group.getGroupId())));
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#search(org.kuali.rice.kew.workgroup.Workgroup, java.util.Map, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List search(Workgroup workgroup, Map<String, String> extensionValues, boolean useWildCards) {
		throw new UnsupportedOperationException("Kim does not suppoert this method");
	}

	/**
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#search(org.kuali.rice.kew.workgroup.Workgroup, java.util.Map, org.kuali.rice.kew.user.WorkflowUser)
	 */
	@SuppressWarnings("unchecked")
	public List search(Workgroup workgroup, Map<String, String> extensionValues, Person user) throws KEWUserNotFoundException {
		throw new UnsupportedOperationException("Kim does not suppoert this method");
	}

	public void loadXml(InputStream stream, String principalId) {
        try {
        	new WorkgroupXmlHandler().parseWorkgroupEntries(stream);
        } catch (Exception e) {
        	if (e instanceof RuntimeException) {
        		throw (RuntimeException)e;
        	}
            throw new RuntimeException("Caught Exception parsing workgroup xml", e);
        }
    }

	/**
	 * @see org.kuali.rice.kew.xml.export.XmlExporter#export(org.kuali.rice.kew.export.ExportDataSet)
	 */
	public Element export(ExportDataSet dataSet) {
		throw new UnsupportedOperationException("Kim does not suppoert this method");
	}

	public IdentityManagementService getIdentityManagementService() {
		if (identityManagementService == null) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();
		}
		return identityManagementService;
	}

	public PersonService<Person> getPersonService() {
		if (personService == null) {
			personService = KIMServiceLocator.getPersonService();
		}
		return personService;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kew.workgroup.WorkgroupService#search(org.kuali.rice.kew.workgroup.Workgroup, java.util.Map, java.lang.String)
	 */
	public List search(Workgroup workgroup,
			Map<String, String> extensionValues, String principalId)
			throws KEWUserNotFoundException {
		throw new UnsupportedOperationException("Kim does not suppoert this method");
	}

}
