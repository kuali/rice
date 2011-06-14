/*
 * Copyright 2011 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.control.RadioGroupControl;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.core.Component;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.web.spring.form.UifFormBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating new UIF components from their base definitions
 * in the dictionary
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentFactory {

    private static final Map<String, Component> componentDefinitions = new HashMap<String, Component>();

    protected static final String MESSAGE_FIELD = "MessageField";
    protected static final String TEXT_CONTROL = "TextControl";
    protected static final String RADIO_GROUP_CONTROL = "RadioGroupControl";

    /**
     * Adds a component instance to the factories map of components indexed by the given dictionary id
     *
     * @param componentDictionaryId - id to index component under
     * @param component - component instance to add
     */
    public static void addComponentDefinition(String componentDictionaryId, Component component) {
        componentDefinitions.put(componentDictionaryId, ComponentUtils.copyObject(component));
    }

    /**
     * Returns a new <code>Component</code> instance from the given component id initialized by the
     * corresponding dictionary configuration
     *
     * @param componentDictionaryId - id for the component to retrieve, note this is the id initially
     * assigned and could be the spring generated id (if a static id was not set)
     * @return new component instance or null if no such component definition was found
     */
    public static Component getNewComponentInstance(String componentDictionaryId) {
        if (componentDefinitions.containsKey(componentDictionaryId)) {
            Component component = componentDefinitions.get(componentDictionaryId);

            return ComponentUtils.copyObject(component);
        }

        return null;
    }

    public static MessageField getMessageField() {
        return (MessageField) getNewComponentInstance(MESSAGE_FIELD);
    }

    public static TextControl getTextControl() {
        return (TextControl) getNewComponentInstance(TEXT_CONTROL);
    }

    public static RadioGroupControl getRadioGroupControl() {
        return (RadioGroupControl) getNewComponentInstance(RADIO_GROUP_CONTROL);
    }

    /**
     * Gets a fresh copy of the component by the id passed in which is translated to the
     * corresponding dictionary id
     *
     * @param id - id for the component to retrieve
     * @return Component new instance initialized from the dictionary
     */
    public static Component getComponentById(String id) {
        if (id.contains("_")) {
            id = StringUtils.substringBefore(id, "_");
        }

        Component component = getNewComponentInstance(id);

        return component;
    }

    /**
     * Gets a fresh copy of the component by the id passed in with its lifecycle performed upon it,
     * using the form data passed in
     *
     * @param form - object containing the view data
     * @param id - id for the component to retrieve
     * @return Component instance that has been run through the lifecycle
     */
    public static Component getComponentByIdWithLifecycle(UifFormBase form, String id) {
        String origId = id;

        Component component = getComponentById(id);

        form.getView().getViewHelperService().performComponentLifecycle(form, component, origId);
        form.getView().getViewIndex().indexComponent(component);

        return component;
    }

    protected static DataDictionaryService getDataDictionaryService() {
        return KRADServiceLocatorWeb.getDataDictionaryService();
    }
}
