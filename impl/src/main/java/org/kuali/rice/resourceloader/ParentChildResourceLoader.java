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

import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.definition.ObjectDefinition;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ParentChildResourceLoader extends BaseResourceLoader {

    private ResourceLoader parent;
    private ResourceLoader child;

    public ParentChildResourceLoader(ResourceLoader parent, ResourceLoader child) {
	super(new QName(child.getName().toString() + " to parent " + parent.getName().toString()));
	this.parent = parent;
	this.child = child;
    }

    public Object getObject(ObjectDefinition definition) {
	Object object = child.getObject(definition);
	if (object == null) {
	    object = parent.getObject(definition);
	}
	return object;
    }

    public Object getService(QName qname) {
	Object service = child.getService(qname);
	if (service == null) {
	    service = parent.getService(qname);
	}
	return service;
    }

    public void start() throws Exception {
	// just start the child, it will be assumed that parent will be started from it's context
	if (!child.isStarted()) {
	    child.start();
	}
	super.start();
    }

    public void stop() throws Exception {
	child.stop();
	super.stop();
    }

    public void addResourceLoader(ResourceLoader resourceLoader) {
	this.child.addResourceLoader(resourceLoader);
    }

    public void addResourceLoaderFirst(ResourceLoader resourceLoader) {
	this.child.addResourceLoaderFirst(resourceLoader);
    }

    public ResourceLoader getResourceLoader(QName name) {
	return this.child.getResourceLoader(name);
    }

    public List<QName> getResourceLoaderNames() {
	return this.child.getResourceLoaderNames();
    }

    public List<ResourceLoader> getResourceLoaders() {
	return this.child.getResourceLoaders();
    }

    public void removeResourceLoader(QName name) {
	this.child.removeResourceLoader(name);
    }



}
