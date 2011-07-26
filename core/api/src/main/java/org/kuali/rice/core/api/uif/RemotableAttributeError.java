package org.kuali.rice.core.api.uif;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.w3c.dom.Element;

@XmlRootElement(name = RemotableAttributeError.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableAttributeError.Constants.TYPE_NAME,
        propOrder = {RemotableAttributeError.Elements.ATTRIBUTE_NAME, RemotableAttributeError.Elements.ERRORS,
                CoreConstants.CommonElements.FUTURE_ELEMENTS})
public class RemotableAttributeError extends AbstractDataTransferObject implements AttributeError {

    @XmlElement(name = Elements.ATTRIBUTE_NAME, required = false)
    private final String attributeName;
    @XmlElement(name = Elements.ERRORS, required = false)
    private final List<String> errors;
    @SuppressWarnings("unused") @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private RemotableAttributeError() {
        this.attributeName = null;
        this.errors = null;
    }

    private RemotableAttributeError(Builder builder) {
        this.attributeName = builder.getAttributeName();
        this.errors = builder.getErrors();
    }

    @Override
    public String getAttributeName() {
        return this.attributeName;
    }

    @Override
    public List<String> getErrors() {
        return this.errors;
    }

    /**
     * A builder which can be used to construct {@link RemotableAttributeError} instances.  Enforces the constraints of
     * the {@link AttributeError}.
     */
    public static final class Builder implements Serializable, ModelBuilder, AttributeError {

        private String attributeName;
        private List<String> errors = new ArrayList<String>();

        private Builder(String attributeName) {
            this.attributeName = attributeName;
        }

        public static Builder create(String attributeName) {
            return new Builder(attributeName);
        }

        public static Builder create(AttributeError contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(contract.getAttributeName());
            builder.setErrors(contract.getErrors());
            return builder;
        }

        public RemotableAttributeError build() {
            if (errors.isEmpty()) {
                throw new IllegalStateException("must contain at least one error");
            }

            for (String err : errors) {
                if (StringUtils.isBlank(err)) {
                    throw new IllegalStateException("contains a blank error");
                }
            }

            return new RemotableAttributeError(this);
        }

        @Override
        public String getAttributeName() {
            return this.attributeName;
        }

        @Override
        public List<String> getErrors() {
            return Collections.unmodifiableList(this.errors);
        }

        public void setAttributeName(String attributeName) {
            if (StringUtils.isBlank(attributeName)) {
                throw new IllegalArgumentException("attributeName is blank");
            }

            this.attributeName = attributeName;
        }

        public void setErrors(List<String> errors) {
            if (errors == null) {
                throw new IllegalArgumentException("errors is null");
            }

            this.errors = new ArrayList<String>(errors);
        }

        /**
         * Adds errors to the AttributeError.  The passed in errors cannot be null.
         *
         * @param firstError the first error to add
         * @param restErrors any subsequent errors to add
         */
        public void addErrors(String firstError, String... restErrors) {
            if (firstError == null) {
                throw new IllegalArgumentException("errors is null");
            }

            this.errors.add(firstError);

            if (restErrors != null) {
                this.errors.addAll(Arrays.asList(restErrors));
            }
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "attributeError";
        final static String TYPE_NAME = "attributeErrorType";

    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled
     * to XML.
     */
    static class Elements {

        final static String ATTRIBUTE_NAME = "attributeName";
        final static String ERRORS = "errors";

    }

}