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
package org.kuali.rice.resourceloader;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.lang.ClassUtils;
import org.kuali.rice.definition.ObjectDefinition;

/**
 * A BaseResourceLoader implementation which wraps services with a Proxy that
 * switches the current context ClassLoader of the Thread.
 *
 * @author Eric Westfall
 */
public class BaseWrappingResourceLoader extends BaseResourceLoader {

	private static final String[] PACKAGES_TO_FILTER = new String[] { "org.springframework" };

	public BaseWrappingResourceLoader(QName name, ClassLoader classLoader, ServiceLocator serviceLocator) {
		super(name, classLoader, serviceLocator);
	}

	public BaseWrappingResourceLoader(QName name, ClassLoader classLoader) {
		super(name, classLoader);
	}

	public BaseWrappingResourceLoader(QName name, ServiceLocator serviceLocator) {
		super(name, serviceLocator);
	}

	public BaseWrappingResourceLoader(QName name) {
		super(name);
	}

	protected Object postProcessService(QName serviceName, Object service) {
		if (service != null && shouldWrapService(serviceName, service)) {
			return ContextClassLoaderProxy.wrap(service, getInterfacesToProxy(service), getClassLoader());
		}
		return service;
	}

	protected Object postProcessObject(ObjectDefinition definition, Object object) {
		if (object != null && shouldWrapObject(definition, object)) {
			return ContextClassLoaderProxy.wrap(object, getInterfacesToProxy(object), getClassLoader());
		}
		return object;
	}

	protected Class[] getInterfacesToProxy(Object object) {
		List interfaces = ClassUtils.getAllInterfaces(object.getClass());
		for (Iterator iterator = interfaces.iterator(); iterator.hasNext();) {
			Class objectInterface = (Class) iterator.next();
			for (String packageNames : getPackageNamesToFilter()) {
				if (objectInterface.getName().startsWith(packageNames)) {
					iterator.remove();
				}
			}
		}
		Class[] interfaceArray = new Class[interfaces.size()];
		return (Class[]) interfaces.toArray(interfaceArray);
	}

	protected String[] getPackageNamesToFilter() {
		return PACKAGES_TO_FILTER;
	}

	protected boolean shouldWrapService(QName serviceName, Object service) {
		return true;
	}

	protected boolean shouldWrapObject(ObjectDefinition definition, Object object) {
		return true;
	}

}