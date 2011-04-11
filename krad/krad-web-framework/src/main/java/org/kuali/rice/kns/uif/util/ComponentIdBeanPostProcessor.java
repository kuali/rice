/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kuali.rice.kns.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.core.Component;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Sets the unique Id for a <code>Component</code> if not set
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentIdBeanPostProcessor implements BeanPostProcessor {

    public ComponentIdBeanPostProcessor() {}

    /**
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object,
     *      java.lang.String)
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * Sets the unique Id for a <code>Component</code> if not set
     * 
     * <p>
     * Will use the bean name (if not the spring generated) or generate a
     * numeric identifier
     * </p>
     * 
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object,
     *      java.lang.String)
     * @see org.kuali.rice.kns.uif.core.Component#getDecorators
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Component) {
            Component component = (Component) bean;

            if (StringUtils.isBlank(component.getId())) {
                if (!StringUtils.contains(beanName, "$")) {
                    component.setId(beanName);
                }
                else {
                    component.setId(ComponentFactory.getNextId());
                }
            }
        }

        return bean;
    }

}
