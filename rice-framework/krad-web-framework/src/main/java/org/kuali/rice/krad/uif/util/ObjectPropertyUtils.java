/**
 * Copyright 2005-2013 The Kuali Foundation
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;
import org.kuali.rice.krad.datadictionary.parse.StringListConverter;
import org.kuali.rice.krad.datadictionary.parse.StringMapConverter;
import org.kuali.rice.krad.uif.util.ObjectPropertyReference.Resolver;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.core.convert.support.GenericConversionService;

/**
 * Utility methods to get/set property values and working with objects
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ObjectPropertyUtils {

    private static final Logger LOG = Logger.getLogger(ObjectPropertyUtils.class);

    /**
     * Used by {@link #parsePathExpression(Object, String, PathEntry)} to track parse state without
     * the need to construct a new parser stack for each expression.
     */
    private static final ThreadLocal<Deque<Object>> TL_EL_PARSER_STACK = new ThreadLocal<Deque<Object>>();

    /**
     * Array of quote characters supported by
     * {@link #parsePathExpression(Object, String, PathEntry)}.
     */
    private static final char[] QUOTE_CHARS = new char[]{'\"', '\'',};

    /**
     * Internal property descriptor cache.
     * 
     * <p>
     * NOTE: WeakHashMap is used as the internal cache representation. Since class objects are used
     * as the keys, this allows property descriptors to stay in cache until the class loader is
     * unloaded, but will not prevent the class loader itself from unloading. PropertyDescriptor
     * instances do not hold hard references back to the classes they refer to, so weak value
     * maintenance is not necessary.
     * </p>
     */
    private static Map<Class<?>, Map<String, PropertyDescriptor>> PROPERTY_DESCRIPTOR_CACHE = Collections
            .synchronizedMap(new WeakHashMap<Class<?>, Map<String, PropertyDescriptor>>(2048));

    /**
     * Private constructor - utility class only.
     */
    private ObjectPropertyUtils() {}

    /**
     * Find a character in a sequence, paying attention to quotes, escape characters, and nested
     * grouping markers.
     * 
     * @param charSequence The string.
     * @param findChar The character to find.
     * @param quoteChar Array of quote character.
     * @param escapeChar The escape character.
     * @param openMarkerIndex The index of the group's opening marker, for nesting support. When 0
     *        or greater, then it is expected that character at the given position marks the start
     *        of a logical group (e.g. '[') and that findChar marks the end of a group (e.g. ']').
     *        The use of -1 to find open markers.
     * 
     * @return The position of an the next occurrence of findChar in charSequence that does not
     *         occur within a quoted string or nested group, and is not escaped.
     */
    public static int indexOfExt(CharSequence charSequence, char findChar, char[] quoteChars, int escapeChar,
            int openMarkerIndex) {

        // Short circuit: null string or search from beyond the end of the string, return -1. 
        if (charSequence == null || openMarkerIndex + 1 >= charSequence.length()) {
            return -1;
        }

        // Start scanning at the index of the indicated grouping open marker.
        char currentChar = (openMarkerIndex == -1 ? '\0' : charSequence.charAt(openMarkerIndex));

        // Note the open group marker.
        char groupOpenMarker = currentChar;

        // Look back one character during lexical scanning.
        char lastChar = currentChar;

        // Track open markers encountered while scanning.
        int nestedGroupCount = 0;

        // Track quote state.
        char inQuote = '\0';

        // Scan the character sequence, starting with the character following the open marker.
        for (int currentIndex = openMarkerIndex + 1; currentIndex < charSequence.length(); currentIndex++) {
            lastChar = currentChar;
            currentChar = charSequence.charAt(currentIndex);

            // Ignore escaped characters.
            if (lastChar == escapeChar) {
                continue;
            }

            // Toggle quote state when a quote is encountered.
            if (inQuote == '\0') {
                for (char quoteChar : quoteChars) {
                    if (currentChar == quoteChar) {
                        inQuote = quoteChar;
                    }
                }
            } else if (currentChar == inQuote) {
                inQuote = '\0';
            }

            // Ignore quoted characters.
            if (inQuote == '\0') {
                if (currentChar == findChar) {
                    // The character has been found.
                    if (nestedGroupCount == 0) {
                        // Return the current index when not within a nested group.
                        return currentIndex;
                    } else {
                        // Decrement the nested group count when crossing a nested group close marker.
                        nestedGroupCount--;
                    }
                } else if (currentChar == groupOpenMarker) {
                    // Increment the nested group count when crossing an open group marker.
                    nestedGroupCount++;
                }
            }
        }

        // The characer has not been found, return -1.
        return -1;
    }

    /**
     * Find a character in a sequence, paying attention to EL default quotes and escape characters.
     * 
     * @param charSequence The string.
     * @param findChar The character to find.
     * 
     * @return The position of the next occurrence of findChar in charSequence that does not occur
     *         within a quoted string, and is not escaped.
     * @see #parsePathExpression(Object, String, PathEntry)
     */
    public static int indexOfExt(CharSequence charSequence, char findChar) {
        return indexOfExt(charSequence, findChar, QUOTE_CHARS, '\\', -1);
    }

    /**
     * Path entry interface for use with
     * {@link ObjectPropertyUtils#parsePathExpression(Object, String, PathEntry)}.
     * 
     * @param <T> Reference type representing the next parse node.
     * @param <S> The parse node type.
     */
    public static interface PathEntry<T, S> {

        /**
         * Parse one node.
         * 
         * @param node The current parse node.
         * @param next The next path token.
         * @param inherit True indicates that the current node is the result of a subexpression,
         *        false indicates the next node in the same expression.
         * @return A reference to the next parse node.
         */
        T parse(S node, String next, boolean inherit);

        /**
         * Prepare the next parse node based on a reference returned from the prior node.
         * 
         * @param prev The reference data from the previous node.
         * @return The next parse node.
         */
        S prepare(T prev);

        /**
         * Resolve the next path element based on reference data from the previous node.
         * 
         * @param prev The reference data from the previous node.
         * @return the next path element based on the returned reference data.
         */
        String dereference(T prev);
    }

    /**
     * Simple abstract path entry, to be used when all parse nodes transition to the same type and
     * subexpressions are not supported.
     */
    public static abstract class SimplePathEntry<T> implements PathEntry<T, T> {

        /**
         * Parse one node.
         * 
         * @param node The current parse node.
         * @param next The next path token.
         * @return A reference to the next parse node.
         */
        public abstract T parse(T node, String next);

        @Override
        public T parse(T node, String next, boolean inherit) {
            return parse(node, next);
        }

        @Override
        public T prepare(T prev) {
            return prev;
        }

        @Override
        public String dereference(T prev) {
            return (String) prev;
        }
    }

    /**
     * Parse a path expression. Provides modular support for the partial JSP EL path syntax using an
     * arbitrary root bean as the initial name space.
     * 
     * <p>
     * NOTE: This is not full JSP EL, only the path reference portion without support for floating
     * point literals. See this <a href= "http://jsp.java.net/spec/jsp-2_1-fr-spec-el.pdf"> JSP
     * Reference</a> for the full BNF.
     * </p>
     * 
     * <pre>
     * Value ::= ValuePrefix (ValueSuffix)*
     * ValuePrefix ::= Literal
     *     | NonLiteralValuePrefix
     * NonLiteralValuePrefix ::= '(' Expression ')'
     *     | Identifier
     * ValueSuffix ::= '.' Identifier
     *     | '[' Expression ']'
     * Identifier ::= Java language identifier
     * Literal ::= BooleanLiteral
     *     | IntegerLiteral
     *     | FloatingPointLiteral
     *     | StringLiteral
     *     | NullLiteral
     * BooleanLiteral ::= 'true'
     *     | 'false'
     * StringLiteral ::= '([^'\]|\'|\\)*'
     *     | "([^"\]|\"|\\)*"
     *   i.e., a string of any characters enclosed by
     *   single or double quotes, where \ is used to
     *   escape ', ",and \. It is possible to use single
     *   quotes within double quotes, and vice versa,
     *   without escaping.
     * IntegerLiteral ::= ['0'-'9']+
     * NullLiteral ::= 'null'
     * </pre>
     * 
     * @param root The root object.
     * @param path The path expression.
     * @param pathEntry The path entry adaptor to use for processing parse node transition.
     * @param <T> Reference type representing the next parse node.
     * @param <S> The parse node type.
     * 
     * @return The valid of the bean property indicated by the given path expression, null if the
     *         path expression doesn't resolve to a valid property.
     * @see ObjectPropertyUtils#getPropertyValue(Object, String)
     */
    public static <T, S> T parsePathExpression(final S root, String path,
            final PathEntry<T, S> pathEntry) {

        // NOTE: This iterative parser allows support for subexpressions
        // without recursion. When a subexpression start token '[' is
        // encountered the current continuation is pushed onto a stack. When
        // the subexpression is resolved, the continuation is popped back
        // off the stack and resolved using the subexpression result as the
        // arg. All subexpressions start with the same root passed in as an
        // argument for this method. - MWF

        @SuppressWarnings("unchecked")
        Deque<T> parserStack = (Deque<T>) TL_EL_PARSER_STACK.get();
        assert parserStack == null || parserStack.isEmpty();
        if (parserStack == null) {
            TL_EL_PARSER_STACK.set(new LinkedList<Object>());
            @SuppressWarnings("unchecked")
            Deque<T> newParserStack = (Deque<T>) TL_EL_PARSER_STACK.get();
            parserStack = newParserStack;
        }
        try {

            // Track current continuation
            T currentContinuation = pathEntry.parse(root, null, false);

            // Path is truncated as tokens are parsed, once the last token in the path has been parsed, it will be set to null.
            while (path != null) {

                // Locate the next token break
                int indexOfNextLeftParen = indexOfExt(path, '(');
                int indexOfNextLeftBracket = indexOfExt(path, '[');
                int indexOfNextPeriod = indexOfExt(path, '.');
                int indexOfNextRightParen = indexOfExt(path, ')');
                int indexOfNextRightBracket = indexOfExt(path, ']');

                // Identify tokens hidden by parentheses
                if (indexOfNextLeftParen >= 0) {
                    if (indexOfNextRightParen == -1) {
                        throw new IllegalArgumentException("Unmatched '(': " + path);
                    }
                    if (indexOfNextRightParen >= path.length() - 1) {
                        indexOfNextLeftBracket = -1;
                        indexOfNextPeriod = -1;
                        indexOfNextRightBracket = -1;
                    }
                    if (indexOfNextLeftParen < indexOfNextLeftBracket && indexOfNextLeftBracket < indexOfNextRightParen) {
                        int newIndexOfNextLeftBracket = indexOfExt(path.substring(indexOfNextRightParen + 1), '[');
                        if (newIndexOfNextLeftBracket == -1) {
                            indexOfNextLeftBracket = -1;
                        } else {
                            indexOfNextLeftBracket = newIndexOfNextLeftBracket + indexOfNextRightParen + 1;
                        }
                    }
                    if (indexOfNextLeftParen < indexOfNextPeriod && indexOfNextPeriod < indexOfNextRightParen) {
                        int newIndexOfNextPeriod = indexOfExt(path.substring(indexOfNextRightParen + 1), '.');
                        if (newIndexOfNextPeriod == -1) {
                            indexOfNextPeriod = -1;
                        } else {
                            indexOfNextPeriod = newIndexOfNextPeriod + indexOfNextRightParen + 1;
                        }
                    }
                    if (indexOfNextLeftParen < indexOfNextRightBracket
                            && indexOfNextRightBracket < indexOfNextRightParen) {
                        int newIndexOfRightBracket = indexOfExt(path.substring(indexOfNextRightParen + 1), ']');
                        if (newIndexOfRightBracket == -1) {
                            indexOfNextRightBracket = -1;
                        } else {
                            indexOfNextRightBracket = newIndexOfRightBracket + indexOfNextRightParen + 1;
                        }
                    }
                } else if (indexOfNextRightParen >= 0) {
                    throw new IllegalArgumentException("Unmatched ')': " + path);
                }

                // Special case #1: right bracket is hidden by the left bracket
                // Example: foo[bar].baz
                if (indexOfNextLeftBracket != -1 && indexOfNextRightBracket > indexOfNextLeftBracket) {
                    // If the left bracket comes first, ignore the right bracket
                    indexOfNextRightBracket = -1;
                }

                // Special case #2: brackets are hidden by a dot
                // Example: foo.bar[baz]
                if (indexOfNextPeriod != -1 && indexOfNextLeftBracket > indexOfNextPeriod) {
                    // If the dot comes first, ignore the brackets
                    indexOfNextLeftBracket = -1;
                }

                // Special case #3: left bracket hidden by a right bracket
                // Example: foo][bar.baz]
                if (indexOfNextRightBracket != -1 && indexOfNextLeftBracket > indexOfNextRightBracket) {
                    // If the right bracket comes first, ignore the left bracket
                    indexOfNextLeftBracket = -1;
                }

                // Special case #4: dot is hidden by a right bracket.
                // Example: foo].bar[baz]
                if (indexOfNextRightBracket != -1 && indexOfNextPeriod > indexOfNextRightBracket) {
                    // If the right bracket comes first, ignore the dot
                    indexOfNextPeriod = -1;
                }

                // Special case #5: parenthetical expression on the left, remove the parentheses
                if (indexOfNextLeftParen == 0) {
                    if (indexOfNextRightParen < path.length() - 1) {
                        path = path.substring(1, indexOfNextRightParen) + path.substring(indexOfNextRightParen + 1);
                    } else {
                        path = path.substring(1, indexOfNextRightParen);
                    }
                    indexOfNextLeftParen = -1;
                    indexOfNextRightParen = -1;
                    if (indexOfNextPeriod >= 2) {
                        indexOfNextPeriod -= 2;
                    }
                    if (indexOfNextLeftBracket >= 2) {
                        indexOfNextLeftBracket -= 2;
                    }
                    if (indexOfNextRightBracket >= 2) {
                        indexOfNextRightBracket -= 2;
                    }
                }

                // Entering bracketed subexpression
                if (indexOfNextLeftBracket != -1) {

                    // Resolve non-empty key reference as the current continuation
                    if (indexOfNextLeftBracket != 0) {
                        currentContinuation = pathEntry.parse(pathEntry.prepare(currentContinuation),
                                path.substring(0, indexOfNextLeftBracket), false);
                    }

                    // Push current continuation down in the stack.
                    parserStack.push(currentContinuation);

                    // Reset the current continuation for evaluating the
                    // subexpression
                    currentContinuation = pathEntry.parse(root, null, false);
                    path = path.substring(indexOfNextLeftBracket + 1);

                } else if (indexOfNextLeftParen != -1) {
                    // Approaching a parenthetical expression, not preceded by a subexpression,
                    // resolve the key reference as the current continuation
                    currentContinuation = pathEntry.parse(pathEntry.prepare(currentContinuation),
                            path.substring(0, indexOfNextLeftParen), false);
                    path = path.substring(indexOfNextLeftParen); // Keep the left parenthesis

                } else if (indexOfNextPeriod != -1) {
                    // Crossing a period, not preceded by a subexpression,
                    // resolve the key reference as the current continuation
                    currentContinuation = pathEntry.parse(pathEntry.prepare(currentContinuation),
                            path.substring(0, indexOfNextPeriod), false);
                    path = path.substring(indexOfNextPeriod + 1); // Skip the period

                } else if (indexOfNextRightBracket > 0) {
                    // Approaching a right bracket, resolve the key reference as the current continuation
                    currentContinuation = pathEntry.parse(pathEntry.prepare(currentContinuation),
                            path.substring(0, indexOfNextRightBracket), false);
                    path = path.substring(indexOfNextRightBracket); // Keep the right bracket

                } else if (indexOfNextRightBracket == 0) {
                    // Crossing a right bracket.

                    // Use the current continuation as the parameter for resolving
                    // the top continuation on the stack, then make the result the
                    // current continuation.
                    currentContinuation = pathEntry.parse(pathEntry.prepare(parserStack.pop()),
                            pathEntry.dereference(currentContinuation), true);
                    if (indexOfNextRightBracket + 1 < path.length()) {
                        // short-circuit the next step, as an optimization for
                        // handling dot resolution without permitting double-dots
                        switch (path.charAt(indexOfNextRightBracket + 1)) {
                            case '.':
                                // crossing a dot, skip it
                                path = path.substring(indexOfNextRightBracket + 2);
                                break;
                            case '[':
                            case ']':
                                // crossing to another subexpression, don't skip it.
                                path = path.substring(indexOfNextRightBracket + 1);
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Expected '.', '[', or ']': " + path);
                        }
                    } else {
                        path = null;
                    }
                } else {
                    // Only a symbolic reference, resolve it and return.
                    currentContinuation = pathEntry.parse(pathEntry.prepare(currentContinuation), path, false);
                    path = null;
                }

            }
            return currentContinuation;
        } finally {
            assert parserStack == TL_EL_PARSER_STACK.get();
            if (!parserStack.isEmpty()) {
                parserStack.clear();
            }
        }
    }

    /**
     * Get a mapping of property descriptors by property name for a bean class.
     * 
     * @param beanClass The bean class.
     * @return A mapping of all property descriptors for the bean class, by property name.
     */
    public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> beanClass) {
        Map<String, PropertyDescriptor> propertyDescriptors = PROPERTY_DESCRIPTOR_CACHE.get(beanClass);
        if (propertyDescriptors == null) {
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(beanClass);
            } catch (IntrospectionException E) {
                throw new IllegalArgumentException(
                        "Bean Info not found for bean " + beanClass);
            }
            Map<String, PropertyDescriptor> unsynchronizedPropertyDescriptorMap = new java.util.LinkedHashMap<String, PropertyDescriptor>();
            for (PropertyDescriptor propertyDescriptor : beanInfo
                    .getPropertyDescriptors()) {
                unsynchronizedPropertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            }
            propertyDescriptors = Collections.unmodifiableMap(Collections
                    .synchronizedMap(unsynchronizedPropertyDescriptorMap));
        }
        return propertyDescriptors;
    }

    /**
     * Get a property descriptor from a class by property name.
     * 
     * @param beanClass The bean class.
     * @param propertyName The bean property name.
     * @return The property descriptor named on the bean class.
     */
    public static PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        PropertyDescriptor propertyDescriptor = getPropertyDescriptors(beanClass).get(propertyName);
        if (propertyDescriptor != null) {
            return propertyDescriptor;
        } else {
            throw new IllegalArgumentException("Property " + propertyName
                    + " not found for bean " + beanClass);
        }
    }

    /**
     * Get the read method for a specific property on a bean class.
     * 
     * @param beanClass The bean class.
     * @param propertyName The property name.
     * @return The read method for the property.
     */
    private static Method getReadMethod(Class<?> beanClass, String propertyName) {
        Method rm;
        try {
            rm = getPropertyDescriptor(beanClass, propertyName).getReadMethod();
        } catch (IllegalArgumentException e) {
            rm = null;
        }
        if (rm == null)
            try {
                rm = beanClass.getMethod("get" + Character.toUpperCase(propertyName.charAt(0))
                        + propertyName.substring(1));
            } catch (SecurityException e) {} catch (NoSuchMethodException e) {}
        if (rm == null)
            try {
                Method trm = beanClass.getMethod("is"
                        + Character.toUpperCase(propertyName.charAt(0))
                        + propertyName.substring(1));
                if (trm.getReturnType() == Boolean.class
                        || trm.getReturnType() == Boolean.TYPE)
                    rm = trm;
            } catch (SecurityException e) {} catch (NoSuchMethodException e) {}
        return rm;
    }

    static boolean canRead(Class<?> bc, String name) {
        if (name == null) // self reference
            return true;
        if (bc.isArray() || List.class.isAssignableFrom(bc))
            try {
                return "length".equals(name) || "size".equals(name)
                        || Integer.parseInt(name) >= 0;
            } catch (NumberFormatException e) {
                return false;
            }
        else if (Map.class.isAssignableFrom(bc))
            return true;
        else
            try {
                return getReadMethod(bc, name) != null;
            } catch (IllegalArgumentException e) {
                return false;
            }
    }

    Class<?> _getPropertyType(Class<?> bc, String name) {
        if (bc == null)
            return null;
        if (name == null)
            return bc;
        if (!canRead(bc, name))
            throw new IllegalArgumentException("Property " + name + " on " + bc
                    + " is not readable");
        if (bc.isArray())
            if ("length".equals(name) || "size".equals(name))
                return int.class;
            else
                return bc.getComponentType();
        else if (Map.class.isAssignableFrom(bc)
                || List.class.isAssignableFrom(bc))
            if ("length".equals(name) || "size".equals(name))
                return int.class;
            else
                return Object.class;
        else
            try {
                return getReadMethod(bc, name).getReturnType();
            } catch (NullPointerException E) {
                LOG.debug("null return type reference", E);
                return null;
            } catch (IllegalArgumentException E) {
                // LOG.debug( "invalid return type reference", E );
                return null;
            }
    }

    static <V> V get(Object bean, Class<?> bc, String name) {
        if (!canRead(bc, name))
            throw new IllegalArgumentException(
                    "No read method defined for property " + name + " on " + bc);

        if (name == null) {
            @SuppressWarnings("unchecked")
            V rv = (V) bean;
            return rv;

        } else if (bc.isArray())
            if ("length".equals(name) || "size".equals(name)) {
                @SuppressWarnings("unchecked")
                V rv = (V) new Integer(Array.getLength(bean));
                return rv;
            } else {
                int i = Integer.parseInt(name);
                if (i >= Array.getLength(bean))
                    throw new IllegalArgumentException("Array index " + i
                            + " in path expression is greater than array size "
                            + Arrays.toString((Object[]) bean));
                @SuppressWarnings("unchecked")
                V rv = (V) Array.get(bean, i);
                return rv;
            }

        else if (List.class.isAssignableFrom(bc)) {
            List<?> rl = (List<?>) bean;
            if ("length".equals(name) || "size".equals(name)) {
                @SuppressWarnings("unchecked")
                V rv = (V) new Integer(rl.size());
                return rv;
            } else {
                int i = Integer.parseInt(name);
                if (i >= rl.size())
                    throw new IllegalArgumentException("Array index " + i
                            + " in path expression is greater than list size "
                            + rl);
                @SuppressWarnings("unchecked")
                V rv = (V) rl.get(i);
                return rv;
            }

        } else if (Map.class.isAssignableFrom(bc)) {
            Map<?, ?> rm = (Map<?, ?>) bean;
            if ("length".equals(name) || "size".equals(name)) {
                @SuppressWarnings("unchecked")
                V rv = (V) new Integer(rm.size());
                return rv;
            } else if (!rm.containsKey(name))
                throw new IllegalArgumentException("No key " + name
                        + " found in map " + rm);
            else {
                @SuppressWarnings("unchecked")
                V rv = (V) rm.get(name);
                return rv;
            }

        } else {
            Method readMethod = getReadMethod(bc, name);
            try {
                @SuppressWarnings("unchecked")
                V rv = (V) getReadMethod(bc, name).invoke(bean,
                        new Object[0]);
                return rv;
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Illegal access invoking property read method " + readMethod, e);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                }
                throw new IllegalStateException("Unexpected invocation target exception invoking property read method "
                        + readMethod, e);
            }
        }
    }

    private static <V> ObjectPropertyReference<V> dereference(Object bean, String path) {
        return ObjectPropertyReference.resolvePath(bean, path, new Resolver<V>() {
            @Override
            public V dereference(Object bean, String name) {
                return get(bean, bean.getClass(), name);
            }
        });
    }

    public static void copyPropertiesToObject(Map<String, String> properties, Object object) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            setPropertyValue(object, property.getKey(), property.getValue());
        }
    }

    public static Class<?> getPropertyType(Class<?> object, String propertyPath) {
        return new BeanWrapperImpl(object).getPropertyType(propertyPath);
    }

    public static Class<?> getPropertyType(Object object, String propertyPath) {
        return wrapObject(object).getPropertyType(propertyPath);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Object> T getPropertyValue(Object object, String propertyPath) {
        if (ProcessLogger.isTraceActive() && object != null) {
            ProcessLogger.ntrace(object.getClass().getSimpleName() + ":r:" + propertyPath, "", 1000);
        }
        T result = null;
        try {
//            ObjectPropertyReference<T> ref = dereference(object, propertyPath);
//            result = ref.dereference();
            result = (T) wrapObject(object).getPropertyValue(propertyPath);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error getting property '" + propertyPath + "' from " + object, e);
        }
        return result;
    }

    public static void initializeProperty(Object object, String propertyPath) {
        Class<?> propertyType = getPropertyType(object, propertyPath);
        try {
            setPropertyValue(object, propertyPath, propertyType.newInstance());
        } catch (InstantiationException e) {
            // just set the value to null
            setPropertyValue(object, propertyPath, null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to set new instance for property: " + propertyPath, e);
        }
    }

    public static void setPropertyValue(Object object, String propertyPath, Object propertyValue) {
        if (ProcessLogger.isTraceActive() && object != null) {
            ProcessLogger.ntrace(object.getClass().getSimpleName() + ":w:" + propertyPath + ":", "", 1000);
        }
        wrapObject(object).setPropertyValue(propertyPath, propertyValue);
    }

    public static void setPropertyValue(Object object, String propertyPath, Object propertyValue, boolean ignoreUnknown) {
        try {
            setPropertyValue(object, propertyPath, propertyValue);
        } catch (BeansException e) {
            // only throw exception if they have indicated to not ignore unknown
            if (!ignoreUnknown) {
                throw new RuntimeException(e);
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("Ignoring exception thrown during setting of property '" + propertyPath + "': "
                        + e.getLocalizedMessage());
            }
        }
    }

    public static boolean isReadableProperty(Object object, String propertyPath) {
        return wrapObject(object).isReadableProperty(propertyPath);
    }

    public static boolean isWritableProperty(Object object, String propertyPath) {
        return wrapObject(object).isWritableProperty(propertyPath);
    }

    /**
     * 
     * @param object
     * @return
     * @deprecated KULRICE-10063 BeanWrapper is unnecessarily heavy, and is being replaced.
     */
    public static BeanWrapper wrapObject(Object object) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(object);
        beanWrapper.setAutoGrowNestedPaths(true);

        GenericConversionService conversionService = new GenericConversionService();
        conversionService.addConverter(new StringMapConverter());
        conversionService.addConverter(new StringListConverter());
        beanWrapper.setConversionService(conversionService);

        return beanWrapper;
    }

}
