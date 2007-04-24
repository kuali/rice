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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.DocumentType;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.rice.KNSServiceLocator;

public class DocumentTypeValueFinder extends KeyValuesBase {

    /**
     * 
     * Constructs a DocumentTypeValueFinder.java.
     */
    public DocumentTypeValueFinder() {
        super();
    }

    public List getKeyValues() {

        // get a list of all DocumentTypes
        KeyValuesService boService = KNSServiceLocator.getKeyValuesService();
        List docTypes = (List) boService.findAll(DocumentType.class);

        // calling comparator.
        DocumentTypeComparator docTypeComparator = new DocumentTypeComparator();

        // sort using comparator.
        Collections.sort(docTypes, docTypeComparator);

        // create a new list (code, descriptive-name)
        List labels = new ArrayList();
        labels.add(new KeyLabelPair("", "")); // blank first entry
        labels.add(new KeyLabelPair("ALL", "ALL - All Document Types")); // ALL document entry

        for (Iterator iter = docTypes.iterator(); iter.hasNext();) {
            DocumentType docType = (DocumentType) iter.next();
            labels.add(new KeyLabelPair(docType.getFinancialDocumentTypeCode(), docType.getFinancialDocumentTypeCode() + " - " + docType.getFinancialDocumentName()));
        }

        return labels;
    }

}
