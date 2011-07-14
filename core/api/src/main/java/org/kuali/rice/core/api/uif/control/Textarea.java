package org.kuali.rice.core.api.uif.control;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Textarea.Constants.TYPE_NAME)
public class Textarea extends AbstractControl implements TextareaContract {

    @XmlElement(name = Elements.ROWS, required = false)
    private final Integer rows;

    @XmlElement(name = Elements.COLS, required = false)
    private final Integer cols;

    @XmlElement(name = Elements.WATERMARK, required = false)
    private final String watermark;

    @XmlElement(name = Elements.DEFAULT_VALUE, required = false)
    private final String defaultValue;

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

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    private Textarea() {
        rows = null;
        cols = null;
        watermark = null;
        defaultValue = null;
    }

    private Textarea(Builder b) {
        super(b);
        rows = b.rows;
        cols = b.cols;
        watermark = b.watermark;
        defaultValue = b.defaultValue;
    }

    public static final class Builder extends AbstractControl.Builder implements TextareaContract {
        private Integer rows;
        private Integer cols;
        private String watermark;
        private String defaultValue;

        private Builder(String name) {
            super(name);
        }

        public static Builder create(String name) {
            return new Builder(name);
        }

        public static Builder create(TextareaContract contract) {
            Builder b = create(contract.getName());

            partialCreate(contract, b);

            b.setCols(contract.getCols());
            b.setRows(contract.getRows());
            b.setWatermark(contract.getWatermark());
            b.setDefaultValue(contract.getDefaultValue());
            return b;
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
        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
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
        static final String DEFAULT_VALUE = "defaultValue";
    }
}
