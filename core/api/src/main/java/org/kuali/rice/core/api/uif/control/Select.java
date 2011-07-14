package org.kuali.rice.core.api.uif.control;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Select.Constants.TYPE_NAME)
public class Select extends AbstractControl implements SelectContract {

    @XmlElement(name = Elements.SIZE, required = false)
    private final Integer size;

    @XmlElement(name = Elements.DEFAULT_VALUE, required = false)
    private final String defaultValue;

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    private Select() {
        size = null;
        defaultValue = null;
    }

    private Select(Builder b) {
        super(b);
        size = b.size;
        defaultValue = b.defaultValue;
    }

    public static final class Builder extends AbstractControl.Builder implements SelectContract {
        private Integer size;
        private String defaultValue;

        private Builder(String name) {
            super(name);
        }

        public static Builder create(String name) {
            return new Builder(name);
        }

        public static Builder create(SelectContract contract) {
            Builder b = create(contract.getName());

            partialCreate(contract, b);

            b.setSize(contract.getSize());
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
        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public Select build() {
            return new Select(this);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "SelectType";
    }

    static final class Elements {
        static final String SIZE = "size";
        static final String DEFAULT_VALUE = "defaultValue";
    }
}
