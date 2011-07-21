package org.kuali.rice.core.api.uif;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

/**
 * An abstract widget that all widgets inherit from.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RemotableAbstractWidget.Constants.TYPE_NAME)
public abstract class RemotableAbstractWidget implements Widget, ModelObjectComplete {

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    @Override
    public final int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public final boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public final String toString() {
        return ToStringBuilder.reflectionToString(this);
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
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }
}
