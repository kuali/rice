/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import org.kuali.rice.krad.datadictionary.validation.ViewAttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.rice.krad.service.DictionaryValidationService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ViewValidationService;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;

/**
 * Implementation of Validation service for views, uses the same validation mechanisms as DictionaryValidationService
 * but uses a different AttributeValueReader to get the correct information from InputFields - which
 * include any AttributeDefinition defined attributes, if defined and not overriden
 *
 * @see ViewValidationService
 */
public class ViewValidationServiceImpl implements ViewValidationService {

    protected DictionaryValidationService dictionaryValidationService;

    @Override
    public DictionaryValidationResult validateView(ViewModel model) {
        return validateView(model.getPreviousView(), model);
    }

    @Override
    public DictionaryValidationResult validateView(View view, ViewModel model) {
        return getDictionaryValidationService().validate(new ViewAttributeValueReader(view, model), true);
    }

    public DictionaryValidationService getDictionaryValidationService() {
        if (dictionaryValidationService == null) {
            this.dictionaryValidationService = KRADServiceLocatorWeb.getDictionaryValidationService();
        }
        return dictionaryValidationService;
    }

    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }
}
