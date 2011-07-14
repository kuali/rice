package org.kuali.rice.core.api.uif.control;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = PasswordInput.Constants.TYPE_NAME)
public class PasswordInput extends AbstractControl implements PasswordInputContract {

    @XmlElement(name = Elements.SIZE, required = false)
    private final Integer size;

    @XmlElement(name = Elements.WATERMARK, required = false)
    private final String watermark;

    @XmlElement(name = Elements.DEFAULT_VALUE, required = false)
    private final String defaultValue;

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public String getWatermark() {
        return watermark;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    private PasswordInput() {
        size = null;
        watermark = null;
        defaultValue = null;
    }

    private PasswordInput(Builder b) {
        super(b);
        size = b.size;
        watermark = b.watermark;
        defaultValue = b.defaultValue;
    }

    public static final class Builder extends AbstractControl.Builder implements PasswordInputContract {
        private Integer size;
        private String watermark;
        private String defaultValue;

        private Builder(String name) {
            super(name);
        }

        public static Builder create(String name) {
            return new Builder(name);
        }

        public static Builder create(PasswordInputContract contract) {
            Builder b = create(contract.getName());

            partialCreate(contract, b);

            b.setSize(contract.getSize());
            b.setWatermark(contract.getWatermark());
            b.setDefaultValue(contract.getDefaultValue());
            return b;
        }

        @Override
        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            if (size != null && size < 1) {
                throw new IllegalArgumentException("size was < 1");
            }

            this.size = size;
        }

        @Override
        public String getWatermark() {
            return watermark;
        }

        public void setWatermark(String watermark) {
            this.watermark = watermark;
        }


        @Override
        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public PasswordInput build() {
            return new PasswordInput(this);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "PasswordInputType";
    }

    static final class Elements {
        static final String SIZE = "size";
        static final String WATERMARK = "watermark";
        static final String DEFAULT_VALUE = "defaultValue";
    }
}
