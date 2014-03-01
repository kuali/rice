/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.document;

import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

/**
 * Base class for transactional documents
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@MappedSuperclass
public abstract class TransactionalDocumentBase extends DocumentBase implements TransactionalDocument, SessionDocument {
    private static final long serialVersionUID = 1L;

    /**
     * EclipseLink static weaving does not weave MappedSuperclass unless an Entity or Embedded is
     * weaved which uses it, hence this class.
     */
    @Embeddable
    private static final class WeaveMe extends TransactionalDocumentBase {}

    // EclipseLink chokes with an NPE if a mapped superclass does not have any attributes.  This keeps it happy.
    @Transient
    transient private String eclipseLinkBugHackAttribute;
    
    /**
     * @see org.kuali.rice.krad.document.TransactionalDocument#getAllowsCopy()
     *      Checks if copy is set to true in data dictionary and the document instance implements
     *      Copyable.
     */
    @Override
    public boolean getAllowsCopy() {
        return this instanceof Copyable
                && KRADServiceLocatorWeb.getDocumentDictionaryService().getAllowsCopy(this).booleanValue();
    }

    /**
     * This method to check whether the document class implements SessionDocument
     *
     * @return true if the document is a session document
     */
    public boolean isSessionDocument() {
        return SessionDocument.class.isAssignableFrom(this.getClass());
    }
}
