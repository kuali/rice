package org.kuali.rice.krad.uif.container;

import org.kuali.rice.krad.uif.component.ComponentSecurity;

/**
 * Collection Group security is used to flag that permissions exist for the associated {@link CollectionGroup}
 * in KIM and should be checked to determine the associated group, line, and field state. In particular this adds
 * the edit line and view line flags
 *
 * <p>
 * In addition, properties such as additional role and permission details can be configured to use when
 * checking the KIM permissions
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionGroupSecurity extends ComponentSecurity {
    private static final long serialVersionUID = 1134455196763917062L;

    private boolean editLineAuthz;
    private boolean viewLineAuthz;

    public CollectionGroupSecurity() {
        super();

        editLineAuthz = false;
        viewLineAuthz = false;
    }

    /**
     * Indicates whether the collection group line has edit authorization and KIM should be consulted
     *
     * @return boolean true if the line has edit authorization, false if not
     */
    public boolean isEditLineAuthz() {
        return editLineAuthz;
    }

    /**
     * Setter for the edit line authorization flag
     *
     * @param editLineAuthz
     */
    public void setEditLineAuthz(boolean editLineAuthz) {
        this.editLineAuthz = editLineAuthz;
    }

    /**
     * Indicates whether the collection group line has view authorization and KIM should be consulted
     *
     * @return boolean true if the line has view authorization, false if not
     */
    public boolean isViewLineAuthz() {
        return viewLineAuthz;
    }

    /**
     * Setter for the view line authorization flag
     *
     * @param viewLineAuthz
     */
    public void setViewLineAuthz(boolean viewLineAuthz) {
        this.viewLineAuthz = viewLineAuthz;
    }

}
