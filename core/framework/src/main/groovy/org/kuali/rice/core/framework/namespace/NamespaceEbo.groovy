package org.kuali.rice.core.framework.namespace

import org.kuali.rice.core.api.namespace.Namespace
import org.kuali.rice.core.api.namespace.NamespaceContract
import org.kuali.rice.kns.bo.ExternalizableBusinessObject
import org.kuali.rice.kns.bo.Inactivateable

class NamespaceEbo implements NamespaceContract, Inactivateable, ExternalizableBusinessObject {

    private static final long serialVersionUID = 1L;

    def String applicationCode
    def String code
    def String name
    def boolean active

    /**
     * Converts a mutable bo to it's immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static Namespace to(NamespaceEbo bo) {
        if (bo == null) {
            return null
        }

        return Namespace.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static NamespaceEbo from(Namespace im) {
        if (im == null) {
            return null
        }

        NamespaceEbo bo = new NamespaceEbo()
        bo.applicationCode = im.applicationCode
        bo.active = im.active
        bo.code = im.code
        bo.name = im.name

        return bo
    }

    void refresh() { }
}
