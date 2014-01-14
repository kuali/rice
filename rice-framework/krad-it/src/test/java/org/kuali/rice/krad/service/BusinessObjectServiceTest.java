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
package org.kuali.rice.krad.service;

import org.junit.Test;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateTypeBo;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * BusinessObjectServiceTest tests KULRICE-1666: missing Spring mapping for ojbCollectionHelper
 * (not injected into BusinessObjectDaoTest)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@KRADTestCase.Legacy
public class BusinessObjectServiceTest extends KRADTestCase {

    static final String DELEGATE_ID =  "delegateTypeBo1";
    static final String ROLE_ID = "1";

    /**
     * This method tests saving a BO with a collection member
     *
     * @throws Exception
     */
    @Test
    public void testSave() throws Exception {
        BusinessObjectService businessObjectService = KNSServiceLocator.getBusinessObjectService();

        DelegateTypeBo delegateTypeBo = createDelegateTypeBo();

        businessObjectService.save(delegateTypeBo);
    }
    
    /**
     * Checks that a business object can be correctly retrieved through BusinessObjectService#retrieve
     */
    @Test
    public void testRetrieve() {
        BusinessObjectService businessObjectService = KNSServiceLocator.getBusinessObjectService();

        DelegateTypeBo originalDelegateType = createDelegateTypeBo();

        originalDelegateType = (DelegateTypeBo)businessObjectService.save(originalDelegateType);

        DelegateTypeBo delegateTypeBo2 = (DelegateTypeBo)businessObjectService.retrieve(originalDelegateType);
    	assertNotNull("delegateTypeBo2 should not be null", delegateTypeBo2);
    	assertEquals("delegateTypeBo2 should have the same Kim Type ID as originalDelegateType",
                originalDelegateType.getKimTypeId(), delegateTypeBo2.getKimTypeId());

        DelegateTypeBo delegateTypeBo3 = new DelegateTypeBo();
        delegateTypeBo3.setDelegationId(originalDelegateType.getDelegationId());
        delegateTypeBo2 = (DelegateTypeBo)businessObjectService.retrieve(delegateTypeBo3);
    	assertNotNull("delegateTypeBo2 should not be null", delegateTypeBo2);
    	assertEquals("delegateTypeBo2 should have the same Kim Type ID as originalDelegateType",
                originalDelegateType.getKimTypeId(), delegateTypeBo2.getKimTypeId());

        delegateTypeBo3.setDelegationId("doesNotExist");
        delegateTypeBo2 = (DelegateTypeBo)businessObjectService.retrieve(delegateTypeBo3);
    	assertNull("delegateTypeBo2 should be null", delegateTypeBo2);

        DelegateTypeBo delegateTypeBo4 = new DelegateTypeBo();
        delegateTypeBo4.setDelegationId(originalDelegateType.getDelegationId());
        delegateTypeBo2 = (DelegateTypeBo)businessObjectService.findBySinglePrimaryKey(DelegateTypeBo.class,
                delegateTypeBo4.getDelegationId());
        assertNotNull("delegateTypeBo2 should not be null", delegateTypeBo2);
        assertEquals("delegateTypeBo2 should have the same Kim Type ID as originalDelegateType.",
                originalDelegateType.getKimTypeId(), delegateTypeBo2.getKimTypeId());
    }

    protected DelegateTypeBo createDelegateTypeBo() {
        DelegateTypeBo delegateTypeBo = new DelegateTypeBo();
        delegateTypeBo.setActive(true);
        delegateTypeBo.setDelegationId(DELEGATE_ID);
        delegateTypeBo.setDelegationType(DelegationType.PRIMARY);
        delegateTypeBo.setKimTypeId("1");
        delegateTypeBo.setRoleId(ROLE_ID);

        // Create delegate member1
        DelegateMemberBo member1 = new DelegateMemberBo();
        member1.setDelegationId("delegateMemberBo1");
        member1.setAttributes(Collections.<String, String>emptyMap());
        member1.setDelegationId(DELEGATE_ID);
        member1.setMemberId("delegateMemberBoUser1");
        member1.setRoleMemberId(ROLE_ID);
        member1.setType( MemberType.PRINCIPAL );

        // Create delegate member2
        DelegateMemberBo member2 = new DelegateMemberBo();
        member2.setDelegationId("delegateMemberBo2");
        member2.setAttributes(Collections.<String, String>emptyMap());
        member2.setDelegationId(DELEGATE_ID);
        member2.setMemberId("delegateMemberBoUser2");
        member2.setRoleMemberId(ROLE_ID);
        member2.setType( MemberType.PRINCIPAL );

        List<DelegateMemberBo> delegateMemberBos = new ArrayList<DelegateMemberBo>();
        delegateMemberBos.add(member1);
        delegateMemberBos.add(member2);

        delegateTypeBo.setMembers(delegateMemberBos);

        return delegateTypeBo;
    }
}
