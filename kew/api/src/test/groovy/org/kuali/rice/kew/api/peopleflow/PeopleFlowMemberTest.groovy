/*
 * Copyright 2011 The Kuali Foundation
 *
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
package org.kuali.rice.kew.api.peopleflow

import org.junit.Assert
import org.junit.Test
import org.kuali.rice.core.test.JAXBAssert

/**
 * Unit test for RuleValidationContext object
 */
class PeopleFlowMemberTest {

    private static final String MINIMAL_XML = """
    <peopleFlowMember>
        <memberId>admin</memberId>
        <memberType>P</memberType>
        <priority>1</priority>
    </peopleFlowMember>
    """

    private static final String MAXIMAL_XML = """
    <peopleFlowMember>
        <id>1</id>
        <peopleFlowId>2</peopleFlowId>
        <memberId>admin</memberId>
        <memberType>P</memberType>
        <priority>10</priority>
        <delegatedFromId>3</delegatedFromId>
        <versionNumber>1</versionNumber>
    </peopleFlowMember>
    """

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_invalid_null_memberId() {
        PeopleFlowMember.Builder.create(null, MemberType.PRINCIPAL)
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_invalid_null_memberType() {
        PeopleFlowMember.Builder.create("admin", null)
    }

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_invalid_null_contract() {
        PeopleFlowMember.Builder.create(null)
    }

    @Test
    void test_Builder_minimal() {
        PeopleFlowMember.Builder builder = createMinimal()
        Assert.assertNotNull(builder)
        assert "admin" == builder.getMemberId()
        assert MemberType.PRINCIPAL == builder.getMemberType()
        // should be initialized to default
        assert 1 == builder.getPriority()

        PeopleFlowMember member = builder.build()
        Assert.assertNotNull(member)
        assert "admin" == member.getMemberId()
        assert MemberType.PRINCIPAL == member.getMemberType()
        assert 1 == member.getPriority()
        Assert.assertNull(member.getPeopleFlowId())
        Assert.assertNull(member.getDelegatedFromId())
        Assert.assertNull(member.getId())
        Assert.assertNull(member.getVersionNumber())
    }

    @Test
    void test_Builder_maximal() {
        PeopleFlowMember.Builder builder = createMaximal()
        PeopleFlowMember member = builder.build()
        Assert.assertNotNull(member)
        assert "admin" == member.getMemberId()
        assert MemberType.PRINCIPAL == member.getMemberType()
        assert 10 == member.getPriority()
        assert "2" == member.getPeopleFlowId()
        assert "3" == member.getDelegatedFromId()
        assert "1" == member.getId()
        assert 1 == member.getVersionNumber()
    }

    @Test
	void test_Xml_Marshal_Unmarshal_minimal() {
        JAXBAssert.assertEqualXmlMarshalUnmarshal(createMinimal().build(), MINIMAL_XML, PeopleFlowMember.class)
	}

    @Test
	void test_Xml_Marshal_Unmarshal_maximal() {
        JAXBAssert.assertEqualXmlMarshalUnmarshal(createMaximal().build(), MAXIMAL_XML, PeopleFlowMember.class)
	}

    private PeopleFlowMember.Builder createMinimal() {
        return PeopleFlowMember.Builder.create("admin", MemberType.PRINCIPAL)
    }

    private PeopleFlowMember.Builder createMaximal() {
        PeopleFlowMember.Builder builder = PeopleFlowMember.Builder.create("admin", MemberType.PRINCIPAL)
        builder.setId("1")
        builder.setPriority(10)
        builder.setPeopleFlowId("2")
        builder.setDelegatedFromId("3")
        builder.setVersionNumber(1)
        return builder
    }

}