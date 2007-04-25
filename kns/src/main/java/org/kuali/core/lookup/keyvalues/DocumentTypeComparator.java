/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.lookup.keyvalues;

import java.util.Comparator;

import org.kuali.core.bo.DocumentType;

public class DocumentTypeComparator implements Comparator {

    public DocumentTypeComparator() {
        super();
    }

    public int compare(Object o1, Object o2) {

        DocumentType dt1 = (DocumentType) o1;
        DocumentType dt2 = (DocumentType) o2;
        return dt1.getFinancialDocumentTypeCode().compareToIgnoreCase(dt2.getFinancialDocumentTypeCode());
    }

}
