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

import org.w3c.dom.Node;

import edu.iu.uis.eden.test.web.framework.Property;
import edu.iu.uis.eden.test.web.framework.PropertyScheme;
import edu.iu.uis.eden.test.web.framework.Script;
import edu.iu.uis.eden.test.web.framework.Util;

/**
 * ScriptAction that puts the thread to sleep for a specified
 * amount of seconds
 * <pre>
 * &lt;sleep seconds="..." /&gt;
 * </pre>
 * The 'seconds' attribute is resolved via {@link edu.iu.uis.eden.test.web.framework.Util#getResolvableAttribute(Node, String, PropertyScheme)},
 * defaulting to literal scheme.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SleepAction extends BaseScriptAction {
    private static final String[] NAMES = { "sleep" };

    public String[] getNames() {
        return NAMES;
    }

    public void process(Script script, Node node) {
        Property property = Util.getResolvableAttribute(node, "seconds", PropertyScheme.LITERAL_SCHEME);
        if (property == null) {
            String message = "'seconds' attribute must be specified for 'sleep' element";
            log.error(message);
            throw new RuntimeException(message);
        }
        Object o = script.getState().retrieveProperty(property);
        if (o == null) {
            String message = "Could not load seconds property: " + property;
            log.error(message);
            throw new RuntimeException(message);
        }
        int secs = Integer.parseInt(o.toString());
        log.info("Sleeping for " + secs + " seconds...");
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException ie) {
            log.error("Interrupted during sleep", ie);
        }
    }

    public String toString() {
        return "[SleepAction]";
    }
}