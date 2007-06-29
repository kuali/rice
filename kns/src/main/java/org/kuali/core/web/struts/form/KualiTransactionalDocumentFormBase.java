/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.web.struts.form;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.document.authorization.TransactionalDocumentActionFlags;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.rice.KNSServiceLocator;


/**
 * This class is the base action form for all transactional documents.
 */
public class KualiTransactionalDocumentFormBase extends KualiDocumentFormBase {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6463383454050206811L;
	protected Map forcedReadOnlyFields;

    /**
     * This constructor sets up empty instances for the dependent objects...
     */
    public KualiTransactionalDocumentFormBase() {
        super();

        // create a blank TransactionalDocumentActionFlags instance, since form-recreation needs it
        setDocumentActionFlags(new TransactionalDocumentActionFlags());
        forcedReadOnlyFields = new HashMap();
    }

    /**
     * This method retrieves an instance of the form.
     * 
     * @return
     */
    public TransactionalDocument getTransactionalDocument() {
        return (TransactionalDocument) getDocument();
    }
    

    /**
     * Locates the <code>DictionaryService</code> to discover the type name of the document.
     * 
     * @return
     */
    protected String discoverDocumentTypeName() {
        return ((DataDictionaryService) KNSServiceLocator.getDataDictionaryService()).getDataDictionary().getDocumentEntry(getDocument().getClass().getName()).getDocumentTypeName();
    }

    /**
     * This method formats the given java.sql.Date as MMM d, yyyy.
     * 
     * @param reversalDate
     * 
     * @return String
     */
    protected static String formatReversalDate(java.sql.Date reversalDate) {
        if (reversalDate == null) {
            return "";
        }
        // new for thread safety
        return KNSServiceLocator.getDateTimeService().toString(reversalDate, "MMM d, yyyy");
    }

    /**
     * Gets the forcedReadOnlyFields attribute.
     * 
     * @return Returns the forcedReadOnlyFields.
     */
    public Map getForcedReadOnlyFields() {
        return forcedReadOnlyFields;
    }

    /**
     * Sets the forcedReadOnlyFields attribute value.
     * 
     * @param forcedReadOnlyFields The forcedReadOnlyFields to set.
     */
    public void setForcedReadOnlyFields(Map forcedReadOnlyFields) {
        this.forcedReadOnlyFields = forcedReadOnlyFields;
    }
}
