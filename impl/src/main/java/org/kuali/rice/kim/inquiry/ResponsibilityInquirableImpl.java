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
 * See the License for the specific language governing responsibilitys and
 * limitations under the License.
 */
package org.kuali.rice.kim.inquiry;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.rice.core.impl.namespace.NamespaceBo;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.impl.ResponsibilityImpl;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.ResponsibilityAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.lookup.RoleLookupableHelperServiceImpl;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.HtmlData.MultipleAnchorHtmlData;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.uif.widget.Inquiry;
import org.kuali.rice.kns.util.ObjectUtils;

import java.util.*;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ResponsibilityInquirableImpl extends RoleMemberInquirableImpl {

	protected final String KIM_RESPONSIBILITY_REQUIRED_ATTRIBUTE_ID = "kimResponsibilityRequiredAttributeId";
	protected final String RESPONSIBILITY_ID = "responsibilityId";
	transient private static ResponsibilityService responsibilityService;

	@Override
	public void buildInquirableLink(Object dataObject, String propertyName, Inquiry inquiry){
		
		if(NAME.equals(propertyName) || NAME_TO_DISPLAY.equals(propertyName)){
			Map<String, String> primaryKeys = new HashMap<String, String>();
			primaryKeys.put(RESPONSIBILITY_ID, RESPONSIBILITY_ID);
			inquiry.buildInquiryLink(dataObject, propertyName, ResponsibilityImpl.class, primaryKeys);
    	} else if(NAMESPACE_CODE.equals(propertyName) || TEMPLATE_NAMESPACE_CODE.equals(propertyName)){
    		Map<String, String> primaryKeys = new HashMap<String, String>();
			primaryKeys.put(propertyName, "code");
			inquiry.buildInquiryLink(dataObject, propertyName, NamespaceBo.class, primaryKeys);
        } else if(DETAIL_OBJECTS.equals(propertyName)){
        	//return getAttributesInquiryUrl(businessObject, DETAIL_OBJECTS);
        	super.buildInquirableLink(dataObject, propertyName, inquiry);
        } else if(ASSIGNED_TO_ROLES.equals(propertyName)){
//        	return getAssignedRoleInquiryUrl(dataObject);
        	super.buildInquirableLink(dataObject, propertyName, inquiry);
        }else{
        	super.buildInquirableLink(dataObject, propertyName, inquiry);	
        }
	}
	
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
    	/*
    	 *  - responsibility detail values (attribute name and value separated by colon, commas between attributes)
		 *	- required role qualifiers (attribute name and value separated by colon, commas between attributes)
		 *	- list of roles assigned: role type, role namespace, role name
    	 */
    	if(NAME.equals(attributeName) || NAME_TO_DISPLAY.equals(attributeName)){
			List<String> primaryKeys = new ArrayList<String>();
			primaryKeys.add(RESPONSIBILITY_ID);
			return getInquiryUrlForPrimaryKeys(ResponsibilityImpl.class, businessObject, primaryKeys, null);
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

    @SuppressWarnings("unchecked")
	protected HtmlData getAttributesInquiryUrl(BusinessObject businessObject, String attributeName){
    	List<ResponsibilityAttributeDataImpl> responsibilityAttributeData = 
    		(List<ResponsibilityAttributeDataImpl>)ObjectUtils.getPropertyValue(businessObject, attributeName);
    	List<AnchorHtmlData> htmlData = new ArrayList<AnchorHtmlData>();
		List<String> primaryKeys = new ArrayList<String>();
		primaryKeys.add(ATTRIBUTE_DATA_ID);
    	for(ResponsibilityAttributeDataImpl responsibilityAttributeDataImpl: responsibilityAttributeData){
    		htmlData.add(getInquiryUrlForPrimaryKeys(ResponsibilityAttributeDataImpl.class, responsibilityAttributeDataImpl, primaryKeys, 
    			getKimAttributeLabelFromDD(responsibilityAttributeDataImpl.getKimAttribute().getAttributeName())+KimConstants.KimUIConstants.NAME_VALUE_SEPARATOR+
    			responsibilityAttributeDataImpl.getAttributeValue()));
    	}
    	return new MultipleAnchorHtmlData(htmlData);
    }

    @SuppressWarnings("unchecked")
	protected HtmlData getAssignedRoleInquiryUrl(BusinessObject businessObject){
    	ResponsibilityImpl responsibility = (ResponsibilityImpl)businessObject;
    	List<RoleImpl> assignedToRoles = responsibility.getAssignedToRoles();
    	List<AnchorHtmlData> htmlData = new ArrayList<AnchorHtmlData>();
		if(assignedToRoles!=null && !assignedToRoles.isEmpty()){
			List<String> primaryKeys = Collections.singletonList(ROLE_ID);
			RoleService roleService = KimApiServiceLocator.getRoleService();
			for(RoleImpl roleImpl: assignedToRoles){
				KimRoleInfo roleInfo = roleService.getRole(roleImpl.getRoleId());
				AnchorHtmlData inquiryHtmlData = getInquiryUrlForPrimaryKeys(RoleImpl.class, roleInfo, primaryKeys, 
        				roleInfo.getNamespaceCode()+" "+
        				roleInfo.getRoleName());
				inquiryHtmlData.setHref(RoleLookupableHelperServiceImpl.getCustomRoleInquiryHref(inquiryHtmlData.getHref()));
        		htmlData.add(inquiryHtmlData);
        	}
		}
    	return new MultipleAnchorHtmlData(htmlData);
    }

    @Override
	public Object getDataObject(Map fieldValues){
    	return getBusinessObject(fieldValues);
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
		criteria.put("responsibilityId", fieldValues.get("responsibilityId").toString());
		KimResponsibilityImpl responsibilityImpl = (KimResponsibilityImpl) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimResponsibilityImpl.class, criteria);
		return getResponsibilitiesSearchResultsCopy(responsibilityImpl);
	}

	public ResponsibilityService getResponsibilityService() {
		if (responsibilityService == null ) {
			responsibilityService = KimApiServiceLocator.getResponsibilityService();
		}
		return responsibilityService;
	}
	
	@SuppressWarnings("unchecked")
	private ResponsibilityImpl getResponsibilitiesSearchResultsCopy(KimResponsibilityImpl responsibilitySearchResult){
		ResponsibilityImpl responsibilitySearchResultCopy = new ResponsibilityImpl();
		try{
			PropertyUtils.copyProperties(responsibilitySearchResultCopy, responsibilitySearchResult);
		} catch(Exception ex){
			//TODO: remove this
			ex.printStackTrace();
		}
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put("responsibilityId", responsibilitySearchResultCopy.getResponsibilityId());
		List<RoleResponsibilityImpl> roleResponsibilitys = 
			(List<RoleResponsibilityImpl>) KNSServiceLocator.getBusinessObjectService().findMatching(RoleResponsibilityImpl.class, criteria);
		List<RoleImpl> assignedToRoles = new ArrayList<RoleImpl>();
		for(RoleResponsibilityImpl roleResponsibilityImpl: roleResponsibilitys){
			assignedToRoles.add(getRoleImpl(roleResponsibilityImpl.getRoleId()));
		}
		responsibilitySearchResultCopy.setAssignedToRoles(assignedToRoles);
		return responsibilitySearchResultCopy;
	}

}
