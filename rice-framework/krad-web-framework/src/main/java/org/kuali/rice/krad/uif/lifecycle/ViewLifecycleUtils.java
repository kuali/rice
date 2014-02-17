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
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.RecycleUtils;

/**
 * Utilities for working with {@link LifecycleElement} instances.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class ViewLifecycleUtils {

    private static final Logger LOG = Logger.getLogger(ViewLifecycleUtils.class);

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

        returnMap.put(propertyName, (LifecycleElement) nestedElement.unwrap());
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
     *
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    private static class ElementMetadata {

        /**
         * Set of all view phases, to use as keys for {@link #lifecycleRestrictedProperties}.
         */
        private static final Set<String> VIEW_PHASES;

        /**
         * Initializes the {@link #VIEW_PHASES} set with all constants defined in
         * {@link UifConstants.ViewPhases}.
         */
        static {
            Set<String> viewPhases = new LinkedHashSet<String>();
            for (java.lang.reflect.Field phaseConstantField : UifConstants.ViewPhases.class.getFields()) {
                int mod = phaseConstantField.getModifiers();
                if (phaseConstantField.getType().equals(String.class) &&
                        Modifier.isStatic(mod) && Modifier.isFinal(mod)) {
                    try {
                        viewPhases.add((String) phaseConstantField.get(null));
                    } catch (IllegalAccessException e) {
                        throw new ExceptionInInitializerError(e);
                    }
                }
            }
            VIEW_PHASES = Collections.unmodifiableSet(viewPhases);
        }

        /**
         * Set of all restricted properties on the element class, keyed by view phase.
         */
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

            Map<String, Set<String>> mutableLifecycleRestrictedProperties = new HashMap<String, Set<String>>(
                    restrictedPropertyNames.size());

            for (String restrictedPropertyName : restrictedPropertyNames) {
                ViewLifecycleRestriction restriction = ObjectPropertyUtils.getReadMethod(elementClass,
                        restrictedPropertyName).getAnnotation(ViewLifecycleRestriction.class);
                for (String phase : restriction.value()) {
                    Set<String> restrictedByPhase = mutableLifecycleRestrictedProperties.get(phase);

                    if (restrictedByPhase == null) {
                        restrictedByPhase = new HashSet<String>(restrictedPropertyNames);
                        mutableLifecycleRestrictedProperties.put(phase, restrictedByPhase);
                    }

                    restrictedByPhase.remove(restrictedPropertyName);
                    break;
                }
            }

            Set<String> immutableRestrictedPropertyNames = Collections.unmodifiableSet(restrictedPropertyNames);
            for (String phase : VIEW_PHASES) {
                Set<String> restrictedByPhase = mutableLifecycleRestrictedProperties.get(phase);

                if (restrictedByPhase == null) {
                    mutableLifecycleRestrictedProperties.put(phase, immutableRestrictedPropertyNames);
                } else {
                    mutableLifecycleRestrictedProperties.put(phase, Collections.unmodifiableSet(restrictedByPhase));
                }
            }

            lifecycleRestrictedProperties = Collections.unmodifiableMap(mutableLifecycleRestrictedProperties);
        }
    }

}