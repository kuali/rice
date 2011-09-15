package org.kuali.rice.kew.framework.document.lookup;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectUtils;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An immutable data transfer object implementation of the {@link DocumentLookupResultValuesContract}.
 * Instances of this class should be constructed using the nested {@link Builder} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = DocumentLookupResultValues.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentLookupResultValues.Constants.TYPE_NAME, propOrder = {
    DocumentLookupResultValues.Elements.RESULT_VALUES,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentLookupResultValues extends AbstractDataTransferObject implements DocumentLookupResultValuesContract {

    @XmlElementWrapper(name = Elements.RESULT_VALUES, required = false)
    @XmlElement(name = Elements.RESULT_VALUE, required = false)
    private final List<DocumentLookupResultValue> resultValues;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    @SuppressWarnings("unused")
    private DocumentLookupResultValues() {
        this.resultValues = null;
    }

    private DocumentLookupResultValues(Builder builder) {
        this.resultValues = ModelObjectUtils.buildImmutableCopy(builder.getResultValues());
    }

    @Override
    public List<DocumentLookupResultValue> getResultValues() {
        return this.resultValues;
    }

    /**
     * A builder which can be used to construct {@link DocumentLookupResultValues} instances.  Enforces the
     * constraints of the {@link DocumentLookupResultValuesContract}.
     */
    public final static class Builder implements Serializable, ModelBuilder, DocumentLookupResultValuesContract {

        private List<DocumentLookupResultValue.Builder> resultValues;

        private Builder() {
            setResultValues(new ArrayList<DocumentLookupResultValue.Builder>());
        }

        /**
         * Creates new empty builder instance.  The various lists on this builder are initialized to empty lists.  The
         * internal list of result value builders is initialized to an empty list.
         *
         * @return a new empty builder instance
         */
        public static Builder create() {
            return new Builder();
        }

        /**
         * Creates a new builder instance initialized with copies of the properties from the given contract.
         *
         * @param contract the contract from which to copy properties
         *
         * @return a builder instance initialized with properties from the given contract
         *
         * @throws IllegalArgumentException if the given contract is null
         */
        public static Builder create(DocumentLookupResultValuesContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            if (!CollectionUtils.isEmpty(contract.getResultValues())) {
                for (DocumentLookupResultValueContract resultValueContract : contract.getResultValues()) {
                    //builder.getResultValues().add(DocumentLookupResultValue.Builder.create(resultValueContract));
                }
            }
            return builder;
        }

        @Override
        public DocumentLookupResultValues build() {
            return new DocumentLookupResultValues(this);
        }

        @Override
        public List<DocumentLookupResultValue.Builder> getResultValues() {
            return this.resultValues;
        }

        public void setResultValues(List<DocumentLookupResultValue.Builder> resultValues) {
            this.resultValues = resultValues;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentLookupResultValues";
        final static String TYPE_NAME = "DocumentLookupResultValuesType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String RESULT_VALUES = "resultValues";
        final static String RESULT_VALUE = "resultValue";
    }

}
