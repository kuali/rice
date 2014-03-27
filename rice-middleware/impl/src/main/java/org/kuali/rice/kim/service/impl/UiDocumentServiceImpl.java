/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.PredicateUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.uif.RemotableCheckbox;
import org.kuali.rice.core.api.uif.RemotableCheckboxGroup;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.kim.api.identity.address.EntityAddressContract;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.email.EntityEmailContract;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.phone.EntityPhone;
import org.kuali.rice.kim.api.identity.phone.EntityPhoneContract;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.responsibility.ResponsibilityService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.bo.ui.GroupDocumentMember;
import org.kuali.rice.kim.bo.ui.GroupDocumentQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRolePermission;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibilityAction;
import org.kuali.rice.kim.bo.ui.PersonDocumentAddress;
import org.kuali.rice.kim.bo.ui.PersonDocumentAffiliation;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmail;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmploymentInfo;
import org.kuali.rice.kim.bo.ui.PersonDocumentGroup;
import org.kuali.rice.kim.bo.ui.PersonDocumentName;
import org.kuali.rice.kim.bo.ui.PersonDocumentPhone;
import org.kuali.rice.kim.bo.ui.PersonDocumentPrivacy;
import org.kuali.rice.kim.bo.ui.PersonDocumentRole;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMemberQualifier;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.framework.services.KimFrameworkServiceLocator;
import org.kuali.rice.kim.framework.type.KimTypeService;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberAttributeDataBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateTypeBo;
import org.kuali.rice.kim.impl.group.GroupAttributeBo;
import org.kuali.rice.kim.impl.group.GroupBo;
import org.kuali.rice.kim.impl.group.GroupMemberBo;
import org.kuali.rice.kim.impl.identity.IdentityArchiveService;
import org.kuali.rice.kim.impl.identity.address.EntityAddressBo;
import org.kuali.rice.kim.impl.identity.address.EntityAddressTypeBo;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationBo;
import org.kuali.rice.kim.impl.identity.affiliation.EntityAffiliationTypeBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailTypeBo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentBo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentStatusBo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentTypeBo;
import org.kuali.rice.kim.impl.identity.entity.EntityBo;
import org.kuali.rice.kim.impl.identity.name.EntityNameBo;
import org.kuali.rice.kim.impl.identity.name.EntityNameTypeBo;
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneBo;
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneTypeBo;
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo;
import org.kuali.rice.kim.impl.identity.privacy.EntityPrivacyPreferencesBo;
import org.kuali.rice.kim.impl.identity.type.EntityTypeContactInfoBo;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityInternalService;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.role.RoleBoLite;
import org.kuali.rice.kim.impl.role.RoleMemberAttributeDataBo;
import org.kuali.rice.kim.impl.role.RoleMemberBo;
import org.kuali.rice.kim.impl.role.RolePermissionBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityActionBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityBo;
import org.kuali.rice.kim.impl.services.KimImplServiceLocator;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.util.KimCommonUtilsInternal;
import org.kuali.rice.kns.datadictionary.exporter.AttributesMapBuilder;
import org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceHelper;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.exporter.ExportMap;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This is a description of what this class does - shyu don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@SuppressWarnings("deprecation")
public class UiDocumentServiceImpl implements UiDocumentService {
	private static final Logger LOG = Logger.getLogger(UiDocumentServiceImpl.class);
	private static final String SHOW_BLANK_QUALIFIERS = "kim.show.blank.qualifiers";
    private static final String KIM_IDENTITY_ARCHIVE_SERVICE = "kimIdentityArchiveService";

    private RoleService roleService;
	private DataObjectService dataObjectService;
	private IdentityService identityService;
    private PermissionService permissionService;
	private GroupService groupService;
	private ResponsibilityService responsibilityService;
    private ResponsibilityInternalService responsibilityInternalService;
	private KimTypeInfoService kimTypeInfoService;
    private DocumentHelperService documentHelperService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;

