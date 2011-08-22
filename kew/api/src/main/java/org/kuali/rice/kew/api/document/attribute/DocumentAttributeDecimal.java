package org.kuali.rice.kew.api.document.attribute;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * A document attribute which contains decimal data.  Construct instances of {@code DocumentAttributeDecimal} using
 * it's builder or the {@link DocumentAttributeFactory}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = DocumentAttributeDecimal.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentAttributeDecimal.Constants.TYPE_NAME, propOrder = {
    DocumentAttributeDecimal.Elements.VALUE
})
public final class DocumentAttributeDecimal extends DocumentAttribute {

    @XmlElement(name = Elements.VALUE, required = false)
    private final BigDecimal value;

    @SuppressWarnings("unused")
    private DocumentAttributeDecimal() {
        this.value = null;
    }

    private DocumentAttributeDecimal(Builder builder) {
        super(builder.getName());
        this.value = builder.getValue();
    }

    @Override
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public DocumentAttributeDataType getDataType() {
        return DocumentAttributeDataType.DECIMAL;
    }

    public static final class Builder extends AbstractBuilder<BigDecimal> {

        private Builder(String name) {
            super(name);
        }

        public static Builder create(String name) {
            return new Builder(name);
        }

        @Override
        public DocumentAttributeDataType getDataType() {
            return DocumentAttributeDataType.DECIMAL;
        }

        @Override
        public DocumentAttributeDecimal build() {
            return new DocumentAttributeDecimal(this);
        }

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
