/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary;

import org.kuali.rice.kns.rule.PromptBeforeValidation;
import org.kuali.rice.kns.web.derivedvaluesetter.DerivedValuesSetter;
import org.kuali.rice.krad.document.authorization.DocumentAuthorizer;
import org.kuali.rice.krad.document.authorization.DocumentPresentationController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public class DocumentEntry extends org.kuali.rice.krad.datadictionary.DocumentEntry {

    protected Class<? extends PromptBeforeValidation> promptBeforeValidationClass;
    protected Class<? extends DerivedValuesSetter> derivedValuesSetterClass;
    protected List<String> webScriptFiles = new ArrayList<String>(3);
    protected List<HeaderNavigation> headerNavigationList = new ArrayList<HeaderNavigation>();

    protected boolean sessionDocument = false;

    public List<HeaderNavigation> getHeaderNavigationList() {
        return headerNavigationList;
    }

    public List<String> getWebScriptFiles() {
        return webScriptFiles;
    }

    /**
     * @return Returns the preRulesCheckClass.
     */
    public Class<? extends PromptBeforeValidation> getPromptBeforeValidationClass() {
        return promptBeforeValidationClass;
    }

    /**
     * The promptBeforeValidationClass element is the full class name of the java
     * class which determines whether the user should be asked any questions prior to running validation.
     *
     * @see KualiDocumentActionBase#promptBeforeValidation(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, String)
     */
    public void setPromptBeforeValidationClass(Class<? extends PromptBeforeValidation> preRulesCheckClass) {
        this.promptBeforeValidationClass = preRulesCheckClass;
    }

    /**
     * The webScriptFile element defines the name of javascript files
     * that are necessary for processing the document.  The specified
     * javascript files will be included in the generated html.
     */
    public void setWebScriptFiles(List<String> webScriptFiles) {
        this.webScriptFiles = webScriptFiles;
    }

    /**
     * The headerNavigation element defines a set of additional
     * tabs which will appear on the document.
     */
    public void setHeaderNavigationList(List<HeaderNavigation> headerNavigationList) {
        this.headerNavigationList = headerNavigationList;
    }

    public boolean isSessionDocument() {
        return this.sessionDocument;
    }

    public void setSessionDocument(boolean sessionDocument) {
        this.sessionDocument = sessionDocument;
    }

    /**
     * @return the derivedValuesSetter
     */
    public Class<? extends DerivedValuesSetter> getDerivedValuesSetterClass() {
        return this.derivedValuesSetterClass;
    }

    /**
     * @param derivedValuesSetter the derivedValuesSetter to set
     */
    public void setDerivedValuesSetterClass(Class<? extends DerivedValuesSetter> derivedValuesSetter) {
        this.derivedValuesSetterClass = derivedValuesSetter;
    }
}
