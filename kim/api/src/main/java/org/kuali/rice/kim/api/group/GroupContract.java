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

package org.kuali.rice.kim.api.group;

import org.kuali.rice.core.api.mo.GloballyUnique;
import org.kuali.rice.core.api.mo.Versioned;
import org.kuali.rice.core.api.mo.active.Inactivatable;
import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kim.api.person.PersonContract;

import java.util.List;
import java.util.Set;

public interface GroupContract extends Versioned, GloballyUnique, Inactivatable {
    /**
     * This is the unique id for the Group.  This cannot be null or a blank string.
     *
     * <p>
     * This is a unique Id assigned to a Group.
     * </p>
     *
     * @return id
     */
    String getId();

    /**
     * This is the namespace code for the Group.
     *
     * <p>
     * This is a namespace code assigned to a Group.  Together with name, it makes up another unique identifier for Group
     * </p>
     *
     * @return namespaceCode
     */
    String getNamespaceCode();

    /**
     * This is the name for the Group.
     *
     * <p>
     * This is a name assigned to a Group.  Together with NamespaceCode, it makes up another unique identifier for Group
     * </p>
     *
     * @return name
     */
	String getName();

    /**
     * This a description for the Group.
     *
     * <p>
     * This is a description assigned to a Group.
     * </p>
     *
     * @return description
     */
	String getDescription();

    /**
     * This a Kim Type Id for the Group.
     *
     * <p>
     * This links a Kim Type to the Group to allow custom types of Groups.
     * </p>
     *
     * @return description
     */
	String getKimTypeId();

    /**
     * This a list of the members of the Group.
     *
     * <p>
     * This returns a list of group members
     * </p>
     *
     * @return members
     */
    List<? extends GroupMemberContract> getMembers();

    /*List<? extends PersonContract> getPersonMembers();

    List<? extends GroupContract> getGroupMembers();*/

    /**
     * This is a set of Attributes for a Group.
     *
     * <p>
     * This is a set of attributes which are key-label pairs that are defined by the Group's Kim Type.
     * </p>
     *
     * @return attributes
     */
    List<? extends GroupAttributeContract> getAttributes();




    /**
     * This is a set of Attributes for a Group.
     *
     * <p>
     * This is a set of attributes which are key-label pairs that are defined by the Group's Kim Type.
     * </p>
     *
     * @return attributes
     */
	//AttributeSet getAttributes();

}
