/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.core.api.mo.active;

import java.sql.Timestamp;


public interface InactivatableFromTo {
	/**
	 * Gets the date for which the record become active
	 *
	 * @return Timestamp of active from date
	 */
	public Timestamp getActiveFromDate();

	/**
	 * Gets the date for which the record become inactive
	 *
	 * @return Timestamp of active to date
	 */
	public Timestamp getActiveToDate();
}
