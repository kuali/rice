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
package org.kuali.rice.krad.uif.container;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.SelectControlBase;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.InputFieldBase;

/**
 * various tests for CollectionGroup
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionGroupTest {
    private CollectionGroup group;

    @Before
    public void setup() {
        group = new CollectionGroupBase();
        List<Component> items = new ArrayList<Component>();
        InputField field = new InputFieldBase();
        field.setControl(new SelectControlBase());
        items.add(field);
        items.add(new TextAreaControl());
        group.setItems(items);
    }

    /**
     * test that the collection group is set in all nested components' contexts
     */
    @Test
    public void testPushCollectionGroupToReference() {
        group.pushCollectionGroupToReference();
        for (Component component : group.getItems()) {
            testForCollectionGroupInContext(component, group);
        }
        Control innerControl = ((InputField) group.getItems().get(0)).getControl();
        testForCollectionGroupInContext(innerControl, group);
    }

    /**
     * test that the collection group is available in the component's contexts
     * 
     * @param component
     */
    private void testForCollectionGroupInContext(Component component, CollectionGroup group) {
        assertTrue("The component does not have the collection group key in the context",
                component.getContext().containsKey(UifConstants.ContextVariableNames.COLLECTION_GROUP));
        assertTrue("The collection group found is not the parent group",
                component.getContext().get(UifConstants.ContextVariableNames.COLLECTION_GROUP) == group);
    }

}