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
package org.kuali.rice.core.impl.component;


import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.IdClass
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.Transient
import org.hibernate.annotations.Type
import org.kuali.rice.core.api.component.Component
import org.kuali.rice.core.api.component.ComponentContract
import org.kuali.rice.core.impl.namespace.NamespaceBo
import org.kuali.rice.kns.bo.Inactivateable
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

@IdClass(ComponentId.class)
@Entity
@Table(name="KRNS_PARM_DTL_TYP_T")
public class ComponentBo extends PersistableBusinessObjectBase implements ComponentContract, Inactivateable {

    private static final long serialVersionUID = 1L;

	@Id
	@Column(name="NMSPC_CD")
	def String namespaceCode;

	@Id
	@Column(name="PARM_DTL_TYP_CD")
	def String code;

	@Column(name="NM")
	def String name;

	@Type(type="yes_no")
	@Column(name="ACTV_IND")
	def boolean active = true;

    @Transient
	def boolean virtual;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="NMSPC_CD", insertable=false, updatable=false)
	def NamespaceBo namespace;

   /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static Component to(ComponentBo bo) {
        if (bo == null) {
            return null
        }

        return Component.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static ComponentBo from(Component im) {
        if (im == null) {
            return null
        }

        ComponentBo bo = new ComponentBo()
        bo.code = im.code
        bo.name = im.name
        bo.active = im.active
        bo.namespaceCode = im.namespaceCode
        bo.virtual = im.virtual
		bo.versionNumber = im.versionNumber
		bo.objectId = im.objectId

        return bo;
    }
}

