package org.kuali.rice.kns.uif.core;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kns.uif.container.CollectionGroup;
import org.kuali.rice.kns.uif.container.MaintenanceView;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.util.ObjectPropertyUtils;
import org.kuali.rice.kns.util.KNSConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection filter for maintenance groups that removes inactive lines if certain
 * conditions are met
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceActiveCollectionFilter implements CollectionFilter {

    private String oldBindingObjectPath;

    /**
     * Iterates through the collection and if the collection line type implements <code>Inactivatable</code>
     * and removes inactive lines that are ok to not show
     *
     * <p>
     * In the case of a new line being added, the user is not allowed to hide the record (even if it is inactive).
     * Likewise in the case of an edit where the active flag has changed between the old and new side, the user
     * is not allowed to hide
     * </p>
     *
     * @see org.kuali.rice.kns.uif.core.CollectionFilter#filter(org.kuali.rice.kns.uif.container.View, Object, org.kuali.rice.kns.uif.container.CollectionGroup)
     */
    @Override
    public void filter(View view, Object model, CollectionGroup collectionGroup) {
        MaintenanceView maintenanceView = (MaintenanceView) view;
        boolean isMaintenanceEdit = KNSConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceView.getMaintenanceAction());

        // get the collection for this group from the model
        List<Object> newCollection =
                ObjectPropertyUtils.getPropertyValue(model, collectionGroup.getBindingInfo().getBindingPath());

        // if edit action, get collection from old data object
        List<Object> oldCollection = null;
        if (isMaintenanceEdit) {
            String oldCollectionBindingPath = StringUtils.replaceOnce(collectionGroup.getBindingInfo().getBindingPath(),
                    collectionGroup.getBindingInfo().getBindingObjectPath(), oldBindingObjectPath);
            oldCollection = ObjectPropertyUtils.getPropertyValue(model, oldCollectionBindingPath);
        }

        // iterate through and filter out inactive records
        List<Object> showRecords = new ArrayList<Object>();
        for (int i = 0; i < newCollection.size(); i++) {
            Object line = newCollection.get(i);
            if (line instanceof Inactivatable) {
                boolean active = ((Inactivatable) line).isActive();

                if (active) {
                    showRecords.add(line);
                } else if (isMaintenanceEdit && (oldCollection != null) && (oldCollection.size() > i)) {
                    // if active status has changed, show record
                    Inactivatable oldLine = (Inactivatable) oldCollection.get(i);
                    if (oldLine.isActive()) {
                        showRecords.add(line);
                    }
                } else {
                    // TODO: if newly added line, show record
                }
            }
        }

        // update collection in model
        ObjectPropertyUtils.setPropertyValue(model, collectionGroup.getBindingInfo().getBindingPath(), showRecords);
    }

    /**
     * Gives the binding path to the old data object for comparison, used to
     * get the active status of the old object
     *
     * @return String binding path
     */
    public String getOldBindingObjectPath() {
        return oldBindingObjectPath;
    }

    /**
     * Setter for the path to the old data object
     *
     * @param oldBindingObjectPath
     */
    public void setOldBindingObjectPath(String oldBindingObjectPath) {
        this.oldBindingObjectPath = oldBindingObjectPath;
    }
}
