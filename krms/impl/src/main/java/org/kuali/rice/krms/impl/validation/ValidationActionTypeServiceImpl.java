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
package org.kuali.rice.krms.impl.validation;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.framework.type.ValidationActionService;
import org.kuali.rice.krms.framework.engine.Action;
import org.kuali.rice.krms.framework.type.ActionTypeService;
import org.kuali.rice.krms.framework.type.ValidationActionType;
import org.kuali.rice.krms.framework.type.ValidationActionTypeService;
import org.kuali.rice.krms.impl.type.KrmsTypeServiceBase;

import javax.jws.WebParam;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org).
 */
public class ValidationActionTypeServiceImpl extends KrmsTypeServiceBase implements ValidationActionTypeService {
    private ValidationActionService validationService;

    private ValidationActionTypeServiceImpl() {}

    /**
     * Factory method for getting a {@link ActionTypeService}
     * @return a {@link ActionTypeService}
     */
    public static ActionTypeService getInstance() {
        return new ValidationActionTypeServiceImpl();
    }

    @Override
    public Action loadAction(ActionDefinition validationActionDefinition) {

        if (validationActionDefinition == null) { throw new RiceIllegalArgumentException("validationActionDefinition must not be null"); }
        if (validationActionDefinition.getAttributes() == null) { throw new RiceIllegalArgumentException("validationActionDefinition must not be null");}
        if (!validationActionDefinition.getAttributes().containsKey(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE)) {
            throw new RiceIllegalArgumentException("validationActionDefinition does not contain an " +
                    ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE + " attribute");
        }
        String validationActionTypeCode = validationActionDefinition.getAttributes().get(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE);

        if (StringUtils.isBlank(validationActionTypeCode)) {
            throw new RiceIllegalArgumentException(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE + " attribute must not be null or blank");
        }

        if (ValidationActionType.WARNING.getCode().equals(validationActionTypeCode)) {
            return new ValidationAction(ValidationActionType.WARNING, validationActionDefinition.getDescription());
        }
        if (ValidationActionType.ERROR.getCode().equals(validationActionTypeCode)) {
            return new ValidationAction(ValidationActionType.ERROR, validationActionDefinition.getDescription());
        }
        return null;
    }

    @Override
    public List<RemotableAttributeField> getAttributeFields(@WebParam(name = "krmsTypeId") String krmsTypeId) throws RiceIllegalArgumentException {
        RadioButtonTypeServiceUtil radioButtonTypeServiceUtil = new RadioButtonTypeServiceUtil();
        return radioButtonTypeServiceUtil.getAttributeFields(krmsTypeId);
    }
    
    @Override
    public void setValidationService(ValidationActionService mockValidationService) {
        if (mockValidationService == null) {
            throw new RiceIllegalArgumentException("validationService must not be null");
        }
        this.validationService = mockValidationService;
    }
}
