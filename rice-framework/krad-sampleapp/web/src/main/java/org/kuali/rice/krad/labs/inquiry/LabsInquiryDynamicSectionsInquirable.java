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
package org.kuali.rice.krad.labs.inquiry;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.krad.inquiry.InquirableImpl;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.InquiryView;

/**
 * Demonstrates the ability to add new sections and modify old ones.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsInquiryDynamicSectionsInquirable extends InquirableImpl {

    @Override
    public void performCustomInitialization(LifecycleElement component) {
        super.performCustomInitialization(component);

        if (component instanceof InquiryView) {
            InquiryView inquiryView = (InquiryView) component;
            PageGroup pageGroup = inquiryView.getCurrentPage();

            Group oldSection = (Group) pageGroup.getItems().get(0);
            oldSection.setHeaderText(oldSection.getHeaderText() + " - Customized");

            Group newSection = ComponentFactory.getGroupWithDisclosureGridLayout();
            newSection.setHeaderText("Dynamically Added Section");
            DataField newDataField = ComponentFactory.getDataField("newDataField", "Dynamically Added Field");
            newDataField.setForcedValue("This is a dynamically set value.");

            List<Component> fields = new ArrayList<Component>();
            fields.add(newDataField);
            newSection.setItems(fields);

            List<Component> sections = new ArrayList<Component>();
            sections.addAll(pageGroup.getItems());
            sections.add(newSection);
            pageGroup.setItems(sections);
        }

    }
}
