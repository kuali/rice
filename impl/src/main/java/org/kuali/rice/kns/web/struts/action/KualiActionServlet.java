/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.web.struts.action;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionServlet;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.ModuleConfigurer;
import org.kuali.rice.core.config.RiceConfigurer;
import org.kuali.rice.core.util.RiceConstants;

public class KualiActionServlet extends ActionServlet {
    private static final Logger LOG = Logger.getLogger(KualiActionServlet.class);

    /**
     * <p>Initialize other global characteristics of the controller servlet.</p>
     * Overridden to remove the ConvertUtils.deregister() command that caused problems 
     * with the concurrent data dictionary load.  (KULRNE-4405)
     * 
     * @exception ServletException if we cannot initialize these resources
     */
    protected void initOther() throws ServletException {

        String value = null;
        value = getServletConfig().getInitParameter("config");
        if (value != null) {
            config = value;
        }

        // Backwards compatibility for form beans of Java wrapper classes
        // Set to true for strict Struts 1.0 compatibility
        value = getServletConfig().getInitParameter("convertNull");
        if ("true".equalsIgnoreCase(value)
            || "yes".equalsIgnoreCase(value)
            || "on".equalsIgnoreCase(value)
            || "y".equalsIgnoreCase(value)
            || "1".equalsIgnoreCase(value)) {

            convertNull = true;
        }

        if (convertNull) {
            ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
            ConvertUtils.register(new BigIntegerConverter(null), BigInteger.class);
            ConvertUtils.register(new BooleanConverter(null), Boolean.class);
            ConvertUtils.register(new ByteConverter(null), Byte.class);
            ConvertUtils.register(new CharacterConverter(null), Character.class);
            ConvertUtils.register(new DoubleConverter(null), Double.class);
            ConvertUtils.register(new FloatConverter(null), Float.class);
            ConvertUtils.register(new IntegerConverter(null), Integer.class);
            ConvertUtils.register(new LongConverter(null), Long.class);
            ConvertUtils.register(new ShortConverter(null), Short.class);
        }

    }
    
    KualiActionServletConfig serverConfigOverride = null;
    
    @Override
    public ServletConfig getServletConfig() {
        if ( serverConfigOverride == null ) {
            ServletConfig config = super.getServletConfig();
            
            if ( config == null ) {
                return null;
            } else {
                serverConfigOverride = new KualiActionServletConfig(config);
            }
        }
        return serverConfigOverride;
    }

    /**
     * A custom ServletConfig implementation which dynamically includes web content based on the installed modules in the RiceConfigurer object.
     *   Accomplishes this by implementing custom
     * {@link #getInitParameter(String)} and {@link #getInitParameterNames()} methods.
     */
    private class KualiActionServletConfig implements ServletConfig {

        private ServletConfig wrapped;
        private Map<String,String> initParameters = new HashMap<String, String>();
        
        @SuppressWarnings("deprecation")
        public KualiActionServletConfig(ServletConfig wrapped) {
            this.wrapped = wrapped;
            // copy out all the init parameters so they can be augmented
            Enumeration<String> initParameterNames = wrapped.getInitParameterNames();
            while ( initParameterNames.hasMoreElements() ) {
                String paramName = initParameterNames.nextElement();
                initParameters.put( paramName, wrapped.getInitParameter(paramName) );
            }
            // loop over the installed modules, adding their struts configuration to the servlet
            // if they have a web interface
            RiceConfigurer rice = (RiceConfigurer)ConfigContext.getCurrentContextConfig().getObject( RiceConstants.RICE_CONFIGURER_CONFIG_NAME );
            if ( LOG.isInfoEnabled() ) {
            	LOG.info( "Configuring init parameters of the KualiActionServlet from RiceConfigurer: " + rice );
            }
            for ( ModuleConfigurer module : rice.getModules() ) {
                // only install the web configuration if the module has web content
                // and it is running in a "local" mode
                // in "embedded" or "remote" modes, the UIs are hosted on a central server
                if ( module.shouldRenderWebInterface() ) {
                	if ( LOG.isInfoEnabled() ) {
                		LOG.info( "Configuring Web Content for Module: " + module.getModuleName() 
                				+ " / " + module.getWebModuleConfigName() 
                				+ " / " + module.getWebModuleConfigurationFiles()
                				+ " / Base URL: " + module.getWebModuleBaseUrl() );
                	}
                    if ( !initParameters.containsKey( module.getWebModuleConfigName() ) ) {
                        initParameters.put( module.getWebModuleConfigName(), module.getWebModuleConfigurationFiles() );
                        // hack for EN web content
                        if ( module.getModuleName().equals( "KEW" ) ) {
                            initParameters.put( module.getWebModuleConfigName().replace("kew", "en"), module.getWebModuleConfigurationFiles().replace("kew", "en") );
                        }
                    }
                }
            }
        }
        
        public String getInitParameter(String name) {
            return initParameters.get(name);
        }
        
        @SuppressWarnings("unchecked")
		public Enumeration<String> getInitParameterNames() {
            return new IteratorEnumeration( initParameters.keySet().iterator() );
        }
        
        public ServletContext getServletContext() {
            return wrapped.getServletContext();
        }
        public String getServletName() {
            return wrapped.getServletName();
        }
    }

}