    /**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
	@Override
    public void saveEntityPerson(
	    IdentityManagementPersonDocument identityManagementPersonDocument) {
		EntityBo kimEntity = new EntityBo();
		EntityBo origEntity = getEntityBo(identityManagementPersonDocument.getEntityId());
		boolean creatingNew = true;
		if (origEntity == null) {
			origEntity = new EntityBo();
			kimEntity.setActive(true);
		} else {
			// TODO : in order to resolve optimistic locking issue. has to get identity and set the version number if identity records matched
			// Need to look into this.
			//kimEntity = origEntity;
			kimEntity.setActive(origEntity.isActive());
			kimEntity.setVersionNumber(origEntity.getVersionNumber());
			creatingNew = false;
		}

		kimEntity.setId(identityManagementPersonDocument.getEntityId());
		String initiatorPrincipalId = getInitiatorPrincipalId(identityManagementPersonDocument);
		boolean inactivatingPrincipal = false;
		if(canModifyEntity(initiatorPrincipalId, identityManagementPersonDocument.getPrincipalId())){
			inactivatingPrincipal = setupPrincipal(identityManagementPersonDocument, kimEntity, origEntity.getPrincipals());
			setupAffiliation(identityManagementPersonDocument, kimEntity, origEntity.getAffiliations(), origEntity.getEmploymentInformation());
			setupName(identityManagementPersonDocument, kimEntity, origEntity.getNames());
		// entitytype
			List<EntityTypeContactInfoBo> entityTypes = new ArrayList<EntityTypeContactInfoBo>();
			EntityTypeContactInfoBo entityType = new EntityTypeContactInfoBo();
			entityType.setEntityId(identityManagementPersonDocument.getEntityId());
			entityType.setEntityTypeCode(KimConstants.EntityTypes.PERSON);
			entityType.setActive(true);
			entityTypes.add(entityType);
			EntityTypeContactInfoBo origEntityType = new EntityTypeContactInfoBo();
			for (EntityTypeContactInfoBo type : origEntity.getEntityTypeContactInfos()) {
				// should check identity.entitytypeid, but it's not persist in persondoc yet
				if (type.getEntityTypeCode()!=null && StringUtils.equals(type.getEntityTypeCode(), entityType.getEntityTypeCode())) {
					origEntityType = type;
					entityType.setVersionNumber(type.getVersionNumber());
					entityType.setActive(type.isActive());
				}
			}
			setupPhone(identityManagementPersonDocument, entityType, origEntityType.getPhoneNumbers());
			setupEmail(identityManagementPersonDocument, entityType, origEntityType.getEmailAddresses());
			setupAddress(identityManagementPersonDocument, entityType, origEntityType.getAddresses());
            kimEntity.setEntityTypeContactInfos(entityTypes);
		} else{
			if(ObjectUtils.isNotNull(origEntity.getExternalIdentifiers())) {
                kimEntity.setExternalIdentifiers(origEntity.getExternalIdentifiers());
            }
			if(ObjectUtils.isNotNull(origEntity.getEmploymentInformation())) {
                kimEntity.setEmploymentInformation(origEntity.getEmploymentInformation());
            }
			if(ObjectUtils.isNotNull(origEntity.getAffiliations())) {
                kimEntity.setAffiliations(origEntity.getAffiliations());
            }
			if(ObjectUtils.isNotNull(origEntity.getNames())) {
                kimEntity.setNames(origEntity.getNames());
            }
			if(ObjectUtils.isNotNull(origEntity.getEntityTypeContactInfos())) {
                kimEntity.setEntityTypeContactInfos(origEntity.getEntityTypeContactInfos());
            }
		}
		if(creatingNew || canOverrideEntityPrivacyPreferences(getInitiatorPrincipalId(identityManagementPersonDocument), identityManagementPersonDocument.getPrincipalId())) {
			setupPrivacy(identityManagementPersonDocument, kimEntity, origEntity.getPrivacyPreferences());
		} else {
			if(ObjectUtils.isNotNull(origEntity.getPrivacyPreferences())) {
				kimEntity.setPrivacyPreferences(origEntity.getPrivacyPreferences());
			}
		}
		// Save the kim entity changes here.
        kimEntity = getDataObjectService().save(kimEntity);

		// If person is being inactivated, do not bother populating roles, groups etc for this member since
		// none of this is reinstated on activation.
	    if ( !inactivatingPrincipal ) {
	        List <GroupMemberBo>  groupPrincipals = populateGroupMembers(identityManagementPersonDocument);
	        List <RoleMemberBo>  rolePrincipals = populateRoleMembers(identityManagementPersonDocument);
	        List <DelegateTypeBo> personDelegations = populateDelegations(identityManagementPersonDocument);
	        List <RoleResponsibilityActionBo> roleRspActions = populateRoleRspActions(identityManagementPersonDocument);
	        List <Object> bos = new ArrayList<Object>();
	        //if(ObjectUtils.isNotNull(kimEntity.getPrivacyPreferences()))
	        //	bos.add(kimEntity.getPrivacyPreferences());
	        bos.addAll(groupPrincipals);
	        bos.addAll(rolePrincipals);
	        bos.addAll(roleRspActions);
	        bos.addAll(personDelegations);
	        for ( Object bo : bos ) {
	            getDataObjectService().save(bo);
	        }
	        List <RoleMemberAttributeDataBo> blankRoleMemberAttrs = getBlankRoleMemberAttrs(rolePrincipals);
	        if (!blankRoleMemberAttrs.isEmpty()) {
	            for ( RoleMemberAttributeDataBo blankRoleMemberAttr : blankRoleMemberAttrs ) {
	                getDataObjectService().delete(blankRoleMemberAttr);
	            }
	        }
	    } else {
	        //when a person is inactivated, inactivate their group, role, and delegation memberships
	        KimImplServiceLocator.getRoleInternalService().principalInactivated(
                    identityManagementPersonDocument.getPrincipalId());
	    }
	}

	private String getInitiatorPrincipalId(Document document){
		try{
			return document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
		} catch(Exception ex){
			return null;
		}
	}

	@Override
    public Map<String,Object> getAttributeEntries( List<KimAttributeField> definitions ) {
		final Map<String,Object> attributeEntries = new HashMap<String,Object>();
		if (definitions != null) {
	        for (AttributeDefinition definition : DataDictionaryTypeServiceHelper.toKimAttributeDefinitions(definitions)) {
				final AttributesMapBuilder builder = new AttributesMapBuilder();
                final ExportMap map = builder.buildAttributeMap(definition, "");
                attributeEntries.put(definition.getName(),map.getExportData());
			}
		}
        return attributeEntries;
	}


	/**
	 *
	 * @see org.kuali.rice.kim.service.UiDocumentService#loadEntityToPersonDoc(IdentityManagementPersonDocument, String)
	 */
	@Override
    public void loadEntityToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId) {
		Principal principal = this.getIdentityService().getPrincipal(principalId);
        Entity kimEntity = null;

        if(ObjectUtils.isNotNull(principal)) {
            // If the principal is not null it was found in the identity management service
            kimEntity = this.getIdentityService().getEntity(principal.getEntityId());
        }

        if(ObjectUtils.isNull(principal) || ObjectUtils.isNull(kimEntity)) {
            // If the principal or entity is null look up the entity in the
            // archive service, and then get the principal from it
            IdentityArchiveService identityArchive = getIdentityArchiveService();
            EntityDefault entityInfo = identityArchive.getEntityDefaultFromArchiveByPrincipalId(principalId);
            principal = entityInfo.getPrincipals().get(0);
            Entity.Builder eb  = Entity.Builder.create();
            eb.setId(entityInfo.getEntityId());
            kimEntity = eb.build();

        }
        if(kimEntity == null) {
            throw new RuntimeException("Entity does not exist for principal id: " + principalId);
        }
        if(principal==null) {
        	throw new RuntimeException("Principal does not exist for principal id:"+principalId);
        }

        identityManagementPersonDocument.setPrincipalId(principal.getPrincipalId());
        identityManagementPersonDocument.setPrincipalName(principal.getPrincipalName());
        //identityManagementPersonDocument.setPassword(principal.getPassword());
        identityManagementPersonDocument.setActive(principal.isActive());
		identityManagementPersonDocument.setEntityId(kimEntity.getId());
		if ( ObjectUtils.isNotNull( kimEntity.getPrivacyPreferences() ) ) {
			identityManagementPersonDocument.setPrivacy(loadPrivacyReferences(kimEntity.getPrivacyPreferences()));
		}
		//identityManagementPersonDocument.setActive(kimEntity.isActive());
		identityManagementPersonDocument.setAffiliations(loadAffiliations(kimEntity.getAffiliations(),kimEntity.getEmploymentInformation()));
		identityManagementPersonDocument.setNames(loadNames( identityManagementPersonDocument, principalId, kimEntity.getNames(), identityManagementPersonDocument.getPrivacy().isSuppressName() ));
		EntityTypeContactInfo entityType = null;
		for (EntityTypeContactInfo type : kimEntity.getEntityTypeContactInfos()) {
			if (KimConstants.EntityTypes.PERSON.equals(type.getEntityTypeCode())) {
				entityType = EntityTypeContactInfo.Builder.create(type).build();
			}
		}

		if(entityType!=null){
			identityManagementPersonDocument.setEmails(loadEmails(identityManagementPersonDocument, principalId, entityType.getEmailAddresses(), identityManagementPersonDocument.getPrivacy().isSuppressEmail()));
			identityManagementPersonDocument.setPhones(loadPhones(identityManagementPersonDocument, principalId, entityType.getPhoneNumbers(), identityManagementPersonDocument.getPrivacy().isSuppressPhone()));
			identityManagementPersonDocument.setAddrs(loadAddresses(identityManagementPersonDocument, principalId, entityType.getAddresses(), identityManagementPersonDocument.getPrivacy().isSuppressAddress()));
		}

		List<Group> groups = getGroupService().getGroups(getGroupService().getDirectGroupIdsByPrincipalId(
                identityManagementPersonDocument.getPrincipalId()));
		loadGroupToPersonDoc(identityManagementPersonDocument, groups);
		loadRoleToPersonDoc(identityManagementPersonDocument);
		loadDelegationsToPersonDoc(identityManagementPersonDocument);
	}

    public List<DelegateTypeBo> getPersonDelegations(String principalId){
		if(principalId==null) {
			return new ArrayList<DelegateTypeBo>();
        }
		List<DelegateMemberBo> delegationMembers =
        		getDataObjectService().findMatching(DelegateMemberBo.class, QueryByCriteria.Builder.fromPredicates(
        		        PredicateFactory.equal(KimConstants.PrimaryKeyConstants.MEMBER_ID, principalId),
        		        PredicateFactory.equal(KIMPropertyConstants.DelegationMember.MEMBER_TYPE_CODE, MemberType.PRINCIPAL.getCode()))).getResults();
		List<DelegateTypeBo> delegations = new ArrayList<DelegateTypeBo>();
		List<String> delegationIds = new ArrayList<String>();
		if(ObjectUtils.isNotNull(delegationMembers)){
			for(DelegateMemberBo delegationMember: delegationMembers){
				if( delegationMember.getDelegationId() != null && !delegationIds.contains(delegationMember.getDelegationId())){
					delegationIds.add(delegationMember.getDelegationId());
					delegations.add(getDataObjectService().find(DelegateTypeBo.class, delegationMember.getDelegationId()));
				}
			}
		}
		return delegations;
	}


    protected void loadDelegationsToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument){
		List<RoleDocumentDelegation> delList = new ArrayList<RoleDocumentDelegation>();
		RoleDocumentDelegation documentDelegation;
		List<DelegateTypeBo> origDelegations = getPersonDelegations(identityManagementPersonDocument.getPrincipalId());
		if(ObjectUtils.isNotNull(origDelegations)){
			for(DelegateTypeBo del: origDelegations){
				if(del.isActive()){
					documentDelegation = new RoleDocumentDelegation();
					documentDelegation.setActive(del.isActive());
					documentDelegation.setDelegationId(del.getDelegationId());
					documentDelegation.setDelegationTypeCode(del.getDelegationTypeCode());
					documentDelegation.setKimTypeId(del.getKimTypeId());
					documentDelegation.setMembers(
							loadDelegationMembers(identityManagementPersonDocument,
									del.getMembers(), (Role)getMember(MemberType.ROLE, del.getRoleId())));
					documentDelegation.setRoleId(del.getRoleId());
					documentDelegation.setEdit(true);
					delList.add(documentDelegation);
				}
			}
		}
		identityManagementPersonDocument.setDelegations(delList);
		setDelegationMembersInDocument(identityManagementPersonDocument);
	}

	public void setDelegationMembersInDocument(IdentityManagementPersonDocument identityManagementPersonDocument){
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getDelegations())){
			for(RoleDocumentDelegation delegation: identityManagementPersonDocument.getDelegations()){
				if(CollectionUtils.isNotEmpty(delegation.getMembers())){
					for(RoleDocumentDelegationMember member: delegation.getMembers()){
						if (StringUtils.equals(member.getMemberId(), identityManagementPersonDocument.getPrincipalId()))
						{
						    member.setDelegationTypeCode(delegation.getDelegationTypeCode());
						    identityManagementPersonDocument.getDelegationMembers().add(member);
						}
					}
				}
			}
		}
	}

    protected List<RoleDocumentDelegationMember> loadDelegationMembers(
    		IdentityManagementPersonDocument identityManagementPersonDocument, List<DelegateMemberBo> members, Role roleImpl){
		List<RoleDocumentDelegationMember> pndMembers = new ArrayList<RoleDocumentDelegationMember>();
		RoleDocumentDelegationMember pndMember;
		RoleMemberBo roleMember;
		if(ObjectUtils.isNotNull(members)){
			for(DelegateMemberBo member: members){
				pndMember = new RoleDocumentDelegationMember();
				pndMember.setActiveFromDate(member.getActiveFromDateValue());
				pndMember.setActiveToDate(member.getActiveToDateValue());
				pndMember.setActive(member.isActive( getDateTimeService().getCurrentTimestamp() ));
				pndMember.setRoleBo( RoleBo.from(roleImpl) );
				if(pndMember.isActive()){
                    pndMember.setMemberId(member.getMemberId());
                    pndMember.setDelegationMemberId(member.getDelegationMemberId());
                    pndMember.setMemberTypeCode(member.getType().getCode());
                    pndMember.setDelegationId(member.getDelegationId());
                    pndMember.setVersionNumber(member.getVersionNumber());
                    pndMember.setObjectId(member.getObjectId());

					pndMember.setRoleMemberId(member.getRoleMemberId());
					roleMember = getRoleMemberForRoleMemberId(member.getRoleMemberId());
					if(roleMember!=null){
						pndMember.setRoleMemberName(getMemberName(roleMember.getType(), roleMember.getMemberId()));
						pndMember.setRoleMemberNamespaceCode(getMemberNamespaceCode(roleMember.getType(), roleMember.getMemberId()));
					}
					pndMember.setMemberNamespaceCode(getMemberNamespaceCode(member.getType(), member.getMemberId()));
					pndMember.setMemberName(getMemberName(member.getType(), member.getMemberId()));
					pndMember.setEdit(true);
					pndMember.setQualifiers(loadDelegationMemberQualifiers(identityManagementPersonDocument, pndMember.getAttributesHelper().getDefinitions(), member.getAttributeDetails()));
					pndMembers.add(pndMember);
				}
			}
		}
		return pndMembers;
	}

    protected List<RoleDocumentDelegationMemberQualifier> loadDelegationMemberQualifiers(IdentityManagementPersonDocument identityManagementPersonDocument,
    		List<KimAttributeField> origAttributeDefinitions, List<DelegateMemberAttributeDataBo> attributeDataList){
		List<RoleDocumentDelegationMemberQualifier> pndMemberRoleQualifiers = new ArrayList<RoleDocumentDelegationMemberQualifier>();
		RoleDocumentDelegationMemberQualifier pndMemberRoleQualifier;
		boolean attributePresent = false;
		String origAttributeId;
		if(origAttributeDefinitions!=null){
			for(KimAttributeField key: origAttributeDefinitions) {
				origAttributeId = identityManagementPersonDocument.getKimAttributeDefnId(key);
				if(ObjectUtils.isNotNull(attributeDataList)){
					for(DelegateMemberAttributeDataBo memberRoleQualifier: attributeDataList){
						if(StringUtils.equals(origAttributeId, memberRoleQualifier.getKimAttribute().getId())){
							pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
							pndMemberRoleQualifier.setAttrDataId(memberRoleQualifier.getId());
							pndMemberRoleQualifier.setAttrVal(memberRoleQualifier.getAttributeValue());
							pndMemberRoleQualifier.setDelegationMemberId(memberRoleQualifier.getAssignedToId());
							pndMemberRoleQualifier.setKimTypId(memberRoleQualifier.getKimTypeId());
							pndMemberRoleQualifier.setKimAttrDefnId(memberRoleQualifier.getKimAttributeId());
							pndMemberRoleQualifier.setKimAttribute(memberRoleQualifier.getKimAttribute());
							pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
							attributePresent = true;
						}
					}
				}
				if(!attributePresent){
					pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
					pndMemberRoleQualifier.setKimAttrDefnId(origAttributeId);
					pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
				}
				attributePresent = false;
			}
		}
		return pndMemberRoleQualifiers;
	}

	/**
	 *
     * This method loads related group data to pending person document when user initiates the 'edit' or 'inquiry'.
     *
	 * @param identityManagementPersonDocument
	 * @param groups
	 */
    protected void loadGroupToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, List<? extends Group> groups) {
        List <PersonDocumentGroup> docGroups = new ArrayList <PersonDocumentGroup>();
        if(ObjectUtils.isNotNull(groups)){
            for (Group group: groups) {
                if (getGroupService().isDirectMemberOfGroup(identityManagementPersonDocument.getPrincipalId(), group.getId())) {
                    PersonDocumentGroup docGroup = new PersonDocumentGroup();
                    docGroup.setGroupId(group.getId());
                    docGroup.setGroupName(group.getName());
                    docGroup.setNamespaceCode(group.getNamespaceCode());
                    docGroup.setPrincipalId(identityManagementPersonDocument.getPrincipalId());
                    Collection<GroupMember> groupMemberships = null;
                    groupMemberships = getGroupService().getMembersOfGroup(group.getId());

                    if(ObjectUtils.isNotNull(groupMemberships)){
                        for (GroupMember groupMember: groupMemberships) {
                            if (StringUtils.equals(groupMember.getMemberId(), identityManagementPersonDocument.getPrincipalId()) &&
                                    KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.equals(groupMember.getType())) {
                                docGroup.setGroupMemberId(groupMember.getId());
                                if (groupMember.getActiveFromDate() != null) {
                                    docGroup.setActiveFromDate(groupMember.getActiveFromDate() == null ? null : new Timestamp(groupMember.getActiveFromDate().getMillis()));
                                }
                                if (groupMember.getActiveToDate() != null) {
                                    docGroup.setActiveToDate(groupMember.getActiveToDate() == null ? null : new Timestamp(groupMember.getActiveToDate().getMillis()));
                                }
                                continue;
                            }
                        }
                    }
                    docGroup.setKimTypeId(group.getKimTypeId());
                    docGroup.setEdit(true);
                    docGroups.add(docGroup);
                }
            }
        }
        identityManagementPersonDocument.setGroups(docGroups);
    }

	/**
	 * Used to populate the {@link PersonDocumentRole} objects for a {@link IdentityManagementPersonDocument}
	 *
	 * @param identityManagementPersonDocument {@link IdentityManagementPersonDocument}
	 */
    protected void loadRoleToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument) {
        List <PersonDocumentRole> docRoles = new ArrayList <PersonDocumentRole>();
        // a list for Id's of the roles added to docRoles
        List<String> roleIds = new ArrayList<String>();

        // get the membership objects for the PrincipalId
        List<RoleMemberBo> roleMembers = getRoleMembersForPrincipal(identityManagementPersonDocument.getPrincipalId());

        // if the PrincipalId is a member of any roles, add those roles to docRoles
        if(ObjectUtils.isNotNull(roleMembers)){
            // for each membership get the role and add it, if not already added
            for (RoleMemberBo member : roleMembers) {
				if(member.isActive() && !roleIds.contains(member.getRoleId())) {
					loadDocRoles(docRoles, roleIds, member, roleMembers);
				}
            }
        }

        // complete the attributes for each role being being returned
        for (PersonDocumentRole role : docRoles) {
            role.setDefinitions(getAttributeDefinitionsForRole(role));

            KimDocumentRoleMember newRolePrncpl = new KimDocumentRoleMember();
            newRolePrncpl.setMemberTypeCode(MemberType.PRINCIPAL.getCode());
            newRolePrncpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
            role.setNewRolePrncpl(newRolePrncpl);

            if(role.getDefinitions()!=null){
                for (KimAttributeField key : role.getDefinitions()) {
                    KimDocumentRoleQualifier qualifier = new KimDocumentRoleQualifier();
                    setAttrDefnIdForQualifier(qualifier,key);
                    role.getNewRolePrncpl().getQualifiers().add(qualifier);
                }
            }

            // load the role's ResponsibilityActions
            loadRoleRstAction(role);

            role.setAttributeEntry( getAttributeEntries( role.getDefinitions() ) );
        }

        // add the PersonDocumentRoles to the IdentityManagementPersonDocument
        identityManagementPersonDocument.setRoles(docRoles);
    }

    /**
     * Selects a {@link RoleBoLite} for passed {@link RoleMemberBo} and adds to List of {@link PersonDocumentRole} objects
     *
     * @param docRoles a list of {@link PersonDocumentRole} roles
     * @param roleIds a list of the Ids of the Roles already added
     * @param member a {@link RoleMemberBo} of a {@link RoleBoLite}
	 * @param roleMembers a list of {@link RoleMemberBo} membership objects for the PrincipalId
     */
    private void loadDocRoles(List <PersonDocumentRole> docRoles, List<String> roleIds,  RoleMemberBo member, List<RoleMemberBo> roleMembers) {

        // get the RoleBoLite object by it's Id from a role membership object
        RoleBoLite role =  getDataObjectService().find(RoleBoLite.class, member.getRoleId());

		// create list of RoleMemberBo's for the same role
		List<RoleMemberBo> matchingMembers = new ArrayList<RoleMemberBo>();
		for (RoleMemberBo tempMember : roleMembers) {
			if (tempMember.getRoleId().equals(member.getRoleId())){
				matchingMembers.add(tempMember);
			}
		}

        // if not already found add role to docRoles
        if (ObjectUtils.isNotNull(role) && !roleIds.contains(role.getId())) {
            PersonDocumentRole docRole = new PersonDocumentRole();
            docRole.setKimTypeId(role.getKimTypeId());
            docRole.setActive(role.isActive());
            docRole.setNamespaceCode(role.getNamespaceCode());
            docRole.setEdit(true);
            docRole.setRoleId(role.getId());
            docRole.setRoleName(role.getName());
			docRole.setRolePrncpls(populateDocRolePrncpl(role.getNamespaceCode(), matchingMembers, member.getMemberId(), getAttributeDefinitionsForRole(docRole)));
            docRoles.add(docRole);
            roleIds.add(role.getId());
        }
    }

	protected List<KimAttributeField> getAttributeDefinitionsForRole(PersonDocumentRole role) {
    	KimTypeService kimTypeService = KimFrameworkServiceLocator.getKimTypeService(KimTypeBo.to(
                role.getKimRoleType()));
    	//it is possible that the the kimTypeService is coming from a remote application
        // and therefore it can't be guarenteed that it is up and working, so using a try/catch to catch this possibility.
        try {
        	if ( kimTypeService != null ) {
        		return kimTypeService.getAttributeDefinitions(role.getKimTypeId());
        	}
        } catch (Exception ex) {
            LOG.warn("Not able to retrieve KimTypeService from remote system for KIM Role Type: " + role.getKimRoleType(), ex);
        }
    	return Collections.emptyList();
	}

	protected void loadRoleRstAction(PersonDocumentRole role) {
		if(role!=null && CollectionUtils.isNotEmpty(role.getRolePrncpls())){
			for (KimDocumentRoleMember roleMbr : role.getRolePrncpls()) {
				List<RoleResponsibilityActionBo> actions = getRoleRspActions( roleMbr.getRoleMemberId());
				if(ObjectUtils.isNotNull(actions)){
					for (RoleResponsibilityActionBo entRoleRspAction :actions) {
						KimDocumentRoleResponsibilityAction roleRspAction = new KimDocumentRoleResponsibilityAction();
						roleRspAction.setRoleResponsibilityActionId(entRoleRspAction.getId());
                        roleRspAction.setRoleResponsibilityId(entRoleRspAction.getRoleResponsibilityId());
						roleRspAction.setActionTypeCode(entRoleRspAction.getActionTypeCode());
						roleRspAction.setActionPolicyCode(entRoleRspAction.getActionPolicyCode());
						roleRspAction.setPriorityNumber(entRoleRspAction.getPriorityNumber());
						roleRspAction.setRoleResponsibilityActionId(entRoleRspAction.getId());
						KradDataServiceLocator.getDataObjectService().wrap(roleRspAction).fetchRelationship("roleResponsibility");
						roleMbr.getRoleRspActions().add(roleRspAction);
					}
				}
			}
		}
	}

	protected void setAttrDefnIdForQualifier(KimDocumentRoleQualifier qualifier, KimAttributeField definition) {
    	qualifier.setKimAttrDefnId(getAttributeDefnId(definition));
    	KradDataServiceLocator.getDataObjectService().wrap(qualifier).fetchRelationship("kimAttribute");
    }

	protected String getAttributeDefnId(KimAttributeField definition) {
    	return definition.getId();
    }

	@Override
    public List<EntityEmployment> getEntityEmploymentInformationInfo(String entityId) {
        EntityBo entityImpl = getEntityBo(entityId);
        List<EntityEmployment> empInfos = new ArrayList<EntityEmployment>();
        if(entityImpl != null && CollectionUtils.isNotEmpty(entityImpl.getEmploymentInformation())){
        	for(EntityEmploymentBo empImpl: entityImpl.getEmploymentInformation()){
            	empInfos.add(EntityEmploymentBo.to(empImpl));
        	}
        }
        return empInfos;
	}

	private EntityBo getEntityBo(String entityId) {
		return getDataObjectService().find(EntityBo.class, entityId);
	}

    protected List<RoleBo> getRolesForPrincipal(String principalId) {
		if ( principalId == null ) {
			return new ArrayList<RoleBo>();
		}
        return getDataObjectService().findMatching(RoleBo.class, QueryByCriteria.Builder.fromPredicates(
                PredicateFactory.equal("members." + KimConstants.PrimaryKeyConstants.MEMBER_ID, principalId),
                PredicateFactory.equal("members." + KIMPropertyConstants.DelegationMember.MEMBER_TYPE_CODE, MemberType.PRINCIPAL.getCode()))).getResults();
	}

	protected List<RoleMemberBo> getRoleMembersForPrincipal(String principalId) {
		if ( principalId == null ) {
			return new ArrayList<RoleMemberBo>();
		}
        return getDataObjectService().findMatching(RoleMemberBo.class, QueryByCriteria.Builder.fromPredicates(
                PredicateFactory.equal(KimConstants.PrimaryKeyConstants.MEMBER_ID, principalId),
                PredicateFactory.equal(KIMPropertyConstants.DelegationMember.MEMBER_TYPE_CODE, MemberType.PRINCIPAL.getCode()))).getResults();
	}

	@Override
    public RoleMemberBo getRoleMember(String id) {
		if ( id == null ) {
			return null;
		}
		return getDataObjectService().find(RoleMemberBo.class, id);
	}

    protected List<RoleResponsibilityActionBo> getRoleRspActions(String roleMemberId) {
		return getDataObjectService().findMatching(RoleResponsibilityActionBo.class,
		        QueryByCriteria.Builder.forAttribute(KIMPropertyConstants.RoleMember.ROLE_MEMBER_ID, roleMemberId).build()).getResults();
	}

    protected List<KimDocumentRoleMember> populateDocRolePrncpl(String namespaceCode, List <RoleMemberBo> roleMembers, String principalId, List<KimAttributeField> definitions) {
		List <KimDocumentRoleMember> docRoleMembers = new ArrayList <KimDocumentRoleMember>();
		if(ObjectUtils.isNotNull(roleMembers)){
	    	for (RoleMemberBo rolePrincipal : roleMembers) {
	    		if (rolePrincipal.isActive(getDateTimeService().getCurrentTimestamp()) && MemberType.PRINCIPAL.equals(
                        rolePrincipal.getType()) &&
	    				StringUtils.equals(rolePrincipal.getMemberId(), principalId)) {
	        		KimDocumentRoleMember docRolePrncpl = new KimDocumentRoleMember();
	        		docRolePrncpl.setMemberId(rolePrincipal.getMemberId());
	        		docRolePrncpl.setRoleMemberId(rolePrincipal.getId());
	        		docRolePrncpl.setActive(rolePrincipal.isActive(getDateTimeService().getCurrentTimestamp()));
	        		docRolePrncpl.setRoleId(rolePrincipal.getRoleId());
	        		docRolePrncpl.setActiveFromDate(rolePrincipal.getActiveFromDateValue());
	        		docRolePrncpl.setActiveToDate(rolePrincipal.getActiveToDateValue());
	         		docRolePrncpl.setQualifiers(populateDocRoleQualifier(namespaceCode, rolePrincipal.getAttributeDetails(), definitions));
	         		docRolePrncpl.setEdit(true);
	        		docRoleMembers.add(docRolePrncpl);
	    		 }
	    	}
		}
    	return docRoleMembers;
    }

    // UI layout for rolequalifier is a little different from kimroleattribute set up.
    // each principal may have member with same role multiple times with different qualifier, but the role
    // only displayed once, and the qualifier displayed multiple times.
    protected List<KimDocumentRoleQualifier> populateDocRoleQualifier(String namespaceCode, List <RoleMemberAttributeDataBo> qualifiers, List<KimAttributeField> definitions) {

		List <KimDocumentRoleQualifier> docRoleQualifiers = new ArrayList <KimDocumentRoleQualifier>();
		if(definitions!=null){
			for (KimAttributeField definition : definitions) {
				String attrDefId=definition.getId();
				boolean qualifierFound = false;
				if(ObjectUtils.isNotNull(qualifiers)){
					for (RoleMemberAttributeDataBo qualifier : qualifiers) {
						if (attrDefId!=null && StringUtils.equals(attrDefId, qualifier.getKimAttributeId())) {
				    		KimDocumentRoleQualifier docRoleQualifier = new KimDocumentRoleQualifier();
				    		docRoleQualifier.setAttrDataId(qualifier.getId());
				    		docRoleQualifier.setAttrVal(qualifier.getAttributeValue());
				    		docRoleQualifier.setKimAttrDefnId(qualifier.getKimAttributeId());
				    		docRoleQualifier.setKimAttribute(qualifier.getKimAttribute());
				    		docRoleQualifier.setKimTypId(qualifier.getKimTypeId());
				    		docRoleQualifier.setRoleMemberId(qualifier.getAssignedToId());
				    		docRoleQualifier.setEdit(true);
				    		formatAttrValIfNecessary(docRoleQualifier);
				    		docRoleQualifiers.add(docRoleQualifier);
				    		qualifierFound = true;
				    		break;
						}
					}
				}
				if (!qualifierFound) {
		    		KimDocumentRoleQualifier docRoleQualifier = new KimDocumentRoleQualifier();
		    		docRoleQualifier.setAttrVal("");
		    		docRoleQualifier.setKimAttrDefnId(attrDefId);
		    		KradDataServiceLocator.getDataObjectService().wrap(docRoleQualifier).fetchRelationship("kimAttribute");
		    		docRoleQualifiers.add(docRoleQualifier);
				}
			}
			// If all of the qualifiers are empty, return an empty list
			// This is to prevent dynamic qualifiers from appearing in the
			// person maintenance roles tab.  see KULRICE-3989 for more detail
			// and KULRICE-5071 for detail on switching from config value to
			// application-scoped parameter
			if (!isBlankRoleQualifierVisible(namespaceCode)) {
				int qualCount = 0;
				for (KimDocumentRoleQualifier qual : docRoleQualifiers){
					if (StringUtils.isEmpty(qual.getAttrVal())){
						qualCount++;
					}
				}
				if (qualCount == docRoleQualifiers.size()){
					return new ArrayList <KimDocumentRoleQualifier>();
				}
			}
		}
    	return docRoleQualifiers;
    }

    protected List<PersonDocumentName> loadNames( IdentityManagementPersonDocument personDoc, String principalId, List <EntityName> names, boolean suppressDisplay ) {
		List<PersonDocumentName> docNames = new ArrayList<PersonDocumentName>();
		if(ObjectUtils.isNotNull(names)){
			for (EntityName name: names) {
				if(name.isActive()){
					PersonDocumentName docName = new PersonDocumentName();
                    if (name.getNameType() != null) {
					    docName.setNameCode(name.getNameType().getCode());
                    }

					//We do not need to check the privacy setting here - The UI should care of it
					docName.setFirstName(name.getFirstNameUnmasked());
					docName.setLastName(name.getLastNameUnmasked());
					docName.setMiddleName(name.getMiddleNameUnmasked());
					docName.setNamePrefix(name.getNamePrefixUnmasked());
					docName.setNameSuffix(name.getNameSuffixUnmasked());

					docName.setActive(name.isActive());
					docName.setDflt(name.isDefaultValue());
					docName.setEdit(true);
					docName.setEntityNameId(name.getId());
					docNames.add(docName);
				}
			}
		}
		return docNames;
	}

	@Override
    public boolean canModifyEntity( String currentUserPrincipalId, String toModifyPrincipalId ){
		return (StringUtils.isNotBlank(currentUserPrincipalId) && StringUtils.isNotBlank(toModifyPrincipalId) &&
				currentUserPrincipalId.equals(toModifyPrincipalId)) ||
				getPermissionService().isAuthorized(
						currentUserPrincipalId,
						KimConstants.NAMESPACE_CODE,
						KimConstants.PermissionNames.MODIFY_ENTITY,
						Collections.singletonMap(KimConstants.AttributeConstants.PRINCIPAL_ID, currentUserPrincipalId));
	}

	@Override
    public boolean canOverrideEntityPrivacyPreferences( String currentUserPrincipalId, String toModifyPrincipalId ){
		return (StringUtils.isNotBlank(currentUserPrincipalId) && StringUtils.isNotBlank(toModifyPrincipalId) &&
				currentUserPrincipalId.equals(toModifyPrincipalId)) ||
				getPermissionService().isAuthorized(
						currentUserPrincipalId,
						KimConstants.NAMESPACE_CODE,
						KimConstants.PermissionNames.OVERRIDE_ENTITY_PRIVACY_PREFERENCES,
						Collections.singletonMap(KimConstants.AttributeConstants.PRINCIPAL_ID, currentUserPrincipalId) );
	}

	protected boolean canAssignToRole(IdentityManagementRoleDocument document, String initiatorPrincipalId){
        boolean rulePassed = true;
        Map<String,String> additionalPermissionDetails = new HashMap<String,String>();
        additionalPermissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, document.getRoleNamespace());
        additionalPermissionDetails.put(KimConstants.AttributeConstants.ROLE_NAME, document.getRoleName());
		if(!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(
				document, KimConstants.NAMESPACE_CODE, KimConstants.PermissionTemplateNames.ASSIGN_ROLE,
				initiatorPrincipalId, additionalPermissionDetails, null)){
            rulePassed = false;
		}
		return rulePassed;
	}

	protected List<PersonDocumentAffiliation> loadAffiliations(List <EntityAffiliation> affiliations, List<EntityEmployment> empInfos) {
		List<PersonDocumentAffiliation> docAffiliations = new ArrayList<PersonDocumentAffiliation>();
		if(ObjectUtils.isNotNull(affiliations)){
			for (EntityAffiliation affiliation: affiliations) {
				if(affiliation.isActive()){
					PersonDocumentAffiliation docAffiliation = new PersonDocumentAffiliation();
					docAffiliation.setAffiliationTypeCode(affiliation.getAffiliationType().getCode());
					docAffiliation.setCampusCode(affiliation.getCampusCode());
					docAffiliation.setActive(affiliation.isActive());
					docAffiliation.setDflt(affiliation.isDefaultValue());
					docAffiliation.setEntityAffiliationId(affiliation.getId());
					KradDataServiceLocator.getDataObjectService().wrap(docAffiliation).fetchRelationship("affiliationType");
					// EntityAffiliationImpl does not define empinfos as collection
					docAffiliations.add(docAffiliation);
					docAffiliation.setEdit(true);
					// employment informations
					List<PersonDocumentEmploymentInfo> docEmploymentInformations = new ArrayList<PersonDocumentEmploymentInfo>();
					if(ObjectUtils.isNotNull(empInfos)){
						for (EntityEmployment empInfo: empInfos) {
							if (empInfo.isActive()
                                    && StringUtils.equals(docAffiliation.getEntityAffiliationId(),
                                                          (empInfo.getEntityAffiliation() != null ? empInfo.getEntityAffiliation().getId() : null))) {
								PersonDocumentEmploymentInfo docEmpInfo = new PersonDocumentEmploymentInfo();
								docEmpInfo.setEntityEmploymentId(empInfo.getId());
								docEmpInfo.setEmployeeId(empInfo.getEmployeeId());
								docEmpInfo.setEmploymentRecordId(empInfo.getEmploymentRecordId());
								docEmpInfo.setBaseSalaryAmount(empInfo.getBaseSalaryAmount());
								docEmpInfo.setPrimaryDepartmentCode(empInfo.getPrimaryDepartmentCode());
								docEmpInfo.setEmploymentStatusCode(empInfo.getEmployeeStatus() != null ? empInfo.getEmployeeStatus().getCode() : null);
								docEmpInfo.setEmploymentTypeCode(empInfo.getEmployeeType() != null ? empInfo.getEmployeeType().getCode() : null);
								docEmpInfo.setActive(empInfo.isActive());
								docEmpInfo.setPrimary(empInfo.isPrimary());
								docEmpInfo.setEntityAffiliationId(empInfo.getEntityAffiliation() != null ? empInfo.getEntityAffiliation().getId() : null);
								// there is no version number on KimEntityEmploymentInformationInfo
								//docEmpInfo.setVersionNumber(empInfo.getVersionNumber());
								docEmpInfo.setEdit(true);
								KradDataServiceLocator.getDataObjectService().wrap(docEmpInfo).fetchRelationship("employmentType");
								docEmploymentInformations.add(docEmpInfo);
							}
						}
					}
					docAffiliation.setEmpInfos(docEmploymentInformations);
				}
			}
		}
		return docAffiliations;

	}

    protected boolean setupPrincipal(IdentityManagementPersonDocument identityManagementPersonDocument, EntityBo kimEntity, List<PrincipalBo> origPrincipals) {
    	boolean inactivatingPrincipal = false;
		List<PrincipalBo> principals = new ArrayList<PrincipalBo>();
		PrincipalBo principal = new PrincipalBo();
        principal.setPrincipalName(identityManagementPersonDocument.getPrincipalName());
		principal.setPrincipalId(identityManagementPersonDocument.getPrincipalId());
		principal.setActive(identityManagementPersonDocument.isActive());
		principal.setEntityId(identityManagementPersonDocument.getEntityId());
		if(ObjectUtils.isNotNull(origPrincipals)){
			for (PrincipalBo prncpl : origPrincipals) {
				if (prncpl.getPrincipalId()!=null && StringUtils.equals(prncpl.getPrincipalId(), principal.getPrincipalId())) {
					principal.setVersionNumber(prncpl.getVersionNumber());
                    principal.setObjectId(prncpl.getObjectId());
                    principal.setPassword(prncpl.getPassword());
					// check if inactivating the principal
					if ( prncpl.isActive() && !principal.isActive() ) {
						inactivatingPrincipal = true;
					}
				}
			}
		}
		principals.add(principal);

		kimEntity.setPrincipals(principals);
		return inactivatingPrincipal;
	}

    protected void setupPrivacy(IdentityManagementPersonDocument identityManagementPersonDocument, EntityBo kimEntity, EntityPrivacyPreferencesBo origPrivacy) {
		EntityPrivacyPreferencesBo privacyPreferences = new EntityPrivacyPreferencesBo();
		privacyPreferences.setEntityId(identityManagementPersonDocument.getEntityId());
		privacyPreferences.setSuppressAddress(identityManagementPersonDocument.getPrivacy().isSuppressAddress());
		privacyPreferences.setSuppressEmail(identityManagementPersonDocument.getPrivacy().isSuppressEmail());
		privacyPreferences.setSuppressName(identityManagementPersonDocument.getPrivacy().isSuppressName());
		privacyPreferences.setSuppressPhone(identityManagementPersonDocument.getPrivacy().isSuppressPhone());
		privacyPreferences.setSuppressPersonal(identityManagementPersonDocument.getPrivacy().isSuppressPersonal());
		if (ObjectUtils.isNotNull(origPrivacy)) {
			privacyPreferences.setVersionNumber(origPrivacy.getVersionNumber());
            privacyPreferences.setObjectId(origPrivacy.getObjectId());
		}
		kimEntity.setPrivacyPreferences(privacyPreferences);
	}
    protected PersonDocumentPrivacy loadPrivacyReferences(EntityPrivacyPreferences privacyPreferences) {
		PersonDocumentPrivacy docPrivacy = new PersonDocumentPrivacy();
		docPrivacy.setSuppressAddress(privacyPreferences.isSuppressAddress());
		docPrivacy.setSuppressEmail(privacyPreferences.isSuppressEmail());
		docPrivacy.setSuppressName(privacyPreferences.isSuppressName());
		docPrivacy.setSuppressPhone(privacyPreferences.isSuppressPhone());
		docPrivacy.setSuppressPersonal(privacyPreferences.isSuppressPersonal());
		docPrivacy.setEdit(true);
		return docPrivacy;
	}

    protected void setupName(IdentityManagementPersonDocument identityManagementPersonDocument, EntityBo kimEntity, List<EntityNameBo> origNames) {
    	if ( !identityManagementPersonDocument.getPrivacy().isSuppressName() ||
    			canOverrideEntityPrivacyPreferences( getInitiatorPrincipalId(identityManagementPersonDocument), identityManagementPersonDocument.getPrincipalId() ) ) {
	    	List<EntityNameBo> entityNames = new ArrayList<EntityNameBo>();
			if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getNames())){
				for (PersonDocumentName name : identityManagementPersonDocument.getNames()) {
				    EntityNameBo entityName = new EntityNameBo();
					entityName.setNameCode(name.getNameCode());
                    if (name.getEntityNameType() != null) {
                        entityName.setNameType(name.getEntityNameType());
                    } else {
                        if (StringUtils.isNotEmpty(name.getNameCode())) {
                            entityName.setNameType(
                                    EntityNameTypeBo.from(getIdentityService().getNameType(name.getNameCode())));
                        }
                    }
					entityName.setFirstName(name.getFirstName());
					entityName.setLastName(name.getLastName());
					entityName.setMiddleName(name.getMiddleName());
					entityName.setNamePrefix(name.getNamePrefix());
					entityName.setNameSuffix(name.getNameSuffix());
					entityName.setActive(name.isActive());
					entityName.setDefaultValue(name.isDflt());
					entityName.setId(name.getEntityNameId());
					entityName.setEntityId(identityManagementPersonDocument.getEntityId());
					if(ObjectUtils.isNotNull(origNames)){
						for (EntityNameBo origName : origNames) {
							if (origName.getId()!=null && StringUtils.equals(origName.getId(), entityName.getId())) {
								entityName.setVersionNumber(origName.getVersionNumber());
							}

						}
					}
					entityNames.add(entityName);
				}
			}
			kimEntity.setNames(entityNames);
    	}
	}

    protected void setupAffiliation(IdentityManagementPersonDocument identityManagementPersonDocument, EntityBo kimEntity,List<EntityAffiliationBo> origAffiliations, List<EntityEmploymentBo> origEmpInfos) {
		List<EntityAffiliationBo> entityAffiliations = new ArrayList<EntityAffiliationBo>();
		// employment informations
		List<EntityEmploymentBo> entityEmploymentInformations = new ArrayList<EntityEmploymentBo>();
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getAffiliations())){
			for (PersonDocumentAffiliation affiliation : identityManagementPersonDocument.getAffiliations()) {
				EntityAffiliationBo entityAffiliation = new EntityAffiliationBo();
				entityAffiliation.setAffiliationTypeCode(affiliation.getAffiliationTypeCode());
                if (affiliation.getAffiliationType() != null) {
                        entityAffiliation.setAffiliationType(affiliation.getAffiliationType());
                } else {
                    if (StringUtils.isNotEmpty(affiliation.getAffiliationTypeCode())) {
                        entityAffiliation.setAffiliationType(EntityAffiliationTypeBo.from(getIdentityService().getAffiliationType(
                                affiliation.getAffiliationTypeCode())));
                    }
                }
				entityAffiliation.setCampusCode(affiliation.getCampusCode());
				entityAffiliation.setActive(affiliation.isActive());
				entityAffiliation.setDefaultValue(affiliation.isDflt());
				entityAffiliation.setEntityId(identityManagementPersonDocument.getEntityId());
				entityAffiliation.setId(affiliation.getEntityAffiliationId());
				if(ObjectUtils.isNotNull(origAffiliations)){
				// EntityAffiliationImpl does not define empinfos as collection
					for (EntityAffiliationBo origAffiliation : origAffiliations) {
						if(isSameAffiliation(origAffiliation, entityAffiliation)){
							entityAffiliation.setId(origAffiliation.getId());
						}
						if (origAffiliation.getId()!=null && StringUtils.equals(origAffiliation.getId(), entityAffiliation.getId())) {
							entityAffiliation.setVersionNumber(origAffiliation.getVersionNumber());
						}
					}
				}
				entityAffiliations.add(entityAffiliation);
				int employeeRecordCounter = origEmpInfos==null?0:origEmpInfos.size();
				if(CollectionUtils.isNotEmpty(affiliation.getEmpInfos())){
					for (PersonDocumentEmploymentInfo empInfo : affiliation.getEmpInfos()) {
						EntityEmploymentBo entityEmpInfo = new EntityEmploymentBo();
						entityEmpInfo.setId(empInfo.getEntityEmploymentId());
						entityEmpInfo.setEmployeeId(empInfo.getEmployeeId());
						entityEmpInfo.setEmploymentRecordId(empInfo.getEmploymentRecordId());
						entityEmpInfo.setBaseSalaryAmount(empInfo.getBaseSalaryAmount());
						entityEmpInfo.setPrimaryDepartmentCode(empInfo.getPrimaryDepartmentCode());
						entityEmpInfo.setEmployeeStatusCode(empInfo.getEmploymentStatusCode());
                        if (empInfo.getEmploymentStatus() != null) {
                            entityEmpInfo.setEmployeeStatus(empInfo.getEmploymentStatus());
                        } else {
                            if (StringUtils.isNotEmpty(empInfo.getEmploymentStatusCode())) {
                                entityEmpInfo.setEmployeeStatus(EntityEmploymentStatusBo
                                        .from(getIdentityService().getEmploymentStatus(empInfo.getEmploymentStatusCode())));
                            }
                        }
						entityEmpInfo.setEmployeeTypeCode(empInfo.getEmploymentTypeCode());
                        if (empInfo.getEmploymentType() != null) {
                            entityEmpInfo.setEmployeeType(empInfo.getEmploymentType());
                        } else {
                            if (StringUtils.isNotEmpty(empInfo.getEmploymentTypeCode())) {
                                entityEmpInfo.setEmployeeType(EntityEmploymentTypeBo
                                        .from(getIdentityService().getEmploymentType(empInfo.getEmploymentTypeCode())));
                            }
                        }
						entityEmpInfo.setActive(empInfo.isActive());
						entityEmpInfo.setPrimary(empInfo.isPrimary());
						entityEmpInfo.setEntityId(identityManagementPersonDocument.getEntityId());
						entityEmpInfo.setEntityAffiliationId(empInfo.getEntityAffiliationId());
						if(ObjectUtils.isNotNull(origEmpInfos)){
							for (EntityEmploymentBo origEmpInfo : origEmpInfos) {
								if(isSameEmpInfo(origEmpInfo, entityEmpInfo)){
									entityEmpInfo.setId(origEmpInfo.getId());
								}

								if (origEmpInfo.getId()!=null && StringUtils.equals(origEmpInfo.getId(), entityEmpInfo.getId())) {
									entityEmpInfo.setVersionNumber(origEmpInfo.getVersionNumber());
									entityEmpInfo.setEmploymentRecordId(empInfo.getEmploymentRecordId());
								}
							}
						}
						if(StringUtils.isEmpty(entityEmpInfo.getEmploymentRecordId())){
							employeeRecordCounter++;
							entityEmpInfo.setEmploymentRecordId(employeeRecordCounter+"");
						}
						entityEmploymentInformations.add(entityEmpInfo);
					}
				}
			}
		}
		kimEntity.setEmploymentInformation(entityEmploymentInformations);
		kimEntity.setAffiliations(entityAffiliations);
	}

    /*
     * Added to address KULRICE-5071 : "Move the 'show blank qualifier' kim toggle from a Config param to a System param"
     *
     * This method first checks for a namespace specific parameter with a detailTypeCode of "All" and parameterName of "KIM_SHOW_BLANK_QUALIFIERS".
     * If no parameter is found, it checks for the config property "kim.show.blank.qualifiers", and defaults to true if no config property exists.
     *
     */
    private boolean isBlankRoleQualifierVisible(String namespaceCode) {
    	boolean showBlankQualifiers = true;

		Parameter param = getParameterService().getParameter(namespaceCode, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, KimConstants.ParameterKey.SHOW_BLANK_QUALIFIERS);
	    if (param != null) {
	    	showBlankQualifiers = "Y".equals(param.getValue());
	    } else {
	    	String configProperty = ConfigContext.getCurrentContextConfig().getProperty(SHOW_BLANK_QUALIFIERS);
	    	if (configProperty != null) {
	    		showBlankQualifiers = Boolean.valueOf(configProperty);
            }
	    }

	    return showBlankQualifiers;
    }

   private boolean isSameAffiliation(EntityAffiliationBo origAffiliation, EntityAffiliationBo entityAffiliation){
    	//entityId
    	//affiliationTypeCode
    	//campusCode
    	return (origAffiliation!=null && entityAffiliation!=null) &&
    	(StringUtils.isNotEmpty(origAffiliation.getCampusCode()) && StringUtils.equals(origAffiliation.getCampusCode(), entityAffiliation.getCampusCode()))
    	&&
    	(StringUtils.isNotEmpty(origAffiliation.getAffiliationTypeCode()) && StringUtils.equals(origAffiliation.getAffiliationTypeCode(), entityAffiliation.getAffiliationTypeCode()))
 		&&
 		(StringUtils.isNotEmpty(origAffiliation.getEntityId()) && StringUtils.equals(origAffiliation.getEntityId(), entityAffiliation.getEntityId()));
    }

    private boolean isSameEmpInfo(EntityEmploymentBo origEmpInfo, EntityEmploymentBo entityEmpInfo){
    	//emp_info:
    		//employmentRecordId
    		//entityId
    		//These should be unique - add a business rule
    	return (origEmpInfo!=null && entityEmpInfo!=null)
    			&& (StringUtils.isNotEmpty(origEmpInfo.getEmploymentRecordId())
    					&& StringUtils.equals(origEmpInfo.getEmploymentRecordId(), entityEmpInfo.getEmploymentRecordId() )
    				)
    			&& StringUtils.equals( origEmpInfo.getEntityId(),entityEmpInfo.getEntityId());
    }

    protected void setupPhone(IdentityManagementPersonDocument identityManagementPersonDocument, EntityTypeContactInfoBo entityType, List<EntityPhoneBo> origPhones) {
    	if ( !identityManagementPersonDocument.getPrivacy().isSuppressPhone() || canOverrideEntityPrivacyPreferences(getInitiatorPrincipalId(identityManagementPersonDocument), identityManagementPersonDocument.getPrincipalId()) ) {
			List<EntityPhoneBo> entityPhones = new ArrayList<EntityPhoneBo>();
			if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getPhones())){
				for (PersonDocumentPhone phone : identityManagementPersonDocument.getPhones()) {
					EntityPhoneBo entityPhone = new EntityPhoneBo();
					entityPhone.setPhoneTypeCode(phone.getPhoneTypeCode());
                    if (phone.getPhoneType() != null) {
                        entityPhone.setPhoneType(phone.getPhoneType());
                    } else {
                        if (StringUtils.isNotEmpty(phone.getPhoneTypeCode())) {
                            entityPhone.setPhoneType(EntityPhoneTypeBo
                                    .from(getIdentityService().getAddressType(phone.getPhoneTypeCode())));
                        }
                    }
					entityPhone.setEntityId(identityManagementPersonDocument.getEntityId());
					entityPhone.setId(phone.getEntityPhoneId());
					entityPhone.setEntityTypeCode(entityType.getEntityTypeCode());
					entityPhone.setPhoneNumber(phone.getPhoneNumber());
					entityPhone.setCountryCode(phone.getCountryCode());
					entityPhone.setExtensionNumber(phone.getExtensionNumber());
					entityPhone.setActive(phone.isActive());
					entityPhone.setDefaultValue(phone.isDflt());
					if(ObjectUtils.isNotNull(origPhones)){
						for (EntityPhoneContract origPhone : origPhones) {
							if (origPhone.getId()!=null && StringUtils.equals(origPhone.getId(), entityPhone.getId())) {
								entityPhone.setVersionNumber(origPhone.getVersionNumber());
							}
						}
					}
					entityPhone.setId(phone.getEntityPhoneId());
					entityPhones.add(entityPhone);
				}
			}
			entityType.setPhoneNumbers(entityPhones);
    	}
	}

    protected List<PersonDocumentPhone> loadPhones(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId, List<EntityPhone> entityPhones, boolean suppressDisplay ) {
		List<PersonDocumentPhone> docPhones = new ArrayList<PersonDocumentPhone>();
		if(ObjectUtils.isNotNull(entityPhones)){
			for (EntityPhone phone: entityPhones) {
				if(phone.isActive()){
					PersonDocumentPhone docPhone = new PersonDocumentPhone();
                    if (phone.getPhoneType() != null) {
					    docPhone.setPhoneTypeCode(phone.getPhoneType().getCode());
                    }
					//docPhone.setPhoneType(((KimEntityPhoneImpl)phone).getPhoneType());
					docPhone.setEntityTypeCode(phone.getEntityTypeCode());
					//We do not need to check the privacy setting here - The UI should care of it
					docPhone.setPhoneNumber(phone.getPhoneNumberUnmasked());
					docPhone.setCountryCode(phone.getCountryCodeUnmasked());
					docPhone.setExtensionNumber(phone.getExtensionNumberUnmasked());

					docPhone.setActive(phone.isActive());
					docPhone.setDflt(phone.isDefaultValue());
					docPhone.setEntityPhoneId(phone.getId());
					docPhone.setEdit(true);
					docPhones.add(docPhone);
				}
			}
		}
		return docPhones;

	}

    protected void setupEmail(
			IdentityManagementPersonDocument identityManagementPersonDocument,
			EntityTypeContactInfoBo entityType, List<EntityEmailBo> origEmails) {
    	if ( !identityManagementPersonDocument.getPrivacy().isSuppressEmail() || canOverrideEntityPrivacyPreferences(getInitiatorPrincipalId(identityManagementPersonDocument), identityManagementPersonDocument.getPrincipalId()) ) {
			List<EntityEmailBo> entityEmails = new ArrayList<EntityEmailBo>();
			if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getEmails())){
				for (PersonDocumentEmail email : identityManagementPersonDocument.getEmails()) {
					EntityEmailBo entityEmail = new EntityEmailBo();
					entityEmail.setEntityId(identityManagementPersonDocument.getEntityId());
					entityEmail.setEntityTypeCode(entityType.getEntityTypeCode());
                    if (email.getEmailType() != null) {
                        entityEmail.setEmailType(email.getEmailType());
                    } else {
                        if (StringUtils.isNotEmpty(email.getEmailTypeCode())) {
                            entityEmail.setEmailType(
                                    EntityEmailTypeBo.from(getIdentityService().getEmailType(email.getEmailTypeCode())));
                        }
                    }
					entityEmail.setEmailTypeCode(email.getEmailTypeCode());
					entityEmail.setEmailAddress(email.getEmailAddress());
					entityEmail.setActive(email.isActive());
					entityEmail.setDefaultValue(email.isDflt());
					entityEmail.setId(email.getEntityEmailId());
					if(ObjectUtils.isNotNull(origEmails)){
						for (EntityEmailContract origEmail : origEmails) {
							if (origEmail.getId()!=null && StringUtils.equals(origEmail.getId(), entityEmail.getId())) {
								entityEmail.setVersionNumber(origEmail.getVersionNumber());
							}
						}
					}
					entityEmails.add(entityEmail);
				}
			}
			entityType.setEmailAddresses(entityEmails);
    	}
	}
    protected List<PersonDocumentEmail> loadEmails(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId, List<EntityEmail> entityEmails, boolean suppressDisplay ) {
		List<PersonDocumentEmail> emails = new ArrayList<PersonDocumentEmail>();
		if(ObjectUtils.isNotNull(entityEmails)){
			for (EntityEmail email: entityEmails) {
				if(email.isActive()){
					PersonDocumentEmail docEmail = new PersonDocumentEmail();
					//docEmail.setEntityId(email.getEntityId());
					docEmail.setEntityTypeCode(email.getEntityTypeCode());
                    if (email.getEmailType() != null) {
					    docEmail.setEmailTypeCode(email.getEmailType().getCode());
                    }
					// EmailType not on info object.
					//docEmail.setEmailType(((KimEntityEmailImpl)email).getEmailType());
					//We do not need to check the privacy setting here - The UI should care of it
					docEmail.setEmailAddress(email.getEmailAddressUnmasked());

					docEmail.setActive(email.isActive());
					docEmail.setDflt(email.isDefaultValue());
					docEmail.setEntityEmailId(email.getId());
					docEmail.setEdit(true);
					emails.add(docEmail);
				}
			}
		}
		return emails;
	}

    protected void setupAddress(
			IdentityManagementPersonDocument identityManagementPersonDocument,
			EntityTypeContactInfoBo entityType, List<EntityAddressBo> origAddresses) {
    	if ( !identityManagementPersonDocument.getPrivacy().isSuppressAddress() || canOverrideEntityPrivacyPreferences(getInitiatorPrincipalId(identityManagementPersonDocument), identityManagementPersonDocument.getPrincipalId()) ) {
			List<EntityAddressBo> entityAddresses = new ArrayList<EntityAddressBo>();
			if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getAddrs())){
				for (PersonDocumentAddress address : identityManagementPersonDocument.getAddrs()) {
					EntityAddressBo entityAddress = new EntityAddressBo();
					entityAddress.setEntityId(identityManagementPersonDocument.getEntityId());
					entityAddress.setEntityTypeCode(entityType.getEntityTypeCode());
					entityAddress.setAddressTypeCode(address.getAddressTypeCode());
                    if (address.getAddressType() != null) {
                        entityAddress.setAddressType(address.getAddressType());
                    } else {
                        if (StringUtils.isNotEmpty(address.getAddressTypeCode())) {
                            entityAddress.setAddressType(EntityAddressTypeBo.from(
                                    getIdentityService().getAddressType(address.getAddressTypeCode())));
                        }
                    }
					entityAddress.setLine1(address.getLine1());
					entityAddress.setLine2(address.getLine2());
					entityAddress.setLine3(address.getLine3());
					entityAddress.setStateProvinceCode(address.getStateProvinceCode());
					entityAddress.setPostalCode(address.getPostalCode());
					entityAddress.setCountryCode(address.getCountryCode());
					entityAddress.setCity(address.getCity());
					entityAddress.setActive(address.isActive());
					entityAddress.setDefaultValue(address.isDflt());
					entityAddress.setId(address.getEntityAddressId());
					if(ObjectUtils.isNotNull(origAddresses)){
						for (EntityAddressContract origAddress : origAddresses) {
							if (origAddress.getId()!=null && StringUtils.equals(origAddress.getId(), entityAddress.getId())) {
								entityAddress.setVersionNumber(origAddress.getVersionNumber());
							}
						}
					}
					entityAddresses.add(entityAddress);
				}
			}
			entityType.setAddresses(entityAddresses);
    	}
	}

    protected List<PersonDocumentAddress> loadAddresses(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId, List<EntityAddress> entityAddresses, boolean suppressDisplay ) {
		List<PersonDocumentAddress> docAddresses = new ArrayList<PersonDocumentAddress>();
		if(ObjectUtils.isNotNull(entityAddresses)){
			for (EntityAddress address: entityAddresses) {
				if(address.isActive()){
					PersonDocumentAddress docAddress = new PersonDocumentAddress();
					docAddress.setEntityTypeCode(address.getEntityTypeCode());
					docAddress.setAddressTypeCode(address.getAddressType().getCode());

					//We do not need to check the privacy setting here - The UI should care of it
					docAddress.setLine1(address.getLine1Unmasked());
					docAddress.setLine2(address.getLine2Unmasked());
					docAddress.setLine3(address.getLine3Unmasked());
					docAddress.setStateProvinceCode(address.getStateProvinceCodeUnmasked());
					docAddress.setPostalCode(address.getPostalCodeUnmasked());
					docAddress.setCountryCode(address.getCountryCodeUnmasked());
					docAddress.setCity(address.getCityUnmasked());

					docAddress.setActive(address.isActive());
					docAddress.setDflt(address.isDefaultValue());
					docAddress.setEntityAddressId(address.getId());
					docAddress.setEdit(true);
					docAddresses.add(docAddress);
				}
			}
		}
		return docAddresses;
	}


    protected List <GroupMemberBo> populateGroupMembers(IdentityManagementPersonDocument identityManagementPersonDocument) {
		List <GroupMemberBo>  groupPrincipals = new ArrayList<GroupMemberBo>();
//		List<? extends Group> origGroups = getGroupService().getGroupsByPrincipalId(identityManagementPersonDocument.getPrincipalId());
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getGroups())){
			for (PersonDocumentGroup group : identityManagementPersonDocument.getGroups()) {
				GroupMember.Builder groupPrincipalImpl = GroupMember.Builder.create(group.getGroupId(), identityManagementPersonDocument.getPrincipalId(), KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
				if (group.getActiveFromDate() != null) {
					groupPrincipalImpl.setActiveFromDate(new DateTime(group.getActiveFromDate().getTime()));
				}
				if (group.getActiveToDate() != null) {
					groupPrincipalImpl.setActiveToDate(new DateTime(group.getActiveToDate().getTime()));
				}
				groupPrincipalImpl.setId(group.getGroupMemberId());


                //groupPrincipalImpl.setVersionNumber(group.getVersionNumber());
				// get the ORM-layer optimisic locking value
				// TODO: this should be replaced with the retrieval and storage of that value
				// in the document tables and not re-retrieved here
				Collection<GroupMember> currGroupMembers = getGroupService().getMembers(Collections.singletonList(group.getGroupId()));
				if(ObjectUtils.isNotNull(currGroupMembers)){
					for (GroupMember origGroupMember: currGroupMembers) {
                        if (origGroupMember.isActive(new DateTime(System.currentTimeMillis()))
                            && KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.equals(origGroupMember.getType())) {
                            if(origGroupMember.getId()!=null && StringUtils.equals(origGroupMember.getId(), group.getGroupMemberId())){
                                groupPrincipalImpl.setObjectId(origGroupMember.getObjectId());
                                groupPrincipalImpl.setVersionNumber(origGroupMember.getVersionNumber());
                            }
                        }
					}
				}

				groupPrincipals.add(GroupMemberBo.from(groupPrincipalImpl.build()));

			}
		}
		return groupPrincipals;
	}

    protected List<RoleMemberBo> populateRoleMembers(IdentityManagementPersonDocument identityManagementPersonDocument) {
		List<RoleBo> origRoles = getRolesForPrincipal(identityManagementPersonDocument.getPrincipalId());

		List <RoleMemberBo> roleMembers = new ArrayList<RoleMemberBo>();
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getRoles())){
			for (PersonDocumentRole role : identityManagementPersonDocument.getRoles()) {
				//if(role.isEditable()){
					List<RoleMemberBo> origRoleMembers = new ArrayList<RoleMemberBo>();
					if(ObjectUtils.isNotNull(origRoles)){
						for (RoleBo origRole : origRoles) {
							if (origRole.getId()!=null && StringUtils.equals(origRole.getId(), role.getRoleId())) {
								origRoleMembers = origRole.getMembers();
								break;
							}
						}
					}
					if (role.getRolePrncpls().isEmpty()) {
						if (!role.getDefinitions().isEmpty()) {
							RoleMemberBo roleMemberImpl = new RoleMemberBo();
							roleMemberImpl.setRoleId(role.getRoleId());
							roleMemberImpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
							roleMemberImpl.setType(MemberType.PRINCIPAL);
							roleMembers.add(roleMemberImpl);
						}
					} else {
						for (KimDocumentRoleMember roleMember : role.getRolePrncpls()) {
							RoleMemberBo roleMemberImpl = new RoleMemberBo();
							roleMemberImpl.setRoleId(role.getRoleId());
							// TODO : principalId is not ready here yet ?
							roleMemberImpl.setMemberId(identityManagementPersonDocument.getPrincipalId());
							roleMemberImpl.setType(MemberType.PRINCIPAL);
							roleMemberImpl.setId(roleMember.getRoleMemberId());
							if (roleMember.getActiveFromDate() != null) {
								roleMemberImpl.setActiveFromDateValue(
                                        new java.sql.Timestamp(roleMember.getActiveFromDate().getTime()));
							}
							if (roleMember.getActiveToDate() != null) {
								roleMemberImpl.setActiveToDateValue(
                                        new java.sql.Timestamp(roleMember.getActiveToDate().getTime()));
							}
							List<RoleMemberAttributeDataBo> origAttributes = new ArrayList<RoleMemberAttributeDataBo>();
							if(ObjectUtils.isNotNull(origRoleMembers)){
								for (RoleMemberBo origMember : origRoleMembers) {
									if (origMember.getId()!=null && StringUtils.equals(origMember.getId(), roleMember.getRoleMemberId())) {
										origAttributes = origMember.getAttributeDetails();
										roleMemberImpl.setVersionNumber(origMember.getVersionNumber());
									}
								}
							}
							List<RoleMemberAttributeDataBo> attributes = new ArrayList<RoleMemberAttributeDataBo>();
							if(CollectionUtils.isNotEmpty(roleMember.getQualifiers())){
								for (KimDocumentRoleQualifier qualifier : roleMember.getQualifiers()) {
									//if (StringUtils.isNotBlank(qualifier.getAttrVal())) {
										RoleMemberAttributeDataBo attribute = new RoleMemberAttributeDataBo();
										attribute.setId(qualifier.getAttrDataId());
										attribute.setAttributeValue(qualifier.getAttrVal());
										attribute.setKimAttributeId(qualifier.getKimAttrDefnId());
										attribute.setAssignedToId(qualifier.getRoleMemberId());
										attribute.setKimTypeId(qualifier.getKimTypId());

										updateAttrValIfNecessary(attribute);

										if(ObjectUtils.isNotNull(origAttributes)){
											for (RoleMemberAttributeDataBo origAttribute : origAttributes) {
												if (origAttribute.getId()!=null && StringUtils.equals(origAttribute.getId(), qualifier.getAttrDataId())) {
													attribute.setVersionNumber(origAttribute.getVersionNumber());
												}
											}
										}
										if (attribute.getVersionNumber() != null || StringUtils.isNotBlank(qualifier.getAttrVal())) {
											attributes.add(attribute);
										}
									//}
								}
							}
							roleMemberImpl.setAttributeDetails(attributes);
							roleMembers.add(roleMemberImpl);
						}
					}
				//}
			}
		}
		return roleMembers;
	}

	protected List<DelegateTypeBo> populateDelegations(IdentityManagementPersonDocument identityManagementPersonDocument){
		List<DelegateTypeBo> origDelegations = getPersonDelegations(identityManagementPersonDocument.getPrincipalId());
		List<DelegateTypeBo> kimDelegations = new ArrayList<DelegateTypeBo>();
		DelegateTypeBo newKimDelegation;
		DelegateTypeBo origDelegationImplTemp = null;
		List<DelegateMemberBo> origMembers;
		boolean activatingInactive = false;
		String newDelegationIdAssigned = "";
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getDelegations())){
			for(RoleDocumentDelegation roleDocumentDelegation: identityManagementPersonDocument.getDelegations()){
				newKimDelegation = new DelegateTypeBo();
				KimCommonUtilsInternal.copyProperties(newKimDelegation, roleDocumentDelegation);
				newKimDelegation.setRoleId(roleDocumentDelegation.getRoleId());
				if(ObjectUtils.isNotNull(origDelegations)){
					for(DelegateTypeBo origDelegationImpl: origDelegations){
						if((origDelegationImpl.getRoleId()!=null && StringUtils.equals(origDelegationImpl.getRoleId(), newKimDelegation.getRoleId())) &&
								(origDelegationImpl.getDelegationId()!=null && StringUtils.equals(origDelegationImpl.getDelegationId(), newKimDelegation.getDelegationId()))){
							//TODO: verify if you want to add  && newRoleMember.isActive() condition to if...
							newDelegationIdAssigned = newKimDelegation.getDelegationId();
							newKimDelegation.setDelegationId(origDelegationImpl.getDelegationId());
							activatingInactive = true;
						}
						if(origDelegationImpl.getDelegationId()!=null && StringUtils.equals(origDelegationImpl.getDelegationId(), newKimDelegation.getDelegationId())){
							newKimDelegation.setVersionNumber(origDelegationImpl.getVersionNumber());
							origDelegationImplTemp = origDelegationImpl;
						}
					}
				}
				origMembers = (origDelegationImplTemp==null || origDelegationImplTemp.getMembers()==null)?
									new ArrayList<DelegateMemberBo>():origDelegationImplTemp.getMembers();
				newKimDelegation.setMembers(getDelegationMembers(roleDocumentDelegation.getMembers(), origMembers, null, activatingInactive, newDelegationIdAssigned));
                newKimDelegation.setVersionNumber(null);
				kimDelegations.add(newKimDelegation);
				activatingInactive = false;
			}
		}
		return kimDelegations;
	}

    protected List <RoleMemberAttributeDataBo> getBlankRoleMemberAttrs(List <RoleMemberBo> rolePrncpls) {

		List <RoleMemberAttributeDataBo>  blankRoleMemberAttrs = new ArrayList<RoleMemberAttributeDataBo>();
		if(ObjectUtils.isNotNull(rolePrncpls)){
			for (RoleMemberBo roleMbr : rolePrncpls) {
				List <RoleMemberAttributeDataBo>  roleMemberAttrs = new ArrayList<RoleMemberAttributeDataBo>();
				if (CollectionUtils.isNotEmpty(roleMbr.getAttributeDetails())) {
					for (RoleMemberAttributeDataBo attr : roleMbr.getAttributeDetails()) {
						if (StringUtils.isBlank(attr.getAttributeValue())) {
							roleMemberAttrs.add(attr);
						}
					}
					if (!roleMemberAttrs.isEmpty()) {
						roleMbr.getAttributeDetails().removeAll(roleMemberAttrs);
						blankRoleMemberAttrs.addAll(roleMemberAttrs);
					}

				}
			}
		}

		return blankRoleMemberAttrs;

	}

    protected List <RoleResponsibilityActionBo> populateRoleRspActions(IdentityManagementPersonDocument identityManagementPersonDocument) {
//		List<RoleImpl> origRoles = getRolesForPrincipal(identityManagementPersonDocument.getPrincipalId());

		List <RoleResponsibilityActionBo>  roleRspActions = new ArrayList<RoleResponsibilityActionBo>();
		if(CollectionUtils.isNotEmpty(identityManagementPersonDocument.getRoles())){
			for (PersonDocumentRole role : identityManagementPersonDocument.getRoles()) {
				if(CollectionUtils.isNotEmpty(role.getRolePrncpls())){
					for (KimDocumentRoleMember roleMbr : role.getRolePrncpls()) {
						if(CollectionUtils.isNotEmpty(roleMbr.getRoleRspActions())){
							for (KimDocumentRoleResponsibilityAction roleRspAction : roleMbr.getRoleRspActions()) {
								RoleResponsibilityActionBo entRoleRspAction = new RoleResponsibilityActionBo();
								entRoleRspAction.setId(roleRspAction.getRoleResponsibilityActionId());
								entRoleRspAction.setActionPolicyCode(roleRspAction.getActionPolicyCode());
								entRoleRspAction.setActionTypeCode(roleRspAction.getActionTypeCode());
								entRoleRspAction.setPriorityNumber(roleRspAction.getPriorityNumber());
								entRoleRspAction.setRoleMemberId(roleRspAction.getRoleMemberId());
								entRoleRspAction.setRoleResponsibilityId(roleRspAction.getRoleResponsibilityId());
								List<RoleResponsibilityActionBo> actions = getRoleRspActions( roleMbr.getRoleMemberId());
								if(ObjectUtils.isNotNull(actions)){
									for(RoleResponsibilityActionBo orgRspAction : actions) {
										if (orgRspAction.getId()!=null && StringUtils.equals(orgRspAction.getId(), roleRspAction.getRoleResponsibilityActionId())) {
											entRoleRspAction.setVersionNumber(orgRspAction.getVersionNumber());
										}
									}
								}
								roleRspActions.add(entRoleRspAction);
							}
						}
					}
				}
			}
		}
		return roleRspActions;

	}

	protected DataObjectService getDataObjectService() {
		if ( dataObjectService == null ) {
			dataObjectService = KradDataServiceLocator.getDataObjectService();
		}
		return dataObjectService;
	}

	protected IdentityService getIdentityService() {
		if ( identityService == null ) {
			identityService = KimApiServiceLocator.getIdentityService();
		}
		return identityService;
	}

	protected GroupService getGroupService() {
		if ( groupService == null ) {
			groupService = KimApiServiceLocator.getGroupService();
		}
		return groupService;
	}

	protected DocumentHelperService getDocumentHelperService() {
	    if ( documentHelperService == null ) {
	        documentHelperService = KNSServiceLocator.getDocumentHelperService();
		}
	    return this.documentHelperService;
	}

	protected RoleService getRoleService() {
	   	if(roleService == null){
	   		roleService = KimApiServiceLocator.getRoleService();
    	}
		return roleService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	protected ResponsibilityService getResponsibilityService() {
	   	if ( responsibilityService == null ) {
    		responsibilityService = KimApiServiceLocator.getResponsibilityService();
    	}
		return responsibilityService;
	}

	public void setResponsibilityService(ResponsibilityService responsibilityService) {
		this.responsibilityService = responsibilityService;
	}


	/* Role document methods */
	@Override
    public void loadRoleDoc(IdentityManagementRoleDocument identityManagementRoleDocument, Role role){
		RoleBo roleBo = getDataObjectService().find(RoleBo.class, role.getId());

		identityManagementRoleDocument.setRoleId(roleBo.getId());
		identityManagementRoleDocument.setKimType(KimTypeBo.to(roleBo.getKimRoleType()));
		identityManagementRoleDocument.setRoleTypeName(roleBo.getKimRoleType().getName());
		identityManagementRoleDocument.setRoleTypeId(roleBo.getKimTypeId());
		identityManagementRoleDocument.setRoleName(roleBo.getName());
		identityManagementRoleDocument.setRoleDescription(roleBo.getDescription());
		identityManagementRoleDocument.setActive(roleBo.isActive());
		identityManagementRoleDocument.setRoleNamespace(roleBo.getNamespaceCode());
		identityManagementRoleDocument.setEditing(true);

		identityManagementRoleDocument.setPermissions(loadPermissions(
                getDataObjectService().findMatching(RolePermissionBo.class,
                        QueryByCriteria.Builder.forAttribute(KimConstants.PrimaryKeyConstants.SUB_ROLE_ID, role.getId()).build()).getResults()));
        identityManagementRoleDocument.setResponsibilities(loadResponsibilities(
                getDataObjectService().findMatching(RoleResponsibilityBo.class,
                        QueryByCriteria.Builder.forAttribute(KimConstants.PrimaryKeyConstants.SUB_ROLE_ID, role.getId()).build()).getResults()));
        loadResponsibilityRoleRspActions(identityManagementRoleDocument);
        identityManagementRoleDocument.setMembers(loadRoleMembers(identityManagementRoleDocument, roleBo.getMembers()));
        loadMemberRoleRspActions(identityManagementRoleDocument);
		identityManagementRoleDocument.setDelegations(loadRoleDocumentDelegations(identityManagementRoleDocument, getRoleDelegations(roleBo.getId())));
		//Since delegation members are flattened out on the UI...
		setDelegationMembersInDocument(identityManagementRoleDocument);
		identityManagementRoleDocument.setKimType(KimTypeBo.to(roleBo.getKimRoleType()));
    }

    @Override
    public void loadRoleMembersBasedOnSearch(IdentityManagementRoleDocument identityManagementRoleDocument,
                                                    String memberSearchValue){

        List<KimDocumentRoleMember> roleMembersRestricted = new ArrayList<KimDocumentRoleMember>();
        List<KimDocumentRoleMember> members = identityManagementRoleDocument.getMembers();
        for (KimDocumentRoleMember roleMember : members){
            String memberName = roleMember.getMemberName().toLowerCase();
            if (memberName.startsWith(memberSearchValue.toLowerCase())) {
                roleMembersRestricted.add(roleMember);
            }
        }

        identityManagementRoleDocument.setSearchResultMembers(roleMembersRestricted);
    }

    @Override
    public void clearRestrictedRoleMembersSearchResults(IdentityManagementRoleDocument identityManagementRoleDocument) {
        List<KimDocumentRoleMember> roleMembersRestricted =  new ArrayList<KimDocumentRoleMember>();
        List<KimDocumentRoleMember> members = identityManagementRoleDocument.getMembers();
        identityManagementRoleDocument.setSearchResultMembers(roleMembersRestricted);
        identityManagementRoleDocument.setMembers(members);
    }

    @Override
    public void setDelegationMembersInDocument(IdentityManagementRoleDocument identityManagementRoleDocument){
        if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getDelegations())){
            for(RoleDocumentDelegation delegation: identityManagementRoleDocument.getDelegations()){
                if(CollectionUtils.isNotEmpty(delegation.getMembers())){
                    RoleMemberBo roleMember;
                    for(RoleDocumentDelegationMember member: delegation.getMembers()){
                        member.setDelegationTypeCode(delegation.getDelegationTypeCode());
                        if (StringUtils.isEmpty(member.getRoleMemberName())) {
                            roleMember = getRoleMemberForRoleMemberId(member.getRoleMemberId());
                            if(roleMember!=null){
                                member.setRoleMemberName(getMemberName(roleMember.getType(), roleMember.getMemberId()));
                                member.setRoleMemberNamespaceCode(getMemberNamespaceCode(roleMember.getType(), roleMember.getMemberId()));
                            }
                        }
                        member.setEdit(true);
                        identityManagementRoleDocument.getDelegationMembers().add(member);
                    }
                }
            }
        }
    }

	protected List<KimDocumentRoleResponsibility> loadResponsibilities(List<RoleResponsibilityBo> roleResponsibilities){
		List<KimDocumentRoleResponsibility> documentRoleResponsibilities = new ArrayList<KimDocumentRoleResponsibility>();
		if(ObjectUtils.isNotNull(roleResponsibilities)){
			for(RoleResponsibilityBo roleResponsibility: roleResponsibilities){
				if(roleResponsibility.isActive()) {
					KimDocumentRoleResponsibility roleResponsibilityCopy = new KimDocumentRoleResponsibility();
					KimCommonUtilsInternal.copyProperties(roleResponsibilityCopy, roleResponsibility);
					roleResponsibilityCopy.setEdit(true);
					documentRoleResponsibilities.add(roleResponsibilityCopy);
				}
			}
		}
		return documentRoleResponsibilities;
	}

	protected List<KimDocumentRolePermission> loadPermissions(List<RolePermissionBo> rolePermissions){
		List<KimDocumentRolePermission> documentRolePermissions = new ArrayList<KimDocumentRolePermission>();
		KimDocumentRolePermission rolePermissionCopy;
		if(ObjectUtils.isNotNull(rolePermissions)){
			for(RolePermissionBo rolePermission: rolePermissions){
				if ( rolePermission.isActive() ) {
					rolePermissionCopy = new KimDocumentRolePermission();
					rolePermissionCopy.setRolePermissionId(rolePermission.getId());
					rolePermissionCopy.setRoleId(rolePermission.getRoleId());
					rolePermissionCopy.setPermissionId(rolePermission.getPermissionId());
					rolePermissionCopy.setPermission(PermissionBo.to(rolePermission.getPermission()));
					rolePermissionCopy.setEdit(true);
					documentRolePermissions.add(rolePermissionCopy);
				}
			}
		}
		return documentRolePermissions;
	}

    @Override
    public void setMembersInDocument(IdentityManagementRoleDocument identityManagementRoleDocument){
        if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getDelegations())){
            RoleBo roleBo = getDataObjectService().find(RoleBo.class, identityManagementRoleDocument.getRoleId());
            List<RoleMemberBo> members = roleBo.getMembers();
            List<RoleMemberBo> membersToRemove = new ArrayList<RoleMemberBo>();
            boolean found = false;
            for(KimDocumentRoleMember modifiedMember : identityManagementRoleDocument.getModifiedMembers() ) {
                for(RoleMemberBo member : members) {
                    if (modifiedMember.getRoleMemberId().equals(member.getId())) {
                        membersToRemove.add(member);
                        found = true;
                    }
                    if (found) {
                        break;
                    }
                }
            }
            for(RoleMemberBo memberToRemove : membersToRemove ) {
                members.remove(memberToRemove);
            }

            identityManagementRoleDocument.setMembers(loadRoleMembers(identityManagementRoleDocument, members));
            loadMemberRoleRspActions(identityManagementRoleDocument);
        }
    }

    public Map<String, Group> findGroupsForRole(final String roleId) {
        Map<String, Group> roleGroupMembers = new HashMap<String, Group>();

        // Find group members of a given role
        List<RoleMember> groupRoleMembers = getRoleService().findRoleMembers( QueryByCriteria.Builder.fromPredicates(
                PredicateFactory.equal(KIMPropertyConstants.RoleMember.ROLE_ID, roleId)
                , PredicateFactory.equal(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE,MemberType.GROUP.getCode()))).getResults();

        List<String> groupIds = new ArrayList<String>( groupRoleMembers.size() );
        for ( RoleMember rm : groupRoleMembers ) {
            groupIds.add( rm.getMemberId() );
        }

        List<Group> groups = getGroupService().getGroups(groupIds);
        for ( Group g : groups ) {
            roleGroupMembers.put(g.getId(), g);
        }
        return roleGroupMembers;
    }

    protected List<KimDocumentRoleMember> loadRoleMembers(
            IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleMemberBo> members){
        List<KimDocumentRoleMember> pndMembers = new ArrayList<KimDocumentRoleMember>();

        if(KRADUtils.isNull(members) || members.isEmpty() ){
            return pndMembers;
        }

        // extract all the principal role member IDs
        List<String> roleMemberPrincipalIds = new ArrayList<String>();
        for(RoleMemberBo roleMember : members) {
            if (roleMember.getType().getCode().equals(KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.getCode())) {
                if ((!roleMemberPrincipalIds.contains(roleMember.getMemberId())) && roleMember.isActive()) {
                    roleMemberPrincipalIds.add(roleMember.getMemberId());
                }
            }
        }

        // pull in all the names for the principals for later use
        List<EntityDefault> principals = getIdentityService().findEntityDefaults( QueryByCriteria.Builder.fromPredicates(
                PredicateFactory.in("principals." + KIMPropertyConstants.Person.PRINCIPAL_ID, roleMemberPrincipalIds)
                , PredicateFactory.equal("principals." + KRADPropertyConstants.ACTIVE, Boolean.TRUE)
                , PredicateFactory.equal(KRADPropertyConstants.ACTIVE, Boolean.TRUE)
                )).getResults();
        Map<String, EntityDefault> principalIdToEntityMap = new HashMap<String,EntityDefault>( principals.size() );
        for ( EntityDefault entity : principals ) {
            // yes, I'm missing null checks, but since I searched on principal ID - there needs to be
            // at least one record
            principalIdToEntityMap.put( entity.getPrincipals().get(0).getPrincipalId(), entity );
        }

        // pull in all the group members of this role
        Map<String, Group> roleGroupMembers = findGroupsForRole(identityManagementRoleDocument.getRoleId());

        for(RoleMemberBo member: members){
            KimDocumentRoleMember pndMember = new KimDocumentRoleMember();
            pndMember.setActiveFromDate(member.getActiveFromDateValue());
            pndMember.setActiveToDate(member.getActiveToDateValue());
            pndMember.setActive(member.isActive( getDateTimeService().getCurrentTimestamp()));
            if(pndMember.isActive()){
                pndMember.setRoleMemberId(member.getId());
                pndMember.setRoleId(member.getRoleId());
                pndMember.setMemberTypeCode(member.getType().getCode());
                pndMember.setMemberId(member.getMemberId());
                pndMember.setMemberNamespaceCode(getMemberNamespaceCode(member.getType(), member.getMemberId()));

                if ( StringUtils.equals( pndMember.getMemberTypeCode(), MemberType.PRINCIPAL.getCode() ) ) {
                    EntityDefault entity = principalIdToEntityMap.get(member.getMemberId());
                    if ( entity != null ) {
                        pndMember.setMemberName(entity.getPrincipals().get(0).getPrincipalName());
                        if ( entity.getName() != null ) {
                            pndMember.setMemberFullName(entity.getName().getFirstName() + " " + entity.getName().getLastName());
                        }
                    }
                } else if ( StringUtils.equals( pndMember.getMemberTypeCode(), MemberType.GROUP.getCode() ) ) {
                    Group group =  roleGroupMembers.get(member.getMemberId());
                    if (group != null) {
                        pndMember.setMemberName(group.getName());
                        pndMember.setMemberNamespaceCode(group.getNamespaceCode());
                        pndMember.setMemberFullName(group.getName());
                    }
                } else if ( StringUtils.equals( pndMember.getMemberTypeCode(), MemberType.ROLE.getCode() ) ) {
                    pndMember.setMemberName(getMemberName(member.getType(), member.getMemberId()));
                    pndMember.setMemberFullName(getMemberFullName(member.getType(), member.getMemberId()));
                }

                pndMember.setQualifiers(loadRoleMemberQualifiers(identityManagementRoleDocument, member.getAttributeDetails()));
                pndMember.setEdit(true);
                pndMembers.add(pndMember);
            }
        }
        Collections.sort(pndMembers, identityManagementRoleDocument.getMemberMetaDataType());
        return pndMembers;
    }

    protected void loadResponsibilityRoleRspActions(IdentityManagementRoleDocument identityManagementRoleDocument){
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getResponsibilities())){
			for(KimDocumentRoleResponsibility responsibility: identityManagementRoleDocument.getResponsibilities()){
				responsibility.getRoleRspActions().addAll(loadKimDocumentRoleRespActions(
						getRoleResponsibilityActionImpls(responsibility.getRoleResponsibilityId())));
			}
		}
	}

    protected RoleResponsibilityActionBo getRoleResponsibilityActionImpl(String roleResponsibilityActionId){
        return getDataObjectService().find(RoleResponsibilityActionBo.class, roleResponsibilityActionId);
    }

	protected List<RoleResponsibilityActionBo> getRoleResponsibilityActionImpls(String roleResponsibilityId){
		return getDataObjectService().findMatching(RoleResponsibilityActionBo.class, QueryByCriteria.Builder.fromPredicates(
                PredicateFactory.equal(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, "*"),
                PredicateFactory.equal(KimConstants.PrimaryKeyConstants.ROLE_RESPONSIBILITY_ID, roleResponsibilityId)
                )).getResults();
	}

	@Override
    public List<RoleResponsibilityActionBo> getRoleMemberResponsibilityActionImpls(String roleMemberId){
		return getDataObjectService().findMatching(RoleResponsibilityActionBo.class,
                QueryByCriteria.Builder.forAttribute(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId).build()).getResults();
	}

	protected void loadMemberRoleRspActions(IdentityManagementRoleDocument identityManagementRoleDocument){
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getMembers())){
			for(KimDocumentRoleMember member: identityManagementRoleDocument.getMembers()){
				member.getRoleRspActions().addAll(loadKimDocumentRoleRespActions(
						getRoleMemberResponsibilityActionImpls(member.getRoleMemberId()) ) );
			}
		}
	}

	protected List<KimDocumentRoleResponsibilityAction> loadKimDocumentRoleRespActions(
			List<RoleResponsibilityActionBo> roleRespActionImpls){
		List<KimDocumentRoleResponsibilityAction> documentRoleRespActions = new ArrayList<KimDocumentRoleResponsibilityAction>();
		KimDocumentRoleResponsibilityAction documentRoleRespAction;
		if(ObjectUtils.isNotNull(roleRespActionImpls)){
			for(RoleResponsibilityActionBo roleRespActionImpl: roleRespActionImpls){
				documentRoleRespAction = new KimDocumentRoleResponsibilityAction();
				KimCommonUtilsInternal.copyProperties(documentRoleRespAction, roleRespActionImpl);

                //primary key has different name in these objects!  we need to make sure to copy it over
                documentRoleRespAction.setRoleResponsibilityActionId(roleRespActionImpl.getId());

				// handle the roleResponsibility object being null since not all may be defined when ID value is "*"
				if ( ObjectUtils.isNotNull(roleRespActionImpl.getRoleResponsibility()) ) {
					documentRoleRespAction.setKimResponsibility(roleRespActionImpl.getRoleResponsibility().getKimResponsibility());
				}
				documentRoleRespActions.add(documentRoleRespAction);
			}
		}
		return documentRoleRespActions;
	}

    @Override
    public Object getMember(MemberType memberType, String memberId){
    	if(MemberType.PRINCIPAL.equals(memberType)) {
    	    return getIdentityService().getPrincipal(memberId);
        } else if(MemberType.GROUP.equals(memberType)){
            return getGroupService().getGroup(memberId);
        } else if(MemberType.ROLE.equals(memberType)){
        	return getRoleService().getRole(memberId);
        }
    	return null;
    }

	@Override
    public String getMemberName(MemberType memberType, String memberId){
		if (memberType == null || StringUtils.isEmpty(memberId)) {
		    return "";
	    }
		Object member = getMember(memberType, memberId);
		if (member == null) {
		    return "";
		}
		return getMemberName(memberType, member);
	}

	public String getMemberFullName(MemberType memberType, String memberId){
		if(memberType == null || StringUtils.isEmpty(memberId)) {
		    return "";
	    }
        if(MemberType.PRINCIPAL.equals(memberType)){
        	EntityDefault principalInfo = getIdentityService().getEntityDefaultByPrincipalId(memberId);
        	if (principalInfo != null &&  principalInfo.getName() != null ) {
    		    return principalInfo.getName().getFirstName() + " " + principalInfo.getName().getLastName();
        	}
        } else if(MemberType.GROUP.equals(memberType)){
        	Group group = (Group) getMember(memberType, memberId);
        	if (group != null) {
        		return group.getName();
        	}
        } else if(MemberType.ROLE.equals(memberType)){
        	Role role = (Role) getMember(memberType, memberId);
        	if ( role != null ) {
        	    return role.getName();
        	}
        }
        return "";
	}

	@Override
    public String getMemberNamespaceCode(MemberType memberType, String memberId){
        if(memberType == null || StringUtils.isEmpty(memberId)) {
            return "";
        }
    	Object member = getMember(memberType, memberId);
    	if ( member == null ) {
    	    return "";
    	}
        if(MemberType.GROUP.equals(memberType)){
            return ((Group)member).getNamespaceCode();
        } else if(MemberType.ROLE.equals(memberType)){
            return ((Role)member).getNamespaceCode();
        }
        return "";
	}

    @Override
    public String getMemberIdByName(MemberType memberType, String memberNamespaceCode, String memberName){
        if(MemberType.PRINCIPAL.equals(memberType)){
            Principal principal = getIdentityService().getPrincipalByPrincipalName(memberName);
            if(principal!=null) {
                return principal.getPrincipalId();
            }

       } else if(MemberType.GROUP.equals(memberType)){
        	Group groupInfo = getGroupService().getGroupByNamespaceCodeAndName(memberNamespaceCode, memberName);
        	if (groupInfo!=null) {
                return groupInfo.getId();
            }

        } else if(MemberType.ROLE.equals(memberType)){
        	return getRoleService().getRoleIdByNamespaceCodeAndName(memberNamespaceCode, memberName);
        }
        return "";
    }

    @Override
    public String getMemberName(MemberType memberType, Object member){
        if(MemberType.PRINCIPAL.equals(memberType)){
            return ((Principal)member).getPrincipalName();
        } else if(MemberType.GROUP.equals(memberType)){
            return ((Group)member).getName();
        } else if(MemberType.ROLE.equals(memberType)){
        	return ((Role)member).getName();
        }
        return "";
    }

    @Override
    public String getMemberNamespaceCode(MemberType memberType, Object member){
        if(MemberType.GROUP.equals(memberType)){
        	return ((Group)member).getNamespaceCode();
        } else if(MemberType.ROLE.equals(memberType)){
        	return ((Role)member).getNamespaceCode();
        }
        return "";
    }

    protected List<KimDocumentRoleQualifier> loadRoleMemberQualifiers(IdentityManagementRoleDocument identityManagementRoleDocument,
			List<RoleMemberAttributeDataBo> attributeDataList){
		List<KimDocumentRoleQualifier> pndMemberRoleQualifiers = new ArrayList<KimDocumentRoleQualifier>();
		KimDocumentRoleQualifier pndMemberRoleQualifier;

		// add all attributes from attributeDataList
		if(attributeDataList!=null){
			for(RoleMemberAttributeDataBo memberRoleQualifier: attributeDataList){
				pndMemberRoleQualifier = new KimDocumentRoleQualifier();
				pndMemberRoleQualifier.setAttrDataId(memberRoleQualifier.getId());
				pndMemberRoleQualifier.setAttrVal(memberRoleQualifier.getAttributeValue());
				pndMemberRoleQualifier.setRoleMemberId(memberRoleQualifier.getAssignedToId());
				pndMemberRoleQualifier.setKimTypId(memberRoleQualifier.getKimTypeId());
				pndMemberRoleQualifier.setKimAttrDefnId(memberRoleQualifier.getKimAttributeId());
				pndMemberRoleQualifier.setKimAttribute(memberRoleQualifier.getKimAttribute());
				formatAttrValIfNecessary(pndMemberRoleQualifier);
				pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
			}
		}
		// also add any attributes already in the document that are not in the attributeDataList
		int countOfOriginalAttributesNotPresent = 0;
		List<KimDocumentRoleQualifier> fillerRoleQualifiers = new ArrayList<KimDocumentRoleQualifier>();

		List<KimAttributeField> origAttributes = identityManagementRoleDocument.getDefinitions();
		if ( origAttributes != null ) {
			for(KimAttributeField key: origAttributes) {
				boolean attributePresent = false;
				String origAttributeId = identityManagementRoleDocument.getKimAttributeDefnId(key);
				if(attributeDataList!=null){
					for(RoleMemberAttributeDataBo memberRoleQualifier: attributeDataList){
						if(origAttributeId!=null && StringUtils.equals(origAttributeId, memberRoleQualifier.getKimAttribute().getId())){
							attributePresent = true;
							break;
						}
					}
				}
				if(!attributePresent){
					countOfOriginalAttributesNotPresent++;
					pndMemberRoleQualifier = new KimDocumentRoleQualifier();
					pndMemberRoleQualifier.setKimAttrDefnId(origAttributeId);
					KradDataServiceLocator.getDataObjectService().wrap(pndMemberRoleQualifier).fetchRelationship("kimAttribute");
					fillerRoleQualifiers.add(pndMemberRoleQualifier);
				}
			}

			if(countOfOriginalAttributesNotPresent != origAttributes.size()) {
				pndMemberRoleQualifiers.addAll(fillerRoleQualifiers);
			}
		}
		return pndMemberRoleQualifiers;
	}

    @Override
    public List<DelegateTypeBo> getRoleDelegations(String roleId){
		if(roleId==null) {
			return new ArrayList<DelegateTypeBo>();
        }
        return getDataObjectService().findMatching(DelegateTypeBo.class,
                QueryByCriteria.Builder.forAttribute(KIMPropertyConstants.Delegation.ROLE_ID, roleId).build()).getResults();

	}

    protected List<RoleDocumentDelegation> loadRoleDocumentDelegations(IdentityManagementRoleDocument identityManagementRoleDocument, List<DelegateTypeBo> delegations){
		List<RoleDocumentDelegation> delList = new ArrayList<RoleDocumentDelegation>();
		RoleDocumentDelegation documentDelegation;
		if(ObjectUtils.isNotNull(delegations)){
			for(DelegateTypeBo del: delegations){
				documentDelegation = new RoleDocumentDelegation();
				documentDelegation.setActive(del.isActive());
				if(documentDelegation.isActive()){
					documentDelegation.setDelegationId(del.getDelegationId());
					documentDelegation.setDelegationTypeCode(del.getDelegationTypeCode());
					documentDelegation.setKimTypeId(del.getKimTypeId());
					documentDelegation.setMembers(loadDelegationMembers(identityManagementRoleDocument, del.getMembers()));
					documentDelegation.setRoleId(del.getRoleId());
					documentDelegation.setEdit(true);
					delList.add(documentDelegation);
				}
			}
		}
		return delList;
	}

    protected List<RoleDocumentDelegationMember> loadDelegationMembers(IdentityManagementRoleDocument identityManagementRoleDocument, List<DelegateMemberBo> members){
		List<RoleDocumentDelegationMember> pndMembers = new ArrayList<RoleDocumentDelegationMember>();
		RoleDocumentDelegationMember pndMember;
		RoleMemberBo roleMember;
		if(ObjectUtils.isNotNull(members)){
			for(DelegateMemberBo member: members){
				pndMember = new RoleDocumentDelegationMember();
				pndMember.setActiveFromDate(member.getActiveFromDateValue());
				pndMember.setActiveToDate(member.getActiveToDateValue());
				pndMember.setActive(member.isActive(getDateTimeService().getCurrentTimestamp()));
				if(pndMember.isActive()){
					//KimCommonUtilsInternal.copyProperties(pndMember, member);
                    pndMember.setDelegationId(member.getDelegationId());
                    pndMember.setDelegationMemberId(member.getDelegationMemberId());
                    pndMember.setDelegationTypeCode(member.getType().getCode());
                    pndMember.setRoleMemberId(member.getRoleMemberId());
                    pndMember.setMemberId(member.getMemberId());
                    pndMember.setMemberTypeCode(member.getType().getCode());

					roleMember = getRoleMemberForRoleMemberId(member.getRoleMemberId());
					if(roleMember!=null){
						pndMember.setRoleMemberName(getMemberName(roleMember.getType(), roleMember.getMemberId()));
						pndMember.setRoleMemberNamespaceCode(getMemberNamespaceCode(roleMember.getType(), roleMember.getMemberId()));
					}
					pndMember.setMemberNamespaceCode(getMemberNamespaceCode(member.getType(), member.getMemberId()));
					pndMember.setMemberName(getMemberName(member.getType(), member.getMemberId()));
					pndMember.setEdit(true);
					pndMember.setQualifiers(loadDelegationMemberQualifiers(identityManagementRoleDocument, member.getAttributeDetails()));
					pndMembers.add(pndMember);
				}
			}
		}
		return pndMembers;
	}

    protected RoleMemberBo getRoleMemberForRoleMemberId(String roleMemberId){
		return getDataObjectService().find(RoleMemberBo.class, roleMemberId);
    }

    protected List<RoleDocumentDelegationMemberQualifier> loadDelegationMemberQualifiers(IdentityManagementRoleDocument identityManagementRoleDocument,
			List<DelegateMemberAttributeDataBo> attributeDataList){
		List<RoleDocumentDelegationMemberQualifier> pndMemberRoleQualifiers = new ArrayList<RoleDocumentDelegationMemberQualifier>();
		RoleDocumentDelegationMemberQualifier pndMemberRoleQualifier;
		List<KimAttributeField> origAttributes = identityManagementRoleDocument.getDefinitions();
		boolean attributePresent = false;
		String origAttributeId;
		if(origAttributes!=null){
			for(KimAttributeField key: origAttributes) {
				origAttributeId = identityManagementRoleDocument.getKimAttributeDefnId(key);
				if(attributeDataList!=null){
					for(DelegateMemberAttributeDataBo memberRoleQualifier: attributeDataList){
						if(origAttributeId!=null && StringUtils.equals(origAttributeId, memberRoleQualifier.getKimAttribute().getId())){
							pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
							pndMemberRoleQualifier.setAttrDataId(memberRoleQualifier.getId());
							pndMemberRoleQualifier.setAttrVal(memberRoleQualifier.getAttributeValue());
							pndMemberRoleQualifier.setDelegationMemberId(memberRoleQualifier.getAssignedToId());
							pndMemberRoleQualifier.setKimTypId(memberRoleQualifier.getKimTypeId());
							pndMemberRoleQualifier.setKimAttrDefnId(memberRoleQualifier.getKimAttributeId());
							pndMemberRoleQualifier.setKimAttribute(memberRoleQualifier.getKimAttribute());
							pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
							attributePresent = true;
						}
					}
				}
				if(!attributePresent){
					pndMemberRoleQualifier = new RoleDocumentDelegationMemberQualifier();
					pndMemberRoleQualifier.setKimAttrDefnId(origAttributeId);
					KradDataServiceLocator.getDataObjectService().wrap(pndMemberRoleQualifier).fetchRelationship("kimAttribute");
					pndMemberRoleQualifiers.add(pndMemberRoleQualifier);
				}
				attributePresent = false;
			}
		}
		return pndMemberRoleQualifiers;
	}

	/**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
	@Override
    public void saveRole(IdentityManagementRoleDocument identityManagementRoleDocument) {
		String roleId = identityManagementRoleDocument.getRoleId();
		RoleBo roleBo = getDataObjectService().find(RoleBo.class, roleId);

        List<DelegateTypeBo> origRoleDelegations = new ArrayList<DelegateTypeBo>();
        List<RoleResponsibilityBo> origRoleResponsibilities = new ArrayList<RoleResponsibilityBo>();
        List<Object> objectsToSave = new ArrayList<Object>();

		if (roleBo == null) {
		    roleBo = new RoleBo();
	        roleBo.setId(roleId);
	        roleBo.setKimTypeId(identityManagementRoleDocument.getRoleTypeId());
	        roleBo.setNamespaceCode(identityManagementRoleDocument.getRoleNamespace());
	        identityManagementRoleDocument.setActive(true);
		}
        List<RolePermissionBo> origRolePermissions = new ArrayList<RolePermissionBo>();

        QueryByCriteria altCriteria = QueryByCriteria.Builder.forAttribute(KimConstants.PrimaryKeyConstants.SUB_ROLE_ID, roleId).build();
    	origRolePermissions = new ArrayList<RolePermissionBo>(getDataObjectService().findMatching(RolePermissionBo.class, altCriteria).getResults());
        origRoleResponsibilities = getDataObjectService().findMatching(RoleResponsibilityBo.class, altCriteria).getResults();
        origRoleDelegations = getDataObjectService().findMatching(DelegateTypeBo.class, altCriteria).getResults();

        objectsToSave.addAll(getRolePermissions(identityManagementRoleDocument, origRolePermissions));
        objectsToSave.addAll(getRoleResponsibilities(identityManagementRoleDocument, origRoleResponsibilities));
        objectsToSave.addAll(getRoleResponsibilitiesActions(identityManagementRoleDocument));

        objectsToSave.add(roleBo);
        roleBo.setName(identityManagementRoleDocument.getRoleName());
        roleBo.setDescription(identityManagementRoleDocument.getRoleDescription());
        roleBo.setActive(identityManagementRoleDocument.isActive());

		if( getKimTypeInfoService().getKimType(identityManagementRoleDocument.getRoleTypeId()) == null ) {
			LOG.error( "Kim type not found for:"+identityManagementRoleDocument.getRoleTypeId(), new Throwable() );
		}

		String initiatorPrincipalId = getInitiatorPrincipalId(identityManagementRoleDocument);

		if(canAssignToRole(identityManagementRoleDocument, initiatorPrincipalId)){
			updateRoleMembers( roleBo.getId(), roleBo.getKimTypeId(), identityManagementRoleDocument.getModifiedMembers(), roleBo.getMembers());

			objectsToSave.addAll(getRoleMemberResponsibilityActions(roleBo.getMembers()));
			objectsToSave.addAll(getRoleDelegations(identityManagementRoleDocument, origRoleDelegations));
		}
        for ( Object bo : objectsToSave ) {
            getDataObjectService().save(bo);
        }
		KimImplServiceLocator.getResponsibilityInternalService().updateActionRequestsForResponsibilityChange(getChangedRoleResponsibilityIds(identityManagementRoleDocument, origRoleResponsibilities));
		if(!roleBo.isActive()){
			// when a role is inactivated, inactivate the memberships of principals, groups, and roles in
			// that role, delegations, and delegation members, and that roles memberships in other roles
			KimImplServiceLocator.getRoleInternalService().roleInactivated(identityManagementRoleDocument.getRoleId());
		}
	}

	protected List<RolePermissionBo> getRolePermissions(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RolePermissionBo> origRolePermissions){
		List<RolePermissionBo> rolePermissions = new ArrayList<RolePermissionBo>();
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getPermissions())){
			for(KimDocumentRolePermission documentRolePermission: identityManagementRoleDocument.getPermissions()){
				RolePermissionBo newRolePermission = new RolePermissionBo();
				newRolePermission.setId(documentRolePermission.getRolePermissionId());
				newRolePermission.setRoleId(identityManagementRoleDocument.getRoleId());
				newRolePermission.setPermissionId(documentRolePermission.getPermissionId());
				newRolePermission.setActive( documentRolePermission.isActive() );

				newRolePermission.setActive(documentRolePermission.isActive());
				if (ObjectUtils.isNotNull(origRolePermissions)) {
                    for (RolePermissionBo origPermissionImpl : origRolePermissions) {
                        if (!StringUtils.equals(origPermissionImpl.getRoleId(), newRolePermission.getRoleId())
                                && StringUtils.equals(origPermissionImpl.getPermissionId(), newRolePermission.getPermissionId())
                                && origPermissionImpl.isActive()
                                && newRolePermission.isActive()) {
							newRolePermission.setId(origPermissionImpl.getId());
						}
						if(origPermissionImpl.getId()!=null && StringUtils.equals(origPermissionImpl.getId(), newRolePermission.getId())){
							newRolePermission.setVersionNumber(origPermissionImpl.getVersionNumber());
                            newRolePermission.setObjectId(origPermissionImpl.getObjectId());
						}
					}
				}
				rolePermissions.add(newRolePermission);
			}
		}
		return rolePermissions;
	}

	protected List<RoleResponsibilityBo> getRoleResponsibilities(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleResponsibilityBo> origRoleResponsibilities){
		List<RoleResponsibilityBo> roleResponsibilities = new ArrayList<RoleResponsibilityBo>();
		RoleResponsibilityBo newRoleResponsibility;
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getResponsibilities())){
			for(KimDocumentRoleResponsibility documentRoleResponsibility: identityManagementRoleDocument.getResponsibilities()){
				newRoleResponsibility = new RoleResponsibilityBo();
				KimCommonUtilsInternal.copyProperties(newRoleResponsibility, documentRoleResponsibility);
				newRoleResponsibility.setActive(documentRoleResponsibility.isActive());
				newRoleResponsibility.setRoleId(identityManagementRoleDocument.getRoleId());
                newRoleResponsibility.setVersionNumber(null);
				if(ObjectUtils.isNotNull(origRoleResponsibilities)){
					for(RoleResponsibilityBo origResponsibilityImpl: origRoleResponsibilities){
						if(!StringUtils.equals(origResponsibilityImpl.getRoleId(), newRoleResponsibility.getRoleId()) &&
								StringUtils.equals(origResponsibilityImpl.getResponsibilityId(), newRoleResponsibility.getResponsibilityId()) &&
								!origResponsibilityImpl.isActive() && newRoleResponsibility.isActive()){
							newRoleResponsibility.setRoleResponsibilityId(origResponsibilityImpl.getRoleResponsibilityId());
						}
						if(origResponsibilityImpl.getRoleResponsibilityId()!=null && StringUtils.equals(origResponsibilityImpl.getRoleResponsibilityId(), newRoleResponsibility.getRoleResponsibilityId())) {
                            newRoleResponsibility.setVersionNumber(origResponsibilityImpl.getVersionNumber());
                            newRoleResponsibility.setObjectId(origResponsibilityImpl.getObjectId());
                        }
					}
				}
				roleResponsibilities.add(newRoleResponsibility);
			}
		}
		return roleResponsibilities;
	}


	protected List <RoleResponsibilityActionBo> getRoleResponsibilitiesActions(
			IdentityManagementRoleDocument identityManagementRoleDocument){
		List <RoleResponsibilityActionBo>  roleRspActions = new ArrayList<RoleResponsibilityActionBo>();
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getResponsibilities())){
		// loop over the responsibilities assigned to the role
			for(KimDocumentRoleResponsibility roleResponsibility : identityManagementRoleDocument.getResponsibilities()){
				// only process if the actions are not assigned at the role member level
				if(!getResponsibilityInternalService().areActionsAtAssignmentLevelById(roleResponsibility.getResponsibilityId())){
					List<KimDocumentRoleResponsibilityAction> documentRoleResponsibilityActions = roleResponsibility.getRoleRspActions();
					if( ObjectUtils.isNotNull(documentRoleResponsibilityActions)
							&& !documentRoleResponsibilityActions.isEmpty()
							&& StringUtils.isNotBlank(documentRoleResponsibilityActions.get(0).getRoleResponsibilityActionId() ) ) {
						RoleResponsibilityActionBo roleRspAction = new RoleResponsibilityActionBo();
						roleRspAction.setId(documentRoleResponsibilityActions.get(0).getRoleResponsibilityActionId());
						roleRspAction.setActionPolicyCode(documentRoleResponsibilityActions.get(0).getActionPolicyCode());
						roleRspAction.setActionTypeCode(documentRoleResponsibilityActions.get(0).getActionTypeCode());
						roleRspAction.setPriorityNumber(documentRoleResponsibilityActions.get(0).getPriorityNumber());
						roleRspAction.setForceAction(documentRoleResponsibilityActions.get(0).isForceAction());
						roleRspAction.setRoleMemberId("*");
						roleRspAction.setRoleResponsibilityId(documentRoleResponsibilityActions.get(0).getRoleResponsibilityId());
						updateResponsibilityActionVersionNumber(roleRspAction, getRoleResponsibilityActionImpl(roleRspAction.getId()));
						roleRspActions.add(roleRspAction);
					}
				}
			}
		}
		return roleRspActions;
	}

	// FIXME: This should be pulling by the PK, not using another method which pulls multiple records and then finds
	// the right one here!
	protected void updateResponsibilityActionVersionNumber(RoleResponsibilityActionBo newRoleRspAction,
			RoleResponsibilityActionBo origRoleRespActionImpl){
		if(ObjectUtils.isNotNull(origRoleRespActionImpl)){
            if(origRoleRespActionImpl.getId()!=null && StringUtils.equals(origRoleRespActionImpl.getId(), newRoleRspAction.getId())) {
                newRoleRspAction.setVersionNumber(origRoleRespActionImpl.getVersionNumber());
                newRoleRspAction.setObjectId(origRoleRespActionImpl.getObjectId());
            }
		}
	}

	protected List<RoleResponsibilityActionBo> getRoleMemberResponsibilityActions(List<RoleMemberBo> newRoleMembersList){
		List<RoleResponsibilityActionBo> roleRspActions = new ArrayList<RoleResponsibilityActionBo>();
		if(ObjectUtils.isNotNull(newRoleMembersList)){
			for(RoleMemberBo roleMember: newRoleMembersList){
			    if ( roleMember.getRoleRspActions() != null ) {
			        roleRspActions.addAll(roleMember.getRoleRspActions());
			    }
			}
		}
		return roleRspActions;
	}

	/*protected List<RoleResponsibilityActionBo> getRoleMemberResponsibilityActions(IdentityManagementRoleDocument identityManagementRoleDocument){
		List<RoleResponsibilityActionBo> roleRspActions = new ArrayList<RoleResponsibilityActionBo>();
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getMembers())){
			for(KimDocumentRoleMember roleMember: identityManagementRoleDocument.getMembers()){
				for(KimDocumentRoleResponsibilityAction roleRspAction : roleMember.getRoleRspActions()){
					RoleResponsibilityActionBo entRoleRspAction = new RoleResponsibilityActionBo();
					entRoleRspAction.setId(roleRspAction.getRoleResponsibilityActionId());
					entRoleRspAction.setActionPolicyCode(roleRspAction.getActionPolicyCode());
					entRoleRspAction.setActionTypeCode(roleRspAction.getActionTypeCode());
					entRoleRspAction.setPriorityNumber(roleRspAction.getPriorityNumber());
					entRoleRspAction.setRoleMemberId(roleRspAction.getRoleMemberId());
					entRoleRspAction.setForceAction(roleRspAction.isForceAction());
					entRoleRspAction.setRoleResponsibilityId(roleRspAction.getRoleResponsibilityId());
					List<RoleResponsibilityActionBo> actions = getRoleRspActions(roleMember.getRoleMemberId());
					if(ObjectUtils.isNotNull(actions)){
						for(RoleResponsibilityActionBo orgRspAction : actions) {
							if (orgRspAction.getId()!=null && StringUtils.equals(orgRspAction.getId(), roleRspAction.getRoleResponsibilityActionId())) {
								entRoleRspAction.setVersionNumber(orgRspAction.getVersionNumber());
							}
						}
					}
					roleRspActions.add(entRoleRspAction);
				}
			}
		}
		return roleRspActions;
	}*/

    protected void updateRoleMembers( String roleId, String kimTypeId, List<KimDocumentRoleMember> modifiedRoleMembers, List<RoleMemberBo> roleMembers){
//        RoleMemberBo newRoleMember;
//        RoleMemberBo origRoleMemberImplTemp;
//        List<RoleMemberAttributeDataBo> origAttributes;
//        boolean activatingInactive = false;
//        String newRoleMemberIdAssigned = "";

        //identityManagementRoleDocument.setKimType(getKimTypeInfoService().getKimType(identityManagementRoleDocument.getRoleTypeId()));
        KimType roleType = getKimTypeInfoService().getKimType(kimTypeId);
        KimTypeService kimTypeService = KimFrameworkServiceLocator.getKimTypeService(roleType);

        if(CollectionUtils.isEmpty(modifiedRoleMembers)){
            return;
        }
        for(KimDocumentRoleMember documentRoleMember : modifiedRoleMembers){
            boolean isNewRoleMember = true;
            for( RoleMemberBo roleMember : roleMembers ) {
                // are we editing an existing record?
                if ( StringUtils.equals( roleMember.getId(), documentRoleMember.getRoleMemberId()) ) {
                    // yes we are
                    roleMember.setActiveFromDateValue(documentRoleMember.getActiveFromDate());
                    roleMember.setActiveToDateValue(documentRoleMember.getActiveToDate());
                    isNewRoleMember = false;
                    updateRoleMemberResponsibilityActions( documentRoleMember.getRoleRspActions(), roleMember.getRoleRspActions() );
                    break;
                }
            }
            if ( isNewRoleMember ) {
                RoleMemberBo roleMember = null;
                roleMember = new RoleMemberBo();
                roleMember.setId(documentRoleMember.getRoleMemberId());
                roleMember.setRoleId(roleId);
                roleMember.setTypeCode(documentRoleMember.getMemberTypeCode());
                roleMember.setMemberId(documentRoleMember.getMemberId());
                roleMember.setType(MemberType.fromCode(documentRoleMember.getMemberTypeCode()));
                roleMember.setActiveFromDateValue(documentRoleMember.getActiveFromDate());
                roleMember.setActiveToDateValue(documentRoleMember.getActiveToDate());

                roleMember.setAttributeDetails(getRoleMemberAttributeData(documentRoleMember.getQualifiers(), null));
                roleMember.setRoleRspActions( new ArrayList<RoleResponsibilityActionBo>() );
                updateRoleMemberResponsibilityActions( documentRoleMember.getRoleRspActions(), roleMember.getRoleRspActions() );

                roleMembers.add(roleMember);
            }

// TODO
//                        !kimTypeService.validateUniqueAttributes(
//                                identityManagementRoleDocument.getKimType().getId(),
//                                documentRoleMember.getQualifierAsMap(), roleMember.getAttributes()).isEmpty()) {
//
        }
    }

    // FIXME : this is not working yet
	protected void updateRoleMemberResponsibilityActions(List<KimDocumentRoleResponsibilityAction> documentRoleMemberActions, List<RoleResponsibilityActionBo> roleMemberActions){
	    // Make a copy of the list which we can modify - so that we can use it to
	    // remove leftovers from the original list when done with updates and inserts
	    List<RoleResponsibilityActionBo> existingRoleMemberActions = new ArrayList<RoleResponsibilityActionBo>( roleMemberActions );
	    // loop over document items
		for(KimDocumentRoleResponsibilityAction docRoleRspAction : documentRoleMemberActions){
		    boolean isNewAction = true;
            // loop over role member items
		    Iterator<RoleResponsibilityActionBo> rraInterator = existingRoleMemberActions.iterator();
		    while ( rraInterator.hasNext() ) {
		        RoleResponsibilityActionBo roleRspAction = rraInterator.next();
			    // we have a match, update the existing record
                // If the ID's match
				if (StringUtils.equals(roleRspAction.getId(), docRoleRspAction.getRoleResponsibilityActionId())) {
                    // update the existing record
				    roleRspAction.setActionPolicyCode(docRoleRspAction.getActionPolicyCode());
				    roleRspAction.setActionTypeCode(docRoleRspAction.getActionTypeCode());
				    roleRspAction.setPriorityNumber(docRoleRspAction.getPriorityNumber());
				    roleRspAction.setRoleMemberId(docRoleRspAction.getRoleMemberId());
				    roleRspAction.setForceAction(docRoleRspAction.isForceAction());
                    // mark it as a "found" record
				    rraInterator.remove();
				    isNewAction = false;
                }
	            // if no match on the loop, then we have a new record
				if ( isNewAction ) {
				    // create the new item and add it to the list
                    RoleResponsibilityActionBo newRoleRspAction = new RoleResponsibilityActionBo();
                    newRoleRspAction.setId(docRoleRspAction.getRoleResponsibilityActionId());
                    newRoleRspAction.setActionPolicyCode(docRoleRspAction.getActionPolicyCode());
                    newRoleRspAction.setActionTypeCode(docRoleRspAction.getActionTypeCode());
                    newRoleRspAction.setPriorityNumber(docRoleRspAction.getPriorityNumber());
                    newRoleRspAction.setRoleMemberId(docRoleRspAction.getRoleMemberId());
                    newRoleRspAction.setForceAction(docRoleRspAction.isForceAction());
                    newRoleRspAction.setRoleResponsibilityId("*");
                    roleMemberActions.add(newRoleRspAction);
				}
			}
            // for all items not "found", they are no longer present, delete them
            for ( RoleResponsibilityActionBo missingRra : existingRoleMemberActions ) {
                roleMemberActions.remove(missingRra);
            }
		}
		//return roleRspActions;
	}

	protected List<RoleMemberAttributeDataBo> getRoleMemberAttributeData(List<KimDocumentRoleQualifier> qualifiers, List<RoleMemberAttributeDataBo> origAttributes){
		List<RoleMemberAttributeDataBo> roleMemberAttributeDataList = new ArrayList<RoleMemberAttributeDataBo>();
		RoleMemberAttributeDataBo newRoleMemberAttributeData;
		if(CollectionUtils.isNotEmpty(qualifiers)){
			for(KimDocumentRoleQualifier memberRoleQualifier: qualifiers){
				if(StringUtils.isNotBlank(memberRoleQualifier.getAttrVal())){
					newRoleMemberAttributeData = new RoleMemberAttributeDataBo();
					newRoleMemberAttributeData.setId(memberRoleQualifier.getAttrDataId());
					newRoleMemberAttributeData.setAttributeValue(memberRoleQualifier.getAttrVal());
					newRoleMemberAttributeData.setAssignedToId(memberRoleQualifier.getRoleMemberId());
					newRoleMemberAttributeData.setKimTypeId(memberRoleQualifier.getKimTypId());
					newRoleMemberAttributeData.setKimAttributeId(memberRoleQualifier.getKimAttrDefnId());

					updateAttrValIfNecessary(newRoleMemberAttributeData);

					if(ObjectUtils.isNotNull(origAttributes)){
						for(RoleMemberAttributeDataBo origAttribute: origAttributes){
//							if(activatingInactive && StringUtils.equals(origAttribute.getKimAttributeId(), newRoleMemberAttributeData.getKimAttributeId()) &&
//									StringUtils.equals(newRoleMemberAttributeData.getAssignedToId(), newRoleMemberIdAssigned)){
//								newRoleMemberAttributeData.setAssignedToId(origAttribute.getAssignedToId());
//								newRoleMemberAttributeData.setId(origAttribute.getId());
//							}
							if(origAttribute.getId()!=null && StringUtils.equals(origAttribute.getId(), newRoleMemberAttributeData.getId())){
								newRoleMemberAttributeData.setVersionNumber(origAttribute.getVersionNumber());
							}
						}
					}
					roleMemberAttributeDataList.add(newRoleMemberAttributeData);
				}
			}
		}
		return roleMemberAttributeDataList;
	}

	/**
	 * Determines if the attribute value on the attribute data should be updated; if so, it performs some attribute value formatting.
	 * In the default implementation, this method formats checkbox controls
	 *
	 * @param roleMemberAttributeData a role member qualifier attribute to update
	 */
	protected void updateAttrValIfNecessary(RoleMemberAttributeDataBo roleMemberAttributeData) {
		if (doCheckboxLogic(roleMemberAttributeData.getKimTypeId(), roleMemberAttributeData.getKimAttributeId())) {
			convertCheckboxAttributeData(roleMemberAttributeData);
		}
	}

	protected void formatAttrValIfNecessary(KimDocumentRoleQualifier roleQualifier) {
        if (doCheckboxLogic(roleQualifier.getKimTypId(), roleQualifier.getKimAttrDefnId())) {
            formatCheckboxAttributeData(roleQualifier);
        }
	}

    private boolean doCheckboxLogic(String kimTypeId, String attrId) {
        final KimAttributeField attributeDefinition = getAttributeDefinition(kimTypeId, attrId);
        return attributeDefinition != null
                && attributeDefinition.getAttributeField().getControl() != null
                && (attributeDefinition.getAttributeField().getControl() instanceof RemotableCheckboxGroup
                        || attributeDefinition.getAttributeField().getControl() instanceof RemotableCheckbox);
    }

	protected void formatCheckboxAttributeData(KimDocumentRoleQualifier roleQualifier) {
		if (roleQualifier.getAttrVal().equals(KimConstants.KIM_ATTRIBUTE_BOOLEAN_TRUE_STR_VALUE)) {
			roleQualifier.setAttrVal(KimConstants.KIM_ATTRIBUTE_BOOLEAN_TRUE_STR_VALUE_DISPLAY);
		} else if (roleQualifier.getAttrVal().equals(KimConstants.KIM_ATTRIBUTE_BOOLEAN_FALSE_STR_VALUE)) {
			roleQualifier.setAttrVal(KimConstants.KIM_ATTRIBUTE_BOOLEAN_FALSE_STR_VALUE_DISPLAY);
		}
	}

	/**
	 * Finds the KNS attribute used to render the given KimAttributeData
	 *
     * @return the KNS attribute used to render that qualifier, or null if the AttributeDefinition cannot be determined
	 */
	protected KimAttributeField getAttributeDefinition(String kimTypId, String attrDefnId) {
		final KimType type = getKimTypeInfoService().getKimType(kimTypId);
		if (type != null) {
			final KimTypeService typeService = GlobalResourceLoader.<KimTypeService>getService(QName.valueOf(type.getServiceName()));
			if (typeService != null) {
				final KimTypeAttribute attributeInfo = type.getAttributeDefinitionById(attrDefnId);
				if (attributeInfo != null) {
					final List<KimAttributeField> attributeMap = typeService.getAttributeDefinitions(type.getId());
					if (attributeMap != null) {
						return DataDictionaryTypeServiceHelper.findAttributeField(
                                attributeInfo.getKimAttribute().getAttributeName(), attributeMap);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Formats the attribute value on this checkbox attribute, changing "on" to "Y" and "off" to "N"
	 *
	 * @param roleMemberAttributeData the attribute data to format the attribute value of
	 */
	protected void convertCheckboxAttributeData(RoleMemberAttributeDataBo roleMemberAttributeData) {
		if (roleMemberAttributeData.getAttributeValue().equalsIgnoreCase(KimConstants.KIM_ATTRIBUTE_BOOLEAN_TRUE_STR_VALUE_DISPLAY)) {
			roleMemberAttributeData.setAttributeValue(KimConstants.KIM_ATTRIBUTE_BOOLEAN_TRUE_STR_VALUE);
		} else if (roleMemberAttributeData.getAttributeValue().equalsIgnoreCase(KimConstants.KIM_ATTRIBUTE_BOOLEAN_FALSE_STR_VALUE_DISPLAY)) {
			roleMemberAttributeData.setAttributeValue(KimConstants.KIM_ATTRIBUTE_BOOLEAN_FALSE_STR_VALUE);
		}
	}

	protected List<DelegateTypeBo> getRoleDelegations(IdentityManagementRoleDocument identityManagementRoleDocument, List<DelegateTypeBo> origDelegations){
		List<DelegateTypeBo> kimDelegations = new ArrayList<DelegateTypeBo>();
		DelegateTypeBo newKimDelegation;
		DelegateTypeBo origDelegationImplTemp = null;
		List<DelegateMemberBo> origMembers;
        List<DelegateMemberBo> allOrigMembers = new ArrayList<DelegateMemberBo>();;
        boolean activatingInactive = false;
		String newDelegationIdAssigned = "";
            if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getDelegations())){
			for(RoleDocumentDelegation roleDocumentDelegation: identityManagementRoleDocument.getDelegations()){
				newKimDelegation = new DelegateTypeBo();
				KimCommonUtilsInternal.copyProperties(newKimDelegation, roleDocumentDelegation);
				newKimDelegation.setRoleId(identityManagementRoleDocument.getRoleId());
				if(ObjectUtils.isNotNull(origDelegations)){
					for(DelegateTypeBo origDelegationImpl: origDelegations){
						if(StringUtils.equals(origDelegationImpl.getRoleId(), newKimDelegation.getRoleId()) &&
								StringUtils.equals(origDelegationImpl.getDelegationId(), newKimDelegation.getDelegationId())){
							//TODO: verify if you want to add  && newRoleMember.isActive() condition to if...
							newDelegationIdAssigned = newKimDelegation.getDelegationId();
							newKimDelegation.setDelegationId(origDelegationImpl.getDelegationId());
							activatingInactive = true;
						}
						if(origDelegationImpl.getDelegationId()!=null && StringUtils.equals(origDelegationImpl.getDelegationId(), newKimDelegation.getDelegationId())){
							newKimDelegation.setVersionNumber(origDelegationImpl.getVersionNumber());
                            newKimDelegation.setObjectId(origDelegationImpl.getObjectId());
							origDelegationImplTemp = origDelegationImpl;
						}
                        for (DelegateMemberBo delegateMemberBo:origDelegationImpl.getMembers() ) {
                            allOrigMembers.add(delegateMemberBo);
                        }
                    }
				}
				origMembers = (origDelegationImplTemp == null || origDelegationImplTemp.getMembers()==null)?
									new ArrayList<DelegateMemberBo>():origDelegationImplTemp.getMembers();
				newKimDelegation.setMembers(getDelegationMembers(roleDocumentDelegation.getMembers(), origMembers, allOrigMembers, activatingInactive, newDelegationIdAssigned));
                kimDelegations.add(newKimDelegation);
				activatingInactive = false;
			}
		}
		return kimDelegations;
	}

	protected List<DelegateMemberBo> getDelegationMembers(List<RoleDocumentDelegationMember> delegationMembers,
			List<DelegateMemberBo> origDelegationMembers, List<DelegateMemberBo> allOrigMembers, boolean activatingInactive, String newDelegationIdAssigned){
		List<DelegateMemberBo> delegationsMembersList = new ArrayList<DelegateMemberBo>();
		DelegateMemberBo newDelegationMemberImpl;
		DelegateMemberBo origDelegationMemberImplTemp = null;
		List<DelegateMemberAttributeDataBo> origAttributes;
		String delegationMemberId = "";
		if(CollectionUtils.isNotEmpty(delegationMembers)){
			for(RoleDocumentDelegationMember delegationMember: delegationMembers){
				newDelegationMemberImpl = new DelegateMemberBo();
				KimCommonUtilsInternal.copyProperties(newDelegationMemberImpl, delegationMember);
                newDelegationMemberImpl.setType(MemberType.fromCode(delegationMember.getMemberTypeCode()));
				if(ObjectUtils.isNotNull(origDelegationMembers)){
					for(DelegateMemberBo origDelegationMember: origDelegationMembers){
						if(activatingInactive && StringUtils.equals(origDelegationMember.getMemberId(), newDelegationMemberImpl.getMemberId()) &&
								StringUtils.equals(newDelegationMemberImpl.getDelegationId(), newDelegationIdAssigned) &&
								!origDelegationMember.isActive(getDateTimeService().getCurrentTimestamp())){
							newDelegationMemberImpl.setDelegationId(origDelegationMember.getDelegationId());
							delegationMemberId = newDelegationMemberImpl.getDelegationMemberId();
							newDelegationMemberImpl.setDelegationMemberId(origDelegationMember.getDelegationMemberId());
						}
						if(origDelegationMember.getDelegationMemberId()!=null && StringUtils.equals(origDelegationMember.getDelegationMemberId(), newDelegationMemberImpl.getDelegationMemberId())){
							newDelegationMemberImpl.setVersionNumber(origDelegationMember.getVersionNumber());
                            newDelegationMemberImpl.setObjectId(origDelegationMember.getObjectId());
                            origDelegationMemberImplTemp = origDelegationMember;
						}
					}
				}
                if(ObjectUtils.isNotNull(allOrigMembers)){
                    for (DelegateMemberBo origMember : allOrigMembers) {
                        if ((origMember.getDelegationMemberId() != null) &&
                            (origMember.getDelegationMemberId().equals(delegationMember.getDelegationMemberId())) &&
                            (origMember.getRoleMemberId() != null) &&
                            (origMember.getRoleMemberId().equals(delegationMember.getRoleMemberId()))) {
                                newDelegationMemberImpl.setVersionNumber(origMember.getVersionNumber());
                                newDelegationMemberImpl.setObjectId(origMember.getObjectId());
                                origDelegationMemberImplTemp = origMember;
                        }
                    }
                }
                    origAttributes = (origDelegationMemberImplTemp==null || origDelegationMemberImplTemp.getAttributeDetails()==null)?
                            new ArrayList<DelegateMemberAttributeDataBo>():origDelegationMemberImplTemp.getAttributeDetails();
                    newDelegationMemberImpl.setAttributeDetails(getDelegationMemberAttributeData(delegationMember.getQualifiers(), origAttributes, activatingInactive, delegationMemberId));
                    newDelegationMemberImpl.setActiveFromDateValue(delegationMember.getActiveFromDate());
                    newDelegationMemberImpl.setActiveToDateValue(delegationMember.getActiveToDate());
                    newDelegationMemberImpl.setVersionNumber(null);
                    delegationsMembersList.add(newDelegationMemberImpl);
			}
		}
		return delegationsMembersList;
	}

	//TODO: implement logic same as role members - do not insert qualifiers with blank values
	protected List<DelegateMemberAttributeDataBo> getDelegationMemberAttributeData(
			List<RoleDocumentDelegationMemberQualifier> qualifiers, List<DelegateMemberAttributeDataBo> origAttributes,
			boolean activatingInactive, String delegationMemberId){
		List<DelegateMemberAttributeDataBo> delegationMemberAttributeDataList = new ArrayList<DelegateMemberAttributeDataBo>();
		DelegateMemberAttributeDataBo newDelegationMemberAttributeData;
		if(CollectionUtils.isNotEmpty(qualifiers)){
			for(RoleDocumentDelegationMemberQualifier memberRoleQualifier: qualifiers){
				if(StringUtils.isNotBlank(memberRoleQualifier.getAttrVal())){
					newDelegationMemberAttributeData = new DelegateMemberAttributeDataBo();
					newDelegationMemberAttributeData.setId(memberRoleQualifier.getAttrDataId());
					newDelegationMemberAttributeData.setAttributeValue(memberRoleQualifier.getAttrVal());
					newDelegationMemberAttributeData.setAssignedToId(memberRoleQualifier.getDelegationMemberId());
					newDelegationMemberAttributeData.setKimTypeId(memberRoleQualifier.getKimTypId());
					newDelegationMemberAttributeData.setKimAttributeId(memberRoleQualifier.getKimAttrDefnId());
					if(ObjectUtils.isNotNull(origAttributes)){
						for(DelegateMemberAttributeDataBo origAttribute: origAttributes){
							if(activatingInactive && StringUtils.equals(origAttribute.getKimAttributeId(), newDelegationMemberAttributeData.getKimAttributeId()) &&
									StringUtils.equals(newDelegationMemberAttributeData.getAssignedToId(), delegationMemberId)){
								newDelegationMemberAttributeData.setAssignedToId(origAttribute.getAssignedToId());
								newDelegationMemberAttributeData.setId(origAttribute.getId());
							}
							if(StringUtils.equals(origAttribute.getId(), newDelegationMemberAttributeData.getId())){
								newDelegationMemberAttributeData.setVersionNumber(origAttribute.getVersionNumber());
							}
						}
					}
					delegationMemberAttributeDataList.add(newDelegationMemberAttributeData);
				}
			}
		}
		return delegationMemberAttributeDataList;
	}

	/* Group document methods */
	@Override
    public void loadGroupDoc(IdentityManagementGroupDocument identityManagementGroupDocument, Group groupInfo){

		identityManagementGroupDocument.setGroupId(groupInfo.getId());
        KimType kimType = getKimTypeInfoService().getKimType(groupInfo.getKimTypeId());
		identityManagementGroupDocument.setKimType(kimType);
		identityManagementGroupDocument.setGroupTypeName(kimType.getName());
		identityManagementGroupDocument.setGroupTypeId(kimType.getId());
		identityManagementGroupDocument.setGroupName(groupInfo.getName());
		identityManagementGroupDocument.setGroupDescription(groupInfo.getDescription());
		identityManagementGroupDocument.setActive(groupInfo.isActive());
		identityManagementGroupDocument.setGroupNamespace(groupInfo.getNamespaceCode());

        List<GroupMember> members = new ArrayList<GroupMember>(getGroupService().getMembersOfGroup(groupInfo.getId()));
        identityManagementGroupDocument.setMembers(loadGroupMembers(identityManagementGroupDocument, members));



        identityManagementGroupDocument.setQualifiers(loadGroupQualifiers(identityManagementGroupDocument, groupInfo.getAttributes()));
		identityManagementGroupDocument.setEditing(true);
	}

	protected static class GroupMemberNameComparator implements Comparator<GroupDocumentMember> {
		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
        public int compare(GroupDocumentMember m1, GroupDocumentMember m2) {
			return m1.getMemberName().compareToIgnoreCase(m2.getMemberName());
		}
	}

	protected GroupMemberNameComparator groupMemberNameComparator = new GroupMemberNameComparator();

	protected List<GroupDocumentMember> loadGroupMembers(
			IdentityManagementGroupDocument identityManagementGroupDocument, List<GroupMember> members){
		List<GroupDocumentMember> pndMembers = new ArrayList<GroupDocumentMember>();
		GroupDocumentMember pndMember = new GroupDocumentMember();
		if(ObjectUtils.isNotNull(members)){
			for(GroupMember member: members){
				pndMember = new GroupDocumentMember();

				pndMember.setActiveFromDate(member.getActiveFromDate() == null ? null : new Timestamp(member.getActiveFromDate().getMillis()));
				pndMember.setActiveToDate(member.getActiveToDate() == null ? null : new Timestamp(member.getActiveToDate().getMillis()));
				//pndMember.setActive(member.isActive());
				if(pndMember.isActive()){
					pndMember.setGroupMemberId(member.getMemberId());
					pndMember.setGroupId(member.getGroupId());
					pndMember.setMemberId(member.getMemberId());
					pndMember.setMemberName(getMemberName(member.getType(), member.getMemberId()));
					pndMember.setMemberFullName(getMemberFullName(member.getType(), member.getMemberId()));
					pndMember.setMemberTypeCode(member.getType().getCode());
					pndMember.setEdit(true);
					pndMembers.add(pndMember);
				}
			}
		}
		Collections.sort(pndMembers, groupMemberNameComparator);
		return pndMembers;
	}

	protected List<GroupDocumentQualifier> loadGroupQualifiers(IdentityManagementGroupDocument IdentityManagementGroupDocument,
			Map<String, String> attributes){
		List<GroupDocumentQualifier> pndGroupQualifiers = new ArrayList<GroupDocumentQualifier>();
		GroupDocumentQualifier pndGroupQualifier = new GroupDocumentQualifier();
		List<KimAttributeField> origAttributes = IdentityManagementGroupDocument.getDefinitions();
		boolean attributePresent = false;
		String origAttributeId;
		if(origAttributes!=null){

			for(KimAttributeField key: origAttributes) {
				origAttributeId = IdentityManagementGroupDocument.getKimAttributeDefnId(key);
				if(!attributes.isEmpty()){

					for(GroupAttributeBo groupQualifier: KimAttributeDataBo.createFrom(GroupAttributeBo.class, attributes, IdentityManagementGroupDocument.getGroupTypeId())){
						if(origAttributeId!=null && ObjectUtils.isNotNull(groupQualifier.getKimAttribute()) &&
								StringUtils.equals(origAttributeId, groupQualifier.getKimAttribute().getId())){
							pndGroupQualifier = new GroupDocumentQualifier();
							KimCommonUtilsInternal.copyProperties(pndGroupQualifier, groupQualifier);
							pndGroupQualifier.setAttrDataId(groupQualifier.getId());
							pndGroupQualifier.setAttrVal(groupQualifier.getAttributeValue());
							pndGroupQualifier.setKimAttrDefnId(groupQualifier.getKimAttribute().getId());
							pndGroupQualifier.setKimTypId(groupQualifier.getKimType().getId());
							pndGroupQualifier.setGroupId(groupQualifier.getAssignedToId());
							pndGroupQualifiers.add(pndGroupQualifier);
							attributePresent = true;
						}
					}
				}
				if(!attributePresent){
					pndGroupQualifier = new GroupDocumentQualifier();
					pndGroupQualifier.setKimAttrDefnId(origAttributeId);
					pndGroupQualifiers.add(pndGroupQualifier);
				}
				attributePresent = false;
			}
		}
		return pndGroupQualifiers;
	}

	/**
	 * @see org.kuali.rice.kim.service.UiDocumentService#saveEntityPerson(IdentityManagementPersonDocument)
	 */
	@Override
    public void saveGroup(IdentityManagementGroupDocument identityManagementGroupDocument) {
		GroupBo kimGroup = new GroupBo();
		String groupId = identityManagementGroupDocument.getGroupId();
		GroupBo origGroup = getDataObjectService().find(GroupBo.class, groupId);
		List<GroupMemberBo> origGroupMembers = new ArrayList<GroupMemberBo>();
		if (ObjectUtils.isNull(origGroup)) {
			origGroup = new GroupBo();
			kimGroup.setActive(true);
		} else {
			kimGroup.setVersionNumber(origGroup.getVersionNumber());
			//TODO: when a group is inactivated, inactivate the memberships of principals in that group
			//and the memberships of that group in roles
			kimGroup.setActive(identityManagementGroupDocument.isActive());
			origGroupMembers = getDataObjectService().findMatching(GroupMemberBo.class,
			        QueryByCriteria.Builder.forAttribute(KimConstants.PrimaryKeyConstants.GROUP_ID, groupId).build()).getResults();
		}

		kimGroup.setId(identityManagementGroupDocument.getGroupId());
		KimType kimType = getKimTypeInfoService().getKimType(identityManagementGroupDocument.getGroupTypeId());
		if( kimType == null ) {
			throw new RuntimeException("Kim type not found for:"+identityManagementGroupDocument.getGroupTypeId());
		}

		kimGroup.setKimTypeId(kimType.getId());
		kimGroup.setNamespaceCode(identityManagementGroupDocument.getGroupNamespace());
		kimGroup.setName(identityManagementGroupDocument.getGroupName());
		kimGroup.setDescription(identityManagementGroupDocument.getGroupDescription());
		kimGroup.setAttributeDetails(getGroupAttributeData(identityManagementGroupDocument, origGroup.getAttributeDetails()));

		List<GroupMemberBo> newGroupMembersList = getGroupMembers(identityManagementGroupDocument, origGroupMembers);
		kimGroup.setMembers(newGroupMembersList);  // add the new, complete list to the group

        List<String> oldIds;
        List<String> newIds;
        oldIds = getGroupService().getMemberPrincipalIds(kimGroup.getId()); // for the actionList update

        kimGroup = getDataObjectService().save(kimGroup);

		newIds = kimGroup.getMemberPrincipalIds();
		//newIds = getGroupService().getMemberPrincipalIds(kimGroup.getGroupId()); // for the action list update

		// Do an async update of the action list for the updated groups
		org.kuali.rice.kim.service.KIMServiceLocatorInternal.getGroupInternalService().updateForWorkgroupChange(kimGroup.getId(), oldIds, newIds);
		if(!kimGroup.isActive()){
			// when a group is inactivated, inactivate the memberships of principals in that group
			// and the memberships of that group in roles
			KimImplServiceLocator.getRoleInternalService().groupInactivated(identityManagementGroupDocument.getGroupId());
		}
	}

	protected List<GroupMemberBo> getGroupMembers(IdentityManagementGroupDocument identityManagementGroupDocument, List<GroupMemberBo> origGroupMembers){
		List<GroupMemberBo> groupMembers = new ArrayList<GroupMemberBo>();
		GroupMemberBo newGroupMember;
		if(CollectionUtils.isNotEmpty(identityManagementGroupDocument.getMembers())){
			for(GroupDocumentMember documentGroupMember: identityManagementGroupDocument.getMembers()){
				newGroupMember = new GroupMemberBo();
				//KimCommonUtilsInternalInternal.copyProperties(newGroupMember, documentGroupMember);
                //copy properties manually for now until new BO created for DocumentGroupMember

				newGroupMember.setGroupId(identityManagementGroupDocument.getGroupId());
                newGroupMember.setActiveFromDateValue(documentGroupMember.getActiveFromDate());
                newGroupMember.setActiveToDateValue(documentGroupMember.getActiveToDate());
                newGroupMember.setMemberId(documentGroupMember.getMemberId());
                newGroupMember.setTypeCode(documentGroupMember.getMemberTypeCode());
				if(ObjectUtils.isNotNull(origGroupMembers)){
					for(GroupMemberBo origGroupMemberImpl: origGroupMembers){
						if(StringUtils.equals(origGroupMemberImpl.getGroupId(), newGroupMember.getGroupId()) &&
								StringUtils.equals(origGroupMemberImpl.getMemberId(), newGroupMember.getMemberId()) &&
								!origGroupMemberImpl.isActive(getDateTimeService().getCurrentTimestamp())){
							//TODO: verify if you want to add  && newGroupMember.isActive() condition to if...
							newGroupMember.setMemberId(origGroupMemberImpl.getMemberId());
						}
                        if(StringUtils.equals(origGroupMemberImpl.getGroupId(), newGroupMember.getGroupId()) &&
								StringUtils.equals(origGroupMemberImpl.getMemberId(), newGroupMember.getMemberId()) &&
								origGroupMemberImpl.isActive(getDateTimeService().getCurrentTimestamp())){
							newGroupMember.setId(origGroupMemberImpl.getId());
                            newGroupMember.setVersionNumber(origGroupMemberImpl.getVersionNumber());
						}
					}
				}
				groupMembers.add(newGroupMember);
			}
		}
		return groupMembers;
	}

	protected List<GroupAttributeBo> getGroupAttributeData(IdentityManagementGroupDocument identityManagementGroupDocument,
			List<GroupAttributeBo> origAttributes){
		List<GroupAttributeBo> groupAttributeDataList = new ArrayList<GroupAttributeBo>();
		GroupAttributeBo newGroupAttributeData;
		if(CollectionUtils.isNotEmpty(identityManagementGroupDocument.getQualifiers())){
			for(GroupDocumentQualifier groupQualifier: identityManagementGroupDocument.getQualifiers()){
				if(StringUtils.isNotBlank(groupQualifier.getAttrVal())){
					newGroupAttributeData = new GroupAttributeBo();
					newGroupAttributeData.setId(groupQualifier.getAttrDataId());
					newGroupAttributeData.setAttributeValue(groupQualifier.getAttrVal());
					newGroupAttributeData.setAssignedToId(groupQualifier.getGroupId());
					newGroupAttributeData.setKimTypeId(groupQualifier.getKimTypId());
					newGroupAttributeData.setKimAttributeId(groupQualifier.getKimAttrDefnId());
					if(ObjectUtils.isNotNull(origAttributes)){
						for(GroupAttributeBo origAttribute: origAttributes){
							if(StringUtils.equals(origAttribute.getKimAttributeId(), newGroupAttributeData.getKimAttributeId()) &&
									StringUtils.equals(newGroupAttributeData.getAssignedToId(), origAttribute.getAssignedToId())){
							    newGroupAttributeData.setId(origAttribute.getId());
							}
							if(origAttribute.getId()!=null && StringUtils.equals(origAttribute.getId(), newGroupAttributeData.getId())){
							    newGroupAttributeData.setVersionNumber(origAttribute.getVersionNumber());
							}
						}
					}
					groupAttributeDataList.add(newGroupAttributeData);
				}
			}
		}
		return groupAttributeDataList;
	}

    protected Set<String> getChangedRoleResponsibilityIds(
			IdentityManagementRoleDocument identityManagementRoleDocument, List<RoleResponsibilityBo> origRoleResponsibilities){
		Set<String> lRet = new HashSet<String>();
		List<String> newResp = new ArrayList<String>();
		List<String> oldResp = new ArrayList<String>();
		if(CollectionUtils.isNotEmpty(identityManagementRoleDocument.getResponsibilities())){
			for(KimDocumentRoleResponsibility documentRoleResponsibility: identityManagementRoleDocument.getResponsibilities()){
				newResp.add(documentRoleResponsibility.getResponsibilityId());
			}
		}
		if(ObjectUtils.isNotNull(origRoleResponsibilities)){
			for(RoleResponsibilityBo roleRespBo: origRoleResponsibilities){
				oldResp.add(roleRespBo.getResponsibilityId());
			}
		}
		lRet.addAll(newResp);
		lRet.addAll(oldResp);

		return lRet;
	}

	public KimTypeInfoService getKimTypeInfoService() {
		if ( kimTypeInfoService == null ) {
			kimTypeInfoService = KimApiServiceLocator.getKimTypeInfoService();
		}
		return kimTypeInfoService;
	}

    @Override
    public List<KimDocumentRoleMember> getRoleMembers(Map<String,String> fieldValues) {
        List<KimDocumentRoleMember> matchingRoleMembers = new ArrayList<KimDocumentRoleMember>();
        //Remove since they are KNS fieldValues and not BO
        fieldValues.remove(KRADConstants.BACK_LOCATION);
        fieldValues.remove(KRADConstants.DOC_FORM_KEY);
        fieldValues.remove(KRADConstants.DOC_NUM);



		List<RoleMember> matchingRoleMembersTemp = getRoleService().findRoleMembers(toQuery(fieldValues)).getResults();
		KimDocumentRoleMember matchingRoleMember;
		Object roleMemberObject;
		RoleMemberBo roleMemberBo;
		if(CollectionUtils.isNotEmpty(matchingRoleMembersTemp)){
			for(RoleMember roleMember: matchingRoleMembersTemp){
				roleMemberBo = getRoleMember(roleMember.getId());
				roleMemberObject = getMember(roleMemberBo.getType(), roleMemberBo.getMemberId());
				matchingRoleMember = new KimDocumentRoleMember();
                KimDocumentRoleMember.copyProperties(matchingRoleMember, roleMemberBo);
                matchingRoleMember.setMemberId(roleMemberBo.getMemberId());
                matchingRoleMember.setRoleMemberId(roleMemberBo.getId());
				matchingRoleMember.setMemberName(getMemberName(roleMemberBo.getType(), roleMemberObject));
				matchingRoleMember.setMemberNamespaceCode(getMemberNamespaceCode(roleMemberBo.getType(), roleMemberObject));
				matchingRoleMember.setQualifiers(getQualifiers(roleMemberBo.getAttributeDetails()));
				matchingRoleMembers.add(matchingRoleMember);
			}
		}
		return matchingRoleMembers;
    }

   private QueryByCriteria toQuery(Map<String,String> fieldValues) {
       String memberTypeCode = fieldValues.get(KIMPropertyConstants.KimMember.MEMBER_TYPE_CODE);
       String memberName = fieldValues.get(KimConstants.KimUIConstants.MEMBER_NAME);
       String memberNamespaceCode = fieldValues.get(KimConstants.KimUIConstants.MEMBER_NAMESPACE_CODE);

       if(StringUtils.isNotEmpty(memberName) || StringUtils.isNotEmpty(memberNamespaceCode)) {
            String memberId  = getMemberIdByName(MemberType.fromCode(memberTypeCode),memberNamespaceCode,memberName)  ;
           if(StringUtils.isNotEmpty(memberId)) {
                  fieldValues.put(KIMPropertyConstants.KimMember.MEMBER_ID, memberId);
           }
       }

       List<Predicate> pred = new ArrayList<Predicate>();

       pred.add(PredicateUtils.convertMapToPredicate(fieldValues));
       Predicate[] predicates = new Predicate[0];
       predicates = pred.toArray(predicates)  ;
        return QueryByCriteria.Builder.fromPredicates(predicates);
    }

    private List<KimDocumentRoleQualifier> getQualifiers(List<RoleMemberAttributeDataBo> attributes){
    	if (attributes==null) {return null;}
    	List<KimDocumentRoleQualifier> qualifiers = new ArrayList<KimDocumentRoleQualifier>();
    	KimDocumentRoleQualifier qualifier;
    	if(ObjectUtils.isNotNull(attributes)){
	    	for(RoleMemberAttributeDataBo attribute: attributes){
		    	qualifier = new KimDocumentRoleQualifier();
				qualifier.setAttrDataId(attribute.getId());
				qualifier.setAttrVal(attribute.getAttributeValue());
				qualifier.setRoleMemberId(attribute.getAssignedToId());
				qualifier.setKimTypId(attribute.getKimTypeId());
				qualifier.setKimAttrDefnId(attribute.getKimAttributeId());
				qualifier.setKimAttribute(attribute.getKimAttribute());
				qualifiers.add(qualifier);
	    	}
    	}
    	return qualifiers;
    }

	public ResponsibilityInternalService getResponsibilityInternalService() {
		if ( responsibilityInternalService == null ) {
				responsibilityInternalService = KimImplServiceLocator.getResponsibilityInternalService();
		}
		return responsibilityInternalService;
	}

   public PermissionService getPermissionService() {
		if ( permissionService == null ) {
				permissionService = KimApiServiceLocator.getPermissionService();
		}
		return permissionService;
	}

    public ParameterService getParameterService() {
    	if ( parameterService == null ) {
    		parameterService = CoreFrameworkServiceLocator.getParameterService();
    	}
    	return parameterService;
    }

    public DateTimeService getDateTimeService() {
        if ( dateTimeService == null ) {
            dateTimeService = CoreApiServiceLocator.getDateTimeService();
        }
        return this.dateTimeService;
    }

    public void setParameterService(ParameterService parameterService) {
    	this.parameterService = parameterService;
    }

    public static IdentityArchiveService getIdentityArchiveService() {
        return GlobalResourceLoader.getService(KIM_IDENTITY_ARCHIVE_SERVICE);
    }
}
