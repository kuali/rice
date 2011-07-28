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
 * A text expand widget.  This can be used along side a textarea control.
 */
@XmlRootElement(name = RemotableTextExpand.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableTextExpand.Constants.TYPE_NAME, propOrder = {
		CoreConstants.CommonElements.FUTURE_ELEMENTS })
public final class RemotableTextExpand extends RemotableAbstractWidget {

    private static final RemotableTextExpand INSTANCE = new RemotableTextExpand(Builder.INSTANCE);

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    private RemotableTextExpand() {
        super();
    }

    private RemotableTextExpand(Builder b) {
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
        public RemotableTextExpand build() {
            return RemotableTextExpand.INSTANCE;
        }
    }


    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "TextExpandType";
        final static String ROOT_ELEMENT_NAME = "textExpand";
    }
}
