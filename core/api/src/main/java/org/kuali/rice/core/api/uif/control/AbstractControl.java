package org.kuali.rice.core.api.uif.control;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.core.api.uif.control.widget.AbstractWidget;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

/**
 * This is the base class for all ui controls.  It defines the elements common to all ui controls.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AbstractControl.Constants.TYPE_NAME)
public abstract class AbstractControl implements AbstractControlContract, ModelObjectComplete {

    @XmlElement(name = Elements.NAME, required = true)
    private final String name;

    @XmlElement(name = Elements.SHORT_LABEL, required = false)
    private final String shortLabel;

    @XmlElement(name = Elements.LONG_LABEL, required = false)
    private final String longLabel;

    @XmlElement(name = Elements.HELP_SUMMARY, required = false)
    private final String helpSummary;

    @XmlElement(name = Elements.HELP_CONSTRAInteger, required = false)
    private final String helpConstraInteger;

    @XmlElement(name = Elements.HELP_DESCRIPTION, required = false)
    private final String helpDescription;

    @XmlElement(name = Elements.FORCE_UPPERCASE, required = false)
    private final boolean forceUpperCase;

    @XmlElement(name = Elements.MIN_LENGTH, required = false)
    private final Integer minLength;

    @XmlElement(name = Elements.MAX_LENGTH, required = false)
    private final Integer maxLength;

    @XmlElement(name = Elements.MIN_VALUE, required = false)
    private final Integer minValue;

    @XmlElement(name = Elements.MAX_VALUE, required = false)
    private final Integer maxValue;

    @XmlElement(name = Elements.REGEX_CONSTRAInteger, required = false)
    private final String regexConstraint;

    @XmlElement(name = Elements.REGEX_CONSTRAInteger_MSG, required = false)
    private final String regexContraintMsg;

    @XmlElement(name = Elements.REQUIRED, required = false)
    private final boolean required;

    @XmlElement(name = Elements.WIDGETS, required = false)
    private final Collection<? extends AbstractWidget> widgets;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final String getShortLabel() {
        return shortLabel;
    }

    @Override
    public final String getLongLabel() {
        return longLabel;
    }

    @Override
    public final String getHelpSummary() {
        return helpSummary;
    }

    @Override
    public final String getHelpConstraInteger() {
        return helpConstraInteger;
    }

    @Override
    public final String getHelpDescription() {
        return helpDescription;
    }

    @Override
    public final boolean isForceUpperCase() {
        return forceUpperCase;
    }

    @Override
    public final Integer getMinLength() {
        return minLength;
    }

    @Override
    public final Integer getMaxLength() {
        return maxLength;
    }

    @Override
    public final Integer getMinValue() {
        return minValue;
    }

    @Override
    public final Integer getMaxValue() {
        return maxValue;
    }

    @Override
    public final String getRegexConstraint() {
        return regexConstraint;
    }

    @Override
    public final String getRegexContraintMsg() {
        return regexContraintMsg;
    }

    @Override
    public final boolean isRequired() {
        return required;
    }

    @Override
    public final Collection<? extends AbstractWidget> getWidgets() {
        return widgets;
    }

    /* default visibility ctor to allow subclassing. */
    AbstractControl() {
        this.name = null;
        this.shortLabel = null;
        this.longLabel = null;
        this.helpSummary = null;
        this.helpConstraInteger = null;
        this.helpDescription = null;
        this.forceUpperCase = false;
        this.minLength = null;
        this.maxLength = null;
        this.minValue = null;
        this.maxValue = null;
        this.regexConstraint = null;
        this.regexContraintMsg = null;
        this.required = false;
        this.widgets = null;
    }

    /* default visibility ctor to allow subclassing. */
    AbstractControl(Builder b) {
        this.name = b.name;
        this.shortLabel = b.shortLabel;
        this.longLabel = b.longLabel;
        this.helpSummary = b.helpSummary;
        this.helpConstraInteger = b.helpConstraInteger;
        this.helpDescription = b.helpDescription;
        this.forceUpperCase = b.forceUpperCase;
        this.minLength = b.minLength;
        this.maxLength = b.maxLength;
        this.minValue = b.getMinValue();
        this.maxValue = b.maxValue;
        this.regexConstraint = b.regexConstraint;
        this.regexContraintMsg = b.regexContraintMsg;
        this.required = b.required;
        this.widgets = b.widgets;
    }

    @Override
    public final int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public final boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public final String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    abstract static class Builder implements AbstractControlContract, ModelBuilder {
        private String name;
        private String shortLabel;
        private String longLabel;

        private String helpSummary;
        private String helpConstraInteger;
        private String helpDescription;

        private boolean forceUpperCase;

        private Integer minLength;
        private Integer maxLength;

        private Integer minValue;
        private Integer maxValue;

        private String regexConstraint;
        private String regexContraintMsg;

        private boolean required;

        private Collection<? extends AbstractWidget> widgets;

        Builder(String name) {
            setName(name);
        }

        static final void partialCreate(AbstractControlContract contract, Builder b) {

            b.setShortLabel(contract.getShortLabel());
            b.setLongLabel(contract.getLongLabel());

            b.setHelpSummary(contract.getHelpSummary());
            b.setHelpConstraInteger(contract.getHelpConstraInteger());
            b.setHelpDescription(contract.getHelpDescription());

            b.setForceUpperCase(contract.isForceUpperCase());

            b.setMinLength(contract.getMinLength());
            b.setMaxLength(contract.getMaxLength());

            b.setMinValue(contract.getMinValue());
            b.setMaxValue(contract.getMaxValue());

            b.setRegexConstraint(contract.getRegexConstraint());
            b.setRegexContraintMsg(contract.getRegexContraintMsg());

            b.setRequired(contract.isRequired());

            b.setWidgets(contract.getWidgets());
        }

        @Override
        public final String getName() {
            return name;
        }

        public final void setName(String name) {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("name is blank");
            }

            this.name = name;
        }

        @Override
        public final String getShortLabel() {
            return shortLabel;
        }

        public final void setShortLabel(String shortLabel) {
            this.shortLabel = shortLabel;
        }

        @Override
        public final String getLongLabel() {
            return longLabel;
        }

        public final void setLongLabel(String longLabel) {
            this.longLabel = longLabel;
        }

        @Override
        public final String getHelpSummary() {
            return helpSummary;
        }

        public final void setHelpSummary(String helpSummary) {
            this.helpSummary = helpSummary;
        }

        @Override
        public final String getHelpConstraInteger() {
            return helpConstraInteger;
        }

        public final void setHelpConstraInteger(String helpConstraInteger) {
            this.helpConstraInteger = helpConstraInteger;
        }

        @Override
        public final String getHelpDescription() {
            return helpDescription;
        }

        public final void setHelpDescription(String helpDescription) {
            this.helpDescription = helpDescription;
        }

        @Override
        public final boolean isForceUpperCase() {
            return forceUpperCase;
        }

        public final void setForceUpperCase(boolean forceUpperCase) {
            this.forceUpperCase = forceUpperCase;
        }

        @Override
        public final Integer getMinLength() {
            return minLength;
        }

        public final void setMinLength(Integer minLength) {
            if (minLength != null && minLength < 1) {
                throw new IllegalArgumentException("minLength was < 1");
            }
            
            this.minLength = minLength;
        }

        @Override
        public final Integer getMaxLength() {
            return maxLength;
        }

        public final void setMaxLength(Integer maxLength) {
            if (maxLength != null && maxLength < 1) {
                throw new IllegalArgumentException("maxLength was < 1");
            }
            
            this.maxLength = maxLength;
        }

        @Override
        public final Integer getMinValue() {
            return minValue;
        }

        public final void setMinValue(Integer minValue) {
            this.minValue = minValue;
        }

        @Override
        public final Integer getMaxValue() {
            return maxValue;
        }

        public final void setMaxValue(Integer maxValue) {
            this.maxValue = maxValue;
        }

        @Override
        public final String getRegexConstraint() {
            return regexConstraint;
        }

        public final void setRegexConstraint(String regexConstraint) {
            this.regexConstraint = regexConstraint;
        }

        @Override
        public final String getRegexContraintMsg() {
            return regexContraintMsg;
        }

        public final void setRegexContraintMsg(String regexContraintMsg) {
            this.regexContraintMsg = regexContraintMsg;
        }

        @Override
        public final boolean isRequired() {
            return required;
        }

        public final void setRequired(boolean required) {
            this.required = required;
        }

        @Override
        public final Collection<? extends AbstractWidget> getWidgets() {
            return widgets;
        }

        public final void setWidgets(Collection<? extends AbstractWidget> widgets) {
            this.widgets = widgets;
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "AbstractControlType";
        static final String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }

    static final class Elements {
        static final String NAME = "name";
        static final String SHORT_LABEL = "shortLabel";
        static final String LONG_LABEL = "longLabel";
        static final String HELP_SUMMARY = "helpSummary";
        static final String HELP_CONSTRAInteger = "helpConstraInteger";
        static final String HELP_DESCRIPTION = "helpDescription";
        static final String FORCE_UPPERCASE = "forceUpperCase";
        static final String MIN_LENGTH = "minLength";
        static final String MAX_LENGTH = "maxLength";
        static final String MIN_VALUE = "minValue";
        static final String MAX_VALUE = "maxValue";
        static final String REGEX_CONSTRAInteger = "regexConstraint";
        static final String REGEX_CONSTRAInteger_MSG = "regexContraintMsg";
        static final String REQUIRED = "required";
        static final String WIDGETS = "widgets";
    }
}
