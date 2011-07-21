package org.kuali.rice.core.api.uif;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A hidden input control type.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableHiddenInput.Constants.TYPE_NAME)
public final class RemotableHiddenInput extends RemotableAbstractControl implements Sized {

    @XmlElement(name = Elements.SIZE, required = false)
    private final Integer size;

    @Override
    public Integer getSize() {
        return size;
    }

    private RemotableHiddenInput() {
        size = null;
    }

    private RemotableHiddenInput(Builder b) {
        size = b.size;
    }

    public static final class Builder extends RemotableAbstractControl.Builder implements Sized {
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
        public RemotableHiddenInput build() {
            return new RemotableHiddenInput(this);
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
