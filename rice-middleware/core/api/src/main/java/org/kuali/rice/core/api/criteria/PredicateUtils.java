/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.core.api.criteria;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.search.SearchOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.kuali.rice.core.api.criteria.PredicateFactory.and;
import static org.kuali.rice.core.api.criteria.PredicateFactory.between;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;
import static org.kuali.rice.core.api.criteria.PredicateFactory.equalIgnoreCase;
import static org.kuali.rice.core.api.criteria.PredicateFactory.greaterThan;
import static org.kuali.rice.core.api.criteria.PredicateFactory.greaterThanOrEqual;
import static org.kuali.rice.core.api.criteria.PredicateFactory.in;
import static org.kuali.rice.core.api.criteria.PredicateFactory.isNotNull;
import static org.kuali.rice.core.api.criteria.PredicateFactory.isNull;
import static org.kuali.rice.core.api.criteria.PredicateFactory.lessThan;
import static org.kuali.rice.core.api.criteria.PredicateFactory.lessThanOrEqual;
import static org.kuali.rice.core.api.criteria.PredicateFactory.likeIgnoreCase;
import static org.kuali.rice.core.api.criteria.PredicateFactory.notEqualIgnoreCase;
import static org.kuali.rice.core.api.criteria.PredicateFactory.notLikeIgnoreCase;
import static org.kuali.rice.core.api.criteria.PredicateFactory.or;

public final class PredicateUtils {

    private PredicateUtils() {
        throw new UnsupportedOperationException("do not call");
    }

    public static Predicate convertObjectMapToPredicate(Map<String, Object> criteria) {
        List<Predicate> p = new ArrayList<Predicate>();
        for (Map.Entry<String, Object> entry : criteria.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof String) {
                    p.add(equalIgnoreCase(entry.getKey(), (String)entry.getValue()));
                } else {
                    p.add(equal(entry.getKey(), entry.getValue()));
                }

            }
        }
        //wrap everything in an 'and'
        return and(p.toArray(new Predicate[p.size()]));
    }


    /*
     * Method to assist in converting a map of values for a lookup
     */
    public static Predicate convertMapToPredicate(Map<String, String> criteria) {
        List<Predicate> p = new ArrayList<Predicate>();
        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue())) {
                List<String> values = new ArrayList<String>();
                getValueRecursive(entry.getValue(), values);

                boolean isOr = entry.getValue().contains(SearchOperator.OR.op());
                boolean isAnd = entry.getValue().contains(SearchOperator.AND.op());

                if (isOr) {
                    // to be safe, we split our IN clause into groups of 1000 so Oracle doesn't choke
                    List<Predicate> inPredicates = new ArrayList<>();
                    List<String> inValues = new ArrayList<>();
                    for (String value : values) {
                        if (inValues.size() == 1000) {
                            inPredicates.add(in(entry.getKey(), inValues));
                            inValues = new ArrayList<>();
                        }
                        inValues.add(value);
                    }
                    inPredicates.add(in(entry.getKey(), inValues));
                    if (inPredicates.size() == 1) {
                        p.add(inPredicates.get(0));
                    } else {
                        p.add(or(inPredicates.toArray(new Predicate[inPredicates.size()])));
                    }
                } else {
                    List<Predicate> tempPredicates = new ArrayList<Predicate>();
                    for (String value : values) {
                        tempPredicates.add(parsePredicate(entry.getKey(), value));
                    }
                    if (isAnd) {
                        p.add(and(tempPredicates.toArray(new Predicate[tempPredicates.size()])));
                    } else {
                        p.addAll(tempPredicates);
                    }
                }
            }
        }
        //wrap everything in an 'and'
        return and(p.toArray(new Predicate[p.size()]));
    }

    /**
     * sort of parses a predicate out of a value
     * @param key the map entry key
     * @param value the expression value
     * @return a parsed predicate or null if unable to parse expression
     */
    private static Predicate parsePredicate(String key, String value) {
        if (value.contains(SearchOperator.NULL.op())) {
            if (isNot(value)) {
                return isNotNull(key);
            } else {
                return isNull(key);
            }
        } else if (value.contains(SearchOperator.BETWEEN_EXCLUSIVE_UPPER.op())) {
            String[] betweenVals = StringUtils.split(value, SearchOperator.BETWEEN_EXCLUSIVE_UPPER.op());
            if (betweenVals.length == 2) {
                return between(key, betweenVals[0], betweenVals[1], SearchOperator.BETWEEN_EXCLUSIVE_UPPER);
            } // else ?
            return null;
        } else if (value.contains(SearchOperator.BETWEEN.op())) {
            String[] betweenVals = StringUtils.split(value, SearchOperator.BETWEEN.op());
            if (betweenVals.length == 2) {
                return between(key, betweenVals[0], betweenVals[1]);
            } // else ?
            return null;
        } else if (value.contains(SearchOperator.GREATER_THAN_EQUAL.op())) {
            return greaterThanOrEqual(key, StringUtils.replace(value, SearchOperator.GREATER_THAN_EQUAL.op(), ""));
        } else if (value.contains(SearchOperator.LESS_THAN_EQUAL.op())) {
            return lessThanOrEqual(key, StringUtils.replace(value, SearchOperator.LESS_THAN_EQUAL.op(), ""));
        } else if (value.contains(SearchOperator.GREATER_THAN.op())) {
            return greaterThan(key, StringUtils.replace(value, SearchOperator.GREATER_THAN.op(), ""));
        } else if (value.contains(SearchOperator.LESS_THAN.op())) {
            return lessThan(key, StringUtils.replace(value, SearchOperator.LESS_THAN.op(), ""));
        } else if (value.contains(SearchOperator.LIKE_MANY.op()) || (value.contains(SearchOperator.LIKE_ONE.op()))) {
            if (isNot(value)) {
                return notLikeIgnoreCase(key, stripNot(value));
            } else {
                return likeIgnoreCase(key, value);
            }
        } else {
            if (isNot(value)) {
                return notEqualIgnoreCase(key, stripNot(value));
            } else {
                return equalIgnoreCase(key, value);
            }
        }
    }

    private static void getValueRecursive(String valueEntered, List<String> lRet) {
 		if(valueEntered == null) {
 			return;
 		}

 		valueEntered = valueEntered.trim();
        valueEntered = valueEntered.replaceAll("%", "*");
 		if(lRet == null){
 			throw new NullPointerException("The list passed in is by reference and should never be null.");
 		}

 		if (StringUtils.contains(valueEntered, SearchOperator.OR.op())) {
 			List<String> l = Arrays.asList(StringUtils.split(valueEntered, SearchOperator.OR.op()));
 			for(String value : l){
 				getValueRecursive(value, lRet);
 			}
 			return;
 		}
 		if (StringUtils.contains(valueEntered, SearchOperator.AND.op())) {
 			//splitValueList.addAll(Arrays.asList(StringUtils.split(valueEntered, KRADConstants.AND.op())));
 			List<String> l = Arrays.asList(StringUtils.split(valueEntered, SearchOperator.AND.op()));
 			for(String value : l){
 				getValueRecursive(value, lRet);
 			}
 			return;
 		}

 		// lRet is pass by ref and should NEVER be null
 		lRet.add(valueEntered);
    }

    private static boolean isNot(String value) {
        if (value == null) {
            return false;
        }
        return value.contains(SearchOperator.NOT.op());
    }

    // oh so hacky
    private static String stripNot(String value) {
        if (value.trim().startsWith(SearchOperator.NOT.op())) {
            value = value.trim().replaceFirst(SearchOperator.NOT.op(), "");
        }
        return value;
    }
}


