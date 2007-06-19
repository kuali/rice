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
package edu.iu.uis.eden.messaging;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * Encapsulates an asynchronous call to a service.
 * 
 * @author rkirkend
 */
public class AsynchronousCall implements Serializable {

	private static final long serialVersionUID = -1036656564567726747L;

	private Object[] arguments;

	private Class[] paramTypes;

	private ServiceInfo serviceInfo;

	private String methodName;

	private Serializable context;

	private AsynchronousCallback callback;

	private Long repeatCallTimeIncrement;
	
	private boolean ignoreStoreAndForward;

	public AsynchronousCall(Class[] paramTypes, Object[] arguments, ServiceInfo serviceInfo, String methodName, Serializable context, AsynchronousCallback callback, Long repeatCallTimeIncrement) {
		this.arguments = arguments;
		this.paramTypes = paramTypes;
		this.serviceInfo = serviceInfo;
		this.methodName = methodName;
		this.context = context;
		this.callback = callback;
		this.repeatCallTimeIncrement = repeatCallTimeIncrement;
	}

	public Object[] getArguments() {
		return this.arguments;
	}

	public Class[] getParamTypes() {
		return this.paramTypes;
	}

	public ServiceInfo getServiceInfo() {
		return this.serviceInfo;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public AsynchronousCallback getCallback() {
		return this.callback;
	}

	public String toString() {
		return "[AsynchronousCall: " + "serviceInfo=" + this.serviceInfo + ", methodName=" + this.methodName + ", context" + this.context + ", paramTypes=" + getStringifiedArray(this.paramTypes) + ", arguments=" + getStringifiedArray(this.arguments) + "]";
	}

	/**
	 * Takes an Object[] and returns a human-readable String of the contents
	 * Candidate for relocation to a utility class
	 * 
	 * @param array
	 *            the Object[]
	 * @return a human-readable String of the contents
	 */
	private static final String getStringifiedArray(Object[] array) {
		if (array == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer(array.getClass().toString());
		sb.append("[");
		StringUtils.join(array, ", ");
		sb.append("]");
		return sb.toString();
	}


	public Long getRepeatCallTimeIncrement() {
		return this.repeatCallTimeIncrement;
	}

	public void setRepeatCallTimeIncrement(Long repeatCallTimeIncrement) {
		this.repeatCallTimeIncrement = repeatCallTimeIncrement;
	}

	public boolean isIgnoreStoreAndForward() {
		return this.ignoreStoreAndForward;
	}

	public void setIgnoreStoreAndForward(boolean ignoreStoreAndForward) {
		this.ignoreStoreAndForward = ignoreStoreAndForward;
	}

	public Serializable getContext() {
		return this.context;
	}

	public void setContext(Serializable context) {
		this.context = context;
	}

}