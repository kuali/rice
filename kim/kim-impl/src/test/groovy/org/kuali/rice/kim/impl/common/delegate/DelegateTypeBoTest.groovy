/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.kim.impl.common.delegate

import org.junit.Test
import org.kuali.rice.kim.api.common.delegate.DelegateType
import org.junit.Assert
import org.kuali.rice.kim.api.common.delegate.DelegateMember
import org.kuali.rice.core.api.delegation.DelegationType

class DelegateTypeBoTest {
    @Test
    public void testNotEqualsWithDelegateType() {
        DelegateType.Builder builder = DelegateType.Builder.create('10101', DelegationType.SECONDARY,
                getDelegateMemberBuilders())
        builder.setActive(true)
        DelegateType immutable = builder.build()
        DelegateTypeBo bo = DelegateTypeBo.from(immutable)
        Assert.assertFalse(bo.equals(immutable))
        Assert.assertFalse(immutable.equals(bo))
        Assert.assertEquals(immutable, DelegateTypeBo.to(bo))
    }

    private List<DelegateMember.Builder> getDelegateMemberBuilders() {
        DelegateMember.Builder memberBuilder = DelegateMember.Builder.create()
        memberBuilder.setDelegationMemberId('10001')
        memberBuilder.setVersionNumber(1)
        memberBuilder.setAttributes([:])
        List<DelegateMember.Builder> members = new ArrayList<DelegateMember.Builder>()
        members.add(memberBuilder)

        return members
    }
}
