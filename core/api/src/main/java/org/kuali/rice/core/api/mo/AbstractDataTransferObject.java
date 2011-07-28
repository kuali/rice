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
 *     Note: the equals/hashCode implementation excludes {@value CoreConstants.CommonElements.FUTURE_ELEMENTS} field.
 *     This element should be present on all jaxb annotated classes.
 * </ol>
 *
 * <b>Important: all classes extending this class must be immutable</b>
 */
public abstract class AbstractDataTransferObject implements ModelObjectComplete {

    private transient volatile Integer _hashCode;
    private transient volatile String _toString;

    protected AbstractDataTransferObject() {
        super();
    }

    @Override
    public int hashCode() {
        //using DCL idiom to cache hashCodes.  Hashcodes on immutable objects never change.  They can be safely cached.
        //see effective java 2nd ed. pg. 71
        Integer h = _hashCode;
        if (h == null) {
            synchronized (this) {
                h = _hashCode;
                if (h == null) {
                    _hashCode = h = Integer.valueOf(HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE));
                }
            }
        }

        return h.intValue();
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        //using DCL idiom to cache toString.  toStrings on immutable objects never change.  They can be safely cached.
        //see effective java 2nd ed. pg. 71
        String t = _toString;
        if (t == null) {
            synchronized (this) {
                t = _toString;
                if (t == null) {
                    _toString = t = ToStringBuilder.reflectionToString(this);
                }
            }
        }

        return t;
    }

    @SuppressWarnings("unused")
    protected void beforeUnmarshal(Unmarshaller u, Object parent) throws Exception {
    }

    @SuppressWarnings("unused")
    protected void afterUnmarshal(Unmarshaller u, Object parent) throws Exception {
        CollectionUtils.makeUnmodifiableAndNullSafe(this);
    }

    /**
     * Defines some internal constants used on this class.
     */
    protected static class Constants {
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }
}
