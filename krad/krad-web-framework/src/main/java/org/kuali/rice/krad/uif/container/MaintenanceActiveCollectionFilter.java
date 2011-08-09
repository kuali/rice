package org.kuali.rice.krad.uif.container;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection filter for maintenance groups that removes inactive lines if certain
 * conditions are met
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceActiveCollectionFilter implements CollectionFilter {
    private static final long serialVersionUID = -6045332235106531456L;

    private String oldBindingObjectPath;

    /**
     * Iterates through the collection and if the collection line type implements <code>Inactivatable</code>
     * active indexes are added to the show indexes list
     *
     * <p>
     * In the case of a new line being added, the user is not allowed to hide the record (even if it is inactive).
     * Likewise in the case of an edit where the active flag has changed between the old and new side, the user
     * is not allowed to hide
     * </p>
     *
     * @see CollectionFilter#filter(org.kuali.rice.krad.uif.view.View, Object, org.kuali.rice.krad.uif.container.CollectionGroup)
     */
    @Override
    public List<Integer> filter(View view, Object model, CollectionGroup collectionGroup) {

        // get the collection for this group from the model
        List<Object> newCollection =
                ObjectPropertyUtils.getPropertyValue(model, collectionGroup.getBindingInfo().getBindingPath());

        // Get collection from old data object
        List<Object> oldCollection = null;
        String oldCollectionBindingPath = null;
        oldCollectionBindingPath = StringUtils.replaceOnce(collectionGroup.getBindingInfo().getBindingPath(),
                    collectionGroup.getBindingInfo().getBindingObjectPath(), oldBindingObjectPath);
        oldCollection = ObjectPropertyUtils.getPropertyValue(model, oldCollectionBindingPath);

        // iterate through and add only active indexes
        List<Integer> showIndexes = new ArrayList<Integer>();
        for (int i = 0; i < newCollection.size(); i++) {
            Object line = newCollection.get(i);
            if (line instanceof Inactivatable) {
                boolean active = ((Inactivatable) line).isActive();
                if ((oldCollection != null) && (oldCollection.size() > i)) {
                    // if active status has changed, show record
                    Inactivatable oldLine = (Inactivatable) oldCollection.get(i);
                    if (oldLine.isActive()) {
                        showIndexes.add(i);
                    }
                } else {
                    // TODO: if newly added line, show record
                    // If only new and no old add the newline
                    if (active) {
                        showIndexes.add(i);
                    }
                }
            }
        }

        return showIndexes;
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
