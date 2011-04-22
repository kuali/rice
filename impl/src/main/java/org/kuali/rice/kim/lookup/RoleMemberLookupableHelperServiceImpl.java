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
package org.kuali.rice.kim.lookup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.bo.entity.dto.KimEntityInfo;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public abstract class RoleMemberLookupableHelperServiceImpl extends KimLookupableHelperServiceImpl {

	protected static final String DETAIL_CRITERIA = "detailCriteria";
	protected static final String WILDCARD = "*";
    protected static final String TEMPLATE_NAMESPACE_CODE = "template." + KimConstants.UniqueKeyConstants.NAMESPACE_CODE;
    protected static final String TEMPLATE_NAME = "template.name";
    protected static final String NAMESPACE_CODE = KimConstants.UniqueKeyConstants.NAMESPACE_CODE;
    protected static final String NAME = "name";
    protected static final String GROUP_NAME = KimConstants.UniqueKeyConstants.GROUP_NAME;
    protected static final String ASSIGNED_TO_PRINCIPAL_NAME = "assignedToPrincipal.principalName";
    protected static final String ASSIGNED_TO_GROUP_NAMESPACE_CODE = "assignedToGroupNamespaceForLookup";
    protected static final String ASSIGNED_TO_GROUP_NAME = "assignedToGroup." + KimConstants.UniqueKeyConstants.GROUP_NAME;
    protected static final String ASSIGNED_TO_NAMESPACE_FOR_LOOKUP = "assignedToRoleNamespaceForLookup";
    protected static final String ASSIGNED_TO_ROLE_NAME = "assignedToRole." + KimConstants.UniqueKeyConstants.ROLE_NAME;
    protected static final String ATTRIBUTE_NAME = "attributeName";
    protected static final String ATTRIBUTE_VALUE = "attributeValue";
    protected static final String ASSIGNED_TO_ROLE_NAMESPACE_CODE = KimConstants.UniqueKeyConstants.NAMESPACE_CODE;
    protected static final String ASSIGNED_TO_ROLE_ROLE_NAME = KimConstants.UniqueKeyConstants.ROLE_NAME;
    protected static final String ASSIGNED_TO_ROLE_MEMBER_ID = "members.memberId";
    protected static final String DETAIL_OBJECTS_ATTRIBUTE_VALUE = "detailObjects.attributeValue";
    protected static final String DETAIL_OBJECTS_ATTRIBUTE_NAME = "detailObjects.kimAttribute.attributeName";
    
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
    	Map<String, String> searchCriteria = buildRoleSearchCriteria(fieldValues);
    	if(searchCriteria == null)
    		return new ArrayList<BusinessObject>();
        return getMemberSearchResults(fieldValues, unbounded);
    }

    protected abstract List<? extends BusinessObject> getMemberSearchResults(Map<String, String> searchCriteria, boolean unbounded);
    
    protected Map<String, String> buildSearchCriteria(Map<String, String> fieldValues){
        String templateNamespaceCode = fieldValues.get(TEMPLATE_NAMESPACE_CODE);
        String templateName = fieldValues.get(TEMPLATE_NAME);
        String namespaceCode = fieldValues.get(NAMESPACE_CODE);
        String name = fieldValues.get(NAME);
        String attributeDetailValue = fieldValues.get(ATTRIBUTE_VALUE);
        String attributeDetailName = fieldValues.get(ATTRIBUTE_NAME);
        String detailCriteria = fieldValues.get( DETAIL_CRITERIA );
        String active = fieldValues.get( KNSPropertyConstants.ACTIVE );

    	Map<String,String> searchCriteria = new HashMap<String, String>();
    	if(StringUtils.isNotEmpty(templateNamespaceCode)) {
    		searchCriteria.put(TEMPLATE_NAMESPACE_CODE, WILDCARD+templateNamespaceCode+WILDCARD);
    	}
        if(StringUtils.isNotEmpty(templateName)) {
        	searchCriteria.put(TEMPLATE_NAME, WILDCARD+templateName+WILDCARD);
        }
        if(StringUtils.isNotEmpty(namespaceCode)) {
        	searchCriteria.put(NAMESPACE_CODE, WILDCARD+namespaceCode+WILDCARD);
        }
        if(StringUtils.isNotEmpty(name)) {
        	searchCriteria.put(NAME, WILDCARD+name+WILDCARD);
        }
        if(StringUtils.isNotEmpty(attributeDetailValue)) {
        	searchCriteria.put(DETAIL_OBJECTS_ATTRIBUTE_VALUE, WILDCARD+attributeDetailValue+WILDCARD);
        }
        if(StringUtils.isNotEmpty(attributeDetailName)) {
        	searchCriteria.put(DETAIL_OBJECTS_ATTRIBUTE_NAME, WILDCARD+attributeDetailName+WILDCARD);
        }
        if ( StringUtils.isNotBlank( detailCriteria ) ) {
        	searchCriteria.put(DETAIL_CRITERIA, detailCriteria);
        }
        if ( StringUtils.isNotBlank( active ) ) {
        	searchCriteria.put(KNSPropertyConstants.ACTIVE, active);
        }

        return searchCriteria;
    }
    
    protected String getQueryString(String parameter){
    	if(StringUtils.isEmpty(parameter))
    		return WILDCARD;
    	else
    		return WILDCARD+parameter+WILDCARD;
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

    @SuppressWarnings({ "unchecked" })
	protected Map<String, String> buildRoleSearchCriteria(Map<String, String> fieldValues){
       	String assignedToPrincipalName = fieldValues.get(ASSIGNED_TO_PRINCIPAL_NAME);
    	Map<String, String> searchCriteria;
    	List<KimPrincipalInfo> principals = new ArrayList<KimPrincipalInfo>();
        if(StringUtils.isNotEmpty(assignedToPrincipalName)){
        	searchCriteria = new HashMap<String, String>();
        	searchCriteria.put("principals.principalName", WILDCARD+assignedToPrincipalName+WILDCARD);
        	List<KimEntityInfo> kimEntityInfoList = KIMServiceLocator.getIdentityManagementService().lookupEntityInfo(searchCriteria, true);
        	if(kimEntityInfoList == null || kimEntityInfoList.isEmpty()) {
        		return null;
        	}
        	else {
        		for (KimEntityInfo kimEntityInfo : kimEntityInfoList) {
        			if(kimEntityInfo.getPrincipals() != null){
        				principals.addAll(kimEntityInfo.getPrincipals());
        			}
        		}
        	}
        }
        String assignedToGroupNamespaceCode = fieldValues.get(ASSIGNED_TO_GROUP_NAMESPACE_CODE);
        String assignedToGroupName = fieldValues.get(ASSIGNED_TO_GROUP_NAME);
        List<GroupInfo> groups = null;
        if(StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isEmpty(assignedToGroupName) ||
        		StringUtils.isEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName) ||
        		StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName)){
        	searchCriteria = new HashMap<String, String>();
        	searchCriteria.put(NAMESPACE_CODE, getQueryString(assignedToGroupNamespaceCode));
        	searchCriteria.put(GROUP_NAME, getQueryString(assignedToGroupName));
        	groups = (List<GroupInfo>) KIMServiceLocator.getGroupService().lookupGroups(searchCriteria);
        	if(groups==null || groups.size()==0)
        		return null;
        }

        String assignedToRoleNamespaceCode = fieldValues.get(ASSIGNED_TO_NAMESPACE_FOR_LOOKUP);
        String assignedToRoleName = fieldValues.get(ASSIGNED_TO_ROLE_NAME);

    	searchCriteria = new HashMap<String, String>();
        if(StringUtils.isNotEmpty(assignedToRoleNamespaceCode))
        	searchCriteria.put(ASSIGNED_TO_ROLE_NAMESPACE_CODE, WILDCARD+assignedToRoleNamespaceCode+WILDCARD);
        if(StringUtils.isNotEmpty(assignedToRoleName))
        	searchCriteria.put(ASSIGNED_TO_ROLE_ROLE_NAME, WILDCARD+assignedToRoleName+WILDCARD);

    	StringBuffer memberQueryString = null;
        if(principals!=null){
        	memberQueryString = new StringBuffer();
        	for(KimPrincipalInfo principal: principals){
        		memberQueryString.append(principal.getPrincipalId()+KimConstants.KimUIConstants.OR_OPERATOR);
        	}
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
            	memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
        }
        if(groups!=null){
        	if(memberQueryString==null)
        		memberQueryString = new StringBuffer();
        	else if(StringUtils.isNotEmpty(memberQueryString.toString()))
        		memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
        	for(GroupInfo group: groups){
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
	
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getRows()
	 */
	@Override
	public List<Row> getRows() {
		List<Row> rows = super.getRows();
		Iterator<Row> i = rows.iterator();
		while ( i.hasNext() ) {
			Row row = i.next();
			int numFieldsRemoved = 0;
			boolean rowIsBlank = true;
			for (Iterator<Field> fieldIter = row.getFields().iterator(); fieldIter.hasNext();) {
				Field field = fieldIter.next();
				String propertyName = field.getPropertyName();
				if ( propertyName.equals(DETAIL_CRITERIA) ) {
					Object val = getParameters().get( propertyName );
					String propVal = null;
					if ( val != null ) {
						propVal = (val instanceof String)?(String)val:((String[])val)[0];
					}
					if ( StringUtils.isBlank( propVal ) ) {
						fieldIter.remove();
						numFieldsRemoved++;
					} else {
						field.setReadOnly(true);
						rowIsBlank = false;
						// leaving this in would prevent the "clear" button from resetting this value
//						field.setDefaultValue( propVal );
					}
				} else if (!Field.BLANK_SPACE.equals(field.getFieldType())) {
					rowIsBlank = false;
				}
			}
			// Check if any fields were removed from the row.
			if (numFieldsRemoved > 0) {
				// If fields were removed, check whether or not the remainder of the row is empty or only has blank space fields.
				if (rowIsBlank) {
					// If so, then remove the row entirely.
					i.remove();
				} else {
					// Otherwise, add one blank space for each field that was removed, using a technique similar to FieldUtils.createBlankSpace.
					// It is safe to just add blank spaces as needed, since the removable field is the last of the visible ones in the list (or
					// at least for the Permission and Responsibility lookups).
					while (numFieldsRemoved > 0) {
						Field blankSpace = new Field();
						blankSpace.setFieldType(Field.BLANK_SPACE);
						blankSpace.setPropertyName(Field.BLANK_SPACE);
						row.getFields().add(blankSpace);
						numFieldsRemoved--;
					}
				}
			}
		}
		return rows;
	}
    
	protected Long getActualSizeIfTruncated(List result){
		Long actualSizeIfTruncated = new Long(0); 
		if(result instanceof CollectionIncomplete)
			actualSizeIfTruncated = ((CollectionIncomplete)result).getActualSizeIfTruncated();
		return actualSizeIfTruncated;
	}
	
	@SuppressWarnings("unchecked")
	protected List<RoleImpl> searchRoles(Map<String, String> roleSearchCriteria, boolean unbounded){
		List<RoleImpl> roles = (List<RoleImpl>)getLookupService().findCollectionBySearchHelper(
				RoleImpl.class, roleSearchCriteria, unbounded);
		String membersCrt = roleSearchCriteria.get("members.memberId");
		List<RoleImpl> roles2Remove = new ArrayList<RoleImpl>();
		if(StringUtils.isNotBlank(membersCrt)){
			List<String> memberSearchIds = new ArrayList<String>();
			List<String> memberIds = new ArrayList<String>(); 
			if(membersCrt.contains(KimConstants.KimUIConstants.OR_OPERATOR))
				memberSearchIds = new ArrayList<String>(Arrays.asList(membersCrt.split("\\|")));
			else
				memberSearchIds.add(membersCrt);
			for(RoleImpl roleImpl : roles){	
				List<RoleMemberImpl> roleMembers = roleImpl.getMembers();
				memberIds.clear(); 
		        CollectionUtils.filter(roleMembers, new Predicate() {
					public boolean evaluate(Object object) {
						RoleMemberImpl member = (RoleMemberImpl) object;
						// keep active member
						return member.isActive();
					}
				});
		       
		        if(roleMembers != null && !roleMembers.isEmpty()){
		        	for(RoleMemberImpl memberImpl : roleMembers)
		        		memberIds.add(memberImpl.getMemberId());
		        	if(((List<String>)CollectionUtils.intersection(memberSearchIds, memberIds)).isEmpty())
		        		roles2Remove.add(roleImpl);
		        }
		        else
		        {
		        	roles2Remove.add(roleImpl);
		        }
			}
		}
		if(!roles2Remove.isEmpty())
			roles.removeAll(roles2Remove);
		return roles;
	}


}
