package org.kuali.rice.core.api.uif;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A text input control type.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableTextInput.Constants.TYPE_NAME)
public final class RemotableTextInput extends RemotableAbstractControl implements Sized, Watermarked {

    @XmlElement(name = Elements.SIZE, required = false)
    private final Integer size;

    @XmlElement(name = Elements.WATERMARK, required = false)
    private final String watermark;

    @Override
    public Integer getSize() {
        return size;
    }

    @Override
    public String getWatermark() {
        return watermark;
    }

    private RemotableTextInput() {
        size = null;
        watermark = null;
    }

    private RemotableTextInput(Builder b) {
        size = b.size;
        watermark = b.watermark;
    }

    public static final class Builder extends RemotableAbstractControl.Builder implements Sized, Watermarked {
        private Integer size;
        private String watermark;

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
        public String getWatermark() {
            return watermark;
        }

        public void setWatermark(String watermark) {
            this.watermark = watermark;
        }

        @Override
        public RemotableTextInput build() {
            return new RemotableTextInput(this);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "TextInputType";
    }

    static final class Elements {
        static final String SIZE = "size";
        static final String WATERMARK = "watermark";
    }
}
