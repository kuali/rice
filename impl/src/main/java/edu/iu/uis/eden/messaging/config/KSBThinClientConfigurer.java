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
package edu.iu.uis.eden.messaging.config;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ModuleConfigurer;
import org.kuali.rice.lifecycle.Lifecycle;

import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

/**
 * A configurer which starts up an alternate Spring file to configure some services that are needed in
 * "Thin Client" mode.  A "thin client" is a client which communicates directly with service
 * endpoints and doesn't use the registry or the message queue.  Esentially, all it uses the KSB for are
 * it's Digital Signature services.
 *
 * <p>TODO This is essentially a hack to allow for secure thin client mode of workflow to work properly
 * without requiring the entire service bus to be started (which requires datasources, etc.).  We need
 * to examine how better to support this sort of a concept in the future.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KSBThinClientConfigurer extends ModuleConfigurer {

	private static final Logger LOG = Logger.getLogger(KSBThinClientConfigurer.class);

	private boolean isStarted = false;

	public Config loadConfig(Config parentConfig) throws Exception {
	    return parentConfig;
	}

	@Override
	protected List<Lifecycle> loadLifecycles() throws Exception {
	    List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
	    lifecycles.add(KSBResourceLoaderFactory.createThinClientKSBResourceLoader());
	    return lifecycles;
	}

	public void stop() throws Exception {
	    this.isStarted = false;
	}

	public boolean isStarted() {
		return this.isStarted;
	}

	protected void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

}