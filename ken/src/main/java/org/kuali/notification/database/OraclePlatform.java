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

import org.apache.log4j.Logger;

/**
 * Oracle implementation of KEN {@link Platform}
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class OraclePlatform implements Platform {
    private static final Logger LOG = Logger.getLogger(OraclePlatform.class);

    /**
     * @see org.kuali.notification.database.Platform#getSelectForUpdateSuffix(long)
     */
    public String getSelectForUpdateSuffix(long waitMillis) {
        String sql = "for update";
        if (WAIT_FOREVER == waitMillis) {
            // do nothing
            LOG.warn("Selecting for update and waiting forever...");
        } else if (NO_WAIT == waitMillis) {
            sql += " nowait";
        } else {
            // Oracle only supports wait time in seconds...
            long seconds = waitMillis / 1000;
            if (seconds == 0) seconds = 1;
            sql += " wait " + seconds;
        }
        return sql;
    }
}