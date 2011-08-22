package org.kuali.rice.kew.api.document.lookup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContract;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeContract;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeFactory;
import org.w3c.dom.Element;

@XmlRootElement(name = DocumentLookupResult.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentLookupResult.Constants.TYPE_NAME, propOrder = {
    DocumentLookupResult.Elements.DOCUMENT,
    DocumentLookupResult.Elements.DOCUMENT_ATTRIBUTES,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentLookupResult extends AbstractDataTransferObject implements DocumentLookupResultContract {

    @XmlElement(name = Elements.DOCUMENT, required = false)
    private final Document document;

    @XmlElementWrapper(name = Elements.DOCUMENT_ATTRIBUTES, required = true)
    @XmlElement(name = Elements.DOCUMENT_ATTRIBUTE, required = false)
    private final List<DocumentAttribute> documentAttributes;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private DocumentLookupResult() {
        this.document = null;
        this.documentAttributes = null;
    }

    private DocumentLookupResult(Builder builder) {
        this.document = builder.getDocument().build();
        List<DocumentAttribute> documentAttributes = new ArrayList<DocumentAttribute>();
        for (DocumentAttribute.AbstractBuilder<?> documentAttribute : builder.getDocumentAttributes()) {
            documentAttributes.add(documentAttribute.build());
        }
        this.documentAttributes = Collections.unmodifiableList(documentAttributes);
    }

    @Override
    public Document getDocument() {
        return this.document;
    }

    @Override
    public List<DocumentAttribute> getDocumentAttributes() {
        return this.documentAttributes;
    }

    /**
     * A builder which can be used to construct {@link DocumentLookupResult} instances.  Enforces the constraints of the {@link DocumentLookupResultContract}.
     */
    public final static class Builder implements Serializable, ModelBuilder, DocumentLookupResultContract {

        private Document.Builder document;
        private List<DocumentAttribute.AbstractBuilder<?>> documentAttributes;

        private Builder(Document.Builder document) {
            setDocument(document);
            setDocumentAttributes(new ArrayList<DocumentAttribute.AbstractBuilder<?>>());
        }

        public static Builder create(Document.Builder document) {
            return new Builder(document);
        }

        public static Builder create(DocumentLookupResultContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Document.Builder documentBuilder = Document.Builder.create(contract.getDocument());
            Builder builder = create(documentBuilder);
            List<DocumentAttribute.AbstractBuilder<?>> documentAttributes = new ArrayList<DocumentAttribute.AbstractBuilder<?>>();
            for (DocumentAttributeContract documentAttributeContract : contract.getDocumentAttributes()) {
                documentAttributes.add(DocumentAttributeFactory.loadContractIntoBuilder(documentAttributeContract));
            }
            builder.setDocumentAttributes(documentAttributes);
            return builder;
        }

        public DocumentLookupResult build() {
            return new DocumentLookupResult(this);
        }

        @Override
        public Document.Builder getDocument() {
            return this.document;
        }

        @Override
        public List<DocumentAttribute.AbstractBuilder<?>> getDocumentAttributes() {
            return this.documentAttributes;
        }

        public void setDocument(Document.Builder document) {
            this.document = document;
        }

        public void setDocumentAttributes(List<DocumentAttribute.AbstractBuilder<?>> documentAttributes) {
            this.documentAttributes = documentAttributes;
        }

    }


    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentLookupResult";
        final static String TYPE_NAME = "DocumentLookupResultType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String DOCUMENT = "document";
        final static String DOCUMENT_ATTRIBUTES = "documentAttributes";
        final static String DOCUMENT_ATTRIBUTE = "documentAttribute";
    }

}
