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
package edu.iu.uis.eden.database.platform;

import org.apache.ojb.broker.PersistenceBroker;

/**
 * Platform implementation that generates Mckoi-compliant SQL
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DerbyPlatform extends ANSISqlPlatform {

    public String getLockRouteHeaderQuerySQL(Long routeHeaderId, boolean wait) {
        return "SELECT DOC_HDR_ID FROM EN_DOC_HDR_T WHERE DOC_HDR_ID=?";
    }

    private static long nextVal = 1000;
    
    public Long getNextValSQL(String sequenceName,	PersistenceBroker persistenceBroker) {
		return nextVal++;
	}

    public String toString() {
        return "[Derby]";
    }
}