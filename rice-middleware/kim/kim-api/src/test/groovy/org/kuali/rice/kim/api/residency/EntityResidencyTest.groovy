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
package org.kuali.rice.kim.api.residency

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.junit.Test
import org.kuali.rice.kim.api.identity.CodedAttribute
import org.kuali.rice.kim.api.identity.CodedAttributeContract
import org.kuali.rice.kim.api.identity.residency.EntityResidency
import org.junit.Assert
import org.kuali.rice.kim.api.test.JAXBAssert
import org.kuali.rice.kim.api.identity.residency.EntityResidencyContract


class EntityResidencyTest {
    static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final String ENTITY_ID = "190192";
    private static final String ID = "1"
    private static final String DETERMINATION_METHOD = "hmm"
    private static final String IN_STATE = "TX"
    private static final String SUPPRESS_PERSONAL = "false";
    private static final String ESTABLISHED_DATE_STRING = "2012-06-01 12:00:00"
    private static final DateTime ESTABLISHED_DATE = new DateTime(FORMATTER.parseDateTime(ESTABLISHED_DATE_STRING))
    private static final String CHANGE_DATE_STRING = "2012-01-01 12:00:00"
    private static final DateTime CHANGE_DATE = new DateTime(FORMATTER.parseDateTime(CHANGE_DATE_STRING))
    private static final String COUNTRY_CODE = "USA"
    private static final String COUNTY_CODE = "MVN"
    private static final String STATE_PROVINCE_CODE = "TX"

    private static final String TYPE_CODE = "Home"
    private static final String TYPE_NAME = "Home-y"
    private static final String TYPE_SORT_CODE = "0"
    private static final String TYPE_ACTIVE = "true"
    private static final Long TYPE_VERSION_NUMBER = Integer.valueOf(1)
    private static final String TYPE_OBJECT_ID = UUID.randomUUID()

    private static final String STATUS_CODE = "STS"
    private static final String STATUS_NAME = "STS name"
    private static final String STATUS_SORT_CODE = "0"
    private static final String STATUS_ACTIVE = "true"
    private static final Long STATUS_VERSION_NUMBER = Integer.valueOf(1)
    private static final String STATUS_OBJECT_ID = UUID.randomUUID()
    
    private static final Long VERSION_NUMBER = new Integer(1);
	private static final String OBJECT_ID = UUID.randomUUID();

    private static final String XML = """
    <entityResidency xmlns="http://rice.kuali.org/kim/v2_0">
        <id>${ID}</id>
        <entityId>${ENTITY_ID}</entityId>
        <determinationMethod>${DETERMINATION_METHOD}</determinationMethod>
        <inState>${IN_STATE}</inState>
        <establishedDate>${ESTABLISHED_DATE}</establishedDate>
        <changeDate>${CHANGE_DATE}</changeDate>
        <countryCode>${COUNTRY_CODE}</countryCode>
        <countyCode>${COUNTY_CODE}</countyCode>
        <stateProvinceCode>${STATE_PROVINCE_CODE}</stateProvinceCode>
        <residencyType>
            <code>${TYPE_CODE}</code>
            <name>${TYPE_NAME}</name>
            <active>${TYPE_ACTIVE}</active>
            <sortCode>${TYPE_SORT_CODE}</sortCode>
            <versionNumber>${TYPE_VERSION_NUMBER}</versionNumber>
            <objectId>${TYPE_OBJECT_ID}</objectId>
        </residencyType>
        <residencyStatus>
            <code>${STATUS_CODE}</code>
            <name>${STATUS_NAME}</name>
            <active>${STATUS_ACTIVE}</active>
            <sortCode>${STATUS_SORT_CODE}</sortCode>
            <versionNumber>${STATUS_VERSION_NUMBER}</versionNumber>
            <objectId>${STATUS_OBJECT_ID}</objectId>
        </residencyStatus>
        <versionNumber>${VERSION_NUMBER}</versionNumber>
        <objectId>${OBJECT_ID}</objectId>
    </entityResidency>
    """

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_id_whitespace() {
        EntityResidency.Builder builder = EntityResidency.Builder.create();
        builder.setId("")
    }

    @Test
    void test_copy() {
        def o1 = EntityResidency.Builder.create().build();
        def o2 = EntityResidency.Builder.create(o1).build();

        Assert.assertEquals(o1, o2);
    }

    @Test
    void happy_path() {
        EntityResidency.Builder.create();
    }

    @Test
	public void test_Xml_Marshal_Unmarshal() {
		JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, EntityResidency.class)
	}

    public static create() {
		return EntityResidency.Builder.create(new EntityResidencyContract() {
            def String id = EntityResidencyTest.ID
            def String entityId = EntityResidencyTest.ENTITY_ID
            def String determinationMethod = EntityResidencyTest.DETERMINATION_METHOD
            def String inState = EntityResidencyTest.IN_STATE
            def Long versionNumber = EntityResidencyTest.VERSION_NUMBER
			def String objectId = EntityResidencyTest.OBJECT_ID
            def DateTime establishedDate = EntityResidencyTest.ESTABLISHED_DATE
            def DateTime changeDate = EntityResidencyTest.CHANGE_DATE
            def String countryCode = EntityResidencyTest.COUNTRY_CODE
            def String countyCode =  EntityResidencyTest.COUNTY_CODE
            def String stateProvinceCode = EntityResidencyTest.STATE_PROVINCE_CODE
            def CodedAttribute getResidencyStatus() { return EntityResidencyTest.createStatus() }
            def CodedAttribute getResidencyType() { return EntityResidencyTest.createType() }
        }).build()

	}

    public static createType() {
        return CodedAttribute.Builder.create(new CodedAttributeContract() {
            def String code = TYPE_CODE
            def String name = TYPE_NAME
            def boolean active = TYPE_ACTIVE
            def String sortCode = TYPE_SORT_CODE
            def Long versionNumber = TYPE_VERSION_NUMBER
            def String objectId = TYPE_OBJECT_ID
        }).build()
    }

    public static createStatus() {
        return CodedAttribute.Builder.create(new CodedAttributeContract() {
            def String code = STATUS_CODE
            def String name = STATUS_NAME
            def boolean active = STATUS_ACTIVE
            def String sortCode = STATUS_SORT_CODE
            def Long versionNumber = STATUS_VERSION_NUMBER
            def String objectId = STATUS_OBJECT_ID
        }).build()
    }
}
