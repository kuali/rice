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
 * See the License for the specific language governing responsibilitys and
 * limitations under the License.
 */
package org.kuali.rice.kim.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.rice.kim.bo.impl.ResponsibilityImpl;
import org.kuali.rice.kim.bo.impl.ResponsibilityImpl;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityRequiredAttributeImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.ResponsibilityAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.support.KimTypeInternalService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.ParameterNamespace;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.HtmlData.MultipleAnchorHtmlData;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ResponsibilityInquirableImpl extends RoleMemberInquirableImpl {

	protected final String KIM_RESPONSIBILITY_REQUIRED_ATTRIBUTE_ID = "kimResponsibilityRequiredAttributeId";
	protected final String RESPONSIBILITY_ID = "responsibilityId";
	transient private static ResponsibilityService responsibilityService;
	
    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
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
			ParameterNamespace parameterNamespace = new ParameterNamespace();
			parameterNamespace.setCode((String)ObjectUtils.getPropertyValue(businessObject, attributeName));
			return getInquiryUrlForPrimaryKeys(ParameterNamespace.class, parameterNamespace, primaryKeys, null);
        } else if(DETAIL_OBJECTS.equals(attributeName)){
        	//return getAttributesInquiryUrl(businessObject, DETAIL_OBJECTS);
        } else if(REQUIRED_ROLE_QUALIFIER_ATTRIBUTES.equals(attributeName)){
        	//return getRequiredRoleQualifierAttributesInquiryUrl(businessObject, REQUIRED_ROLE_QUALIFIER_ATTRIBUTES);
        } else if(ASSIGNED_TO_ROLES.equals(attributeName)){
        	return getAssignedRoleInquiryUrl(businessObject);
        }
		
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

    protected HtmlData getRequiredRoleQualifierAttributesInquiryUrl(BusinessObject businessObject, String attributeName){
    	List<KimResponsibilityRequiredAttributeImpl> requiredAttributeData = 
    		(List<KimResponsibilityRequiredAttributeImpl>)ObjectUtils.getPropertyValue(businessObject, attributeName);
    	List<AnchorHtmlData> htmlData = new ArrayList<AnchorHtmlData>();
		List<String> primaryKeys = new ArrayList<String>();
		primaryKeys.add(KIM_RESPONSIBILITY_REQUIRED_ATTRIBUTE_ID);
    	for(KimResponsibilityRequiredAttributeImpl requiredAttributeDataImpl: requiredAttributeData){
    		htmlData.add(getInquiryUrlForPrimaryKeys(KimResponsibilityRequiredAttributeImpl.class, requiredAttributeDataImpl, primaryKeys, 
    			getKimAttributeLabelFromDD(requiredAttributeDataImpl.getKimAttribute().getAttributeName())));
    	}
    	return new MultipleAnchorHtmlData(htmlData);
    }

    protected HtmlData getAttributesInquiryUrl(BusinessObject businessObject, String attributeName){
    	List<ResponsibilityAttributeDataImpl> responsibilityAttributeData = 
    		(List<ResponsibilityAttributeDataImpl>)ObjectUtils.getPropertyValue(businessObject, attributeName);
    	List<AnchorHtmlData> htmlData = new ArrayList<AnchorHtmlData>();
		List<String> primaryKeys = new ArrayList<String>();
		primaryKeys.add(ATTRIBUTE_DATA_ID);
    	for(ResponsibilityAttributeDataImpl responsibilityAttributeDataImpl: responsibilityAttributeData){
    		htmlData.add(getInquiryUrlForPrimaryKeys(ResponsibilityAttributeDataImpl.class, responsibilityAttributeDataImpl, primaryKeys, 
    			getKimAttributeLabelFromDD(responsibilityAttributeDataImpl.getKimAttribute().getAttributeName())+KimConstants.NAME_VALUE_SEPARATOR+
    			responsibilityAttributeDataImpl.getAttributeValue()));
    	}
    	return new MultipleAnchorHtmlData(htmlData);
    }

    protected HtmlData getAssignedRoleInquiryUrl(BusinessObject businessObject){
    	ResponsibilityImpl responsibility = (ResponsibilityImpl)businessObject;
    	List<KimRoleImpl> assignedToRoles = responsibility.getAssignedToRoles();
    	List<AnchorHtmlData> htmlData = new ArrayList<AnchorHtmlData>();
		List<String> primaryKeys = new ArrayList<String>();
		primaryKeys.add(ROLE_ID);
		if(assignedToRoles!=null && !assignedToRoles.isEmpty()){
			RoleService roleService = KIMServiceLocator.getRoleService();
			KimTypeInternalService kimTypeInternalService = KIMServiceLocator.getTypeInternalService();
			KimRoleInfo roleInfo;
			KimTypeImpl kimType;
			for(KimRoleImpl roleImpl: assignedToRoles){
				htmlData.add(getInquiryUrlForPrimaryKeys(KimRoleImpl.class, roleImpl, primaryKeys, 
        				roleImpl.getKimRoleType().getName()+KimConstants.NAME_VALUE_SEPARATOR+
        				roleImpl.getNamespaceCode()+KimConstants.NAME_VALUE_SEPARATOR+
        				roleImpl.getRoleName()));
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
		criteria.put("responsibilityId", fieldValues.get("responsibilityId").toString());
		KimResponsibilityImpl responsibilityImpl = (KimResponsibilityImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimResponsibilityImpl.class, criteria);
		return getResponsibilitiesSearchResultsCopy(responsibilityImpl);
	}

	public ResponsibilityService getResponsibilityService() {
		if (responsibilityService == null ) {
			responsibilityService = KIMServiceLocator.getResponsibilityService();
		}
		return responsibilityService;
	}
	
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
			(List<RoleResponsibilityImpl>)KNSServiceLocator.getBusinessObjectService().findMatching(RoleResponsibilityImpl.class, criteria);
		List<KimRoleImpl> assignedToRoles = new ArrayList<KimRoleImpl>();
		for(RoleResponsibilityImpl roleResponsibilityImpl: roleResponsibilitys){
			assignedToRoles.add(getKimRoleImpl(roleResponsibilityImpl.getRoleId()));
		}
		responsibilitySearchResultCopy.setAssignedToRoles(assignedToRoles);
		return responsibilitySearchResultCopy;
	}

}