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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
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
//    protected List<KimPermissionInfo> permissions = null;
    protected List<String> docTypeHierarchyList = new ArrayList<String>();
    protected LinkedHashMap<String,List<KimPermissionInfo>> permissionsByDocumentType = new LinkedHashMap<String, List<KimPermissionInfo>>();
    protected Map<String,List<KimRoleInfo>> permissionRoles = new HashMap<String, List<KimRoleInfo>>();
    protected Map<String,String> attributeLabels;
    protected Map<String,String> seenTemplates = new HashMap<String,String>();
    protected List<RouteNode> routeNodes;
    protected Map<String,List<KimResponsibilityInfo>> responsibilityMap;

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

//	public List<KimPermissionInfo> getPermissions() {
//		return this.permissions;
//	}
//
//	public void setPermissions(List<KimPermissionInfo> permissions) {
//		this.permissions = permissions;
//	}

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

	/**
	 * @return the attributeLabels
	 */
	public Map<String, String> getAttributeLabels() {
		return this.attributeLabels;
	}

	/**
	 * @param attributeLabels the attributeLabels to set
	 */
	public void setAttributeLabels(Map<String, String> attributeLabels) {
		this.attributeLabels = attributeLabels;
	}

	/**
	 * @return the seenTemplates
	 */
	public Map<String, String> getSeenTemplates() {
		return this.seenTemplates;
	}

	/**
	 * @param seenTemplates the seenTemplates to set
	 */
	public void setSeenTemplates(Map<String, String> seenTemplates) {
		this.seenTemplates = seenTemplates;
	}

	/**
	 * @return the routeNodes
	 */
	public List<RouteNode> getRouteNodes() {
		return this.routeNodes;
	}

	/**
	 * @param routeNodes the routeNodes to set
	 */
	public void setRouteNodes(List<RouteNode> routeNodes) {
		this.routeNodes = routeNodes;
	}

	/**
	 * @return the responsibilityMap
	 */
	public Map<String, List<KimResponsibilityInfo>> getResponsibilityMap() {
		return this.responsibilityMap;
	}

	/**
	 * @param responsibilityMap the responsibilityMap to set
	 */
	public void setResponsibilityMap(
			Map<String, List<KimResponsibilityInfo>> responsibilityMap) {
		this.responsibilityMap = responsibilityMap;
	}

	/**
	 * @return the docTypeHierarchyList
	 */
	public List<String> getDocTypeHierarchyList() {
		return this.docTypeHierarchyList;
	}

	/**
	 * @param docTypeHierarchyList the docTypeHierarchyList to set
	 */
	public void setDocTypeHierarchyList(List<String> docTypeHierarchyList) {
		this.docTypeHierarchyList = docTypeHierarchyList;
	}

	/**
	 * @return the permissionsByDocumentType
	 */
	public LinkedHashMap<String, List<KimPermissionInfo>> getPermissionsByDocumentType() {
		return this.permissionsByDocumentType;
	}

	/**
	 * @param permissionsByDocumentType the permissionsByDocumentType to set
	 */
	public void setPermissionsByDocumentType(
			LinkedHashMap<String, List<KimPermissionInfo>> permissionsByDocumentType) {
		this.permissionsByDocumentType = permissionsByDocumentType;
	}

	public void addDocumentType( String documentTypeName ) {
		docTypeHierarchyList.add(documentTypeName);		
	}
	
	public void setPermissionsForDocumentType( String documentTypeName, List<KimPermissionInfo> perms ) {
		permissionsByDocumentType.put(documentTypeName, perms);
	}

}
