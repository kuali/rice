package org.kuali.rice.core.api.uif;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A password input control type.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotablePasswordInput.Constants.TYPE_NAME)
public final class RemotablePasswordInput extends RemotableAbstractControl implements Sized {

    @XmlElement(name = Elements.SIZE, required = false)
    private final Integer size;

    @Override
    public Integer getSize() {
        return size;
    }

    private RemotablePasswordInput() {
        size = null;
    }

    private RemotablePasswordInput(Builder b) {
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
        public RemotablePasswordInput build() {
            return new RemotablePasswordInput(this);
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
    }
}
