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
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a simple validation result.  Includes the name of the field and the error message.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = ValidationResult.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ValidationResult.Constants.TYPE_NAME, propOrder = {
    ValidationResult.Elements.FIELD_NAME,
    ValidationResult.Elements.ERROR_MESSAGE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class ValidationResult
    extends AbstractDataTransferObject
    implements ValidationResultContract {

    @XmlElement(name = Elements.FIELD_NAME, required = true)
	private final String fieldName;
    @XmlElement(name = Elements.ERROR_MESSAGE, required = false)
	private final String errorMessage;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private ValidationResult() {
        this.fieldName = null;
        this.errorMessage = null;
    }

    private ValidationResult(Builder builder) {
        this.fieldName = builder.getFieldName();
        this.errorMessage = builder.getErrorMessage();
    }

    @Override
	public String getErrorMessage() {
		return errorMessage;
	}

    @Override
	public String getFieldName() {
		return fieldName;
	}

    /**
     * A builder which can be used to construct {@link ValidationResult} instances.  Enforces the constraints of the {@link ValidationResultContract}.
     *
     */
    public final static class Builder
        implements Serializable, ModelBuilder, ValidationResultContract
    {

        private String fieldName;
	    private String errorMessage;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(ValidationResultContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setFieldName(contract.getFieldName());
            builder.setErrorMessage(contract.getErrorMessage());
            return builder;
        }

        public static Builder create(String fieldName, String errorMessage) {
            if (fieldName == null) {
                throw new IllegalArgumentException("fieldName was null");
            }
            Builder builder = create();
            builder.setFieldName(fieldName);
            builder.setErrorMessage(errorMessage);
            return builder;
        }

        public ValidationResult build() {
            return new ValidationResult(this);
        }

        @Override
        public String getFieldName() {
            return this.fieldName;
        }

        @Override
        public String getErrorMessage() {
            return this.errorMessage;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "validationResult";
        final static String TYPE_NAME = "ValidationResultType";
    }
    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String FIELD_NAME = "fieldName";
        final static String ERROR_MESSAGE = "errorMessage";
    }
}