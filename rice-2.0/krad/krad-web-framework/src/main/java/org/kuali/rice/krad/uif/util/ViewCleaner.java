/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif.util;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.PropertyReplacer;
import org.kuali.rice.krad.uif.container.CollectionFilter;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.PageGroup;
import org.kuali.rice.krad.uif.field.ActionField;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.modifier.ComponentModifier;
import org.kuali.rice.krad.uif.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class for trimming component instances for storage
 *
 * <p>
 * Invoked to trim the view instance before storing on the form as the post view. Certain information is keep
 * around to support post methods that need to operate on the previous view configuration. Examples include component
 * refresh and collection add/delete line.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewCleaner {

    /**
     * Cleans a view instance removing all pages except the current page and then invoking the view
     * index to perform cleaning on contained components
     *
     * @param view - view instance to clean
     */
    public static void cleanView(View view) {
        view.setApplicationHeader(null);
        view.setApplicationFooter(null);
        view.setNavigation(null);
        view.setPage(null);
        view.setViewMenuLink(null);
        view.setClientSideState(new HashMap<String, Object>());

        // clear all view pages exception the current page
        PageGroup currentPage = view.getCurrentPage();

        List<Component> pages = new ArrayList<Component>();
        pages.add(currentPage);
        view.setItems(pages);

        cleanContainer(view);

        view.getViewIndex().clearIndexesAfterRender();
    }

    /**
     * Cleans a collection group instance removing the items and collection prototypes (note add line fields
     * are keep around to support the add line action)
     *
     * @param collectionGroup - collection group instance to clean
     */
    public static void cleanCollectionGroup(CollectionGroup collectionGroup) {
        collectionGroup.setAddLineLabelField(null);
        collectionGroup.setAddLineActionFields(new ArrayList<ActionField>());
        collectionGroup.setActionFields(new ArrayList<ActionField>());
        collectionGroup.setSubCollections(new ArrayList<CollectionGroup>());
        collectionGroup.setActiveCollectionFilter(null);
        collectionGroup.setFilters(new ArrayList<CollectionFilter>());

        cleanContainer(collectionGroup);
    }

    /**
     * General purpose method to clean any container, removes all nested components except the items list
     *
     * @param container - container instance to clean
     */
    public static void cleanContainer(Container container) {
        container.setHeader(null);
        container.setFooter(null);
        container.setHelp(null);
        container.setLayoutManager(null);
        container.setInstructionalMessageField(null);
        container.setComponentOptions(new HashMap<String, String>());
        container.setComponentModifiers(new ArrayList<ComponentModifier>());
        container.setPropertyReplacers(new ArrayList<PropertyReplacer>());
    }

    /**
     * Cleans an input field instance removing the control and inherited component properties
     *
     * @param inputField - input field instance to clean
     */
    public static void cleanInputField(InputField inputField) {
        inputField.setControl(null);
        inputField.setInstructionalMessageField(null);
        inputField.setConstraintMessageField(null);
        inputField.setFieldDirectInquiry(null);
        inputField.setFieldInquiry(null);
        inputField.setLabelField(null);
        inputField.setComponentOptions(new HashMap<String, String>());
        inputField.setComponentModifiers(new ArrayList<ComponentModifier>());
        inputField.setPropertyReplacers(new ArrayList<PropertyReplacer>());
    }
}
