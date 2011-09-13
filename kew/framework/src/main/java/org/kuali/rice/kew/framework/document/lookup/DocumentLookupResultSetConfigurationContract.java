package org.kuali.rice.kew.framework.document.lookup;

import org.kuali.rice.core.api.uif.AttributeField;

import java.util.List;

/**
 * Defines how the display of results on the document lookup should be customized and configured.  This class can be
 * used to remove standard fields from the result set that are typically show on document lookup results.  This class
 * is also used to add additional fields
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentLookupResultSetConfigurationContract {

    /**
     * Returns true if the custom field names returned by {@link #getCustomFieldNamesToAdd()} should be used to define
     * the order of searchable attributes as well as additional custom additional fields.  Returns false if any
     * searchable attribute values should be included in the result set according to their existing configuration.
     *
     * @return true if custom field names defined by this object should override any default searchable attribute result
     * field display behavior, flase if searchable attribute fields should still be displayed in the result set
     * according to their own configuration
     */
    boolean isOverrideSearchableAttributes();

    /**
     * Returns a list of field names of custom fields representing document attributes which should be added to the
     * result set.  This may contains fields that are defined in {@link #getAdditionalAttributeFields()} or also fields
     * defined as part of a {@link org.kuali.rice.kew.framework.document.attribute.SearchableAttribute} (see {@code isOverrideSearchableAttributes()}).
     *
     * @return a list of field names of custom document attributes which should be added to the result set, can be an
     * empty or null list in which case no fields will be added
     */
    List<String> getCustomFieldNamesToAdd();

    /**
     * Returns a list of the standard (built-in) document lookup result fields which should not be displayed in the
     * result set.  The document lookup implementation should do it's best to honor the request to remove standard
     * fields from the result set, but it is free to ignore such requests if needed.  An example of this would be a
     * preference for the implementation of document lookup that requires certain result set fields to remain (such as
     * the document id and route log which is usually recommended to display).
     *
     * @return a list of standard result fields to remove from inclusion in the result set, may be an empty or null
     * list if no standard result fields should be removed
     */
    List<StandardResultField> getStandardResultFieldsToRemove();

    /**
     * Gets attribute field definitions for additional attributes that may be displayed in the result set.  This simply
     * defines the attribute field definition for each of these fields, their inclusion here does not necessarily mean
     * they will be visible in the result set.  This is controlled primarily by {@link #getCustomFieldNamesToAdd()}.
     *
     * @return a list containing additional attribute fields to define for use when constructing the result set, this
     * method can return a null or empty list if there are no additional attribute fields to define
     */
    List<? extends AttributeField> getAdditionalAttributeFields();

}
