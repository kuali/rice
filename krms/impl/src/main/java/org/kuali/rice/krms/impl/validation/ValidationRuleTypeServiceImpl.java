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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.framework.type.ValidationRuleService;
import org.kuali.rice.krms.framework.engine.Rule;
import org.kuali.rice.krms.framework.type.ValidationRuleType;
import org.kuali.rice.krms.framework.type.ValidationRuleTypeService;
import org.kuali.rice.krms.impl.provider.repository.RepositoryToEngineTranslatorImpl;
import org.kuali.rice.krms.impl.type.KrmsTypeServiceBase;
import org.kuali.rice.krms.impl.util.KRMSServiceLocatorInternal;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ValidationRuleTypeServiceImpl extends KrmsTypeServiceBase implements ValidationRuleTypeService {

    private ValidationRuleType type;

    private ValidationRuleService validationService;

    private ValidationRuleTypeServiceImpl(){
        super();
    }

    /**
     * Factory method for getting a {@link ValidationRuleTypeService}
     * @param type indicates the type of validationRule that the returned {@link ValidationRuleTypeService} will produce
     * @return a {@link ValidationRuleTypeService} corresponding to the given {@link ValidationRuleType}.
     */
    public static ValidationRuleTypeService getInstance(ValidationRuleType type) {
        return new ValidationRuleTypeServiceImpl(type);
    }

    /**
     * private constructor to enforce use of static factory
     * @param type
     */
    private ValidationRuleTypeServiceImpl(ValidationRuleType type) {
        if (type == null) { throw new IllegalArgumentException("type must not be null"); }
        this.type = type;
    }

    @Override
    public Rule loadRule(RuleDefinition validationRuleDefinition) {
        if (validationRuleDefinition == null) { throw new RiceIllegalArgumentException("validationRuleDefinition must not be null"); }

        if (validationRuleDefinition.getAttributes() == null ||
                !validationRuleDefinition.getAttributes().containsKey(ATTRIBUTE_FIELD_NAME)) {

            throw new RiceIllegalArgumentException("validationRuleDefinition does not contain an " +
                    ATTRIBUTE_FIELD_NAME + " attribute");
        }

        String validationId = validationRuleDefinition.getAttributes().get(ATTRIBUTE_FIELD_NAME);

        if (StringUtils.isBlank(validationId)) {
            throw new RiceIllegalArgumentException(ATTRIBUTE_FIELD_NAME + " attribute must not be null or blank");
        }

        RepositoryToEngineTranslatorImpl translator = new RepositoryToEngineTranslatorImpl();
        // if the ValidationRuleDefinition is valid, constructing the ValidationRule is cake
        return translator.translateRuleDefinition(validationRuleDefinition);
    }

    /**
     * @return the configured {@link org.kuali.rice.krms.framework.type.ValidationActionService}
     */
    public ValidationRuleService getValidationRuleService() {
        if (validationService == null) {
            validationService = KRMSServiceLocatorInternal.getValidationRuleService();
        }

        return validationService;
    }

    @Override
    public void setValidationRuleService(ValidationRuleService validationService) {
        if (validationService == null) {
            throw new RiceIllegalArgumentException("validationService must not be null");
        }
        this.validationService = validationService;
    }

}

