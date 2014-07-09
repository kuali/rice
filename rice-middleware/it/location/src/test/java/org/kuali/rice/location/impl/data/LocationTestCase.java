/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.location.impl.data;

import org.kuali.rice.krad.test.KRADTestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Default test base for Location unit tests.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class LocationTestCase extends KRADTestCase {

    /**
     * Returns the List of tables that should be cleared on every test run.
     */
    protected List<String> getPerTestTablesToClear() {
        List<String> tablesToClear = new ArrayList<String>();
        tablesToClear.add("KRLC_.*");
        return tablesToClear;
    }
}
