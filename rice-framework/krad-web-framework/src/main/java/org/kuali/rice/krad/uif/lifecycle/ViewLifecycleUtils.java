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
package org.kuali.rice.krad.uif.lifecycle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.RecycleUtils;
import org.kuali.rice.krad.uif.view.View;

/**
 * Utilities for working with {@link LifecycleElement} instances.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class ViewLifecycleUtils {

    private static final Logger LOG = Logger.getLogger(ViewLifecycleUtils.class);

    private static final String COMPONENT_CONTEXT_PREFIX = '#' + UifConstants.ContextVariableNames.COMPONENT + '.';
    private static final String PARENT_CONTEXT_PREFIX = '#' + UifConstants.ContextVariableNames.PARENT + '.';
    
    /**
     * Gets property names of all bean properties on the lifecycle element restricted for the
     * indicated view phase.
     *
     * @param element The lifecycle element.
     * @param viewPhase The view phase to retrieve restrictions for.
     * @return set of property names
     */
    public static Set<String> getLifecycleRestrictedProperties(LifecycleElement element, String viewPhase) {
        Set<String> restrictedPropertyNames = getMetadata(element.getClass()).lifecycleRestrictedProperties.get(
                viewPhase);
        if (restrictedPropertyNames == null) {
            return Collections.emptySet();
        } else {
            return restrictedPropertyNames;
        }
    }

    /**
     * Gets the next lifecycle phase to be executed on the provided element.
     *
     * <dl>
     * <dt>{@link org.kuali.rice.krad.uif.UifConstants.ViewStatus#CREATED CREATED}</dt>
     * <dt>{@link org.kuali.rice.krad.uif.UifConstants.ViewStatus#CACHED CACHED}</dt>
     * <dd>{@link org.kuali.rice.krad.uif.UifConstants.ViewPhases#INITIALIZE INITIALIZE}</dd>
     * 
     * <dt>{@link org.kuali.rice.krad.uif.UifConstants.ViewStatus#INITIALIZED INITIALIZED}</dt>
     * <dd>{@link org.kuali.rice.krad.uif.UifConstants.ViewPhases#APPLY_MODEL APPLY_MODEL}</dd>
     * 
     * <dt>{@link org.kuali.rice.krad.uif.UifConstants.ViewStatus#MODEL_APPLIED MODEL_APPLIED}</dt>
     * <dd>{@link org.kuali.rice.krad.uif.UifConstants.ViewPhases#FINALIZE FINALIZE}</dd>
     * 
     * <dt>{@link org.kuali.rice.krad.uif.UifConstants.ViewStatus#FINAL FINAL}</dt>
     * <dd>{@link org.kuali.rice.krad.uif.UifConstants.ViewPhases#RENDER RENDER}</dd>
     * </dl>
     *
     * <p>
     * If the view status is null, invalid, or {@link org.kuali.rice.krad.uif.UifConstants.ViewStatus#RENDERED RENDERED},
     * then {@link org.kuali.rice.krad.uif.UifConstants.ViewPhases#INITIALIZE} will be returned and a warning logged.
     * </p>
     *
     * @param element The lifecycle element.
     * @return The next phase in the element's lifecycle based on view status
     * @see LifecycleElement#getViewStatus()
     * @see org.kuali.rice.krad.uif.UifConstants.ViewPhases
     */
    public static String getNextLifecyclePhase(LifecycleElement element) {
        if (element == null) {
            return UifConstants.ViewPhases.INITIALIZE;
        }

        String viewStatus = element.getViewStatus();

        if (viewStatus == null || UifConstants.ViewStatus.CACHED.equals(viewStatus) || UifConstants.ViewStatus.CREATED
                .equals(viewStatus)) {
            return UifConstants.ViewPhases.INITIALIZE;

        } else if (UifConstants.ViewStatus.INITIALIZED.equals(viewStatus)) {
            return UifConstants.ViewPhases.APPLY_MODEL;

        } else if (UifConstants.ViewStatus.MODEL_APPLIED.equals(viewStatus)) {
            return UifConstants.ViewPhases.FINALIZE;

        } else if (UifConstants.ViewStatus.FINAL.equals(viewStatus) || UifConstants.ViewStatus.RENDERED.equals(
                viewStatus)) {
            return UifConstants.ViewPhases.RENDER;

        } else {
            ViewLifecycle.reportIllegalState("Invalid view status " + viewStatus);
            return UifConstants.ViewPhases.INITIALIZE;
        }
    }

    /**
     * Gets sub-elements for lifecycle processing.
     *
     * @param element The element to scan.
     * @return map of lifecycle elements
     */
    public static Map<String, LifecycleElement> getElementsForLifecycle(LifecycleElement element) {
        return getElementsForLifecycle(element, getNextLifecyclePhase(element));
    }

    /**
     * Helper method for {@link #getElementsForLifecycle(LifecycleElement, String)}.
     *
     * <p>
     * Unwraps the lifecycle element if not null, and dynamically creates the lifecycle map when the
     * first non-null element is added.
     * </p>
     *
     * @param map The lifecycle map.
     * @param propertyName The property path to the nested element.
     * @param nestedElement The nested element to add.
     * @return map, or a newly created map if the provided map was empty and the nested element was
     * not null.
     */
    private static Map<String, LifecycleElement> addElementToLifecycleMap(Map<String, LifecycleElement> map,
            String propertyName, LifecycleElement nestedElement) {
        if (nestedElement == null) {
            return map;
        }

        Map<String, LifecycleElement> returnMap = map;
        if (returnMap == Collections.EMPTY_MAP) {
            returnMap = RecycleUtils.getInstance(LinkedHashMap.class);
        }

        returnMap.put(propertyName, CopyUtils.unwrap((LifecycleElement) nestedElement));
        return returnMap;
    }

    /**
     * Gets subcomponents for lifecycle processing.
     *
     * @param element The component to scan.
     * @param viewPhase The view phase to return subcomponents for.
     * @return lifecycle components
     */
    public static Map<String, LifecycleElement> getElementsForLifecycle(LifecycleElement element, String viewPhase) {
        if (element == null) {
            return Collections.emptyMap();
        }

        Set<String> nestedElementProperties = ObjectPropertyUtils.getReadablePropertyNamesByType(element,
                LifecycleElement.class);
        Set<String> nestedElementCollectionProperties = ObjectPropertyUtils.getReadablePropertyNamesByCollectionType(
                element, LifecycleElement.class);
        if (nestedElementProperties.isEmpty() && nestedElementCollectionProperties.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<String> restrictedPropertyNames = getLifecycleRestrictedProperties(element, viewPhase);

        Map<String, LifecycleElement> elements = Collections.emptyMap();

        for (String propertyName : nestedElementProperties) {
            if (restrictedPropertyNames.contains(propertyName)) {
                continue;
            }

            Object propertyValue = ObjectPropertyUtils.getPropertyValue(element, propertyName);
            elements = addElementToLifecycleMap(elements, propertyName, (LifecycleElement) propertyValue);
        }

        for (String propertyName : nestedElementCollectionProperties) {
            if (restrictedPropertyNames.contains(propertyName)) {
                continue;
            }

            Object nestedElementCollection = ObjectPropertyUtils.getPropertyValue(element, propertyName);
            if (element.getClass().isArray()) {
                for (int i = 0; i < Array.getLength(nestedElementCollection); i++) {
                    elements = addElementToLifecycleMap(elements, propertyName + "[" + i + "]",
                            (LifecycleElement) Array.get(nestedElementCollection, i));
                }
            } else if (nestedElementCollection instanceof List) {
                for (int i = 0; i < ((List<?>) nestedElementCollection).size(); i++) {
                    elements = addElementToLifecycleMap(elements, propertyName + "[" + i + "]",
                            (LifecycleElement) ((List<?>) nestedElementCollection).get(i));
                }
            } else if (nestedElementCollection instanceof Map) {
                for (Entry<?, ?> entry : ((Map<?, ?>) nestedElementCollection).entrySet()) {
                    elements = addElementToLifecycleMap(elements, propertyName + "[" + entry.getKey() + "]",
                            (LifecycleElement) entry.getValue());
                }
            }
        }

        return elements == Collections.EMPTY_MAP ? elements : Collections.unmodifiableMap(elements);
    }
    
    /**
     * Recycle a map returned by {@link #getElementsForLifecycle(LifecycleElement, String)}.
     * 
     * @param elementMap map to recycle
     */
    public static void recycleElementMap(Map<?, ?> elementMap) {
        if (elementMap instanceof LinkedHashMap) {
            elementMap.clear();
            RecycleUtils.recycle(elementMap);
        }
    }

    /**
     * Return the lifecycle elements of the specified type from the given list
     *
     * <p>
     * Elements that match, implement or are extended from the specified {@code elementType} are
     * returned in the result. If an element is a parent to other elements then these child elements
     * are searched for matching types as well.
     * </p>
     *
     * @param items list of elements from which to search
     * @param elementType the class or interface of the element type to return
     * @param <T> the type of the elements that are returned
     * @return List of matching elements
     */
    public static <T extends LifecycleElement> List<T> getElementsOfTypeDeep(
            Collection<? extends LifecycleElement> items, Class<T> elementType) {
        if (items == null) {
            return Collections.emptyList();
        }

        List<T> elements = Collections.emptyList();

        @SuppressWarnings("unchecked") Queue<LifecycleElement> elementQueue = RecycleUtils.getInstance(
                LinkedList.class);
        elementQueue.addAll(items);

        try {
            while (!elementQueue.isEmpty()) {
                LifecycleElement currentElement = elementQueue.poll();
                if (currentElement == null) {
                    continue;
                }

                if (elementType.isInstance(currentElement)) {
                    if (elements.isEmpty()) {
                        elements = new ArrayList<T>();
                    }

                    elements.add(elementType.cast(currentElement));
                }

                elementQueue.addAll(getElementsForLifecycle(currentElement).values());
            }
        } finally {
            elementQueue.clear();
            RecycleUtils.recycle(elementQueue);
        }
        return elements;
    }

    /**
     * Returns the lifecycle elements of the specified type from the given list.
     *
     * <p>
     * Elements that match, implement or are extended from the specified {@code elementType} are
     * returned in the result. If an element is a parent to other elements then these child
     * components are searched for matching component types as well.
     * </p>
     *
     * @param element The elements to search
     * @param elementType the class or interface of the elements type to return
     * @param <T> the type of the elements that are returned
     * @return List of matching elements
     */
    public static <T extends LifecycleElement> List<T> getElementsOfTypeDeep(LifecycleElement element,
            Class<T> elementType) {
        return getElementsOfTypeDeep(Collections.singletonList(element), elementType);
    }

    /**
     * Returns elements of the given type that are direct children of the given element including
     * itself, or a child of a non-component child of the given element.
     *
     * <p>
     * Deep search is only performed on non-component nested elements.
     * </p>
     *
     * @param element instance to get children for
     * @param elementType type for component to return
     * @param <T> type of component that will be returned
     * @return list of child components with the given type
     */
    public static <T extends LifecycleElement> List<T> getElementsOfTypeShallow(LifecycleElement element,
            Class<T> elementType) {
        if (element == null) {
            return Collections.emptyList();
        }

        List<T> typeElements = getNestedElementsOfTypeShallow(element, elementType);

        if (elementType.isInstance(element)) {
            if (typeElements.isEmpty()) {
                typeElements = Collections.singletonList(elementType.cast(element));
            } else {
                typeElements.add(0, elementType.cast(element));
            }
        }

        return typeElements;
    }

    /**
     * Get nested elements of the type specified one layer deep; this defers from
     * getElementsOfTypeShallow because it does NOT include itself as a match if it also matches the
     * type being requested.
     *
     * @param element instance to get children for
     * @param elementType type for element to return
     * @param <T> type of element that will be returned
     * @return list of child elements with the given type
     */
    public static <T extends LifecycleElement> List<T> getNestedElementsOfTypeShallow(LifecycleElement element,
            Class<T> elementType) {
        if (element == null) {
            return Collections.emptyList();
        }

        List<T> elements = Collections.emptyList();

        @SuppressWarnings("unchecked") Queue<LifecycleElement> elementQueue = RecycleUtils.getInstance(
                LinkedList.class);
        try {
            elementQueue.add(element);

            while (!elementQueue.isEmpty()) {
                LifecycleElement currentElement = elementQueue.poll();
                if (currentElement == null) {
                    continue;
                }

                if (elementType.isInstance(currentElement) && currentElement != element) {
                    if (elements.isEmpty()) {
                        elements = new ArrayList<T>();
                    }

                    elements.add(elementType.cast(currentElement));
                }

                for (LifecycleElement nestedElement : getElementsForLifecycle(currentElement).values()) {
                    if (!(nestedElement instanceof Component)) {
                        elementQueue.offer(nestedElement);
                    }
                }
            }
        } finally {
            elementQueue.clear();
            RecycleUtils.recycle(elementQueue);
        }
        return elements;
    }

    /**
     * Private constructor - utility class only.
     */
    private ViewLifecycleUtils() {}

    /**
     * Internal metadata cache.
     */
    private static final Map<Class<?>, ElementMetadata> METADATA_CACHE = Collections.synchronizedMap(
            new WeakHashMap<Class<?>, ElementMetadata>(2048));

    /**
     * Gets the element metadata for a lifecycle element implementation class.
     *
     * @param elementClass The {@link LifecycleElement} class.
     * @return {@link ElementMetadata} instance for elementClass
     */
    private static ElementMetadata getMetadata(Class<?> elementClass) {
        ElementMetadata metadata = METADATA_CACHE.get(elementClass);

        if (metadata == null) {
            metadata = new ElementMetadata(elementClass);
            METADATA_CACHE.put(elementClass, metadata);
        }

        return metadata;
    }

    /**
     * Stores metadata related to a lifecycle element class, for reducing overhead.
     */
    private static class ElementMetadata {

        // set of all restricted properties on the element class, keyed by view phase
        private final Map<String, Set<String>> lifecycleRestrictedProperties;

        /**
         * Creates a new metadata wrapper for a bean class.
         *
         * @param elementClass The element class.
         */
        private ElementMetadata(Class<?> elementClass) {
            Set<String> restrictedPropertyNames = ObjectPropertyUtils.getReadablePropertyNamesByAnnotationType(
                    elementClass, ViewLifecycleRestriction.class);

            if (restrictedPropertyNames.isEmpty()) {
                lifecycleRestrictedProperties = Collections.emptyMap();

                return;
            }

            Map<String, Set<String>> mutableLifecycleRestrictedProperties = new HashMap<String, Set<String>>();

            mutableLifecycleRestrictedProperties.put(UifConstants.ViewPhases.FINALIZE, new HashSet<String>(
                    restrictedPropertyNames));
            mutableLifecycleRestrictedProperties.put(UifConstants.ViewPhases.APPLY_MODEL, new HashSet<String>(
                    restrictedPropertyNames));
            mutableLifecycleRestrictedProperties.put(UifConstants.ViewPhases.INITIALIZE, new HashSet<String>(
                    restrictedPropertyNames));
            mutableLifecycleRestrictedProperties.put(UifConstants.ViewPhases.PRE_PROCESS, new HashSet<String>(
                    restrictedPropertyNames));

            // remove properties that should be included for certain phases
            for (String restrictedPropertyName : restrictedPropertyNames) {
                ViewLifecycleRestriction restriction = ObjectPropertyUtils.getReadMethod(elementClass,
                        restrictedPropertyName).getAnnotation(ViewLifecycleRestriction.class);

                if (restriction.value().length > 0) {
                    removedRestrictionsForPrecedingPhases(mutableLifecycleRestrictedProperties, restrictedPropertyName,
                            restriction.value()[0]);
                } else if (restriction.exclude().length > 0) {
                    // include all by default if a exclude is defined
                    removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, restrictedPropertyName,
                            UifConstants.ViewPhases.PRE_PROCESS);
                    removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, restrictedPropertyName,
                            UifConstants.ViewPhases.INITIALIZE);
                    removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, restrictedPropertyName,
                            UifConstants.ViewPhases.APPLY_MODEL);
                    removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, restrictedPropertyName,
                            UifConstants.ViewPhases.FINALIZE);
                }

                // add back explicit exclusions
                if (restriction.exclude().length > 0) {
                    for (String excludePhase : restriction.exclude()) {
                        Set<String> restrictedProperties = mutableLifecycleRestrictedProperties.get(excludePhase);

                        restrictedProperties.add(restrictedPropertyName);
                    }
                }
            }

            lifecycleRestrictedProperties = Collections.unmodifiableMap(mutableLifecycleRestrictedProperties);
        }

        private void removedRestrictionsForPrecedingPhases(
                Map<String, Set<String>> mutableLifecycleRestrictedProperties, String propertyName, String phase) {
            if (phase.equals(UifConstants.ViewPhases.FINALIZE)) {
                removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, propertyName,
                        UifConstants.ViewPhases.FINALIZE);
                removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, propertyName,
                        UifConstants.ViewPhases.APPLY_MODEL);
                removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, propertyName,
                        UifConstants.ViewPhases.INITIALIZE);
                removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, propertyName,
                        UifConstants.ViewPhases.PRE_PROCESS);
            } else if (phase.equals(UifConstants.ViewPhases.APPLY_MODEL)) {
                removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, propertyName,
                        UifConstants.ViewPhases.APPLY_MODEL);
                removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, propertyName,
                        UifConstants.ViewPhases.INITIALIZE);
                removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, propertyName,
                        UifConstants.ViewPhases.PRE_PROCESS);
            } else if (phase.equals(UifConstants.ViewPhases.INITIALIZE)) {
                removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, propertyName,
                        UifConstants.ViewPhases.INITIALIZE);
                removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, propertyName,
                        UifConstants.ViewPhases.PRE_PROCESS);
            } else if (phase.equals(UifConstants.ViewPhases.PRE_PROCESS)) {
                removedRestrictionsForPhase(mutableLifecycleRestrictedProperties, propertyName,
                        UifConstants.ViewPhases.PRE_PROCESS);
            }
        }

        private void removedRestrictionsForPhase(Map<String, Set<String>> mutableLifecycleRestrictedProperties,
                String propertyName, String phase) {
            Set<String> restrictedProperties = mutableLifecycleRestrictedProperties.get(phase);

            restrictedProperties.remove(propertyName);
        }
    }

    /**
     * Determines if a component should be excluded from the current lifecycle.
     * 
     * @param component The component.
     */
    public static boolean isExcluded(Component component) {
        String excludeUnless = component.getExcludeUnless();        
        if (StringUtils.isNotBlank(excludeUnless) &&
                !resolvePropertyPath(excludeUnless, component)) {
            return true;
        }
        
        return resolvePropertyPath(component.getExcludeIf(), component);
    }

    /**
     * Helper method for use with {@link #isExcluded(Component)}.
     * 
     * <p>
     * Resolves a property path based on either the model, or on the pre-model context when the path
     * expression starts with '#'. Note that this method is intended for resolution at the
     * initialize phase, so the full context is not available. However, in addition to the values
     * evident in {@link View#getPreModelContext()}, #component and #parent will resolve to the
     * component, and its lifecycle parent, respectively.
     * </p>
     * 
     * @param path property path
     * @param component component to evaluate the expression relative to
     * @return true if the path resolves to the boolean value true, otherwise false
     */
    private static boolean resolvePropertyPath(String path, Component component) {
        if (StringUtils.isBlank(path)) {
            return false;
        }

        Object root;
        if (path.startsWith(COMPONENT_CONTEXT_PREFIX)) {
            root = component;
            path = path.substring(COMPONENT_CONTEXT_PREFIX.length());
        } else if (path.startsWith(PARENT_CONTEXT_PREFIX)) {
            root = ViewLifecycle.getPhase().getParent();
            path = path.substring(PARENT_CONTEXT_PREFIX.length());
        } else if (path.charAt(0) == '#') {
            Map<String, Object> context = ViewLifecycle.getView().getPreModelContext();

            int iod = path.indexOf('.');
            if (iod == -1) {
                return Boolean.TRUE.equals(context.get(path.substring(1)));
            }

            String contextVariable = path.substring(1, iod);
            root = context.get(contextVariable);
            path = path.substring(iod + 1);
        } else {
            root = ViewLifecycle.getModel();
        }

        return Boolean.TRUE.equals(ObjectPropertyUtils.getPropertyValue(root, path));
    }
    
}