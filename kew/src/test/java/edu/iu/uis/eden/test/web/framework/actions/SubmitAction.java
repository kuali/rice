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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import edu.iu.uis.eden.test.web.framework.Filter;
import edu.iu.uis.eden.test.web.framework.Property;
import edu.iu.uis.eden.test.web.framework.PropertyScheme;
import edu.iu.uis.eden.test.web.framework.Script;
import edu.iu.uis.eden.test.web.framework.ScriptState;
import edu.iu.uis.eden.test.web.framework.Util;

/**
 * ScriptAction that submits an HTTP request with parameters in the current RESPONSE map,
 * and populates the RESPONSE variable map with any variables defined in meta-data
 * syntax in the output.
 * <pre>
 * &lt;submit [ method="..." ] [ uri="..." ]/&gt; 
 * </pre>
 * The 'uri' attribute is resolved via {@link edu.iu.uis.eden.test.web.framework.Util#getResolvableAttribute(Node, String, PropertyScheme)},
 * defaulting to literal scheme.<br/>
 * <br/>
 * The meta-data syntax is:<br/>
 * <br/>
 * <code>(?i)\\[var ([\\p{Alnum}_\\.\\[\\]]+)=(.*)\\]</code><br/>
 * <br/>
 * e.g.<br/>
 * <pre>
 * &lt;!--
 *   let's define some variables!
 *   [var color=green]
 *   [var shape=square]
 *   ok, 'color' and 'shape' will now be present in the RESPONSE map once the response is parsed!
 * --&gt;
 * </pre>
 * There are also some "special" variables defined:<br/>
 * <dl>
 * <dt>OUTPUT</dt>
 * <dd>The literal response output before any filtering</dd>
 * <dt>FILTERED_OUTPUT</dt>
 * <dd>The response output after filters have been applied</dd>
 * </dl>
 * The only filter applied by default at this time is the CANONICALIZE filter.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SubmitAction extends BaseScriptAction {
    public static final String RESPONSE_OUTPUT = "OUTPUT";
    public static final String RESPONSE_FILTERED_OUTPUT = "FILTERED_OUTPUT";

    private static final String[] NAMES = { "submit" };
    private static final Pattern RESPONSE_VAR_PATTERN;
    static {
        RESPONSE_VAR_PATTERN = Pattern.compile("(?i)\\[var ([\\p{Alnum}_\\.\\[\\]]+)=(.*)\\]");
    }

    public String[] getNames() {
        return NAMES;
    }

    public void process(Script script, Node node) {
        String method = Util.getAttribute(node, "method");
        if (method == null) {
            method = "GET";
        }

        Property uriProp = Util.getResolvableAttribute(node, "uri", PropertyScheme.LITERAL_SCHEME);
        String uri = null;
        if (uriProp != null) {
            Object uriValue = script.getState().retrieveProperty(uriProp);
            if (uriValue == null) {
                String message = "Could not load uri property: " + uriProp;
                log.error(message);
                throw new RuntimeException(message);
            }
            uri = Util.getAsString(uriValue, "uri");
        }

        String user = script.getState().getUser();
        log.info("Submitting request: user=" + user + ", method=" + method + ", uri=" + uri);
        String output;
        try {
          output = script.getController().submit(method, uri, script);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking controller", e);
        }

        Map response = script.getState().getResponse();
        response.clear();
        response.put(RESPONSE_OUTPUT, output);

        Matcher matcher = RESPONSE_VAR_PATTERN.matcher(output);
        /*  TODO - look into Matcher.find()... could be problem
         *  EX:   Pattern   [-+]?[0-9]*\.?[0-9]+
         *        Value to Match = 123456.a34
         *        Matcher.matches() = false
         *        Matcher.find() = true
         *        Matcher.group(0) = 123456.
         *        next find() = 34
         */
        while (matcher.find()) {
            String name = matcher.group(1);
            String value = matcher.group(2);
            log.info("Extracted variable from response: " + name + "=" + value);
            response.put(name, value);
        }

        //log.info("Submit response: " + output);
        Filter filter = (Filter) script.getState().getFilters().get(ScriptState.CANONICALIZE_FILTER_NAME);
        if (filter == null) {
            log.warn(ScriptState.CANONICALIZE_FILTER_NAME + " filter not found");
        } else {
            output = ScriptState.CANONICALIZE_FILTER.filter(output);
        }
        response.put(RESPONSE_FILTERED_OUTPUT, output);
    }
}