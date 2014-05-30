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

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.widget.Accordion;

/**
 * Accordion group class used to stack groups by there header titles in an accordion layout.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "accordionGroup", parent = "Uif-AccordionGroup"),
        @BeanTag(name = "accordionSection", parent = "Uif-AccordionSection"),
        @BeanTag(name = "accordionSubSection", parent = "Uif-AccordionSubSection"),
        @BeanTag(name = "disclosureAccordionSection", parent = "Uif-Disclosure-AccordionSection"),
        @BeanTag(name = "disclosureAccordionSubSection", parent = "Uif-Disclosure-AccordionSubSection")})
public class AccordionGroup extends GroupBase {
    private static final long serialVersionUID = 7230145606607506418L;

    private Accordion accordionWidget;

    /**
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);
        this.addDataAttribute(UifConstants.DataAttributes.TYPE, "Uif-AccordionGroup");
    }

    /**
     * Only groups are supported for this group.
     *
     * {@inheritDoc}
     */
    @Override
    public Set<Class<? extends Component>> getSupportedComponents() {
        Set<Class<? extends Component>> supportedComponents = new HashSet<Class<? extends Component>>();
        supportedComponents.add(Group.class);

        return supportedComponents;
    }

    /**
     * Gets the widget which contains any configuration for the accordion widget component used to render
     * this AccordionGroup.
     *
     * @return the accordionWidget
     */
    @BeanTagAttribute
    public Accordion getAccordionWidget() {
        return this.accordionWidget;
    }

    /**
     * @see AccordionGroup#getAccordionWidget()
     */
    public void setAccordionWidget(Accordion accordionWidget) {
        this.accordionWidget = accordionWidget;
    }
}
