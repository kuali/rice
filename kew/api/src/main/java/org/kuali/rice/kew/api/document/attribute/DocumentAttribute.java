package org.kuali.rice.kew.api.document.attribute;

/**
 * TODO...
 */

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentAttribute.Constants.TYPE_NAME, propOrder = {
    DocumentAttribute.Elements.NAME,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
@XmlSeeAlso( { DocumentAttributeString.class, DocumentAttributeDateTime.class, DocumentAttributeInteger.class, DocumentAttributeDecimal.class } )
public abstract class DocumentAttribute<T> extends AbstractDataTransferObject {

    @XmlElement(name = Elements.NAME, required = true)
    private final String name;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    protected DocumentAttribute() {
        this.name = null;
    }

    DocumentAttribute(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name was null or blank");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract T getValue();

    public abstract DocumentAttributeDataType getDataType();

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String TYPE_NAME = "DocumentAttributeType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String NAME = "name";
    }

}
