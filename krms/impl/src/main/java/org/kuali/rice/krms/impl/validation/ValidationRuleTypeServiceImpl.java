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
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.framework.engine.Rule;
import org.kuali.rice.krms.framework.type.ValidationRuleType;
import org.kuali.rice.krms.framework.type.ValidationRuleTypeService;
import org.kuali.rice.krms.impl.provider.repository.RepositoryToEngineTranslator;
import org.kuali.rice.krms.impl.type.KrmsTypeServiceBase;

import javax.jws.WebParam;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class ValidationRuleTypeServiceImpl extends KrmsTypeServiceBase implements ValidationRuleTypeService {

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

    @Override
    public List<RemotableAttributeField> getAttributeFields(@WebParam(name = "krmsTypeId") String krmsTypeId) throws RiceIllegalArgumentException {
        RadioButtonTypeServiceUtil radioButtonTypeServiceUtil = new RadioButtonTypeServiceUtil();
        return radioButtonTypeServiceUtil.getAttributeFields(krmsTypeId, null);
    }

    /**
     * @param translator the RepositoryToEngineTranslator to set
     */
    public void setRepositoryToEngineTranslator(RepositoryToEngineTranslator translator) {
        this.translator = translator;
    }
}

