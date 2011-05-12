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

package org.kuali.rice.kim.impl.permission

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import org.hibernate.annotations.Type
import org.kuali.rice.kim.api.template.Template
import org.kuali.rice.kim.api.template.TemplateContract
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

@Entity
@Table(name="KRIM_PERM_TMPL_T")
public class PermissionTemplateBo extends PersistableBusinessObjectBase implements TemplateContract {
    private static final long serialVersionUID = 1L;

    @Id
	@Column(name="PERM_TMPL_ID")
	String id

	@Column(name="NMSPC_CD")
	String namespaceCode
	
    @Column(name="NM")
	String name

	@Column(name="DESC_TXT", length=400)
	String description;

	@Column(name="KIM_TYP_ID")
	String kimTypeId
	
	@Column(name="ACTV_IND")
	@Type(type="yes_no")
	boolean active

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static Template to(PermissionTemplateBo bo) {
        if (bo == null) {
            return null
        }

        return Template.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static PermissionTemplateBo from(Template im) {
        if (im == null) {
            return null
        }

        PermissionTemplateBo bo = new PermissionTemplateBo()
        bo.id = im.id
        bo.namespaceCode = im.namespaceCode
        bo.name = im.name
        bo.description = im.description
        bo.active = im.active
        bo.kimTypeId = im.kimTypeId
        bo.versionNumber = im.versionNumber
		bo.objectId = im.objectId;

        return bo
    }

}
