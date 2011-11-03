/**
 * Copyright 2005-2011 The Kuali Foundation
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

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

public final class PredicateUtils {

    private PredicateUtils() {
        throw new UnsupportedOperationException("do not call");
    }

    public static Predicate convertMapToPredicate(Map<String, String> criteria) {
        List<Predicate> p = new ArrayList<Predicate>();
        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue())) {
                List<String> values = new ArrayList<String>();
                getValueRecursive(entry.getValue(), values);
                List<Predicate> tempPredicates = new ArrayList<Predicate>();
                p.addAll(tempPredicates);

                // TODO: how to handle different types of data when everything comes in as string....
                for (String value : values) {
                    if (value.contains(SearchOperator.NULL.op())) {
                        if (isNot(value)) {
                            tempPredicates.add(isNotNull(entry.getKey()));
                        } else {
                            tempPredicates.add(isNull(entry.getKey()));
                        }
                    } else if (value.contains(SearchOperator.BETWEEN_EXCLUSIVE_UPPER.op())) {
                        String[] betweenVals = StringUtils.split(value, SearchOperator.BETWEEN_EXCLUSIVE_UPPER.op());
                        if (betweenVals.length == 2) {
                            tempPredicates.add(and(greaterThanOrEqual(entry.getKey(), betweenVals[0]),
                                                   lessThan(entry.getKey(), betweenVals[1])));
                        }
                    } else if (value.contains(SearchOperator.BETWEEN.op())) {
                        String[] betweenVals = StringUtils.split(value, SearchOperator.BETWEEN.op());
                        if (betweenVals.length == 2) {
                            tempPredicates.add(and(greaterThanOrEqual(entry.getKey(), betweenVals[0]),
                                                   lessThanOrEqual(entry.getKey(), betweenVals[1])));
                        }
                    } else if (value.contains(SearchOperator.GREATER_THAN_EQUAL.op())) {
                        tempPredicates.add(greaterThanOrEqual(entry.getKey(), StringUtils.replace(value, SearchOperator.GREATER_THAN_EQUAL.op(), "")));
                    } else if (value.contains(SearchOperator.LESS_THAN_EQUAL.op())) {
                        tempPredicates.add(lessThanOrEqual(entry.getKey(), StringUtils.replace(value, SearchOperator.LESS_THAN_EQUAL.op(), "")));
                    } else if (value.contains(SearchOperator.GREATER_THAN.op())) {
                        tempPredicates.add(greaterThan(entry.getKey(), StringUtils.replace(value, SearchOperator.GREATER_THAN.op(), "")));
                    } else if (value.contains(SearchOperator.LESS_THAN.op())) {
                        tempPredicates.add(lessThan(entry.getKey(), StringUtils.replace(value, SearchOperator.LESS_THAN.op(), "")));

                    } else if (value.contains(SearchOperator.NOT.op())) {
                        String[] notValues = StringUtils.split(value, SearchOperator.NOT.op());
                        List<Predicate> notPreds = new ArrayList<Predicate>(notValues.length);
                        for (String notValue : notValues) {
                            notPreds.add(notEqual(entry.getKey(), StringUtils.replace(notValue, SearchOperator.NOT.op(), "")));
                        }
                        tempPredicates.add(and(notPreds.toArray(new Predicate[notPreds.size()])));
                    } else if (value.contains(SearchOperator.LIKE_MANY.op())
                                || (value.contains(SearchOperator.LIKE_ONE.op()))) {
                        if (isNot(value)) {
                            tempPredicates.add(notLike(entry.getKey(), value ));
                        } else {
                            tempPredicates.add(like(entry.getKey(), value ));
                        }
                    } else {
                        if (isNot(value)) {
                            tempPredicates.add(notEqual(entry.getKey(), value));
                        } else {
                            tempPredicates.add(equal(entry.getKey(), value));
                        }
                    }
                }
                if (entry.getValue().contains(SearchOperator.AND.op())) {
                    p.add(and(tempPredicates.toArray(new Predicate[tempPredicates.size()])));
                } else if (entry.getValue().contains(SearchOperator.OR.op())) {
                    p.add(or(tempPredicates.toArray(new Predicate[tempPredicates.size()])));
                } else {
                    p.addAll(tempPredicates);
                }
            }
        }
        //wrap everything in an 'and'
        return and(p.toArray(new Predicate[p.size()]));
    }

    private static void getValueRecursive(String valueEntered, List<String> lRet) {
 		if(valueEntered == null) {
 			return;
 		}

 		valueEntered = valueEntered.trim();

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
}


