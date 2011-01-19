/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.group.impl;

import org.kuali.rice.kim.bo.types.impl.KimAttributeDataImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_GRP_ATTR_DATA_T")
public class GroupAttributeDataImpl extends KimAttributeDataImpl {
	//@Id
	//@GeneratedValue(generator="KRSB_SVC_DEF_S")
	//@GenericGenerator(name="KRSB_SVC_DEF_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
	//		@Parameter(name="sequence_name",value="KRSB_SVC_DEF_S"),
	//		@Parameter(name="value_column",value="id")
	//	})
	//@Column(name="ATTR_DATA_ID")
	//protected String attributeDataId;
	
    @Column(name="GRP_ID")
    protected String groupId;

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
