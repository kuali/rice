/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kns.document;

import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;

import javax.persistence.MappedSuperclass;

/**
 * This is the base class implementation for all transaction processing eDocs. 
 */
@MappedSuperclass
public abstract class TransactionalDocumentBase extends DocumentBase implements TransactionalDocument,  SessionDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionalDocumentBase.class);

    /**
     * Default constructor.
     */
    public TransactionalDocumentBase() {
        super();
    }

    /**
     * @see org.kuali.rice.kns.document.TransactionalDocument#getAllowsCopy()
     * Checks if copy is set to true in data dictionary and the document instance implements
     * Copyable.
     */
    public boolean getAllowsCopy() {
        return KNSServiceLocatorWeb.getTransactionalDocumentDictionaryService().getAllowsCopy(this).booleanValue() && this instanceof Copyable;
    }

    /**
     * 
     * This method to check whether the document class implements SessionDocument
     * 
     * @return
     */
    public boolean isSessionDocument() {
        return SessionDocument.class.isAssignableFrom(this.getClass());
    }

    
}
