/**
 * Copyright 2005-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.widget;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.view.View;

/**
 * Allows client-side reordering of the group contents
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Reorderer extends WidgetBase {
    private static final long serialVersionUID = 6142957061046219120L;

    private String movableStyleClass;

    public Reorderer() {
        super();
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Adds the movable style class to each group item</li>
     * <li>Prepares the movable widget option based on the movable style class</li>
     * </ul>
     */
    @Override
    public void performFinalize(View view, Object model, Component component) {
        super.performFinalize(view, model, component);

        if (!(component instanceof Group)) {
            throw new RiceIllegalArgumentException("Parent component for Reorderer widget must be a group.");
        }

        if (StringUtils.isNotBlank(movableStyleClass)) {
            for (Component item : ((Group) component).getItems()) {
                item.addStyleClass(movableStyleClass);
            }

            // add the default movable class to the selectors option if not already configured
            if (!getComponentOptions().containsKey(UifConstants.ReordererOptionKeys.SELECTORS)) {
                String selectorsOption =
                        "{" + UifConstants.ReordererOptionKeys.MOVABLES + " : 'span." + movableStyleClass + "' }";
                getComponentOptions().put(UifConstants.ReordererOptionKeys.SELECTORS, selectorsOption);
            }
        }
    }

    /**
     * Returns the style class for the item spans that will identify a movable element
     *
     * <p>
     * Given style class will be used to build a jQuery selector that is then passed to the
     * reorderer widget through the options
     * </p>
     *
     * @return String style class
     */
    public String getMovableStyleClass() {
        return movableStyleClass;
    }

    /**
     * Setter for the style class that identifies movable elements (spans)
     *
     * @param movableStyleClass
     */
    public void setMovableStyleClass(String movableStyleClass) {
        this.movableStyleClass = movableStyleClass;
    }
}
