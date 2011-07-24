package org.kuali.rice.kew.api.document.attribute;

/**
 * TODO...
 */

import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = DocumentAttributeDateTime.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentAttributeDateTime.Constants.TYPE_NAME, propOrder = {
    DocumentAttributeDateTime.Elements.VALUE
})
public final class DocumentAttributeDateTime extends DocumentAttribute<DateTime> {

    @XmlElement(name = Elements.VALUE, required = false)
    private final DateTime value;

    /**
     * Private constructor used only by JAXB.
     */
    private DocumentAttributeDateTime() {
        this.value = null;
    }

    public DocumentAttributeDateTime(String name, DateTime value) {
        super(name);
        this.value = value;
    }

    public static DocumentAttributeDateTime create(String name, DateTime value) {
        return new DocumentAttributeDateTime(name, value);
    }

    @Override
    public DateTime getValue() {
        return value;
    }

    @Override
    public DocumentAttributeDataType getDataType() {
        return DocumentAttributeDataType.DATE_TIME;
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentAttributeDateTime";
        final static String TYPE_NAME = "DocumentAttributeDateTimeType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String VALUE = "value";
    }

}
