package org.kuali.rice.core.api.uif;

import java.util.Map;

/**
 * A Control with key-label pairs of values for a user to select from.
 */
public interface KeyLabeled {

    /**
     * The key-label pairs to display on the control.  This field cannot be null or an empty map.
     *
     * @return a map containing values.
     */
    Map<String, String> getKeyLabels();
}
