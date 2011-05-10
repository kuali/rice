/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kim.api.role;

import org.kuali.rice.core.util.AttributeSet;

public interface DelegateContract {

    /**
     * <p>String representing the DelegationTypeCode</p>
     * <p>Examples are
     * <ul>
     *     <li>"P" - Primary </li>
     *     <li>"S" - Secondary</li>
     * </ul>
     * </p>
     *
     * This field should always be non-null
     *
     * @return The DelegationTypeCode
     */
    String getDelegationTypeCode();

    /**
     * String identifier of the Member.  Should always be a non-null value.
     *
     * @return the member identifier of this Delegate
     */
    String getMemberId();

    /**
     * <p>A string representation of the Member Type of this Delegate
     *
     * <p>Examples are
     * <ul>
     *     <li>"P" - Person</li>
     *     <li>"G" - Group</li>
     *     <li>"R" - Role</li>
     * </ul>
     * </p>
     *
     * This field should always be non-null.
     *
     * @return String representing the member type.
     */
    String getMemberTypeCode();

    /**
     * Returns any qualifiers associated with this Delegate.
     * @return AttributeSet (Map<String,String>) of qualifiers
     */
    AttributeSet getQualifier();

    /**
     * Identifier for this Delegate.  Cannot be null or an empty String.
     * @return String identifier of this delegation
     */
    String getDelegationId();

    /**
     * String identifier of the associated RoleMember.  Cannot be null or an empty String.
     * @return String identifier of the associated RoleMember
     */
    String getRoleMemberId();

    /**
     * @return The name of the Member
     */
    String getMemberName();

    /**
     * The namespace for the member.  A namespace identifies the system/module to which this member applies.
     *
     * @return Namespace for the member.
     */
    String getMemberNamespaceCode();

    /**
     * @return the String identifier of delegation member
     */
    String getDelegationMemberId();

    /**
     * @return the role Id
     */
    String getRoleId();
}
