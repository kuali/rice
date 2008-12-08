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
package org.kuali.rice.kew.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.lang.text.StrSubstitutor;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.applicationconstants.ApplicationConstant;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * Various static utility methods.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Utilities {
    /**
     * Commons-Lang StrSubstitor which substitutes variables specified like ${name} in strings,
     * using a lookup implementation that pulls variables from the core config
     */
    private static StrSubstitutor substitutor = new StrSubstitutor(new ConfigStringLookup());

    /**
     * Performs variable substitution on the specified string, replacing variables specified like ${name}
     * with the value of the corresponding config parameter obtained from the current context Config object
     * @param string the string on which to perform variable substitution
     * @return a string with any variables substituted with configuration parameter values
     */
    public static String substituteConfigParameters(String string) {
        return substitutor.replace(string);
    }

    public static String getApplicationConstant(String name) {
    	ApplicationConstant constant = KEWServiceLocator.getApplicationConstantsService().findByName(name);
    	if (constant == null) {
    		return null;
    	}
    	return constant.getApplicationConstantValue();
    }

    public static boolean getBooleanConstant(String name, boolean defaultValue) {
    	String value = getApplicationConstant(name);
    	if (value == null) {
    		return defaultValue;
    	}
    	return Boolean.valueOf(value);
    }

    public static String getKNSParameterValue(String nameSpace, String detailType, String name) {
        Parameter parameter = KNSServiceLocator.getKualiConfigurationService().getParameterWithoutExceptions(nameSpace, detailType, name);
        if (parameter == null) {
            return null;
        }
        return parameter.getParameterValue();
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().equals("");
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean equals (Object a, Object b) {
        return ((a == null && b == null) || (a != null && a.equals(b)));
    }

    public static String collectStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    public static Calendar convertTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        return calendar;
    }

    public static Timestamp convertCalendar(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return new Timestamp(calendar.getTime().getTime());
    }

    public static Set asSet(Object[] objects) {
        Set set = new HashSet();
        if (objects != null) {
            for (int index = 0; index < objects.length; index++) {
                Object object = objects[index];
                set.add(object);
            }
        }
        return set;
    }

    public static Set asSet(Object object) {
        Set set = new HashSet();
        if (object != null) {
            set.add(object);
        }
        return set;
    }

    public static List asList(Object object) {
        List<Object> list = new ArrayList<Object>(1);
        if (object != null) {
            list.add(object);
        }
        return list;
    }

    /**
     *
     *	Consider moving out of this class if this bugs
     */
    public class PrioritySorter implements Comparator {
        public int compare(Object arg0, Object arg1) {
            ActionRequestValue ar1 = (ActionRequestValue) arg0;
            ActionRequestValue ar2 = (ActionRequestValue) arg1;
            int value = ar1.getPriority().compareTo(ar2.getPriority());
            if (value == 0) {
                value = ActionRequestValue.compareActionCode(ar1.getActionRequested(), ar2.getActionRequested());
                if (value == 0) {
                    if ( (ar1.getActionRequestId() != null) && (ar2.getActionRequestId() != null) ) {
                        value = ar1.getActionRequestId().compareTo(ar2.getActionRequestId());
                    } else {
                        // if even one action request id is null at this point return that the two are equal
                        value = 0;
                    }
                }
            }
            return value;
        }
    }

    /**
     *
     *	Consider moving out of this class if this bugs
     */
    public class RouteLogActionRequestSorter implements Comparator {
        public int compare(Object arg0, Object arg1) {
            ActionRequestValue ar1 = (ActionRequestValue) arg0;
            ActionRequestValue ar2 = (ActionRequestValue) arg1;

            if (! ar1.getChildrenRequests().isEmpty()) {
                Collections.sort(ar1.getChildrenRequests(), new RouteLogActionRequestSorter());
            }
            if (! ar2.getChildrenRequests().isEmpty()) {
                Collections.sort(ar2.getChildrenRequests(), new RouteLogActionRequestSorter());
            }

            int routeLevelCompareVal = ar1.getRouteLevel().compareTo(ar2.getRouteLevel());
            if (routeLevelCompareVal != 0) {
                return routeLevelCompareVal;
            }

            if (ar1.isActive() && ar2.isPending()) {
                return -1;
            } else if (ar2.isActive() && ar1.isPending()) {
                return 1;
            }

            return new Utilities().new PrioritySorter().compare(arg0, arg1);
        }
    }

    public static boolean validateDate(String date, boolean dateOptional) {
        if ((date == null) || date.trim().equals("")) {
            if (dateOptional) {
                return true;
            } else {
                return false;
            }
        }

        try {
            Date parsedDate = RiceConstants.getDefaultDateFormat().parse(date.trim());
            if (! RiceConstants.getDefaultDateFormat().format(parsedDate).equals(date)) return false;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            int yearInt = calendar.get(Calendar.YEAR);
            if (yearInt <= 0 || yearInt > 2999) {
              return false;
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean checkDateRanges(String fromDate, String toDate) {
        try {
            Date parsedDate = RiceConstants.getDefaultDateFormat().parse(fromDate.trim());
            Calendar fromCalendar = Calendar.getInstance();
            fromCalendar.setTime(parsedDate);
            fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
            fromCalendar.set(Calendar.MINUTE, 0);
            fromCalendar.set(Calendar.SECOND, 0);
            fromCalendar.set(Calendar.MILLISECOND, 0);
            parsedDate = RiceConstants.getDefaultDateFormat().parse(toDate.trim());
            Calendar toCalendar = Calendar.getInstance();
            toCalendar.setTime(parsedDate);
            toCalendar.set(Calendar.HOUR_OF_DAY, 0);
            toCalendar.set(Calendar.MINUTE, 0);
            toCalendar.set(Calendar.SECOND, 0);
            toCalendar.set(Calendar.MILLISECOND, 0);
            if (fromCalendar.after(toCalendar)) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Performs a "brute force" comparison of collections by testing whether the collections contain each other.
     * This circuments any particular uniqueness or ordering constraints on the collections
     * (for instance, lists that are unordered but contain the same elements, where a hashset would not suffice
     * for comparison purposes because it enforces element uniqueness)
     */
    public static boolean collectionsEquivalent(Collection a, Collection b) {
        if (a == null && b == null) return true;
        if (a == null ^ b == null) return false;
        return a.containsAll(b) && b.containsAll(a);
    }

    public static String getIpNumber() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new WorkflowRuntimeException("Error retrieving ip number.", e);
        }
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new WorkflowRuntimeException("Error retrieving host name.", e);
        }
    }

    /**
     * Helper method that takes a List of {@link KeyValue} and presents it as a Map
     * @param <T> the key type
     * @param <Z> the value type
     * @param collection collection of {@link KeyValue}
     * @return a Map<T,Z> representing the keys and values in the KeyValue collection
     */
    public static <T,Z> Map<T, Z> getKeyValueCollectionAsMap(List<? extends KeyValue> collection) {
        Map<T, Z> map = new HashMap<T, Z>(collection.size());
        for (KeyValue kv: collection) {
            map.put((T) kv.getKey(), (Z) kv.getValue());
        }
        return map;
    }

    /**
     * Helper method that takes a List of {@link KeyValue} and presents it as a Map containing
     * KeyValue values
     * @param <T> the key type
     * @param <Z> the collection/map value type, which must be a KeyValue
     * @param collection collection of {@link KeyValue}
     * @return a Map<T,Z> where keys of the KeyValues in the collection are mapped to their respective KeyValue object
     */
    public static <T,Z  extends KeyValue> Map<T, Z> getKeyValueCollectionAsLookupTable(List<Z> collection) {
        Map<T, Z> map = new HashMap<T, Z>(collection.size());
        for (Z kv: collection) {
            map.put((T) kv.getKey(), kv);
        }
        return map;
    }
}