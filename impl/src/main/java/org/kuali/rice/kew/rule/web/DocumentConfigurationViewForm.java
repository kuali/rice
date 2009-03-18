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

import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kns.web.struts.form.KualiForm;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentConfigurationViewForm extends KualiForm {

	private static final long serialVersionUID = -6251534168125209176L;
	
	protected String documentTypeName;
	protected DocumentType documentType; 
	protected DocumentType parentDocumentType; 
	protected List<DocumentType> childDocumentTypes; 
    protected List<KimPermissionInfo> permissions = null;
    protected Map<String,List<KimRoleInfo>> permissionRoles;

	/**
	 * @return the permissionRoles
	 */
	public Map<String, List<KimRoleInfo>> getPermissionRoles() {
		return this.permissionRoles;
	}

	/**
	 * @param permissionRoles the permissionRoles to set
	 */
	public void setPermissionRoles(Map<String, List<KimRoleInfo>> permissionRoles) {
		this.permissionRoles = permissionRoles;
	}

	public String getDocumentTypeName() {
		return this.documentTypeName;
	}

	public void setDocumentTypeName(String documentTypeName) {
		this.documentTypeName = documentTypeName;
	}

	public DocumentType getDocumentType() {
		return this.documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public List<KimPermissionInfo> getPermissions() {
		return this.permissions;
	}

	public void setPermissions(List<KimPermissionInfo> permissions) {
		this.permissions = permissions;
	}

	public DocumentType getParentDocumentType() {
		return this.parentDocumentType;
	}

	public void setParentDocumentType(DocumentType parentDocumentType) {
		this.parentDocumentType = parentDocumentType;
	}

	public List<DocumentType> getChildDocumentTypes() {
		return this.childDocumentTypes;
	}

	public void setChildDocumentTypes(List<DocumentType> childDocumentTypes) {
		this.childDocumentTypes = childDocumentTypes;
	}
	

}
