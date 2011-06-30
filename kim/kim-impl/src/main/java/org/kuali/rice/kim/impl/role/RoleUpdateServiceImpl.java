package org.kuali.rice.kim.impl.role;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.jaxb.AttributeSetAdapter;
import org.kuali.rice.core.util.jaxb.SqlDateAdapter;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleUpdateService;
import org.kuali.rice.kim.impl.common.delegate.DelegateBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberAttributeDataBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.SequenceAccessorService;

import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoleUpdateServiceImpl extends RoleServiceBase implements RoleUpdateService {
    private static final Logger LOG = Logger.getLogger(RoleUpdateServiceImpl.class);

    @Override
    public void assignPrincipalToRole(@WebParam(name = "principalId") String principalId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualifier") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifier) throws UnsupportedOperationException {
        // look up the role
        RoleBo role = getRoleBoByName(namespaceCode, roleName);
        role.refreshReferenceObject("members");

        // check that identical member does not already exist
        if (doAnyMemberRecordsMatch(role.getMembers(), principalId, Role.PRINCIPAL_MEMBER_TYPE, qualifier)) {
            return;
        }
        // create the new role member object
        RoleMemberBo newRoleMember = new RoleMemberBo();
        // get a new ID from the sequence
        SequenceAccessorService sas = getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S, RoleMemberBo.class);
        newRoleMember.setRoleMemberId(nextSeq.toString());

        newRoleMember.setRoleId(role.getId());
        newRoleMember.setMemberId(principalId);
        newRoleMember.setMemberTypeCode(Role.PRINCIPAL_MEMBER_TYPE);

        // build role member attribute objects from the given AttributeSet
        addMemberAttributeData(newRoleMember, qualifier, role.getKimTypeId());

        // add row to member table
        // When members are added to roles, clients must be notified.
        getResponsibilityInternalService().saveRoleMember(newRoleMember);
        getIdentityManagementNotificationService().roleUpdated();
    }

    @Override
    public void assignGroupToRole(@WebParam(name = "groupId") String groupId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualifier") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifier) throws UnsupportedOperationException {
        // look up the role
        RoleBo role = getRoleBoByName(namespaceCode, roleName);
        // check that identical member does not already exist
        if (doAnyMemberRecordsMatch(role.getMembers(), groupId, Role.GROUP_MEMBER_TYPE, qualifier)) {
            return;
        }
        // create the new role member object
        RoleMemberBo newRoleMember = new RoleMemberBo();
        // get a new ID from the sequence
        SequenceAccessorService sas = getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S, RoleMemberBo.class);
        newRoleMember.setRoleMemberId(nextSeq.toString());

        newRoleMember.setRoleId(role.getId());
        newRoleMember.setMemberId(groupId);
        newRoleMember.setMemberTypeCode(Role.GROUP_MEMBER_TYPE);

        // build role member attribute objects from the given AttributeSet
        addMemberAttributeData(newRoleMember, qualifier, role.getKimTypeId());

        // When members are added to roles, clients must be notified.
        getResponsibilityInternalService().saveRoleMember(newRoleMember);
        getIdentityManagementNotificationService().roleUpdated();
    }

    @Override
    public void assignRoleToRole(@WebParam(name = "roleId") String roleId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualifier") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifier) throws UnsupportedOperationException {
        // look up the roleBo
        RoleBo roleBo = getRoleBoByName(namespaceCode, roleName);
        // check that identical member does not already exist
        if (doAnyMemberRecordsMatch(roleBo.getMembers(), roleId, Role.ROLE_MEMBER_TYPE, qualifier)) {
            return;
        }
        // Check to make sure this doesn't create a circular membership
        if (!checkForCircularRoleMembership(roleId, roleBo)) {
            throw new IllegalArgumentException("Circular roleBo reference.");
        }
        // create the new roleBo member object
        RoleMemberBo newRoleMember = new RoleMemberBo();
        // get a new ID from the sequence
        SequenceAccessorService sas = getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S, RoleMemberBo.class);
        newRoleMember.setRoleMemberId(nextSeq.toString());

        newRoleMember.setRoleId(roleBo.getId());
        newRoleMember.setMemberId(roleId);
        newRoleMember.setMemberTypeCode(Role.ROLE_MEMBER_TYPE);
        // build roleBo member attribute objects from the given AttributeSet
        addMemberAttributeData(newRoleMember, qualifier, roleBo.getKimTypeId());

        // When members are added to roles, clients must be notified.
        getResponsibilityInternalService().saveRoleMember(newRoleMember);
        getIdentityManagementNotificationService().roleUpdated();
    }

    @Override
    public RoleMember saveRoleMemberForRole(@WebParam(name = "roleMemberId") String roleMemberId, @WebParam(name = "memberId") String memberId, @WebParam(name = "memberTypeCode") String memberTypeCode, @WebParam(name = "roleId") String roleId, @WebParam(name = "qualifications") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifications, @XmlJavaTypeAdapter(value = SqlDateAdapter.class) @WebParam(name = "activeFromDate") Date activeFromDate, @XmlJavaTypeAdapter(value = SqlDateAdapter.class) @WebParam(name = "activeToDate") Date activeToDate) throws UnsupportedOperationException {
        if(StringUtils.isEmpty(roleMemberId) && StringUtils.isEmpty(memberId) && StringUtils.isEmpty(roleId)){
    		throw new IllegalArgumentException("Either Role member ID or a combination of member ID and roleBo ID must be passed in.");
    	}
    	RoleMemberBo origRoleMemberBo;
    	RoleBo roleBo;
    	// create the new roleBo member object
    	RoleMemberBo newRoleMember = new RoleMemberBo();
    	if(StringUtils.isEmpty(roleMemberId)){
	    	// look up the roleBo
	    	roleBo = getRoleBo(roleId);
	    	// check that identical member does not already exist
	    	origRoleMemberBo = matchingMemberRecord( roleBo.getMembers(), memberId, memberTypeCode, qualifications );
    	} else{
    		origRoleMemberBo = getRoleMemberBo(roleMemberId);
    		roleBo = getRoleBo(origRoleMemberBo.getRoleId());
    	}

    	if(origRoleMemberBo !=null){
    		newRoleMember.setRoleMemberId(origRoleMemberBo.getRoleMemberId());
    		newRoleMember.setVersionNumber(origRoleMemberBo.getVersionNumber());
    	} else {
	    	// get a new ID from the sequence
	    	SequenceAccessorService sas = getSequenceAccessorService();
	    	Long nextSeq = sas.getNextAvailableSequenceNumber(
	    			KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S, RoleMemberBo.class);
	    	newRoleMember.setRoleMemberId( nextSeq.toString() );
    	}
    	newRoleMember.setRoleId(roleBo.getId());
    	newRoleMember.setMemberId( memberId );
    	newRoleMember.setMemberTypeCode( memberTypeCode );
		if (activeFromDate != null) {
			newRoleMember.setActiveFromDate(new java.sql.Timestamp(activeFromDate.getTime()));
		}
		if (activeToDate != null) {
			newRoleMember.setActiveToDate(new java.sql.Timestamp(activeToDate.getTime()));
		}
    	// build roleBo member attribute objects from the given AttributeSet
    	addMemberAttributeData( newRoleMember, qualifications, roleBo.getKimTypeId() );

    	// When members are added to roles, clients must be notified.
    	getResponsibilityInternalService().saveRoleMember(newRoleMember);
    	deleteNullMemberAttributeData(newRoleMember.getAttributes());
    	getIdentityManagementNotificationService().roleUpdated();

    	return findRoleMember(newRoleMember.getRoleMemberId());
    }

    @Override
    public void saveRoleRspActions(@WebParam(name = "roleResponsibilityActionId") String roleResponsibilityActionId, @WebParam(name = "roleId") String roleId, @WebParam(name = "roleResponsibilityId") String roleResponsibilityId, @WebParam(name = "roleMemberId") String roleMemberId, @WebParam(name = "actionTypeCode") String actionTypeCode, @WebParam(name = "actionPolicyCode") String actionPolicyCode, @WebParam(name = "priorityNumber") Integer priorityNumber, @WebParam(name = "forceAction") Boolean forceAction) {
        RoleResponsibilityActionBo newRoleRspAction = new RoleResponsibilityActionBo();
		newRoleRspAction.setActionPolicyCode(actionPolicyCode);
		newRoleRspAction.setActionTypeCode(actionTypeCode);
		newRoleRspAction.setPriorityNumber(priorityNumber);
		newRoleRspAction.setForceAction(forceAction);
		newRoleRspAction.setRoleMemberId(roleMemberId);
		newRoleRspAction.setRoleResponsibilityId(roleResponsibilityId);
		if(StringUtils.isEmpty(roleResponsibilityActionId)){
			//If there is an existing one
			Map<String, String> criteria = new HashMap<String, String>(1);
			criteria.put(KimConstants.PrimaryKeyConstants.ROLE_RESPONSIBILITY_ID, roleResponsibilityId);
			criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
			List<RoleResponsibilityActionBo> roleResponsibilityActionImpls = (List<RoleResponsibilityActionBo>)
				getBusinessObjectService().findMatching(RoleResponsibilityActionBo.class, criteria);
			if(roleResponsibilityActionImpls!=null && roleResponsibilityActionImpls.size()>0){
				newRoleRspAction.setId(roleResponsibilityActionImpls.get(0).getId());
				newRoleRspAction.setVersionNumber(roleResponsibilityActionImpls.get(0).getVersionNumber());
			} else{
	//			 get a new ID from the sequence
		    	SequenceAccessorService sas = getSequenceAccessorService();
		    	Long nextSeq = sas.getNextAvailableSequenceNumber(
		    			KimConstants.SequenceNames.KRIM_ROLE_RSP_ACTN_ID_S, RoleResponsibilityActionBo.class);
		    	newRoleRspAction.setId(nextSeq.toString());
			}
		} else{
			Map<String, String> criteria = new HashMap<String, String>(1);
			criteria.put(KimConstants.PrimaryKeyConstants.ROLE_RESPONSIBILITY_ACTION_ID, roleResponsibilityActionId);
			List<RoleResponsibilityActionBo> roleResponsibilityActionImpls = (List<RoleResponsibilityActionBo>)
				getBusinessObjectService().findMatching(RoleResponsibilityActionBo.class, criteria);
			if(CollectionUtils.isNotEmpty(roleResponsibilityActionImpls) && roleResponsibilityActionImpls.size()>0){
				newRoleRspAction.setId(roleResponsibilityActionImpls.get(0).getId());
				newRoleRspAction.setVersionNumber(roleResponsibilityActionImpls.get(0).getVersionNumber());
			}
		}
		getBusinessObjectService().save(newRoleRspAction);
		getIdentityManagementNotificationService().roleUpdated();
    }

    @Override
    public void saveDelegationMemberForRole(@WebParam(name = "assignedToId") String delegationMemberId, @WebParam(name = "roleMemberId") String roleMemberId, @WebParam(name = "memberId") String memberId, @WebParam(name = "memberTypeCode") String memberTypeCode, @WebParam(name = "delegationTypeCode") String delegationTypeCode, @WebParam(name = "roleId") String roleId, @WebParam(name = "qualifications") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifications, @XmlJavaTypeAdapter(value = SqlDateAdapter.class) @WebParam(name = "activeFromDate") Date activeFromDate, @XmlJavaTypeAdapter(value = SqlDateAdapter.class) @WebParam(name = "activeToDate") Date activeToDate) throws UnsupportedOperationException {
        if(StringUtils.isEmpty(delegationMemberId) && StringUtils.isEmpty(memberId) && StringUtils.isEmpty(roleId)){
    		throw new IllegalArgumentException("Either Delegation member ID or a combination of member ID and role ID must be passed in.");
    	}
    	// look up the role
    	RoleBo role = getRoleBo(roleId);
    	DelegateBo delegation = getDelegationOfType(role.getId(), delegationTypeCode);
    	// create the new role member object
    	DelegateMemberBo newDelegationMember = new DelegateMemberBo();

    	DelegateMemberBo origDelegationMember;
    	if(StringUtils.isNotEmpty(delegationMemberId)){
    		origDelegationMember = getDelegateMemberBo(delegationMemberId);
    	} else{
    		List<DelegateMemberBo> origDelegationMembers =
                    this.getDelegationMemberBoListByMemberAndDelegationId(memberId, delegation.getDelegationId());
	    	origDelegationMember =
	    		(origDelegationMembers!=null && origDelegationMembers.size()>0) ? origDelegationMembers.get(0) : null;
    	}
    	if(origDelegationMember!=null){
    		newDelegationMember.setDelegationMemberId(origDelegationMember.getDelegationMemberId());
    		newDelegationMember.setVersionNumber(origDelegationMember.getVersionNumber());
    	} else{
    		newDelegationMember.setDelegationMemberId(getNewDelegationMemberId());
    	}
    	newDelegationMember.setMemberId(memberId);
    	newDelegationMember.setDelegationId(delegation.getDelegationId());
    	newDelegationMember.setRoleMemberId(roleMemberId);
    	newDelegationMember.setTypeCode(memberTypeCode);
		if (activeFromDate != null) {
			newDelegationMember.setActiveFromDate(new java.sql.Timestamp(activeFromDate.getTime()));
		}
		if (activeToDate != null) {
			newDelegationMember.setActiveToDate(new java.sql.Timestamp(activeToDate.getTime()));
		}

    	// build role member attribute objects from the given AttributeSet
    	addDelegationMemberAttributeData( newDelegationMember, qualifications, role.getKimTypeId() );

    	List<DelegateMemberBo> delegationMembers = new ArrayList<DelegateMemberBo>();
    	delegationMembers.add(newDelegationMember);
    	delegation.setMembers(delegationMembers);

    	getBusinessObjectService().save(delegation);
    	for(DelegateMemberBo delegationMember: delegation.getMembers()){
    		deleteNullDelegationMemberAttributeData(delegationMember.getAttributes());
    	}
    	getIdentityManagementNotificationService().roleUpdated();
    }

    @Override
    public void removePrincipalFromRole(@WebParam(name = "principalId") String principalId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualifier") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifier) throws UnsupportedOperationException {
        // look up the role
    	RoleBo role = getRoleBoByName(namespaceCode, roleName);
    	// pull all the principal members
    	role.refreshReferenceObject("members");
    	// look for an exact qualifier match
		for ( RoleMemberBo rm : role.getMembers() ) {
			if ( doesMemberMatch( rm, principalId, Role.PRINCIPAL_MEMBER_TYPE, qualifier ) ) {
		    	// if found, remove
				// When members are removed from roles, clients must be notified.
		    	getResponsibilityInternalService().removeRoleMember(rm);
			}
		}
		getIdentityManagementNotificationService().roleUpdated();
    }

    @Override
    public void removeGroupFromRole(@WebParam(name = "groupId") String groupId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualifier") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifier) throws UnsupportedOperationException {
        // look up the roleBo
    	RoleBo roleBo = getRoleBoByName(namespaceCode, roleName);
    	// pull all the group roleBo members
    	// look for an exact qualifier match
		for ( RoleMemberBo rm : roleBo.getMembers() ) {
			if ( doesMemberMatch( rm, groupId, Role.GROUP_MEMBER_TYPE, qualifier ) ) {
		    	// if found, remove
				// When members are removed from roles, clients must be notified.
		    	getResponsibilityInternalService().removeRoleMember(rm);
			}
		}
		getIdentityManagementNotificationService().roleUpdated();
    }

    @Override
    public void removeRoleFromRole(@WebParam(name = "roleId") String roleId, @WebParam(name = "namespaceCode") String namespaceCode, @WebParam(name = "roleName") String roleName, @WebParam(name = "qualifier") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualifier) throws UnsupportedOperationException {
        // look up the role
    	RoleBo role = getRoleBoByName(namespaceCode, roleName);
    	// pull all the group role members
    	// look for an exact qualifier match
		for ( RoleMemberBo rm : role.getMembers() ) {
			if ( doesMemberMatch( rm, roleId, Role.ROLE_MEMBER_TYPE, qualifier ) ) {
		    	// if found, remove
				// When members are removed from roles, clients must be notified.
		    	getResponsibilityInternalService().removeRoleMember(rm);
			}
		}
		getIdentityManagementNotificationService().roleUpdated();
    }

    @Override
    public void saveRole(@WebParam(name = "roleId") String roleId, @WebParam(name = "roleName") String roleName, @WebParam(name = "roleDescription") String roleDescription, @WebParam(name = "active") boolean active, @WebParam(name = "kimTypeId") String kimTypeId, @WebParam(name = "namespaceCode") String namespaceCode) throws UnsupportedOperationException {
        // look for existing role
        RoleBo role = getBusinessObjectService().findBySinglePrimaryKey(RoleBo.class, roleId);
        if (role == null) {
            role = new RoleBo();
            role.setId(roleId);
        }

        role.setName(roleName);
        role.setDescription(roleDescription);
        role.setActive(active);
        role.setKimTypeId(kimTypeId);
        role.setNamespaceCode(namespaceCode);

        getBusinessObjectService().save(role);
    }

    @Override
    public String getNextAvailableRoleId() throws UnsupportedOperationException {
        Long nextSeq = KRADServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S, RoleBo.class);

        if (nextSeq == null) {
            LOG.error("Unable to get new role id from sequence " + KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S);
            throw new RuntimeException("Unable to get new role id from sequence " + KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S);
        }

        return nextSeq.toString();
    }

    @Override
    public void assignPermissionToRole(String permissionId, String roleId) throws UnsupportedOperationException {
        RolePermissionBo newRolePermission = new RolePermissionBo();

        Long nextSeq = KRADServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_ROLE_PERM_ID_S, RolePermissionBo.class);

        if (nextSeq == null) {
            LOG.error("Unable to get new role permission id from sequence " + KimConstants.SequenceNames.KRIM_ROLE_PERM_ID_S);
            throw new RuntimeException("Unable to get new role permission id from sequence " + KimConstants.SequenceNames.KRIM_ROLE_PERM_ID_S);
        }

        newRolePermission.setId(nextSeq.toString());
        newRolePermission.setRoleId(roleId);
        newRolePermission.setPermissionId(permissionId);
        newRolePermission.setActive(true);

        getBusinessObjectService().save(newRolePermission);
        getIdentityManagementNotificationService().roleUpdated();
    }

    protected void addMemberAttributeData(RoleMemberBo roleMember, AttributeSet qualifier, String kimTypeId) {
        List<RoleMemberAttributeDataBo> attributes = new ArrayList<RoleMemberAttributeDataBo>();
        for (String attributeName : qualifier.keySet()) {
            RoleMemberAttributeDataBo roleMemberAttrBo = new RoleMemberAttributeDataBo();
            roleMemberAttrBo.setAttributeValue(qualifier.get(attributeName));
            roleMemberAttrBo.setKimTypeId(kimTypeId);
            roleMemberAttrBo.setAssignedToId(roleMember.getRoleMemberId());
            // look up the attribute ID
            roleMemberAttrBo.setKimAttributeId(getKimAttributeId(attributeName));

            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put(KimConstants.PrimaryKeyConstants.KIM_ATTRIBUTE_ID, roleMemberAttrBo.getKimAttributeId());
            criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMember.getRoleMemberId());
            List<RoleMemberAttributeDataBo> origRoleMemberAttributes =
                    (List<RoleMemberAttributeDataBo>) getBusinessObjectService().findMatching(RoleMemberAttributeDataBo.class, criteria);
            RoleMemberAttributeDataBo origRoleMemberAttribute =
                    (origRoleMemberAttributes != null && origRoleMemberAttributes.size() > 0) ? origRoleMemberAttributes.get(0) : null;
            if (origRoleMemberAttribute != null) {
                roleMemberAttrBo.setId(origRoleMemberAttribute.getId());
                roleMemberAttrBo.setVersionNumber(origRoleMemberAttribute.getVersionNumber());
            } else {
                // pull the next sequence number for the data ID
                roleMemberAttrBo.setId(getNewAttributeDataId());
            }
            attributes.add(roleMemberAttrBo);
        }
        roleMember.setAttributes(attributes);
    }

    protected void addDelegationMemberAttributeData( DelegateMemberBo delegationMember, AttributeSet qualifier, String kimTypeId ) {
		List<DelegateMemberAttributeDataBo> attributes = new ArrayList<DelegateMemberAttributeDataBo>();
		for ( String attributeName : qualifier.keySet() ) {
			DelegateMemberAttributeDataBo delegateMemberAttrBo = new DelegateMemberAttributeDataBo();
			delegateMemberAttrBo.setAttributeValue(qualifier.get(attributeName));
			delegateMemberAttrBo.setKimTypeId(kimTypeId);
			delegateMemberAttrBo.setAssignedToId(delegationMember.getDelegationMemberId());
			// look up the attribute ID
			delegateMemberAttrBo.setKimAttributeId(getKimAttributeId(attributeName));
	    	Map<String, String> criteria = new HashMap<String, String>();
	    	criteria.put(KimConstants.PrimaryKeyConstants.KIM_ATTRIBUTE_ID, delegateMemberAttrBo.getKimAttributeId());
	    	criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID, delegationMember.getDelegationMemberId());
			List<DelegateMemberAttributeDataBo> origDelegationMemberAttributes =
	    		(List<DelegateMemberAttributeDataBo>)getBusinessObjectService().findMatching(DelegateMemberAttributeDataBo.class, criteria);
			DelegateMemberAttributeDataBo origDelegationMemberAttribute =
	    		(origDelegationMemberAttributes!=null && origDelegationMemberAttributes.size()>0) ? origDelegationMemberAttributes.get(0) : null;
	    	if(origDelegationMemberAttribute!=null){
	    		delegateMemberAttrBo.setId(origDelegationMemberAttribute.getId());
	    		delegateMemberAttrBo.setVersionNumber(origDelegationMemberAttribute.getVersionNumber());
	    	} else{
				// pull the next sequence number for the data ID
				delegateMemberAttrBo.setId(getNewAttributeDataId());
	    	}
			attributes.add( delegateMemberAttrBo );
		}
		delegationMember.setAttributes( attributes );
	}



     // --------------------
    // Persistence Methods
    // --------------------

	private void deleteNullMemberAttributeData(List<RoleMemberAttributeDataBo> attributes) {
		List<RoleMemberAttributeDataBo> attributesToDelete = new ArrayList<RoleMemberAttributeDataBo>();
		for(RoleMemberAttributeDataBo attribute: attributes){
			if(attribute.getAttributeValue()==null){
				attributesToDelete.add(attribute);
			}
		}
		getBusinessObjectService().delete(attributesToDelete);
	}

    private void deleteNullDelegationMemberAttributeData(List<DelegateMemberAttributeDataBo> attributes) {
        List<DelegateMemberAttributeDataBo> attributesToDelete = new ArrayList<DelegateMemberAttributeDataBo>();

		for(DelegateMemberAttributeDataBo attribute: attributes){
			if(attribute.getAttributeValue()==null){
				attributesToDelete.add(attribute);
			}
		}
		getBusinessObjectService().delete(attributesToDelete);
	}
}
