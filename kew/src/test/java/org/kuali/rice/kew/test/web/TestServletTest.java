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

package org.kuali.rice.kew.test.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.kuali.rice.kew.test.web.framework.LocalInteractionController;
import org.kuali.rice.kew.test.web.framework.Script;

import junit.framework.TestCase;


/**
 * Tests the script framework against a trivial TestServlet. 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TestServletTest extends TestCase {
    public void test() throws Exception {
        // we aren't really going to use the context for anything...
        Map context = new HashMap();

        Servlet servlet = new TestServlet();
        Script script = new Script(getClass().getResourceAsStream("TestServletScript.xml"), new LocalInteractionController(servlet));

        script.run(context);
    }
}
