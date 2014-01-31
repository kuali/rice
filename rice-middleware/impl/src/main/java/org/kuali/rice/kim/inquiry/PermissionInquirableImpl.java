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
package org.kuali.rice.kim.inquiry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.coreservice.impl.namespace.NamespaceBo;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.permission.PermissionAttributeBo;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.kim.impl.permission.UberPermissionBo;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.role.RolePermissionBo;
import org.kuali.rice.kim.lookup.RoleLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.uif.widget.Inquiry;
import org.kuali.rice.krad.util.KRADPropertyConstants;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@SuppressWarnings({"unchecked", "deprecation"})
public class PermissionInquirableImpl extends KimInquirableImpl {
    private static final long serialVersionUID = 1L;

	@Override
	public void buildInquirableLink(Object dataObject, String propertyName, Inquiry inquiry){

		if(NAME.equals(propertyName) || NAME_TO_DISPLAY.equals(propertyName)){
			Map<String, String> primaryKeys = new HashMap<String, String>();
			primaryKeys.put(KimConstants.PrimaryKeyConstants.PERMISSION_ID, KimConstants.PrimaryKeyConstants.PERMISSION_ID);
			inquiry.buildInquiryLink(dataObject, propertyName, UberPermissionBo.class, primaryKeys);
		} else if(NAMESPACE_CODE.equals(propertyName) || TEMPLATE_NAMESPACE_CODE.equals(propertyName)){
			Map<String, String> primaryKeys = new HashMap<String, String>();
			primaryKeys.put(propertyName, "code");
			inquiry.buildInquiryLink(dataObject, propertyName, NamespaceBo.class, primaryKeys);
        } else if(DETAIL_OBJECTS.equals(propertyName)){
        	super.buildInquirableLink(dataObject, propertyName, inquiry);
        } else if(ASSIGNED_TO_ROLES.equals(propertyName)){
        	super.buildInquirableLink(dataObject, propertyName, inquiry);
        }else{
        	super.buildInquirableLink(dataObject, propertyName, inquiry);
        }
	}

    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
    	/*
    	 *  - permission detail values (attribute name and value separated by colon, commas between attributes)
		 *	- required role qualifiers (attribute name and value separated by colon, commas between attributes)
		 *	- list of roles assigned: role type, role namespace, role name
    	 */
		if(NAME.equals(attributeName) || NAME_TO_DISPLAY.equals(attributeName)){
			return getInquiryUrlForPrimaryKeys(UberPermissionBo.class, businessObject, Collections.singletonList(KimConstants.PrimaryKeyConstants.PERMISSION_ID), null);
		} else if(NAMESPACE_CODE.equals(attributeName) || TEMPLATE_NAMESPACE_CODE.equals(attributeName)){
			NamespaceBo parameterNamespace = new NamespaceBo();
            String code = (String) KradDataServiceLocator.getDataObjectService().wrap(businessObject).getPropertyValueNullSafe(attributeName);
			parameterNamespace.setCode(code);
			return getInquiryUrlForPrimaryKeys(NamespaceBo.class, parameterNamespace, Collections.singletonList(KRADPropertyConstants.CODE), null);
        } else if(DETAIL_OBJECTS.equals(attributeName)){
        	//return getAttributesInquiryUrl(businessObject, DETAIL_OBJECTS);
        } else if(ASSIGNED_TO_ROLES.equals(attributeName)){
        	return getAssignedRoleInquiryUrl(businessObject);
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

    protected HtmlData getAttributesInquiryUrl(BusinessObject businessObject, String attributeName){
        DataObjectWrapper<BusinessObject> wrapper = KradDataServiceLocator.getDataObjectService().wrap(businessObject);
        List<PermissionAttributeBo> permissionAttributeData =
    		(List<PermissionAttributeBo>) wrapper.getPropertyValueNullSafe(attributeName);
    	List<HtmlData.AnchorHtmlData> htmlData = new ArrayList<HtmlData.AnchorHtmlData>();
		List<String> primaryKeys = new ArrayList<String>();
		primaryKeys.add(ATTRIBUTE_DATA_ID);
    	for(PermissionAttributeBo permissionAttributeDataImpl: permissionAttributeData){
    		htmlData.add(getInquiryUrlForPrimaryKeys(PermissionAttributeBo.class, permissionAttributeDataImpl, primaryKeys,
    			getKimAttributeLabelFromDD(permissionAttributeDataImpl.getKimAttribute().getAttributeName())+
    			KimConstants.KimUIConstants.NAME_VALUE_SEPARATOR+
    			permissionAttributeDataImpl.getAttributeValue()));
    	}
    	return new HtmlData.MultipleAnchorHtmlData(htmlData);
    }

    protected HtmlData getAssignedRoleInquiryUrl(BusinessObject businessObject){
    	UberPermissionBo permission = (UberPermissionBo)businessObject;
    	List<RoleBo> assignedToRoles = permission.getAssignedToRoles();
    	List<HtmlData.AnchorHtmlData> htmlData = new ArrayList<HtmlData.AnchorHtmlData>();
		if(assignedToRoles!=null && !assignedToRoles.isEmpty()){
			RoleService roleService = KimApiServiceLocator.getRoleService();
			for(RoleBo roleImpl: assignedToRoles){
				Role roleInfo = roleService.getRole(roleImpl.getId());
				HtmlData.AnchorHtmlData inquiryHtmlData = getInquiryUrlForPrimaryKeys(RoleBo.class, roleInfo, Collections.singletonList(ID),
        				roleInfo.getNamespaceCode()+ " " + roleInfo.getName());
				inquiryHtmlData.setHref(RoleLookupableHelperServiceImpl.getCustomRoleInquiryHref(inquiryHtmlData.getHref()));
				inquiryHtmlData.setTarget(HtmlData.AnchorHtmlData.TARGET_BLANK);
        		htmlData.add(inquiryHtmlData);
        	}
		}
    	return new HtmlData.MultipleAnchorHtmlData(htmlData);
    }

	@Override
	public Object retrieveDataObject(@SuppressWarnings("rawtypes") Map fieldValues){
        if ( fieldValues.get(ID) == null ) {
            return null;
        }
        PermissionBo permissionBo = getDataObjectService().find(PermissionBo.class, fieldValues.get(ID).toString());
        return getPermissionsSearchResultsCopy(permissionBo);
    }

	private PermissionBo getPermissionsSearchResultsCopy(PermissionBo permissionSearchResult){
		UberPermissionBo permissionSearchResultCopy = new UberPermissionBo();
		try{
			PropertyUtils.copyProperties(permissionSearchResultCopy, permissionSearchResult);
		} catch(Exception ex){
			throw new RuntimeException(ex);
		}
		List<RolePermissionBo> rolePermissions = getDataObjectService().findMatching(RolePermissionBo.class, QueryByCriteria.Builder.forAttribute("permissionId", permissionSearchResultCopy.getId()).build() ).getResults();
		List<RoleBo> assignedToRoles = new ArrayList<RoleBo>();
		for(RolePermissionBo rolePermissionImpl: rolePermissions){
			assignedToRoles.add( getDataObjectService().find(RoleBo.class, rolePermissionImpl.getRoleId()) );
		}
		permissionSearchResultCopy.setAssignedToRoles(assignedToRoles);
		return permissionSearchResultCopy;
	}

}
