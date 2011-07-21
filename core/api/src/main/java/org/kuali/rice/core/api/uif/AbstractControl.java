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
 * An abstract control that all controls inherit from.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = AbstractControl.Constants.TYPE_NAME)
public abstract class AbstractControl implements Control, ModelObjectComplete {

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

    abstract static class Builder implements Control, ModelBuilder {
        Builder() {
            super();
        }

        //todo make ModelBuilder generic so I don't have to do this.
        public abstract AbstractControl build();
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String TYPE_NAME = "AbstractControlType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }
}
