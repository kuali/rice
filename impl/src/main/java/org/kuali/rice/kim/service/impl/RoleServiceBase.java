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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.dto.DelegateMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityActionInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementNotificationService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.ResponsibilityInternalService;
import org.kuali.rice.kim.service.support.KimDelegationTypeService;
import org.kuali.rice.kim.service.support.KimRoleTypeService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.ksb.cache.RiceCacheAdministrator;
import org.kuali.rice.ksb.service.KSBServiceLocator;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RoleServiceBase {
	protected static final String ROLE_IMPL_CACHE_PREFIX = "RoleImpl-ID-";
	protected static final String ROLE_IMPL_BY_NAME_CACHE_PREFIX = "RoleImpl-Name-";
	protected static final String ROLE_IMPL_CACHE_GROUP = "RoleImpl";
	
	private BusinessObjectService businessObjectService;
	private LookupService lookupService;
	private RiceCacheAdministrator cacheAdministrator;
	private SequenceAccessorService sequenceAccessorService;
	private IdentityManagementService identityManagementService;
	private ResponsibilityInternalService responsibilityInternalService;

    private Map<String,KimRoleTypeService> roleTypeServiceCache = Collections.synchronizedMap( new HashMap<String,KimRoleTypeService>() );
	private Map<String,KimDelegationTypeService> delegationTypeServiceCache = Collections.synchronizedMap( new HashMap<String,KimDelegationTypeService>() );

	private Map<String,Boolean> applicationRoleTypeCache = Collections.synchronizedMap( new HashMap<String,Boolean>() );

	public RoleMemberCompleteInfo findRoleMemberCompleteInfo(String roleMemberId){
    	Map<String, String> fieldValues = new HashMap<String, String>();
    	fieldValues.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
    	List<RoleMemberCompleteInfo> roleMemberInfos = findRoleMembersCompleteInfo(fieldValues);
    	if(roleMemberInfos!=null && roleMemberInfos.size()>0)
    		return roleMemberInfos.get(0);
    	return null;
    }
	
	public List<RoleMemberCompleteInfo> findRoleMembersCompleteInfo(Map<String, String> fieldValues){
    	List<RoleMemberCompleteInfo> roleMembersCompleteInfos = new ArrayList<RoleMemberCompleteInfo>();
    	RoleMemberCompleteInfo roleMembersCompleteInfo;
    	List<RoleMemberImpl> roleMembers = (List<RoleMemberImpl>)getLookupService().findCollectionBySearchHelper(
				RoleMemberImpl.class, fieldValues, true);
    	for(RoleMemberImpl roleMember: roleMembers){
    		roleMembersCompleteInfo = roleMember.toSimpleInfo();
			BusinessObject member = getMember(roleMembersCompleteInfo.getMemberTypeCode(), roleMembersCompleteInfo.getMemberId());
			roleMembersCompleteInfo.setMemberName(getMemberName(roleMembersCompleteInfo.getMemberTypeCode(), member));
			roleMembersCompleteInfo.setMemberNamespaceCode(getMemberNamespaceCode(roleMembersCompleteInfo.getMemberTypeCode(), member));
			roleMembersCompleteInfo.setRoleRspActions(getRoleMemberResponsibilityActionInfo(roleMember.getRoleMemberId()));
			roleMembersCompleteInfos.add(roleMembersCompleteInfo);
    	}
    	return roleMembersCompleteInfos;
    }
	
	public List<RoleResponsibilityActionInfo> getRoleMemberResponsibilityActionInfo(String roleMemberId){
		Map<String, String> criteria = new HashMap<String, String>(1);		
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
		List<RoleResponsibilityActionImpl> responsibilityImpls = (List<RoleResponsibilityActionImpl>)
			getBusinessObjectService().findMatching(RoleResponsibilityActionImpl.class, criteria);
		List<RoleResponsibilityActionInfo> roleResponsibilityActionInfos = new ArrayList<RoleResponsibilityActionInfo>();
		RoleResponsibilityActionInfo roleResponsibilityActionInfo;
		for(RoleResponsibilityActionImpl responsibilityActionImpl: responsibilityImpls){
			roleResponsibilityActionInfo = new RoleResponsibilityActionInfo();
			KimCommonUtils.copyProperties(roleResponsibilityActionInfo, responsibilityActionImpl);
			roleResponsibilityActionInfos.add(roleResponsibilityActionInfo);
		}
		return roleResponsibilityActionInfos;
	}
	
	public List<DelegateMemberCompleteInfo> findDelegateMembersCompleteInfo(final Map<String, String> fieldValues){
    	List<DelegateMemberCompleteInfo> delegateMembersCompleteInfo = new ArrayList<DelegateMemberCompleteInfo>();
    	DelegateMemberCompleteInfo delegateMemberCompleteInfo;
    	List<KimDelegationImpl> delegations = (List<KimDelegationImpl>)getLookupService().findCollectionBySearchHelper(
				KimDelegationImpl.class, fieldValues, true);
    	if(delegations!=null && !delegations.isEmpty()){
    		Map<String, String> delegationMemberFieldValues = new HashMap<String, String>();
    		for(String key: fieldValues.keySet()){
    			if(key.startsWith(KimConstants.KimUIConstants.MEMBER_ID_PREFIX)){
    				delegationMemberFieldValues.put(
    						key.substring(key.indexOf(
    						KimConstants.KimUIConstants.MEMBER_ID_PREFIX)+KimConstants.KimUIConstants.MEMBER_ID_PREFIX.length()), 
    						fieldValues.get(key));
    			}
    		}
			StringBuffer memberQueryString = new StringBuffer();
	    	for(KimDelegationImpl delegation: delegations)
	    		memberQueryString.append(delegation.getDelegationId()+KimConstants.KimUIConstants.OR_OPERATOR);
	    	delegationMemberFieldValues.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, 
	    			KimCommonUtils.stripEnd(memberQueryString.toString(), KimConstants.KimUIConstants.OR_OPERATOR));
	    	List<KimDelegationMemberImpl> delegateMembers = (List<KimDelegationMemberImpl>)getLookupService().findCollectionBySearchHelper(
					KimDelegationMemberImpl.class, delegationMemberFieldValues, true);
	    	KimDelegationImpl delegationTemp;
	    	for(KimDelegationMemberImpl delegateMember: delegateMembers){
	    		delegateMemberCompleteInfo = delegateMember.toSimpleInfo();
	    		delegationTemp = getDelegationImpl(delegations, delegateMember.getDelegationId());
	    		delegateMemberCompleteInfo.setRoleId(delegationTemp.getRoleId());
	    		delegateMemberCompleteInfo.setDelegationTypeCode(delegationTemp.getDelegationTypeCode());
				BusinessObject member = getMember(delegateMemberCompleteInfo.getMemberTypeCode(), delegateMemberCompleteInfo.getMemberId());
				delegateMemberCompleteInfo.setMemberName(getMemberName(delegateMemberCompleteInfo.getMemberTypeCode(), member));
				delegateMemberCompleteInfo.setMemberNamespaceCode(getMemberNamespaceCode(delegateMemberCompleteInfo.getMemberTypeCode(), member));
				delegateMembersCompleteInfo.add(delegateMemberCompleteInfo);
	    	}

    	}
    	return delegateMembersCompleteInfo;
    }
	
	protected RoleMemberImpl getRoleMemberImpl( String roleMemberId ) {
    	return (RoleMemberImpl)getBusinessObjectService().findByPrimaryKey(
    			RoleMemberImpl.class,
    			Collections.singletonMap(KIMPropertyConstants.RoleMember.ROLE_MEMBER_ID, roleMemberId) );
    }
	
	protected KimDelegationImpl getDelegationImpl(List<KimDelegationImpl> delegations, String delegationId){
    	if(StringUtils.isEmpty(delegationId) || delegations==null)
    		return null;
    	for(KimDelegationImpl delegation: delegations){
    		if(StringUtils.equals(delegation.getDelegationId(), delegationId))
    			return delegation;
    	}
    	return null;
    }
	
	protected BusinessObject getMember(String memberTypeCode, String memberId){
        Class<? extends BusinessObject> roleMemberTypeClass = null;
        String roleMemberIdName = "";
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
            roleMemberTypeClass = KimPrincipalImpl.class;
            roleMemberIdName = KimConstants.PrimaryKeyConstants.PRINCIPAL_ID;
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
            roleMemberTypeClass = GroupImpl.class;
            roleMemberIdName = KimConstants.PrimaryKeyConstants.GROUP_ID;
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
            roleMemberTypeClass = RoleImpl.class;
            roleMemberIdName = KimConstants.PrimaryKeyConstants.ROLE_ID;
        }
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(roleMemberIdName, memberId);
        return KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(roleMemberTypeClass, criteria);
    }
	
	protected String getMemberName(String memberTypeCode, BusinessObject member){
        String roleMemberName = "";
        if(member==null) return roleMemberName;
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
            roleMemberName = ((KimPrincipalImpl)member).getPrincipalName();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
            roleMemberName = ((GroupImpl)member).getGroupName();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
            roleMemberName = ((RoleImpl)member).getRoleName();
        }
        return roleMemberName;
    }
	
	protected String getMemberNamespaceCode(String memberTypeCode, BusinessObject member){
        String roleMemberNamespaceCode = "";
        if(member==null) return roleMemberNamespaceCode;
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
            roleMemberNamespaceCode = "";
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
            roleMemberNamespaceCode = ((GroupImpl)member).getNamespaceCode();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
            roleMemberNamespaceCode = ((RoleImpl)member).getNamespaceCode();
        }
        return roleMemberNamespaceCode;
    }
	
	protected RoleImpl getRoleImpl(String roleId) {
		if ( StringUtils.isBlank( roleId ) ) {
			return null;
		}
		// check for a non-null result in the cache, return it if found
		RoleImpl cachedResult = getRoleFromCache( roleId );
		if ( cachedResult != null ) {
			return cachedResult;
		}
		// otherwise, run the query
		RoleImpl result = (RoleImpl)getBusinessObjectService().findBySinglePrimaryKey(RoleImpl.class, roleId);
		addRoleImplToCache( result );
		return result;
	}
	
	protected KimDelegationImpl getDelegationOfType(String roleId, String delegationTypeCode){
    	List<KimDelegationImpl> roleDelegations = getRoleDelegations(roleId);
        if(isDelegationPrimary(delegationTypeCode))
            return getPrimaryDelegation(roleId, roleDelegations);
        else
            return getSecondaryDelegation(roleId, roleDelegations);
    }
	
	private KimDelegationImpl getSecondaryDelegation(String roleId, List<KimDelegationImpl> roleDelegations){
        KimDelegationImpl secondaryDelegation = null;
        RoleImpl roleImpl = getRoleImpl(roleId);
        for(KimDelegationImpl delegation: roleDelegations){
            if(isDelegationSecondary(delegation.getDelegationTypeCode()))
                secondaryDelegation = delegation;
        }
        if(secondaryDelegation==null){
            secondaryDelegation = new KimDelegationImpl();
            secondaryDelegation.setRoleId(roleId);
            secondaryDelegation.setDelegationId(getNewDelegationId());
            secondaryDelegation.setDelegationTypeCode(KEWConstants.DELEGATION_SECONDARY);
            secondaryDelegation.setKimTypeId(roleImpl.getKimTypeId());
        }
        return secondaryDelegation;
    }
	
	protected KimDelegationImpl getPrimaryDelegation(String roleId, List<KimDelegationImpl> roleDelegations){
        KimDelegationImpl primaryDelegation = null;
        RoleImpl roleImpl = getRoleImpl(roleId);
        for(KimDelegationImpl delegation: roleDelegations){
            if(isDelegationPrimary(delegation.getDelegationTypeCode()))
                primaryDelegation = delegation;
        }
        if(primaryDelegation==null){
            primaryDelegation = new KimDelegationImpl();
            primaryDelegation.setRoleId(roleId);
            primaryDelegation.setDelegationId(getNewDelegationId());
            primaryDelegation.setDelegationTypeCode(KEWConstants.DELEGATION_PRIMARY);
            primaryDelegation.setKimTypeId(roleImpl.getKimTypeId());
        }
        return primaryDelegation;
    }
	
	protected RoleMemberImpl matchingMemberRecord( List<RoleMemberImpl> roleMembers, String memberId, String memberTypeCode, AttributeSet qualifier ) {
		for ( RoleMemberImpl rm : roleMembers ) {
			if ( doesMemberMatch( rm, memberId, memberTypeCode, qualifier ) ) {
				return rm;
			}
		}
		return null;
	}
	
	protected boolean isDelegationPrimary(String delegationTypeCode){
        return KEWConstants.DELEGATION_PRIMARY.equals(delegationTypeCode);
    }
	
	protected boolean isDelegationSecondary(String delegationTypeCode){
        return KEWConstants.DELEGATION_SECONDARY.equals(delegationTypeCode);
    }
	
	
	private List<KimDelegationImpl> getRoleDelegations(String roleId){
		if(roleId==null)
			return new ArrayList<KimDelegationImpl>();
		Map<String,String> criteria = new HashMap<String,String>(1);
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
		return (List<KimDelegationImpl>)getBusinessObjectService().findMatching(KimDelegationImpl.class, criteria);
	}
	
	protected RoleImpl getRoleImplByName( String namespaceCode, String roleName ) {
		if ( StringUtils.isBlank( namespaceCode )
				|| StringUtils.isBlank( roleName ) ) {
			return null;
		}
		// check for a non-null result in the cache, return it if found
		RoleImpl cachedResult = getRoleFromCache( namespaceCode, roleName );
		if ( cachedResult != null ) {
			return cachedResult;
		}
		AttributeSet criteria = new AttributeSet();
		criteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
		criteria.put(KimConstants.UniqueKeyConstants.ROLE_NAME, roleName);
		criteria.put(KNSPropertyConstants.ACTIVE, "Y");
		// while this is not actually the primary key - there will be at most one row with these criteria
		RoleImpl result = (RoleImpl)getBusinessObjectService().findByPrimaryKey(RoleImpl.class, criteria);
		addRoleImplToCache( result );
		return result;
	}
	
	protected boolean doAnyMemberRecordsMatch( List<RoleMemberImpl> roleMembers, String memberId, String memberTypeCode, AttributeSet qualifier ) {
		for ( RoleMemberImpl rm : roleMembers ) {
			if ( doesMemberMatch( rm, memberId, memberTypeCode, qualifier ) ) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean doesMemberMatch( RoleMemberImpl roleMember, String memberId, String memberTypeCode, AttributeSet qualifier ) {
		if ( roleMember.getMemberId().equals( memberId ) && roleMember.getMemberTypeCode().equals( memberTypeCode ) ) {
			// member ID/type match
    		AttributeSet roleQualifier = roleMember.getQualifier();
    		if ( (qualifier == null || qualifier.isEmpty())
    				&& (roleQualifier == null || roleQualifier.isEmpty()) ) {
    			return true; // blank qualifier match
    		} else {
    			if ( qualifier != null && roleQualifier != null && qualifier.equals( roleQualifier ) ) {
    				return true; // qualifier match
    			}
    		}
		}
		return false;
	}
	
    /**
	 * @return the applicationRoleTypeCache
	 */
	protected Map<String, Boolean> getApplicationRoleTypeCache() {
		return this.applicationRoleTypeCache;
	}
	/**
	 * @return the roleTypeServiceCache
	 */
	protected Map<String, KimRoleTypeService> getRoleTypeServiceCache() {
		return this.roleTypeServiceCache;
	}

	/**
	 * @return the delegationTypeServiceCache
	 */
	protected Map<String, KimDelegationTypeService> getDelegationTypeServiceCache() {
		return this.delegationTypeServiceCache;
	}
	protected String getRoleCacheKey( String roleId ) {
    	return ROLE_IMPL_CACHE_PREFIX + roleId;
    }

    protected String getRoleByNameCacheKey( String namespaceCode, String roleName ) {
    	return ROLE_IMPL_BY_NAME_CACHE_PREFIX + namespaceCode + "-" + roleName;
    	}

    protected void addRoleImplToCache( RoleImpl role ) {
    	if (role != null) {
	    	getCacheAdministrator().putInCache(getRoleCacheKey(role.getRoleId()), role, ROLE_IMPL_CACHE_GROUP);
	    	getCacheAdministrator().putInCache(getRoleByNameCacheKey(role.getNamespaceCode(),role.getRoleName()), role, ROLE_IMPL_CACHE_GROUP);
    		}
    	}

    protected RoleImpl getRoleFromCache( String roleId ) {
    	return (RoleImpl)getCacheAdministrator().getFromCache(getRoleCacheKey(roleId));
    }

    protected RoleImpl getRoleFromCache( String namespaceCode, String roleName ) {
    	return (RoleImpl)getCacheAdministrator().getFromCache(getRoleByNameCacheKey(namespaceCode,roleName));
    }
    
    protected String getNewDelegationId(){
    	SequenceAccessorService sas = getSequenceAccessorService();
    	Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_DLGN_ID_S,
                KimDelegationImpl.class);
        return nextSeq.toString();
    }

    protected String getNewAttributeDataId(){
		SequenceAccessorService sas = getSequenceAccessorService();		
		Long nextSeq = sas.getNextAvailableSequenceNumber(
				KimConstants.SequenceNames.KRIM_ATTR_DATA_ID_S, 
				RoleMemberAttributeDataImpl.class );
		return nextSeq.toString();
    }
    
    protected String getNewDelegationMemberId(){
    	SequenceAccessorService sas = getSequenceAccessorService();
    	Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_DLGN_MBR_ID_S,
                KimDelegationImpl.class);
        return nextSeq.toString();
    }
    
    protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
    
    /**
	 * @return the lookupService
	 */
    protected LookupService getLookupService() {
		if(lookupService == null) {
			lookupService = KNSServiceLocator.getLookupService();
		}
		return lookupService;
	}
    
    protected RiceCacheAdministrator getCacheAdministrator() {
		if ( cacheAdministrator == null ) {
			cacheAdministrator = KEWServiceLocator.getCacheAdministrator();
		}
		return cacheAdministrator;
    }
    
    protected IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();
		}

		return identityManagementService;
	}

	protected SequenceAccessorService getSequenceAccessorService() {
		if ( sequenceAccessorService == null ) {
			sequenceAccessorService = KNSServiceLocator.getSequenceAccessorService();
		}
		return sequenceAccessorService;
	}
	
	protected ResponsibilityInternalService getResponsibilityInternalService() {
		if ( responsibilityInternalService == null ) {
			responsibilityInternalService = KIMServiceLocator.getResponsibilityInternalService();
		}
		return responsibilityInternalService;
	}
	
	protected IdentityManagementNotificationService getIdentityManagementNotificationService() {
        return (IdentityManagementNotificationService)KSBServiceLocator.getMessageHelper().getServiceAsynchronously(new QName("KIM", "kimIdentityManagementNotificationService"));
    }
}
