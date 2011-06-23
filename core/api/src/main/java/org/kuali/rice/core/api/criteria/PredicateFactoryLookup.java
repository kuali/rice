package org.kuali.rice.core.api.criteria;

import org.kuali.rice.core.api.search.SearchOperator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

/**
 * Contains methods used in the predicate factory related to the lookup framework.
 * ***************************************************************************************************************
 * FIXME: issues to talk to the group about.
 * http://kuali.org/rice/documentation/1.0.3/UG_Global/Documents/lookupwildcards.htm
 *
 * 1) Should we support isNotNull, isNull, as wildcards?  Then do we still translate
 * null values into isNull predicates.  I believe the lookup framework right now
 * barfs on null values but that is a guess.
 *
 * 2) We need to support case insensitivity in the old lookup framework.  Right now the lookup
 * framework looks at Data dictionary entries.  This can still be configured in the DD
 * but should be placed in the lookup criteria. We could have a "flag" section on a
 * lookup sequence like foo.bar=(?i)ba*|bing
 *
 * This would translate to
 *
 * or(like("foo.bar", "ba*"), equalsIgnoreCase("foo.bar", "bing"))
 *
 * Btw.  My flag format was stolen from regex but we could use anything really.
 * http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#CASE_INSENSITIVE
 *
 * 3) In the above example, I used a case insensitive flag but Like doesn't support case
 * insensitive.  Should it?
 *
 * 4) Do we need to support escaping in the lookup framework & criteria api.  Right now the
 * lookup framework looks at Data dictionary entries.  This can still be configured in the DD
 * but should be placed in the lookup criteria.  Escaping is tricky and I worry if we support
 * it in the criteria api then it will make the criteria service much harder to make custom
 * implementations.  To me it seems it's better to make escaping behavior undefined.
 *
 * If we do support an escape character then we should probably also support a flag to treat
 * escape chars as literal like (?i) - is that right? need to confirm what flag get append
 * to the regex
 *
 * http://download.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#LITERAL
 *
 * 5) Maybe we should just support what is in the org.kuali.rice.core.framework.logic.LogicalOperator class
 * which btw contains more than just logical operators - This should be split into multiple enums.
 * ?
 * 6) Maybe the predicate class could have a toLookupString, toLookupMap() methods on them to translate
 * to various formats of criteria?  Or maybe this factory method & related methods should get placed into
 * krad or somewhere else?
 * ***************************************************************************************************************
 */
class PredicateFactoryLookup {

    private PredicateFactoryLookup() {
        throw new IllegalArgumentException("do not call");
    }

    static Predicate fromCriteriaMap(Map<String, ?> criteria) {
        if (criteria == null || criteria.isEmpty()) {
            throw new IllegalArgumentException("criteria is null or empty");
        }

        final List<Predicate> toAnd = new ArrayList<Predicate>();
        for (Map.Entry<String, ?> entry : criteria.entrySet()) {
            final String key = entry.getKey();
            if (key == null) {
                throw new IllegalArgumentException("criteria contains a null key");
            }
            toAnd.add(createPredicate(key, entry.getValue()));
        }

        return and(toAnd.toArray(new Predicate[] {}));
    }

    private static Predicate createPredicate(String key, Object value) {
        //null case
        if (value == null) {
            return isNull(key);
        } else if (value instanceof String) {
            if (containsOperator((String) value)) {
                //handle operator parsing
            } else {
               return equal(key, value);
            }
        } else if (value instanceof Collection) {
            //recurs
        } else {
            throw new IllegalArgumentException("criteria map contained a value that was non supported :" + value.getClass().getName());
        }
        return null;
    }

    //does not handle escaping, assumes non-null
    private static boolean containsOperator(String value) {
        for (SearchOperator o : SearchOperator.values()) {
            if (value.contains(o.op())) {
                return true;
            }
        }
        return false;
    }
}
