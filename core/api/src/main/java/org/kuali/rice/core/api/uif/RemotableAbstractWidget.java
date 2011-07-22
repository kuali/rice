package org.kuali.rice.core.api.uif;

import org.kuali.rice.core.api.mo.AbstractJaxbModelObject;
import org.kuali.rice.core.api.mo.ModelBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * An abstract widget that all widgets inherit from.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableAbstractWidget.Constants.TYPE_NAME)
public abstract class RemotableAbstractWidget extends AbstractJaxbModelObject implements Widget {

    /**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    RemotableAbstractWidget() {

    }

    public abstract static class Builder implements Widget, ModelBuilder {
        Builder() {
            super();
        }

        //todo make ModelBuilder generic so I don't have to do this.
        public abstract RemotableAbstractWidget build();
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String TYPE_NAME = "AbstractWidgetType";
    }
}
