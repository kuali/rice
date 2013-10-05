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
package org.kuali.rice.krad.uif.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.service.ViewHelperService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * ViewIndexTest has various tests for ViewIndex
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewIndexTest {
    @Before
    public void setUp() throws Exception {

    }

    /**
     * test that clear indexes after render does not clear fields with a value for
     * refreshWhenChanged and their nested controls
     * 
     * @throws Exception
     */
    @Test
    public void testClearIndexesAfterRender() throws Exception {
        // create an input field
        final InputField field = ViewLifecycle.encapsulateInitialization(new Callable<InputField>(){
            @Override
            public InputField call() throws Exception {
                InputField field = new InputField();
                BindingInfo bindingInfo = new BindingInfo();
                bindingInfo.setBindingPath("property1");
                field.setBindingInfo(bindingInfo);
                String fieldId = "field1";
                field.setId(fieldId);

                List<String> refreshWhenChangedPropertyNames = field.getRefreshWhenChangedPropertyNames();
                refreshWhenChangedPropertyNames = refreshWhenChangedPropertyNames == null ?
                        new ArrayList<String>() : new ArrayList<String>(refreshWhenChangedPropertyNames);
                refreshWhenChangedPropertyNames.add("#lp.allDay eq true");
                field.setRefreshWhenChangedPropertyNames(refreshWhenChangedPropertyNames);

                // set a control
                TextControl textControl = new TextControl();
                String controlId = "text1";
                textControl.setId(controlId);
                field.setControl(textControl);
                return field;
            }
        });

        final ViewIndex viewIndex = new ViewIndex();
        View view = mock(View.class);
        ViewHelperService helper = mock(ViewHelperService.class);
        when(view.getViewHelperService()).thenReturn(helper);

        ViewLifecycle.encapsulateLifecycle(view, new Runnable(){
            @Override
            public void run() {
                Component[] components = {field.copy()};

                //add to view index
                for (Component component : components) {
                    viewIndex.indexComponent(component);
                    viewIndex.addInitialComponentStateIfNeeded(component);
                }

                // verify initial view index state
                for (Component component : components) {
                    assertNotNull(viewIndex.getComponentById(component.getId()));
                }

                viewIndex.clearIndexesAfterRender();
                // confirm that the index still has the components
                for (Component component : components) {
                    assertNotNull(viewIndex.getComponentById(component.getId()));
                    assertTrue(viewIndex.getInitialComponentStates().containsKey(component.getId()));
                }
            }});
        
    }
}
