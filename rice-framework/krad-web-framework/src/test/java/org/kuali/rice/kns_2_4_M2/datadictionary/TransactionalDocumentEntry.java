/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.kns_2_4_M2.datadictionary;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.krad.datadictionary.ReferenceDefinition;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public class TransactionalDocumentEntry extends org.kuali.rice.krad.datadictionary.TransactionalDocumentEntry implements KNSDocumentEntry {

    protected List<String> webScriptFiles = new ArrayList<String>(3);
    protected List<HeaderNavigation> headerNavigationList = new ArrayList<HeaderNavigation>();

    protected boolean sessionDocument = false;

    public TransactionalDocumentEntry() {
        super();

    }

    @Override
    public List<HeaderNavigation> getHeaderNavigationList() {
        return headerNavigationList;
    }

    @Override
    public List<String> getWebScriptFiles() {
        return webScriptFiles;
    }

    /**
     * The webScriptFile element defines the name of javascript files
     * that are necessary for processing the document.  The specified
     * javascript files will be included in the generated html.
     */
    @Override
    public void setWebScriptFiles(List<String> webScriptFiles) {
        this.webScriptFiles = webScriptFiles;
    }

    /**
     * The headerNavigation element defines a set of additional
     * tabs which will appear on the document.
     */
    @Override
    public void setHeaderNavigationList(List<HeaderNavigation> headerNavigationList) {
        this.headerNavigationList = headerNavigationList;
    }

    @Override
    public boolean isSessionDocument() {
        return this.sessionDocument;
    }

    @Override
    public void setSessionDocument(boolean sessionDocument) {
        this.sessionDocument = sessionDocument;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DocumentEntry#completeValidation()
     */
    @Override
    public void completeValidation() {
        super.completeValidation();
        for (ReferenceDefinition reference : defaultExistenceChecks) {
            reference.completeValidation(documentClass, null);
        }
    }

    @Override
    public String toString() {
        return "TransactionalDocumentEntry for documentType " + getDocumentTypeName();
    }
}
