package org.kuali.rice.core.mo;

import java.io.Serializable;

/**
 * All model objects in rice should be {@link Serializable} & override {@link #toString()}.
 */
public interface ModelObjectBasic extends Serializable {

    /**
     * This will return a proper string representation of the Model Object.
     * All of the fields comprising the "public" api should be represented in
     * the return value.
     *
     * @return the string representation
     */
    @Override
    String toString();
}
