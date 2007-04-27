/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.Constants;
import org.kuali.core.bo.FinancialSystemParameter;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.cache.MethodCacheInterceptor;
import org.kuali.core.util.properties.PropertyTree;
import org.kuali.rice.KNSServiceLocator;


/**
 * This class provides utility methods for use during manual testing.
 */

public class TestUtils {
    private static final Log LOG = LogFactory.getLog(KualiTestBase.class);


//    /**
//     * Disables all scheduled tasks, to make debugging easier.
//     */
//    public static void disableScheduledTasks() {
//        Timer timer = KNSServiceLocator.getTaskTimer();
//        timer.cancel();
//    }


    /**
     * Iterates through the given Collection, printing toString of each item in the collection to stderr
     * 
     * @param collection
     */
    public static void dumpCollection(Collection collection) {
        dumpCollection(collection, new ItemStringFormatter());
    }

    /**
     * Iterates through the given Collection, printing f.format() of each item in the collection to stderr
     * 
     * @param collection
     * @param formatter ItemFormatter used to format each item for printing
     */
    public static void dumpCollection(Collection collection, ItemFormatter formatter) {
        LOG.error(formatCollection(collection, formatter));
    }

    /**
     * Suitable for attaching as a detailFormatter in Eclipse
     * 
     * @param collection
     * @param formatter
     * @return String composed of contents of the given Collection, one per line, formatted by the given ItemFormatter
     */
    public static String formatCollection(Collection collection, ItemFormatter formatter) {
        StringBuffer formatted = new StringBuffer("size= ");
        formatted.append(collection.size());

        for (Iterator i = collection.iterator(); i.hasNext();) {
            formatted.append(formatter.format(i.next()));
            formatted.append("\n");
        }

        return formatted.toString();
    }


    /**
     * Iterates through the entries of the given Map, printing toString of each (key,value) to stderr
     * 
     * @param map
     */
    public static void dumpMap(Map map) {
        dumpMap(map, new EntryStringFormatter());
    }

    /**
     * Iterates through the entries of the given Map, printing formatter.format() of each Map.Entry to stderr
     * 
     * @param map
     * @param formatter
     */
    public static void dumpMap(Map map, EntryFormatter formatter) {
        LOG.error(formatMap(map, formatter));
    }

    /**
     * Suitable for attaching as a detailFormatter in Eclipse
     * 
     * @param m
     * @return String composed of contents of the given Map, one entry per line, formatted by the given EntryFormatter
     */
    public static String formatMap(Map map, EntryFormatter formatter) {
        StringBuffer formatted = new StringBuffer("size= ");
        formatted.append(map.size());

        for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
            formatted.append(formatter.format((Map.Entry) i.next()));
            formatted.append("\n");
        }

