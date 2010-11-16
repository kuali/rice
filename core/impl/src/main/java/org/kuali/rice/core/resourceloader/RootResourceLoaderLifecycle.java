/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.core.resourceloader;

import javax.xml.namespace.QName;

import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.lifecycle.BaseLifecycle;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.util.RiceConstants;

public class RootResourceLoaderLifecycle extends BaseLifecycle {

	private ResourceLoader rootResourceLoader;

	public RootResourceLoaderLifecycle(ResourceLoader rootResourceLoader) {
		this.rootResourceLoader = rootResourceLoader;
	}

	@Override
	public void start() throws Exception {
		ResourceLoaderContainer container = new ResourceLoaderContainer(new QName(getServiceNamespace(), RiceConstants.ROOT_RESOURCE_LOADER_CONTAINER_NAME));
		if (this.rootResourceLoader == null) {
		    this.rootResourceLoader = new BaseResourceLoader(new QName(getServiceNamespace(), RiceConstants.DEFAULT_ROOT_RESOURCE_LOADER_NAME));
		}
		container.addResourceLoader(this.rootResourceLoader);
		GlobalResourceLoader.addResourceLoader(container);
		GlobalResourceLoader.start();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
	}

	protected String getServiceNamespace() {
		return ConfigContext.getCurrentContextConfig().getServiceNamespace();
	}



}
