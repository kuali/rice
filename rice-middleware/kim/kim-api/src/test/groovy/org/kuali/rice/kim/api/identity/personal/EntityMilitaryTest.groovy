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
package org.kuali.rice.kim.api.identity.personal

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.junit.Assert
import org.junit.Test
import org.kuali.rice.kim.api.identity.CodedAttribute
import org.kuali.rice.kim.api.identity.CodedAttributeContract
import org.kuali.rice.kim.api.test.JAXBAssert

class EntityMilitaryTest {
    static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final String ID = "1";
    private static final String SELECTIVE_SERVICE = "true"
    private static final String SELECTIVE_SERVICE_NUMBER = "958934"
    private static final String ENTITY_ID = "190192"
    private static final String TYPE_CODE = "Home"
    private static final String TYPE_NAME = "Home-y"
    private static final String TYPE_SORT_CODE = "0"
    private static final String TYPE_ACTIVE = "true"
    private static final Long TYPE_VERSION_NUMBER = Integer.valueOf(1)
    private static final String TYPE_OBJECT_ID = UUID.randomUUID()

    private static final String DISCHARGE_STRING = "2012-01-01 12:00:00"
    private static final DateTime DISCHARGE = new DateTime(FORMATTER.parseDateTime(DISCHARGE_STRING))
    private static final String ACTIVE = "true"
    private static final Long VERSION_NUMBER = new Integer(1);
    private static final String OBJECT_ID = UUID.randomUUID();
    //private static final CodedAttribute RELATIONSHIP_STATUS = createCodedAttribute()

    private static final String XML = """
    <entityMilitary xmlns="http://rice.kuali.org/kim/v2_0">
        <id>${ID}</id>
        <entityId>${ENTITY_ID}</entityId>
        <dischargeDate>${DISCHARGE}</dischargeDate>
        <selectiveService>${SELECTIVE_SERVICE}</selectiveService>
        <selectiveServiceNumber>${SELECTIVE_SERVICE_NUMBER}</selectiveServiceNumber>
        <relationshipStatus>
            <code>${TYPE_CODE}</code>
            <name>${TYPE_NAME}</name>
            <active>${TYPE_ACTIVE}</active>
            <sortCode>${TYPE_SORT_CODE}</sortCode>
            <versionNumber>${TYPE_VERSION_NUMBER}</versionNumber>
            <objectId>${TYPE_OBJECT_ID}</objectId>
        </relationshipStatus>
        <active>${ACTIVE}</active>
        <versionNumber>${VERSION_NUMBER}</versionNumber>
        <objectId>${OBJECT_ID}</objectId>
    </entityMilitary>
    """

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_id_whitespace() {
        EntityMilitary.Builder builder = EntityMilitary.Builder.create();
        builder.setId(" ")
    }

    @Test
    void test_copy() {
        def o1 = EntityMilitary.Builder.create().build();
        def o2 = EntityMilitary.Builder.create(o1).build();

        Assert.assertEquals(o1, o2);
    }

    @Test
    void happy_path() {
        EntityMilitary.Builder.create();
    }

    @Test
    public void test_Xml_Marshal_Unmarshal() {
        JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, EntityMilitary.class)
    }

    public static EntityMilitary create() {
        CodedAttribute
        return EntityMilitary.Builder.create(new EntityMilitaryContract() {
            def String id = EntityMilitaryTest.ID
            def boolean selectiveService = Boolean.valueOf(EntityMilitaryTest.SELECTIVE_SERVICE)
            def String selectiveServiceNumber = EntityMilitaryTest.SELECTIVE_SERVICE_NUMBER
            def DateTime dischargeDate = EntityMilitaryTest.DISCHARGE
            def String entityId = EntityMilitaryTest.ENTITY_ID
            def CodedAttribute getRelationshipStatus() { return EntityMilitaryTest.createRelationshipStatus() }
            def boolean active = EntityMilitaryTest.ACTIVE.toBoolean()
            def Long versionNumber = EntityMilitaryTest.VERSION_NUMBER;
            def String objectId = EntityMilitaryTest.OBJECT_ID
        }).build()

    }

    public static createRelationshipStatus() {
        return CodedAttribute.Builder.create(new CodedAttributeContract() {
            def String code = TYPE_CODE
            def String name = TYPE_NAME
            def boolean active = TYPE_ACTIVE
            def String sortCode = TYPE_SORT_CODE
            def Long versionNumber = TYPE_VERSION_NUMBER
            def String objectId = TYPE_OBJECT_ID
        }).build()
    }

}
