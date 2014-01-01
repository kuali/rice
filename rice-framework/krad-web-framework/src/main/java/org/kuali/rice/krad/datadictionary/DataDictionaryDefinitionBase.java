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
package org.kuali.rice.krad.datadictionary;

import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;

/**
 * Common base class for DataDictionaryDefinition types.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
abstract public class DataDictionaryDefinitionBase extends DictionaryBeanBase implements DataDictionaryDefinition {
    private static final long serialVersionUID = -2003626577498716712L;

    protected String id;
    protected boolean embeddedDataObjectMetadata = false;
    protected boolean generatedFromMetadata = false;

    public DataDictionaryDefinitionBase() {
    }

    @Override
    @BeanTagAttribute(name = "id")
    public String getId() {
        return this.id;
    }

    /**
     * A unique identifier for this data dictionary element.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * Returns true if the given object contains an embedded KRAD Data metadata object
     * which may be used for defaulting certain attributes.
     */
    public boolean hasEmbeddedDataObjectMetadata() {
        return embeddedDataObjectMetadata;
    }

    /**
     * Returns true if this data dictionary object was completely generated from
     * KRAD Data metadata.
     */
    public boolean wasGeneratedFromMetadata() {
        return generatedFromMetadata;
    }

    public void setEmbeddedDataObjectMetadata(boolean embeddedDataObjectMetadata) {
        this.embeddedDataObjectMetadata = embeddedDataObjectMetadata;
    }

    public void setGeneratedFromMetadata(boolean generatedFromMetadata) {
        this.generatedFromMetadata = generatedFromMetadata;
    }

    /**
     * Default implementation so that all subclasses do not need to implement this deprecated method.
     */
    @Override
    @Deprecated
    public void completeValidation(Class<?> rootBusinessObjectClass, Class<?> otherBusinessObjectClass) {
        completeValidation(rootBusinessObjectClass, otherBusinessObjectClass, new ValidationTrace());
    }

    /**
     * Empty implementation so that all subclasses do not need to implement this method if they have no local validation to perform.
     */
    @Override
    public void completeValidation(Class<?> rootBusinessObjectClass, Class<?> otherBusinessObjectClass, ValidationTrace tracer) {}
}
