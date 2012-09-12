/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.krad.messages.Message;
import org.kuali.rice.krad.messages.MessageService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DictionaryBeanPostProcessor implements BeanFactoryPostProcessor {
    private static final Log LOG = LogFactory.getLog(DictionaryBeanPostProcessor.class);

    /**
     * Iterates through all beans in the factory and invokes processing
     *
     * @param beanFactory - bean factory instance to process
     * @throws org.springframework.beans.BeansException
     */
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Set<String> processedBeanNames = new HashSet<String>();

        LOG.info("Beginning dictionary bean post processing");

        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (int i = 0; i < beanNames.length; i++) {
            String beanName = beanNames[i];
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

        }

        LOG.info("Finished dictionary bean post processing");
    }

    protected void processRootBeanDefinition(String beanName, BeanDefinition beanDefinition,
            ConfigurableListableBeanFactory beanFactory) {
        Class<?> beanClass = getBeanClass(beanDefinition, beanFactory);
        if ((beanClass == null) || !DictionaryBean.class.isAssignableFrom(beanClass)) {
            return;
        }

        MutablePropertyValues pvs = beanDefinition.getPropertyValues();

        // take namespace from bean definition (if given) first
        String namespace = "";
        if (pvs.contains(KRADPropertyConstants.NAMESPACE_CODE)) {
            PropertyValue propertyValue = pvs.getPropertyValue(KRADPropertyConstants.NAMESPACE_CODE);
            namespace = getStringValue(propertyValue.getValue());
        }

        // if not on bean definition, get from associated module in the dictionary
        DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();
        namespace = dataDictionary.getNamespaceForBeanDefinition(beanName);

        // use component code on bean definition (if given), else default to the bean id
        String componentCode = beanName;
        if (pvs.contains(KRADPropertyConstants.COMPONENT_CODE)) {
            PropertyValue propertyValue = pvs.getPropertyValue(KRADPropertyConstants.COMPONENT_CODE);
            componentCode = getStringValue(propertyValue.getValue());
        }

        // if a namespace and component was found retrieve all messages associated with them
        if (StringUtils.isNotBlank(namespace) && StringUtils.isNotBlank(componentCode)) {
            Collection<Message> beanMessages = getMessageService().getAllMessagesForComponent(namespace, componentCode);
        }
    }

    /**
     * Retrieves the class for the object that will be created from the bean definition. Since the class might not
     * be configured on the bean definition, but by a parent, each parent bean definition is recursively checked for
     * a class until one is found
     *
     * @param beanDefinition - bean definition to get class for
     * @param beanFactory - bean factory that contains the bean definition
     * @return Class<?> class configured for the bean definition, or null
     */
    protected Class<?> getBeanClass(BeanDefinition beanDefinition, ConfigurableListableBeanFactory beanFactory) {
        if (StringUtils.isNotBlank(beanDefinition.getBeanClassName())) {
            try {
                return Class.forName(beanDefinition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                // swallow exception and return null so bean is not processed
                return null;
            }
        } else if (StringUtils.isNotBlank(beanDefinition.getParentName())) {
            BeanDefinition parentBeanDefinition = beanFactory.getBeanDefinition(beanDefinition.getParentName());
            if (parentBeanDefinition != null) {
                return getBeanClass(parentBeanDefinition, beanFactory);
            }
        }

        return null;
    }

    /**
     * Determines whether the given value is of String type and if so returns the string value
     *
     * @param value - object value to check
     * @return String string value for object or null if object is not a string type
     */
    protected String getStringValue(Object value) {
        if (value instanceof TypedStringValue) {
            TypedStringValue typedStringValue = (TypedStringValue) value;
            return typedStringValue.getValue();
        } else if (value instanceof String) {
            return (String) value;
        }

        return null;
    }

    protected DataDictionaryService getDataDictionaryService() {
        return KRADServiceLocatorWeb.getDataDictionaryService();
    }

    protected MessageService getMessageService() {
        return KRADServiceLocator.getMessageService();
    }

}
