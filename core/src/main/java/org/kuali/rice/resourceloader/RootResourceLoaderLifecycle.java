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
