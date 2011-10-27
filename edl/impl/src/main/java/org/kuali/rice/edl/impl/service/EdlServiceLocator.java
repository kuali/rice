/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.edl.impl.service;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.api.KewApiConstants;

public class EdlServiceLocator {
	
	private static final Logger LOG = Logger.getLogger(EdlServiceLocator.class);

	public static final String EDL_RUN_MODE_PROPERTY = "edl.mode";
	public static final String EDOCLITE_SERVICE = "enEDocLiteService";
	

    static <T> T getService(String serviceName) {
        return GlobalResourceLoader.<T>getService(serviceName);
    }

	public static EDocLiteService getEDocLiteService() {
		return getService(EDOCLITE_SERVICE);
	}
	
	public static Object getBean(String serviceName) {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug("Fetching service " + serviceName);
		}
		return GlobalResourceLoader.getResourceLoader().getService(
				(RunMode.REMOTE.equals(RunMode.valueOf(ConfigContext.getCurrentContextConfig().getProperty(EDL_RUN_MODE_PROPERTY)))) ?
						new QName(KewApiConstants.KEW_MODULE_NAMESPACE, serviceName) : new QName(serviceName));
	}

}
