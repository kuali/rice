/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kew.rule.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.service.RouteNodeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.web.KewKualiAction;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentConfigurationViewAction extends KewKualiAction {

	private PermissionService permissionService;
	private RoleManagementService roleService;
	private ResponsibilityService responsibilityService;
	private DocumentTypeService documentTypeService;
	private DataDictionaryService dataDictionaryService;
	private RouteNodeService routeNodeService;
	
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	populateForm( (DocumentConfigurationViewForm)form );
        return mapping.findForward("basic");
    }
    
    protected void populateForm( DocumentConfigurationViewForm form ) {
    	if ( StringUtils.isNotEmpty( form.getDocumentTypeName() ) ) {
    		form.setDocumentType( getDocumentTypeService().findByName( form.getDocumentTypeName() ) ); 
        	if ( form.getDocumentType() != null ) {
	    		form.getDocumentType().getChildrenDocTypes();
	    		form.setAttributeLabels( new HashMap<String, String>() );
	    		populateRelatedDocuments( form );
	    		populatePermissions( form );
	    		populateResponsibilities( form );
        	}
    	}
    }
    // TODO: find most-specific responsibility
    // TODO: override permissions??
    
    @SuppressWarnings("unchecked")
	public void populateRelatedDocuments( DocumentConfigurationViewForm form ) {
    	form.setParentDocumentType( form.getDocumentType().getParentDocType() );
    	form.setChildDocumentTypes( new ArrayList<DocumentType>( form.getDocumentType().getChildrenDocTypes() ) );
    }
    
	public void populatePermissions( DocumentConfigurationViewForm form ) {
		
		DocumentType docType = form.getDocumentType();
		Map<String,List<KimRoleInfo>> permRoles = new HashMap<String, List<KimRoleInfo>>(); 
		while ( docType != null) {
			String documentTypeName = docType.getName();
			form.addDocumentType(documentTypeName);
			Map<String,String> searchCriteria = new HashMap<String,String>();
			searchCriteria.put("attributeName", "documentTypeName" );
			searchCriteria.put("active", "Y");
			searchCriteria.put("detailCriteria",
					KimAttributes.DOCUMENT_TYPE_NAME+"="+docType.getName()
					);
			List<KimPermissionInfo> perms = getPermissionService().lookupPermissions( searchCriteria, false );
			for ( KimPermissionInfo perm : perms ) {
				List<String> roleIds = getPermissionService().getRoleIdsForPermissions(Collections.singletonList(perm));
				permRoles.put( perm.getPermissionId(), getRoleService().getRoles(roleIds) );
				for ( String attributeName : perm.getDetails().keySet() ) {
					addAttributeLabel(form, attributeName);
				}
			}
			form.setPermissionsForDocumentType(documentTypeName, perms);
			docType = docType.getParentDocType();			
		}
		
		form.setPermissionRoles( permRoles );
	}

	protected void addAttributeLabel( DocumentConfigurationViewForm form, String attributeName ) {
		if ( !form.getAttributeLabels().containsKey(attributeName) ) {
			form.getAttributeLabels().put(attributeName, 
					getDataDictionaryService().getAttributeLabel(KimAttributes.class, attributeName) );
		}
	}
	
	public void populateResponsibilities( DocumentConfigurationViewForm form ) {
		// TODO: pull all the responsibilities
		// TODO: merge the data and attach to route levels
		// pull the route levels and store on form
		List<RouteNode> routeNodes = getRouteNodeService().getFlattenedNodes(form.getDocumentType(), true);
		// TODO: filter out all but Request nodes
		
		form.setRouteNodes( routeNodes );
		// pull all the responsibilities and store into a map for use by the JSP
		
		// TODO: FILTER TO THE "Review" template only
		// TODO: pull responsibility roles
		Map<String,String> searchCriteria = new HashMap<String,String>();
		searchCriteria.put("attributeName", "documentTypeName" );
		searchCriteria.put("active", "Y");
		searchCriteria.put("detailCriteria",
				KimAttributes.DOCUMENT_TYPE_NAME+"="+form.getDocumentType().getName()
				);		
		List<? extends KimResponsibilityInfo> resps = getResponsibilityService().lookupResponsibilityInfo( searchCriteria, false );
		
		Map<String,List<KimResponsibilityInfo>> nodeToRespMap = new LinkedHashMap<String, List<KimResponsibilityInfo>>();
		for ( KimResponsibilityInfo r : resps ) {
			String routeNodeName = r.getDetails().get(KimAttributes.ROUTE_NODE_NAME); 
			if ( StringUtils.isNotBlank(routeNodeName) ) {
				if ( !nodeToRespMap.containsKey( routeNodeName ) ) {
					nodeToRespMap.put(routeNodeName, new ArrayList<KimResponsibilityInfo>() );
				}
				nodeToRespMap.get(routeNodeName).add(r);
			}
		}
		form.setResponsibilityMap( nodeToRespMap );
	}

	/**
	 * @return the permissionService
	 */
	public PermissionService getPermissionService() {
		if ( permissionService == null ) {
			permissionService = KIMServiceLocator.getPermissionService();
		}
		return permissionService;
	}

	/**
	 * @return the roleService
	 */
	public RoleManagementService getRoleService() {
		if ( roleService == null ) {
			roleService = KIMServiceLocator.getRoleManagementService();
		}
		return roleService;
	}

	/**
	 * @return the responsibilityService
	 */
	public ResponsibilityService getResponsibilityService() {
		if ( responsibilityService == null ) {
			responsibilityService = KIMServiceLocator.getResponsibilityService();
		}
		return responsibilityService;
	}

	/**
	 * @return the documentTypeService
	 */
	public DocumentTypeService getDocumentTypeService() {
		if ( documentTypeService == null ) {
			documentTypeService = KEWServiceLocator.getDocumentTypeService();
		}
		return documentTypeService;
	}

	public DataDictionaryService getDataDictionaryService() {
		if(dataDictionaryService == null){
			dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
		}
		return dataDictionaryService;
	}

	/**
	 * @return the routeNodeService
	 */
	public RouteNodeService getRouteNodeService() {
		if ( routeNodeService == null ) {
			routeNodeService = KEWServiceLocator.getRouteNodeService();
		}
		return routeNodeService;
	}
	
}
