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
package edu.iu.uis.eden.test.web.framework.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import edu.iu.uis.eden.test.web.framework.Filter;
import edu.iu.uis.eden.test.web.framework.Property;
import edu.iu.uis.eden.test.web.framework.PropertyScheme;
import edu.iu.uis.eden.test.web.framework.Script;
import edu.iu.uis.eden.test.web.framework.Util;

/**
 * ScriptAction that sets or unsets a variable.
 * <pre>
 * &lt;variable name="..."  [ match="..." ] [ replace="..." replacement="..." ] [ filters="..." ] value="..." /&gt;
 * &lt;variable name="..."  [ match="..." ] [ replace="..." replacement="..." ] [ filters="..." ] /&gt;...&lt;variable&gt;
 * </pre>
 * All attributes are resolved via {@link edu.iu.uis.eden.test.web.framework.Util#getResolvableAttribute(Node, String, PropertyScheme)},
 * defaulting to literal scheme.<br/>
 * Explanation of attributes:
 * <dl>
 * <dt>match</dt>
 * <dd>Re-assigns the value of the variable to the groups found in a supplied regular expression. E.g. variable value: "coursenumber: 1234"; match attribute: ".*coursenumber: +([0-9]+).*"; the result would be a List of all course numbers in the variable value.</dd>
 * <dt>replace, replacement</dt>
 * <dd>These allow regular expression search/replace.  'replace' is the regular expression to match, and 'replacement' is the replacement text.  Implemented as: stringValue.replaceAll(replaceRegex, replacementString);</dd>
 * <dt>filters</dt>
 * <dd>Allows applying a sequence of preconfigured filters to the value.  The list of filter names is comma-delimited.  E.g. filters="DUPLICATE_SPACES, DUPLICATE_NEWLINES"</dd>
 * </dl>
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class VariableAction extends BaseScriptAction {
    private static final String[] NAMES = { "variable" };

    public String[] getNames() {
        return NAMES;
    }

    public void process(Script script, Node node) {
        String vname = Util.getAttribute(node, "name");
        if (vname == null) {
            String message = "'name' attribute must be specified for 'variable' element";
            log.error(message);
            throw new RuntimeException(message);
        }

        Property valueProp = Util.getResolvableAttribute(node, "value", PropertyScheme.LITERAL_SCHEME);
        Object value = null;
        if (valueProp != null) {
            value = script.getState().retrieveProperty(valueProp);
        } else {
            value = Util.getContent(node);
        }
        if (value == null) {
            log.warn("No value specified for variable '" + vname + "'");
        }

        Property matchProp = Util.getResolvableAttribute(node, "match", PropertyScheme.LITERAL_SCHEME);
        if (matchProp != null) {
            Object matchValue = script.getState().retrieveProperty(matchProp);
            if (matchValue == null) { 
                String message = "Could not load match property: " + matchProp;
                log.error(message);
                throw new RuntimeException(message);
            }
            if (!(matchValue instanceof String)) {
                String message = "match property is not a String: " + matchValue;
                log.error(message);
                throw new RuntimeException(message);
            }
            String stringValue = Util.getAsString(value, "Value");
            
            log.info("Assigning result of match \"" + matchValue + "\" groups on value to variable '" + vname + "'");
            Matcher matcher = Pattern.compile((String) matchValue).matcher(stringValue);
            List groupList = new ArrayList();
            // iterate over all matches and add all groups
            /*  TODO - look into Matcher.find()... could be problem
             *  EX:   Pattern   [-+]?[0-9]*\.?[0-9]+
             *        Value to Match = 123456.a34
             *        Matcher.matches() = false
             *        Matcher.find() = true
             *        Matcher.group(0) = 123456.
             *        next find() = 34
             */
            while (matcher.find()) {
                for (int i = 0; i < matcher.groupCount(); i++) {
                    groupList.add(matcher.group(i));
                }
            }
            script.getState().setVariable(vname, groupList);
        }

        Property replaceProp = Util.getResolvableAttribute(node, "replace", PropertyScheme.LITERAL_SCHEME);
        if (replaceProp != null) {
            Object replaceValue = script.getState().retrieveProperty(replaceProp);
            if (!(replaceValue instanceof String)) {
                String message = "replace property is not a String";
                log.error(message);
                throw new RuntimeException(message);
            }
            String replaceRegex = (String) replaceValue;

            Property replacementProp = Util.getResolvableAttribute(node, "replacement", PropertyScheme.LITERAL_SCHEME);
            if (replacementProp == null) {
                String message = "'replacement' attribute must also be specified when 'replace' attribute is specified on 'variable' element";
                log.error(message);
                throw new RuntimeException(message);
            }
            Object replacementValue = script.getState().retrieveProperty(replaceProp);
            if (!(replacementValue instanceof String)) {
                String message = "replacement property is not a String";
                log.error(message);
                throw new RuntimeException(message);
            }
            String replacementString = (String) replacementValue;
            String stringValue = Util.getAsString(value, "Value");
            log.info("Assigning result of replaceAll(\"" + replaceRegex + "\",\"" + replacementString + "\") on value to variable '" + vname + "'");
            value = stringValue.replaceAll(replaceRegex, replacementString);
        }

        Property filtersProp = Util.getResolvableAttribute(node, "filters", PropertyScheme.LITERAL_SCHEME);
        if (filtersProp != null) {
            Object filtersValue = script.getState().retrieveProperty(filtersProp);
            if (filtersValue == null) {
                String message = "Could not load filters property: " + filtersProp;
                log.error(message);
                throw new RuntimeException(message);
            }
            String filtersString = Util.getAsString(filtersValue, "filters value");
            Filter f = (Filter) script.getState().getFilters().get(filtersString);
            List filterNameList = new ArrayList();
            if (f != null) {
                filterNameList.add(filtersString);
            } else {
                String[] filterNames = filtersString.trim().split("\\s*,+\\s*");
                for (int i = 0; i < filterNames.length; i++) {
                    filterNameList.add(filterNames[i]);
                }
            }

            String result = Util.getAsString(value, "Value");
            Iterator it = filterNameList.iterator();
            while (it.hasNext()) {
                f = (Filter) script.getState().getFilters().get((String) it.next());
                if (f != null) {
                    result = f.filter(result);
                }
            }

            if (result.equals(value)) {
                log.warn("Filter did not modify value");
            }

            value = result;
        }

        log.info("Setting variable '" + vname + "' to " + (value == null ? "null" : "'" + value + "'"));
        script.getState().setVariable(vname, value);
    }
}