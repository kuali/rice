/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.core.framework.parameter

import org.kuali.rice.core.api.parameter.EvaluationOperator
import org.kuali.rice.core.api.parameter.ParameterContract
import org.kuali.rice.kns.bo.ExternalizableBusinessObject

class ParameterEbo implements ParameterContract, ExternalizableBusinessObject {

    private static final long serialVersionUID = 1L;

    def String namespaceCode
    def String componentCode
    def String name
    def String applicationCode
    def String value
    def String description
    def String parameterTypeCode
    def String evaluationOperatorCode
    def ParameterTypeEbo parameterType
    def Long versionNumber

    @Override
	EvaluationOperator getEvaluationOperator() {
		return EvaluationOperator.fromOperatorCode(evaluationOperatorCode);
	}

    @Override
    ParameterTypeEbo getParameterType() {
        return parameterType
    }

        /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static org.kuali.rice.core.api.parameter.Parameter to(ParameterEbo bo) {
        if (bo == null) {
            return null
        }

        return org.kuali.rice.core.api.parameter.Parameter.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static ParameterEbo from(org.kuali.rice.core.api.parameter.Parameter im) {
        if (im == null) {
            return null
        }

        ParameterEbo bo = new ParameterEbo()
        bo.namespaceCode = im.namespaceCode
        bo.componentCode = im.componentCode
        bo.name = im.name
        bo.applicationCode = im.applicationCode
        bo.value = im.value
        bo.description = im.description
        bo.parameterTypeCode = im.parameterType.code
        bo.evaluationOperatorCode = im.evaluationOperator.operatorCode

        bo.parameterType = ParameterTypeEbo.from(im.parameterType)
        bo.versionNumber = im.versionNumber

        return bo
    }

    void refresh() { }
}
