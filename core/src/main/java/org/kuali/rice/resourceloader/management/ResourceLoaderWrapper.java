package org.kuali.rice.resourceloader.management;

import javax.xml.namespace.QName;

import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.ResourceLoader;


/**
 * A simple MBean which wraps a ResourceLoader to provide management functions.
 *
 * @author Eric Westfall
 */
public class ResourceLoaderWrapper implements ResourceLoaderWrapperMBean {

	private ResourceLoader resourceLoader;
	
	public ResourceLoaderWrapper(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	public String[] getChildren() {
		return this.resourceLoader.getResourceLoaderNames().toArray(new String[0]);
	}

	public String examineContents() {
		return this.resourceLoader.getContents("", true);
	}
	
	public void dumpContents() {
		System.out.println(examineContents());
	}

	public String getName() {
		return this.resourceLoader.getName().toString();
	}
	
	public void setName(String name) {
	    this.resourceLoader.setName(new QName(name, Core.getCurrentContextConfig().getMessageEntity()));
	}
	
	public String getType() {
		return this.resourceLoader.getClass().getName();
	}

	public boolean isStarted() {
		return this.resourceLoader.isStarted();
	}

	public void start() throws Exception {
	    this.resourceLoader.start();
	}

	public void stop() throws Exception {
	    this.resourceLoader.stop();
	}
	
	public boolean hasService(String namespaceUri, String localPart) {
		return this.resourceLoader.getService(new QName(namespaceUri, localPart)) != null;
	}

}
