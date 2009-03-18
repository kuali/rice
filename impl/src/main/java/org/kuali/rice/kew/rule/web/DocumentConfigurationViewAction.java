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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.web.KewKualiAction;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentConfigurationViewAction extends KewKualiAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	populateForm( (DocumentConfigurationViewForm)form );
        return mapping.findForward("basic");
    }
    
    protected void populateForm( DocumentConfigurationViewForm form ) {
    	if ( StringUtils.isNotEmpty( form.getDocumentTypeName() ) ) {
    		form.setDocumentType( KEWServiceLocator.getDocumentTypeService().findByName( form.getDocumentTypeName() ) ); 
        	if ( form.getDocumentType() != null ) {
	    		form.getDocumentType().getChildrenDocTypes();
	    		populateRelatedDocuments( form );
	    		populatePermissions( form );
	    		populateResponsibilities( form );
        	}
    	}
    }
    // TODO: get route levels
    // TODO: find most-specific responsibility
    // TODO: find most-specific permission
    // TODO: pull needed attribute names for display?
    
    @SuppressWarnings("unchecked")
	public void populateRelatedDocuments( DocumentConfigurationViewForm form ) {
    	form.setParentDocumentType( form.getDocumentType().getParentDocType() );
    	form.setChildDocumentTypes( new ArrayList<DocumentType>( form.getDocumentType().getChildrenDocTypes() ) );
    }
    
	public void populatePermissions( DocumentConfigurationViewForm form ) {
		
		DocumentType docType = form.getDocumentType();
		List<KimPermissionInfo> allPerms = new ArrayList<KimPermissionInfo>();
		Map<String,List<KimRoleInfo>> permRoles = new HashMap<String, List<KimRoleInfo>>(); 
		while ( docType != null) {
			Map<String,String> searchCriteria = new HashMap<String,String>();
			searchCriteria.put("attributeName", "documentTypeName" );
			searchCriteria.put("active", "Y");
			searchCriteria.put("detailCriteria",
					KimAttributes.DOCUMENT_TYPE_NAME+"="+docType.getName()
					);
			List<KimPermissionInfo> perms = KIMServiceLocator.getPermissionService().lookupPermissions( searchCriteria, false );
			for ( KimPermissionInfo perm : perms ) {
				List<String> roleIds = KIMServiceLocator.getPermissionService().getRoleIdsForPermissions(Collections.singletonList(perm));
				permRoles.put( perm.getPermissionId(), KIMServiceLocator.getRoleService().getRoles(roleIds) );
			}
			allPerms.addAll(perms);
			docType = docType.getParentDocType();			
		}
		
		form.setPermissions( allPerms );
		form.setPermissionRoles( permRoles );
	}

	public void populateResponsibilities( DocumentConfigurationViewForm form ) {
		// TODO: pull all the route levels
		// TODO: pull all the responsibilities
		// TODO: merge the data and attach to route levels
//		Map<String,String> searchCriteria = new HashMap<String,String>();
//		searchCriteria.put("attributeName", "documentTypeName" );
//		searchCriteria.put("active", "Y");
//		searchCriteria.put("detailCriteria",
//				KimAttributes.DOCUMENT_TYPE_NAME+"="+form.getDocumentType().getName()
//				);
//		form.setPermissions( KIMServiceLocator.getPermissionService().lookupPermissions( searchCriteria, false ) );
	}
	
}
