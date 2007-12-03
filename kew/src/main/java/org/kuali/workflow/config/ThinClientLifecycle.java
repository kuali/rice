/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.workflow.config;

import org.kuali.rice.lifecycle.BaseLifecycle;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ThinClientLifecycle extends BaseLifecycle {

	private ThinClientResourceLoader resourceLoader;

	@Override
	public void start() throws Exception {
		resourceLoader = new ThinClientResourceLoader();
		resourceLoader.start();
		GlobalResourceLoader.addResourceLoaderFirst(resourceLoader);
		super.start();
	}

	@Override
	public void stop() throws Exception {
		if (resourceLoader != null) {
			resourceLoader.stop();
		}
		super.stop();
	}

}
