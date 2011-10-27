package org.kuali.rice.krad.uif.container;

import org.kuali.rice.krad.uif.view.View;

import java.io.Serializable;
import java.util.List;

/**
 * Provides filtering on collection data within a <code>CollectionGroup</code>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface CollectionFilter extends Serializable {

    /**
     * Invoked to filter the collection data before the collection group is
     * built. Note the collection should be retrieved from the model and the valid
     * row indexes must be returned in the return list
     *
     * @param view - view instance for the collection group
     * @param model - object containing the view data and from which the collection should be pulled/updated
     * @param collectionGroup - collection group instance containing configuration for the collection
     * @return the list that contains valid row indexes
     */
    public List<Integer> filter(View view, Object model, CollectionGroup collectionGroup);
}
