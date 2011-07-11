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
     * active indexes are added to the show indexes list
     *
     * @see CollectionFilter#filter(org.kuali.rice.krad.uif.container.View, Object, org.kuali.rice.krad.uif.container.CollectionGroup)
     */
    @Override
    public List<Integer> filter(View view, Object model, CollectionGroup collectionGroup) {
        // get the collection for this group from the model
        List<Object> modelCollection =
                ObjectPropertyUtils.getPropertyValue(model, collectionGroup.getBindingInfo().getBindingPath());

        // iterate through and add only active indexes
        List<Integer> showIndexes = new ArrayList<Integer>();
        if (modelCollection != null) {
            int lineIndex = 0;
            for (Object line : modelCollection) {
                if (line instanceof Inactivatable) {
                    boolean active = ((Inactivatable) line).isActive();
                    if (active) {
                        showIndexes.add(lineIndex);
                    }
                }
                lineIndex++;
            }
        }

        return showIndexes;
    }
}
