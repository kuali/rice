/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.bo.user;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Kuali Group Pojo to access Group Information.
 * 
 * 
 */
public class KualiGroup extends PersistableBusinessObjectBase {
    
    public static final KualiGroup KUALI_UNIVERSAL_GROUP = new KualiUniversalGroup();
    
    /* *****************************************************************
     * DO NOT PUT WORKGROUP NAME CONSTANTS HERE
     * 
     * This is a core system module class and should not have references to any other module.
     * Any workgroup constants should be added to FS_PARM_T and loaded from there via the configuration service.
     *   
     * *****************************************************************/

    // -----------------------------------
    // from fs_work_grp_t
    // -----------------------------------
    // FS_WORK_GRP_ID;12;VARCHAR2;10;;;NO;;
    // FS_WORK_GRP_DESC;12;VARCHAR2;40;;;YES;;
    // FIN_COA_CD;12;VARCHAR2;2;;;YES;; -- not included to allow for potential future integration with workflow regarding workgroups
    // ORG_CD;12;VARCHAR2;4;;;YES;; -- not included to allow for potential future integration with workflow regarding workgroups

    private static final long serialVersionUID = 6228441443664145127L;
    private String groupName;
    private String groupDescription;
    private List groupUsers;

    /**
     * No args constructor
     * 
     */
    public KualiGroup() {
        this.groupUsers = Collections.EMPTY_LIST;
    }

    /**
     * Constructor taking a single argument of groupName
     * 
     * @param groupName
     */
    public KualiGroup(String groupName) {
        this.groupName = groupName;
        this.groupUsers = Collections.EMPTY_LIST;
    }

    /**
     * Constructor taking arguments for groupName and for groupUsers
     * 
     * @param groupName
     * @param groupUsers
     */
    public KualiGroup(String groupName, List groupMembers) {
        this.groupName = groupName;
        this.groupUsers = groupMembers;
    }

    /**
     * @return Returns the groupUsers.
     */
    public List getGroupUsers() {
        return groupUsers;
    }

    /**
     * @param groupUsers The groupUsers to set.
     */
    public void setGroupUsers(List groupMembers) {
        this.groupUsers = groupMembers;
    }

    /**
     * @return Returns the groupName.
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName The groupName to set.
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return Returns the groupDescription.
     */
    public String getGroupDescription() {
        return groupDescription;
    }

    /**
     * @param groupDescription The groupDescription to set.
     */
    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    /**
     * boolean check to determine if a user is a member of a group
     * 
     * @param kualiUser
     * @return true if the group has the member passed in
     */
    public boolean hasMember(UniversalUser universalUser) {
        if (universalUser != null && universalUser.getPersonUniversalIdentifier() != null) {
            for (Iterator iter = groupUsers.iterator(); iter.hasNext();) {
                String groupMemberNetworkId = (String) iter.next();
                if (universalUser.getPersonUserIdentifier().toLowerCase().equals(groupMemberNetworkId.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("groupName", getGroupName());

        return m;
    }
}