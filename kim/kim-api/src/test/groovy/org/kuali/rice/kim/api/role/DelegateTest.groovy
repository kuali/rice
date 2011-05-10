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
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import org.junit.Assert
import org.junit.Test
import org.kuali.rice.core.util.AttributeSet

class DelegateTest {

    private final shouldFail = new GroovyTestCase().&shouldFail

    static final String DELEGATION_ID = "1001";
    static final String DELEGATION_TYPE_CODE = "P";
    static final String MEMBER_ID = "1";
    static final String MEMBER_TYPE_CODE = "G";
    static final AttributeSet QUALIFIER = new AttributeSet();
    static final String ROLE_MEMBER_ID = "22";


    private static final String XML = """
        <delegate xmlns="http://rice.kuali.org/kim/v2_0">
            <delegationId>${DELEGATION_ID}</delegationId>
            <delegationTypeCode>${DELEGATION_TYPE_CODE}</delegationTypeCode>
            <memberId>${MEMBER_ID}</memberId>
            <memberTypeCode>${MEMBER_TYPE_CODE}</memberTypeCode>
            <qualifier> </qualifier>
            <roleMemberId>${ROLE_MEMBER_ID}</roleMemberId>
        </delegate>
    """

    @Test
    public void testXmlMarshalingAndUnMarshalling() {
        JAXBContext jc = JAXBContext.newInstance(Delegate.class)
        Marshaller marshaller = jc.createMarshaller()
        StringWriter sw = new StringWriter()

        Delegate.Builder builder = Delegate.Builder.create(DELEGATION_ID, DELEGATION_TYPE_CODE, MEMBER_ID, MEMBER_TYPE_CODE, ROLE_MEMBER_ID, QUALIFIER)
        Delegate delegate = builder.build()
        marshaller.marshal(delegate, sw)
        String xml = sw.toString()

        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Object actual = unmarshaller.unmarshal(new StringReader(xml))
        Object expected = unmarshaller.unmarshal(new StringReader(XML))
        Assert.assertEquals(expected, actual)
    }

    @Test
    public void testXmlUnmarshal() {
        JAXBContext jc = JAXBContext.newInstance(Delegate.class)
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        Delegate delegate = (Delegate) unmarshaller.unmarshal(new StringReader(XML))
        Assert.assertEquals(DELEGATION_ID, delegate.delegationId)
        Assert.assertEquals(DELEGATION_TYPE_CODE, delegate.delegationTypeCode)
        Assert.assertEquals(MEMBER_ID, delegate.memberId)
        Assert.assertEquals(MEMBER_TYPE_CODE, delegate.memberTypeCode)
        Assert.assertEquals(ROLE_MEMBER_ID, delegate.roleMemberId)
        Assert.assertEquals(QUALIFIER, delegate.qualifier)
    }


    @Test
    public void test_Builder() {
        Delegate.Builder.create(
                DELEGATION_ID, DELEGATION_TYPE_CODE, MEMBER_ID, MEMBER_TYPE_CODE, ROLE_MEMBER_ID, QUALIFIER);
    }

    @Test
    public void test_Builder_setDelegationTypeCode_blank() {
        Delegate.Builder b = Delegate.Builder.create(
                DELEGATION_ID, DELEGATION_TYPE_CODE, MEMBER_ID, MEMBER_TYPE_CODE, ROLE_MEMBER_ID, QUALIFIER);
        shouldFail(IllegalArgumentException) {
            b.setDelegationTypeCode(" ")
        }
    }

    @Test
    public void test_Builder_setDelegationTypeCode_null() {
        Delegate.Builder b = Delegate.Builder.create(
                DELEGATION_ID, DELEGATION_TYPE_CODE, MEMBER_ID, MEMBER_TYPE_CODE, ROLE_MEMBER_ID, QUALIFIER);
        shouldFail(IllegalArgumentException) {
            b.setDelegationTypeCode(null)
        }
    }

    @Test
    public void test_Builder_setMemberId_blank() {
        Delegate.Builder b = Delegate.Builder.create(
                DELEGATION_ID, DELEGATION_TYPE_CODE, MEMBER_ID, MEMBER_TYPE_CODE, ROLE_MEMBER_ID, QUALIFIER);
        shouldFail(IllegalArgumentException) {
            b.setMemberId("")
        }
    }

    @Test
    public void test_Builder_setMemberId_null() {
        Delegate.Builder b = Delegate.Builder.create(
                DELEGATION_ID, DELEGATION_TYPE_CODE, MEMBER_ID, MEMBER_TYPE_CODE, ROLE_MEMBER_ID, QUALIFIER);
        shouldFail(IllegalArgumentException) {
            b.setMemberId(null)
        }
    }



    @Test
    public void test_Builder_setMemberTypeCode_blank() {
        Delegate.Builder b = Delegate.Builder.create(
                DELEGATION_ID, DELEGATION_TYPE_CODE, MEMBER_ID, MEMBER_TYPE_CODE, ROLE_MEMBER_ID, QUALIFIER);
        shouldFail(IllegalArgumentException) {
            b.setMemberTypeCode("")
        }
    }

    @Test
    public void test_Builder_setMemberTypeCode_null() {
        Delegate.Builder b = Delegate.Builder.create(
                DELEGATION_ID, DELEGATION_TYPE_CODE, MEMBER_ID, MEMBER_TYPE_CODE, ROLE_MEMBER_ID, QUALIFIER);
        shouldFail(IllegalArgumentException) {
            b.setMemberTypeCode(null)
        }
    }



    @Test
    public void test_Builder_setDelegationId_blank() {
        Delegate.Builder b = Delegate.Builder.create(
                DELEGATION_ID, DELEGATION_TYPE_CODE, MEMBER_ID, MEMBER_TYPE_CODE, ROLE_MEMBER_ID, QUALIFIER);
        shouldFail(IllegalArgumentException) {
            b.setDelegationId("")
        }
    }

    @Test
    public void test_Builder_setDelegationId_null() {
        Delegate.Builder b = Delegate.Builder.create(
                DELEGATION_ID, DELEGATION_TYPE_CODE, MEMBER_ID, MEMBER_TYPE_CODE, ROLE_MEMBER_ID, QUALIFIER);
        shouldFail(IllegalArgumentException) {
            b.setDelegationId(null)
        }
    }

    @Test
    public void test_Builder_setRoleMemberId_blank() {
        Delegate.Builder b = Delegate.Builder.create(
                DELEGATION_ID, DELEGATION_TYPE_CODE, MEMBER_ID, MEMBER_TYPE_CODE, ROLE_MEMBER_ID, QUALIFIER);
        shouldFail(IllegalArgumentException) {
            b.setRoleMemberId("")
        }
    }

    @Test
    public void test_Builder_setRoleMemberId_null() {
        Delegate.Builder b = Delegate.Builder.create(
                DELEGATION_ID, DELEGATION_TYPE_CODE, MEMBER_ID, MEMBER_TYPE_CODE, ROLE_MEMBER_ID, QUALIFIER);
        shouldFail(IllegalArgumentException) {
            b.setRoleMemberId(null)
        }
    }


}
