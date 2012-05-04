/**
 * Copyright 2005-2012 The Kuali Foundation
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

import org.kuali.rice.krad.maintenance.MaintainableImpl;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krms.impl.repository.ContextBo;

import java.util.Map;

/**
 * {@link org.kuali.rice.krad.maintenance.Maintainable} for the {@link ContextBo}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * */
public class ContextMaintainable extends MaintainableImpl {


    @Override
    public Object retrieveObjectForEditOrCopy(MaintenanceDocument document, Map<String, String> dataObjectKeys) {

        ContextBo contextBo = (ContextBo) super.retrieveObjectForEditOrCopy(document, dataObjectKeys);

        if (KRADConstants.MAINTENANCE_COPY_ACTION.equals(getMaintenanceAction())) {
            document.getDocumentHeader().setDocumentDescription("New Context Document");

            contextBo = contextBo.copyContext(" " + System.currentTimeMillis());
        }

        return contextBo;
    }

}
