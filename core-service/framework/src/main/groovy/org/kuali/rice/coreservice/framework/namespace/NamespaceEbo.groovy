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
package org.kuali.rice.coreservice.framework.namespace

import org.kuali.rice.coreservice.api.namespace.Namespace
import org.kuali.rice.coreservice.api.namespace.NamespaceContract
import org.kuali.rice.krad.bo.ExternalizableBusinessObject
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable


//@ToString
class NamespaceEbo implements NamespaceContract, MutableInactivatable, ExternalizableBusinessObject {

    private static final long serialVersionUID = 1L;

    def String applicationId
    def String code
    def String name
    def boolean active
    def Long versionNumber
	def String objectId

    /**
     * Converts a mutable bo to its immutable counterpart
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
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static NamespaceEbo from(Namespace im) {
        if (im == null) {
            return null
        }

        NamespaceEbo bo = new NamespaceEbo()
        bo.applicationId = im.applicationId
        bo.active = im.active
        bo.code = im.code
        bo.name = im.name
        bo.versionNumber = im.versionNumber
		bo.objectId = im.objectId

        return bo
    }

    void refresh() { }
}
