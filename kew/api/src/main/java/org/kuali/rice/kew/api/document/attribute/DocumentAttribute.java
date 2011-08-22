package org.kuali.rice.kew.api.document.attribute;

/**
 * TODO...
 */

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupConfigurationContract;
import org.springframework.core.convert.converter.Converter;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentAttribute.Constants.TYPE_NAME, propOrder = {
    DocumentAttribute.Elements.NAME,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
@XmlSeeAlso( { DocumentAttributeString.class, DocumentAttributeDateTime.class, DocumentAttributeInteger.class, DocumentAttributeDecimal.class } )
public abstract class DocumentAttribute extends AbstractDataTransferObject implements DocumentAttributeContract {

    private static final long serialVersionUID = -1935235225791818090L;
    
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

    @Override
    public String getName() {
        return name;
    }

    public abstract static class AbstractBuilder<T> implements Serializable, ModelBuilder, DocumentAttributeContract {

        private static final long serialVersionUID = -4402662354421207678L;
        
        private String name;
        private T value;

        protected AbstractBuilder(String name) {
            setName(name);
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("name was null or blank");
            }
            this.name = name;
        }

        @Override
        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

        public abstract DocumentAttribute build();
        
    }

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
