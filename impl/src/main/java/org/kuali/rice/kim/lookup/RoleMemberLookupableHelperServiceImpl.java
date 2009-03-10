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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.KNSServiceLocator;

import com.sun.tools.doclets.internal.toolkit.resources.doclets_zh_CN;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public abstract class RoleMemberLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

	protected static final String DETAIL_CRITERIA = "detailCriteria";
	protected static final String PERCENT = "%";
    protected static final String TEMPLATE_NAMESPACE_CODE = "template.namespaceCode";
    protected static final String TEMPLATE_NAME = "template.name";
    protected static final String NAMESPACE_CODE = "namespaceCode";
    protected static final String NAME = "name";
    protected static final String GROUP_NAME = "groupName";
    protected static final String ASSIGNED_TO_PRINCIPAL_NAME = "assignedToPrincipal.principalName";
    protected static final String ASSIGNED_TO_GROUP_NAMESPACE_CODE = "assignedToGroupNamespaceForLookup";
    protected static final String ASSIGNED_TO_GROUP_NAME = "assignedToGroup.groupName";
    protected static final String ASSIGNED_TO_NAMESPACE_FOR_LOOKUP = "assignedToRoleNamespaceForLookup";
    protected static final String ASSIGNED_TO_ROLE_NAME = "assignedToRole.roleName";
    protected static final String ATTRIBUTE_VALUE = "attributeValue";
    protected static final String ASSIGNED_TO_ROLE_NAMESPACE_CODE = "namespaceCode";
    protected static final String ASSIGNED_TO_ROLE_ROLE_NAME = "roleName";
    protected static final String ASSIGNED_TO_ROLE_MEMBER_ID = "members.memberId";
    protected static final String DETAIL_OBJECTS_ATTRIBUTE_VALUE = "detailObjects.attributeValue";
    
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String,String> fieldValues) {
    	Map<String, String> searchCriteria = buildRoleSearchCriteria(fieldValues);
    	if(searchCriteria == null)
    		return new ArrayList<BusinessObject>();
        return getMemberSearchResults(fieldValues);
    }
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsUnbounded(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(
    		Map<String, String> fieldValues) {
    	Map<String, String> searchCriteria = buildRoleSearchCriteria(fieldValues);
    	if(searchCriteria == null)
    		return new ArrayList<BusinessObject>();
        return getMemberSearchResults(fieldValues);
    }

    protected abstract List<? extends BusinessObject> getMemberSearchResults(Map<String, String> searchCriteria);
    
    protected Map<String, String> buildSearchCriteria(Map<String, String> fieldValues){
        String templateNamespaceCode = fieldValues.get(TEMPLATE_NAMESPACE_CODE);
        String templateName = fieldValues.get(TEMPLATE_NAME);
        String namespaceCode = fieldValues.get(NAMESPACE_CODE);
        String name = fieldValues.get(NAME);
        String attributeDetailValue = fieldValues.get(ATTRIBUTE_VALUE);
        String detailCriteria = fieldValues.get( DETAIL_CRITERIA );

    	Map<String,String> searchCriteria = new HashMap<String, String>();
    	if(StringUtils.isNotEmpty(templateNamespaceCode))
        	searchCriteria.put(TEMPLATE_NAMESPACE_CODE, PERCENT+templateNamespaceCode+PERCENT);
        if(StringUtils.isNotEmpty(templateName))
        	searchCriteria.put(TEMPLATE_NAME, PERCENT+templateName+PERCENT);
        if(StringUtils.isNotEmpty(namespaceCode))
        	searchCriteria.put(NAMESPACE_CODE, PERCENT+namespaceCode+PERCENT);
        if(StringUtils.isNotEmpty(name))
        	searchCriteria.put(NAME, PERCENT+name+PERCENT);
        if(StringUtils.isNotEmpty(attributeDetailValue))
        	searchCriteria.put(DETAIL_OBJECTS_ATTRIBUTE_VALUE, PERCENT+attributeDetailValue+PERCENT);
        if ( StringUtils.isNotBlank( detailCriteria ) ) {
        	searchCriteria.put(DETAIL_CRITERIA, detailCriteria);
        }

        return searchCriteria;
    }
    
    protected String getQueryString(String parameter){
    	if(StringUtils.isEmpty(parameter))
    		return PERCENT;
    	else
    		return PERCENT+parameter+PERCENT;
    }
    
    /**
     * - detail value: 
     * if this is provided a full (template namespace and template name) or namespace must be supplied 
     * - may need to do further restrictions once we see how this performs
     *  
     * @param fieldValues the values of the query
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);
/*
        String valueTemplateNamespaceCode = (String) fieldValues.get(TEMPLATE_NAMESPACE_CODE);
        String valueTemplateName = (String) fieldValues.get(TEMPLATE_NAME);
        String name = (String) fieldValues.get(NAME);
        
        if (!((StringUtils.isNotEmpty(valueTemplateNamespaceCode) && StringUtils.isNotEmpty(valueTemplateName)) 
        		|| StringUtils.isNotEmpty(name))){
            throw new ValidationException("For a search to be performed on an attribute detail value, " +
            		"a combination of template namespace and template name, or a namespace must be supplied");
        }
        */
    }

    protected Map<String, String> buildRoleSearchCriteria(Map<String, String> fieldValues){
    	String assignedToPrincipalName = fieldValues.get(ASSIGNED_TO_PRINCIPAL_NAME);
    	Map<String, String> searchCriteria;
    	List<KimPrincipalImpl> principals = null;
        if(StringUtils.isNotEmpty(assignedToPrincipalName)){
        	searchCriteria = new HashMap<String, String>();
        	searchCriteria.put("principalName", PERCENT+assignedToPrincipalName+PERCENT);
        	principals = 
        		(List<KimPrincipalImpl>)KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded(KimPrincipalImpl.class, searchCriteria);
        	if(principals==null || principals.isEmpty())
        		return null;
        }
        String assignedToGroupNamespaceCode = fieldValues.get(ASSIGNED_TO_GROUP_NAMESPACE_CODE);
        String assignedToGroupName = fieldValues.get(ASSIGNED_TO_GROUP_NAME);
        List<KimGroupImpl> groupImpls = null;
        if(StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isEmpty(assignedToGroupName) ||
        		StringUtils.isEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName) ||
        		StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName)){
        	searchCriteria = new HashMap<String, String>();
        	searchCriteria.put(NAMESPACE_CODE, getQueryString(assignedToGroupNamespaceCode));
        	searchCriteria.put(GROUP_NAME, getQueryString(assignedToGroupName));
        	groupImpls = 
        		(List<KimGroupImpl>)KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded(KimGroupImpl.class, searchCriteria);
        	if(groupImpls==null || groupImpls.size()==0)
        		return null;
        }

        String assignedToRoleNamespaceCode = fieldValues.get(ASSIGNED_TO_NAMESPACE_FOR_LOOKUP);
        String assignedToRoleName = fieldValues.get(ASSIGNED_TO_ROLE_NAME);

    	searchCriteria = new HashMap<String, String>();
        if(StringUtils.isNotEmpty(assignedToRoleNamespaceCode))
        	searchCriteria.put(ASSIGNED_TO_ROLE_NAMESPACE_CODE, PERCENT+assignedToRoleNamespaceCode+PERCENT);
        if(StringUtils.isNotEmpty(assignedToRoleName))
        	searchCriteria.put(ASSIGNED_TO_ROLE_ROLE_NAME, PERCENT+assignedToRoleName+PERCENT);

    	StringBuffer memberQueryString = null;
        if(principals!=null){
        	memberQueryString = new StringBuffer();
        	for(KimPrincipalImpl principal: principals){
        		memberQueryString.append(principal.getPrincipalId()+KimConstants.KimUIConstants.OR_OPERATOR);
        	}
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
            	memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
        }
        if(groupImpls!=null){
        	if(memberQueryString==null)
        		memberQueryString = new StringBuffer();
        	else if(StringUtils.isNotEmpty(memberQueryString.toString()))
        		memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
        	for(KimGroupImpl group: groupImpls){
        		memberQueryString.append(group.getGroupId()+KimConstants.KimUIConstants.OR_OPERATOR);
        	}
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
            	memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
        	searchCriteria.put(ASSIGNED_TO_ROLE_MEMBER_ID, memberQueryString.toString());
        }
        if(memberQueryString!=null && StringUtils.isNotEmpty(memberQueryString.toString()))
        	searchCriteria.put(ASSIGNED_TO_ROLE_MEMBER_ID, memberQueryString.toString());

        return searchCriteria;
    }

    
    /** Checks whether the 2nd map is a subset of the first. */
	protected boolean isMapSubset( AttributeSet mainMap, AttributeSet subsetMap ) {
		for ( Map.Entry<String, String> keyValue : subsetMap.entrySet() ) {
			if ( !mainMap.containsKey(keyValue.getKey()) 
					|| !StringUtils.equals( mainMap.get(keyValue.getKey()), keyValue.getValue() ) ) {
//				if ( LOG.isDebugEnabled() ) {
//					LOG.debug( "Maps did not match:\n" + mainMap + "\n" + subsetMap );
//				}
				return false;
			}
		}
//		if ( LOG.isDebugEnabled() ) {
//			LOG.debug( "Maps matched:\n" + mainMap + "\n" + subsetMap );
//		}
		return true;
	}

	/** Converts a special criteria string that is in the form key=value,key2=value2 into a map */
	protected AttributeSet parseDetailCriteria( String detailCritiera ) {
	    if ( StringUtils.isBlank(detailCritiera) ) {
	        return new AttributeSet(0);
	    }
		String[] keyValuePairs = StringUtils.split(detailCritiera, ',');
		if ( keyValuePairs == null || keyValuePairs.length == 0 ) {
		    return new AttributeSet(0);
		}
		AttributeSet parsedDetails = new AttributeSet( keyValuePairs.length );
		for ( String keyValueStr : keyValuePairs ) {
			String[] keyValue = StringUtils.split(keyValueStr, '=');
			if ( keyValue.length >= 2 ) {
				parsedDetails.put(keyValue[0], keyValue[1]);
			}
		}
		return parsedDetails;
	}
	
    
}