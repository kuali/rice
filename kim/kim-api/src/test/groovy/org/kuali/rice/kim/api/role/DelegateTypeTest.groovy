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



package org.kuali.rice.kim.api.role

import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller
import org.junit.Assert
import org.junit.Test
import javax.xml.bind.Marshaller

class DelegateTypeTest {

    private final shouldFail = new GroovyTestCase().&shouldFail

    private static final String ROLE_ID = "1"
    private static final String DELEGATION_ID = "42"
    private static final String DELEGATION_TYPE_CODE = "P"
    private static final String KIM_TYPE_ID = "187"
    private static final List<Delegate.Builder> DELEGATES = [create_delegate()]
    private static final boolean ACTIVE = true

    private static create_delegate() {
        return Delegate.Builder.create(
                DelegateTest.DELEGATION_ID, DelegateTest.DELEGATION_TYPE_CODE, DelegateTest.MEMBER_ID,
                DelegateTest.MEMBER_TYPE_CODE, DelegateTest.ROLE_MEMBER_ID, DelegateTest.QUALIFIER)
    }

    private static final String XML = """
      <delegateType xmlns="http://rice.kuali.org/kim/v2_0">
        <roleId>${ROLE_ID}</roleId>
        <delegationId>${DELEGATION_ID}</delegationId>
        <delegationTypeCode>${DELEGATION_TYPE_CODE}</delegationTypeCode>
        <kimTypeId>${KIM_TYPE_ID}</kimTypeId>
        <members>
            <delegationId>${DelegateTest.DELEGATION_ID}</delegationId>
            <delegationTypeCode>${DelegateTest.DELEGATION_TYPE_CODE}</delegationTypeCode>
            <memberId>${DelegateTest.MEMBER_ID}</memberId>
            <memberTypeCode>${DelegateTest.MEMBER_TYPE_CODE}</memberTypeCode>
            <qualifier> </qualifier>
            <roleMemberId>${DelegateTest.ROLE_MEMBER_ID}</roleMemberId>
        </members>
        <active>${ACTIVE}</active>
      </delegateType>
    """

    @Test
    void testXmlUnmarshall() {
        List<Delegate> delegateMembers = new ArrayList<Delegate>();
        for (Delegate.Builder delgateBuilder: DELEGATES) {
            delegateMembers.add(delgateBuilder.build());
        }

        JAXBContext jc = JAXBContext.newInstance(DelegateType.class)
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        DelegateType delegateType = (DelegateType) unmarshaller.unmarshal(new StringReader(XML))
        Assert.assertEquals(ROLE_ID, delegateType.roleId)
        Assert.assertEquals(DELEGATION_ID, delegateType.delegationId)
        Assert.assertEquals(DELEGATION_TYPE_CODE, delegateType.delegationTypeCode)
        Assert.assertEquals(KIM_TYPE_ID, delegateType.kimTypeId)
        Assert.assertEquals(delegateMembers, delegateType.members)
        Assert.assertEquals(ACTIVE, delegateType.active)
    }

    @Test
    public void testXmlMarshalingAndUnMarshalling() {
        JAXBContext jc = JAXBContext.newInstance(DelegateType.class)
        Marshaller marshaller = jc.createMarshaller()
        StringWriter sw = new StringWriter()

        DelegateType.Builder builder = DelegateType.Builder.create(ROLE_ID, DELEGATION_ID, DELEGATION_TYPE_CODE, DELEGATES);
        builder.kimTypeId = KIM_TYPE_ID
        marshaller.marshal(builder.build(), sw)
        String xml = sw.toString()

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Object actual = unmarshaller.unmarshal(new StringReader(xml))
        Object expected = unmarshaller.unmarshal(new StringReader(XML))
        Assert.assertEquals(expected, actual)
    }


    @Test
    void test_builder() {
        DelegateType dt = DelegateType.Builder.create(ROLE_ID, DELEGATION_ID, DELEGATION_TYPE_CODE, DELEGATES).build()
    }

    @Test
    void test_immutableListOfDelegates() {
        DelegateType dt = DelegateType.Builder.create(ROLE_ID, DELEGATION_ID, DELEGATION_TYPE_CODE, DELEGATES).build()
        List<Delegate> delegates = dt.members;
        shouldFail(UnsupportedOperationException) {
            delegates.add(null)
        }
    }

    @Test
    void test_builderForContract() {
        DelegateType dt = DelegateType.Builder.create(ROLE_ID, DELEGATION_ID, DELEGATION_TYPE_CODE, DELEGATES).build()
        DelegateType clone = DelegateType.Builder.create(dt).build();
        Assert.assertEquals(dt, clone)
    }

    @Test
    void test_setRoleId_blank() {
        DelegateType.Builder b = DelegateType.Builder.create(ROLE_ID, DELEGATION_ID, DELEGATION_TYPE_CODE, DELEGATES)
        shouldFail(IllegalArgumentException) {
            b.roleId = " "
        }
    }

    @Test
    void test_setRoleId_null() {
        DelegateType.Builder b = DelegateType.Builder.create(ROLE_ID, DELEGATION_ID, DELEGATION_TYPE_CODE, DELEGATES)
        shouldFail(IllegalArgumentException) {
            b.roleId = null
        }
    }

    @Test
    void test_setDelegationId_blank() {
        DelegateType.Builder b = DelegateType.Builder.create(ROLE_ID, DELEGATION_ID, DELEGATION_TYPE_CODE, DELEGATES)
        shouldFail(IllegalArgumentException) {
            b.delegationId = ""
        }
    }

    @Test
    void test_setDelegationId_null() {
        DelegateType.Builder b = DelegateType.Builder.create(ROLE_ID, DELEGATION_ID, DELEGATION_TYPE_CODE, DELEGATES)
        shouldFail(IllegalArgumentException) {
            b.delegationId = null
        }
    }

    @Test
    void test_setDelegationTypeCode_blank() {
        DelegateType.Builder b = DelegateType.Builder.create(ROLE_ID, DELEGATION_ID, DELEGATION_TYPE_CODE, DELEGATES)
        shouldFail(IllegalArgumentException) {
            b.delegationTypeCode = ""
        }
    }

    @Test
    void test_setDelegationTypeCode_null() {
        DelegateType.Builder b = DelegateType.Builder.create(ROLE_ID, DELEGATION_ID, DELEGATION_TYPE_CODE, DELEGATES)
        shouldFail(IllegalArgumentException) {
            b.delegationTypeCode = null
        }
    }


}
