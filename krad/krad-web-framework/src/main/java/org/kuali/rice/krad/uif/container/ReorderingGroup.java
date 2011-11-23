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
package org.kuali.rice.krad.uif.container;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.widget.Reorderer;

import java.util.List;

/**
 * Group implementation that supports reordering of the group items
 *
 * <p>
 * Uses a {@link org.kuali.rice.krad.uif.widget.Reorderer} widget to perform the reordering client side
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ReorderingGroup extends Group {
    private static final long serialVersionUID = -9069458348367183223L;

    private Reorderer reorderer;

    public ReorderingGroup() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(reorderer);

        return components;
    }

    /**
     * Widget that will perform the reordering of the group's items client side
     *
     * @return Reorderer widget instance
     */
    public Reorderer getReorderer() {
        return reorderer;
    }

    /**
     * Setter for the groups reorderer widget
     *
     * @param reorderer
     */
    public void setReorderer(Reorderer reorderer) {
        this.reorderer = reorderer;
    }
}
