package org.kuali.rice.kns.uif.core;

import org.kuali.rice.kns.uif.container.CollectionGroup;
import org.kuali.rice.kns.uif.container.View;

import java.io.Serializable;

/**
 * Provides filtering on collection data within a <code>CollectionGroup</code>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface CollectionFilter extends Serializable {

    /**
     * Invoked to filter the collection data before the collection group is
     * built. Note the collection should be retrieved from the model and updated
     * back to the model
     *
     * @param view - view instance for the collection group
     * @param model - object containing the view data and from which the collection should be pulled/updated
     * @param collectionGroup - collection group instance containing configuration for the collection
     */
    public void filter(View view, Object model, CollectionGroup collectionGroup);
}
