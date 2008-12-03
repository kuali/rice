/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package org.kuali.rice.core.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.util.ReflectionUtils;

/**
 * This bean postprocessor initializes fields which are marked with the {@link RiceService} annotation
 * and are null after property injection and prior to init call, with a named Rice service obtained
 * from a specified (or global) resource loader. 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
// some of the implementation derived from PersistenceAnnotationBeanPostProcessor which is an
// example of an InstantiationAwareBeanPostProcessorAdapter
// http://fisheye1.atlassian.com/browse/springframework/spring/tiger/src/org/springframework/orm/jpa/spi/PersistenceAnnotationBeanPostProcessor.java?r=1.1
public class GRLServiceInjectionPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {
    private static final Logger LOG = Logger.getLogger(GRLServiceInjectionPostProcessor.class);

    // holds cached Class metadata
    private Map<Class<?>, List<AnnotatedMember>> classMetadata = new HashMap<Class<?>, List<AnnotatedMember>>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        List<AnnotatedMember> metadata = findClassMetadata(bean.getClass());
        for (AnnotatedMember member: metadata) {
            Object value = member.read(bean);
            if (value == null) { // only look up the service if the member value is null
                Object newValue = lookupRiceService(member.service);
                if (newValue != null) {
                    // if it is non-null, then inject it
                    member.inject(bean, newValue);
                }
            }
        }
        return super.postProcessBeforeInitialization(bean, beanName);
    }
    
    // Resolve the object against the application context
    protected Object lookupRiceService(RiceService annotation) {
        String resourceLoader = annotation.resourceLoader();
        String name = annotation.name();
        
        ResourceLoader rl;
        // null is not a constant expression as far as Java and annotations go
        // so we have to rely on the default of an empty string for resource loader name
        if (StringUtils.isEmpty(resourceLoader)) {
            LOG.error("Using global resource loader");
            rl = GlobalResourceLoader.getResourceLoader();
            if (rl == null) {
                // ...not so good
                throw new RuntimeException("Global resource loader could not be obtained");
            }
        } else {
            QName rlName = QName.valueOf(resourceLoader);

            if (StringUtils.isBlank(rlName.getNamespaceURI())) {
                // if they don't specify a namespace in the string just use the "current context" namespace
                rlName = new QName(ConfigContext.getCurrentContextConfig().getServiceNamespace(), rlName.getLocalPart());
            }
            rl = GlobalResourceLoader.getResourceLoader(rlName);
            if (rl == null) {
                throw new RuntimeException("Named resource loader not found: " + resourceLoader);
            }
        }

        LOG.error("Looking up service for injection: " + name);
        return rl.getService(QName.valueOf(name));
    }

    /**
     * Helper method to scan the properties of the bean and find members that need service injection. 
     * 
     * @param clazz the bean class
     * @return list of {@link RiceService}-annotated members
     */
    private synchronized List<AnnotatedMember> findClassMetadata(Class<? extends Object> clazz) {
        List<AnnotatedMember> metadata = classMetadata.get(clazz);
        if (metadata == null) {
            final List<AnnotatedMember> newMetadata = new LinkedList<AnnotatedMember>();

            ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
                public void doWith(Field f) throws IllegalArgumentException, IllegalAccessException {
                    addIfPresent(newMetadata, f);
                }
            });
        
            // TODO is it correct to walk up the hierarchy for methods? Otherwise inheritance
            // is implied? CL to resolve
            ReflectionUtils.doWithMethods(clazz, new ReflectionUtils.MethodCallback() {
                public void doWith(Method m) throws IllegalArgumentException, IllegalAccessException {
                    addIfPresent(newMetadata, m);
                }
            });
            
            metadata = newMetadata;
            classMetadata.put(clazz, metadata);
        }
        return metadata;
    }
    
    /**
     * Adds an AnnotatedMember for the member if it is actually annotated.
     * 
     * @param metadata the class metadata
     * @param ao the particular member (field or method)
     */
    private void addIfPresent(List<AnnotatedMember> metadata, AccessibleObject ao) {
        RiceService annotation = ao.getAnnotation(RiceService.class);
        if (annotation != null) {
            metadata.add(new AnnotatedMember(ao, annotation));
        }
    }

    /**
     * Class representing Rice service injection information about an annotated field.
     */
    private final class AnnotatedMember {
        private final AccessibleObject member;
        private final RiceService service;
        
        public AnnotatedMember(AccessibleObject member, RiceService service) {
            this.member = member;
            this.service = service;

            if (service.name() == null) {
                throw new IllegalArgumentException("Service name must be specified in RiceService annotation");
            }
        }

        public Object read(Object instance) {
            // I'm sure some handy utility class or code exists to easily set properties
            // but I couldn't find it quickly, so it's straight up reflection for now
            // working from model in inject
            try {
                if (!member.isAccessible()) {
                    member.setAccessible(true);
                }
                if (member instanceof Field) {
                    return ((Field) member).get(instance);
                }
                else if (member instanceof Method) {
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod((Method) member);
                    if (pd == null) {
                        throw new IllegalArgumentException("Annotated was found on a method that did not resolve to a bean property: " + member);
                    }
                    Method getter = pd.getReadMethod();
                    if (getter == null) {
                        // we still want to support setter-only properties, so if there is no getter...then
                        // just assume the value is null and that we should set it
                        return null;
                        //throw new IllegalArgumentException("No getter found for property " + pd.getName());
                    }
                    return getter.invoke(instance, (Object[]) null); // it had better be a no-arg getter
                }
                else {
                    throw new IllegalArgumentException("Cannot read unknown AccessibleObject type " + member);
                }
            }
            catch (IllegalAccessException ex) {
                throw new IllegalArgumentException("Cannot inject member " + member, ex);
            }
            catch (InvocationTargetException ex) {
                // Method threw an exception
                throw new IllegalArgumentException("Attempt to inject setter method " + member +
                                                   " resulted in an exception", ex);
            }
        }

        public void inject(Object instance, Object value) {
            try {
                if (!member.isAccessible()) {
                    member.setAccessible(true);
                }
                if (member instanceof Field) {
                    ((Field) member).set(instance, value);
                }
                else if (member instanceof Method) {
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod((Method) member);
                    if (pd == null) {
                        throw new IllegalArgumentException("Annotated was found on a method that did not resolve to a bean property: " + member);
                    }
                    Method setter = pd.getWriteMethod();
                    if (setter == null) {
                        throw new IllegalArgumentException("No setter found for property " + pd.getName());
                    }
                    setter.invoke(instance, value); // it had better be a single-arg setter
                }
                else {
                    throw new IllegalArgumentException("Cannot inject unknown AccessibleObject type " + member);
                }
            }
            catch (IllegalAccessException ex) {
                throw new IllegalArgumentException("Cannot inject member " + member, ex);
            }
            catch (InvocationTargetException ex) {
                // Method threw an exception
                throw new IllegalArgumentException("Attempt to inject setter method " + member +
                                                   " resulted in an exception", ex);
            }
        }
    }
}
