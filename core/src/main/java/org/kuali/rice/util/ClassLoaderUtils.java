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
package org.kuali.rice.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Provides common utility methods for dealing with Classloaders.
 * 
 * @author ewestfal
 */
public class ClassLoaderUtils {

	/**
	 * Returns the default class loader within the current context.  If there is a context classloader
	 * it is returned, otherwise the classloader which loaded the ClassLoaderUtil Class is returned.
	 * 
	 * @return the appropriate default classloader which is guaranteed to be non-null
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = ClassLoaderUtils.class.getClassLoader();
		}
		return classLoader;
	}
	
	/**
	 * Checks if the given object is an instance of the given class, unwrapping any proxies if
	 * necessary to get to the underlying object.
	 */
	public static boolean isInstanceOf(Object object, Class instanceClass) {
		if (object == null) {
			return false;
		}
		if (instanceClass.isInstance(object)) {
			return true;
		}
		return isInstanceOf(unwrapFromProxyOnce(object), instanceClass);
	}
	
	public static Object unwrapFromProxy(Object proxy) {
		Object unwrapped = unwrapFromProxyOnce(proxy);
		if (unwrapped == null) {
			return proxy;
		}
		return unwrapFromProxy(unwrapped);
	}
	
	/**
	 * Unwraps the underlying object from the given proxy (which may itself be a proxy).  If the
	 * given object is not a valid proxy, then null is returned.
	 */
	private static Object unwrapFromProxyOnce(Object proxy) {
		if (proxy != null && Proxy.isProxyClass(proxy.getClass())) {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxy);
			if (invocationHandler instanceof TargetedInvocationHandler) {
				return ((TargetedInvocationHandler)invocationHandler).getTarget();
			}
		}
		return null;
	}
	
}
