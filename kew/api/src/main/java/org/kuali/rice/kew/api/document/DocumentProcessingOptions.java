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
        DocumentProcessingOptions.Elements.SEND_NOTIFICATIONS,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentProcessingOptions extends AbstractDataTransferObject {

    @XmlElement(name = Elements.RUN_POST_PROCESSOR, required = true)
    private final boolean runPostProcessor;

    @XmlElement(name = Elements.INDEX_SEARCH_ATTRIBUTES, required = true)
    private final boolean indexSearchAttributes;

    @XmlElement(name = Elements.SEND_NOTIFICATIONS, required = true)
    private final boolean sendNotifications;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    private DocumentProcessingOptions() {
        this(true, true, true);
    }

    private DocumentProcessingOptions(boolean runPostProcessor, boolean indexSearchAttributes, boolean sendNotifications) {
        this.runPostProcessor = runPostProcessor;
        this.indexSearchAttributes = indexSearchAttributes;
        this.sendNotifications = sendNotifications;
    }

    public static DocumentProcessingOptions create(boolean runPostProcessor, boolean indexSearchAttributes) {
        return create(runPostProcessor, indexSearchAttributes, true);
    }

    public static DocumentProcessingOptions create(boolean runPostProcessor, boolean indexSearchAttributes, boolean sendNotifications) {
        return new DocumentProcessingOptions(runPostProcessor, indexSearchAttributes, sendNotifications);
    }

    public static DocumentProcessingOptions createDefault() {
        return new DocumentProcessingOptions();
    }

    public boolean isRunPostProcessor() {
        return runPostProcessor;
    }

    public boolean isIndexSearchAttributes() {
        return indexSearchAttributes;
    }

    public boolean isSendNotifications() {
        return sendNotifications;
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
        final static String SEND_NOTIFICATIONS = "sendNotifications";
    }
    
}
