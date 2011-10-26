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
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.framework.type.ValidationRuleService;
import org.kuali.rice.krms.framework.engine.Rule;
import org.kuali.rice.krms.framework.type.ValidationRuleType;
import org.kuali.rice.krms.framework.type.ValidationRuleTypeService;
import org.kuali.rice.krms.impl.provider.repository.RepositoryToEngineTranslator;
import org.kuali.rice.krms.impl.type.KrmsTypeServiceBase;
import org.kuali.rice.krms.impl.util.KRMSServiceLocatorInternal;

import javax.jws.WebParam;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class ValidationRuleTypeServiceImpl extends KrmsTypeServiceBase implements ValidationRuleTypeService {

    private ValidationRuleService validationService;

    private RepositoryToEngineTranslator translator;

    /**
     * private constructor to enforce use of static factory
     */
    private ValidationRuleTypeServiceImpl(){
        super();
    }

    /**
     * Factory method for getting a {@link ValidationRuleTypeService}
     * @return a {@link ValidationRuleTypeService} corresponding to the given {@link ValidationRuleType}.
     */
    public static ValidationRuleTypeService getInstance() {
        return new ValidationRuleTypeServiceImpl();
    }

    @Override
    public Rule loadRule(RuleDefinition validationRuleDefinition) {
        if (validationRuleDefinition == null) { throw new RiceIllegalArgumentException("validationRuleDefinition must not be null"); }
        if (validationRuleDefinition.getAttributes() == null) { throw new RiceIllegalArgumentException("validationRuleDefinition must not be null");}

        if (!validationRuleDefinition.getAttributes().containsKey(ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE)) {

            throw new RiceIllegalArgumentException("validationRuleDefinition does not contain an " +
                    ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE + " attribute");
        }
        String validationRuleTypeCode = validationRuleDefinition.getAttributes().get(ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE);

        if (StringUtils.isBlank(validationRuleTypeCode)) {
            throw new RiceIllegalArgumentException(ValidationRuleTypeService.VALIDATIONS_RULE_TYPE_CODE_ATTRIBUTE + " attribute must not be null or blank");
        }

        if (ValidationRuleType.VALID.getCode().equals(validationRuleTypeCode)) {
            return new ValidationRule(ValidationRuleType.VALID, validationRuleDefinition.getName(),
                    translator.translatePropositionDefinition(validationRuleDefinition.getProposition()),
                    translator.translateActionDefinitions(validationRuleDefinition.getActions()));
        }
        if (ValidationRuleType.INVALID.getCode().equals(validationRuleTypeCode)) {
            return new ValidationRule(ValidationRuleType.INVALID, validationRuleDefinition.getName(),
                    translator.translatePropositionDefinition(validationRuleDefinition.getProposition()),
                    translator.translateActionDefinitions(validationRuleDefinition.getActions()));
        }
        return null;
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

    @Override
    public RuleDefinition getValidationRule(
            @WebParam(name = "validationId") String validationId) throws RiceIllegalArgumentException {
        return null;  //TODO EGHM
    }

    @Override
    public RuleDefinition getValidationRuleByName(@WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "name") String name) throws RiceIllegalArgumentException {
        return null;  //TODO EGHM
    }

    /**
     * TODO EGHM
     *
     * @param validation
     * @return
     * @throws org.kuali.rice.core.api.exception.RiceIllegalArgumentException if the given Validation definition is null
     * @throws org.kuali.rice.core.api.exception.RiceIllegalArgumentException if the given Validation definition has a
     * non-null id.  When creating a new
     * Validation definition, the ID will be generated.
     * @throws org.kuali.rice.core.api.exception.RiceIllegalStateException if a Validation with the given namespace code
     * and name already exists
     */
    @Override
    public RuleDefinition createValidationRule(@WebParam(
            name = "validation") RuleDefinition validation) throws RiceIllegalArgumentException, RiceIllegalStateException {
        return null; //TODO EGHM
    }

    /**
     * @param validation
     * @return
     * @throws org.kuali.rice.core.api.exception.RiceIllegalArgumentException
     * @throws org.kuali.rice.core.api.exception.RiceIllegalStateException if the Validation does not exist in the system
     * under the given validationId
     */
    @Override
    public RuleDefinition updateValidationRule(@WebParam(
            name = "validation") RuleDefinition validation) throws RiceIllegalArgumentException, RiceIllegalStateException {
        return null;  //TODO EGHM
    }

    /**
     * @param translator the RepositoryToEngineTranslator to set
     */
    public void setRepositoryToEngineTranslator(RepositoryToEngineTranslator translator) {
        this.translator = translator;
    }
}

