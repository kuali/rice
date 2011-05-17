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
import org.kuali.rice.kim.api.common.delegate.DelegateContract;

import java.util.List;

public interface RoleMembershipContract {

    String getRoleId();

    AttributeSet getQualifier();

    List<? extends DelegateContract> getDelegates();

    String getRoleMemberId();

    String getMemberId();

    String getMemberTypeCode();

    /**
     * @return String Identifier of the role from which the group or principal was derived.
     */
    String getEmbeddedRoleId();

    /**
     * @return String value used to sort the role members into a meaningful order
     */
    String getRoleSortingCode();
}
