/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.core.impl.config.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.Unmarshaller;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

/**
 * Unit testing for JAXBConfigImpl
 */
public class JAXBConfigImplTest {

    private static final Logger logger = LoggerFactory.getLogger(JAXBConfigImplTest.class);
    private static final String SIMPLE = "classpath:org/kuali/rice/core/impl/config/property/simple.xml";
    private static final String OTHER = "classpath:org/kuali/rice/core/impl/config/property/other.xml";
    private static final String BREAKFAST = "classpath:org/kuali/rice/core/impl/config/property/breakfast.xml";
    private static final String CEREAL = "classpath:org/kuali/rice/core/impl/config/property/cereal.xml";

    @Test
    public void testParseValue() throws IOException {
        Properties props = new Properties();
        props.setProperty("db.vendor", "mysql");
        props.setProperty("mysql.driver", "foo");
        props.setProperty("oracle.driver", "bar");
        props.setProperty("jdbc.driver", "${${db.vendor}.driver}");

        Param param = new Param();
        param.setSystem(true);
        param.setName("barbaz");
        param.setValue("${bar} plus ${baz} = ${jdbc.driver}");

        JAXBConfigImpl config = new JAXBConfigImpl(props);
        config.setSystemOverride(true);
        config.doSystem(param);
        config.resolveRawToCache();

        String barbaz1 = config.getProperty("barbaz");
        String barbaz2 = config.getRawProperties().getProperty("barbaz");
        String barbaz3 = System.getProperty("barbaz");

        List<String> barbazes = ImmutableList.of(barbaz1, barbaz2, barbaz3);

        logger.info("{}", barbazes);
    }

    @Test
    public void testPlaceholderResolution() throws IOException {
        Properties overrides = new Properties();
        //overrides.setProperty("cereal.flavor", "chocolate");
        JAXBConfigImpl config = new JAXBConfigImpl(CEREAL, overrides);
        config.parseConfig();
        //Assert.assertEquals("chocolate cheerios", config.getProperty("cereal.type")); // 2.99 is the value in breakfast.xml
        info(config.getProperties());
        config.putProperty("cereal.flavor", "chocolate");
        info(config.getProperties());
        System.out.print("");
    }

    @Test
    public void testLocationBasedPropertyOverride() throws IOException {
        String key = "milk.price";
        String value = "2.50";
        Properties overrides = new Properties();
        overrides.setProperty(key, value);
        JAXBConfigImpl config = new JAXBConfigImpl(BREAKFAST, overrides);
        config.parseConfig();
        Assert.assertEquals("2.99", config.getProperty(key)); // 2.99 is the value in breakfast.xml
        config.putProperties(overrides);
        Assert.assertEquals(value, config.getProperty(key)); // 2.50 is the value in the overrides properties
    }

    @Test
    public void testLocationBasedPropertyWins() throws IOException {
        String key = "milk.price";
        String value = "2.50";
        Properties properties = new Properties();
        properties.setProperty(key, value);
        JAXBConfigImpl config = new JAXBConfigImpl(BREAKFAST, properties);
        config.parseConfig();
        Assert.assertEquals("2.99", config.getProperty(key)); // 2.99 is the value in breakfast.xml
    }

    @Test
    public void testTheBasics() throws IOException {
        JAXBConfigImpl config = getConfig();
        config.setSystemOverride(true);
        Assert.assertTrue(config.isSystemOverride());
        config.putProperty("joe", "blow");
        Properties p = config.getProperties();
        Map<String, Object> objects = config.getObjects();
        Assert.assertEquals("Bill", p.getProperty("name"));
        Assert.assertEquals("Bill", config.getProperty("name"));
        Assert.assertTrue(objects.size() == 0);
        Assert.assertNull(config.getObject("whatever"));
        config.putConfig(new JAXBConfigImpl());
        config.putObject("truck", "chevy");
        Assert.assertEquals("chevy", (String) config.getObject("truck"));
        config.removeObject("truck");
        Assert.assertNull(config.getObject("truck"));
        config.putProperty("truck", "chevy");
        Assert.assertEquals("chevy", config.getProperty("truck"));
        config.removeProperty("truck");
        Assert.assertNull(config.getProperty("truck"));

        Properties newProperties = new Properties();
        newProperties.setProperty("hello", "world");
        config.putProperties(null);
        config.putProperties(new Properties());
        config.putProperties(newProperties);

        String path = config.getProperty("path");
        Assert.assertEquals("/usr/bin;/usr/local/bin;", path);

        logger.info("Displaying " + p.size() + " properties\n\n" + config.getPropertyValuesAsString(p));
        logger.info(config.toString());

    }

    @Test
    public void testConstructors1() {
        JAXBConfigImpl jci = new JAXBConfigImpl();
        jci.putObjects(new HashMap<String, Object>());
        new JAXBConfigImpl(jci);
    }

    @Test
    public void testConstructors2() {
        new JAXBConfigImpl(SIMPLE, new JAXBConfigImpl());
    }

