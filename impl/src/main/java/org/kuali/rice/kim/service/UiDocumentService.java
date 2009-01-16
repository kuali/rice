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
package org.kuali.rice.kim.service;

import java.util.Map;

import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface UiDocumentService {
	/**
	 * 
	 * This method to populate Entity tables from person document pending tables when it is approved.
	 * 	  
	 * @param identityManagementPersonDocument
	 */
    void saveEntityPerson(IdentityManagementPersonDocument identityManagementPersonDocument);
    
    /**
     * 
     * This method is to set up the DD attribute entry map for role qualifiers, so it can be rendered.
     * 
     * @param personDocRole
     */
    Map<String,Object> getAttributeEntries( AttributeDefinitionMap definitions );
	/**
	 * 
	 * This method is to load entity to person document pending Bos when user 'initiate' a document for 'editing' entity.
	 * 
	 * @param identityManagementPersonDocument
	 * @param kimEntity
	 */
	void loadEntityToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, KimEntityImpl kimEntity);

	/**
	 * 
	 * This method loads a role document
	 * 
	 * @param identityManagementPersonDocument
	 */
	public void loadRoleDoc(IdentityManagementRoleDocument identityManagementRoleDocument, KimRole kimRole);
	
}