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
package org.kuali.rice.core.config;

import javax.xml.namespace.QName;

import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.resourceloader.SpringResourceLoader;

/**
 * ModuleConfigurer that supplies a Spring-based ResourceLoader.  By default the
 * standard spring resource is (MODULE)SpringBeans.xml, the test resource is
 * (MODULE)SpringBeans-test.xml, and the resource loader name is (MODULE)_SPRING_RESOURCE_LOADER.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SpringModuleConfigurer extends BaseModuleConfigurer {
	
    /**
     * The name of this resource loader
     */
    protected final String resourceLoaderName;

    @Override
	public String getSpringFileLocations(){
    	return getDefaultSpringBeansPath(getConfigPackagePath(moduleName), moduleName);
	}
	
    /* helper methods for constructors */
    private static final String getConfigPackagePath(String moduleName) {
    	return "org/kuali/rice/" + moduleName.toLowerCase() + "/config/";
    }
    private static final String getDefaultSpringBeansPath(String configPackagePath, String moduleName) {
        return configPackagePath + moduleName.toUpperCase() + "SpringBeans.xml"; 
    }
    public static final String getDefaultResourceLoaderName(String moduleName) {
        return moduleName.toUpperCase() + "_SPRING_RESOURCE_LOADER";        
    }
    public static final QName getDefaultResourceLoaderQName(String moduleName) {
        return new QName(ConfigContext.getCurrentContextConfig().getMessageEntity(), getDefaultResourceLoaderName(moduleName));
    }

    /**
     * This constructs a SpringModuleConfigurer, with default standard and test Spring context resources,
     * (MODULE)SpringBeans.xml, and (MODULE)SpringBeans-test.xml
     * @param moduleName the module name
     */
    public SpringModuleConfigurer(String moduleName) {
        this(moduleName,
             getDefaultResourceLoaderName(moduleName));
    }
    
    /**
     * Constructs a SpringModuleConfigurer with default context resources but custom resource loader name.
     * @param moduleName the module name
     * @param resourceLoaderName the resource loader name
     */
    public SpringModuleConfigurer(String moduleName, String resourceLoaderName) {
    	super(moduleName);
    	this.resourceLoaderName = resourceLoaderName;
    }
    
    @Override
    public Config loadConfig(Config parentConfig) throws Exception {
        Config c = super.loadConfig(parentConfig);
        // check for test flag
        String s = c.getProperty("rice." + moduleName.toLowerCase() + ".testMode");

        if (Boolean.valueOf(s)) {
            testMode = true;
        }
        
        return c;
    }

    /**
     * Constructs a SpringResourceLoader from the appropriate Spring context resource and with the configured
     * resource loader name (and current context config message entity)
     * @see org.kuali.rice.core.config.BaseModuleConfigurer#createResourceLoader()
     */
    @Override
    protected ResourceLoader createResourceLoader() {
        String context = getSpringFileLocations();
        ResourceLoader resourceLoader = new SpringResourceLoader(new QName(ConfigContext.getCurrentContextConfig().getMessageEntity(), resourceLoaderName), context);
        return resourceLoader;
    }
    
}