package org.kuali.rice.krms.api.repository.term;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.krms.api.repository.category.CategoryContract;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public interface TermSpecificationContract extends Identifiable, Inactivatable, Versioned {

    /**
     * Gets the name for this {@link TermSpecificationContract}.  This is an important key
     * that must be unique within a namespace, and is used to determine how to resolve any terms
     * having this specification. Will not be null or empty.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the namespace of this {@link TermSpecificationContract}.  Will not be null or empty.
     *
     * @return the namespace of the TermSpecificationDefinitionContract
     */
    public String getNamespace();

    /**
     * Gets the fully qualified class name for the values of any term having this specification.  E.g. if the
     * type of the fact values for the "total dollar amount of a grant" term was {@link java.math.BigDecimal},
     * then the term specification's type would be the String "java.math.BigDecimal".  Will never return null or
     * the empty string.
     *
     * @return the fully qualified name of the java type of values for this term.
     */
    String getType();

    /**
     * Gets the description for this term specification, which will typically be a suitable description for
     * any term having this specification as well.  May return null if no description is specified for the term
     * specification.
     *
     * @return the description for this term specification.
     */
    String getDescription();

    /**
     * Gets an ordered list of the categories which this term specification
     * definition belongs to.  This list can be empty but will never be null.
     *
     * @return the list of categories for this term specification definition.
     */
    List<? extends CategoryContract> getCategories();

}
