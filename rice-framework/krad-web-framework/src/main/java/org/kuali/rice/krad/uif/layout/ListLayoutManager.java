/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.uif.layout;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ListAware;
import org.kuali.rice.krad.uif.container.Group;

/**
 * List layout manager is a layout manager for group types to output their items as either ordered or
 * unordered lists.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "listLayout-bean", parent = "Uif-ListLayout"),
        @BeanTag(name = "orderedListLayout-bean", parent = "Uif-OrderedListLayout")})
public class ListLayoutManager extends LayoutManagerBase {
    private static final long serialVersionUID = -8611267646944565117L;

    private boolean orderedList;

    public ListLayoutManager() {
        super();
    }

    /**
     * Iterates through the groups items and sets the rendered in list boolean.
     *
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, Component component) {
        super.performApplyModel(model, component);

        Group parentGroup = (Group) component;

        for (Component item : parentGroup.getItems()) {
            if (ListAware.class.isAssignableFrom(item.getClass())) {
                ((ListAware) item).setRenderedInList(true);
            }
        }
    }

    /**
     * If true, this list layout is an ordered list (ol).  Otherwise, the the layout is an unordered list (ul).
     *
     * @return true if orderedList, false if unordered
     */
    @BeanTagAttribute(name = "orderedList")
    public boolean isOrderedList() {
        return orderedList;
    }

    /**
     * @see ListLayoutManager#isOrderedList()
     */
    public void setOrderedList(boolean orderedList) {
        this.orderedList = orderedList;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#copyProperties(Object)
     */
    @Override
    protected <T> void copyProperties(T listLayoutManager) {
        super.copyProperties(listLayoutManager);

        ListLayoutManager listLayoutManagerCopy = (ListLayoutManager) listLayoutManager;

        listLayoutManagerCopy.setOrderedList(this.orderedList);
    }
}
