/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.docsearch;

import edu.iu.uis.eden.doctype.DocumentType;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class CustomDocumentSearchGenerator extends StandardDocumentSearchGenerator {

	/* (non-Javadoc)
	 * @see edu.iu.uis.eden.docsearch.StandardDocumentSearchGenerator#addExtraDocumentTypesToSearch(java.lang.StringBuffer, edu.iu.uis.eden.doctype.DocumentType)
	 */
	@Override
	protected void addExtraDocumentTypesToSearch(StringBuffer whereSql, DocumentType docType) {
		if ("SearchDocType_DefaultCustomProcessor_2".equals(docType.getName())) {
			addDocumentTypeNameToSearchOn(whereSql, "SearchDocType_DefaultCustomProcessor");
		} else if ("SearchDocType_DefaultCustomProcessor".equals(docType.getName())) {
			addDocumentTypeNameToSearchOn(whereSql, "SearchDocType_DefaultCustomProcessor_2");
		}
	}
}
