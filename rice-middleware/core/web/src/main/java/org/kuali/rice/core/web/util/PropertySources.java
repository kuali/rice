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
package org.kuali.rice.core.web.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.springframework.beans.factory.BeanFactoryUtils.beansOfTypeIncludingAncestors;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * Utility class for dealing with {@link PropertySource}s.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PropertySources {

    private static final Logger logger = LoggerFactory.getLogger(PropertySources.class);

    /**
     * Check system properties and servlet context init params for an annotated Spring configuration
     * class located under {@code key}. If present, create a new
     * {@AnnotationConfigWebApplicationContext} and register
     * the annotated configuration class. Refresh the context and then examine it for a
     * {@code PropertySource<?>} bean. There must be exactly one {@code PropertySource<?>} bean
     * present in the application context.
     */
    public static Optional<PropertySource<?>> getPropertySource(ServletContextEvent sce, String key) {
        Optional<String> annotatedClass = getProperty(sce, key);
        if (annotatedClass.isPresent()) {
            AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
            context.setServletContext(sce.getServletContext());
            PropertySource<?> propertySource = getPropertySource(annotatedClass.get(), context);
            return Optional.<PropertySource<?>> of(propertySource);
        } else {
            return Optional.absent();
        }
    }

    /**
     * Register {@code propertySource} as the first thing Spring will check when looking up property
     * values.
     */
    public static void addFirst(ConfigurableApplicationContext context, PropertySource<?> propertySource) {
        ConfigurableEnvironment env = context.getEnvironment();
        MutablePropertySources propertySources = env.getPropertySources();
        propertySources.addFirst(propertySource);
    }

    protected static PropertySource<?> getPropertySource(String className, AnnotationConfigWebApplicationContext context) {
        try {
            logger.info("Loading [{}] to setup a Spring property source", className);
            Class<?> annotatedClass = Class.forName(className);
            context.register(annotatedClass);
            context.refresh();
            PropertySource<?> propertySource = getPropertySource(context, annotatedClass);
            context.close();
            logger.info("Spring property source was successfully setup");
            return propertySource;
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected error configuring Spring property source", e);
        }
    }

    protected static PropertySource<?> getPropertySource(ListableBeanFactory factory, Class<?> annotatedClass) {
        @SuppressWarnings("rawtypes")
        Collection<PropertySource> collection = beansOfTypeIncludingAncestors(factory, PropertySource.class).values();
        checkSizeEqualsOne(collection, annotatedClass);
        return collection.iterator().next();
    }

    protected static void checkSizeEqualsOne(Collection<?> collection, Class<?> annotatedClass) {
        Object[] args = {annotatedClass.getCanonicalName(), collection.size()};
        String errorMessage = "[%s] contained %s property source beans.  There must always be exactly 1 property source bean";
        Preconditions.checkState(collection.size() == 1, errorMessage, args);
    }

    /**
     * Examine both system properties and servlet context init parameters for the presence of a
     * value under {@code key}.
     */
    public static Optional<String> getProperty(ServletContextEvent sce, String key) {
        checkNotNull(key, "'key' cannot be null");

        // If there is a system property value, use it
        String sys = System.getProperty(key);
        if (!StringUtils.isBlank(sys)) {
            logger.info("Found [{}] defined in system properties: [{}]", key, sys);
            return Optional.of(sys);
        }

        // If there is a servlet context value, use it. Unless it's blank or an unresolved placeholder
        String web = sce.getServletContext().getInitParameter(key);
        if (StringUtils.isBlank(web) || isPlaceHolder(web)) {
            return Optional.absent();
        } else {
            logger.info("Found [{}] defined in servlet context: [{}]", key, web);
            return Optional.of(web);
        }
    }

    /**
     * Convert {@code ServletContext} init parameters into a {@code Properties} object.
     */
    public static Properties convert(ServletContext context) {
        Properties properties = new Properties();
        @SuppressWarnings("unchecked")
        Enumeration<String> paramNames = context.getInitParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            properties.put(paramName, context.getInitParameter(paramName));
        }
        return properties;
    }

    protected static boolean isPlaceHolder(String value) {
        checkNotNull(value, "'value' cannot be null");
        return value.startsWith("${") && value.endsWith("}");
    }

}
