/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary;

import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.DocumentPresentationController;
import org.kuali.rice.kns.rule.PromptBeforeValidation;
import org.kuali.rice.kns.web.derivedvaluesetter.DerivedValuesSetter;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntry;
import org.springframework.beans.factory.InitializingBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public interface KNSDocumentEntry extends DataDictionaryEntry, Serializable, InitializingBean {
    List<HeaderNavigation> getHeaderNavigationList();

    List<String> getWebScriptFiles();

    Class<? extends PromptBeforeValidation> getPromptBeforeValidationClass();

    void setPromptBeforeValidationClass(Class<? extends PromptBeforeValidation> preRulesCheckClass);

    void setWebScriptFiles(List<String> webScriptFiles);

    void setHeaderNavigationList(List<HeaderNavigation> headerNavigationList);

    boolean isSessionDocument();

    void setSessionDocument(boolean sessionDocument);

    Class<? extends DerivedValuesSetter> getDerivedValuesSetterClass();

    void setDerivedValuesSetterClass(Class<? extends DerivedValuesSetter> derivedValuesSetter);

    public Class<? extends DocumentAuthorizer> getDocumentAuthorizerClass();

    public Class<? extends DocumentPresentationController> getDocumentPresentationControllerClass();


}
