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

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.kns.authorization.BusinessObjectAuthorizationsBase;

/**
 * 
 * This class holds all the information needed to describe the authorization
 * related restrictions for a MaintenanceDocument.
 * 
 * IMPORTANT NOTE: This class defaults to fully editable, if not otherwise
 * specified. So if this class is queried for the status of a field, and the
 * field has not been specified in this class, it will return a
 * FieldAuthorization class populated with the fieldName and EDITABLE.
 * 
 * 
 */
public class MaintenanceDocumentAuthorizations extends BusinessObjectAuthorizationsBase {
	private Set<String> readOnlySectionIds;

	public MaintenanceDocumentAuthorizations() {
		super();
		readOnlySectionIds = new HashSet<String>();
	}

	public void addReadOnlySectionId(String sectionId) {
		readOnlySectionIds.add(sectionId);
	}

	public Set<String> getReadOnlySectionIds() {
		return readOnlySectionIds;
	}

	public void clearAllRestrictions() {
		super.clearAllRestrictions();
		readOnlySectionIds.clear();
	}
}
