package org.kuali.rice.core.api.uif;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class describes an attribute.  It can be considered the definition for an attribute.
 * It also contains preferred rendering instructions for an attribute. ie when rendering an attribute
 * in a user interface use this control with these widgets.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AttributeField.Constants.TYPE_NAME)
public final class AttributeField implements AttributeFieldContract, ModelObjectComplete {

    @XmlElement(name = Elements.NAME, required = true)
    private final String name;

    @XmlElement(name = Elements.SHORT_LABEL, required = false)
    private final String shortLabel;

    @XmlElement(name = Elements.LONG_LABEL, required = false)
    private final String longLabel;

    @XmlElement(name = Elements.HELP_SUMMARY, required = false)
    private final String helpSummary;

    @XmlElement(name = Elements.HELP_CONSTRAINT, required = false)
    private final String helpConstraint;

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

    @XmlElement(name = Elements.REGEX_CONSTRAINT, required = false)
    private final String regexConstraint;

    @XmlElement(name = Elements.REGEX_CONSTRAINT_MSG, required = false)
    private final String regexContraintMsg;

    @XmlElement(name = Elements.REQUIRED, required = false)
    private final boolean required;

    @XmlElement(name = Elements.DEFAULT_VALUES, required = false)
    private final Collection<String> defaultValues;

    @XmlElement(name = Elements.CONTROL, required = false)
    private final AbstractControl control;
    
    @XmlElement(name = Elements.WIDGETS, required = false)
    private final Collection<? extends AbstractWidget> widgets;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getShortLabel() {
        return shortLabel;
    }

    @Override
    public String getLongLabel() {
        return longLabel;
    }

    @Override
    public String getHelpSummary() {
        return helpSummary;
    }

    @Override
    public String getHelpConstraint() {
        return helpConstraint;
    }

    @Override
    public String getHelpDescription() {
        return helpDescription;
    }

    @Override
    public boolean isForceUpperCase() {
        return forceUpperCase;
    }

    @Override
    public Integer getMinLength() {
        return minLength;
    }

    @Override
    public Integer getMaxLength() {
        return maxLength;
    }

    @Override
    public Integer getMinValue() {
        return minValue;
    }

    @Override
    public Integer getMaxValue() {
        return maxValue;
    }

    @Override
    public String getRegexConstraint() {
        return regexConstraint;
    }

