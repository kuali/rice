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
package org.kuali.rice.resourceloader.support;

import javax.xml.namespace.QName;

import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Exports services in the {@link GlobalResourceLoader} as beans available to Spring.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ResourceLoaderServiceFactoryBean implements BeanNameAware, FactoryBean, InitializingBean {
    /**
     * The service to retrieve.  If unset, defaults to bean name.
     */
    private String serviceName;
    /**
     * The KSB namespace in which to look for the service, default is null, which is 'local' services
     */
    private String namespace;
    /**
     * Whether we provide a singleton.  Since we cannot make that guarantee, the default is 'false'
     */
    private boolean singleton;
    /**
     * The resource loader in which to look up the service
     */
    private ResourceLoader resourceLoader;

    /**
     * @return the configured service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the service name to retrieve from the ResourceLoader
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    /**
     * @return the namespace under which to look up the service/bean
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace under which to look up the service/bean
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * @param singleton whether we know that the target bean will be a singleton
     */
    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    /**
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {
        return singleton;
    }

    /**
     * @param resourceLoader the ResourceLoader in which to look up the service
     */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * @return the ResourceLoader with which this factory bean has been configured
     */
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
    
    /**
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String name) {
        // by default the service name is just the bean name
        this.serviceName = name;
    }

    public void afterPropertiesSet() throws Exception {
        if (this.getServiceName() == null) {
            throw new ConfigurationException("No serviceName given.");
        }
        if (this.resourceLoader == null) {
            this.resourceLoader = GlobalResourceLoader.getResourceLoader();
        }
    }

    /**
     * Constructs a QName from the defined namespace and beanName
     * @param beanName the bean name to look up
     * @return a QName with beanName qualified with namespace
     */
    protected QName getQName(String beanName) {
        return new QName(namespace, beanName);
    }

    /**
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public Object getObject() throws Exception {
        return resourceLoader.getService(getQName(this.getServiceName()));
    }

    /**
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class getObjectType() {
        // we cannot know the type before lookup, so return null as per contract
        return null;
    }
}