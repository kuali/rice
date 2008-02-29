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
package org.kuali.rice.config;

import javax.xml.namespace.QName;

import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.resourceloader.SpringResourceLoader;

/**
 * ModuleConfigurer that supplies a Spring-based ResourceLoader.  By default the
 * standard spring resource is (MODULE)SpringBeans.xml, the test resource is
 * (MODULE)SpringBeans-test.xml, and the resource loader name is (MODULE)_SPRING_RESOURCE_LOADER.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SpringModuleConfigurer extends BaseModuleConfigurer {
    /**
     * The resource path of the Spring context to use (when not in test mode)
     */
    protected final String springResource;
    /**
     * The resource path of the Spring context to use in test
     */
    protected final String springResourceTest;
    /**
     * The name of this resource loader
     */
    protected final String resourceLoaderName;

    /* helper methods for constructors */
    private static final String getDefaultSpringBeansPath(String moduleName) {
        return moduleName.toUpperCase() + "SpringBeans.xml"; 
    }
    private static final String getDefaultSpringBeansTestPath(String moduleName) {
        return moduleName.toUpperCase() + "SpringBeans-test.xml";
    }
    public static final String getDefaultResourceLoaderName(String moduleName) {
        return moduleName.toUpperCase() + "_SPRING_RESOURCE_LOADER";        
    }
    public static final QName getDefaultResourceLoaderQName(String moduleName) {
        return new QName(Core.getCurrentContextConfig().getMessageEntity(), getDefaultResourceLoaderName(moduleName));
    }

    /**
     * This constructs a SpringModuleConfigurer, with default standard and test Spring context resources,
     * (MODULE)SpringBeans.xml, and (MODULE)SpringBeans-test.xml
     * @param moduleName the module name
     */
    public SpringModuleConfigurer(String moduleName) {
        this(moduleName,
             getDefaultResourceLoaderName(moduleName),
             getDefaultSpringBeansPath(moduleName),
             getDefaultSpringBeansTestPath(moduleName));
    }

    /**
     * Constructs a SpringModuleConfigurer with default context resources but custom resource loader name.
     * @param moduleName the module name
     * @param resourceLoaderName the resource loader name
     */
    public SpringModuleConfigurer(String moduleName, String resourceLoaderName) {
        this(moduleName,
             resourceLoaderName,
             getDefaultSpringBeansPath(moduleName),
             getDefaultSpringBeansTestPath(moduleName));
        
    }

    /**
     * Constructs a SpringModuleConfigurer with specified resource loader name and spring resource paths
     * @param moduleName the module name
     * @param resourceLoaderName the resource loader name
     */
    public SpringModuleConfigurer(String moduleName, String resourceLoaderName, String springResource, String testSpringResource) {
        super(moduleName);
        this.resourceLoaderName = resourceLoaderName;
        this.springResource = springResource;
        this.springResourceTest = testSpringResource;
    }

    /**
     * Constructs a SpringResourceLoader from the appropriate Spring context resource and with the configured
     * resource loader name (and current context config message entity)
     * @see org.kuali.rice.config.BaseModuleConfigurer#createResourceLoader()
     */
    @Override
    protected ResourceLoader createResourceLoader() {
        String context;
        if (isTestMode()) {
            context = springResourceTest;
        } else {
            context = springResource;
        }

        ResourceLoader resourceLoader = new SpringResourceLoader(new QName(Core.getCurrentContextConfig().getMessageEntity(), resourceLoaderName), context);

        return resourceLoader;
    }
}