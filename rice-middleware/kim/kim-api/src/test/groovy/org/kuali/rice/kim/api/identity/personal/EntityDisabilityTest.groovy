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

import org.junit.Test
import org.junit.Assert
import org.kuali.rice.kim.api.identity.personal.EntityDisability
import org.kuali.rice.kim.api.identity.personal.EntityDisabilityContract
import org.kuali.rice.kim.api.test.JAXBAssert
import org.kuali.rice.kim.api.identity.CodedAttribute
import org.kuali.rice.kim.api.identity.CodedAttributeContract
import org.joda.time.DateTime

public class EntityDisabilityTest {
    private static final String ID = "1";
    private static final String STATUS_CODE = "P"
    private static final String ENTITY_ID = "190192"
    private static final String TYPE_CODE = "Home"
    private static final String TYPE_NAME = "Home-y"
    private static final String TYPE_SORT_CODE = "0"
    private static final String TYPE_ACTIVE = "true"
    private static final Long TYPE_VERSION_NUMBER = Integer.valueOf(1)
    private static final String TYPE_OBJECT_ID = UUID.randomUUID()

    private static final String ACTIVE = "true"
    private static final Long VERSION_NUMBER = new Integer(1);
    private static final String OBJECT_ID = UUID.randomUUID();

    private static final String XML = """
    <entityDisability xmlns="http://rice.kuali.org/kim/v2_0">
        <id>${ID}</id>
        <entityId>${ENTITY_ID}</entityId>
        <statusCode>${STATUS_CODE}</statusCode>
        <determinationSourceType>
            <code>${TYPE_CODE}</code>
            <name>${TYPE_NAME}</name>
            <active>${TYPE_ACTIVE}</active>
            <sortCode>${TYPE_SORT_CODE}</sortCode>
            <versionNumber>${TYPE_VERSION_NUMBER}</versionNumber>
            <objectId>${TYPE_OBJECT_ID}</objectId>
        </determinationSourceType>
        <accommodationsNeeded>
          <accommodationNeeded>
            <code>${TYPE_CODE}</code>
            <name>${TYPE_NAME}</name>
            <active>${TYPE_ACTIVE}</active>
            <sortCode>${TYPE_SORT_CODE}</sortCode>
            <versionNumber>${TYPE_VERSION_NUMBER}</versionNumber>
            <objectId>${TYPE_OBJECT_ID}</objectId>"
          </accommodationNeeded>
        </accommodationsNeeded>
        <conditionType>
            <code>${TYPE_CODE}</code>
            <name>${TYPE_NAME}</name>
            <active>${TYPE_ACTIVE}</active>
            <sortCode>${TYPE_SORT_CODE}</sortCode>
            <versionNumber>${TYPE_VERSION_NUMBER}</versionNumber>
            <objectId>${TYPE_OBJECT_ID}</objectId>
        </conditionType>
        <active>${ACTIVE}</active>
        <versionNumber>${VERSION_NUMBER}</versionNumber>
        <objectId>${OBJECT_ID}</objectId>
    </entityDisability>
    """

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_id_whitespace() {
        EntityDisability.Builder builder = EntityDisability.Builder.create();
        builder.setId(" ")
    }

    @Test
    void test_copy() {
        def o1 = EntityDisability.Builder.create().build();
        def o2 = EntityDisability.Builder.create(o1).build();

        Assert.assertEquals(o1, o2);
    }

    @Test
    void happy_path() {
        EntityDisability.Builder.create();
    }

    @Test
    public void test_Xml_Marshal_Unmarshal() {
        JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, EntityDisability.class)
    }

    public static create() {
        List<CodedAttribute> accomodations = Collections.singletonList(EntityDisabilityTest.createCodedAttribute());
        return EntityDisability.Builder.create(new EntityDisabilityContract() {
            def String id = EntityDisabilityTest.ID
            def String statusCode = EntityDisabilityTest.STATUS_CODE
            def String entityId = EntityDisabilityTest.ENTITY_ID
            def CodedAttribute getDeterminationSourceType() { return EntityDisabilityTest.createCodedAttribute() }
            def CodedAttribute getConditionType() { return EntityDisabilityTest.createCodedAttribute() }
            def List<CodedAttribute> getAccommodationsNeeded() { return accomodations }
            def boolean active = EntityDisabilityTest.ACTIVE.toBoolean()
            def Long versionNumber = EntityDisabilityTest.VERSION_NUMBER;
            def String objectId = EntityDisabilityTest.OBJECT_ID
        }).build()

    }

    public static createCodedAttribute() {
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
