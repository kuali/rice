package org.kuali.rice.kew.api.document.attribute;

/**
 * TODO...
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigInteger;

@XmlRootElement(name = DocumentAttributeInteger.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentAttributeInteger.Constants.TYPE_NAME, propOrder = {
    DocumentAttributeInteger.Elements.VALUE
})
public final class DocumentAttributeInteger extends DocumentAttribute<BigInteger> {

    @XmlElement(name = Elements.VALUE, required = false)
    private final BigInteger value;

    /**
     * Private constructor used only by JAXB.
     */
    private DocumentAttributeInteger() {
        this.value = null;
    }

    public DocumentAttributeInteger(String name, BigInteger value) {
        super(name);
        this.value = value;
    }

    public static DocumentAttributeInteger create(String name, BigInteger value) {
        return new DocumentAttributeInteger(name, value);
    }

    @Override
    public BigInteger getValue() {
        return value;
    }

    @Override
    public DocumentAttributeDataType getDataType() {
        return DocumentAttributeDataType.INTEGER;
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentAttributeInteger";
        final static String TYPE_NAME = "DocumentAttributeIntegerType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String VALUE = "value";
    }

}
