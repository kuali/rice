/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kcb;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Initializing bean that initializes KCB (specifically the GlobalKCBServiceLocator)
 * on Spring context initialization.  This bean should be eagerly initialized (not marked lazy) 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KCBInitializer implements BeanFactoryAware, InitializingBean, DisposableBean {
    private BeanFactory beanFactory;

    /**
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        GlobalKCBServiceLocator.init(beanFactory);
        // kill the reference, our job is done
        beanFactory = null;
    }

    public void destroy() throws Exception {
        // prevent anything from accessing our services after the module has been destroyed/shutdown
        // our module's lifecycle is tied to the Spring context lifecycle for now
        GlobalKCBServiceLocator.destroy();
    }
}