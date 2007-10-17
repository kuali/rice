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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.kuali.workflow.attribute.Extension;
import org.kuali.workflow.workgroup.BaseWorkgroupExtension;
import org.kuali.workflow.workgroup.WorkgroupType;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.Routable;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.web.session.UserSession;
import edu.iu.uis.eden.workgroup.dao.BaseWorkgroupDAO;

/**
 * A simple implementation of the WorkgroupRoutingService
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BaseWorkgroupRoutingService implements WorkgroupRoutingService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BaseWorkgroupRoutingService.class);

    private static final String DEFAULT_DOCUMENT_TYPE = "EDENSERVICE-DOCS.WKGRPREQ";
    private static String WORKGROUP_SEARCHABLE_ATTRIBUTE_NAME = "WorkgroupNameXMLSearchableAttribute";
    private static final String ACTIVE_IND_BLANK = "workgroup.WorkgroupService.activeInd.empty";
    private static final String NAME_BLANK = "workgroup.WorkgroupService.workgroupName.empty";
    private static String WORKGROUP_LOCKED = "workgroup.WorkgroupService.workgroupInRoute";
    private static String NO_MEMBERS = "workgroup.WorkgroupService.members.empty";
    private static String NAME_EXISTS = "workgroup.WorkgroupService.workgroupName.exists";
    private static String INVALID_TYPE = "workgroup.WorkgroupService.type.invalid";
    private BaseWorkgroupDAO workgroupDAO;



    public String getDefaultDocumentTypeName() {
        return DEFAULT_DOCUMENT_TYPE;
    }

    public WorkflowDocument createWorkgroupDocument(UserSession initiator, WorkgroupType workgroupType) throws WorkflowException {
        String documentType = DEFAULT_DOCUMENT_TYPE;
        if (workgroupType != null) {
        	if (!StringUtils.isEmpty(workgroupType.getDocumentTypeName())) {
        		documentType = workgroupType.getDocumentTypeName();
        	}
        }
        return new WorkflowDocument(new WorkflowIdVO(initiator.getWorkflowUser().getWorkflowId()), documentType);
    }

    public Workgroup findByDocumentId(Long documentId) throws EdenUserNotFoundException {
        BaseWorkgroup workgroup = getWorkgroupDAO().findByDocumentId(documentId);
        if (workgroup != null) {
            workgroup.materializeMembers();
        }
        return workgroup;
    }

    public void activateRoutedWorkgroup(Long documentId) throws EdenUserNotFoundException {
        LOG.debug("activating routed workgroup");
        BaseWorkgroup newWorkgroup = (BaseWorkgroup)findByDocumentId(documentId);
        BaseWorkgroup oldWorkgroup = (BaseWorkgroup) getWorkgroupService().getWorkgroup(newWorkgroup.getWorkflowGroupId());

        if (oldWorkgroup != null) {
            oldWorkgroup.setCurrentInd(Boolean.FALSE);
            getWorkgroupService().save(oldWorkgroup);
        }

        newWorkgroup.setCurrentInd(Boolean.TRUE);
        getWorkgroupService().save(newWorkgroup);
        if (oldWorkgroup != null) {
            // if there was an old workgroup then we need to update for member change
            KEWServiceLocator.getActionListService().updateActionItemsForWorkgroupChange(oldWorkgroup, newWorkgroup);
        }
    }

    public Long getLockingDocumentId(GroupId groupId) throws WorkflowException {
        Workgroup workgroup = getEnrouteWorkgroup(groupId);
        if (workgroup == null) {
            return null;
        }
        Routable routableWorkgroup = (Routable)workgroup;
        boolean isCurrent = routableWorkgroup.getCurrentInd().booleanValue();
        boolean isDead = true;
        if (routableWorkgroup.getDocumentId() != null && routableWorkgroup.getDocumentId().longValue() > 0) {
            DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(routableWorkgroup.getDocumentId());
            if (routeHeader == null) {
                LOG.warn("Could not locate locking document with id " + routableWorkgroup.getDocumentId() + ".  This means that the original document " +
                    "id of this workgroup is no longer in the database which could be a data problem!");
            } else {
                isDead = routeHeader.isDisaproved() || routeHeader.isCanceled();
            }
        } else if (routableWorkgroup.getDocumentId() == null || routableWorkgroup.getDocumentId().longValue() == -1) {
            isDead = false;
        }
        return (!isCurrent && !isDead ? routableWorkgroup.getDocumentId() : null);
    }

    public void route(Workgroup workgroup, UserSession user, String annotation) throws WorkflowException {
        BaseWorkgroup simpleWorkgroup = (BaseWorkgroup)workgroup;
        materializeMembersForRouting(simpleWorkgroup);
        validateWorkgroup(simpleWorkgroup);
        if (simpleWorkgroup.getDocumentId() == null) {
            throw new WorkflowException("Workgroup document does not contain a valid document id.");
        }
        WorkflowDocument document = getWorkflowDocumentForRouting(simpleWorkgroup.getDocumentId(), workgroup, user.getWorkflowUser());
        saveWorkgroupForRouting(document, simpleWorkgroup);
        generateXmlContent(document, simpleWorkgroup);
        document.routeDocument(annotation);
    }

    public void blanketApprove(Workgroup workgroup, UserSession user,
            String annotation) throws WorkflowException {
        BaseWorkgroup simpleWorkgroup = (BaseWorkgroup)workgroup;
        materializeMembersForRouting(simpleWorkgroup);
        validateWorkgroup(simpleWorkgroup);
        if (simpleWorkgroup.getDocumentId() == null) {
            throw new WorkflowException("Workgroup document does not contain a valid document id.");
        }
        WorkflowDocument document = getWorkflowDocumentForRouting(simpleWorkgroup.getDocumentId(), workgroup, user.getWorkflowUser());
        saveWorkgroupForRouting(document, simpleWorkgroup);
        generateXmlContent(document, simpleWorkgroup);
        document.blanketApprove(annotation);
    }

    public void versionAndSave(Workgroup workgroup) throws WorkflowException {
		BaseWorkgroup baseWorkgroup = (BaseWorkgroup)workgroup;
		baseWorkgroup.setVersionNumber(new Integer(0));
		baseWorkgroup.setCurrentInd(Boolean.TRUE);
		if (baseWorkgroup.getWorkgroupId() != null) {
			BaseWorkgroup existingWorkgroup = getWorkgroupDAO().findByWorkgroupId(baseWorkgroup.getWorkgroupId());
			existingWorkgroup.setCurrentInd(Boolean.FALSE);
			getWorkgroupDAO().save(existingWorkgroup);
			baseWorkgroup.setVersionNumber(getNextVersionNumber(baseWorkgroup.getWorkflowGroupId()));
		}
		KEWServiceLocator.getWorkgroupService().save(workgroup);
	}

    public void removeWorkgroupInvolvement(Id entityToBeRemoved, List<Long> workgroupIds, Long documentId) throws WorkflowException {
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
	for (Long workgroupId : workgroupIds) {
	    BaseWorkgroup existingWorkgroup = (BaseWorkgroup)KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupId));
	    if (!shouldChangeWorkgroupInvolvement(documentId, existingWorkgroup)) {
		continue;
	    }
	    BaseWorkgroup workgroup = createNewRemoveReplaceVersion(existingWorkgroup, documentId);
	    List<BaseWorkgroupMember> finalMembers = new ArrayList<BaseWorkgroupMember>();
	    for (BaseWorkgroupMember member : workgroup.getWorkgroupMembers()) {
		if (member.getMemberType().equals(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD)) {
		    if (userToRemove != null && member.getWorkflowId().equals(userToRemove.getWorkflowId())) {
			continue;
		    }
		} else if (member.getMemberType().equals(EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD)) {
		    if (workgroupToRemove != null && member.getWorkflowId().equals(workgroupToRemove.getWorkflowGroupId().getGroupId().toString())) {
			continue;
		    }
		}
		finalMembers.add(member);
	    }
	    if (finalMembers.isEmpty()) {
		// deactivate the workgroup instead
		workgroup.setActiveInd(false);
	    } else {
		workgroup.setWorkgroupMembers(finalMembers);
	    }
	    // call to updaateActionItemsForWorkgroupChange below depends on materialized members
	    workgroup.setMembers(new ArrayList<Recipient>());
	    workgroup.materializeMembers();
	    versionAndSave(workgroup);
	    KEWServiceLocator.getActionListService().updateActionItemsForWorkgroupChange(existingWorkgroup, workgroup);
	}
    }

    public void replaceWorkgroupInvolvement(Id entityToBeReplaced, Id newEntity, List<Long> workgroupIds, Long documentId) throws WorkflowException {
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
	for (Long workgroupId : workgroupIds) {
	    BaseWorkgroup existingWorkgroup = (BaseWorkgroup)KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupId));
	    if (!shouldChangeWorkgroupInvolvement(documentId, existingWorkgroup)) {
		continue;
	    }
	    BaseWorkgroup workgroup = createNewRemoveReplaceVersion(existingWorkgroup, documentId);
	    for (BaseWorkgroupMember member : workgroup.getWorkgroupMembers()) {
		if (member.getMemberType().equals(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD)) {
		    if (userToReplace != null && member.getWorkflowId().equals(userToReplace.getWorkflowId())) {
			if (newUser != null) {
			    member.setMemberType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
			    member.setWorkflowId(newUser.getWorkflowId());
			} else if (newWorkgroup != null) {
			    member.setMemberType(EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD);
			    member.setWorkflowId(newWorkgroup.getWorkflowGroupId().getGroupId().toString());
			}
		    }
		} else if (member.getMemberType().equals(EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD)) {
		    if (workgroupToReplace != null && member.getWorkflowId().equals(workgroupToReplace.getWorkflowGroupId().getGroupId().toString())) {
			if (newUser != null) {
			    member.setMemberType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
			    member.setWorkflowId(newUser.getWorkflowId());
			} else if (newWorkgroup != null) {
			    member.setMemberType(EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD);
			    member.setWorkflowId(newWorkgroup.getWorkflowGroupId().getGroupId().toString());
			}
		    }
		}
	    }
	    // call to updaateActionItemsForWorkgroupChange below depends on materialized members
	    workgroup.setMembers(new ArrayList<Recipient>());
	    workgroup.materializeMembers();
	    versionAndSave(workgroup);
	    KEWServiceLocator.getActionListService().updateActionItemsForWorkgroupChange(existingWorkgroup, workgroup);
	}

    }

    /**
     * If a workgroup has been modified and is no longer current since the original request was made, we need to
     * be sure to NOT update the workgroup.
     */
    protected boolean shouldChangeWorkgroupInvolvement(Long documentId, BaseWorkgroup workgroup) throws WorkflowException {
	if (!workgroup.getCurrentInd()) {
	    LOG.warn("Workgroup requested for workgroup involvement change by document " + documentId + " is no longer current.  " +
		    "Change will not be executed!  Workgroup id is: " + workgroup.getWorkgroupId() + " and version number is " + workgroup.getVersionNumber());
	    return false;
	}
	Long lockingDocumentId = KEWServiceLocator.getWorkgroupRoutingService().getLockingDocumentId(workgroup.getWorkflowGroupId());
	if (lockingDocumentId != null) {
	    LOG.warn("Workgroup requested for workgroup involvement change by document " + documentId + " is locked by document " + lockingDocumentId + " and cannot be modified.  " +
		    "Change will not be executed!  Workgroup id is: " + workgroup.getWorkgroupId() + " and version number is " + workgroup.getVersionNumber());
	    return false;
	}
	return true;
    }


    protected BaseWorkgroup createNewRemoveReplaceVersion(BaseWorkgroup workgroup, Long documentId) throws EdenUserNotFoundException {
	BaseWorkgroup copy = (BaseWorkgroup)KEWServiceLocator.getWorkgroupService().copy(workgroup);
	copy.setDocumentId(documentId);
	List<BaseWorkgroupMember> members = new ArrayList<BaseWorkgroupMember>();
	for (BaseWorkgroupMember member : workgroup.getWorkgroupMembers()) {
	    BaseWorkgroupMember copyMember = copyMember(copy, member);
	    members.add(copyMember);
	}
	copy.setWorkgroupMembers(members);
	copy.setMembers(new ArrayList<Recipient>());
	copy.materializeMembers();
	List<Extension> extensions = new ArrayList<Extension>();
	for (Extension extension : copy.getExtensions()) {
	    extensions.add(copyExtension(copy, (BaseWorkgroupExtension)extension));
	}
	copy.setExtensions(extensions);
	return copy;
    }

    protected BaseWorkgroupMember copyMember(BaseWorkgroup workgroup, BaseWorkgroupMember member) {
	BaseWorkgroupMember copy = new BaseWorkgroupMember();
	copy.setMemberType(member.getMemberType());
	copy.setWorkflowId(member.getWorkflowId());
	copy.setWorkgroup(workgroup);
	return copy;
    }

    protected BaseWorkgroupExtension copyExtension(BaseWorkgroup workgroup, BaseWorkgroupExtension extension) {
	BaseWorkgroupExtension copyExtension = new BaseWorkgroupExtension();
	copyExtension.setWorkgroup(workgroup);
	copyExtension.setWorkgroupTypeAttribute(extension.getWorkgroupTypeAttribute());
	return copyExtension;
    }

    protected void materializeMembersForRouting(BaseWorkgroup workgroup) throws WorkflowException {
        workgroup.getWorkgroupMembers().clear();
        for (Recipient member : workgroup.getMembers()) {
        	BaseWorkgroupMember workgroupMember = new BaseWorkgroupMember();
        	workgroupMember.setWorkgroup(workgroup);
        	workgroupMember.setWorkgroupVersionNumber(workgroup.getVersionNumber());
        	if (member instanceof WorkflowUser) {
        		WorkflowUser user = (WorkflowUser)member;
                workgroupMember.setWorkflowId(user.getWorkflowId());
                workgroupMember.setMemberType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
        	} else if (member instanceof Workgroup) {
        		Workgroup nestedWorkgroup = (Workgroup)member;
        		// check for a cycle in the workgroup membership
                if (nestedWorkgroup.hasMember(workgroup)) {
                	throw new WorkflowException("A cycle was detected in workgroup membership.  Workgroup '" + nestedWorkgroup.getGroupNameId().getNameId() + "' has '" + workgroup.getGroupNameId() +"' as a member");
                }
                workgroupMember.setWorkflowId(nestedWorkgroup.getWorkflowGroupId().getGroupId().toString());
                workgroupMember.setMemberType(EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD);
        	} else {
        		LOG.error("Invalid recipient type found for workgroup member when materializing members for routing: " + member.getClass().getName());
        		continue;
        	}
            workgroup.getWorkgroupMembers().add(workgroupMember);
        }
    }

    protected void validateWorkgroup(BaseWorkgroup workgroup) throws WorkflowException {
        List errors = new ArrayList();
        if (workgroup.getWorkflowGroupId() != null && workgroup.getWorkflowGroupId().getGroupId().longValue() > 0) {
            Long lockingDocumentId = getLockingDocumentId(workgroup.getWorkflowGroupId());
            // could have been routed in time user took to route this.
            if (lockingDocumentId != null) {
                errors.add(new WorkflowServiceErrorImpl("Workgroup is currently in route, and locked for changes.", WORKGROUP_LOCKED, "document " + lockingDocumentId));
            }
        }
        if (workgroup.getActiveInd() == null) {
            errors.add(new WorkflowServiceErrorImpl("Workgroup active indicator is empty.", ACTIVE_IND_BLANK));
        }

        String workgroupName = (workgroup.getGroupNameId() == null ? null : workgroup.getGroupNameId().getNameId());
        if (StringUtils.isEmpty(workgroupName)) {
            errors.add(new WorkflowServiceErrorImpl("Workgroup name is empty.", NAME_BLANK));
        } else {
            Workgroup existingWorkgroup = getWorkgroupService().getWorkgroup(workgroup.getGroupNameId());
            if (existingWorkgroup != null && existingWorkgroup.getActiveInd().booleanValue()) {
                if (workgroup.getWorkgroupId() == null) {
                    errors.add(new WorkflowServiceErrorImpl("Workgroup name already in use.", NAME_EXISTS));
                } else if (existingWorkgroup.getWorkflowGroupId().getGroupId().intValue() != workgroup.getWorkgroupId().intValue()) {
                    errors.add(new WorkflowServiceErrorImpl("Workgroup name already in use.", NAME_EXISTS));
                }
            }
        }

        if (!StringUtils.isBlank(workgroup.getWorkgroupType())) {
            WorkgroupType workgroupType = KEWServiceLocator.getWorkgroupTypeService().findByName(workgroup.getWorkgroupType());
            if (workgroupType == null) {
                errors.add(new WorkflowServiceErrorImpl("Could not locate the workgroup type '" + workgroup.getWorkgroupType() + "' defined on the workgroup.", INVALID_TYPE, workgroup.getWorkgroupType()));
            }
        }

        if (workgroup.getWorkgroupMembers().isEmpty()) {
            errors.add(new WorkflowServiceErrorImpl("Workgroup does not have members.", NO_MEMBERS));
        }

        LOG.debug("Exit validateWorkgroup(..) ");
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("Workgroup Validation Error", errors);
        }
    }

    protected void generateXmlContent(WorkflowDocument document, BaseWorkgroup workgroup) {
    	ExportDataSet dataSet = new ExportDataSet(ExportFormat.XML);
    	dataSet.getWorkgroups().add(workgroup);
    	Element element = KEWServiceLocator.getWorkgroupService().export(dataSet);
    	String xmlContent = XmlHelper.jotNode(element);
    	document.setApplicationContent(xmlContent);
    }

    protected WorkflowDocument getWorkflowDocumentForRouting(Long routeHeaderId, Workgroup workgroup, WorkflowUser user) throws WorkflowException {
        WorkflowDocument workflowDocument = new WorkflowDocument(new WorkflowIdVO(user.getWorkflowId()), routeHeaderId);
        workflowDocument.setTitle(getDocTitle(workgroup));
        addSearchableAttributeDefinitions(workflowDocument, workgroup);
        return workflowDocument;
    }

    protected void saveWorkgroupForRouting(WorkflowDocument document, BaseWorkgroup workgroup) throws WorkflowException {
        Routable routableWorkgroup = (Routable)workgroup;
        Integer versionNumber = new Integer(0);
        if (workgroup.getWorkflowGroupId() == null || workgroup.getWorkflowGroupId().getGroupId() == null ||
                workgroup.getWorkflowGroupId().getGroupId().longValue() <= 0) {
            workgroup.setWorkflowGroupId(new WorkflowGroupId(document.getRouteHeaderId()));
        } else {
            versionNumber = getNextVersionNumber(workgroup.getWorkflowGroupId());
        }
        routableWorkgroup.setDocumentId(document.getRouteHeaderId());
        routableWorkgroup.setCurrentInd(Boolean.FALSE);
        workgroup.setVersionNumber(versionNumber);
        KEWServiceLocator.getWorkgroupService().save(workgroup);
    }

    protected Integer getNextVersionNumber(GroupId groupId) throws WorkflowException {
        BaseWorkgroup workgroup = (BaseWorkgroup)getEnrouteWorkgroup(groupId);
        if (workgroup == null) {
            return new Integer(0);
        }
        return new Integer(workgroup.getVersionNumber().intValue() + 1);
    }

    /**
     * Constructs the title for the Workgroup Document.
     */
    protected String getDocTitle(Workgroup workgroup) {
        return ("Routing workgroup " + workgroup.getGroupNameId().getNameId());
    }

    /**
     * Adds the searchable attribute definitions to this document to allow for searching by Workgroup Name
     */
    protected void addSearchableAttributeDefinitions(WorkflowDocument document, Workgroup workgroup) {
        RuleAttribute searchableAttribute = KEWServiceLocator.getRuleAttributeService().findByName(WORKGROUP_SEARCHABLE_ATTRIBUTE_NAME);
        if (searchableAttribute != null) {
            WorkflowAttributeDefinitionVO xmldef = new WorkflowAttributeDefinitionVO(WORKGROUP_SEARCHABLE_ATTRIBUTE_NAME);
            xmldef.addProperty("wrkgrp_nm", workgroup.getGroupNameId().getNameId());
            document.addSearchableDefinition(xmldef);
        }
    }

    /**
     * Returns the currently enroute workgroup for the given id.  Used to determine whether or not the Workgroup is
     * locked for changes.
     */
    protected BaseWorkgroup getEnrouteWorkgroup(GroupId groupId) throws WorkflowException {
        BaseWorkgroup workgroup = null;
        if (groupId instanceof WorkflowGroupId) {
            workgroup = getWorkgroupDAO().findEnrouteWorkgroupById(((WorkflowGroupId)groupId).getGroupId());
        } else if (groupId instanceof GroupNameId) {
            workgroup = getWorkgroupDAO().findEnrouteWorkgroupByName(((GroupNameId)groupId).getNameId());
        } else {
            throw new WorkflowException("Invalid GroupId type: " + groupId);
        }
        if (workgroup == null) {
            LOG.warn("Received null retrieving workgroup by " + groupId);
        } else {
            workgroup.materializeMembers();
        }
        return workgroup;
    }

    protected WorkgroupService getWorkgroupService() {
        return KEWServiceLocator.getWorkgroupService();
    }

    protected BaseWorkgroupDAO getWorkgroupDAO() {
        return workgroupDAO;
    }

    public void setWorkgroupDAO(BaseWorkgroupDAO workgroupDAO) {
        this.workgroupDAO = workgroupDAO;
    }

}
