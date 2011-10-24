/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.validation;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.api.validation.ValidationActionService;
import org.kuali.rice.krms.framework.engine.Action;
import org.kuali.rice.krms.framework.type.ActionTypeService;
import org.kuali.rice.krms.framework.type.ValidationActionType;
import org.kuali.rice.krms.framework.type.ValidationActionTypeService;
import org.kuali.rice.krms.impl.type.KrmsTypeServiceBase;
import org.kuali.rice.krms.impl.util.KRMSServiceLocatorInternal;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ValidationWarningActionTypeServiceImpl extends KrmsTypeServiceBase implements ValidationActionTypeService {
    private ValidationActionService validationService;

    private ValidationWarningActionTypeServiceImpl() {}

    /**
     * Factory method for getting a {@link ActionTypeService}
     * @return a {@link ValidationActionTypeService} corresponding to the given {@link org.kuali.rice.krms.framework.type.ValidationActionType}.
     */
    public static ActionTypeService getInstance() {
        return new ValidationWarningActionTypeServiceImpl();
    }


    /**
     * @return the configured {@link org.kuali.rice.krms.api.validation.ValidationActionService}
     */
    public ValidationActionService getValidationService() {
        if (validationService == null) {
            validationService = KRMSServiceLocatorInternal.getValidationActionService();
        }

        return validationService;
    }

    @Override
    public Action loadAction(ActionDefinition actionDefinition) {
        // TODO EGHM translator?
        return new ValidationAction(ValidationActionType.WARNING, actionDefinition.getTypeId(), actionDefinition.getDescription());
    }

    /**
     * @return the configured {@link org.kuali.rice.krms.api.validation.ValidationActionService}
     */
    public void setValidationService(ValidationActionService validationService) {
        if (validationService == null) {
            throw new RiceIllegalArgumentException("validationService must not be null");
        }
        this.validationService = validationService;
    }
}
