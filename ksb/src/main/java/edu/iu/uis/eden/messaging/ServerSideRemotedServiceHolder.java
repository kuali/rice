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

/**
 *
 * @author rkirkend
 */
public class ServerSideRemotedServiceHolder extends RemotedServiceHolder {

	private Object injectedPojo;
	
	/**
	 * @param service
	 * @param entry
	 */
	public ServerSideRemotedServiceHolder(Object service, Object injectedPojo, ServiceInfo entry) {
		super(service, entry);
		this.setInjectedPojo(injectedPojo);
	}

	public Object getInjectedPojo() {
		return this.injectedPojo;
	}

	public void setInjectedPojo(Object injectedPojo) {
		this.injectedPojo = injectedPojo;
	}
}