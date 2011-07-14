package org.kuali.rice.core.api.uif.control;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This is a hidden imput control.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = HiddenInput.Constants.TYPE_NAME)
public class HiddenInput extends AbstractControl implements HiddenInputContract {
    @XmlElement(name = Elements.DEFAULT_VALUE, required = false)
    private final String defaultValue;

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    private HiddenInput() {
        defaultValue = null;
    }

    private HiddenInput(Builder b) {
        super(b);
        defaultValue = b.defaultValue;
    }

    public static final class Builder extends AbstractControl.Builder implements HiddenInputContract {
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
        public HiddenInput build() {
            return new HiddenInput(this);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "HiddenInputType";
    }

    static final class Elements {
        static final String DEFAULT_VALUE = "defaultValue";
    }
}
