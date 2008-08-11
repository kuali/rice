/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.workgroup;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.kuali.rice.kew.attribute.Extension;
import org.kuali.rice.kew.attribute.ExtensionData;
import org.kuali.rice.kew.bo.BaseWorkflowPersistable;


/**
 * An extension of a {@link Workgroup}.  Provides attribute-specific data
 * extensions to the workgroup for a particular {@link WorkgroupType}.  Contains
 * a List of {@link ExtensionData}s.
 *
 * @see Workgroup
 * @see WorkgroupType
 * @see ExtensionData
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="EN_WRKGRP_EXT_T")
public class BaseWorkgroupExtension extends BaseWorkflowPersistable implements Extension {

	private static final long serialVersionUID = -305147691188181612L;

	@Id
	@Column(name="WRKGRP_EXT_ID")
	private Long workgroupExtensionId;
	@Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;

	@OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumns({@JoinColumn(name="WRKGRP_ID"), @JoinColumn(name="WRKGRP_VER_NBR")})
	private BaseWorkgroup workgroup;
	@OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="WRKGRP_TYP_ATTRIB_ID")
	private WorkgroupTypeAttribute workgroupTypeAttribute;

    @Transient
	private List<ExtensionData> data = new ArrayList<ExtensionData>();

	public String getAttributeName() {
		return workgroupTypeAttribute.getAttribute().getName();
	}

	public List<ExtensionData> getData() {
		return data;
	}

	public String getDataValue(String key) {
		for (ExtensionData data : getData()) {
			if (data.getKey().equals(key)) {
				return data.getValue();
			}
		}
		return null;
	}

	public Integer getLockVerNbr() {
		return lockVerNbr;
	}

	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}

	public WorkgroupTypeAttribute getWorkgroupTypeAttribute() {
		return workgroupTypeAttribute;
	}

	public void setWorkgroupTypeAttribute(WorkgroupTypeAttribute ruleTemplateAttribute) {
		this.workgroupTypeAttribute = ruleTemplateAttribute;
	}

	public BaseWorkgroup getWorkgroup() {
		return workgroup;
	}

	public void setWorkgroup(BaseWorkgroup workgroup) {
		this.workgroup = workgroup;
	}

	public Long getWorkgroupExtensionId() {
		return workgroupExtensionId;
	}

	public void setWorkgroupExtensionId(Long workgroupExtensionId) {
		this.workgroupExtensionId = workgroupExtensionId;
	}

}
