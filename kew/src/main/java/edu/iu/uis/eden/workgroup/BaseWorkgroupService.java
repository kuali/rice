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
package edu.iu.uis.eden.workgroup;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;
import org.kuali.workflow.attribute.Extension;
import org.kuali.workflow.workgroup.BaseWorkgroupExtension;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.vo.WorkflowGroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.workgroup.dao.BaseWorkgroupDAO;
import edu.iu.uis.eden.workgroup.dao.BaseWorkgroupMemberDAO;
import edu.iu.uis.eden.xml.WorkgroupXmlHandler;
import edu.iu.uis.eden.xml.export.WorkgroupXmlExporter;

/**
 * The standard implementation of the WorkgroupService which is backed by a database.
 *
 * This implementation utilizes caching of workgroups which is safe in a clustered environment.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BaseWorkgroupService implements WorkgroupService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BaseWorkgroupService.class);

	protected BaseWorkgroupDAO workgroupDAO;
	protected BaseWorkgroupMemberDAO workgroupMemberDAO;
	protected WorkgroupCapabilities capabilities = WorkgroupCapabilities.getAll();

    public static final String WORKGROUP_ID_CACHE_GROUP = "WorkgroupId";
    public static final String WORKGROUP_NAME_CACHE_GROUP = "WorkgroupName";

	// WorkgroupService methods

	public WorkgroupCapabilities getCapabilities() {
		return capabilities;
	}

	public Workgroup getBlankWorkgroup() {
		return new BaseWorkgroup();
	}

	public Extension getBlankWorkgroupExtension() {
		return new BaseWorkgroupExtension();
	}

	public boolean isUserMemberOfGroup(GroupId groupId, WorkflowUser user) throws EdenUserNotFoundException {
		Workgroup workgroup = this.getWorkgroup(groupId);
		return workgroup == null ? false : workgroup.hasMember(user);
	}

	public List search(Workgroup workgroup, Map<String, String> extensionValues, boolean useWildCards) {
		List workgroups = getWorkgroupDAO().search(workgroup, extensionValues);
		try {
			materializeMembers(workgroups);
		} catch (EdenUserNotFoundException e) {
			throw new WorkflowRuntimeException("A problem was encountered when searching for workgroup.", e);
		}
		return workgroups;
	}

	public List search(Workgroup workgroup, Map<String, String> extensionValues, WorkflowUser user) throws EdenUserNotFoundException {
		List workgroups = getWorkgroupDAO().find(workgroup, extensionValues, user);
		materializeMembers(workgroups);
		return workgroups;
	}

	public Workgroup getWorkgroup(WorkgroupIdVO groupIdVO) {
		GroupId groupId = null;
		if (groupIdVO instanceof WorkflowGroupIdVO) {
			groupId = new WorkflowGroupId(((WorkflowGroupIdVO) groupIdVO).getWorkgroupId());
		} else if (groupIdVO instanceof WorkgroupNameIdVO) {
			groupId = new GroupNameId(((WorkgroupNameIdVO) groupIdVO).getWorkgroupName());
		} else {
			throw new IllegalArgumentException("Attempting to find workgroup with invalid id type: " + groupIdVO);
		}
		return getWorkgroup(groupId);
	}

	public Workgroup getWorkgroup(GroupId groupId) {
		return getWorkgroup(groupId, false);
	}

	public Workgroup getWorkgroup(GroupId groupId, boolean loadWorkgroupExtensions) {
		BaseWorkgroup workgroup = null;
		try {
			if (groupId instanceof WorkflowGroupId) {
				LOG.debug("Loading workgoup by id " + groupId);
				WorkflowGroupId workflowGroupId = (WorkflowGroupId) groupId;
				workgroup = fetchFromCache(groupId);
				if (workgroup == null) {
					workgroup = getWorkgroupDAO().findByWorkgroupId(workflowGroupId.getGroupId());
					if (workgroup != null) {
						workgroup = initializeLoadedWorkgroup(workgroup);
						addToCache(workgroup);
					}
				}
			} else if (groupId instanceof GroupNameId) {
				LOG.debug("Loading workgroup from name " + groupId);
				GroupNameId groupNameId = (GroupNameId) groupId;
				workgroup = fetchFromCache(groupNameId);
				if (workgroup == null) {
					workgroup = getWorkgroupDAO().findByName(groupNameId.getNameId());
					if (workgroup != null) {
						workgroup = initializeLoadedWorkgroup(workgroup);
						addToCache(workgroup);
					}
				}
			}
			// if we can't find it in the database, ask subclasses if they have an extension workgroup to load
			if (workgroup == null) {
				workgroup = getExternalWorkgroup(groupId, loadWorkgroupExtensions);
			}
		} catch (EdenUserNotFoundException e) {
			throw new WorkflowRuntimeException("Error locating user in workgroup.", e);
		}
		return workgroup;
	}

	/**
	 * Initializes the Workgroup after being loaded from the database.  This method can be
	 * overriden by subclasses to perform institution specific functions (such as wrapping
	 * the workgroup in a custom implementation prior to it being cached).
	 *
	 * The default behavior of this method is to materialize the members of the SimpleWorkgroup
	 * from the OJB data bean using the <code>materializeMembers()</code> method.
	 * Overridders of this method should be sure to perform this step as well if desired,
	 * preferably by invoking super.initializeLoadedWorkgroup.
	 *
	 * @param the workgroup to initialize
	 * @return the initialized workgroup, does not have to be the exact same instance as the workgroup
	 * passed in
	 */
	protected BaseWorkgroup initializeLoadedWorkgroup(BaseWorkgroup workgroup) throws EdenUserNotFoundException {
		workgroup.materializeMembers();
		return workgroup;
	}

	protected BaseWorkgroup getExternalWorkgroup(GroupId groupId, boolean loadWorkgroupExtensions) {
		return null;
	}

	public List<Workgroup> getUsersGroups(WorkflowUser user) throws EdenUserNotFoundException {
		List workgroupMembers = getWorkgroupMemberDAO().findByWorkflowId(user.getWorkflowUserId().getWorkflowId());
		List<Workgroup> workgroups = new ArrayList<Workgroup>();
		for (Iterator iter = workgroupMembers.iterator(); iter.hasNext();) {
			BaseWorkgroupMember member = (BaseWorkgroupMember) iter.next();
			Workgroup workgroup = getWorkgroup(new WorkflowGroupId(member.getWorkgroupId()));
			if (workgroup == null) {
				LOG.warn("Attempted to find workgroup for workgroup Id:" + member.getWorkgroupId() + " for member:" + member.getWorkflowId() + " but null was returned");
			} else {
				List<Workgroup> workgroupsGroups = getWorkgroupsGroups(workgroup);
				if (workgroupsGroups != null) {
					workgroups.addAll(workgroupsGroups);
				}
				workgroups.add(workgroup);
			}
		}
		return workgroups;
	}

    public List<Workgroup> getWorkgroupsGroups(Workgroup workgroup) {
    	List<Workgroup> parentWorkgroups = new ArrayList<Workgroup>();
    	List<Long> immediateWorkgroupsGroupsIds = getWorkgroupDAO().getImmediateWorkgroupsGroupIds(workgroup.getWorkflowGroupId().getGroupId());
    	for (Long workgroupId : immediateWorkgroupsGroupsIds) {
			Workgroup parentWorkgroup = getWorkgroup(new WorkflowGroupId(workgroupId));
			parentWorkgroups.add(parentWorkgroup);
			parentWorkgroups.addAll(getWorkgroupsGroups(parentWorkgroup));
		}
    	return parentWorkgroups;
    }

	public Set<String> getUsersGroupNames(WorkflowUser user) {
		return getWorkgroupDAO().findWorkgroupNamesForUser(user.getWorkflowId());
	}

	public Set<Long> getUsersGroupIds(WorkflowUser user) {
	    return getWorkgroupDAO().findWorkgroupIdsForUser(user.getWorkflowId());
	}


	public Workgroup copy(Workgroup workgroup) {
		if (workgroup == null) {
			return null;
		}
		if (!(workgroup instanceof BaseWorkgroup)) {
			throw new IllegalArgumentException("Can only copy instances of BaseWorkgroups, given class was: " + workgroup.getClass().getName());
		}
		BaseWorkgroup baseWorkgroup = (BaseWorkgroup)workgroup;
		BaseWorkgroup copyWorkgroup = new BaseWorkgroup();
		copyWorkgroup.setActiveInd(baseWorkgroup.getActiveInd());
		copyWorkgroup.setDescription(baseWorkgroup.getDescription());
		copyWorkgroup.setGroupNameId(baseWorkgroup.getGroupNameId());
		copyWorkgroup.setMembers(baseWorkgroup.getMembers());
		copyWorkgroup.setWorkgroupMembers(baseWorkgroup.getWorkgroupMembers());
		copyWorkgroup.setWorkflowGroupId(baseWorkgroup.getWorkflowGroupId());
		copyWorkgroup.setWorkgroupType(baseWorkgroup.getWorkgroupType());
		copyWorkgroup.setExtensions(baseWorkgroup.getExtensions());
		return copyWorkgroup;
	}

	protected BaseWorkgroup fetchFromCache(GroupId groupId) {
		return (BaseWorkgroup)KEWServiceLocator.getCacheAdministrator().getFromCache(generateCacheKey(groupId));
	}

	protected String generateCacheKey(GroupId groupId) {
		if (groupId == null) {
			throw new IllegalArgumentException("The GroupId is null when attempting to generate cache key.");
		} else if (groupId instanceof WorkflowGroupId) {
			return WORKGROUP_ID_CACHE_GROUP + ":" + ((WorkflowGroupId)groupId);
		} else if (groupId instanceof GroupNameId) {
			return WORKGROUP_NAME_CACHE_GROUP + ":" + ((GroupNameId)groupId);
		} else {
			throw new IllegalArgumentException("The given GroupId type is invalid: " + groupId.getClass().getName());
		}
	}

	protected void addToCache(BaseWorkgroup workgroup) {
		if (workgroup != null) {
			WorkflowGroupId groupId = workgroup.getWorkflowGroupId();
			GroupNameId nameId = workgroup.getGroupNameId();
			if (groupId != null && !groupId.isEmpty()) {
				KEWServiceLocator.getCacheAdministrator().putInCache(generateCacheKey(groupId), workgroup, WORKGROUP_ID_CACHE_GROUP);
				LOG.debug("Caching workgroup by id=" + groupId.getGroupId() + " with name '" + (nameId != null ? nameId.getNameId() : "null") + "'");
			}
			if (nameId != null && !nameId.isEmpty()) {
				KEWServiceLocator.getCacheAdministrator().putInCache(generateCacheKey(nameId), workgroup, WORKGROUP_NAME_CACHE_GROUP);
				LOG.debug("Caching workgroup by name='" + nameId.getNameId() + "' with id " + (groupId != null ? groupId.getGroupId().toString() : "null"));
			}

		}
	}

	public void removeNameFromCache(GroupNameId name) {
		KEWServiceLocator.getCacheAdministrator().flushEntry(generateCacheKey(name));
	}

	public void removeFromCacheById(WorkflowGroupId id) {
		KEWServiceLocator.getCacheAdministrator().flushEntry(generateCacheKey(id));
	}

        // XMLLoader methods

	/**
	 * Loads workgroups from the given XML.
	 */
    public void loadXml(InputStream stream, WorkflowUser user) {
        try {
        	new WorkgroupXmlHandler().parseWorkgroupEntries(stream);
        } catch (Exception e) {
        	if (e instanceof RuntimeException) {
        		throw (RuntimeException)e;
        	}
            throw new RuntimeException("Caught Exception parsing workgroup xml", e);
        }
    }

    // XMLExporter methods

    public Element export(ExportDataSet dataSet) {
        WorkgroupXmlExporter exporter = new WorkgroupXmlExporter();
        return exporter.export(dataSet);
    }

    // helper methods

    protected void materializeMembers(List workgroups) throws EdenUserNotFoundException {
    	for (Iterator iterator = workgroups.iterator(); iterator.hasNext();) {
			((BaseWorkgroup) iterator.next()).materializeMembers();
		}
    }

    public void save(Workgroup workgroup) {
    	// workgroup ids are always the same so lets remove any existing workgroups with this id from the cache
    	if (workgroup.getWorkflowGroupId() != null) {
    		Workgroup original = getWorkgroup(workgroup.getWorkflowGroupId());
    		if (original != null) {
    			removeFromCache(original);
    		}
    	}
    	getWorkgroupDAO().save(workgroup);
	}

    protected void removeFromCache(Workgroup workgroup) {
    	removeFromCacheById(workgroup.getWorkflowGroupId());
    	removeNameFromCache(workgroup.getGroupNameId());
    }

	public void setWorkgroupDAO(BaseWorkgroupDAO workgroupDAO) {
		this.workgroupDAO = workgroupDAO;
	}

	protected BaseWorkgroupDAO getWorkgroupDAO() {
		return workgroupDAO;
	}

	protected BaseWorkgroupMemberDAO getWorkgroupMemberDAO() {
		return workgroupMemberDAO;
	}

	public void setWorkgroupMemberDAO(BaseWorkgroupMemberDAO workgroupMemberDAO) {
		this.workgroupMemberDAO = workgroupMemberDAO;
	}

}