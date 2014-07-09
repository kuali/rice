/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.kim.service.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.impl.role.RoleMemberBo;
import org.kuali.rice.kim.test.KIMTestCase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class InactiveMemberOnlyTest extends KIMTestCase {

    @Test
    public void testInactiveRoleMemberOnly() {

        boolean testResult = false ;

        KimType.Builder ktBuilder = KimType.Builder.create();
        ktBuilder.setId("1");
        ktBuilder.setNamespaceCode("KUALI");
        ktBuilder.setName("Default");
        Long version = new Long(1) ;
        ktBuilder.setVersionNumber(version);
        KimType kt = ktBuilder.build() ;

        IdentityManagementRoleDocument identityManagementRoleDocument = new IdentityManagementRoleDocument();
        identityManagementRoleDocument.setKimType(kt);
        identityManagementRoleDocument.setRoleId("KRSAP10003");
        identityManagementRoleDocument.setRoleTypeId("1");
        identityManagementRoleDocument.setRoleName("Default");
        identityManagementRoleDocument.setRoleNamespace("KR_SAP");
        identityManagementRoleDocument.setRoleName("Sample App Admin");

        RoleMemberBo roleMemberBo = new RoleMemberBo();
        roleMemberBo.setId("KRSAP10003");
        roleMemberBo.setRoleId("KRSAP1003");
        roleMemberBo.setMemberId("dev1");
        roleMemberBo.setTypeCode("P");

        // make the role member inactive
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        roleMemberBo.setActiveToDateValue(timestamp);
        ArrayList<RoleMemberBo> roleMemberBos = new ArrayList<RoleMemberBo>();
        roleMemberBos.add(roleMemberBo);

        // We've got one role member, and it is inactive.
        UiDocumentServiceImpl uiDocumentServiceImpl = new UiDocumentServiceImpl() ;
        List<KimDocumentRoleMember> kimDocumentRoleMembers = uiDocumentServiceImpl.loadRoleMembers(identityManagementRoleDocument, roleMemberBos);

        if (kimDocumentRoleMembers.isEmpty()) {
            testResult = true;
        }

        assertTrue("loadRoleMembers failed with single inactive member.", testResult);
    }
}

