/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.bo.ui;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;


/**
 * This is a description of what this class does - shyu don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Entity
@Table(name="KRIM_PND_GRP_ATTR_DATA_T")
public class GroupDocumentQualifier extends KimDocumentAttributeDataBusinessObjectBase {
	@Column(name="GRP_ID")
	private String groupId;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap hashMap = new LinkedHashMap();
		hashMap.put("attrDataId", getAttrDataId());
		hashMap.put("groupId", getGroupId());
		hashMap.put("kimTypId", getKimTypId());
		hashMap.put("kimAttrDefnId", getKimAttrDefnId());
		hashMap.put("attrVal", getAttrVal());
		return hashMap;
	}
}
