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

    @Override
	EvaluationOperator getEvaluationOperator() {
		return EvaluationOperator.fromOperatorCode(evaluationOperatorCode);
	}

    @Override
    ParameterTypeEbo getParameterType() {
        return parameterType
    }

        /**
     * Converts a mutable bo to it's immutable counterpart
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
     * Converts a immutable object to it's mutable bo counterpart
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

        return bo
    }

    void refresh() { }
}
