package org.kuali.rice.kew.api.document.lookup;

import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;

/**
 * Holds an element of data returned as part of a {@code DocumentLookupResult}.
 * This is essentially represented by an instance of a {@code DocumentAttribute} which
 * defines the name of the data element as well as it's raw value.  This data also
 * stores a "display" value which is used for front-end rendering of the data
 * and may contain html markup.
 *
 * <p>This structure also provides a value </p>
 */
public interface DocumentLookupResultDataContract<T> {

    DocumentAttribute<T> getDocumentAttribute();

    String getDisplayValue();

}
