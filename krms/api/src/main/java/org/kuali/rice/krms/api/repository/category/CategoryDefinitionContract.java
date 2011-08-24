package org.kuali.rice.krms.api.repository.category;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;

/**
 * Defines the category definition.
 */
public interface CategoryDefinitionContract extends Identifiable, Versioned {

    /**
     * Returns the name of the category definition.  The combination of name and namespaceCode
     * represent a unique business key for the category definition.  The name should never be
     * null or blank.
     *
     * @return the name of the category definition, should never be null or blank
     */
    String getName();

    /**
     * Returns the namespace of the category definition.  The combination of
     * namespace and name represent a unique business key for the category
     * definition.  The namespace should never be null or blank.
     *
     * @return the namespace of the category definition, should never be null or blank
     */
    String getNamespace();

}
