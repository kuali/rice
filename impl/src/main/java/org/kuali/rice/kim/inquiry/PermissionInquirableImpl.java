/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.inquiry;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.rice.core.impl.namespace.NamespaceBo;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.bo.impl.PermissionImpl;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.role.impl.PermissionAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RolePermissionImpl;
import org.kuali.rice.kim.lookup.RoleLookupableHelperServiceImpl;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.HtmlData.MultipleAnchorHtmlData;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PermissionInquirableImpl extends RoleMemberInquirableImpl {

	transient private static PermissionService permissionService;

    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
    	/*
    	 *  - permission detail values (attribute name and value separated by colon, commas between attributes)
		 *	- required role qualifiers (attribute name and value separated by colon, commas between attributes)
		 *	- list of roles assigned: role type, role namespace, role name
    	 */
		if(NAME.equals(attributeName) || NAME_TO_DISPLAY.equals(attributeName)){
			List<String> primaryKeys = new ArrayList<String>();
			primaryKeys.add(KimConstants.PrimaryKeyConstants.PERMISSION_ID);
			return getInquiryUrlForPrimaryKeys(PermissionImpl.class, businessObject, primaryKeys, null);
		} else if(NAMESPACE_CODE.equals(attributeName) || TEMPLATE_NAMESPACE_CODE.equals(attributeName)){
			List<String> primaryKeys = new ArrayList<String>();
			primaryKeys.add("code");
			NamespaceBo parameterNamespace = new NamespaceBo();
			parameterNamespace.setCode((String)ObjectUtils.getPropertyValue(businessObject, attributeName));
			return getInquiryUrlForPrimaryKeys(NamespaceBo.class, parameterNamespace, primaryKeys, null);
        } else if(DETAIL_OBJECTS.equals(attributeName)){
        	//return getAttributesInquiryUrl(businessObject, DETAIL_OBJECTS);
        } else if(ASSIGNED_TO_ROLES.equals(attributeName)){
        	return getAssignedRoleInquiryUrl(businessObject);
        }
		
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

    protected HtmlData getAttributesInquiryUrl(BusinessObject businessObject, String attributeName){
    	List<PermissionAttributeDataImpl> permissionAttributeData = 
    		(List<PermissionAttributeDataImpl>)ObjectUtils.getPropertyValue(businessObject, attributeName);
    	List<AnchorHtmlData> htmlData = new ArrayList<AnchorHtmlData>();
		List<String> primaryKeys = new ArrayList<String>();
		primaryKeys.add(ATTRIBUTE_DATA_ID);
    	for(PermissionAttributeDataImpl permissionAttributeDataImpl: permissionAttributeData){
    		htmlData.add(getInquiryUrlForPrimaryKeys(PermissionAttributeDataImpl.class, permissionAttributeDataImpl, primaryKeys, 
    			getKimAttributeLabelFromDD(permissionAttributeDataImpl.getKimAttribute().getAttributeName())+
    			KimConstants.KimUIConstants.NAME_VALUE_SEPARATOR+
    			permissionAttributeDataImpl.getAttributeValue()));
    	}
    	return new MultipleAnchorHtmlData(htmlData);
    }

    protected HtmlData getAssignedRoleInquiryUrl(BusinessObject businessObject){
    	PermissionImpl permission = (PermissionImpl)businessObject;
    	List<RoleImpl> assignedToRoles = permission.getAssignedToRoles();
    	List<AnchorHtmlData> htmlData = new ArrayList<AnchorHtmlData>();
		List<String> primaryKeys = new ArrayList<String>();
		primaryKeys.add(ROLE_ID);
		if(assignedToRoles!=null && !assignedToRoles.isEmpty()){
			RoleService roleService = KIMServiceLocator.getRoleService();
			KimRoleInfo roleInfo;
			AnchorHtmlData inquiryHtmlData;
			for(RoleImpl roleImpl: assignedToRoles){
				roleInfo = roleService.getRole(roleImpl.getRoleId());
				inquiryHtmlData = getInquiryUrlForPrimaryKeys(RoleImpl.class, roleInfo, primaryKeys, 
        				roleInfo.getNamespaceCode()+" "+
        				roleInfo.getRoleName());
				inquiryHtmlData.setHref(RoleLookupableHelperServiceImpl.getCustomRoleInquiryHref(inquiryHtmlData.getHref()));
				inquiryHtmlData.setTarget(AnchorHtmlData.TARGET_BLANK);
        		htmlData.add(inquiryHtmlData);
        	}
		}
    	return new MultipleAnchorHtmlData(htmlData);
    }


	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BusinessObject getBusinessObject(Map fieldValues) {
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put("permissionId", fieldValues.get("permissionId").toString());
		KimPermissionImpl permissionImpl = (KimPermissionImpl) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimPermissionImpl.class, criteria);
		return getPermissionsSearchResultsCopy(permissionImpl);
	}

	public PermissionService getPermissionService() {
		if (permissionService == null ) {
			permissionService = KIMServiceLocator.getPermissionService();
		}
		return permissionService;
	}
	
	private PermissionImpl getPermissionsSearchResultsCopy(KimPermissionImpl permissionSearchResult){
		PermissionImpl permissionSearchResultCopy = new PermissionImpl();
		try{
			PropertyUtils.copyProperties(permissionSearchResultCopy, permissionSearchResult);
		} catch(Exception ex){
			//TODO: remove this
			ex.printStackTrace();
		}
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put("permissionId", permissionSearchResultCopy.getPermissionId());
		List<RolePermissionImpl> rolePermissions = 
			(List<RolePermissionImpl>) KNSServiceLocator.getBusinessObjectService().findMatching(RolePermissionImpl.class, criteria);
		List<RoleImpl> assignedToRoles = new ArrayList<RoleImpl>();
		for(RolePermissionImpl rolePermissionImpl: rolePermissions){
			assignedToRoles.add(getRoleImpl(rolePermissionImpl.getRoleId()));
		}
		permissionSearchResultCopy.setAssignedToRoles(assignedToRoles);
		return permissionSearchResultCopy;
	}

}
