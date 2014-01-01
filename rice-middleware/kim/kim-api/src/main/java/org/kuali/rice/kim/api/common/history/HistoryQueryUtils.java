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
package org.kuali.rice.kim.api.common.history;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.criteria.OrderByField;
import org.kuali.rice.core.api.criteria.OrderDirection;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

public final class HistoryQueryUtils {

    private HistoryQueryUtils() {
        throw new UnsupportedOperationException();
    }

    public static QueryByCriteria historyQuery(String id, DateTime asOfDate) {
        Timestamp asOfTimestamp = new Timestamp(asOfDate.getMillis());
        Predicate predicate =
            and(
                equal("id", id),
                and(
                    or(isNull("activeFromDateValue"), lessThanOrEqual("activeFromDateValue", asOfTimestamp)),
                    or(isNull("activeToDateValue"), greaterThan("activeToDateValue", asOfTimestamp))
                )
            );

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setMaxResults(Integer.valueOf(1));
        criteria.setPredicates(predicate);
        List<OrderByField> orderByFields = new ArrayList<OrderByField>();
        orderByFields.add(OrderByField.Builder.create("activeFromDateValue", OrderDirection.DESCENDING).build());
        orderByFields.add(OrderByField.Builder.create("historyId", OrderDirection.DESCENDING).build());
        criteria.setOrderByFields(orderByFields);

        return criteria.build();
    }

    public static QueryByCriteria futureRecordQuery(String id, DateTime asOfDate) {
        Timestamp asOfTimestamp = new Timestamp(asOfDate.getMillis());
        Predicate predicate =
                and(
                    equal("id", id),
                    or(isNull("activeFromDateValue"), greaterThan("activeFromDateValue", asOfTimestamp))
                );

        QueryByCriteria.Builder criteria = QueryByCriteria.Builder.create();
        criteria.setMaxResults(Integer.valueOf(1));
        criteria.setPredicates(predicate);
        List<OrderByField> orderByFields = new ArrayList<OrderByField>();
        orderByFields.add(OrderByField.Builder.create("activeFromDateValue", OrderDirection.DESCENDING).build());
        orderByFields.add(OrderByField.Builder.create("historyId", OrderDirection.DESCENDING).build());
        criteria.setOrderByFields(orderByFields);

        return criteria.build();
    }

    public static Predicate between(String startField,String endField, DateTime asOfDate) {
        // the precision of this check should be to the second, not milliseconds, so we want to chop off any
        // milliseconds and do a ceiling of our seconds. Sometimes changes are made in near real time after a record
        // becomes activated or inactivated so we want to have the best result possible if they are still within the
        // same second, so we essentially always round up to ensure that this check will never fail in high throughput
        // environments
        asOfDate = asOfDate.secondOfDay().roundCeilingCopy();
        return
                and(
                    or(isNull(startField), lessThanOrEqual(startField, asOfDate.toDate())),
                    or(isNull(endField), greaterThan(endField, asOfDate.toDate()))
                );
    }
}
