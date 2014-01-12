/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.core.api.mo.common.active;

import org.joda.time.DateTime;

public final class InactivatableFromToUtils {

    private InactivatableFromToUtils() {
        throw new UnsupportedOperationException("do not call");
    }

    public static boolean isActive(DateTime activeFromDate, DateTime activeToDate, DateTime activeAsOfDate) {
        if (activeAsOfDate == null) {
            activeAsOfDate = DateTime.now();
        }
        return computeActive(activeFromDate, activeToDate, activeAsOfDate);
    }

    private static boolean computeActive(DateTime activeFromDate, DateTime activeToDate, DateTime activeAsOfDate) {
        // the precision of this check should be to the second, not milliseconds, so we want to chop off any
        // milliseconds and do a ceiling of our seconds. Sometimes changes are made in near real time after a record
        // becomes activated or inactivated so we want to have the best result possible if they are still within the
        // same second, so we essentially always round up to ensure that this check will never fail in high throughput
        // environments
        activeAsOfDate = activeAsOfDate.secondOfDay().roundCeilingCopy();
        return (activeFromDate == null || activeAsOfDate.getMillis() >= activeFromDate.getMillis()) &&
                (activeToDate == null || activeAsOfDate.getMillis() < activeToDate.getMillis());
    }

}
