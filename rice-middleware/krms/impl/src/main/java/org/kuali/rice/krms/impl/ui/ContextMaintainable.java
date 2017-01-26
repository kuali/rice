/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krms.impl.ui;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.impl.cache.DistributedCacheManagerDecorator;
import org.kuali.rice.krad.maintenance.MaintainableImpl;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krms.api.KrmsConstants;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.impl.repository.ContextBo;
import org.kuali.rice.krms.impl.repository.RepositoryBoIncrementer;

import java.util.Map;

/**
 * {@link org.kuali.rice.krad.maintenance.Maintainable} for the {@link ContextBo}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * */
public class ContextMaintainable extends MaintainableImpl {

    private static final RepositoryBoIncrementer contextIdIncrementer = new RepositoryBoIncrementer(ContextBo.CONTEXT_SEQ_NAME);

    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        ContextBo newContext = (ContextBo) document.getNewMaintainableObject().getDataObject();

        newContext.setId(contextIdIncrementer.getNewId());

        super.processAfterNew(document, requestParameters);    
    }

    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        ContextBo context = (ContextBo) document.getNewMaintainableObject().getDataObject();

        context.setId(contextIdIncrementer.getNewId());

        super.processAfterCopy(document,
                requestParameters);
    }

    @Override
    public void saveDataObject() {
        super.saveDataObject();

        //flush context cache
        DistributedCacheManagerDecorator distributedCacheManagerDecorator =
                GlobalResourceLoader.getService(KrmsConstants.KRMS_DISTRIBUTED_CACHE);
        distributedCacheManagerDecorator.getCache(ContextDefinition.Cache.NAME).clear();
    }

    @Override
    public Object retrieveObjectForEditOrCopy(MaintenanceDocument document, Map<String, String> dataObjectKeys) {

        ContextBo contextBo = (ContextBo) super.retrieveObjectForEditOrCopy(document, dataObjectKeys);

        if (KRADConstants.MAINTENANCE_COPY_ACTION.equals(getMaintenanceAction())) {
            document.getDocumentHeader().setDocumentDescription("New Context Document");

            contextBo = contextBo.copyContext(" Copy " + System.currentTimeMillis());
        }

        return contextBo;
    }
}
