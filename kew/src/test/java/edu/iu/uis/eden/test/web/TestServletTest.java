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

package edu.iu.uis.eden.test.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;

import junit.framework.TestCase;

import edu.iu.uis.eden.test.web.framework.LocalInteractionController;
import edu.iu.uis.eden.test.web.framework.Script;

/**
 * Tests the script framework against a trivial TestServlet. 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
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