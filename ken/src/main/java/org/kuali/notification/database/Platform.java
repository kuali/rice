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
package org.kuali.notification.database;

/**
 * Database platform abstraction for KEN... need to sync all these and put in shared :( 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface Platform {
    public static final long WAIT_FOREVER = -1;
    public static final long NO_WAIT = 0;

    /**
     * Returns the suffix to append to a SQL query in order to perform
     * a "select for update" lock on the table
     * 
     * @param waitMillis the milliseconds to wait, -1 forever, 0 if no wait
     * @return the suffix to append to a SQL query in order to perform a "select for update" lock on the table
     */
    public String getSelectForUpdateSuffix(long waitMillis);
}
