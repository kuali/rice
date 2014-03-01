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
package org.kuali.rice.krad.uif.freemarker;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.core.InlineTemplateElement;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateException;

/**
 * Register inline template processing adaptors for high-traffic KRAD templates.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FreeMarkerInlineRenderBootstrap implements InitializingBean, ApplicationContextAware, ServletContextAware {

    private static final Logger LOG = Logger.getLogger(FreeMarkerInlineRenderBootstrap.class);
    
    /**
     * The freemarker configuration.
     */
    private static Configuration freeMarkerConfig;

    /**
     * The application context.
     */
    private static ApplicationContext applicationContext;
    
    /**
     * The servlet context.
     */
    private static ServletContext servletContext;

    /**
     * The tablib factory for use in the component rendering phase.
     */
    private static TaglibFactory taglibFactory;

    /**
     * The object wrapper for use in the component rendering phase.
     */
    private static ObjectWrapper objectWrapper;

    /**
     * Servlet context hash model for use in the component rendering phase.
     */
    private static ServletContextHashModel servletContextHashModel;
    
    /**
     * Get the FreeMarker configuration initialized for the current KRAD application. 
     * 
     * @return The FreeMarker configuration initialized for the current KRAD application.
     */
    public static Configuration getFreeMarkerConfig() {
        if (freeMarkerConfig == null) {
            throw new IllegalStateException("FreeMarker configuruation is not available, "
                    + "use krad-base-servlet.xml or define FreeMarkerInlineRenderBootstrap in servlet.xml");
        }
        
        return freeMarkerConfig;
    }

    /**
     * Get the servlet context initialized for the current KRAD application. 
     * 
     * @return The servlet context initialized for the current KRAD application.
     */
    public static ServletContext getServletContext() {
        if (servletContext == null) {
            throw new IllegalStateException("Servlet context is not available, "
                    + "use krad-base-servlet.xml or define FreeMarkerInlineRenderBootstrap in servlet.xml");
        }
        
        return servletContext;
    }

    /**
     * Get the tablib factory for use in the component rendering phase.
     * 
     * @return The tablib factory for use in the component rendering phase.
     */
    public static TaglibFactory getTaglibFactory() {
        return taglibFactory;
    }

    /**
     * Get the object wrapper for use in the component rendering phase.
     * 
     * @return The object wrapper for use in the component rendering phase.
     */
    public static ObjectWrapper getObjectWrapper() {
        return objectWrapper;
    }

    /**
     * Get the servlet context hash model for use in the component rendering phase.
     * 
     * @return The servlet context hash model for use in the component rendering phase.
     */
    public static ServletContextHashModel getServletContextHashModel() {
        return servletContextHashModel;
    }

    /**
     * Needed for JSP access in FreeMarker.
     * 
     * <p>Derived from Spring FreeMarkerView.</p>
     */
    private static class ServletAdapter extends GenericServlet {

        private static final long serialVersionUID = 8509364718276109450L;

        @Override
        public void service(ServletRequest servletRequest, ServletResponse servletResponse) {}
        
    }

    /**
     * Internal implementation of the {@link ServletConfig} interface,
     * to be passed to the servlet adapter.
     * 
     * <p>Derived from Spring FreeMarkerView.</p>
     */
    private static class DelegatingServletConfig implements ServletConfig {

        public String getServletName() {
            return applicationContext.getDisplayName();
        }

        public ServletContext getServletContext() {
            return servletContext;
        }

        public String getInitParameter(String paramName) {
            return null;
        }

        public Enumeration<String> getInitParameterNames() {
            return Collections.enumeration(new HashSet<String>());
        }
    }

    /**
     * Initialize FreeMarker elements after servlet context and FreeMarker configuration have both
     * been populated.
     */
    private static void finishConfig() {
        if (freeMarkerConfig != null && servletContext != null) {
            taglibFactory = new TaglibFactory(servletContext);
            
            objectWrapper = freeMarkerConfig.getObjectWrapper();
            if (objectWrapper == null) {
                objectWrapper = ObjectWrapper.DEFAULT_WRAPPER;
            }

            GenericServlet servlet = new ServletAdapter();
            try {
                servlet.init(new DelegatingServletConfig());
            } catch (ServletException ex) {
                throw new BeanInitializationException("Initialization of GenericServlet adapter failed", ex);
            }
            
            servletContextHashModel = new ServletContextHashModel(servlet, ObjectWrapper.DEFAULT_WRAPPER);
            
            LOG.info("Freemarker configuration complete");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            freeMarkerConfig = ((FreeMarkerConfigurer) applicationContext.getBean("freemarkerConfig"))
                    .createConfiguration();
            LOG.info("Set freemarker bootstrap " + freeMarkerConfig);
        } catch (IOException e) {
            throw new IllegalStateException("Error loading freemarker configuration", e);
        } catch (TemplateException e) {
            throw new IllegalStateException("Error loading freemarker configuration", e);
        }
        finishConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        FreeMarkerInlineRenderBootstrap.servletContext = servletContext;
        finishConfig();
    }

    /**
     * Register high-traffic KRAD template adaptors.  
     * 
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        InlineTemplateElement.registerAdaptor("script", new FreeMarkerScriptAdaptor());
        InlineTemplateElement.registerAdaptor("template", new FreeMarkerTemplateAdaptor());
        InlineTemplateElement.registerAdaptor("collectionGroup", new FreeMarkerCollectionGroupAdaptor());
        InlineTemplateElement.registerAdaptor("stacked", new FreeMarkerStackedAdaptor());
        InlineTemplateElement.registerAdaptor("groupWrap-open", new FreeMarkerOpenGroupWrapAdaptor());
        InlineTemplateElement.registerAdaptor("groupWrap-close", new FreeMarkerCloseGroupWrapAdaptor());
    }

}
