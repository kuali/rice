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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.w3c.dom.Node;

import edu.iu.uis.eden.test.web.framework.Property;
import edu.iu.uis.eden.test.web.framework.PropertyScheme;
import edu.iu.uis.eden.test.web.framework.Script;
import edu.iu.uis.eden.test.web.framework.Util;

/**
 * ScriptAction that asserts an expected property value
 * <pre>
 * &lt;assert actual=".." ( regex="..." | expected="..." ) /&gt; 
 * &lt;assert actual=".."&gt;...&lt;/assert>
 * </pre>
 * The 'actual', 'regex' and 'expected' attributes are resolved via {@link edu.iu.uis.eden.test.web.framework.Util#getResolvableAttribute(Node, String, PropertyScheme)}.
 * The 'actual' attribute defautls to variable scheme.
 * The 'regex' and 'expected' attributes default to literal scheme.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AssertAction extends BaseScriptAction {
    private static final String[] NAMES = { "assert" };

    public String[] getNames() {
        return NAMES;
    }

    public void process(Script script, Node node) {
        Property actualProp = Util.getResolvableAttribute(node, "actual", PropertyScheme.VARIABLE_SCHEME); 
        if (actualProp == null) {
            String message = "'actual' attribute must be specified for 'assert' element";
            log.error(message);
            throw new RuntimeException(message);
        }

        Object actualValue = script.getState().retrieveProperty(actualProp);
        //LOG.debug("Actual value of '" + actualProp + "': " + actualValue);
        //LOG.debug("Value of CONTEXT.postProcessorListenerCallbacks: " + retreiveProperty(variables, "CONTEXT.postProcessorListenerCallbacks"));

        Property regexProp = Util.getResolvableAttribute(node, "regex", PropertyScheme.LITERAL_SCHEME);
        if (regexProp != null) {
            Object regexValue = script.getState().retrieveProperty(regexProp);
            if (regexValue == null) {
                String message = "Could not load regex property: " + regexProp;
                log.error(message);
                throw new RuntimeException(message);
            }
            log.info("Checking '" + actualValue + "' against regex: '" + regexValue + "'");
            Matcher matcher = Pattern.compile(regexValue.toString()).matcher((CharSequence) actualValue);
            Assert.assertTrue("Value does not match regex '" + regexValue + "'", matcher.matches());
            return;
        }

        Property expectedProp = Util.getResolvableAttribute(node, "expected", PropertyScheme.LITERAL_SCHEME);
        Object expectedValue = null;
        if (expectedProp == null) {
            expectedValue = Util.getContent(node);
            if (expectedValue == null) {
                log.warn("Neither expected property nor content was specified");
            }
        } else {
            expectedValue = script.getState().retrieveProperty(expectedProp);
        }

        log.debug("Checking expected " + expectedProp + " '" + expectedValue + "' against " + actualProp + " '" + actualValue + "'");
        if (expectedValue instanceof String && actualValue instanceof String) {
            // do this so JUnit can display diff pane
            Assert.assertEquals((String) expectedValue, (String) actualValue);
        } else {
            Assert.assertEquals(expectedValue, actualValue);
        }
    }
}