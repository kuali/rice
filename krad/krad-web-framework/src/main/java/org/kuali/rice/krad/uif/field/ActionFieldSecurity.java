package org.kuali.rice.krad.uif.field;

/**
 * Action field security adds the take action flags to the standard component security
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActionFieldSecurity extends FieldSecurity {
    private static final long serialVersionUID = 585138507596582667L;

    private boolean performActionAuthz;
    private boolean performLineActionAuthz;

    public ActionFieldSecurity() {
        super();

        performActionAuthz = false;
        performLineActionAuthz = false;
    }

    /**
     * Indicates whether the action field has take action authorization and KIM should be consulted
     *
     * @return boolean true if the action field has perform action authorization, false if not
     */
    public boolean isPerformActionAuthz() {
        return performActionAuthz;
    }

    /**
     * Setter for the perform action authorization flag
     *
     * @param performActionAuthz
     */
    public void setPerformActionAuthz(boolean performActionAuthz) {
        this.performActionAuthz = performActionAuthz;
    }

    /**
     * Indicates whether the line action field has take action authorization and KIM should be consulted
     *
     * @return boolean true if the line action field has perform action authorization, false if not
     */
    public boolean isPerformLineActionAuthz() {
        return performLineActionAuthz;
    }

    /**
     * Setter for the perform line action authorization flag
     *
     * @param performLineActionAuthz
     */
    public void setPerformLineActionAuthz(boolean performLineActionAuthz) {
        this.performLineActionAuthz = performLineActionAuthz;
    }
}
