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
package org.kuali.rice.kim.bo.impl;

import java.util.LinkedHashMap;

import org.kuali.rice.kim.bo.role.KimResponsibility;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ReviewResponsibility extends PersistableBusinessObjectBase {

	private static final long serialVersionUID = 1L;

	// standard responsibility attributes 
	protected String responsibilityId;
	protected String namespaceCode;
	protected String name;
	protected String description;
	protected boolean active;

	// review responsibility standard properties
	protected String documentTypeName;
	protected String routeNodeName;
	protected boolean actionDetailsAtRoleMemberLevel;
	protected boolean required;
	protected String qualifierResolverProvidedIdentifier;
	
	public ReviewResponsibility() {
	}
	
	public ReviewResponsibility( KimResponsibility resp ) {
		loadFromKimResponsibility(resp);
	}
	
	public void loadFromKimResponsibility( KimResponsibility resp ) {
    	setResponsibilityId( resp.getResponsibilityId() );
    	setNamespaceCode( resp.getNamespaceCode() );
    	setName( resp.getName() );
    	setDescription( resp.getDescription() );
    	setActive( resp.isActive() );
    	AttributeSet respDetails = resp.getDetails();
    	setDocumentTypeName( respDetails.get( KimAttributes.DOCUMENT_TYPE_NAME ) );
    	setRouteNodeName( respDetails.get( KimAttributes.DOCUMENT_TYPE_NAME ) );
    	setActionDetailsAtRoleMemberLevel( Boolean.valueOf( respDetails.get( KimAttributes.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL ) ) );
    	setRequired( Boolean.valueOf( respDetails.get( KimAttributes.REQUIRED ) ) );
    	setQualifierResolverProvidedIdentifier( respDetails.get( KimAttributes.QUALIFIER_RESOLVER_PROVIDED_IDENTIFIER ) );
	}
	
	/**
	 * @return the responsibilityId
	 */
	public String getResponsibilityId() {
		return this.responsibilityId;
	}

	/**
	 * @param responsibilityId the responsibilityId to set
	 */
	public void setResponsibilityId(String responsibilityId) {
		this.responsibilityId = responsibilityId;
	}

	/**
	 * @return the namespaceCode
	 */
	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	/**
	 * @param namespaceCode the namespaceCode to set
	 */
	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the documentTypeName
	 */
	public String getDocumentTypeName() {
		return this.documentTypeName;
	}

	/**
	 * @param documentTypeName the documentTypeName to set
	 */
	public void setDocumentTypeName(String documentTypeName) {
		this.documentTypeName = documentTypeName;
	}

	/**
	 * @return the routeNodeName
	 */
	public String getRouteNodeName() {
		return this.routeNodeName;
	}

	/**
	 * @param routeNodeName the routeNodeName to set
	 */
	public void setRouteNodeName(String routeNodeName) {
		this.routeNodeName = routeNodeName;
	}

	/**
	 * @return the actionDetailsAtRoleMemberLevel
	 */
	public boolean isActionDetailsAtRoleMemberLevel() {
		return this.actionDetailsAtRoleMemberLevel;
	}

	/**
	 * @param actionDetailsAtRoleMemberLevel the actionDetailsAtRoleMemberLevel to set
	 */
	public void setActionDetailsAtRoleMemberLevel(
			boolean actionDetailsAtRoleMemberLevel) {
		this.actionDetailsAtRoleMemberLevel = actionDetailsAtRoleMemberLevel;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return this.required;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * @return the qualifierResolverProvidedIdentifier
	 */
	public String getQualifierResolverProvidedIdentifier() {
		return this.qualifierResolverProvidedIdentifier;
	}

	/**
	 * @param qualifierResolverProvidedIdentifier the qualifierResolverProvidedIdentifier to set
	 */
	public void setQualifierResolverProvidedIdentifier(
			String qualifierResolverProvidedIdentifier) {
		this.qualifierResolverProvidedIdentifier = qualifierResolverProvidedIdentifier;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap<String,Object> m = new LinkedHashMap<String, Object>();
		m.put("responsibilityId", responsibilityId);
		m.put("namespaceCode", namespaceCode);
		m.put("name", name);
		m.put("active", active);
		m.put("documentTypeName", documentTypeName);
		m.put("routeNodeName", routeNodeName);
		m.put("required", required);
		m.put("actionDetailsAtRoleMemberLevel", actionDetailsAtRoleMemberLevel);
		m.put("qualifierResolverProvidedIdentifier", qualifierResolverProvidedIdentifier);
		return m;
	}

	public void refresh() {
		// do nothing - not a persistable object
	}
	
	@Override
	public void refreshNonUpdateableReferences() {
		// do nothing - not a persistable object
	}
	@Override
	public void refreshReferenceObject(String referenceObjectName) {
		// do nothing - not a persistable object
	}

}
