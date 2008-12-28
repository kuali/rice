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
}

