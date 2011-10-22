/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.docsearch;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.impl.document.search.DocumentSearchGeneratorImpl;

/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CustomDocumentSearchGeneratorImpl extends DocumentSearchGeneratorImpl {
	
	@Override
	public void addExtraDocumentTypesToSearch(StringBuilder whereSql, DocumentType docType) {
		if ("SearchDocType_DefaultCustomProcessor_2".equals(docType.getName())) {
			addDocumentTypeNameToSearchOn(whereSql, "SearchDocType_DefaultCustomProcessor");
		} else if ("SearchDocType_DefaultCustomProcessor".equals(docType.getName())) {
			addDocumentTypeNameToSearchOn(whereSql, "SearchDocType_DefaultCustomProcessor_2");
		}
	}

}
