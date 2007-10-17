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
package edu.iu.uis.eden.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.lang.ClassUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.util.ClassLoaderUtils;

/**
 * A {@link BeanPostProcessor} which is used to allow for service overrides from the
 * Institutional Plugin.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ServiceOverrideAutoProxyCreator implements BeanPostProcessor {

	private String[] beanNames;
	
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (!isMatch(beanName)) {
			return bean;
		}
		try {
			return createOverrideProxy(beanName, bean);
		} catch (NoSuchMethodException e) {
			throw new WorkflowRuntimeException("Failed to instantiate service override proxy for bean '" + beanName + "'.");
		}
	}

	public void setBeanNames(String[] beanNames) {
		this.beanNames = beanNames;
	}
	
	protected boolean isMatch(String beanName) {
		for(String validBeanName : beanNames) {
			if (beanName.equals(validBeanName)) {
				return true;
			}
		}
		return false;
	}
	
	protected Object createOverrideProxy(String beanName, Object bean) throws NoSuchMethodException {
		InvocationHandler handler = new ServiceOverrideProxy(new QName(beanName), bean);
		return Proxy.newProxyInstance(ClassLoaderUtils.getDefaultClassLoader(), getInterfacesToProxy(bean), handler); 
	}

	@SuppressWarnings("unchecked")
	protected static Class[] getInterfacesToProxy(Object proxiedObject) {
    	List interfaces = ClassUtils.getAllInterfaces(proxiedObject.getClass());
    	// introduce the OverridableService interface into the mix
    	interfaces.add(OverridableService.class);
    	Class[] interfaceArray = new Class[interfaces.size()];
    	return (Class[]) interfaces.toArray(interfaceArray);
    }
	
}