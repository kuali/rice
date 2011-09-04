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
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.control.RadioGroupControl;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.LabelField;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class for creating new UIF components from their base definitions
 * in the dictionary
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentFactory {

    private static final Map<String, Component> componentDefinitions = new HashMap<String, Component>();

    protected static final String GROUP = "Group";
    protected static final String LABEL_FIELD = "LabelField";
    protected static final String MESSAGE_FIELD = "MessageField";
    protected static final String TEXT_CONTROL = "TextControl";
    protected static final String RADIO_GROUP_CONTROL = "RadioGroupControl";
    protected static final String RADIO_GROUP_CONTROL_HORIZONTAL = "RadioGroupControlHorizontal";

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

    public static LabelField getLabelField() {
        return (LabelField) getNewComponentInstance(LABEL_FIELD);
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

    public static RadioGroupControl getRadioGroupControlHorizontal() {
        return (RadioGroupControl) getNewComponentInstance(RADIO_GROUP_CONTROL_HORIZONTAL);
    }

    public static Group getGroup() {
        return (Group) getNewComponentInstance(GROUP);
    }

    /**
     * Gets a fresh copy of the component by the id passed in which is translated to the
     * corresponding dictionary id
     *
     * @param id - id for the component to retrieve
     * @return Component new instance initialized from the dictionary
     */
    public static Component getComponentById(UifFormBase form, String id) {
        Component origComponent = form.getView().getViewIndex().getComponentById(id);
        Component component = getNewComponentInstance(origComponent.getBaseId());

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
        Component origComponent = form.getView().getViewIndex().getComponentById(id);
        Component component = getComponentById(form, id);

        form.getView().getViewHelperService().performComponentLifecycle(form, component, id);

        if (component instanceof Field) {
            ((Field) component).setLabelFieldRendered(((Field) origComponent).isLabelFieldRendered());
        }

        if (component instanceof AttributeField) {
            ((AttributeField) component).setBindingInfo(((AttributeField) origComponent).getBindingInfo());
        }

        if (component instanceof CollectionGroup) {
            ((CollectionGroup) component).setBindingInfo(((CollectionGroup) origComponent).getBindingInfo());
        }

        if (component instanceof Group || component instanceof FieldGroup) {
            List<AttributeField> fields = ComponentUtils.getComponentsOfTypeDeep(component, AttributeField.class);
            String suffix = StringUtils.replaceOnce(component.getId(), component.getBaseId(), "");
            for (AttributeField field : fields) {
                AttributeField origField = (AttributeField) form.getView().getViewIndex().getComponentById(
                        StringUtils.replaceOnce(field.getId(), field.getBaseId(), field.getBaseId() + suffix));
                if (origField != null) {
                    field.setBindingInfo(origField.getBindingInfo());
                    field.setLabelFieldRendered(origField.isLabelFieldRendered());
                }
            }

            List<CollectionGroup> collections = ComponentUtils.getComponentsOfTypeDeep(component,
                    CollectionGroup.class);
            for (CollectionGroup collection : collections) {
                CollectionGroup origField = (CollectionGroup) form.getView().getViewIndex().getComponentById(
                        StringUtils.replaceOnce(collection.getId(), collection.getBaseId(),
                                collection.getBaseId() + suffix));
                if (origField != null) {
                    collection.setBindingInfo(origField.getBindingInfo());
                }
            }
        }

        form.getView().getViewIndex().indexComponent(component);

        return component;
    }

}
