package org.kuali.rice.core.api.uif;

import java.io.Serializable;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.w3c.dom.Element;

/**
 * Immutable implementation of {@link LookupAttributeField}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = RemotableLookupAttributeField.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableLookupAttributeField.Constants.TYPE_NAME, propOrder = {
    RemotableLookupAttributeField.Elements.ATTRIBUTE_FIELD,
    RemotableLookupAttributeField.Elements.IN_CRITERIA,
    RemotableLookupAttributeField.Elements.IN_RESULTS,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class RemotableLookupAttributeField extends AbstractDataTransferObject implements LookupAttributeField {

    @XmlElement(name = Elements.ATTRIBUTE_FIELD, required = true)
    private final RemotableAttributeField attributeField;

    @XmlElement(name = Elements.IN_CRITERIA, required = true)
    private final boolean inCriteria;

    @XmlElement(name = Elements.IN_RESULTS, required = true)
    private final boolean inResults;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private RemotableLookupAttributeField() {
        this.attributeField = null;
        this.inCriteria = false;
        this.inResults = false;
    }

    private RemotableLookupAttributeField(Builder builder) {
        this.attributeField = builder.getAttributeField().build();
        this.inCriteria = builder.isInCriteria();
        this.inResults = builder.isInResults();
    }

    @Override
    public RemotableAttributeField getAttributeField() {
        return this.attributeField;
    }

    @Override
    public boolean isInCriteria() {
        return this.inCriteria;
    }

    @Override
    public boolean isInResults() {
        return this.inResults;
    }

    /**
     * A builder which can be used to construct {@link RemotableLookupAttributeField} instances.  Enforces the constraints of the {@link LookupAttributeField}.
     */
    public final static class Builder implements Serializable, ModelBuilder, LookupAttributeField {

        private RemotableAttributeField.Builder attributeField;
        private boolean inCriteria;
        private boolean inResults;

        private Builder(RemotableAttributeField.Builder attributeField) {
            setAttributeField(attributeField);
            setInCriteria(true);
            setInResults(true);
        }

        public static Builder create(RemotableAttributeField.Builder attributeField) {
            return new Builder(attributeField);
        }

        public static Builder create(LookupAttributeField contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(RemotableAttributeField.Builder.create(contract.getAttributeField()));
            builder.setInCriteria(contract.isInCriteria());
            builder.setInResults(contract.isInResults());
            return builder;
        }

        public RemotableLookupAttributeField build() {
            return new RemotableLookupAttributeField(this);
        }

        @Override
        public RemotableAttributeField.Builder getAttributeField() {
            return this.attributeField;
        }

        @Override
        public boolean isInCriteria() {
            return this.inCriteria;
        }

        @Override
        public boolean isInResults() {
            return this.inResults;
        }

        public void setAttributeField(RemotableAttributeField.Builder attributeField) {
            if (attributeField == null) {
                throw new IllegalArgumentException("attributeField was null");
            }
            this.attributeField = attributeField;
        }

        public void setInCriteria(boolean inCriteria) {
            this.inCriteria = inCriteria;
        }

        public void setInResults(boolean inResults) {
            this.inResults = inResults;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "remotableLookupAttributeField";
        final static String TYPE_NAME = "RemotableLookupAttributeFieldType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String ATTRIBUTE_FIELD = "attributeField";
        final static String IN_CRITERIA = "inCriteria";
        final static String IN_RESULTS = "inResults";
    }

}
