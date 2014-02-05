/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.labs;

import org.kuali.rice.kim.api.group.GroupMemberContract;
import org.kuali.rice.kim.impl.membership.AbstractMemberBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Cacheable(false)
@Table(name = "KRIM_GRP_MBR_T")

public class LabsGroupControl extends AbstractMemberBo implements GroupMemberContract{

    private transient String myGroupName;
    private transient String myGroupNameSpace;

    private static final long serialVersionUID = 6773749266062306217L;

    @PortableSequenceGenerator(name = "KRIM_GRP_MBR_ID_S")
    @GeneratedValue(generator = "KRIM_GRP_MBR_ID_S")
    @Id
    @Column(name = "GRP_MBR_ID")
    private String id;

    @Column(name = "GRP_ID")
    private String groupId;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMyGroupName() {
        return myGroupName;
    }

    public void setMyGroupName(String myGroupName) {
        this.myGroupName = myGroupName;
    }

    public String getMyGroupNameSpace() {
        return myGroupNameSpace;
    }

    public void setMyGroupNameSpace(String myGroupNameSpace) {
        this.myGroupNameSpace = myGroupNameSpace;
    }
}
