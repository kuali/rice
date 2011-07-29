package org.kuali.rice.core.api.uif;

import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * An abstract control that all controls inherit from.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableAbstractControl.Constants.TYPE_NAME)
public abstract class RemotableAbstractControl extends AbstractDataTransferObject implements Control {

    /**
     * Should only be invoked by JAXB.
     */
    @SuppressWarnings("unused")
    RemotableAbstractControl() {

    }

    public abstract static class Builder implements Control, ModelBuilder {
        Builder() {
            super();
        }

        //todo make ModelBuilder generic so I don't have to do this.
        public abstract RemotableAbstractControl build();
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String TYPE_NAME = "AbstractControlType";
    }
}
