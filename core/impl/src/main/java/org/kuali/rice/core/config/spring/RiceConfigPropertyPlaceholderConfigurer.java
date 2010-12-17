/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.core.config.spring;

import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.ConfigLogger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.StringValueResolver;

/**
 * This PropertyPlaceholderConfigurer impl will load properties from the current ConfigContext.
 * It also creates a BeanDefinitionVisitor that will allow for injection of Properties objects created
 * from properties of a like prefix identified by a value of $[my.prefix].
 * 
 * example:
 * foo.prop1=bar
 * foo.prop2=foo
 * fooPropertyObject=$[foo.]
 * 
 * results in
 * fooPropertyObject={prop1=bar; prop2=foo}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RiceConfigPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    
    protected final org.apache.log4j.Logger log = Logger.getLogger(RiceConfigPropertyPlaceholderConfigurer.class);

    // these have to be redeclared because they are private in the base with no getters
    private String beanName;
    private BeanFactory beanFactory;
    
    @Override
    protected void loadProperties(Properties props) throws IOException {
        // perform standard property resource file loading
        super.loadProperties(props);
        // load the Rice properties
        Config config = ConfigContext.getCurrentContextConfig();
        if (config != null) {
            log.debug("Replacing parameters in Spring using config:\r\n" + config);
            ConfigLogger.logConfig(config);
            props.putAll(config.getProperties());
        }
    }
    
    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
        super.setBeanName(beanName);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        super.setBeanFactory(beanFactory);
    }

    // this has to be redeclared because private in the base with no getter
    private String nullValue;
    
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {

        StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
        RiceConfigBeanDefinitionVisitor visitor = new RiceConfigBeanDefinitionVisitor(valueResolver);

        String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
        for (int i = 0; i < beanNames.length; i++) {
            // Check that we're not parsing our own bean definition,
            // to avoid failing on unresolvable placeholders in properties file locations.
            if (!(beanNames[i].equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
                BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(beanNames[i]);
                try {
                    visitor.visitBeanDefinition(bd);
                } catch (BeanDefinitionStoreException ex) {
                    throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanNames[i], ex.getMessage());
                }
            }
        }

        // New in Spring 2.5: resolve placeholders in alias target names and aliases as well.
        beanFactoryToProcess.resolveAliases(valueResolver);
    }

    /**
     * BeanDefinitionVisitor that resolves placeholders in String values, delegating to the <code>parseStringValue</code>
     * method of the containing class.
     */
    public class PlaceholderResolvingStringValueResolver implements StringValueResolver {

        private final Properties props;

        public PlaceholderResolvingStringValueResolver(Properties props) {
            this.props = props;
        }

        public String resolveStringValue(String strVal) throws BeansException {
            String value = parseStringValue(strVal, this.props, new HashSet<String>());
            return (value.equals(nullValue) ? null : value);
        }

        public Properties resolvePropertiesValue(String strVal) {
            Properties prefixedProps = new Properties();

            for (Object key : props.keySet()) {
                String keyStr = (String) key;
                if (keyStr.startsWith(strVal)) {
                    String newKeyStr = keyStr.substring(strVal.length());
                    prefixedProps.put(newKeyStr, resolveStringValue((String) props.get(key)));
                }
            }

            return prefixedProps;
        }

    }
    
    @Override
    public void setNullValue(String nullValue) {
        this.nullValue = nullValue;
        super.setNullValue(nullValue);
    }

}
