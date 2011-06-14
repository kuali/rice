package org.kuali.rice.krad.uif.core;

import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection filter that removes inactive lines from a collection whose line types
 * implement the <code>Inactivatable</code> interface
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ActiveCollectionFilter implements CollectionFilter {
    private static final long serialVersionUID = 3273495753269940272L;

    /**
     * Iterates through the collection and if the collection line type implements <code>Inactivatable</code>,
     * inactive lines are removed from the underlying collection in the model
     *
     * @see CollectionFilter#filter(org.kuali.rice.krad.uif.container.View, Object, org.kuali.rice.krad.uif.container.CollectionGroup)
     */
    @Override
    public void filter(View view, Object model, CollectionGroup collectionGroup) {
        // get the collection for this group from the model
        List<Object> modelCollection =
                ObjectPropertyUtils.getPropertyValue(model, collectionGroup.getBindingInfo().getBindingPath());

        // iterate through and filter out inactive records
        List<Object> activeRecords = new ArrayList<Object>();
        for (Object line : modelCollection) {
            if (line instanceof Inactivatable) {
                boolean active = ((Inactivatable) line).isActive();
                if (active) {
                    activeRecords.add(line);
                }
            }
        }

        // update collection in model
        ObjectPropertyUtils.setPropertyValue(model, collectionGroup.getBindingInfo().getBindingPath(), activeRecords);
    }
}
