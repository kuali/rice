package org.kuali.rice.kew.api.document;

/**
 * TODO...
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = DocumentAttributeString.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentAttributeString.Constants.TYPE_NAME, propOrder = {
    DocumentAttributeString.Elements.VALUE
})
public final class DocumentAttributeString extends DocumentAttribute<String> {

    @XmlElement(name = Elements.VALUE, required = false)
    private final String value;

    /**
     * Private constructor used only by JAXB.
     */
    private DocumentAttributeString() {
        this.value = null;
    }

    public DocumentAttributeString(String name, String value) {
        super(name);
        this.value = value;
    }

    public static DocumentAttributeString create(String name, String value) {
        return new DocumentAttributeString(name, value);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public DocumentAttributeDataType getDataType() {
        return DocumentAttributeDataType.STRING;
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentAttributeString";
        final static String TYPE_NAME = "DocumentAttributeStringType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String VALUE = "value";
    }

}
