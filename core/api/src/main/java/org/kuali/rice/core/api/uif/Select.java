package org.kuali.rice.core.api.uif;

import java.util.List;

/** A select control. */
public interface Select extends Sized, KeyLabeled {

    /**
     * Gets an immutable list of Grouped keyLabel pairs.  When this list is non-empty,
     * {@link #getKeyLabels()} must be empty.  Cannot be null.
     *
     * @return the list of groups.
     */
    List<? extends SelectGroup> getGroups();

    /**
     * Whether the select control allows selection of multiple values.  defaults to false.
     *
     * @return allows multiple selections
     */
    boolean isMultiple();
}