    @Override
    public String getRegexContraintMsg() {
        return regexContraintMsg;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public Collection<String> getDefaultValues() {
        return defaultValues;
    }

    @Override
    public AbstractControl getControl() {
        return control;
    }

    @Override
    public Collection<? extends AbstractWidget> getWidgets() {
        return widgets;
    }

    private AttributeField() {
        this.name = null;
        this.shortLabel = null;
        this.longLabel = null;
        this.helpSummary = null;
        this.helpConstraint = null;
        this.helpDescription = null;
        this.forceUpperCase = false;
        this.minLength = null;
        this.maxLength = null;
        this.minValue = null;
        this.maxValue = null;
        this.regexConstraint = null;
        this.regexContraintMsg = null;
        this.required = false;
        this.defaultValues = null;
        this.control = null;
        this.widgets = null;
    }

    private AttributeField(Builder b) {
        this.name = b.name;
        this.shortLabel = b.shortLabel;
        this.longLabel = b.longLabel;
        this.helpSummary = b.helpSummary;
        this.helpConstraint = b.helpConstraint;
        this.helpDescription = b.helpDescription;
        this.forceUpperCase = b.forceUpperCase;
        this.minLength = b.minLength;
        this.maxLength = b.maxLength;
        this.minValue = b.minValue;
        this.maxValue = b.maxValue;
        this.regexConstraint = b.regexConstraint;
        this.regexContraintMsg = b.regexContraintMsg;
        this.required = b.required;
        this.defaultValues = b.defaultValues;
        this.control = b.control.build();

        final List<AbstractWidget> temp = new ArrayList<AbstractWidget>();
        for (AbstractWidget.Builder attr : b.widgets) {
            temp.add(attr.build());
        }
        this.widgets = Collections.unmodifiableList(temp);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static final class Builder implements AttributeFieldContract, ModelBuilder {
        private String name;
        private String shortLabel;
        private String longLabel;

        private String helpSummary;
        private String helpConstraint;
        private String helpDescription;

        private boolean forceUpperCase;

        private Integer minLength;
        private Integer maxLength;

        private Integer minValue;
        private Integer maxValue;

        private String regexConstraint;
        private String regexContraintMsg;

        private boolean required;

        private Collection<String> defaultValues;
        private AbstractControl.Builder control;

        private Collection<AbstractWidget.Builder> widgets;

        private Builder(String name) {
            setName(name);
        }

        public static Builder create(String name) {
            return new Builder(name);
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            if (StringUtils.isBlank(name)) {
                throw new IllegalArgumentException("name is blank");
            }

            this.name = name;
        }

        @Override
        public String getShortLabel() {
            return shortLabel;
        }

        public void setShortLabel(String shortLabel) {
            this.shortLabel = shortLabel;
        }

        @Override
        public String getLongLabel() {
            return longLabel;
        }

        public void setLongLabel(String longLabel) {
            this.longLabel = longLabel;
        }

        @Override
        public String getHelpSummary() {
            return helpSummary;
        }

        public void setHelpSummary(String helpSummary) {
            this.helpSummary = helpSummary;
        }

        @Override
        public String getHelpConstraint() {
            return helpConstraint;
        }

        public void setHelpConstraint(String helpConstraint) {
            this.helpConstraint = helpConstraint;
        }

        @Override
        public String getHelpDescription() {
            return helpDescription;
        }

        public void setHelpDescription(String helpDescription) {
            this.helpDescription = helpDescription;
        }

        @Override
        public boolean isForceUpperCase() {
            return forceUpperCase;
        }

        public void setForceUpperCase(boolean forceUpperCase) {
            this.forceUpperCase = forceUpperCase;
        }

        @Override
        public Integer getMinLength() {
            return minLength;
        }

        public void setMinLength(Integer minLength) {
            if (minLength != null && minLength < 1) {
                throw new IllegalArgumentException("minLength was < 1");
            }
            
            this.minLength = minLength;
        }

        @Override
        public Integer getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(Integer maxLength) {
            if (maxLength != null && maxLength < 1) {
                throw new IllegalArgumentException("maxLength was < 1");
            }
            
            this.maxLength = maxLength;
        }

        @Override
        public Integer getMinValue() {
            return minValue;
        }

        public void setMinValue(Integer minValue) {
            this.minValue = minValue;
        }

        @Override
        public Integer getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(Integer maxValue) {
            this.maxValue = maxValue;
        }

        @Override
        public String getRegexConstraint() {
            return regexConstraint;
        }

        public void setRegexConstraint(String regexConstraint) {
            this.regexConstraint = regexConstraint;
        }

        @Override
        public String getRegexContraintMsg() {
            return regexContraintMsg;
        }

        public void setRegexContraintMsg(String regexContraintMsg) {
            this.regexContraintMsg = regexContraintMsg;
        }

        @Override
        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        @Override
        public Collection<String> getDefaultValues() {
            return defaultValues;
        }

        public void setDefaultValues(Collection<String> defaultValues) {
            this.defaultValues = defaultValues;
        }

        @Override
        public AbstractControl.Builder getControl() {
            return control;
        }

        public void setControl(AbstractControl.Builder control) {
            this.control = control;
        }

        @Override
        public Collection<AbstractWidget.Builder> getWidgets() {
            return widgets;
        }

        public void setWidgets(Collection<AbstractWidget.Builder> widgets) {
            this.widgets = widgets;
        }

        @Override
        public AttributeField build() {
            return new AttributeField(this);
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
        static final String HELP_CONSTRAINT = "helpConstraint";
        static final String HELP_DESCRIPTION = "helpDescription";
        static final String FORCE_UPPERCASE = "forceUpperCase";
        static final String MIN_LENGTH = "minLength";
        static final String MAX_LENGTH = "maxLength";
        static final String MIN_VALUE = "minValue";
        static final String MAX_VALUE = "maxValue";
        static final String REGEX_CONSTRAINT = "regexConstraint";
        static final String REGEX_CONSTRAINT_MSG = "regexContraintMsg";
        static final String REQUIRED = "required";
        static final String DEFAULT_VALUES = "defaultValues";
        static final String CONTROL = "control";
        static final String WIDGETS = "widgets";
    }
}
