package org.kuali.rice.core.api.uif;

import org.kuali.rice.core.api.CoreConstants;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

/**
 * A hidden input control type.
 */
@XmlRootElement(name = RemotableHiddenInput.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableHiddenInput.Constants.TYPE_NAME, propOrder = {
		CoreConstants.CommonElements.FUTURE_ELEMENTS })
public final class RemotableHiddenInput extends RemotableAbstractControl {

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private RemotableHiddenInput() {
    }

    private RemotableHiddenInput(Builder b) {
    }

    public static final class Builder extends RemotableAbstractControl.Builder {

        private Builder() {
            super();
        }

        public static Builder create() {
            return new Builder();
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
        final static String ROOT_ELEMENT_NAME = "hiddenInput";
    }

    static final class Elements {
    }
}
