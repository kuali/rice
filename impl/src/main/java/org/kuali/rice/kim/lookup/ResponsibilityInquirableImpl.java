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
package org.kuali.rice.kim.lookup;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.impl.ResponsibilityImpl;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityRequiredAttributeImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.ResponsibilityAttributeDataImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.support.KimTypeInternalService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.HtmlData.MultipleAnchorHtmlData;
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
	
    /**
     * @see org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl#getInquiryUrl(org.kuali.rice.kns.bo.BusinessObject,
     *      java.lang.String, boolean)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
    	/*
    	 *  - permission detail values (attribute name and value separated by colon, commas between attributes)
		 *	- required role qualifiers (attribute name and value separated by colon, commas between attributes)
		 *	- list of roles assigned: role type, role namespace, role name
    	 */
		if(NAME.equals(attributeName) || NAMESPACE_CODE.equals(attributeName)){
			List<String> primaryKeys = new ArrayList<String>();
			primaryKeys.add(RESPONSIBILITY_ID);
			return getInquiryUrlForPrimaryKeys(ResponsibilityImpl.class, businessObject, primaryKeys, null);
        } else if(DETAIL_OBJECTS.equals(attributeName)){
        	return getAttributesInquiryUrl(businessObject, DETAIL_OBJECTS);
        } else if(REQUIRED_ROLE_QUALIFIER_ATTRIBUTES.equals(attributeName)){
        	return getRequiredRoleQualifierAttributesInquiryUrl(businessObject, REQUIRED_ROLE_QUALIFIER_ATTRIBUTES);
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

}