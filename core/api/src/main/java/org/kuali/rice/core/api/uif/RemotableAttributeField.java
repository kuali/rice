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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @see AttributeField for more info.
 */
@XmlRootElement(name = RemotableAttributeField.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableAttributeField.Constants.TYPE_NAME, propOrder = {
		RemotableAttributeField.Elements.NAME,
		RemotableAttributeField.Elements.DATA_TYPE,
		RemotableAttributeField.Elements.SHORT_LABEL,
		RemotableAttributeField.Elements.LONG_LABEL,
		RemotableAttributeField.Elements.HELP_SUMMARY,
		RemotableAttributeField.Elements.HELP_CONSTRAINT,
		RemotableAttributeField.Elements.HELP_DESCRIPTION,
		RemotableAttributeField.Elements.FORCE_UPPERCASE,
		RemotableAttributeField.Elements.MIN_LENGTH,
		RemotableAttributeField.Elements.MAX_LENGTH,
		RemotableAttributeField.Elements.MIN_VALUE,
		RemotableAttributeField.Elements.MAX_VALUE,
		RemotableAttributeField.Elements.REGEX_CONSTRAINT,
		RemotableAttributeField.Elements.REGEX_CONSTRAINT_MSG,
		RemotableAttributeField.Elements.REQUIRED,
		RemotableAttributeField.Elements.DEFAULT_VALUES,
		RemotableAttributeField.Elements.CONTROL,
		RemotableAttributeField.Elements.REQUIRED,
		RemotableAttributeField.Elements.WIDGETS,
		CoreConstants.CommonElements.FUTURE_ELEMENTS })
public final class RemotableAttributeField implements AttributeField, ModelObjectComplete {

    @XmlElement(name = Elements.NAME, required = true)
    private final String name;

    @XmlJavaTypeAdapter(DataType.Adapter.class)
	@XmlElement(name = Elements.DATA_TYPE, required = false)
    private final DataType dataType;

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

    @XmlElements(value = {
        @XmlElement(name = RemotableCheckboxGroup.Constants.ROOT_ELEMENT_NAME, type = RemotableCheckboxGroup.class, required = false),
        @XmlElement(name = RemotableHiddenInput.Constants.ROOT_ELEMENT_NAME, type = RemotableHiddenInput.class, required = false),
        @XmlElement(name = RemotablePasswordInput.Constants.ROOT_ELEMENT_NAME, type = RemotablePasswordInput.class, required = false),
        @XmlElement(name = RemotableRadioButtonGroup.Constants.ROOT_ELEMENT_NAME, type = RemotableRadioButtonGroup.class, required = false),
        @XmlElement(name = RemotableSelect.Constants.ROOT_ELEMENT_NAME, type = RemotableSelect.class, required = false),
        @XmlElement(name = RemotableTextarea.Constants.ROOT_ELEMENT_NAME, type = RemotableTextarea.class, required = false),
        @XmlElement(name = RemotableTextInput.Constants.ROOT_ELEMENT_NAME, type = RemotableTextInput.class, required = false)
    })
    private final RemotableAbstractControl control;

    @XmlElementWrapper(name = Elements.WIDGETS, required = false)
    @XmlElements(value = {
        @XmlElement(name = RemotableDatepicker.Constants.ROOT_ELEMENT_NAME, type = RemotableDatepicker.class, required = false),
        @XmlElement(name = RemotableQuickFinder.Constants.ROOT_ELEMENT_NAME, type = RemotableQuickFinder.class, required = false),
        @XmlElement(name = RemotableTextExpand.Constants.ROOT_ELEMENT_NAME, type = RemotableTextExpand.class, required = false)
    })
    private final Collection<? extends RemotableAbstractWidget> widgets;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private RemotableAttributeField() {
        this.name = null;
        this.dataType = null;
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

    private RemotableAttributeField(Builder b) {
        this.name = b.name;
        this.dataType = b.dataType;
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

        final List<RemotableAbstractWidget> temp = new ArrayList<RemotableAbstractWidget>();
        for (RemotableAbstractWidget.Builder attr : b.widgets) {
            temp.add(attr.build());
        }
        this.widgets = Collections.unmodifiableList(temp);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataType getDataType() {
        return dataType;
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
    public RemotableAbstractControl getControl() {
        return control;
    }

    @Override
    public Collection<? extends RemotableAbstractWidget> getWidgets() {
        return widgets;
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

    public static final class Builder implements AttributeField, ModelBuilder {
        private String name;
        private DataType dataType;
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
        private RemotableAbstractControl.Builder control;

        private Collection<RemotableAbstractWidget.Builder> widgets;

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
        public DataType getDataType() {
            return dataType;
        }

        public void setDataType(DataType dataType) {
            this.dataType = dataType;
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
        public RemotableAbstractControl.Builder getControl() {
            return control;
        }

        public void setControl(RemotableAbstractControl.Builder control) {
            this.control = control;
        }

        @Override
        public Collection<RemotableAbstractWidget.Builder> getWidgets() {
            return widgets;
        }

        public void setWidgets(Collection<RemotableAbstractWidget.Builder> widgets) {
            this.widgets = widgets;
        }

        @Override
        public RemotableAttributeField build() {
            return new RemotableAttributeField(this);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "AbstractControlType";
        final static String ROOT_ELEMENT_NAME = "attributeField";
        static final String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }

    static final class Elements {
        static final String NAME = "name";
        static final String DATA_TYPE = "dataType";
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
