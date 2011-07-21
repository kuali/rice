package org.kuali.rice.core.api.uif;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * A text expand widget.  This can be used along side a textarea control.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableTextExpand.Constants.TYPE_NAME)
public final class RemotableTextExpand extends RemotableAbstractWidget {

    private RemotableTextExpand() {
        super();
    }

    private RemotableTextExpand(Builder b) {
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
        public RemotableTextExpand build() {
            return new RemotableTextExpand(this);
        }
    }


    /**
     * Defines some internal constants used on this class.
     */
    static final class Constants {
        static final String TYPE_NAME = "TextExpandType";
    }
}
