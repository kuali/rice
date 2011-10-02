package org.kuali.rice.kew.api.document;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

@XmlRootElement(name = DocumentProcessingOptions.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentProcessingOptions.Constants.TYPE_NAME, propOrder = {
        DocumentProcessingOptions.Elements.RUN_POST_PROCESSOR,
        DocumentProcessingOptions.Elements.INDEX_SEARCH_ATTRIBUTES,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentProcessingOptions extends AbstractDataTransferObject {

    @XmlElement(name = Elements.RUN_POST_PROCESSOR, required = true)
    private final boolean runPostProcessor;

    @XmlElement(name = Elements.INDEX_SEARCH_ATTRIBUTES, required = true)
    private final boolean indexSearchAttributes;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    public DocumentProcessingOptions() {
        this(true, true);
    }

    public DocumentProcessingOptions(boolean runPostProcessor, boolean indexSearchAttributes) {
        this.runPostProcessor = runPostProcessor;
        this.indexSearchAttributes = indexSearchAttributes;
    }

    public boolean isRunPostProcessor() {
        return runPostProcessor;
    }

    public boolean isIndexSearchAttributes() {
        return indexSearchAttributes;
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentProcessingOptions";
        final static String TYPE_NAME = "DocumentProcessingOptionsType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String RUN_POST_PROCESSOR = "runPostProcessor";
        final static String INDEX_SEARCH_ATTRIBUTES = "indexSearchAttributes";
    }
    
}
