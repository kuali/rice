package org.kuali.rice.core.api.uif.control;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = HiddenInput.Constants.TYPE_NAME)
public class HiddenInput extends AbstractControl implements Sized {

    @XmlElement(name = Elements.SIZE, required = false)
    private final Integer size;

    @Override
    public Integer getSize() {
        return size;
    }

    private HiddenInput() {
        size = null;
    }

    private HiddenInput(Builder b) {
        size = b.size;
    }

    public static final class Builder extends AbstractControl.Builder implements Sized {
        private Integer size;

        private Builder() {
            super();
        }

        public static Builder create() {
            return new Builder();
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
        static final String SIZE = "size";
    }
}
