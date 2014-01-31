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
package org.kuali.rice.kim.impl.responsibility;

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
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityBo;
import org.kuali.rice.kim.inquiry.KimInquirableImpl;
import org.kuali.rice.kim.lookup.RoleLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.uif.widget.Inquiry;

@SuppressWarnings({"unchecked", "deprecation"})
public class ResponsibilityInquirableImpl extends KimInquirableImpl {
	private static final long serialVersionUID = 1L;
	
	@Override
	public void buildInquirableLink(Object dataObject, String propertyName, Inquiry inquiry){

		if(NAME.equals(propertyName) || NAME_TO_DISPLAY.equals(propertyName)){
			Map<String, String> primaryKeys = new HashMap<String, String>();
			primaryKeys.put(ID, ID);
			inquiry.buildInquiryLink(dataObject, propertyName, UberResponsibilityBo.class, primaryKeys);
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
    	 *  - responsibility detail values (attribute name and value separated by colon, commas between attributes)
		 *	- required role qualifiers (attribute name and value separated by colon, commas between attributes)
		 *	- list of roles assigned: role type, role namespace, role name
    	 */
    	if(NAME.equals(attributeName) || NAME_TO_DISPLAY.equals(attributeName)){
			return getInquiryUrlForPrimaryKeys(UberResponsibilityBo.class, businessObject, Collections.singletonList(ID), null);
    	} else if(NAMESPACE_CODE.equals(attributeName) || TEMPLATE_NAMESPACE_CODE.equals(attributeName)){
            String code = (String) KradDataServiceLocator.getDataObjectService().wrap(businessObject).getPropertyValueNullSafe(attributeName);
            NamespaceBo parameterNamespace = new NamespaceBo();
			parameterNamespace.setCode(code);
			return getInquiryUrlForPrimaryKeys(NamespaceBo.class, parameterNamespace, Collections.singletonList("code"), null);
        } else if(DETAIL_OBJECTS.equals(attributeName)){
        	//return getAttributesInquiryUrl(businessObject, DETAIL_OBJECTS);
        } else if(ASSIGNED_TO_ROLES.equals(attributeName)){
        	return getAssignedRoleInquiryUrl(businessObject);
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

	protected HtmlData getAttributesInquiryUrl(BusinessObject businessObject, String attributeName){
        DataObjectWrapper<BusinessObject> wrapper = KradDataServiceLocator.getDataObjectService().wrap(businessObject);
    	List<ResponsibilityAttributeBo> responsibilityAttributeData =
    		(List<ResponsibilityAttributeBo>) wrapper.getPropertyValueNullSafe(attributeName);
    	List<HtmlData.AnchorHtmlData> htmlData = new ArrayList<HtmlData.AnchorHtmlData>();
		List<String> primaryKeys = new ArrayList<String>();
		primaryKeys.add(ATTRIBUTE_DATA_ID);
    	for(ResponsibilityAttributeBo responsibilityAttributeDataImpl: responsibilityAttributeData){
    		htmlData.add(getInquiryUrlForPrimaryKeys(ResponsibilityAttributeBo.class, responsibilityAttributeDataImpl, primaryKeys,
    			getKimAttributeLabelFromDD(responsibilityAttributeDataImpl.getKimAttribute().getAttributeName())+ KimConstants.KimUIConstants.NAME_VALUE_SEPARATOR+
    			responsibilityAttributeDataImpl.getAttributeValue()));
    	}
    	return new HtmlData.MultipleAnchorHtmlData(htmlData);
    }

	protected HtmlData getAssignedRoleInquiryUrl(BusinessObject businessObject){
    	UberResponsibilityBo responsibility = (UberResponsibilityBo)businessObject;
    	List<RoleBo> assignedToRoles = responsibility.getAssignedToRoles();
    	List<HtmlData.AnchorHtmlData> htmlData = new ArrayList<HtmlData.AnchorHtmlData>();
		if(assignedToRoles!=null && !assignedToRoles.isEmpty()){
			List<String> primaryKeys = Collections.singletonList(ID);
			RoleService roleService = KimApiServiceLocator.getRoleService();
			for(RoleBo roleImpl: assignedToRoles){
				Role role = roleService.getRole(roleImpl.getId());
				HtmlData.AnchorHtmlData inquiryHtmlData = getInquiryUrlForPrimaryKeys(RoleBo.class, role, primaryKeys,
        				role.getNamespaceCode()+" "+
        				role.getName());
				inquiryHtmlData.setHref(RoleLookupableHelperServiceImpl.getCustomRoleInquiryHref(inquiryHtmlData.getHref()));
        		htmlData.add(inquiryHtmlData);
        	}
		}
    	return new HtmlData.MultipleAnchorHtmlData(htmlData);
    }

    @Override
	public Object retrieveDataObject(@SuppressWarnings("rawtypes") Map fieldValues){
    	return getBusinessObject(fieldValues);
    }

	@Override
	public BusinessObject getBusinessObject(@SuppressWarnings("rawtypes") Map fieldValues) {
        if ( fieldValues.get(ID) == null ) {
            return null;
        }       
		ResponsibilityBo responsibilityImpl = getDataObjectService().find(ResponsibilityBo.class, fieldValues.get(ID).toString());
		return getResponsibilitiesSearchResultsCopy(responsibilityImpl);
	}

	private UberResponsibilityBo getResponsibilitiesSearchResultsCopy(ResponsibilityBo responsibilitySearchResult){
		UberResponsibilityBo responsibilitySearchResultCopy = new UberResponsibilityBo();
		try{
			PropertyUtils.copyProperties(responsibilitySearchResultCopy, responsibilitySearchResult);
		} catch(Exception ex){
            throw new RuntimeException(ex);
		}
		List<RoleResponsibilityBo> roleResponsibilitys = 
		        getDataObjectService().findMatching(RoleResponsibilityBo.class, 
		                QueryByCriteria.Builder.forAttribute("responsibilityId", responsibilitySearchResultCopy.getId()).build()).getResults();
		List<RoleBo> assignedToRoles = new ArrayList<RoleBo>();
		for(RoleResponsibilityBo roleResponsibilityImpl: roleResponsibilitys){
			assignedToRoles.add( getDataObjectService().find(RoleBo.class, roleResponsibilityImpl.getRoleId()));
		}
		responsibilitySearchResultCopy.setAssignedToRoles(assignedToRoles);
		return responsibilitySearchResultCopy;
	}

}
