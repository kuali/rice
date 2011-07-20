package org.kuali.rice.core.api.uif;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Textarea.Constants.TYPE_NAME)
public class Textarea extends AbstractControl implements Watermarked, RowsCols {

    @XmlElement(name = Elements.ROWS, required = false)
    private final Integer rows;

    @XmlElement(name = Elements.COLS, required = false)
    private final Integer cols;

    @XmlElement(name = Elements.WATERMARK, required = false)
    private final String watermark;

    @Override
    public Integer getRows() {
        return rows;
    }

    @Override
    public Integer getCols() {
        return cols;
    }

    @Override
    public String getWatermark() {
        return watermark;
    }

    private Textarea() {
        rows = null;
        cols = null;
        watermark = null;
    }

    private Textarea(Builder b) {
        rows = b.rows;
        cols = b.cols;
        watermark = b.watermark;
    }

    public static final class Builder extends AbstractControl.Builder implements Watermarked, RowsCols {
        private Integer rows;
        private Integer cols;
        private String watermark;

        private Builder() {
            super();
        }

        public static Builder create() {
            return new Builder();
        }

        @Override
        public Integer getRows() {
            return rows;
        }

        public void setRows(Integer rows) {
            if (rows != null && rows < 1) {
                throw new IllegalArgumentException("rows was < 1");
            }

            this.rows = rows;
        }

        @Override
        public Integer getCols() {
            return cols;
        }

        public void setCols(Integer cols) {
            if (cols != null && cols < 1) {
                throw new IllegalArgumentException("cols was < 1");
            }

            this.cols = cols;
        }

        @Override
        public String getWatermark() {
            return watermark;
        }

        public void setWatermark(String watermark) {
            this.watermark = watermark;
        }

        @Override
        public Textarea build() {
            return new Textarea(this);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "TextareaType";
    }

    static final class Elements {
        static final String COLS = "cols";
        static final String ROWS = "rows";
        static final String WATERMARK = "watermark";
    }
}
