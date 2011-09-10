package org.kuali.rice.kew.framework.document.lookup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectUtils;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeContract;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeFactory;
import org.w3c.dom.Element;

@XmlRootElement(name = DocumentLookupResultValue.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentLookupResultValue.Constants.TYPE_NAME, propOrder = {
    DocumentLookupResultValue.Elements.DOCUMENT_ID,
    DocumentLookupResultValue.Elements.DOCUMENT_ATTRIBUTES,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentLookupResultValue extends AbstractDataTransferObject
        implements DocumentLookupResultValueContract {

    @XmlElement(name = Elements.DOCUMENT_ID, required = true)
    private final String documentId;

    @XmlElementWrapper(name = Elements.DOCUMENT_ATTRIBUTES, required = false)
    @XmlElement(name = Elements.DOCUMENT_ATTRIBUTE, required = false)
    private final List<DocumentAttribute> documentAttributes;
    
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private DocumentLookupResultValue() {
        this.documentId = null;
        this.documentAttributes = null;
    }

    private DocumentLookupResultValue(Builder builder) {
        this.documentId = builder.getDocumentId();
        this.documentAttributes = ModelObjectUtils.buildImmutableCopy(builder.getDocumentAttributes());
    }

    @Override
    public String getDocumentId() {
        return this.documentId;
    }

    @Override
    public List<DocumentAttribute> getDocumentAttributes() {
        return this.documentAttributes;
    }

    /**
     * A builder which can be used to construct {@link DocumentLookupResultValue} instances.  Enforces the constraints
     * of the {@link DocumentLookupResultValueContract}.
     */
    public final static class Builder implements Serializable, ModelBuilder, DocumentLookupResultValueContract {

        private String documentId;
        private List<DocumentAttribute.AbstractBuilder<?>> documentAttributes;
        
        private Builder(String documentId) {
            setDocumentId(documentId);
            setDocumentAttributes(new ArrayList<DocumentAttribute.AbstractBuilder<?>>());
        }

        public static Builder create(String documentId) {
            return new Builder(documentId);
        }

        public static Builder create(DocumentLookupResultValueContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(contract.getDocumentId());
            if (contract.getDocumentAttributes() != null) {
                for (DocumentAttributeContract documentAttribute : contract.getDocumentAttributes()) {
                    builder.getDocumentAttributes().add(DocumentAttributeFactory.loadContractIntoBuilder(documentAttribute));
                }
            }
            return builder;
        }

        public DocumentLookupResultValue build() {
            return new DocumentLookupResultValue(this);
        }

        @Override
        public String getDocumentId() {
            return this.documentId;
        }

        @Override
        public List<DocumentAttribute.AbstractBuilder<?>> getDocumentAttributes() {
            return this.documentAttributes;
        }

        public void setDocumentId(String documentId) {
            if (StringUtils.isBlank(documentId)) {
                throw new IllegalArgumentException("documentId was null or blank");
            }
            this.documentId = documentId;
        }

        public void setDocumentAttributes(List<DocumentAttribute.AbstractBuilder<?>> documentAttributes) {
            this.documentAttributes = documentAttributes;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentLookupResultValue";
        final static String TYPE_NAME = "DocumentLookupResultValueType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled
     * to XML.
     */
    static class Elements {
        final static String DOCUMENT_ID = "documentId";
        final static String DOCUMENT_ATTRIBUTES = "documentAttributes";
        final static String DOCUMENT_ATTRIBUTE = "documentAttribute";
    }

}
