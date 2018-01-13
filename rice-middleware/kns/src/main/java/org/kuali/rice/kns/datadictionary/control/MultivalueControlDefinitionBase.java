/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary.control;

import org.kuali.rice.krad.datadictionary.exception.CompletionException;

/**
 * Base class for control which provide a list of values to choose between.
 *
 * @deprecated Only used by KNS classes, use KRAD.
 */
@Deprecated
public abstract class MultivalueControlDefinitionBase extends ControlDefinitionBase {
    private static final long serialVersionUID = -9164657952021540261L;

	/**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    @Override
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        super.completeValidation(rootBusinessObjectClass, otherBusinessObjectClass);

        String valuesFinder = getValuesFinderClass();
        if (valuesFinder == null) {
            throw new CompletionException("error validating " + rootBusinessObjectClass.getName() + " control: keyValuesFinder was never set (" + "" + ")");
        }
    }
}
