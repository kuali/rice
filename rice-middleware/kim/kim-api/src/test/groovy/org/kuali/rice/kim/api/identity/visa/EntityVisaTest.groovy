/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.kim.api.identity.visa

import org.junit.Test
import org.kuali.rice.kim.api.identity.CodedAttribute
import org.kuali.rice.kim.api.identity.CodedAttributeContract
import org.kuali.rice.kim.api.test.JAXBAssert
import org.junit.Assert


class EntityVisaTest {
    private static final String ID = "1"
    private static final String ENTITY_ID = "190192";
    private static final String VISA_ID = "1111111111111111"
    private static final String VISA_TYPE_KEY = "VISA"
    private static final String VISA_ENTRY = "ENTRY"
    private static final Long VERSION_NUMBER = new Integer(1);
    private static final String OBJECT_ID = UUID.randomUUID();

    private static final String TYPE_CODE = "Visa"
    private static final String TYPE_NAME = "Visa-y"
    private static final String TYPE_SORT_CODE = "0"
    private static final String TYPE_ACTIVE = "true"
    private static final Long TYPE_VERSION_NUMBER = Integer.valueOf(1)
    private static final String TYPE_OBJECT_ID = UUID.randomUUID()

    private static final String XML = """
    <entityVisa xmlns="http://rice.kuali.org/kim/v2_0">
        <id>${ID}</id>
        <entityId>${ENTITY_ID}</entityId>
        <visaTypeKey>${VISA_TYPE_KEY}</visaTypeKey>
        <visaEntry>${VISA_ENTRY}</visaEntry>
        <visaId>${VISA_ID}</visaId>
        <versionNumber>${VERSION_NUMBER}</versionNumber>
        <objectId>${OBJECT_ID}</objectId>
        <visaType>
            <code>${TYPE_CODE}</code>
            <name>${TYPE_NAME}</name>
            <active>${TYPE_ACTIVE}</active>
            <sortCode>${TYPE_SORT_CODE}</sortCode>
            <versionNumber>${TYPE_VERSION_NUMBER}</versionNumber>
            <objectId>${TYPE_OBJECT_ID}</objectId>
        </visaType>
    </entityVisa>
    """

    @Test(expected=IllegalArgumentException.class)
    void test_Builder_fail_id_whitespace() {
        EntityVisa.Builder builder = EntityVisa.Builder.create();
        builder.setId("")
    }

    @Test
    void test_copy() {
        def o1 = EntityVisa.Builder.create().build();
        def o2 = EntityVisa.Builder.create(o1).build();

        Assert.assertEquals(o1, o2);
    }

    @Test
    void happy_path() {
        EntityVisa.Builder.create();
    }

    @Test
    public void test_Xml_Marshal_Unmarshal() {
        JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, EntityVisa.class)
    }

    public static create() {
        return EntityVisa.Builder.create(new EntityVisaContract() {
            def String entityId = EntityVisaTest.ENTITY_ID
            def String visaTypeKey = EntityVisaTest.VISA_TYPE_KEY
            def String visaEntry = EntityVisaTest.VISA_ENTRY
            def String visaId = EntityVisaTest.VISA_ID
            def String id = EntityVisaTest.ID
            def Long versionNumber = EntityVisaTest.VERSION_NUMBER;
            def String objectId = EntityVisaTest.OBJECT_ID
            def CodedAttribute getVisaType() { CodedAttribute.Builder.create(new CodedAttributeContract() {
                def String code = EntityVisaTest.TYPE_CODE
                def String name = EntityVisaTest.TYPE_NAME
                def boolean active = EntityVisaTest.TYPE_ACTIVE
                def String sortCode = EntityVisaTest.TYPE_SORT_CODE
                def Long versionNumber = EntityVisaTest.TYPE_VERSION_NUMBER
                def String objectId = EntityVisaTest.TYPE_OBJECT_ID
            }).build()
            }

        }).build()

    }
    
}
