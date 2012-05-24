/*
 * Copyright 2006-2012 The Kuali Foundation
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

package edu.sampleu.demo.layout;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: 5/23/12
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class UifGeneratedTestViewHelperServiceImpl extends ViewHelperServiceImpl {

    @Override
    protected void performCustomInitialization(View view, Component component) {
        super.performCustomInitialization(view, component);
        List<Component> fields = new ArrayList<Component>();
        if(component instanceof PageGroup && component.getId().equals("UifGeneratedFields-Page1")){
            for(int i=0; i < 400; i++){
                InputField field = ComponentFactory.getInputField();
                field.setId(view.getNextId());
                field.setBaseId(field.getId());
                Control control = ComponentFactory.getTextControl();
                field.setControl(control);
                field.setPropertyName("field1");
                field.setLabel("Field");
                field.setRequired(true);
                fields.add(field);
            }
            ((PageGroup) component).setItems(fields);
        }

    }
}
