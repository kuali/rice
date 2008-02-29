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
package org.kuali.notification.dao;

import org.kuali.notification.test.NotificationTestCaseBase;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;

/**
 * Convenience test case implementation that just stores the BusinessObjectDao bean
 * in a protected member field for ease of use
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@BaselineMode(Mode.ROLLBACK)
public abstract class BusinessObjectDaoTestCaseBase extends NotificationTestCaseBase {
    protected BusinessObjectDao businessObjectDao;

    /**
     * @see org.kuali.rice.test.BaselineTestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        businessObjectDao = services.getBusinesObjectDao();
    }
}