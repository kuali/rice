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
package org.kuali.rice.resourceloader;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A simple {@link ResourceLoader} which wraps a Spring {@link ConfigurableApplicationContext}.
 *
 * Starts and stops the {@link ConfigurableApplicationContext}.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SpringResourceLoader extends BaseResourceLoader {

	private static final Logger LOG = Logger.getLogger(SpringResourceLoader.class);

	private ConfigurableApplicationContext context;

	private String fileLoc;

	public SpringResourceLoader(QName name, String fileLoc) {
		super(name);
		this.fileLoc = fileLoc;
	}

	public Object getService(QName serviceName) {
	    	if (!isStarted()) {
	    	    return null;
	    	}
		if (this.getContext().containsBean(serviceName.toString())) {
			Object service = this.getContext().getBean(serviceName.toString());
			return postProcessService(serviceName, service);
		}
		return super.getService(serviceName);
	}

	@Override
	public void start() throws Exception {
		LOG.info("Creating Spring context " + this.fileLoc);
		this.context = new ClassPathXmlApplicationContext(this.fileLoc);
		super.start();
	}

	@Override
	public void stop() throws Exception {
		LOG.info("Stoping Spring context " + this.fileLoc);
		this.context.close();
		super.stop();
	}

	public ConfigurableApplicationContext getContext() {
		return this.context;
	}

}
