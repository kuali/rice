package org.kuali.rice.core.api.uif;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * A datepicker widget that can be used by a TextInput or HiddenInput control.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableDatepicker.Constants.TYPE_NAME)
public final class RemotableDatepicker extends RemotableAbstractWidget {

    private RemotableDatepicker() {
        super();
    }

    private RemotableDatepicker(Builder b) {
        super();
    }

    public static final class Builder extends RemotableAbstractWidget.Builder {

        private Builder() {
            super();
        }

        public static Builder create() {
            return new Builder();
        }

        @Override
        public RemotableDatepicker build() {
            return new RemotableDatepicker(this);
        }
    }


    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "DatepickerType";
    }
}
