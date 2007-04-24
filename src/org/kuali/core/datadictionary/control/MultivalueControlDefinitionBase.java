/*
 * Copyright 2005-2006 The Kuali Foundation.
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

package org.kuali.core.datadictionary.control;

import org.kuali.core.datadictionary.ValidationCompletionUtils;
import org.kuali.core.datadictionary.exception.ClassValidationException;
import org.kuali.core.datadictionary.exception.CompletionException;
import org.kuali.core.lookup.keyvalues.KeyValuesFinder;

/**
 * A single HTML select control.
 * 
 */
public abstract class MultivalueControlDefinitionBase extends ControlDefinitionBase {
    /**
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        super.completeValidation(rootBusinessObjectClass, otherBusinessObjectClass, validationCompletionUtils);

        Class valuesFinder = getValuesFinderClass();
        if (valuesFinder == null) {
            throw new CompletionException("error validating " + rootBusinessObjectClass.getName() + " control: keyValuesFinder was never set (" + getParseLocation() + ")");
        }
        if (!KeyValuesFinder.class.isAssignableFrom(valuesFinder)) {
            throw new ClassValidationException("error validating " + rootBusinessObjectClass.getName() + " control: class '" + valuesFinder.getName() + "' is not a KeyValuesFinder subclass (" + getParseLocation() + ")");
        }
    }
}