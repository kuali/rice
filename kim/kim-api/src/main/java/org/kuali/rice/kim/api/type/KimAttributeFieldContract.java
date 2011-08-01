package org.kuali.rice.kim.api.type;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.uif.AttributeField;

/**
 * An dynamic attribute for kim.
 */
public interface KimAttributeFieldContract extends Identifiable {

    /**
     * Gets the attribute field definition.  Cannot be null.
     *
     * @return the field
     */
    AttributeField getAttributeField();

    /**
     * Whether the attribute is a "unique" attribute according to KIM
     * @return unique status
     */
    boolean isUnique();
}
