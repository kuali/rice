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
package edu.iu.uis.eden.workgroup.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.kuali.workflow.attribute.web.WebExtensions;
import org.kuali.workflow.workgroup.WorkgroupType;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.routeheader.Routable;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.WorkflowRoutingForm;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * A Struts ActionForm for the {@link WorkgroupAction}.
 *
 * @see WorkgroupAction
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupForm extends WorkflowRoutingForm {

	private static final long serialVersionUID = 6253549718029785616L;

	private Long workgroupId;
	private String workgroupName = "";
	private List workgroupMembers = ListUtils.lazyList(new ArrayList(), new Factory() {
		public Object create() {
			return new WorkgroupMember();
		}
	});
	private String workgroupMember;
	private String memberType = "";
    private String removedMember;
    private String methodToCall = "";
    private String showEdit;
    private String command;
    private String instructionForCreateNew;
    private String lookupableImplServiceName;
    private Workgroup existingWorkgroup;
    private WorkgroupType existingWorkgroupType;
    private WebExtensions existingExtensions = new WebExtensions();
    private List<WorkgroupMember> existingWorkgroupMembers;
    private Workgroup workgroup;
    private WorkgroupType workgroupType;
    private String lookupType;
    private List<WorkgroupType> workgroupTypes;
    private String currentWorkgroupType;
    private WebExtensions extensions = new WebExtensions();
    public boolean workgroupTypeEditable = true;
    public boolean readOnly = false;
    private List<KeyLabelPair> memberTypes = new ArrayList<KeyLabelPair>();

	public WorkgroupForm() {
    	workgroup = KEWServiceLocator.getWorkgroupService().getBlankWorkgroup();
        instructionForCreateNew = Utilities.getApplicationConstant(EdenConstants.WORKGROUP_CREATE_NEW_INSTRUCTION_KEY);
        memberTypes.add(new KeyLabelPair(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD, "User"));
        memberTypes.add(new KeyLabelPair(EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD, "Workgroup"));
    }

    public Long getWorkgroupId() {
    	return workgroupId;
    }

    public void setWorkgroupId(Long workgroupId) {
    	this.workgroupId = workgroupId;
    }

    public String getWorkgroupName() {
		return workgroupName;
	}

	public void setWorkgroupName(String workgroupName) {
		this.workgroupName = workgroupName;
	}

	public String getRouteLogPopup() {
        return Utilities.getApplicationConstant(EdenConstants.WORKGROUP_ROUTE_LOG_POPUP_KEY).trim();
    }

    public Workgroup getExistingWorkgroup() {
        return existingWorkgroup;
    }

    public void setExistingWorkgroup(Workgroup existingWorkgroup) {
        this.existingWorkgroup = existingWorkgroup;
    }

    public WorkgroupType getExistingWorkgroupType() {
		return existingWorkgroupType;
	}

	public void setExistingWorkgroupType(WorkgroupType existingWorkgroupType) {
		this.existingWorkgroupType = existingWorkgroupType;
	}

	public WebExtensions getExistingExtensions() {
		return existingExtensions;
	}

	public void setExistingExtensions(WebExtensions existingExtensions) {
		this.existingExtensions = existingExtensions;
	}

	public String getRemovedMember() {
        return removedMember;
    }

    public void setRemovedMember(String removedMember) {
        this.removedMember = removedMember;
    }

    public String getMethodToCall() {
        return methodToCall;
    }

    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    public String getWorkgroupMember() {
        return workgroupMember;
    }

    public void setWorkgroupMember(String workgroupMember) {
        this.workgroupMember = (workgroupMember == null)? workgroupMember : workgroupMember.trim();
    }

    public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

	public Workgroup getWorkgroup() {
        return workgroup;
    }

    public void setWorkgroup(Workgroup workgroup) {
        this.workgroup = workgroup;
    }

    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        if (workgroup.getGroupNameId() != null && !StringUtils.isEmpty(workgroup.getGroupNameId().getNameId())) {
            workgroup.setGroupNameId(new GroupNameId(workgroup.getGroupNameId().getNameId().trim()));
        }
        if (workgroup.getDescription() != null) {
            workgroup.setDescription(workgroup.getDescription().trim());
        }
        return null;
    }

    public String getShowEdit() {
        return showEdit;
    }

    public void setShowEdit(String showEdit) {
        this.showEdit = showEdit;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
    public String getInstructionForCreateNew() {
        return instructionForCreateNew;
    }
    public void setInstructionForCreateNew(String instructionForCreateNew) {
        this.instructionForCreateNew = instructionForCreateNew;
    }

    public String getSearchLink(){
        return "Lookup.do?lookupableImplServiceName=WorkGroupLookupableImplService";
    }

    public String getSearchLinkText(){
        return "Workgroup Search";
    }
    public String getLookupableImplServiceName() {
        return lookupableImplServiceName;
    }
    public void setLookupableImplServiceName(String lookupableImplServiceName) {
        this.lookupableImplServiceName = lookupableImplServiceName;
    }

    public String getLookupType() {
        return lookupType;
    }
    public void setLookupType(String lookupType) {
        this.lookupType = lookupType;
    }

    public List getWorkgroupMembers() {
		return workgroupMembers;
	}

	public void setWorkgroupMembers(List workgroupMembers) {
		this.workgroupMembers = workgroupMembers;
	}

	public List<WorkgroupType> getWorkgroupTypes() {
		return workgroupTypes;
	}

	public void setWorkgroupTypes(List<WorkgroupType> workgroupTypes) {
		this.workgroupTypes = workgroupTypes;
	}

	/**
     * Returns true if the workgroups on this form represent Routable workgroups
     */
    public boolean isRoutable() {
    	return (existingWorkgroup == null || existingWorkgroup instanceof Routable) &&
    		workgroup instanceof Routable;
    }

    public Routable getRoutableWorkgroup() {
    	return (Routable)getWorkgroup();
    }

    public void setDocId(Long docId) {
    	super.setDocId(docId);
    	if (isRoutable()) {
    		getRoutableWorkgroup().setDocumentId(docId);
    	}
    }

    /**
     * This method grabs the ids and members off of the Workgroup and populates them on the
     * form bean in a manner which can easily be accessed by the JSP Expression Language
     */
    public void loadWebWorkgroupValues() {
    	if (workgroup != null) {
    		WorkflowGroupId groupId = workgroup.getWorkflowGroupId();
    		setWorkgroupId(groupId == null ? null : groupId.getGroupId());
    		GroupNameId nameId = workgroup.getGroupNameId();
    		setWorkgroupName(nameId == null ? "" : nameId.getNameId());
    		for (Recipient member : workgroup.getMembers()) {
    			if (member instanceof WorkflowUser) {
    				WorkflowUser user = (WorkflowUser)member;
    				getWorkgroupMembers().add(new WorkgroupMember(user));
    			} else if (member instanceof Workgroup) {
    				Workgroup nestedWorkgroup = (Workgroup)member;
    				getWorkgroupMembers().add(new WorkgroupMember(nestedWorkgroup));
    			}
    		}
    	}
    }

    public static WorkgroupType createDefaultWorkgroupType() {
    	WorkgroupType workgroupType = new WorkgroupType();
    	workgroupType.setActive(true);
    	workgroupType.setLabel("Default");
    	workgroupType.setName("");
    	return workgroupType;
    }

    /**
     * A simple wrapper to allow the Struts indexed properties to work correctly.
     */
    public static class WorkgroupMember {
    	private String workflowId;
    	private String memberType;
    	private String displayName;
    	private String displayNameSafe;
    	private String authenticationId;
    	public WorkgroupMember() {}
    	public WorkgroupMember(WorkflowUser user) {
    		this.workflowId = user.getWorkflowId();
    		this.memberType = EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD;
    		this.displayName = user.getDisplayName();
    		this.displayNameSafe = user.getDisplayNameSafe();
    		this.authenticationId = user.getAuthenticationUserId().getAuthenticationId();
    	}
    	public WorkgroupMember(Workgroup workgroup) {
    		this.workflowId = workgroup.getWorkflowGroupId().getGroupId().toString();
    		this.memberType = EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD;
    		this.displayName = workgroup.getDisplayName();
    	}
		public String getWorkflowId() {
			return workflowId;
		}
		public void setWorkflowId(String workflowId) {
			this.workflowId = workflowId;
		}
		public String getMemberType() {
			return memberType;
		}
		public void setMemberType(String memberType) {
			this.memberType = memberType;
		}
		public String getAuthenticationId() {
			return authenticationId;
		}
		public void setAuthenticationId(String authenticationId) {
			this.authenticationId = authenticationId;
		}
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		public String getDisplayNameSafe() {
		    return this.displayNameSafe;
		}
		public void setDisplayNameSafe(String displayNameSafe) {
		    this.displayNameSafe = displayNameSafe;
		}

    }

	public String getCurrentWorkgroupType() {
		return currentWorkgroupType;
	}

	public void setCurrentWorkgroupType(String currentWorkgroupType) {
		this.currentWorkgroupType = currentWorkgroupType;
	}

	public WebExtensions getExtensions() {
		return extensions;
	}

	public void setExtensions(WebExtensions extensions) {
		this.extensions = extensions;
	}

	public WorkgroupType getWorkgroupType() {
		return workgroupType;
	}

	public void setWorkgroupType(WorkgroupType workgroupType) {
		this.workgroupType = workgroupType;
	}

    public boolean isWorkgroupTypeEditable() {
		return workgroupTypeEditable;
	}

	public void setWorkgroupTypeEditable(boolean workgroupTypeEditable) {
		this.workgroupTypeEditable = workgroupTypeEditable;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public List<KeyLabelPair> getMemberTypes() {
		return memberTypes;
	}

	public List<WorkgroupMember> getExistingWorkgroupMembers() {
		return existingWorkgroupMembers;
	}

	public void setExistingWorkgroupMembers(List<WorkgroupMember> existingWorkgroupMembers) {
		this.existingWorkgroupMembers = existingWorkgroupMembers;
	}



}