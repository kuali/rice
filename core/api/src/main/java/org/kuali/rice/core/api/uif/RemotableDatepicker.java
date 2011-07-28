package org.kuali.rice.core.api.uif;

import org.kuali.rice.core.api.CoreConstants;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

/**
 * A datepicker widget that can be used by a TextInput or HiddenInput control.
 */
@XmlRootElement(name = RemotableDatepicker.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableDatepicker.Constants.TYPE_NAME, propOrder = {
		CoreConstants.CommonElements.FUTURE_ELEMENTS })
public final class RemotableDatepicker extends RemotableAbstractWidget {

    private static final RemotableDatepicker INSTANCE = new RemotableDatepicker(Builder.INSTANCE);

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private RemotableDatepicker() {
        super();
    }

    private RemotableDatepicker(Builder b) {
        super();
    }

    public static final class Builder extends RemotableAbstractWidget.Builder {

        private static final Builder INSTANCE = new Builder();

        private Builder() {
            super();
        }

        //no important state in these classes so returning a singleton
        public static Builder create() {
            return INSTANCE;
        }

        @Override
        public RemotableDatepicker build() {
            return RemotableDatepicker.INSTANCE;
        }
    }


    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "DatepickerType";
        final static String ROOT_ELEMENT_NAME = "datepicker";
    }
}
