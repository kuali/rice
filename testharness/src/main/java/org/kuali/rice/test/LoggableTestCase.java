/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.test;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.kuali.rice.test.runners.NamedTestClassRunner;

/**
 * TestCase subclass that merely introduces a protected 'log' member for
 * subclass access
 * 
 * @author Aaron Hamid (arh14 at cornell dot edu)
 * @version $Revision: 1.3 $ $Date: 2007-08-15 15:49:48 $
 * @since 0.9
 */
@RunWith(NamedTestClassRunner.class)
public abstract class LoggableTestCase extends Assert {

    protected final Logger log = Logger.getLogger(getClass());

    private String name;

    public LoggableTestCase() {
        super();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}