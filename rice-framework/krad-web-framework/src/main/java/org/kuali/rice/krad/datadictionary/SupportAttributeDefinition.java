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

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.util.ExternalizableBusinessObjectUtils;

/**
 * Support attributes define additional attributes that can be used to generate
 * lookup field conversions and lookup parameters.
 *
 * Field conversions and lookup parameters are normally generated using foreign key relationships
 * defined within OJB and the DD.  Because Person objects are linked in a special way (i.e. they may
 * come from an external data source and not from the DB, such as LDAP), it is often necessary to define
 * extra fields that are related to each other, sort of like a supplemental foreign key.
 *
 * sourceName is the name of the POJO property of the business object
 * targetName is the name of attribute that corresponds to the sourceName in the looked up BO
 * identifier when true, only the field marked as an identifier will be passed in as a lookup parameter
 * at most one supportAttribute for each relationship should be defined as identifier="true"
 */
@BeanTag(name = "supportAttributeDefinition")
public class SupportAttributeDefinition extends PrimitiveAttributeDefinition {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SupportAttributeDefinition.class);
    private static final long serialVersionUID = -1719022365280776405L;

    protected boolean identifier;

    public SupportAttributeDefinition() {}

    @BeanTagAttribute(name = "identifier")
    public boolean isIdentifier() {
        return identifier;
    }

    /**
     * identifier when true, only the field marked as an identifier will be passed in as a lookup parameter
     * at most one supportAttribute for each relationship should be defined as identifier="true"
     */
    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    /**
     * Directly validate simple fields.
     *
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        completeValidation(rootBusinessObjectClass, otherBusinessObjectClass, new ValidationTrace());
    }

    /**
     * Directly validate simple fields
     *
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntry#completeValidation(org.kuali.rice.krad.datadictionary.validator.ValidationTrace)
     */
    @Override
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass,
            ValidationTrace tracer) {
        tracer.addBean(this.getClass().getSimpleName(), ValidationTrace.NO_BEAN_ID);
        try {
            if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, getSourceName())) {
                String currentValues[] = {"attribute = " + getSourceName(), "class = " + rootBusinessObjectClass};
                tracer.createError("Unable to find attribute in class", currentValues);
            }
            if (!DataDictionary.isPropertyOf(otherBusinessObjectClass, getTargetName())
                    && !ExternalizableBusinessObjectUtils.isExternalizableBusinessObjectInterface(
                    otherBusinessObjectClass)) {

                String currentValues[] = {"attribute = " + getTargetName(), "class = " + otherBusinessObjectClass};
                tracer.createError("Unable to find attribute in class", currentValues);
            }
        } catch (RuntimeException ex) {
            String currentValues[] = {"Exception = " + ex.getMessage()};
            tracer.createError("Unable to validate attribute", currentValues);
            LOG.error( "Exception while validating SupportAttributeDefintion on " + rootBusinessObjectClass + ": " + this, ex);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SupportAttributeDefinition [identifier=").append(this.identifier).append(", sourceName=")
                .append(this.sourceName).append(", targetName=").append(this.targetName).append("]");
        return builder.toString();
    }

}

