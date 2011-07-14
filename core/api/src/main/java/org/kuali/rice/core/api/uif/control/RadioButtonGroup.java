package org.kuali.rice.core.api.uif.control;

import javax.xml.bind.annotation.XmlElement;

public class RadioButtonGroup extends AbstractControl implements RadioButtonGroupContract {

    @XmlElement(name = Elements.DEFAULT_VALUE, required = false)
    private final String defaultValue;

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    private RadioButtonGroup() {
        defaultValue = null;
    }

    private RadioButtonGroup(Builder b) {
        super(b);
        defaultValue = b.defaultValue;
    }

    public static final class Builder extends AbstractControl.Builder implements RadioButtonGroupContract {
        private String defaultValue;

        private Builder(String name) {
            super(name);
        }

        public static Builder create(String name) {
            return new Builder(name);
        }

        public static Builder create(TextInputContract contract) {
            Builder b = create(contract.getName());

            partialCreate(contract, b);

            b.setDefaultValue(contract.getDefaultValue());
            return b;
        }

        @Override
        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public RadioButtonGroup build() {
            return new RadioButtonGroup(this);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "RadioButtonGroupType";
    }

    static final class Elements {
        static final String DEFAULT_VALUE = "defaultValue";
    }
}
