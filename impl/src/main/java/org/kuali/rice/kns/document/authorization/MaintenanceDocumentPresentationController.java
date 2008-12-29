/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.document.authorization;

import java.util.List;
import java.util.Set;

import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * The DocumentPresentationController class is used for non-user related lock down 
 * 
 * 
 */

public interface MaintenanceDocumentPresentationController extends DocumentPresentationController {
	
	/**
	 * 
	 * @param boClass
	 * @return boolean (true is can create new records)
	 */
	public boolean canCreate(Class boClass);
	
	/**
	 * 
	 * 
	 * @param document
	 * @return the list of uncondionallyReadOnly fields
	 */
	public List getReadOnlyFields(Document document);
	
	public void addMaintenanceDocumentRestrictions(MaintenanceDocumentAuthorizations auths, MaintenanceDocument document);
	/**
	 * This method should execute logic based on the state of the document or data in the document and return a list of
	 * property names that are not specified as read-only in the data dictionary but need to be read-only, based on this 
	 * conditional logic.
	 * 
	 * @param document
	 * @return Set of property names that can be used to identify additional AttributeDefinitions that should be read-only
	 */
	public Set<String> getConditionallyReadOnlyPropertyNames(Document document);

	/**
	 * This method should execute logic based on the state of the document or data in the document and return a list of
	 * property names that should be hidden, based on this conditional logic.
	 * 
	 * @param document
	 * @return Set of property names that can be used to identify AttributeDefinitions that should be hidden
	 */
	public Set<String> getConditionallyHiddenPropertyNames(Document document);

	/**
	 * This method should execute logic based on the state of the document or data in the document and return a list of
	 * section ids that need to be hidden, based on this conditional logic.
	 * 
	 * @param document
	 * @return Set of section ids that can be used to identify Sections that should be hidden
	 */
	public Set<String> getConditionallyHiddenSectionIds(Document document);
}

