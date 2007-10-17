/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.database.platform;

import org.apache.ojb.broker.PersistenceBroker;

/**
 * Interface that abstracts database dependent sql from core
 *
 * TODO Had to move this down into embedded source because of the OJB dependencies.  This probably will
 * go away once we get rid of the embedded plugin.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface Platform {

    /**
     * Supplies a parameterized sequence incrementation query
     * @param sequenceName name of the sequence to be incremented
     * @return paramaterized sequence incrementation query
     */
    Long getNextValSQL(String sequenceName, PersistenceBroker persistenceBroker);
    /**
     * Generates the query used to select route header rows for update
     * @param routeHeaderId id of the routeHeader to select for update
     * @param wait whether to block until lock is released
     * @return the query used to select route header rows for update
     */
    String getLockRouteHeaderQuerySQL(Long routeHeaderId, boolean wait);
    /**
     * Supplies the sql for a given date string that will satisfy a where clause
     * @param date in YYYY/MM/DD format
     * @param time in hh:mm:ss format
     * @return the sql for a given date string that will satisfy a where clause
     * @see SqlUtil#establishDateString(String, String, String, StringBuffer, Platform)
     * @see SqlUtil#formatDate(String)
     * TODO: refactor to use a parsed Date object or milliseconds instead of date String
     */
    String getDateSQL(String date, String time);

}