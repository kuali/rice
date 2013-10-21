/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.testtools.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * Util Properties methods which have come from refactoring test generation using freemarker.
 * </p>
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PropertiesUtils {

    /**
     * @param inputStream to read properties from
     * @return Properties read from given Inputstream
     * @throws IOException
     */
    public static Properties loadProperties(InputStream inputStream) throws IOException {
        Properties props = new Properties();

        if(inputStream != null) {
            props.load(inputStream);
        }

        return props;
    }

    /**
     * <p>
     * Read Properties from given Inputstream or Resource Loaction if the InputStream is null.
     * </p>
     * @param fileLocation null means use resourceLocation
     * @param resourceLocation load resource as a stream {@code getClass().getClassLoader().getResourceAsStream(resourceLocation);}
     * @return Properties read from given Inputstream or Resource Loaction if the InputStream is null
     * @throws IOException
     */
    public Properties loadProperties(String fileLocation, String resourceLocation) throws IOException {
        Properties props = null;
        InputStream in = null;
        if(fileLocation != null) {
            in = new FileInputStream(fileLocation);
        } else {
            in = getClass().getClassLoader().getResourceAsStream(resourceLocation);
        }
        if(in != null) {
            props = PropertiesUtils.loadProperties(in);
            in.close();
        }

        return props;
    }

    public static Properties loadPropertiesWithSystemOverrides(InputStream inputStream) throws IOException {
        Properties props = PropertiesUtils.loadProperties(inputStream);
        PropertiesUtils.systemPropertiesOverride(props);
        return props;
    }

    public static Properties loadPropertiesWithSystemOverridesAndNumberedPropertiesToList(InputStream inputStream) throws IOException {
        Properties props = PropertiesUtils.loadProperties(inputStream);
        PropertiesUtils.systemPropertiesOverride(props);
        PropertiesUtils.transformNumberedPropertiesToList(props);
        return props;
    }

    /**
     * <p>
     * Given a key that ends in a number, remove the number.
     * </p>
     * @param numberedKey in the form of some.key.number
     * @return some.key part of some.key.number
     */
    public static String removeNumber(final String numberedKey) {
        String unnumberedKey = numberedKey;
        int firstNumberIndex = unnumberedKey.length() - 1;
        while (Character.isDigit(unnumberedKey.charAt(firstNumberIndex))) {
            firstNumberIndex--;
        }
        unnumberedKey = unnumberedKey.substring(0, firstNumberIndex + 1);

        return unnumberedKey;
    }

    /**
     * <p>
     * Override the given Properties with JVM argument {@code -Dkey=value}.
     * </p>
     * @param props properties to update with System.getProperty overrides.
     */
    public static void systemPropertiesOverride(Properties props) {
        PropertiesUtils.systemPropertiesOverride(props, null);
    }

    /**
     * <p>
     * Override the given Properties with JVM argument {@code -Darg.key=value}.
     * </p>
     * -Dkey.propertyname= to override the property value for propertyname.
     * @param props properties to update with System.getProperty overrides.
     * @param arg optional value that the property names will be appended to.
     */
    public static void systemPropertiesOverride(Properties props, String arg) {
        Enumeration<?> names = props.propertyNames();
        Object nameObject;
        String key;
        while (names.hasMoreElements()) {

            nameObject = names.nextElement();
            if (nameObject instanceof String) {

                key = (String)nameObject;
                if (arg == null || arg.isEmpty()) {
                    props.setProperty(key, System.getProperty(key, props.getProperty(key)));
                } else {
                    props.setProperty(key, System.getProperty(arg + "." + key, props.getProperty(key)));
                }
            }
        }
    }

    /**
     * <p>
     * Transform the given Properties keys which end in numbers to a List placed in a Map with the map key being the unumbered
     * part of the Properties key with an s appended to it.
     * </p>
     * @param props Properties to have keys ending in
     */
    public static void transformNumberedPropertiesToList(Properties props) {
        String key = null;
        String unnumberedKey = null;
        List<String> keyList = null;
        List<String> removeKeys = new LinkedList<String>();

        // unnumber keys and place their values in a list
        Iterator keys = props.keySet().iterator();
        Map<String, List<String>> keysLists = new HashMap<String, List<String>>();
        while (keys.hasNext()) {
            key = (String)keys.next();
            if (Character.isDigit(key.charAt(key.length()-1))) {
                unnumberedKey = removeNumber(key);
                if (keysLists.get(unnumberedKey) == null) {
                    keyList = new ArrayList<String>();
                    keyList.add(props.getProperty(key));
                    keysLists.put(unnumberedKey, keyList);
                    removeKeys.add(key);
                } else {
                    keyList = keysLists.get(unnumberedKey);
                    keyList.add(props.getProperty(key));
                    keysLists.put(unnumberedKey, keyList);
                    removeKeys.add(key);
                }
            }
        }

        // remove keys that where unnumbered
        Iterator removeKey = removeKeys.iterator();
        while (removeKey.hasNext()) {
            key = (String)removeKey.next();
            props.remove(key);
        }

        // put new unnumbered key values mapped by unnumber key with an s appended to it.
        Iterator newKeys = keysLists.keySet().iterator();
        String newKey = null;
        while (newKeys.hasNext()) {
            newKey = (String)newKeys.next();
            props.put(newKey + "s", keysLists.get(newKey));
        }
    }
}
