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

import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.uif.component.ReferenceCopy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class for copying objects using reflection. Modified from the jCommon
 * library: http://www.matthicks.com/2008/05/fastest-deep-cloning.html
 * 
 * @author Matt Hicks
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CloneUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CloneUtils.class);

    private static final Map<String, Field[]> fieldCache = new HashMap<String, Field[]>();
    private static final Map<String, Field> internalFields = new HashMap<String, Field>();

    public static final <O> O deepClone(O original) {
        try {
            return deepCloneReflection(original);
        } catch (Exception e) {
            throw new RiceRuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static final <O> O deepCloneReflection(O original) throws Exception {
        return (O) deepCloneReflectionInternal(original, new HashMap<Object, Object>(), false);
    }

    protected static final Object deepCloneReflectionInternal(Object original, Map<Object, Object> cache,
            boolean referenceCollectionCopy) throws Exception {
        if (original == null) { // No need to clone nulls
            return original;
        }
        else if (cache.containsKey(original)) {
            return cache.get(original);
        }

        // Deep clone
        Object clone = null;
        if (List.class.isAssignableFrom(original.getClass())) {
            clone = deepCloneList(original, cache, referenceCollectionCopy);
        }
        else if (Map.class.isAssignableFrom(original.getClass())) {
            clone = deepCloneMap(original, cache, referenceCollectionCopy);
        }
        else {
            clone = deepCloneObject(original, cache);
        }

        return clone;
    }

    protected static Object deepCloneObject(Object original, Map<Object, Object> cache) throws Exception {
        if (original instanceof Number) { // Numbers are immutable
            if (original instanceof AtomicInteger) {
                // AtomicIntegers are mutable
            }
            else if (original instanceof AtomicLong) {
                // AtomLongs are mutable
            }
            else {
                return original;
            }
        }
        else if (original instanceof String) { // Strings are immutable
            return original;
        }
        else if (original instanceof Character) { // Characters are immutable
            return original;
        }
        else if (original instanceof Class) { // Classes are immutable
            return original;
        }
        else if (original instanceof Boolean) {
            return new Boolean(((Boolean) original).booleanValue());
        }

        // To our understanding, this is a mutable object, so clone it
        Class<?> c = original.getClass();
        Field[] fields = getFields(c, false);
        try {
            Object copy = instantiate(original);

            // Put into cache
            cache.put(original, copy);

            // iterate through and copy fields
            for (Field f : fields) {
                Object object = f.get(original);

                boolean referenceCopy = false;
                boolean referenceCollectionCopy = false;
                ReferenceCopy copyAnnotation = f.getAnnotation(ReferenceCopy.class);
                if (copyAnnotation != null) {
                    referenceCopy = true;
                    referenceCollectionCopy = copyAnnotation.newCollectionInstance();
                }

                if (!referenceCopy || referenceCollectionCopy) {
                    object = CloneUtils.deepCloneReflectionInternal(object, cache, referenceCollectionCopy);
                }
                f.set(copy, object);
            }

            return copy;
        }
        catch (Throwable t) {
            LOG.debug("Exception during clone (returning original): " + t.getMessage());
            return original;
        }
    }

    @SuppressWarnings("unchecked")
    protected static Object deepCloneMap(Object original, Map<Object, Object> cache, boolean referenceCollectionCopy)
            throws Exception {
        // Instantiate a new instance
        Map<Object, Object> clone = (Map<Object, Object>) instantiate(original);

        // Populate data
        for (Entry<Object, Object> entry : ((Map<Object, Object>) original).entrySet()) {
            if (referenceCollectionCopy) {
                clone.put(entry.getKey(), entry.getValue());
            }
            else {
                clone.put(deepCloneReflectionInternal(entry.getKey(), cache, false),
                        deepCloneReflectionInternal(entry.getValue(), cache, false));
            }
        }

        return clone;
    }

    @SuppressWarnings("unchecked")
    protected static Object deepCloneList(Object original, Map<Object, Object> cache, boolean referenceCollectionCopy)
            throws Exception {
        // Instantiate a new instance
        List<Object> clone = (List<Object>) instantiate(original);

        // Populate data
        for (Iterator<Object> iterator = ((List<Object>) original).iterator(); iterator.hasNext();) {
            Object object = iterator.next();
            if (referenceCollectionCopy) {
                clone.add(object);
            }
            else {
                clone.add(deepCloneReflectionInternal(object, cache, false));
            }
        }

        return clone;
    }

    /**
     * Retrieves all field names for the given class that have the given annotation
     *
     * @param clazz - class to find field annotations for
     * @param annotationClass - class for annotation to find
     * @return Map<String, Annotation> map containing the field name that has the annotation as a key and the
     *         annotation instance as a value
     */
    public static Map<String, Annotation> getFieldsWithAnnotation(Class<?> clazz,
            Class<? extends Annotation> annotationClass) {
        Map<String, Annotation> annotationFields = new HashMap<String, Annotation>();

        Field[] fields = getFields(clazz, false);
        for (Field f : fields) {
            Annotation fieldAnnotation = f.getAnnotation(annotationClass);
            if (fieldAnnotation != null) {
                annotationFields.put(f.getName(), fieldAnnotation);
            }
        }

        return annotationFields;
    }

    /**
     * Determines whether the property of the given class has the given annotation specified
     *
     * @param clazz - class containing the property to check
     * @param propertyName - name of the property to check
     * @param annotationClass - class for the annotation to look for
     * @return boolean true if the field associated with the property name has the given annotation, false if not
     */
    public static boolean fieldHasAnnotation(Class<?> clazz, String propertyName,
            Class<? extends Annotation> annotationClass) {
        Field[] fields = getFields(clazz, false);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getName().equals(propertyName)) {
                Annotation fieldAnnotation = field.getAnnotation(annotationClass);
                if (fieldAnnotation != null) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    protected static final Object instantiate(Object original) throws InstantiationException, IllegalAccessException {
        return original.getClass().newInstance();
    }

    public static Field[] getFields(Object object, boolean includeStatic) {
        return getFields(object, includeStatic, true);
    }

    public static Field[] getFields(Object object, boolean includeStatic, boolean includeTransient) {
        Class<?> c = object.getClass();
        return getFields(c, includeStatic, includeTransient);
    }

    public static Field[] getFields(Class<?> c, boolean includeStatic) {
        return getFields(c, includeStatic, true);
    }

    public static Field[] getFields(Class<?> c, boolean includeStatic, boolean includeTransient) {
        String cacheKey = c.getCanonicalName() + ":" + includeStatic;
        Field[] array = fieldCache.get(cacheKey);

        if (array == null) {
            ArrayList<Field> fields = new ArrayList<Field>();

            List<Class<?>> classes = getClassHierarchy(c, false);

            // Reverse order so we make sure we maintain consistent order
            Collections.reverse(classes);

            for (Class<?> clazz : classes) {
                Field[] allFields = clazz.getDeclaredFields();
                for (Field f : allFields) {
                    if ((!includeTransient) && ((f.getModifiers() & Modifier.TRANSIENT) == Modifier.TRANSIENT)) {
                        continue;
                    }
                    else if (f.isSynthetic()) {
                        // Synthetic fields are bad!!!
                        continue;
                    }
                    boolean isStatic = (f.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
                    if ((isStatic) && (!includeStatic)) {
                        continue;
                    }
                    if (f.getName().equalsIgnoreCase("serialVersionUID")) {
                        continue;
                    }
                    f.setAccessible(true);
                    fields.add(f);
                }
            }

            array = fields.toArray(new Field[fields.size()]);
            fieldCache.put(cacheKey, array);
        }
        return array;
    }

    protected static final Field internalField(Object object, String fieldName) {
        if (object == null) {
            System.out.println("Internal Field: " + object + ", " + fieldName);
            return null;
        }

        String key = object.getClass().getCanonicalName() + "." + fieldName;
        Field field = internalFields.get(key);
        if (field == null) {
            Field[] fields = getFields(object.getClass(), false);

            for (Field f : fields) {
                String name = f.getName();
                if (name.equals(fieldName)) {
                    field = f;
                    internalFields.put(key, field);
                    break;
                }
            }
        }

        return field;
    }

    protected static List<Class<?>> getClassHierarchy(Class<?> c, boolean includeInterfaces) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        while (c != Object.class) {
            classes.add(c);
            if (includeInterfaces) {
                Class<?>[] interfaces = c.getInterfaces();
                for (Class<?> i : interfaces) {
                    classes.add(i);
                }
            }
            c = c.getSuperclass();
            if (c == null) {
                break;
            }
        }

        return classes;
    }

}
