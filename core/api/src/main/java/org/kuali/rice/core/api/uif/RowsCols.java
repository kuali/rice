package org.kuali.rice.core.api.uif;

/**
 * A Control with a vertical and horizontal size based on number of rows and columns.
 */
public interface RowsCols {

    /**
     * The rows value to make the control (The vertical size).  This field can be null. Cannot be less than 1.
     *
     * @return the rows value or null.
     */
    Integer getRows();

    /**
     * The cols value to make the control (The horizontal size).  This field can be null. Cannot be less than 1.
     *
     * @return the cols value or null.
     */
    Integer getCols();
}
