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
        return
                and(
                    or(isNull(startField), greaterThanOrEqual(startField, asOfDate.toDate())),
                    or(isNull(endField), lessThan(endField, asOfDate.toDate()))
                );
    }
}
