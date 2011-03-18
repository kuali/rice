/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.rice.kim.config;

import java.util.Collections;
import java.util.List;

import org.kuali.rice.core.config.ModuleConfigurer;


/**
 * A configure which starts up an alternate Spring file to configure some services that are needed in
 * "Thin Client" mode for KIM.  A "thin client" is a client which communicates directly with service
 * endpoints and doesn't use the registry or the message queue.
 *
 * <p>TODO This is essentially a hack to allow for just the IdentityManagementServiceImpl to be loaded
 * into a "thin client" application.  The PersonServiceImpl services pulls in way too many dependencies
 * for a thin client application so we can't include it here.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KIMThinClientConfigurer extends ModuleConfigurer {

	public KIMThinClientConfigurer() {
        super();
        setModuleName( "KIM" );
        setHasWebInterface(false);
    }
	
	@Override
	public List<String> getPrimarySpringFiles(){
		return Collections.singletonList("classpath:org/kuali/rice/kim/config/KIMThinClientSpringBeans.xml");
	}
	
}
