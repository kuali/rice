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
package org.kuali.rice.core.impl.config.property

import org.junit.Before
import org.junit.Test
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.framework.config.property.SimpleConfig
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import org.springframework.core.io.ByteArrayResource

import static org.junit.Assert.assertEquals

/**
 * Tests ConfigPropertiesFactoryBean
 */
class ConfigPropertiesFactoryBeanTest {
    SimpleConfig cfg = new SimpleConfig()
    AutowireCapableBeanFactory factory = new DefaultListableBeanFactory()

    @Before
    void initConfigContext() {
        cfg.putProperty("root", "root")
        cfg.putProperty("a.1", "a.1")
        cfg.putProperty("a.2", "a.2")
        cfg.putProperty("b.1", "b.1")
        cfg.putProperty("b.2", "b.2")
        ConfigContext.init(cfg)
    }

    @Test
    void testBare() {
        ConfigPropertiesFactoryBean bean = new ConfigPropertiesFactoryBean();
        bean = factory.initializeBean(bean, "bean");
        Properties props = bean.getObject();
        assertEquals(cfg.getProperties(), props)
    }

    @Test
    void testPrefixed() {
        ConfigPropertiesFactoryBean bean = new ConfigPropertiesFactoryBean();
        bean.setPrefix("a.")
        bean = factory.initializeBean(bean, "bean");
        Properties props = bean.getObject();
        assertEquals([1: 'a.1', 2: 'a.2'] as Properties, props)
    }

    // confirm standard PropertiesLoaderSupport functionality still works as expected

    @Test
    void testWithLocal() {
        ConfigPropertiesFactoryBean bean = new ConfigPropertiesFactoryBean();
        bean.setPrefix("a.")
        // set local properties; they will add but not override
        bean.setProperties(['1': 'local.a.1', '3': 'local.a.3' ] as Properties)
        bean.setLocation(new ByteArrayResource("""
          1=resource.a.1
          3=resource.a.3
        """.getBytes()))
        bean = factory.initializeBean(bean, "bean");
        // rice config overrides resource, both override local
        assertEquals([1: 'a.1', 2: 'a.2', 3: 'resource.a.3'] as Properties, bean.getObject())

        bean = new ConfigPropertiesFactoryBean();
        bean.setPrefix("a.")
        bean.setProperties(['1': 'local.a.1', '3': 'local.a.3' ] as Properties)
        bean.setLocation(new ByteArrayResource("""
          1=resource.a.1
          3=resource.a.3
          4=resource.a.4
        """.getBytes()))
        bean.setLocalOverride(true)
        bean = factory.initializeBean(bean, "bean");
        // rice config overrides resource, local overrides both
        assertEquals([1: 'local.a.1', 2: 'a.2', 3: 'local.a.3', 4: 'resource.a.4'] as Properties, bean.getObject())
    }
}
