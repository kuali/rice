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
package org.kuali.rice.testtools.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
 * Util Properties methods which have come from refactoring test generation using freemarker, providing overriding of
 * file properties from System Properties as well as setting file properties as System Properties, as well as turning
 * numbered properties to a List.
 * </p>
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PropertiesUtils {

    /**
     * <p>
     * Plain load Properties from the given inputStream.
     * </p>
     *
     * @param inputStream to read properties from
     * @return Properties read from given Inputstream
     * @throws IOException
     */
    public Properties loadProperties(InputStream inputStream) throws IOException {
        Properties props = new Properties();

        if(inputStream != null) {
            props.load(inputStream);
        }

        return props;
    }

    /**
     * <p>
     * Read Properties from given Inputstream or Resource Loaction if the InputStream is null.
     * </p><p>
     * If a FileNotFoundException is thrown opening the InputStream an attempt will be made to read as a resource.
     * </p>
     *
     * @param fileLocation null means use resourceLocation
     * @param resourceLocation load resource as a stream {@code getClass().getClassLoader().getResourceAsStream(resourceLocation);}
     * @return Properties read from given Inputstream or Resource Loaction if the InputStream is null
     * @throws IOException
     * @deprecated {@see #loadProperties(String)}
     */
    public Properties loadProperties(String fileLocation, String resourceLocation) throws IOException {
        Properties props = null;
        InputStream in = null;
        if(fileLocation != null) {
            try {
                in = new FileInputStream(fileLocation);
            } catch (FileNotFoundException fio) {
                System.out.println(fio.getMessage() + " trying to read as resource.");
                if (resourceLocation != null) {
                    System.out.println("Trying to read as " + resourceLocation+ " as a resource.");
                    in = getClass().getClassLoader().getResourceAsStream(resourceLocation);
                }
            }
        } else {
            in = getClass().getClassLoader().getResourceAsStream(resourceLocation);
            if (in == null) {
                System.out.println("Unable to read " + fileLocation + " as a file or " + resourceLocation + " as a resource stream");
            }
        }
        if(in != null) {
            props = loadProperties(in);
            in.close();
        }

        return props;
    }

    /**
     * <p>
     * Read Properties from given Loaction
     * </p><p>
     * If a FileNotFoundException is thrown opening the InputStream an attempt will be made to read as a resource.
     * </p>
     *
     * @param location
     * @return Properties read from given Inputstream or Resource Loaction if the InputStream is null
     * @throws IOException
     */
    public Properties loadProperties(String location) throws IOException {
        Properties props = null;
        InputStream in = null;
        try {
            in = new FileInputStream(location);
        } catch (FileNotFoundException fio) {
            System.out.println(fio.getMessage() + " trying to read as resource.");
            in = getClass().getClassLoader().getResourceAsStream(location);
            if (in == null) {
                System.out.println("Unable to read " + location + " as a resource stream");
            }
        }
        if(in != null) {
            props = loadProperties(in);
            in.close();
        }

        return props;
    }

    public Properties loadPropertiesWithSystemAndOverrides(String location) throws IOException {
        Properties properties =  loadProperties(location);
        return systemPropertiesAndOverride(properties);
    }

    /**
     * <p>
     * Beware of classloader/timing issues!  Sometimes properties get added to the System properties after the point
     * you might expect.  Resulting in the System property not really being set for when you expected, such as with
     * settign statics.  Looks like WebDriverLegacyITBase has this going on with public.remote.url
     * </p>
     *
     * @param location file or resource to load properties from, file is attempted first
     * @return Properties
     * @throws IOException
     */
    public Properties loadPropertiesWithSystemAndOverridesIntoSystem(String location) throws IOException {
        Properties properties = loadProperties(location);
        properties = systemPropertiesAndOverride(properties);

        Iterator propKeys = properties.keySet().iterator();
        while (propKeys.hasNext()) {
            String key = (String)propKeys.next();
            if (System.getProperty(key) == null) {
                System.setProperty(key, properties.getProperty(key));
            }
        }
        return properties;
    }

    public Properties loadPropertiesWithSystemOverrides(String location) throws IOException {
        Properties properties =  loadProperties(location);
        return systemPropertiesOverride(properties);
    }

    /**
     * <p>
     * Read the properties from an inputStream overridding keys defined as JVM arguments.
     * </p><p>
     * {@see #systemPropertiesOverride}
     * </p>
     *
     * @param inputStream to read properties from
     * @return Properties loaded from inputStream and overridden with JVM arguments
     * @throws IOException
     */
    public  Properties loadPropertiesWithSystemOverrides(InputStream inputStream) throws IOException {
        Properties props = loadProperties(inputStream);
        props = systemPropertiesOverride(props);
        return props;
    }

    /**
     * <p>
     * Read the properties from an inputStream overridding keys defined as JVM arguments and transforming numbered keys
     * into a list.
     * </p><p>
     * {@see #systemPropertiesOverride}
     * {@see #transformNumberedPropertiesToList}
     * </p>
     *
     * @param inputStream
     * @return Properties loaded from inputStream, overridden with JVM arguments, and keys ending in numbers transformed to a List
     * @throws IOException
     */
    public Properties loadPropertiesWithSystemOverridesAndNumberedPropertiesToList(InputStream inputStream) throws IOException {
        Properties props = loadProperties(inputStream);
        props = systemPropertiesOverride(props);
        props = transformNumberedPropertiesToList(props);
        return props;
    }

    /**
     * <p>
     * Given a key that ends in a number, remove the number.
     * </p>
     *
     * @param numberedKey in the form of some.key.number
     * @return some.key part of some.key.number
     */
    public String removeNumber(final String numberedKey) {
        String unnumberedKey = numberedKey;
        int firstNumberIndex = unnumberedKey.length() - 1;
        while (Character.isDigit(unnumberedKey.charAt(firstNumberIndex))) {
            firstNumberIndex--;
        }
        unnumberedKey = unnumberedKey.substring(0, firstNumberIndex + 1);

        return unnumberedKey;
    }

    public Properties systemPropertiesAndOverride(Properties props) {
        return systemPropertiesAndOverride(props, null);
    }

    /**
     * <p>
     * Override the given Properties with JVM argument {@code -Dkey=value}.
     * </p>
     *
     * @param props properties to update with System.getProperty overrides.
     */
    public Properties systemPropertiesOverride(Properties props) {
        return systemPropertiesOverride(props, null);
    }

    /**
     * <p>
     * In addition to overriding file properties from System Properties, System properties are added to the returned
     * Properties.
     * </p><p>
     * {@see #systemPropertiesOverride}
     * </p>
     *
     *
     * @param props Properties with System Properties added and Overriding file properties
     * @param arg filter System Properties added to Properties by the Property key starting with arg
     * @return
     */
    public Properties systemPropertiesAndOverride(Properties props, String arg) {
        PropertiesUtils propUtils = new PropertiesUtils();
        props = propUtils.systemPropertiesOverride(props, arg);

        Iterator iter = System.getProperties().stringPropertyNames().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            if (arg == null || arg.equals("")) {
                if (!props.contains(key)) {
                    props.setProperty(key, System.getProperty(key));
                }
            } else {
                if (key.startsWith(arg) && !props.contains(key)) {
                    props.setProperty(key, System.getProperty(key));
                }
            }
        }
        return props;
    }

    /**
     * <p>
     * Override the given Properties with JVM argument {@code -Darg.key=value}.
     * </p><p>
     * -Dkey.propertyname= to override the property value for propertyname.
     * </p>
     *
     * @param props properties to update with System.getProperty overrides.
     * @param arg optional value that the property names will be appended to.
     */
    public Properties systemPropertiesOverride(Properties props, String arg) {
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
        return props;
    }

    /**
     * <p>
     * Transform the given Properties keys which end in numbers to a List placed in a Map with the map key being the unumbered
     * part of the Properties key with an s appended to it.
     * </p>
     *
     * @param props Properties to have keys ending in
     */
    public Properties transformNumberedPropertiesToList(Properties props) {
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
        return props;
    }
}
