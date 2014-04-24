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

import org.kuali.rice.core.framework.util.ReflectionUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Utility methods related to handling context for components.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ContextUtils {

    private ContextUtils() {}

    /**
     * places a key, value pair in each context map of a list of components
     *
     * @param elements the list of elements
     * @param contextName a value to be used as a key to retrieve the object
     * @param contextValue the value to be placed in the context
     */
    public static void pushObjectToContextDeep(Collection<? extends LifecycleElement> elements, String contextName,
            Object contextValue) {
        if (elements == null || elements.isEmpty()) {
            return;
        }

        Queue<LifecycleElement> elementQueue = new LinkedList<LifecycleElement>();

        try {
            elementQueue.addAll(elements);
            while (!elementQueue.isEmpty()) {
                LifecycleElement currentElement = elementQueue.poll();

                if (currentElement == null) {
                    continue;
                }

                if (currentElement instanceof Component) {
                    ((Component) currentElement).pushObjectToContext(contextName, contextValue);
                }

                elementQueue.addAll(ViewLifecycleUtils.getElementsForLifecycle(currentElement).values());
            }
        } finally {
            elementQueue.clear();
            RecycleUtils.recycle(elementQueue);
        }
    }

    /**
     * Pushes object to a component's context so that it is available from
     * {@link org.kuali.rice.krad.uif.component.Component#getContext()}
     *
     * <p>The component's nested components that are available via {@code Component#getComponentsForLifecycle}
     * are also updated recursively</p>
     *
     * @param component the component whose context is to be updated
     * @param contextName a value to be used as a key to retrieve the object
     * @param contextValue the value to be placed in the context
     */
    public static void pushObjectToContextDeep(Component component, String contextName, Object contextValue) {
        if (component == null) {
            return;
        }

        pushObjectToContextDeep(Collections.singletonList(component), contextName, contextValue);
    }

    /**
     * Pushes object to a component's context so that it is available from
     * {@link org.kuali.rice.krad.uif.component.Component#getContext()}
     *
     * <p>The component's nested components that are available via {@code Component#getComponentsForLifecycle}
     * are also updated recursively</p>
     *
     * @param component the component whose context is to be updated
     * @param sourceContext The source context map.
     */
    public static void pushAllToContextDeep(Component component, Map<String, Object> sourceContext) {
        if (component == null) {
            return;
        }

        pushAllToContextDeep(Collections.singletonList(component), sourceContext);
    }

    /**
     * Places a all entries from a map into each context map of a list of components.
     *
     * @param components The list components.
     * @param sourceContext The source context map.
     */
    public static void pushAllToContextDeep(List<? extends Component> components, Map<String, Object> sourceContext) {
        if (components == null || components.isEmpty()) {
            return;
        }

        @SuppressWarnings("unchecked") Queue<LifecycleElement> elementQueue = RecycleUtils.getInstance(
                LinkedList.class);
        try {
            elementQueue.addAll(components);
            while (!elementQueue.isEmpty()) {
                LifecycleElement currentElement = elementQueue.poll();

                if (currentElement == null) {
                    continue;
                }

                if (currentElement instanceof Component) {
                    ((Component) currentElement).pushAllToContext(sourceContext);
                }

                elementQueue.addAll(ViewLifecycleUtils.getElementsForLifecycle(currentElement).values());
            }
        } finally {
            elementQueue.clear();
            RecycleUtils.recycle(elementQueue);
        }
    }

    /**
     * Update the contexts of the given components.
     *
     * @param components the components whose components to update
     * @param collectionGroup collection group the components are associated with
     * @param collectionLine an instance of the data object for the line
     * @param lineIndex the line index
     * @param lineSuffix id suffix for components in the line to make them unique
     */
    public static void updateContextsForLine(List<? extends Component> components, CollectionGroup collectionGroup,
            Object collectionLine, int lineIndex, String lineSuffix) {
        for (Component component : components) {
            updateContextForLine(component, collectionGroup, collectionLine, lineIndex, lineSuffix);
        }
    }

    /**
     * Update the context map for the given component with the collection context.
     *
     * <p>The values of {@code UifConstants.ContextVariableNames.LINE} and {@code UifConstants.ContextVariableNames.INDEX}
     * are set to {@code collectionLine} and {@code lineIndex} respectively.</p>
     *
     * @param component the component whose context is to be updated
     * @param collectionGroup collection group the component is associated with
     * @param collectionLine an instance of the data object for the line
     * @param lineIndex the line index
     * @param lineSuffix id suffix for components in the line to make them unique
     */
    public static void updateContextForLine(Component component, CollectionGroup collectionGroup, Object collectionLine,
            int lineIndex, String lineSuffix) {
        Map<String, Object> toUpdate = new HashMap<String, Object>(5);

        toUpdate.put(UifConstants.ContextVariableNames.COLLECTION_GROUP, collectionGroup);
        toUpdate.put(UifConstants.ContextVariableNames.LINE, collectionLine);
        toUpdate.put(UifConstants.ContextVariableNames.INDEX, Integer.valueOf(lineIndex));
        toUpdate.put(UifConstants.ContextVariableNames.LINE_SUFFIX, lineSuffix);

        boolean isAddLine = (lineIndex == -1);
        toUpdate.put(UifConstants.ContextVariableNames.IS_ADD_LINE, isAddLine);

        pushAllToContextDeep(component, toUpdate);
    }

    /**
     * Sets the context of the given lifecycle element to null, then using reflection recursively finds any
     * lifecycle element children and sets their context to null.
     *
     * @param lifecycleElement lifecycle element instance to clean
     */
    public static void cleanContextDeep(LifecycleElement lifecycleElement) {
        if (lifecycleElement == null) {
            return;
        }

        lifecycleElement.setContext(null);

        // find any children that are lifecycle elements and clean them as well
        Class<?> elementClass = lifecycleElement.getClass();

        List<java.lang.reflect.Field> fields = ReflectionUtils.getAllFields(elementClass);
        for (java.lang.reflect.Field field : fields) {
            // Check for lists that can contain lifecycle elements
            if (Collection.class.isAssignableFrom(field.getType())) {
                ReflectionUtils.makeAccessible(field);
                Collection<Object> elements = (Collection<Object>) ReflectionUtils.getField(field, lifecycleElement);
                if (elements != null) {
                    for (Object element : elements) {
                        if (element != null && LifecycleElement.class.isAssignableFrom(element.getClass())) {
                            cleanContextDeep((LifecycleElement) element);
                        }
                    }
                }
            // Check for Maps that can contain lifecycle elements
            } else if (Map.class.isAssignableFrom(field.getType())) {
                ReflectionUtils.makeAccessible(field);
                Map<Object, Object> elements = (Map<Object, Object>) ReflectionUtils.getField(field, lifecycleElement);
                if (elements != null) {
                    for (Object element : elements.entrySet()) {
                        if (element != null && LifecycleElement.class.isAssignableFrom(element.getClass())) {
                            cleanContextDeep((LifecycleElement) element);
                        }
                    }
                }
            // Check if field is a lifecycle element itself
            } else if (LifecycleElement.class.isAssignableFrom(field.getType())) {
                ReflectionUtils.makeAccessible(field);
                LifecycleElement nestedElement = (LifecycleElement) ReflectionUtils.getField(field, lifecycleElement);

                cleanContextDeep(nestedElement);
            }
        }
    }
}
