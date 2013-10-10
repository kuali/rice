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
package org.kuali.rice.krad.uif.field;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.view.View;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * test various FieldBase methods
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FieldBaseTest {

    @Test
    /**
     * Tests rendering on required messages
     *
     * @see KULRICE-7130
     */
    public void testRequiredMessageDisplay() throws Exception {

        // create mock objects for view, view helper service, model, and component
        View mockView =  mock(View.class);
        ViewHelperService mockViewHelperService = mock(ViewHelperService.class);
        when(mockView.getViewHelperService()).thenReturn(mockViewHelperService);
        when(mockView.copy()).thenReturn(mockView);
        final Object nullModel = null;
        final Component mockComponent = mock(Component.class);

        // build asterisk required message and mock label to test rendering
        final Label mockLabel = mock(Label.class);
        final Message message = ViewLifecycle.encapsulateInitialization(new Callable<Message>(){
            @Override
            public Message call() throws Exception {
                Message message = new Message();
                message.setMessageText("*");
                message.setRender(true);
                when(mockLabel.copy()).thenReturn(mockLabel);
                return message;
            }});

        // required and not readonly - render
        final FieldBase fieldBase = ViewLifecycle.encapsulateInitialization(new Callable<FieldBase>(){
            @Override
            public FieldBase call() throws Exception {
                FieldBase fieldBase = new FieldBase();
                fieldBase.setFieldLabel(mockLabel);
                fieldBase.setRequired(true);
                fieldBase.setReadOnly(false);
                return fieldBase;
            }});

        Runnable finalizeField = new Runnable(){
            @Override
            public void run() {
                when(mockLabel.getRequiredMessage()).thenReturn(message.<Message> copy());
                fieldBase.<FieldBase> copy().performFinalize(nullModel, mockComponent);
            }};
            
        ViewLifecycle.encapsulateLifecycle(mockView, null, null, null, finalizeField);
        assertTrue(fieldBase.getFieldLabel().getRequiredMessage().isRender());

        // required and readonly -  do not render
        ViewLifecycle.encapsulateInitialization(new Callable<Void>(){
            @Override
            public Void call() throws Exception {
                fieldBase.setReadOnly(true);
                return null;
            }});

        ViewLifecycle.encapsulateLifecycle(mockView, null, null, null, finalizeField);
        assertFalse(fieldBase.getFieldLabel().getRequiredMessage().isRender());
    }
}