package org.kuali.rice.core.api.uif;

/**
 * A Select Group for a Select Control.
 */
public interface SelectGroup extends KeyLabeled {
    /**
     * The label for the select group select group. Cannot be null or blank.
     * @return the label
     */
    String getLabel();
}
