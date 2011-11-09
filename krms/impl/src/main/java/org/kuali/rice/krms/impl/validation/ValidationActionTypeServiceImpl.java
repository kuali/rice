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
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableTextInput;
import org.kuali.rice.krad.uif.util.LookupInquiryUtils;
import org.kuali.rice.krms.api.repository.action.ActionDefinition;
import org.kuali.rice.krms.framework.type.ValidationActionService;
import org.kuali.rice.krms.framework.engine.Action;
import org.kuali.rice.krms.framework.type.ActionTypeService;
import org.kuali.rice.krms.framework.type.ValidationActionType;
import org.kuali.rice.krms.framework.type.ValidationActionTypeService;
import org.kuali.rice.krms.impl.repository.ActionBo;
import org.kuali.rice.krms.impl.type.KrmsTypeServiceBase;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org).
 */
public class ValidationActionTypeServiceImpl extends KrmsTypeServiceBase implements ValidationActionTypeService {
    private static final String MESSAGE_FIELD_NAME = "Action Message"; // Database krms_attr_defn_t NM value
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

        // TypeCode
        if (!validationActionDefinition.getAttributes().containsKey(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE)) {
            throw new RiceIllegalArgumentException("validationActionDefinition does not contain an " +
                    ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE + " attribute");
        }
        String validationActionTypeCode = validationActionDefinition.getAttributes().get(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE);
        if (StringUtils.isBlank(validationActionTypeCode)) {
            throw new RiceIllegalArgumentException(ValidationActionTypeService.VALIDATIONS_ACTION_TYPE_CODE_ATTRIBUTE + " attribute must not be null or blank");
        }

        // Message
        if (!validationActionDefinition.getAttributes().containsKey(MESSAGE_FIELD_NAME)) {
            throw new RiceIllegalArgumentException("validationActionDefinition does not contain an " +
                    MESSAGE_FIELD_NAME + " attribute");
        }
        String validationMessage = validationActionDefinition.getAttributes().get(MESSAGE_FIELD_NAME);
        if (StringUtils.isBlank(validationMessage)) {
            throw new RiceIllegalArgumentException(MESSAGE_FIELD_NAME + " attribute must not be null or blank");
        }

        if (ValidationActionType.WARNING.getCode().equals(validationActionTypeCode)) {
            return new ValidationAction(ValidationActionType.WARNING, validationMessage);
        }
        if (ValidationActionType.ERROR.getCode().equals(validationActionTypeCode)) {
            return new ValidationAction(ValidationActionType.ERROR, validationMessage);
        }
        return null;
    }

    @Override
    public List<RemotableAttributeField> getAttributeFields(@WebParam(name = "krmsTypeId") String krmsTypeId) throws RiceIllegalArgumentException {
        RadioButtonTypeServiceUtil radioButtonTypeServiceUtil = new RadioButtonTypeServiceUtil();
        List<String> excludeNames = new ArrayList<String>();
        excludeNames.add(MESSAGE_FIELD_NAME);
        List<RemotableAttributeField> remotableAttributeFields = radioButtonTypeServiceUtil.getAttributeFields(krmsTypeId, excludeNames);
        remotableAttributeFields.add(createMessageField());
        return remotableAttributeFields;
    }

    private RemotableAttributeField createMessageField() {

        RemotableTextInput.Builder controlBuilder = RemotableTextInput.Builder.create();
        controlBuilder.setSize(30);
        controlBuilder = RemotableTextInput.Builder.create();
        controlBuilder.setSize(Integer.valueOf(40));
        controlBuilder.setWatermark("action message");

        RemotableAttributeField.Builder builder = RemotableAttributeField.Builder.create(MESSAGE_FIELD_NAME);
        builder.setRequired(true);
        builder.setDataType(DataType.STRING);
        builder.setControl(controlBuilder);
        builder.setLongLabel("Validation Action Message");
        builder.setShortLabel("Message");
        builder.setMinLength(Integer.valueOf(1));
        builder.setMaxLength(Integer.valueOf(40));

        return builder.build();
    }


    @Override
    public void setValidationService(ValidationActionService mockValidationService) {
        if (mockValidationService == null) {
            throw new RiceIllegalArgumentException("validationService must not be null");
        }
        this.validationService = mockValidationService;
    }
}
