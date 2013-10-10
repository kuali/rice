/**
 * Copyright 2005-2013 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.SelectControl;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.view.View;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * various tests for CollectionGroup
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionGroupTest {
    private CollectionGroup group;

    @Before
    public void setup() {
        group = ViewLifecycle.encapsulateInitialization(new Callable<CollectionGroup>(){
            @Override
            public CollectionGroup call() throws Exception {
                CollectionGroup group = new CollectionGroup();
                List<Component> items = new ArrayList<Component>();
                InputField field = new InputField();
                field.setControl(new SelectControl());
                items.add(field);
                items.add(new TextAreaControl());
                group.setItems(items);
                return group;
            }});
    }

    /**
     * test that the collection group is set in all nested components' contexts
     */
    @Test
    public void testPushCollectionGroupToReference() {
        View view = mock(View.class);
        ViewHelperService helper = mock(ViewHelperService.class);
        when(view.getViewHelperService()).thenReturn(helper);
        ViewLifecycle.encapsulateLifecycle(view, null, null, null, new Runnable(){
            @Override
            public void run() {
                CollectionGroup mutableGroup = group.copy();
                mutableGroup.pushCollectionGroupToReference();
                for (Component component: mutableGroup.getItems()) {
                    testForCollectionGroupInContext(component, mutableGroup);
                }
                Control innerControl = ((InputField) mutableGroup.getItems().get(0)).getControl();
                testForCollectionGroupInContext(innerControl, mutableGroup);
            }});
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