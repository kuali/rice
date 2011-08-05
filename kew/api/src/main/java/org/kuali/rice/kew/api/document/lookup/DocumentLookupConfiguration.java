package org.kuali.rice.kew.api.document.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.document.attribute.AttributeFields;
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
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = DocumentLookupConfiguration.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentLookupConfiguration.Constants.TYPE_NAME, propOrder = {
    DocumentLookupConfiguration.Elements.DOCUMENT_TYPE_NAME,
    DocumentLookupConfiguration.Elements.SEARCH_ATTRIBUTE_FIELDS,
    DocumentLookupConfiguration.Elements.RESULT_SET_ATTRIBUTE_FIELDS,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentLookupConfiguration extends AbstractDataTransferObject implements DocumentLookupConfigurationContract {

    private static final long serialVersionUID = -5764134034667636217L;
    
    @XmlElement(name = Elements.DOCUMENT_TYPE_NAME, required = true)
    private final String documentTypeName;

    @XmlElementWrapper(name = Elements.SEARCH_ATTRIBUTE_FIELDS, required = false)
    @XmlElement(name = Elements.ATTRIBUTE_FIELDS, required = false)
    private final List<AttributeFields> searchAttributeFields;

    @XmlElementWrapper(name = Elements.RESULT_SET_ATTRIBUTE_FIELDS, required = false)
    @XmlElement(name = Elements.ATTRIBUTE_FIELDS, required = false)
    private final List<AttributeFields> resultSetAttributeFields;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    @SuppressWarnings("unused")
    private DocumentLookupConfiguration() {
        this.documentTypeName = null;
        this.searchAttributeFields = null;
        this.resultSetAttributeFields = null;
    }

    private DocumentLookupConfiguration(Builder builder) {
        this.documentTypeName = builder.getDocumentTypeName();
        if (builder.getSearchAttributeFields() == null) {
            this.searchAttributeFields = Collections.emptyList();
        } else {
            this.searchAttributeFields = Collections.unmodifiableList(new ArrayList<AttributeFields>(builder.getSearchAttributeFields()));
        }
        if (builder.getResultSetAttributeFields() == null) {
            this.resultSetAttributeFields = Collections.emptyList();
        } else {
            this.resultSetAttributeFields = Collections.unmodifiableList(new ArrayList<AttributeFields>(
                    builder.getResultSetAttributeFields()));
        }
    }

    @Override
    public String getDocumentTypeName() {
        return documentTypeName;
    }

    @Override
    public List<AttributeFields> getSearchAttributeFields() {
        return this.searchAttributeFields;
    }

    @Override
    public List<AttributeFields> getResultSetAttributeFields() {
        return this.resultSetAttributeFields;
    }

    public List<RemotableAttributeField> getFlattenedSearchAttributeFields() {
        List<RemotableAttributeField> searchAttributeFields = new ArrayList<RemotableAttributeField>();
        for (AttributeFields attributeFields : getSearchAttributeFields()) {
            searchAttributeFields.addAll(attributeFields.getRemotableAttributeFields());
        }
        return searchAttributeFields;
    }

    public List<RemotableAttributeField> getFlattenedResultSetAttributeFields() {
        List<RemotableAttributeField> resultSetAttributeFields = new ArrayList<RemotableAttributeField>();
        for (AttributeFields attributeFields : getResultSetAttributeFields()) {
            resultSetAttributeFields.addAll(attributeFields.getRemotableAttributeFields());
        }
        return resultSetAttributeFields;
    }

    /**
     * A builder which can be used to construct {@link DocumentLookupConfiguration} instances.  Enforces the constraints of the {@link DocumentLookupConfigurationContract}.
     */
    public final static class Builder implements Serializable, ModelBuilder, DocumentLookupConfigurationContract {

        private String documentTypeName;
        private List<AttributeFields> searchAttributeFields;
        private List<AttributeFields> resultSetAttributeFields;

        private Builder(String documentTypeName) {
            setDocumentTypeName(documentTypeName);
            setSearchAttributeFields(new ArrayList<AttributeFields>());
            setResultSetAttributeFields(new ArrayList<AttributeFields>());
        }

        public static Builder create(String documentTypeName) {
            return new Builder(documentTypeName);
        }

        public static Builder create(DocumentLookupConfigurationContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(contract.getDocumentTypeName());
            builder.setSearchAttributeFields(contract.getSearchAttributeFields());
            builder.setResultSetAttributeFields(contract.getResultSetAttributeFields());
            return builder;
        }

        public DocumentLookupConfiguration build() {
            return new DocumentLookupConfiguration(this);
        }

        @Override
        public String getDocumentTypeName() {
            return this.documentTypeName;
        }

        @Override
        public List<AttributeFields> getSearchAttributeFields() {
            return this.searchAttributeFields;
        }

        @Override
        public List<AttributeFields> getResultSetAttributeFields() {
            return this.resultSetAttributeFields;
        }

        public void setDocumentTypeName(String documentTypeName) {
            if (StringUtils.isBlank(documentTypeName)) {
                throw new IllegalArgumentException("documentTypeName was null or blank");
            }
            this.documentTypeName = documentTypeName;
        }

        public void setSearchAttributeFields(List<AttributeFields> searchAttributeFields) {
            this.searchAttributeFields = searchAttributeFields;
        }

        public void setResultSetAttributeFields(List<AttributeFields> resultSetAttributeFields) {
            this.resultSetAttributeFields = resultSetAttributeFields;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentLookupConfiguration";
        final static String TYPE_NAME = "DocumentLookupConfigurationType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String DOCUMENT_TYPE_NAME = "documentTypeName";
        final static String SEARCH_ATTRIBUTE_FIELDS = "searchAttributeFields";
        final static String ATTRIBUTE_FIELDS = "attributeFields";
        final static String RESULT_SET_ATTRIBUTE_FIELDS = "resultSetAttributeFields";
    }

}
