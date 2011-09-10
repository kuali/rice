/*
 * Copyright 2007-2009 The Kuali Foundation
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

import java.util.List;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kns.web.ui.Row;

/**
 * Used by the KNS lookup framework to produce rows to render for document search.
 *
 * <p>Client applications should not need to implement this interface, it is intended for internal use only.</p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface DocumentLookupCriteriaProcessor {

	public List<Row> getRows(DocumentType documentType, List<Row> defaultRows, boolean detailed, boolean superSearch);

}
