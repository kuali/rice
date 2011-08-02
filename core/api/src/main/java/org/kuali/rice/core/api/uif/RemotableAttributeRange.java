package org.kuali.rice.core.api.uif;

import java.io.Serializable;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.w3c.dom.Element;

@XmlRootElement(name = RemotableAttributeRange.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableAttributeRange.Constants.TYPE_NAME, propOrder = {
    RemotableAttributeRange.Elements.LOWER_BOUND_NAME,
    RemotableAttributeRange.Elements.LOWER_BOUND_LABEL,
    RemotableAttributeRange.Elements.LOWER_BOUND_INCLUSIVE,
    RemotableAttributeRange.Elements.UPPER_BOUND_NAME,
    RemotableAttributeRange.Elements.UPPER_BOUND_LABEL,
    RemotableAttributeRange.Elements.UPPER_BOUND_INCLUSIVE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class RemotableAttributeRange extends AbstractDataTransferObject implements AttributeRange {

    @XmlElement(name = Elements.LOWER_BOUND_NAME, required = true)
    private final String lowerBoundName;

    @XmlElement(name = Elements.LOWER_BOUND_LABEL, required = false)
    private final String lowerBoundLabel;

    @XmlElement(name = Elements.LOWER_BOUND_INCLUSIVE, required = false)
    private final boolean lowerBoundInclusive;

    @XmlElement(name = Elements.UPPER_BOUND_NAME, required = true)
    private final String upperBoundName;

    @XmlElement(name = Elements.UPPER_BOUND_LABEL, required = false)
    private final String upperBoundLabel;

    @XmlElement(name = Elements.UPPER_BOUND_INCLUSIVE, required = false)
    private final boolean upperBoundInclusive;
    
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private RemotableAttributeRange() {
        this.lowerBoundName = null;
        this.lowerBoundLabel = null;
        this.lowerBoundInclusive = false;
        this.upperBoundName = null;
        this.upperBoundLabel = null;
        this.upperBoundInclusive = false;
    }

    private RemotableAttributeRange(Builder builder) {
        this.lowerBoundName = builder.getLowerBoundName();
        this.lowerBoundLabel = builder.getLowerBoundLabel();
        this.lowerBoundInclusive = builder.isLowerBoundInclusive();
        this.upperBoundName = builder.getUpperBoundName();
        this.upperBoundLabel = builder.getUpperBoundLabel();
        this.upperBoundInclusive = builder.isUpperBoundInclusive();
    }

    @Override
    public String getLowerBoundName() {
        return this.lowerBoundName;
    }

    @Override
    public String getLowerBoundLabel() {
        return this.lowerBoundLabel;
    }

    @Override
    public boolean isLowerBoundInclusive() {
        return this.lowerBoundInclusive;
    }

    @Override
    public String getUpperBoundName() {
        return this.upperBoundName;
    }

    @Override
    public String getUpperBoundLabel() {
        return this.upperBoundLabel;
    }

    @Override
    public boolean isUpperBoundInclusive() {
        return this.upperBoundInclusive;
    }

    /**
     * A builder which can be used to construct {@link RemotableAttributeRange} instances.  Enforces the constraints of the {@link AttributeRange}.
     */
    public final static class Builder implements Serializable, ModelBuilder, AttributeRange {

        private String lowerBoundName;
        private String lowerBoundLabel;
        private boolean lowerBoundInclusive;
        private String upperBoundName;
        private String upperBoundLabel;
        private boolean upperBoundInclusive;

        private Builder(String lowerBoundName, String upperBoundName) {
            setLowerBoundName(lowerBoundName);
            setUpperBoundName(upperBoundName);
            setLowerBoundInclusive(true);
            setUpperBoundInclusive(true);
        }

        public static Builder create(String lowerBoundName, String upperBoundName) {
            return new Builder(lowerBoundName, upperBoundName);
        }

        public static Builder create(AttributeRange contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create(contract.getLowerBoundName(), contract.getUpperBoundName());
            builder.setLowerBoundInclusive(contract.isLowerBoundInclusive());
            builder.setUpperBoundInclusive(contract.isUpperBoundInclusive());
            return builder;
        }

        public RemotableAttributeRange build() {
            return new RemotableAttributeRange(this);
        }

        @Override
        public String getLowerBoundName() {
            return this.lowerBoundName;
        }

        @Override
        public String getLowerBoundLabel() {
            return this.lowerBoundLabel;
        }

        @Override
        public boolean isLowerBoundInclusive() {
            return this.lowerBoundInclusive;
        }

        @Override
        public String getUpperBoundName() {
            return this.upperBoundName;
        }

        @Override
        public String getUpperBoundLabel() {
            return this.upperBoundLabel;
        }

        @Override
        public boolean isUpperBoundInclusive() {
            return this.upperBoundInclusive;
        }

        public void setLowerBoundName(String lowerBoundName) {
            if (StringUtils.isBlank(lowerBoundName)) {
                throw new IllegalArgumentException("lowerBoundName was null or blank");
            }
            this.lowerBoundName = lowerBoundName;
        }

        public void setLowerBoundLabel(String lowerBoundLabel) {
            this.lowerBoundLabel = lowerBoundLabel;
        }

        public void setLowerBoundInclusive(boolean lowerBoundInclusive) {
            this.lowerBoundInclusive = lowerBoundInclusive;
        }

        public void setUpperBoundName(String upperBoundName) {
            if (StringUtils.isBlank(upperBoundName)) {
                throw new IllegalArgumentException("upperBoundName was null or blank");
            }
            this.upperBoundName = upperBoundName;
        }

        public void setUpperBoundLabel(String upperBoundLabel) {
            this.upperBoundLabel = upperBoundLabel;
        }

        public void setUpperBoundInclusive(boolean upperBoundInclusive) {
            this.upperBoundInclusive = upperBoundInclusive;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "remotableAttributeRange";
        final static String TYPE_NAME = "RemotableAttributeRangeType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String LOWER_BOUND_NAME = "lowerBoundName";
        final static String LOWER_BOUND_LABEL = "lowerBoundLabel";
        final static String LOWER_BOUND_INCLUSIVE = "lowerBoundInclusive";
        final static String UPPER_BOUND_NAME = "upperBoundName";
        final static String UPPER_BOUND_LABEL = "upperBoundLabel";
        final static String UPPER_BOUND_INCLUSIVE = "upperBoundInclusive";
    }

}
