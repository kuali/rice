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
package org.kuali.rice.krad.uif.field;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * test various FieldBase methods
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FieldBaseTest {

    View view = null;

    @Before
    public void setUp() {
        view = Mockito.mock(View.class);
        ViewHelperService mockViewHelperService = mock(ViewHelperService.class);
        when(view.getViewHelperService()).thenReturn(mockViewHelperService);
    }

    @Test
    /**
     * Tests rendering on required messages
     *
     * @see KULRICE-7130
     */
    public void testRequiredMessageDisplay() throws Exception {

        // create mock component
        //        View mockView =  mock(View.class);
        //        ViewHelperService mockViewHelperService = mock(ViewHelperService.class);
        //        when(mockView.getViewHelperService()).thenReturn(mockViewHelperService);
        //        when(mockView.copy()).thenReturn(mockView);
        //        when(mockView.clone()).thenReturn(mockView);

        ViewLifecycle.encapsulateLifecycle(view, null, null, new Runnable() {
            @Override
            public void run() {
                Object nullModel = null;
                Component mockComponent = mock(Component.class);

                // build asterisk required message and mock label to test rendering
                Label mockLabel = new Label();
                Message message = new Message();
                message.setMessageText("*");
                message.setRender(true);

                FieldBase fieldBase = new FieldBase();
                fieldBase.setFieldLabel(mockLabel);
                fieldBase.setRequired(true);
                fieldBase.setReadOnly(false);

                FieldBase fieldBaseCopy = CopyUtils.copy(fieldBase);
                fieldBaseCopy.performFinalize(nullModel, mockComponent);
                assertTrue(fieldBaseCopy.getFieldLabel().isRenderRequiredIndicator());

                // required and readonly -  do not render
                fieldBaseCopy = CopyUtils.copy(fieldBase);
                fieldBaseCopy.setReadOnly(true);
                fieldBaseCopy.performFinalize(nullModel, mockComponent);
                assertFalse(fieldBaseCopy.getFieldLabel().isRenderRequiredIndicator());
            }
        });

    }
}