package org.kuali.rice.core.api.mo;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.util.CollectionUtils;

import javax.xml.bind.Unmarshaller;

/**
 * All model object's that are Jaxb annotated should extend this class.
 *
 * This class does several important things:
 * <ol>
 *     <li>Defines jaxb callback method to ensure that Collection and Map types are unmarshalled into immutable empty forms rather than null values</li>
 *     <li>Defines equals/hashcode/toString</li>
 *
 *     Note: the equals/hashCode implements excludes {@value CoreConstants.CommonElements.FUTURE_ELEMENTS} element.
 *     This element should be included on all jaxb annotated classes.
 * </ol>
 */
public abstract class AbstractJaxbModelObject implements ModelObjectComplete {

    protected AbstractJaxbModelObject() {
        super();
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @SuppressWarnings("unused")
    void beforeUnmarshal(Unmarshaller u, Object parent) throws Exception {
    }

    @SuppressWarnings("unused")
    void afterUnmarshal(Unmarshaller u, Object parent) throws Exception {
        CollectionUtils.makeUnmodifiableAndNullSafe(this);
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }
}
