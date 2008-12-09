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
package org.kuali.rice.core.util;

import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentAuthorizationService;
import org.kuali.rice.kns.service.impl.DocumentServiceImpl;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.springframework.beans.factory.InitializingBean;

/**
 * Extends DocumentServiceImpl to exercise providing an overridden bean in the client
 * without having to redefine and inject every Rice bean dependency.
 * {@link RiceService} annotation and {@link GRLServiceInjectionPostProcessor} should
 * inject everything we need. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ClientDocumentServiceImpl extends DocumentServiceImpl implements InitializingBean {
    public static ClientDocumentServiceImpl me;

    public void afterPropertiesSet() throws Exception {
        me = this;
    }

    // some member fields are private at the moment; do the best we can

    public WorkflowDocumentService getWorkflowDocumentService() {
        return super.getWorkflowDocumentService();
    }
    public BusinessObjectService getBusinessObjectService() {
        return super.getBusinessObjectService();
    }
    public DocumentAuthorizationService getDocumentAuthorizationService() {
        return super.getDocumentAuthorizationService();
    }
    public DocumentDao getDocumentDao() {
        return super.getDocumentDao();
    }
}