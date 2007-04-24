/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core.util.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * This class is what is recommended by the spring folks to get around stronger validation in spring 2 that tries to prevent circular
 * references: http://opensource.atlassian.com/projects/spring/browse/SPR-2415?page=comments
 */
public class ClassPathXmlApplicationContext extends org.springframework.context.support.ClassPathXmlApplicationContext {

    /**
     * Sets allowRawInjectionDespiteWrapping to true.
     * 
     * @see org.springframework.context.support.AbstractRefreshableApplicationContext#createBeanFactory()
     */
    @Override
    protected DefaultListableBeanFactory createBeanFactory() {
        DefaultListableBeanFactory beanFactory = super.createBeanFactory();
        beanFactory.setAllowRawInjectionDespiteWrapping(true);
        return beanFactory;
    }

    /**
     * @see org.springframework.context.support.ClassPathXmlApplicationContext#ClassPathXmlApplicationContext(java.lang.String)
     */
    public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
        super(configLocation);
    }

    /**
     * @see org.springframework.context.support.ClassPathXmlApplicationContext#ClassPathXmlApplicationContext(java.lang.String[])
     */
    public ClassPathXmlApplicationContext(String[] configLocations) throws BeansException {
        super(configLocations);
    }

    /**
     * @see org.springframework.context.support.ClassPathXmlApplicationContext#ClassPathXmlApplicationContext(java.lang.String[],org.springframework.context.ApplicationContext)
     */
    public ClassPathXmlApplicationContext(String[] configLocations, ApplicationContext parent) throws BeansException {
        super(configLocations, parent);
    }

    /**
     * @see org.springframework.context.support.ClassPathXmlApplicationContext#ClassPathXmlApplicationContext(java.lang.String[],boolean)
     */
    public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
        super(configLocations, refresh);
    }

    /**
     * @see org.springframework.context.support.ClassPathXmlApplicationContext#ClassPathXmlApplicationContext(java.lang.String[],boolean,org.springframework.context.ApplicationContext)
     */
    public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent) throws BeansException {
        super(configLocations, refresh, parent);
    }

    /**
     * @see org.springframework.context.support.ClassPathXmlApplicationContext#ClassPathXmlApplicationContext(java.lang.String,java.lang.Class)
     */
    public ClassPathXmlApplicationContext(String path, Class clazz) throws BeansException {
        super(path, clazz);
    }

    /**
     * @see org.springframework.context.support.ClassPathXmlApplicationContext#ClassPathXmlApplicationContext(java.lang.String[],java.lang.Class)
     */
    public ClassPathXmlApplicationContext(String[] paths, Class clazz) throws BeansException {
        super(paths, clazz);
    }

    /**
     * @see org.springframework.context.support.ClassPathXmlApplicationContext#ClassPathXmlApplicationContext(java.lang.String[],java.lang.Class,org.springframework.context.ApplicationContext)
     */
    public ClassPathXmlApplicationContext(String[] paths, Class clazz, ApplicationContext parent) throws BeansException {
        super(paths, clazz, parent);
    }
}