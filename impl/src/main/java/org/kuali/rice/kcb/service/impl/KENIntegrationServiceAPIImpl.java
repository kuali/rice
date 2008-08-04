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
package org.kuali.rice.kcb.service.impl;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.kuali.notification.service.KENAPIService;
import org.kuali.notification.service.KENServiceConstants;
import org.kuali.rice.core.Core;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kcb.service.KENIntegrationService;

/**
 * Integrates with KEN via the exposed KENAPIService (meaning there is a runtime dependency on KEN) 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KENIntegrationServiceAPIImpl implements KENIntegrationService {

    /**
     * Calls the {@link KENAPIService#getAllChannelNames()}
     * @see org.kuali.rice.kcb.service.KENIntegrationService#getAllChannelNames()
     */
    public Collection<String> getAllChannelNames() {
        KENAPIService api = (KENAPIService) GlobalResourceLoader.getService(new QName(Core.getCurrentContextConfig().getMessageEntity(), KENServiceConstants.KENAPI_SERVICE));
        return api.getAllChannelNames();
    }

}
