package org.kuali.rice.core.api.uif;

/**
 * Describes an attribute field which is used as part of a lookup.  Includes the base {@code RemotableAttributeField}
 * definition as well as additional information about the field and how it gets rendered and handled as part of a
 * lookup.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LookupAttributeField {

    /**
     * Returns the {@code AttributeField} definition for this lookup attribute.  Cannot be null.
     *
     * @return the attribute field definition, cannot be null
     */
    AttributeField getAttributeField();

    /**
     * Returns true if this field should be included as part of the lookup criteria, false if not.
     *
     * @return true if this field should be included as part of the lookup criteria, false if not
     */
    boolean isInCriteria();

    /**
     * Returns true if this field should be included in the result set of the lookup, false if not.
     *
     * @return true if this field should be included in the result set of the lookup, false if not
     */
    boolean isInResults();

}
