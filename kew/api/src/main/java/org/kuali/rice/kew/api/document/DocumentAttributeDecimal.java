package org.kuali.rice.kew.api.document;

/**
 * TODO...
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

@XmlRootElement(name = DocumentAttributeDecimal.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentAttributeDecimal.Constants.TYPE_NAME, propOrder = {
    DocumentAttributeDecimal.Elements.VALUE
})
public final class DocumentAttributeDecimal extends DocumentAttribute<BigDecimal> {

    @XmlElement(name = Elements.VALUE, required = false)
    private final BigDecimal value;

    /**
     * Private constructor used only by JAXB.
     */
    private DocumentAttributeDecimal() {
        this.value = null;
    }

    public DocumentAttributeDecimal(String name, BigDecimal value) {
        super(name);
        this.value = value;
    }

    public static DocumentAttributeDecimal create(String name, BigDecimal value) {
        return new DocumentAttributeDecimal(name, value);
    }

    @Override
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public DocumentAttributeDataType getDataType() {
        return DocumentAttributeDataType.DECIMAL;
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentAttributeDecimal";
        final static String TYPE_NAME = "DocumentAttributeDecimalType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String VALUE = "value";
    }

}
