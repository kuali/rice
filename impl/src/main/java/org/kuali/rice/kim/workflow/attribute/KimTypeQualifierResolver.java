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
package org.kuali.rice.kim.workflow.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.ui.PersonDocumentGroup;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.KimTypeInfoService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.workflow.attribute.QualifierResolverBase;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimTypeQualifierResolver extends QualifierResolverBase {
	private static final Logger LOG = Logger.getLogger(KimTypeQualifierResolver.class);
	
	protected static final String GROUP_ROUTE_LEVEL = "GroupType";
	protected static final String ROLE_ROUTE_LEVEL = "RoleType";

	private static KimTypeInfoService kimTypeInfoService;
	private static GroupService groupService;
	
	protected static Map<String,KimTypeService> typeServices = new HashMap<String, KimTypeService>();
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.role.QualifierResolver#resolve(org.kuali.rice.kew.engine.RouteContext)
	 */
	public List<AttributeSet> resolve(RouteContext context) {
        String routeLevel = context.getNodeInstance().getName();
        Document document = getDocument(context);
        List<AttributeSet> qualifiers = new ArrayList<AttributeSet>();
        String customDocTypeName = null;
        
        if ( document instanceof IdentityManagementGroupDocument ) {
        	customDocTypeName = handleGroupDocument(qualifiers, (IdentityManagementGroupDocument)document, routeLevel);
        } else if ( document instanceof IdentityManagementRoleDocument ) {
        	customDocTypeName = handleRoleDocument(qualifiers, (IdentityManagementRoleDocument)document, routeLevel);
        } else if ( document instanceof IdentityManagementPersonDocument ) {
        	customDocTypeName = handlePersonDocument(qualifiers, (IdentityManagementPersonDocument)document, routeLevel);
        }
    	// add standard components
        decorateWithCommonQualifiers(qualifiers, context, customDocTypeName);
    	// return the resulting list of AttributeSets
		return qualifiers;
	}

	protected KimTypeService getTypeService( String typeId ) {
    	KimTypeService typeService = typeServices.get(typeId);
    	if ( typeService == null ) {       		
        	KimTypeInfo typeInfo = getKimTypeInfoService().getKimType(typeId);
        	if ( typeInfo != null && StringUtils.isNotEmpty( typeInfo.getKimTypeServiceName() ) ) {
        		typeService = (KimTypeService)KIMServiceLocator.getBean(typeInfo.getKimTypeServiceName());
        		typeServices.put(typeId, typeService);
        	} else {
        		LOG.warn( "Unable to retrieve KIM Type Info object for id: " + typeId );
        	}
    	}
    	return typeService;
	}
	
	protected void putMatchingAttributesIntoQualifier( AttributeSet qualifier, AttributeSet itemAttributes, List<String> routingAttributes ) {
		if ( routingAttributes != null && !routingAttributes.isEmpty() ) {
        	// pull the qualifiers off the document object (group or role member)
    		for ( String attribName : routingAttributes ) {
    			qualifier.put( attribName, itemAttributes.get(attribName));
    		}
		}
	}
	
	protected String handleGroupDocument( List<AttributeSet> qualifiers, IdentityManagementGroupDocument groupDoc, String routeLevel ) {
    	// get the appropriate type service for the group being edited
    	String typeId = groupDoc.getGroupTypeId();
    	qualifiers.add( getGroupQualifier(groupDoc.getGroupId(), typeId, groupDoc.getQualifiersAsAttributeSet(), routeLevel) );
    	
        return null;
	}

	protected String handleRoleDocument( List<AttributeSet> qualifiers, IdentityManagementRoleDocument roleDoc, String routeLevel ) {
        String customDocTypeName = null;

        LOG.warn( "Role member data routing not implemented for the Role document yet!" );
    	// get the appropriate type service for the group being edited
    	String typeId = roleDoc.getRoleTypeId();
    	KimTypeService typeService = getTypeService(typeId);
    	if ( typeService != null ) {
    		// TODO: get from role document the role members which are being added
    		// QUESTION: can they be modified in a way which requires routing?
    		// loop over the added role members
    		// pull the needed information off the member data and add to qualifier
    		// create a qualifier set for each role
    		
        	// add role ID
        	// add KIM Type ID
            for (AttributeSet qualifier : qualifiers) {
                qualifier.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, typeId);
                qualifier.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleDoc.getRoleId());
                qualifier.put(KimAttributes.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER, typeId);
            }        	
    		customDocTypeName = typeService.getWorkflowDocumentTypeName();
    	}		
    	return customDocTypeName;
	}
	
	protected String handlePersonDocument( List<AttributeSet> qualifiers, IdentityManagementPersonDocument personDoc, String routeLevel ) {
    	// check the route level - see if we are doing groups or roles at the moment
        String principalId = personDoc.getPrincipalId();
        if ( GROUP_ROUTE_LEVEL.equals(routeLevel) ) {
        	// if groups, find any groups to which the user was added or removed
        	// get the type and service for each group
        	// handle as per the group document, a qualifier for each group
        	List<String> currentGroups = getGroupService().getDirectGroupIdsForPrincipal(principalId);
        	List<PersonDocumentGroup> groups = personDoc.getGroups();
        	for ( PersonDocumentGroup group : groups ) {
        		// if they are being added to the group, add a qualifier set
        		if ( group.isActive() && !currentGroups.contains( group.getGroupId() ) ) {
            		// pull the group to get its attributes for adding to the qualifier 
            		GroupInfo kimGroup = getGroupService().getGroupInfo(group.getGroupId());
        			qualifiers.add( getGroupQualifier( group.getGroupId(), kimGroup.getKimTypeId(), kimGroup.getAttributes(), routeLevel ) );
        		}
        	}
        	// detect removed groups
        	// get the existing directly assigned groups for the person
        	for ( String groupId : currentGroups ) {
        		for ( PersonDocumentGroup group : groups ) {
        			if ( !group.isActive() ) {
                		GroupInfo kimGroup = getGroupService().getGroupInfo(groupId);
            			qualifiers.add( getGroupQualifier( groupId, kimGroup.getKimTypeId(), kimGroup.getAttributes(), routeLevel ) );
        			}
        		}
        	}
        } else if ( ROLE_ROUTE_LEVEL.equals(routeLevel) ) {
        	// if roles, check the role member data for any roles added
        	// get the type and service for each role
        	// handle as for the role document, a qualifier for each role membership added
        	LOG.warn( "Role-based data routing on the person document not implemented!" );
        }
        
    	// get the appropriate type service for the group being edited
    	//String typeId = personDoc.getRoleTypeId();
//    	KimTypeService typeService = typeServices.get(typeId);
//    	if ( typeService == null ) {       		
//        	KimTypeInfo typeInfo = getKimTypeInfoService().getKimType(typeId);
//    		typeService = (KimTypeService)KIMServiceLocator.getBean(typeInfo.getKimTypeServiceName());
//    		typeServices.put(typeId, typeService);
//    	}
//    	if ( typeService != null ) {
//        	// add role ID
//        	// add KIM Type ID
//            for (AttributeSet qualifier : qualifiers) {
//                qualifier.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, typeId);
//                qualifier.put(KimConstants.PrimaryKeyConstants.ROLE_ID, personDoc.getRoleId());
//            }        	
//    		customDocTypeName = typeService.getWorkflowDocumentTypeName();
//    	}		
    	return null;
	}

    protected AttributeSet getGroupQualifier( String groupId, String kimTypeId, AttributeSet groupAttributes, String routeLevel ) {
		AttributeSet qualifier = new AttributeSet();        			
		// pull the group to get its attributes for adding to the qualifier 
        qualifier.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, kimTypeId);
        qualifier.put(KimAttributes.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER, kimTypeId);
        qualifier.put(KimConstants.PrimaryKeyConstants.GROUP_ID, groupId);
    	KimTypeService typeService = getTypeService(kimTypeId);
    	if ( typeService != null ) {
    		// check for the custom document type for the group
    		String customDocTypeName = typeService.getWorkflowDocumentTypeName();
    		if ( StringUtils.isNotBlank(customDocTypeName)) {
    			qualifier.put(KIM_ATTRIBUTE_DOCUMENT_TYPE_NAME, customDocTypeName );
    		}
    		putMatchingAttributesIntoQualifier(qualifier, groupAttributes, typeService.getWorkflowRoutingAttributes(routeLevel) );
    	}
    	return qualifier;
    }
    
	
	public KimTypeInfoService getKimTypeInfoService() {
		if ( kimTypeInfoService == null ) {
			kimTypeInfoService = KIMServiceLocator.getTypeInfoService();
		}
		return kimTypeInfoService;
	}

	public static GroupService getGroupService() {
		if ( groupService == null ) {
			groupService = KIMServiceLocator.getGroupService();
		}
		return groupService;
	}

}
