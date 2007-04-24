/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import java.util.List;

/**
 * Group of which all Kuali users are a member.
 * 
 * 
 */
class KualiUniversalGroup extends KualiGroup {
    private static final long serialVersionUID = 1859237330240184446L;

    public static final String UNIVERSAL_GROUP_NAME = "kualiUniversalGroup";

    /**
     * Constructs an instance of the universal group
     */
    public KualiUniversalGroup() {
        super(UNIVERSAL_GROUP_NAME);
        super.setGroupDescription("Universal group (group of which all users are members)");
    }


    /**
     * boolean check to determine if a user is a member of a group
     * 
     * @param kualiUser
     * @return true if the given user is not null and has a personSystemId
     */
    public boolean hasMember(UniversalUser universalUser) {
        boolean isMember = false;

        if (universalUser != null && universalUser.getPersonUniversalIdentifier() != null) {
            isMember = true;
        }

        return isMember;
    }


    /* illegal operations */
    /**
     * @return list of all kuali users
     */
    public List getGroupUsers() {
        throw new UnsupportedOperationException("can't generate a list of all KualiUsers");
    }


    /**
     * @param groupUsers
     */
    public void setGroupUsers(List groupMembers) {
        throw new UnsupportedOperationException("membership in KualiUniversalGroup cannot be directly changed");
    }

    /**
     * @param groupName
     */
    public void setGroupName(String groupName) {
        throw new UnsupportedOperationException("KualiUniversalGroup name cannot be changed");
    }

    /**
     * @param groupDescription
     */
    public void setGroupDescription(String groupDescription) {
        throw new UnsupportedOperationException("KualiUniversalGroup description cannot be changed");
    }
}