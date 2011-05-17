package org.kuali.rice.kim.impl.common.template

import javax.persistence.Column
import org.hibernate.annotations.Type
import org.kuali.rice.kim.api.common.template.TemplateContract
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

abstract class TemplateBo extends PersistableBusinessObjectBase implements TemplateContract {

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
}
