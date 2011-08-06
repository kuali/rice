package org.kuali.rice.kew.api.document.lookup;

import java.io.Serializable;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.w3c.dom.Element;

@XmlRootElement(name = DocumentLookupResultData.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentLookupResultData.Constants.TYPE_NAME, propOrder = {
    DocumentLookupResultData.Elements.DOCUMENT_ATTRIBUTE,
    DocumentLookupResultData.Elements.DISPLAY_VALUE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentLookupResultData<T> extends AbstractDataTransferObject implements DocumentLookupResultDataContract<T> {

    @XmlElement(name = Elements.DOCUMENT_ATTRIBUTE, required = true)
    private final DocumentAttribute<T> documentAttribute;

    @XmlElement(name = Elements.DISPLAY_VALUE, required = false)
    private final String displayValue;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private DocumentLookupResultData() {
        this.documentAttribute = null;
        this.displayValue = null;
    }

    private DocumentLookupResultData(Builder builder) {
        this.documentAttribute = builder.getDocumentAttribute();
        this.displayValue = builder.getDisplayValue();
    }

    @Override
    public DocumentAttribute<T> getDocumentAttribute() {
        return this.documentAttribute;
    }

    @Override
    public String getDisplayValue() {
        return this.displayValue;
    }

    /**
     * A builder which can be used to construct {@link DocumentLookupResultData} instances.  Enforces the constraints of the {@link DocumentLookupResultDataContract}.
     */
    public final static class Builder<T> implements Serializable, ModelBuilder, DocumentLookupResultDataContract<T> {

        private DocumentAttribute<T> documentAttribute;
        private String displayValue;

        private Builder(DocumentAttribute<T> documentAttribute) {
            setDocumentAttribute(documentAttribute);
        }

        public static <T> Builder<T> create(DocumentAttribute<T> documentAttribute) {
            return new Builder<T>(documentAttribute);
        }

        public static <T> Builder<T> create(DocumentLookupResultDataContract<T> contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder<T> builder = create(contract.getDocumentAttribute());
            builder.setDisplayValue(contract.getDisplayValue());
            return builder;
        }

        public DocumentLookupResultData<T> build() {
            return new DocumentLookupResultData(this);
        }

        @Override
        public DocumentAttribute<T> getDocumentAttribute() {
            return this.documentAttribute;
        }

        @Override
        public String getDisplayValue() {
            return this.displayValue;
        }

        public void setDocumentAttribute(DocumentAttribute<T> documentAttribute) {
            if (documentAttribute == null) {
                throw new IllegalArgumentException("documentAttribute was null");
            }
            this.documentAttribute = documentAttribute;
        }

        public void setDisplayValue(String displayValue) {
            this.displayValue = displayValue;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentLookupResultData";
        final static String TYPE_NAME = "DocumentLookupResultDataType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String DOCUMENT_ATTRIBUTE = "documentAttribute";
        final static String DISPLAY_VALUE = "displayValue";
    }

}