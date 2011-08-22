/*
 * Copyright 2005-2011 The Kuali Foundation
 *
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
package org.kuali.rice.kew.api.validation;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.kew.api.rule.RuleContract;
import org.kuali.rice.kew.api.rule.RuleDelegationContract;
import org.kuali.rice.kew.api.rule.RuleResponsibility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A set of results from validation of a field of data.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = ValidationResults.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ValidationResults.Constants.TYPE_NAME, propOrder = {
    ValidationResults.Elements.RESULTS,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class ValidationResults
    extends AbstractDataTransferObject
    implements ValidationResultsContract {

	public static final String GLOBAL = "org.kuali.rice.kew.api.validation.ValidationResults.GLOBAL";

    @XmlElement(name = Elements.RESULTS, required = false)
    private final List<ValidationResultContract> validationResults;

    /**
     * Private constructor used only by JAXB.
     */
    private ValidationResults() {
        this.validationResults = new ArrayList<ValidationResultContract>();
    }

    private ValidationResults(Builder builder) {
        this.validationResults = builder.getValidationResults();
    }

    @Override
	public List<ValidationResultContract> getValidationResults() {
		return validationResults;
	}

    /**
     * A builder which can be used to construct {@link ValidationResults} instances.  Enforces the constraints of the {@link ValidationResultsContract}.
     *
     */
    public final static class Builder
        implements Serializable, ModelBuilder, ValidationResultsContract
    {

        private List<ValidationResultContract> validationResults = new ArrayList<ValidationResultContract>();

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(ValidationResultsContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setValidationResults(contract.getValidationResults());
            return builder;
        }

        public ValidationResults build() {
            return new ValidationResults(this);
        }

        @Override
        public List<ValidationResultContract> getValidationResults() {
            return this.validationResults;
        }

        public void setValidationResults(List<ValidationResultContract> results) {
            this.validationResults = results;
        }

        /**
         * Convenience method for adding an error message
         * @param errorMessage
         */
        public void addValidationResult(String errorMessage) {
            addValidationResult(GLOBAL, errorMessage);
        }

        /**
         * Convenience method for adding an error message for a given field
         * @param errorMessage
         */
        public void addValidationResult(String fieldName, String errorMessage) {
            ValidationResult.Builder b = ValidationResult.Builder.create();
            b.setFieldName(fieldName);
            b.setErrorMessage(errorMessage);
            validationResults.add(b.build());
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "validationResults";
        final static String TYPE_NAME = "ValidationResultsType";
    }
    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String RESULTS = "results";
    }
}