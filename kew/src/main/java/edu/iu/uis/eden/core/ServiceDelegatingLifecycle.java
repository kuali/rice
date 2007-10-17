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
package edu.iu.uis.eden.core;

import javax.xml.namespace.QName;

import org.kuali.rice.lifecycle.BaseLifecycle;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

/**
 * A lifecycle that wraps a service.  This fetches and calls a lifecycle available
 * in the GRL and calls lifecycle methods on that.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ServiceDelegatingLifecycle extends BaseLifecycle {

	private QName serviceName;

	public ServiceDelegatingLifecycle(QName serviceName) {
		this.serviceName = serviceName;
	}

	public ServiceDelegatingLifecycle(String serviceName) {
		this(new QName(serviceName));
	}

	public void start() throws Exception {
		((Lifecycle)GlobalResourceLoader.getService(serviceName)).start();
		super.start();
	}

	public void stop() throws Exception {
		((Lifecycle)GlobalResourceLoader.getService(serviceName)).stop();
		super.stop();
	}

}
