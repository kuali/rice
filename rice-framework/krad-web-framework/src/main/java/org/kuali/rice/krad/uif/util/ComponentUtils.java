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
package org.kuali.rice.krad.uif.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.component.Ordered;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.layout.TableLayoutManager;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;
import org.kuali.rice.krad.uif.lifecycle.initialize.AssignIdsTask;
import org.springframework.core.OrderComparator;

/**
 * ComponentUtils is a utility class providing methods to help create and modify {@link Component} instances.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ComponentUtils.class);

    private ComponentUtils() {}

    public static <T extends Component> T copy(T component) {
        return copy(component, null);
    }

    public static <T extends Component> T copy(T component, String idSuffix) {
        if (component == null) {
            return null;
        }

        T copy = CopyUtils.copy(component);

        if (StringUtils.isNotBlank(idSuffix)) {
            updateIdsWithSuffixNested(copy, idSuffix);
        }

        return copy;
    }

    /**
     * Copy a list of components
     * 
     * @param <T> component type
     * @param <T> component type
     * @param components the list of components to copy
     * @return the copied list
     */
    public static <T extends Component> List<T> copy(List<T> components) {
        if (components != null) {
            List<T> componentsCopy = new ArrayList<T>();
            for (T component : components) {
                T copiedComponent = copy(component);
                componentsCopy.add(copiedComponent);
            }

            return componentsCopy;
        }

        return new ArrayList<T>();
    }

    /**
     * Adjusts the ids to contain the given suffix and adds the giving binding prefix for the list of fields.
     *
     * @param <T> component type
     * @param fields list of fields to bind and id
     * @param addBindingPrefix prefix to add to the binding path
     * @param idSuffix id suffix
     */
    public static <T extends Field> void bindAndIdFieldList(List<T> fields, String addBindingPrefix, String idSuffix) {
        updateIdsWithSuffixNested(fields, idSuffix);
        prefixBindingPath(fields, addBindingPrefix);
    }

    public static <T extends Component> T copyComponent(T component, String addBindingPrefix, String idSuffix) {
        T copy = copy(component, idSuffix);

        prefixBindingPathNested(copy, addBindingPrefix);

        return copy;
    }

    public static <T extends Component> List<T> copyComponentList(List<T> components, String idSuffix) {
        if (components == null || components.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> copiedComponentList = new ArrayList<T>(components.size());

        for (T field : components) {
            T copiedComponent = copy(field, idSuffix);
            copiedComponentList.add(copiedComponent);
        }

        return copiedComponentList;
    }

    public static <T extends Object> List<T> getComponentsOfType(List<? extends Component> items,
            Class<T> componentType) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> typeComponents = Collections.emptyList();

        for (Component component : items) {

            if (!componentType.isInstance(component)) {
                continue;
            }

            if (typeComponents.isEmpty()) {
                typeComponents = new ArrayList<T>(items.size());
            }

            typeComponents.add(componentType.cast(component));
        }

        return typeComponents;
    }

    /**
     * Gets all components of the give type that are within the items list of the container, or within a nested
     * container or field group.
     *
     * @param container container instance to pull components from
     * @param componentType type for components to pull
     * @param <T> type for component to pull
     * @return List of nested components with the given type
     */
    public static <T extends Component> List<T> getNestedContainerComponents(Container container,
            Class<T> componentType) {
        List<T> typeComponents = new ArrayList<T>();

        if (container == null) {
            return typeComponents;
        }

        for (Component item : container.getItems()) {
            if (item == null) {
                continue;
            }

            if (item instanceof Container) {
                typeComponents.addAll(getNestedContainerComponents((Container) item, componentType));
            } else if (item instanceof FieldGroup) {
                typeComponents.addAll(getNestedContainerComponents(((FieldGroup) item).getGroup(), componentType));
            } else if (componentType.isAssignableFrom(item.getClass())) {
                typeComponents.add(componentType.cast(item));
            }
        }

        return typeComponents;
    }

    public static <T extends Component> List<T> getNestedNonCollectionComponents(List<Component> items,
            Class<T> componentType) {
        List<T> typeComponents = new ArrayList<T>();

        if (items == null) {
            return typeComponents;
        }

        for (Component item : items) {
            if (item == null) {
                continue;
            }

            if (item instanceof Container && !(item instanceof CollectionGroup)) {
                typeComponents.addAll(getNestedNonCollectionComponents((Container) item, componentType));
            } else if (item instanceof FieldGroup) {
                typeComponents.addAll(getNestedNonCollectionComponents(((FieldGroup) item).getGroup(), componentType));
            } else if (componentType.isAssignableFrom(item.getClass())) {
                typeComponents.add(componentType.cast(item));
            }
        }

        return typeComponents;
    }

    public static <T extends Component> List<T> getNestedNonCollectionComponents(Container container,
            Class<T> componentType) {
        List<T> typeComponents = new ArrayList<T>();

        if (container == null) {
            return typeComponents;
        }

        for (Component item : container.getItems()) {
            if (item == null) {
                continue;
            }

            if (item instanceof Container && !(item instanceof CollectionGroup)) {
                typeComponents.addAll(getNestedNonCollectionComponents((Container) item, componentType));
            } else if (item instanceof FieldGroup) {
                typeComponents.addAll(getNestedNonCollectionComponents(((FieldGroup) item).getGroup(), componentType));
            } else if (componentType.isAssignableFrom(item.getClass())) {
                typeComponents.add(componentType.cast(item));
            }
        }

        return typeComponents;
    }

    /**
     * Get all nested children of a given component.
     *
     * @param component The component to search.
     * @return All nested children of the component.
     * @see ViewLifecycleUtils#getElementsForLifecycle(LifecycleElement)
     */
    public static List<Component> getAllNestedComponents(Component component) {
        if (component == null) {
            return Collections.emptyList();
        }

        List<Component> components = Collections.emptyList();
        @SuppressWarnings("unchecked") Queue<LifecycleElement> elementQueue = RecycleUtils.getInstance(
                LinkedList.class);
        elementQueue.offer(component);

        try {
            while (!elementQueue.isEmpty()) {
                LifecycleElement currentElement = elementQueue.poll();

                if (currentElement == null) {
                    continue;
                }

                if (currentElement instanceof Component && currentElement != component) {
                    if (components.isEmpty()) {
                        components = new ArrayList<Component>();
                    }

                    components.add((Component) currentElement);
                }

                elementQueue.addAll(ViewLifecycleUtils.getElementsForLifecycle(currentElement).values());
            }
        } finally {
            elementQueue.clear();
            RecycleUtils.recycle(elementQueue);
        }

        return components;
    }

    /**
     * Searches for the component with the given id within the given list of components
     *
     * @param components list of components to search through
     * @param componentId id for the component to find
     * @return component found in the list or null
     */
    public static Component findComponentInList(List<Component> components, String componentId) {
        for (Component component : components) {
            if (component != null && StringUtils.equals(component.getId(), componentId)) {
                return component;
            }
        }

        return null;
    }

    public static void prefixBindingPath(List<? extends Component> components, String addBindingPrefix) {
        for (Component component : components) {
            if (component instanceof DataBinding) {
                prefixBindingPath((DataBinding) component, addBindingPrefix);
            } else if ((component instanceof FieldGroup) && (((FieldGroup) component).getItems() != null)) {
                List<? extends Component> fieldGroupItems = ((FieldGroup) component).getItems();
                prefixBindingPath(fieldGroupItems, addBindingPrefix);

                //                List<Field> groupFields = ViewLifecycleUtils
                //                        .getElementsOfTypeDeep(((FieldGroup) field).getItems(), Field.class);
                //                prefixBindingPath(groupFields, addBindingPrefix);
            } else if ((component instanceof Group) && (((Group) component).getItems() != null) &&
                    !(component instanceof CollectionGroup)) {
                List<? extends Component> groupItems = ((Group) component).getItems();
                prefixBindingPath(groupItems, addBindingPrefix);
            }
        }
    }

    public static void prefixBindingPathNested(Component component, String addBindingPrefix) {
        @SuppressWarnings("unchecked") Queue<LifecycleElement> elementQueue = RecycleUtils.getInstance(
                LinkedList.class);
        elementQueue.offer(component);

        try {
            while (!elementQueue.isEmpty()) {
                LifecycleElement currentElement = elementQueue.poll();
                if (currentElement == null) {
                    continue;
                }

                if (currentElement instanceof DataBinding) {
                    if (LOG.isDebugEnabled()) {
                        LOG.info("setting nested binding prefix '" + addBindingPrefix + "' on " + currentElement);
                    }
                    prefixBindingPath((DataBinding) currentElement, addBindingPrefix);
                }

                elementQueue.addAll(ViewLifecycleUtils.getElementsForLifecycle(currentElement).values());
            }
        } finally {
            elementQueue.clear();
            RecycleUtils.recycle(elementQueue);
        }
    }

    public static void prefixBindingPath(DataBinding field, String addBindingPrefix) {
        String bindingPrefix = addBindingPrefix;
        if (StringUtils.isNotBlank(field.getBindingInfo().getBindByNamePrefix())) {
            bindingPrefix += "." + field.getBindingInfo().getBindByNamePrefix();
        }
        field.getBindingInfo().setBindByNamePrefix(bindingPrefix);
    }

    public static void updateIdsWithSuffixNested(List<? extends Component> components, String idSuffix) {
        for (Component component : components) {
            updateIdsWithSuffixNested(component, idSuffix);
        }
    }

    public static void updateIdsWithSuffixNested(Component component, String idSuffix) {
        updateIdWithSuffix(component, idSuffix);

        updateChildIdsWithSuffixNested(component, idSuffix);
    }

    public static void updateChildIdsWithSuffixNested(Component component, String idSuffix) {
        @SuppressWarnings("unchecked") Queue<LifecycleElement> elementQueue = RecycleUtils.getInstance(
                LinkedList.class);
        try {
            elementQueue.addAll(ViewLifecycleUtils.getElementsForLifecycle(component).values());

            while (!elementQueue.isEmpty()) {
                LifecycleElement currentElement = elementQueue.poll();
                if (currentElement == null) {
                    continue;
                }

                if (currentElement instanceof Component) {
                    updateIdWithSuffix((Component) currentElement, idSuffix);
                    elementQueue.addAll(((Component) currentElement).getPropertyReplacerComponents());
                }

                elementQueue.addAll(ViewLifecycleUtils.getElementsForLifecycle(currentElement).values());
            }
        } finally {
            elementQueue.clear();
            RecycleUtils.recycle(elementQueue);
        }
    }

    /**
     * Generate a hash code unique within the current view for a single lifecycle element. A unique
     * ID value will be assigned to the lifecycle element, replacing the current ID.
     *
     * <p>This method may only be called during the view lifecycle.</p>
     *
     * @param element The element to generate a hash code for.
     * @param seed A hash value to use as a seed for the new hash.
     * @return A hash code based on the provided element and seed value.
     * @see AssignIdsTask For a complete description of the algorithm. This method implements a
     * single step in the algorithm described in that task.
     */
    public static int generateId(LifecycleElement element, int seed) {
        if (element == null) {
            return seed;
        }

        final int prime = 6971;
        int hash = prime * seed + element.getClass().getName().hashCode();

        String id = element.getId();
        hash *= prime;
        if (id != null) {
            hash += id.hashCode();
        }

        do {
            hash *= 4507;
            id = Long.toString(((long) hash) - ((long) Integer.MIN_VALUE), 36);
        } while (!ViewLifecycle.getView().getViewIndex().observeAssignedId(id));

        element.setId(UifConstants.COMPONENT_ID_PREFIX + id);

        return hash;
    }

    /**
     * Replace all IDs from a component and its children with new generated ID values.
     *
     * <p>If there are features that depend on a static id of this
     * component, this call may cause errors.</p>
     *
     * @param components A list of component to clear all IDs from.
     * @see AssignIdsTask For a complete description of the algorithm.
     */
    public static void clearAndAssignIds(List<? extends Component> components) {
        if (components == null || components.isEmpty()) {
            return;
        }

        int hash = 1;
        @SuppressWarnings("unchecked") Queue<LifecycleElement> toClear = RecycleUtils.getInstance(LinkedList.class);
        toClear.addAll(components);
        try {
            while (!toClear.isEmpty()) {
                LifecycleElement element = toClear.poll();

                hash = generateId(element, hash);

                for (LifecycleElement nested : ViewLifecycleUtils.getElementsForLifecycle(element).values()) {
                    if (nested != null) {
                        toClear.add(nested);
                    }
                }

                if (element instanceof Component) {
                    List<Component> propertyReplacerComponents = ((Component) element).getPropertyReplacerComponents();
                    if (propertyReplacerComponents == null) {
                        continue;
                    }

                    for (Component nested : propertyReplacerComponents) {
                        if (nested != null) {
                            toClear.add(nested);
                        }
                    }
                }
            }
        } finally {
            toClear.clear();
            RecycleUtils.recycle(toClear);
        }
    }

    /**
     * add a suffix to the id
     *
     * @param component the component instance whose id will be changed
     * @param idSuffix the suffix to be appended
     */
    public static void updateIdWithSuffix(Component component, String idSuffix) {
        if (component != null && !StringUtils.isEmpty(idSuffix)) {
            component.setId(component.getId() + idSuffix);
        }

        if (component instanceof Container) {
            LayoutManager manager = ((Container) component).getLayoutManager();
            if (manager != null) {
                manager.setId(manager.getId() + idSuffix);
            }
        }
    }

    /**
     * Traverse a component tree, setting a property on all components for which the property is writable.
     *
     * @param <T> component type
     * @param <T> component type
     * @param components The components to traverse.
     * @param propertyPath The property path to set.
     * @param propertyValue The property value to set.
     * @see ObjectPropertyUtils#isWritableProperty(Object, String)
     * @see ObjectPropertyUtils#setPropertyValue(Object, String, Object)
     */
    public static <T extends Component> void setComponentsPropertyDeep(List<T> components, String propertyPath,
            Object propertyValue) {
        if (components == null || components.isEmpty()) {
            return;
        }

        Set<Class<?>> skipTypes = null;
        @SuppressWarnings("unchecked") Queue<LifecycleElement> elementQueue = RecycleUtils.getInstance(
                LinkedList.class);
        elementQueue.addAll(components);

        try {
            while (!elementQueue.isEmpty()) {
                LifecycleElement currentElement = elementQueue.poll();
                if (currentElement == null) {
                    continue;
                }

                elementQueue.addAll(ViewLifecycleUtils.getElementsForLifecycle(currentElement).values());

                Class<?> componentClass = currentElement.getClass();
                if (skipTypes != null && skipTypes.contains(componentClass)) {
                    continue;
                }

                if (!ObjectPropertyUtils.isWritableProperty(currentElement, propertyPath)) {
                    if (skipTypes == null) {
                        skipTypes = new HashSet<Class<?>>();
                    }
                    skipTypes.add(componentClass);
                    continue;
                }

                ObjectPropertyUtils.setPropertyValue(currentElement, propertyPath, propertyValue, true);
            }
        } finally {
            elementQueue.clear();
            RecycleUtils.recycle(elementQueue);
        }
    }

    /**
     * Traverse a component tree, setting a property on all components for which the property is writable.
     *
     * @param component The component to traverse.
     * @param propertyPath The property path to set.
     * @param propertyValue The property value to set.
     * @see ObjectPropertyUtils#isWritableProperty(Object, String)
     * @see ObjectPropertyUtils#setPropertyValue(Object, String, Object)
     */
    public static void setComponentPropertyDeep(Component component, String propertyPath, Object propertyValue) {
        setComponentsPropertyDeep(Collections.singletonList(component), propertyPath, propertyValue);
    }

    /**
     * Sets a property on the given component and removes any expressions for that property so the value is not
     * overridden
     *
     * @param component component instance to set property on
     * @param propertyName name of property to set
     * @param propertyValue value to set property to
     */
    public static void setComponentPropertyFinal(Component component, String propertyName, Object propertyValue) {
        if (component == null) {
            return;
        }

        ObjectPropertyUtils.setPropertyValue(component, propertyName, propertyValue);

        if ((component.getPropertyExpressions() != null) && component.getPropertyExpressions().containsKey(
                propertyName)) {
            component.getPropertyExpressions().remove(propertyName);
        }
    }

    /**
     * Indicates if the given component has configuration that it allows it to be refreshed.
     *
     * @param component instance to check
     * @return true if component can be refreshed, false if not
     */
    public static boolean canBeRefreshed(Component component) {
        boolean hasRefreshCondition = StringUtils.isNotBlank(component.getProgressiveRender()) ||
                StringUtils.isNotBlank(component.getConditionalRefresh()) || (component.getRefreshTimer() > 0) ||
                (component.getRefreshWhenChangedPropertyNames() != null && !component
                        .getRefreshWhenChangedPropertyNames().isEmpty());

        return hasRefreshCondition || component.isRefreshedByAction() || component.isDisclosedByAction() ||
                component.isRetrieveViaAjax();
    }

    /**
     * Performs sorting logic of the given list of <code>Ordered</code>
     * instances by its order property
     *
     * <p>
     * Items list is sorted based on its order property. Lower order values are
     * placed higher in the list. If a item does not have a value assigned for
     * the order (or is equal to the default order of 0), it will be assigned
     * the a value based on the given order sequence integer. If two or more
     * items share the same order value, all but the last item found in the list
     * will be removed.
     * </p>
     * 
     * @param <T> ordered type
     * @param <T> ordered type
     * @param items
     * @param defaultOrderSequence
     * @return List<Ordered> sorted items
     * @see org.kuali.rice.krad.uif.component.Component#getOrder()
     * @see org.springframework.core.Ordered
     */
    public static <T extends Ordered> List<T> sort(List<T> items, int defaultOrderSequence) {
        if (items == null) {
            return null;
        }

        List<T> orderedItems = new ArrayList<T>(items.size());

        // do replacement for items with the same order property value
        Set<Integer> foundOrders = new HashSet<Integer>();

        // reverse the list, so items later in the list win
        for (int i = items.size() - 1; i >= 0; i--) {
            T component = items.get(i);
            int order = component.getOrder();

            // if order not set just add to list
            if (order == 0) {
                orderedItems.add(component);
            }
            // check if the order value has been used already
            else if (!foundOrders.contains(Integer.valueOf(order))) {
                orderedItems.add(component);
                foundOrders.add(Integer.valueOf(order));
            }
        }

        // now reverse the list back so we can assign defaults for items without
        // an order value
        for (int i = 0; i < items.size(); i++) {
            Ordered component = items.get(i);
            int order = component.getOrder();

            // if order property not set assign default
            if (order == 0) {
                defaultOrderSequence++;
                while (foundOrders.contains(Integer.valueOf(defaultOrderSequence))) {
                    defaultOrderSequence++;
                }
                component.setOrder(defaultOrderSequence);
            }
        }

        // now sort the list by its order property
        Collections.sort(orderedItems, new OrderComparator());

        return orderedItems;
    }

    /**
     * Gets all the input fields contained in this container, but also in
     * every sub-container that is a child of this container.  When called from the top level
     * View this will be every InputField across all pages.
     *
     * @param container container to scan for input fields
     * @return every InputField that is a child at any level of this container
     */
    public static List<InputField> getAllInputFieldsWithinContainer(Container container) {
        List<InputField> inputFields = new ArrayList<InputField>();

        for (LifecycleElement c : ViewLifecycleUtils.getElementsForLifecycle(container).values()) {
            if (c instanceof InputField) {
                inputFields.add((InputField) c);
            } else if (c instanceof Container) {
                inputFields.addAll(getAllInputFieldsWithinContainer((Container) c));
            } else if (c instanceof FieldGroup) {
                Container cb = ((FieldGroup) c).getGroup();

                inputFields.addAll(getAllInputFieldsWithinContainer(cb));
            }
        }

        return inputFields;
    }

    /**
     * Determines whether the given component contains an expression for the given property name
     *
     * @param component component instance to check for expressions
     * @param propertyName name of the property to determine if there is an expression for
     * @param collectionMatch if set to true will find an expressions for properties that start with the given
     * property name (for matching expressions on collections like prop[index] or prop['key'])
     * @return true if the component has an expression for the property name, false if not
     */
    public static boolean containsPropertyExpression(Component component, String propertyName,
            boolean collectionMatch) {
        boolean hasExpression = false;

        Map<String, String> propertyExpressions = component.getPropertyExpressions();

        if (collectionMatch) {
            for (String expressionPropertyName : propertyExpressions.keySet()) {
                if (expressionPropertyName.startsWith(propertyName)) {
                    hasExpression = true;
                }
            }
        } else if (propertyExpressions.containsKey(propertyName)) {
            hasExpression = true;
        }

        return hasExpression;
    }

    /**
     * Adjust nestingLevel properties for collections which use RichTable with forceLocalJsonData on and for all of its
     * potentially additional nested subcollections
     *
     * @param container container to traverse and update nested levels in
     * @param currentLevel the current nesting level, the initial call to this method should be 0
     */
    public static void adjustNestedLevelsForTableCollections(Container container, int currentLevel) {
        if (container != null
                && container instanceof CollectionGroup
                && container.getLayoutManager() != null
                && container.getLayoutManager() instanceof TableLayoutManager
                && ((TableLayoutManager) container.getLayoutManager()).getRichTable() != null
                && ((TableLayoutManager) container.getLayoutManager()).getRichTable().isRender()
                && ((TableLayoutManager) container.getLayoutManager()).getRichTable().isForceLocalJsonData()) {
            ((TableLayoutManager) container.getLayoutManager()).getRichTable().setNestedLevel(currentLevel);
            currentLevel++;
        }

        if (container != null) {
            List<Container> subContainers = ViewLifecycleUtils.getNestedElementsOfTypeShallow(container,
                    Container.class);
            for (Container subContainer : subContainers) {
                adjustNestedLevelsForTableCollections(subContainer, currentLevel);
            }

            List<FieldGroup> subFieldGroups = ViewLifecycleUtils.getNestedElementsOfTypeShallow(container,
                    FieldGroup.class);
            for (FieldGroup fieldGroup : subFieldGroups) {
                if (fieldGroup != null) {
                    adjustNestedLevelsForTableCollections(fieldGroup.getGroup(), currentLevel);
                }
            }
        }
    }

}
