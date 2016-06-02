/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.TransactionalDocumentAuthorizerBase;
import org.kuali.rice.krad.document.TransactionalDocumentBase;
import org.kuali.rice.krad.document.TransactionalDocumentPresentationControllerBase;

/**
 * Data dictionary entry class for {@link org.kuali.rice.krad.document.TransactionalDocument}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "transactionalDocumentEntry")
public class TransactionalDocumentEntry extends DocumentEntry {

    private static final long serialVersionUID = 5746921563371805425L;

    /**
     * Constructs this {@code TransactionalDocumentEntry} with document presentation and authorization defaults.
     */
    public TransactionalDocumentEntry() {
        super();

        setDocumentClass(getStandardDocumentBaseClass());
        documentAuthorizerClass = TransactionalDocumentAuthorizerBase.class;
        documentPresentationControllerClass = TransactionalDocumentPresentationControllerBase.class;
    }

    /**
     * Returns the default base class for a {@link org.kuali.rice.krad.document.TransactionalDocument}.
     *
     * @return the default base class for a {@link org.kuali.rice.krad.document.TransactionalDocument}
     */
    public Class<? extends Document> getStandardDocumentBaseClass() {
        return TransactionalDocumentBase.class;
    }

}