/*
 * Copyright 2006-2012 The Kuali Foundation
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

package edu.samplu.travel.krad.test;

import com.thoughtworks.selenium.DefaultSelenium;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;


/**
 * abstract class for building other selenium tests
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class AbstractSeleniumIT {

    private DefaultSelenium selenium;

    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*chrome", System.getProperty("remote.public.url"));
        selenium.start();
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
