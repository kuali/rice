package org.kuali.rice.core.framework.parameter

import org.kuali.rice.core.api.parameter.ParameterType
import org.kuali.rice.core.api.parameter.ParameterTypeContract
import org.kuali.rice.kns.bo.ExternalizableBusinessObject
import org.kuali.rice.kns.bo.Inactivateable

class ParameterTypeEbo implements ParameterTypeContract, Inactivateable, ExternalizableBusinessObject {

    private static final long serialVersionUID = 1L;

    def String code
	def String name
	def boolean active = true

    /**
     * Converts a mutable bo to it's immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static ParameterType to(ParameterTypeEbo bo) {
        if (bo == null) {
            return null
        }

        return ParameterType.Builder.create(bo).build()
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static ParameterTypeEbo from(ParameterType im) {
        if (im == null) {
            return null
        }

        ParameterTypeEbo bo = new ParameterTypeEbo()
        bo.active = im.active
        bo.code = im.code
        bo.name = im.name
        return bo
    }

    void refresh() { }
}
