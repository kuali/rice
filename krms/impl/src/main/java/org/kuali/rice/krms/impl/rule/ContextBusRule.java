/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.rule;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;
import org.kuali.rice.krms.impl.repository.ContextBo;
import org.kuali.rice.krms.impl.repository.ContextBoService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;
import org.kuali.rice.krms.impl.util.KRMSPropertyConstants;

public class ContextBusRule extends MaintenanceDocumentRuleBase {
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = true;

        ContextBo context = (ContextBo) document.getNewMaintainableObject().getDataObject();
        isValid &= validateId(context);
        isValid &= validateNameNamespace(context);

        return isValid;
    }

    private boolean validateId(ContextBo context) {
        if (StringUtils.isNotBlank(context.getId())) {
            ContextDefinition contextInDatabase = getContextBoService().getContextByContextId(context.getId());
            if ((contextInDatabase  != null) && (!StringUtils.equals(contextInDatabase.getId(), context.getId()))) {
                this.putFieldError(KRMSPropertyConstants.Context.CONTEXT_ID, "error.context.duplicateId");
                return false;
            }
        }

        return true;
    }

    /**
     * Check if the name-namespace pair already exist.
     * @param context
     * @return true if the name-namespace pair is unique, false otherwise
     */
    private boolean validateNameNamespace(ContextBo context) {
        if (StringUtils.isNotBlank(context.getName()) && StringUtils.isNotBlank(context.getNamespace())) {
            ContextDefinition contextInDatabase = getContextBoService().getContextByNameAndNamespace(context.getName(), context.getNamespace());
            if((contextInDatabase != null) && (!StringUtils.equals(contextInDatabase.getId(), context.getId()))) {
                this.putFieldError(KRMSPropertyConstants.Context.NAME, "error.context.duplicateNameNamespace");
                return false;
            }
        }

        return true;
    }

    public ContextBoService getContextBoService() {
        return KrmsRepositoryServiceLocator.getContextBoService();
    }
}
