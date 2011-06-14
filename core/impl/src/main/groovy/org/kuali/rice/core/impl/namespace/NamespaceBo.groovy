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
package org.kuali.rice.core.impl.namespace;


import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.hibernate.annotations.Type
import org.kuali.rice.core.api.namespace.Namespace
import org.kuali.rice.core.api.namespace.NamespaceContract
import org.kuali.rice.krad.bo.Inactivateable
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase

@Entity
@Table(name="KRCR_NMSPC_T")
class NamespaceBo extends PersistableBusinessObjectBase implements NamespaceContract, Inactivateable {

    private static final long serialVersionUID = 1L;

    @Column(name="APPL_ID")
	def String applicationId;

    @Id
    @Column(name="NMSPC_CD")
    def String code;

    @Column(name="NM")
    def String name;

    @Type(type="yes_no")
    @Column(name="ACTV_IND")
    def boolean active;

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static Namespace to(NamespaceBo bo) {
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
    static NamespaceBo from(Namespace im) {
        if (im == null) {
            return null
        }

        NamespaceBo bo = new NamespaceBo()
        bo.applicationId = im.applicationId
        bo.active = im.active
        bo.code = im.code
        bo.name = im.name
        bo.versionNumber = im.versionNumber
		bo.objectId = im.objectId

        return bo
    }
}

