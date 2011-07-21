package org.kuali.rice.core.api.uif;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * A datepicker widget that can be used by a TextInput or HiddenInput control.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = Datepicker.Constants.TYPE_NAME)
public final class Datepicker extends AbstractWidget {

    private Datepicker() {
        super();
    }

    private Datepicker(Builder b) {
        super();
    }

    public static final class Builder extends AbstractWidget.Builder {

        private Builder() {
            super();
        }

        public static Builder create() {
            return new Builder();
        }

        @Override
        public Datepicker build() {
            return new Datepicker(this);
        }
    }


    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "DatepickerType";
    }
}
