package org.kuali.rice.core.framework.component

import org.kuali.rice.core.api.component.Component
import org.kuali.rice.core.api.component.ComponentContract
import org.kuali.rice.kns.bo.ExternalizableBusinessObject
import org.kuali.rice.kns.bo.Inactivateable

class ComponentEbo implements ComponentContract, Inactivateable, ExternalizableBusinessObject {

    private static final long serialVersionUID = 1L;

    def String namespaceCode
	def String code
	def String name
	def boolean active = true
	def boolean virtual

    /**
     * Converts a mutable ebo to it's immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static Component to(ComponentEbo bo) {
        if (bo == null) {
            return null
        }

        return Component.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static ComponentEbo from(Component im) {
        if (im == null) {
            return null
        }

        ComponentEbo bo = new ComponentEbo()
        bo.code = im.code
        bo.name = im.name
        bo.active = im.active
        bo.namespaceCode = im.namespaceCode
        bo.virtual = im.virtual
        return bo;
    }

    @Override
    void refresh() { }
}
