/*
 * Copyright 2007 The Kuali Foundation
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

import javax.xml.namespace.QName;

import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.BaseLifecycle;

public class RootResourceLoaderLifecycle extends BaseLifecycle {

	private ResourceLoader rootResourceLoader;

	public RootResourceLoaderLifecycle(ResourceLoader rootResourceLoader) {
		this.rootResourceLoader = rootResourceLoader;
	}

	@Override
	public void start() throws Exception {
		ResourceLoaderContainer container = new ResourceLoaderContainer(new QName(getMessageEntity(), RiceConstants.ROOT_RESOURCE_LOADER_CONTAINER_NAME));
		if (this.rootResourceLoader == null) {
		    this.rootResourceLoader = new BaseResourceLoader(new QName(getMessageEntity(), RiceConstants.DEFAULT_ROOT_RESOURCE_LOADER_NAME));
		}
		container.addResourceLoader(this.rootResourceLoader);
		GlobalResourceLoader.addResourceLoaderFirst(container);
		GlobalResourceLoader.start();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
	}

	protected String getMessageEntity() {
		return Core.getCurrentContextConfig().getMessageEntity();
	}



}
