package org.kuali.rice.kew.framework.document.lookup;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
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

@XmlRootElement(name = DocumentLookupCriteriaConfiguration.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentLookupCriteriaConfiguration.Constants.TYPE_NAME, propOrder = {
    DocumentLookupCriteriaConfiguration.Elements.SEARCH_ATTRIBUTE_FIELDS,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentLookupCriteriaConfiguration extends AbstractDataTransferObject implements DocumentLookupCriteriaConfigurationContract {

    private static final long serialVersionUID = -5764134034667636217L;

    @XmlElementWrapper(name = Elements.SEARCH_ATTRIBUTE_FIELDS, required = false)
    @XmlElement(name = Elements.ATTRIBUTE_FIELDS, required = false)
    private final List<AttributeFields> searchAttributeFields;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    @SuppressWarnings("unused")
    private DocumentLookupCriteriaConfiguration() {
        this.searchAttributeFields = null;
    }

    private DocumentLookupCriteriaConfiguration(Builder builder) {
        if (builder.getSearchAttributeFields() == null) {
            this.searchAttributeFields = Collections.emptyList();
        } else {
            this.searchAttributeFields = Collections.unmodifiableList(new ArrayList<AttributeFields>(builder.getSearchAttributeFields()));
        }
    }

    @Override
    public List<AttributeFields> getSearchAttributeFields() {
        return this.searchAttributeFields;
    }

    public List<RemotableAttributeField> getFlattenedSearchAttributeFields() {
        List<RemotableAttributeField> searchAttributeFields = new ArrayList<RemotableAttributeField>();
        for (AttributeFields attributeFields : getSearchAttributeFields()) {
            searchAttributeFields.addAll(attributeFields.getRemotableAttributeFields());
        }
        return searchAttributeFields;
    }

    /**
     * A builder which can be used to construct {@link DocumentLookupCriteriaConfiguration} instances.  Enforces the constraints of the {@link DocumentLookupCriteriaConfigurationContract}.
     */
    public final static class Builder implements Serializable, ModelBuilder, DocumentLookupCriteriaConfigurationContract {

        private List<AttributeFields> searchAttributeFields;

        private Builder() {
            setSearchAttributeFields(new ArrayList<AttributeFields>());
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(DocumentLookupCriteriaConfigurationContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setSearchAttributeFields(contract.getSearchAttributeFields());
            return builder;
        }

        public DocumentLookupCriteriaConfiguration build() {
            return new DocumentLookupCriteriaConfiguration(this);
        }

        @Override
        public List<AttributeFields> getSearchAttributeFields() {
            return this.searchAttributeFields;
        }

        public void setSearchAttributeFields(List<AttributeFields> searchAttributeFields) {
            this.searchAttributeFields = searchAttributeFields;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentLookupCriteriaConfiguration";
        final static String TYPE_NAME = "DocumentLookupCriteriaConfigurationType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String SEARCH_ATTRIBUTE_FIELDS = "searchAttributeFields";
        final static String ATTRIBUTE_FIELDS = "attributeFields";
    }

}
