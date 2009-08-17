/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.test.web.framework.actions;

import org.kuali.rice.kew.test.web.framework.Script;
import org.kuali.rice.kew.test.web.framework.ScriptState;
import org.kuali.rice.kew.test.web.framework.Util;
import org.w3c.dom.Node;


/**
 * ScriptAction that sets or unsets the "USER" or "BACKDOORID" variable
 * as a convenience.
 * <pre>
 * &lt;user&gt;...&lt;/user&gt;
 * &lt;backdoorId&gt;...&lt;/backdoorId&gt;
 * </pre>
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UserAction extends BaseScriptAction {
    private static final String[] NAMES = { "user", "backdoorId" };

    public String[] getNames() {
        return NAMES;
    }

    public void process(Script script, Node node) {
        String key;
        if ("user".equals(node.getNodeName())) {
            key = ScriptState.USER;
        } else {
            key = ScriptState.BACKDOORID;
        }
        String user = Util.getContent(node);

        if (user == null) {
            log.info("Unset " + node.getNodeName());
            script.getState().setVariable(key, null);
        } else {
            log.info("Set " + node.getNodeName() + " to '" + user + "'");
            script.getState().setVariable(key, user);
        }
    }
}
