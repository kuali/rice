/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Created on May 8, 2006

package edu.iu.uis.eden.test.web.framework;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import edu.iu.uis.eden.test.web.ChainingFilter;

/**
 * Encapsulate script state, in a Map of variables which can be accessed using
 * Jakarta Commons BeanUtils property syntax.
 * There are a few "special" variables:
 * <dl>
 * <dt>USER</dt>
 * <dd>The user as whom to submit requests</dd>
 * <dt>BACKDOORID</dt>
 * <dd>The backdoor id</dd>
 * <dt>RESOURCE_PREFIX</dt>
 * <dd>If set, this is prefixed to any unqualifed resource lookups</dd>
 * <dt>REQUEST</dt>
 * <dd>A Map of request parameters</dd>
 * <dt>RESPONSE</dt>
 * <dd>A Map of response variables</dd>
 * <dt>CONTEXT</dt>
 * <dd>The caller-supplied context of the script (if any)</dd>
 * <dt>FILTERS</dt>
 * <dd>Named filters (if any, by default: DUPLICATE_SPACES, LEADING_SPACES, TRAILING_SPACES, DUPLICATE_NEWLINES, TRANSIENT_OUTPUT, CANONICALIZE)</dd>
 * </dl>
 * This class provides access to "properties" which are abstract names that can be resolved in several ways: via
 * the variable map, as a resource in the classloader, as a url, and as literal text.
 * @see edu.iu.uis.eden.test.web.framework.actions.SubmitAction for information on RESPONSE Map contents
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ScriptState {
    public static final String USER = "USER";
    public static final String BACKDOORID = "BACKDOORID";
    public static final String RESOURCE_PREFIX = "RESOURCE_PREFIX";
    public static final String REQUEST = "REQUEST";
    public static final String RESPONSE = "RESPONSE";
    public static final String CONTEXT = "CONTEXT";
    public static final String FILTERS = "FILTERS";

    /* filters */

    public static final Pattern DUPLICATE_SPACES;
    public static final Pattern LEADING_SPACES;
    public static final Pattern TRAILING_SPACES;
    public static final Pattern DUPLICATE_NEWLINES;
    public static final Pattern TRANSIENT_OUTPUT;
    static {
        DUPLICATE_SPACES = Pattern.compile("[\\s&&[^\n]]+");
        LEADING_SPACES = Pattern.compile("(?m)^\\s+");
        TRAILING_SPACES = Pattern.compile("(?m)\\s+$");
        DUPLICATE_NEWLINES = Pattern.compile("\n{2,}");
        TRANSIENT_OUTPUT = Pattern.compile("(?s)\\[transient start\\](.*?)\\[transient end\\]");
    }

    private static final Logger LOG = Logger.getLogger(ScriptState.class);

    private static final String DUPLICATE_SPACES_FILTER_NAME = "DUPLICATE_SPACES";
    private static final String LEADING_SPACES_FILTER_NAME = "LEADING_SPACES"; 
    private static final String TRAILING_SPACES_FILTER_NAME = "TRAILING_SPACES"; 
    private static final String DUPLICATE_NEWLINES_FILTER_NAME = "DUPLICATE_NEWLINES";
    private static final String TRANSIENT_OUTPUT_FILTER_NAME = "TRANSIENT_OUTPUT";
    public static final String CANONICALIZE_FILTER_NAME = "CANONICALIZE";

    public static final Filter DUPLICATE_SPACES_FILTER;
    public static final Filter LEADING_SPACES_FILTER;
    public static final Filter TRAILING_SPACES_FILTER;
    public static final Filter DUPLICATE_NEWLINES_FILTER;
    public static final Filter TRANSIENT_OUTPUT_FILTER;
    public static final Filter CANONICALIZE_FILTER;

    private static final Map DEFAULT_FILTERS = new HashMap();

    /* Initialize the default filters */
    static {
        DUPLICATE_SPACES_FILTER = new RegexReplacementFilter(DUPLICATE_SPACES, " ");
        LEADING_SPACES_FILTER = new RegexReplacementFilter(LEADING_SPACES, "");
        TRAILING_SPACES_FILTER = new RegexReplacementFilter(TRAILING_SPACES, "");
        DUPLICATE_NEWLINES_FILTER = new RegexReplacementFilter(DUPLICATE_NEWLINES, "\n");
        TRANSIENT_OUTPUT_FILTER = new RegexReplacementFilter(TRANSIENT_OUTPUT, "REDACTED TRANSIENT DATA");

        List list = new ArrayList();
        list.add(DUPLICATE_SPACES_FILTER);
        list.add(LEADING_SPACES_FILTER);
        list.add(TRAILING_SPACES_FILTER);
        list.add(DUPLICATE_NEWLINES_FILTER);
        list.add(TRANSIENT_OUTPUT_FILTER);

        CANONICALIZE_FILTER = new ChainingFilter(list);

        DEFAULT_FILTERS.put(DUPLICATE_SPACES_FILTER_NAME, DUPLICATE_SPACES_FILTER);
        DEFAULT_FILTERS.put(LEADING_SPACES_FILTER_NAME, LEADING_SPACES_FILTER);
        DEFAULT_FILTERS.put(TRAILING_SPACES_FILTER_NAME, TRAILING_SPACES_FILTER);
        DEFAULT_FILTERS.put(DUPLICATE_NEWLINES_FILTER_NAME, DUPLICATE_NEWLINES_FILTER);
        DEFAULT_FILTERS.put(TRANSIENT_OUTPUT_FILTER_NAME, TRANSIENT_OUTPUT_FILTER);
        DEFAULT_FILTERS.put(CANONICALIZE_FILTER_NAME, CANONICALIZE_FILTER);
    }

    private Map state = new HashMap();

    public ScriptState() {
        reset();
    }

    public void reset() {
        state.clear();
        state.put(USER, null);              // the end user
        state.put(BACKDOORID, null);        // the backdoorid
        state.put(REQUEST, new HashMap());  // the parameter map
        state.put(RESPONSE, new HashMap()); // the response map
        state.put(CONTEXT, null);           // the context map
        state.put(FILTERS, new HashMap(DEFAULT_FILTERS));  // the filters map
    }

    public String getUser() {
        return (String) state.get(USER);
    }

    public void setUser(String user) {
        state.put(USER, user);
    }

    public String getBackdoorId() {
        return (String) state.get(BACKDOORID);
    }

    public void setBackdoorId(String backdoorId) {
        state.put(BACKDOORID, backdoorId);
    }

    public String getResourcePrefix() {
        return (String) state.get(RESOURCE_PREFIX);
    }

    public void setResourcePrefix(String prefix) {
        state.put(RESOURCE_PREFIX, prefix);
    }

    public Map getRequest() {
        return (Map) state.get(REQUEST);
    }

    public Map getResponse() {
        return (Map) state.get(RESPONSE);
    }

    public Map getContext() {
        return (Map) state.get(CONTEXT);
    }

    public void setContext(Map context) {
        state.put(CONTEXT, context);
    }

    public Map getFilters() {
        return (Map) state.get(FILTERS);
    }

    public Map getVariables() {
        return state;
    }

    /**
     * Sets a key/value pair in the state map root
     * @param name name of the variable
     * @param value value of the variable
     */
    public void setVariable(String name, Object value) {
        try {
            PropertyUtils.setProperty(state, name, value);
        } catch (InvocationTargetException ite) {
            throw new RuntimeException("Error setting property: '" + name + "'", ite);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException("Error setting property: '" + name + "'", iae);
        } catch (NoSuchMethodException nsme) {
            throw new RuntimeException("Error setting property: '" + name + "'", nsme);
        }
    }

    /**
     * Resolves the specified name as a variable
     * @param name the variable name
     * @return variable value if found, null otherwise
     */
    public Object getVariable(String name) {
        return retrieveProperty(new Property(PropertyScheme.VARIABLE_SCHEME.getName(), name));
    }

    /**
     * Resolves the specified name as a literal (just returns it)
     * @param name the literal name
     * @return the literal name
     */
    public Object getLiteral(String name) {
        return retrieveProperty(new Property(PropertyScheme.LITERAL_SCHEME.getName(), name));
    }

    /**
     * Resolves the specified name as a resource
     * @param name the resource name
     * @return resource contents if found, null otherwise
     */
    public Object getResource(String name) {
        return retrieveProperty(new Property(PropertyScheme.RESOURCE_SCHEME.getName(), name));
    }

    /**
     * Resolves the specified name as a url
     * @param name the url
     * @return url contents if found, null otherwise
     */
    public Object getURL(String name) {
        return retrieveProperty(new Property(PropertyScheme.URL_SCHEME.getName(), name));
    }

    /**
     * Resolves the specified name as a qualified property
     * @param name the qualified property name
     * @return value if found, null otherwise
     */
    public Object retrieveProperty(String name) {
        return retrieveProperty(new Property(name));
    }

    /**
     * Resolves the specified name as an unqualified property
     * @param name the potentially unqualified property name
     * @param defaultScheme the default scheme to use if the property is unqualified
     * @return value if found, null otherwise
     */
    public Object retrieveProperty(String name, PropertyScheme defaultScheme) {
        return retrieveProperty(new Property(name), defaultScheme);
    }

    /**
     * Resolves the specified name as an unqualified property
     * @param prop the potentially unqualified property
     * @param defaultScheme the default scheme to use if the property is unqualified
     * @return value if found, null otherwise
     */
    public Object retrieveProperty(Property prop, PropertyScheme defaultScheme) {
        if (prop.scheme == null && defaultScheme != null) {
            prop.scheme = defaultScheme.getName();
        }
        return retrieveProperty(prop);
    }

    /**
     * Resolves the specified name as a qualified property
     * @param prop the qualified property
     * @return value if found, null otherwise
     */
    public Object retrieveProperty(Property prop) {
        Iterator schemes = PropertyScheme.SCHEMES.iterator();
        while (schemes.hasNext()) {
            PropertyScheme scheme = (PropertyScheme) schemes.next();
            if (scheme.getName().equals(prop.scheme) ||
                scheme.getShortName().equals(prop.scheme)) {
                return scheme.load(prop, this);
            }
        }
        String message = "Invalid property scheme: '" + prop.scheme + "'"; 
        LOG.error(message);
        throw new RuntimeException(message);
    }
}