    @Test
    public void testConstructors3() {
        new JAXBConfigImpl(Arrays.asList(SIMPLE), new JAXBConfigImpl());
    }

    @Test
    public void tesetParseConfig() throws IOException {
        JAXBConfigImpl config = new JAXBConfigImpl(Arrays.asList(OTHER));
        config.parseConfig();
        Unmarshaller unmarshaller = config.getUnmarshaller();
        config.parseConfig(OTHER, unmarshaller, 2);
    }

    @Test
    public void testConstructors4() {
        new JAXBConfigImpl(new Properties());
    }

    @Test
    public void testConstructors5() {
        new JAXBConfigImpl(SIMPLE, new Properties());
    }

    @Test
    public void testConstructors6() {
        new JAXBConfigImpl(Arrays.asList(SIMPLE), new Properties());
    }

    @Test
    public void testConstructors7() {
        new JAXBConfigImpl((JAXBConfigImpl) null);
    }

    @Test
    public void testOther() throws IOException {
        JAXBConfigImpl config = new JAXBConfigImpl();
        config.parseConfig();
        Assert.assertNull(config.flatten(null));
        Assert.assertEquals(" ", config.flatten("\n"));
        Assert.assertEquals(" ", config.flatten("\r"));
    }

    @Test
    public void testSingle() throws IOException {
        JAXBConfigImpl config = new JAXBConfigImpl(SIMPLE);
        config.parseConfig();
    }

    @Test
    public void testDoSystem() {
        JAXBConfigImpl config = new JAXBConfigImpl();
        Param p = new Param();
        p.setName("foo");
        p.setValue("bar");
        p.setOverride(false);
        config.doSystem(p);
        Assert.assertEquals("bar", System.getProperty("foo"));
        p.setOverride(false);
        p.setValue("barbar");
        config.doSystem(p);
        Assert.assertEquals("bar", System.getProperty("foo"));
        System.getProperties().remove("foo");
    }

    @Test
    public void testReplaceVariable() {
        String name = "path";
        String value = "${path};/usr/local/bin";
        JAXBConfigImpl config = new JAXBConfigImpl();
        config.replaceVariable(name, value);
    }

    @Test
    public void testLogPropertyChange() {
        JAXBConfigImpl config = new JAXBConfigImpl();
        config.logPropertyChange("", "foo", "${path}", "old", "new");
    }

    @Test
    public void testResolveNullKeySet() {
        String key = "foo";
        JAXBConfigImpl config = new JAXBConfigImpl();
        String value = config.resolve(key, null);
        Assert.assertEquals("", value);
    }

    @Test
    public void testResolveNullKeySet2() {
        String key = "foo";
        JAXBConfigImpl config = new JAXBConfigImpl();
        config.setProperty("foo", "${bar}");
        String value = config.resolve(key, null);
        Assert.assertEquals("", value);
    }

    @Test
    public void testResolveNonNullKeySet() {
        String key = "foo";
        JAXBConfigImpl config = new JAXBConfigImpl();
        config.setProperty("foo", "${bar}");
        String value = config.resolve(key, new HashSet<String>());
        Assert.assertEquals("", value);
    }

    @Test
    public void testResolveCircular() {
        String key = "foo";
        Set<String> keySet = new HashSet<String>();
        keySet.add(key);
        JAXBConfigImpl config = new JAXBConfigImpl();
        try {
            config.resolve(key, keySet);
            Assert.fail("This should have failed due to a circular reference");
        } catch (ConfigurationException e) {
            ; // ignore
        }
    }

    @Test
    public void testGenerateRandomInteger() {
        try {
            JAXBConfigImpl config = new JAXBConfigImpl();
            config.generateRandomInteger("");
            Assert.fail("This should have failed due to an invalid range spec");
        } catch (IllegalArgumentException e) {
            ; // ignore
        }
    }

    @Test
    public void testUnmarshalQuietly() {
        try {
            JAXBConfigImpl config = new JAXBConfigImpl();
            config.unmarshalQuietly(null, null);
            Assert.fail("This should have failed with IllegalStateException");
        } catch (IllegalStateException e) {
            ; // ignore
        }
    }

    protected JAXBConfigImpl getConfig() throws IOException {
        List<String> resources = new ArrayList<String>();
        resources.add(SIMPLE);
        resources.add(OTHER);
        resources.add("");
        resources.add("classpath:non-existent-file-that-does-not-exist.properties");
        JAXBConfigImpl jci = new JAXBConfigImpl(resources);
        jci.parseConfig();
        return jci;
    }

    protected static void info(Properties properties) {
        logger.info("Displaying {} properties\n\n{}\n", properties.size(), toString(properties));
    }

    protected static String toString(Properties properties) {
        List<String> keys = new ArrayList<String>(properties.stringPropertyNames());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            String value = properties.getProperty(key).replace("\r", " ").replace("\n", " ");
            sb.append(key + "=[" + value + "]\n");
        }
        return sb.toString();
    }
}
