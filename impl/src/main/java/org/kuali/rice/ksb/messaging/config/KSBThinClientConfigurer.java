/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
package org.kuali.rice.ksb.messaging.config;

import java.util.Collections;
import java.util.List;

import org.kuali.rice.core.config.ModuleConfigurer;


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
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KSBThinClientConfigurer extends ModuleConfigurer {

	public KSBThinClientConfigurer() {
        super();
        setModuleName( "KSB" );
        setHasWebInterface(false);
    }
	
	@Override
	public List<String> getPrimarySpringFiles(){
		return Collections.singletonList("classpath:org/kuali/rice/ksb/config/KSBThinClientSpringBeans.xml");
	}

}
