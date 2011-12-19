package org.kuali.rice.krad.uif.field;

import org.kuali.rice.krad.uif.component.ComponentSecurity;

/**
 * Field security adds the edit in line and view in line flags to the standard component security
 *
 * <p>
 * These flags are only applicable when the field is part of a collection group. They indicate there is security
 * on the field within the collection line
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FieldSecurity extends ComponentSecurity {

    private boolean editInLineAuthz;
    private boolean viewInLineAuthz;

    public FieldSecurity() {
        super();

        editInLineAuthz = false;
        viewInLineAuthz = false;
    }

    /**
     * Indicates whether the field has edit in line authorization and KIM should be consulted
     *
     * @return boolean true if the field has edit in line authorization, false if not
     */
    public boolean isEditInLineAuthz() {
        return editInLineAuthz;
    }

    /**
     * Setter for the edit in line authorization flag
     *
     * @param editInLineAuthz
     */
    public void setEditInLineAuthz(boolean editInLineAuthz) {
        this.editInLineAuthz = editInLineAuthz;
    }

    /**
     * Indicates whether the field has view in line unmask authorization and KIM should be consulted
     *
     * @return boolean true if the field has view in line unmask authorization, false if not
     */
    public boolean isViewInLineAuthz() {
        return viewInLineAuthz;
    }

    /**
     * Setter for the view in line authorization flag
     *
     * @param viewInLineAuthz
     */
    public void setViewInLineAuthz(boolean viewInLineAuthz) {
        this.viewInLineAuthz = viewInLineAuthz;
    }

}
