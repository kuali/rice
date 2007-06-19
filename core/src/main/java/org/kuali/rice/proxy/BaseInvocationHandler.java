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
package org.kuali.rice.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.kuali.rice.util.ExceptionUtils;

/**
 * An abstract base class for InvocationHanlders which can be used to implement
 * an InvocationHandler that delegates hashCode and equals methods to the proxied
 *
 * @author ewestfal
 */
public abstract class BaseInvocationHandler implements InvocationHandler {

	// preloaded Method objects for the methods in java.lang.Object
    private static Method hashCodeMethod;
    private static Method equalsMethod;
    static {
	try {
	    hashCodeMethod = Object.class.getMethod("hashCode", (Class[])null);
	    equalsMethod = Object.class.getMethod("equals", new Class[] { Object.class });
	    } catch (NoSuchMethodException e) {
	    	throw new NoSuchMethodError(e.getMessage());
	    }
    }

	public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
		Class declaringClass = method.getDeclaringClass();
		if (declaringClass == Object.class) {
		    if (method.equals(hashCodeMethod)) {
		    	return proxyHashCode(proxy);
		    } else if (method.equals(equalsMethod)) {
		    	return proxyEquals(proxy, arguments[0]);
		    } /*else if (m.equals(toStringMethod)) {
			return proxyToString(proxy);
		    } else {
			throw new InternalError(
			    "unexpected Object method dispatched: " + m);
		    }*/
		}
		try {
			return invokeInternal(proxy, method, arguments);
		} catch (Throwable t) {
			throw ExceptionUtils.unwrapActualCause(t);
		}
	}

	protected abstract Object invokeInternal(Object proxy, Method method, Object[] arguments) throws Throwable;

	protected Integer proxyHashCode(Object proxy) {
		return new Integer(System.identityHashCode(proxy));
	}

	protected Boolean proxyEquals(Object proxy, Object other) {
		return (proxy == other ? Boolean.TRUE : Boolean.FALSE);
	}

}