        return formatted.toString();
    }


    /**
     * Recursively prints the contents of the given PropertyTree to stderr
     * 
     * @param tree
     */
    public static void dumpTree(PropertyTree tree) {
        LOG.error(formatTree(tree));
    }

    /**
     * Suitable for attaching as a detailFormatter in Eclipse
     * 
     * @param tree
     * @return String composed of the contents of the given PropertyTree, one entry per line
     */
    public static String formatTree(PropertyTree tree) {
        StringBuffer formatted = new StringBuffer("total size= " + tree.size());

        formatted.append(formatLevel(tree, 0));

        return formatted.toString();
    }

    private static String formatLevel(PropertyTree tree, int level) {
        StringBuffer formatted = new StringBuffer();

        String prefix = buildIndent(level) + ": ";

        Map children = tree.getDirectChildren();
        for (Iterator i = children.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            formatted.append(prefix);

            String key = (String) e.getKey();
            PropertyTree subtree = (PropertyTree) e.getValue();
            String directValue = subtree.toString();
            if (directValue == null) {
                formatted.append(key);
            }
            else {
                formatted.append("(");
                formatted.append(key);
                formatted.append("=");
                formatted.append(directValue);
                formatted.append(")");
            }
            formatted.append("\n");

            formatted.append(formatLevel(subtree, level + 1));
        }

        return formatted.toString();
    }


    private static String buildIndent(int level) {
        int indentSize = level * 4;
        char[] indent = new char[indentSize];
        for (int i = 0; i < indentSize; ++i) {
            indent[i] = ' ';
        }

        return new String(indent);
    }


    public interface ItemFormatter {
        public String format(Object o);
    }

    public interface EntryFormatter {
        public String format(Map.Entry e);
    }


    private static class ItemStringFormatter implements ItemFormatter {
        public String format(Object o) {
            String result = "<null>";

            if (o != null) {
                result = o.toString();
            }

            return result;
        }
    }

    private static class EntryStringFormatter implements EntryFormatter {
        public String format(Map.Entry e) {
            String key = "<null>";
            String value = "<null>";

            if (e != null) {
                if (e.getKey() != null) {
                    key = e.getKey().toString();
                }
                if (e.getValue() != null) {
                    value = e.getValue().toString();
                }
            }

            return "(" + key + "," + value + ")";
        }
    }


    /**
     * Given a list of classnames of TestCase subclasses, assembles a TestSuite containing all tests within those classes, then runs
     * those tests and logs the results.
     * <p>
     * Created this method so that I could use OptimizeIt, which was asking for a main() method to run.
     * 
     * @param args
     */
    public static void main(String args[]) {
        TestSuite tests = new TestSuite();
        for (int i = 0; i < args.length; ++i) {
            String className = args[i];

            Class testClass = null;
            try {
                testClass = Class.forName(className);

            }
            catch (ClassNotFoundException e) {
                LOG.error("unable to load class '" + className + "'");
            }

            if (testClass != null) {
                tests.addTestSuite(testClass);
            }
        }


        if (tests.countTestCases() == 0) {
            LOG.error("no tests to run, exiting");
        }
        else {
            TestRunner.run(tests);
        }
    }


    /**
     * Mocks the FLEXIBLE_OFFSET_ENABLED_FLAG indicator of the {@link org.kuali.core.service.KualiConfigurationService} in
     * SpringServiceLocator. The mock is backed by the current real service, so all other configurations appear unchanged.
     * 
     * @param flexibleOffsetEnabled the mock value of the indicator
     * @see SpringServiceLocator#mockService(String, Object)
     */
    public static void mockConfigurationServiceForFlexibleOffsetEnabled(boolean flexibleOffsetEnabled) {
        Map<String, FinancialSystemParameter> params = KNSServiceLocator.getKualiConfigurationService().getParametersByGroup(Constants.ParameterGroups.SYSTEM);
        for (Map.Entry<String, FinancialSystemParameter> param : params.entrySet()) {
            if (param.getKey().equals(Constants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG)) {
                FinancialSystemParameter sysParam = param.getValue();
                sysParam.setFinancialSystemScriptName(Constants.ParameterGroups.SYSTEM);
                sysParam.setFinancialSystemParameterName(Constants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG);
                sysParam.setFinancialSystemParameterDescription(Constants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG);
                if (flexibleOffsetEnabled) {
                    sysParam.setFinancialSystemParameterText("Y");
                }
                else {
                    sysParam.setFinancialSystemParameterText("N");
                }
                KNSServiceLocator.getBusinessObjectService().save(sysParam);
                return;
            }
        }
    }
    
    /**
     * Removes Spring cache for the given method invocation parameters if one is present.
     */
    public static void clearMethodCache(Class methodClass, String methodName, Class[] parameterTypes, Object[] parameterValues) throws Exception {
        // build cache key
        MethodCacheInterceptor methodCacheInterceptor = KNSServiceLocator.getMethodCacheInterceptor();
        String methodCacheKey = methodCacheInterceptor.buildCacheKey(methodClass.getMethod(methodName, parameterTypes).toString(), parameterValues);
        methodCacheInterceptor.removeCacheKey(methodCacheKey);
    }

    /**
     * This sets the system parameter for flexible offset on or off and clears the method cache for retrieving the parameter.
     */
    public static void setFlexibleOffsetSystemParameter(boolean flexibleOffsetEnabled) throws Exception {
        // retrieve flexible offset parameter
        FinancialSystemParameter flexibleOffsetParameter = new FinancialSystemParameter();
        flexibleOffsetParameter.setFinancialSystemScriptName(Constants.ParameterGroups.SYSTEM);
        flexibleOffsetParameter.setFinancialSystemParameterName(Constants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG);

        flexibleOffsetParameter = (FinancialSystemParameter) KNSServiceLocator.getBusinessObjectService().retrieve(flexibleOffsetParameter);
        if (flexibleOffsetParameter == null) {
            throw new RuntimeException("flexible offset system parameter not found");
        }

        // update parameter text and store
        if (flexibleOffsetEnabled) {
            flexibleOffsetParameter.setFinancialSystemParameterText(Constants.ACTIVE_INDICATOR);
        }
        else {
            flexibleOffsetParameter.setFinancialSystemParameterText(Constants.NON_ACTIVE_INDICATOR);
        }
        KNSServiceLocator.getBusinessObjectService().save(flexibleOffsetParameter);

        // clear method cache
        clearMethodCache(KualiConfigurationService.class, "getApplicationParameterIndicator", new Class[] { String.class, String.class }, 
                new Object[] { Constants.ParameterGroups.SYSTEM, Constants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG });
    }
}
