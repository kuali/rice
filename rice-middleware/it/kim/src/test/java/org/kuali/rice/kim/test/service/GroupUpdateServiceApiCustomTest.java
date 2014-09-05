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
package org.kuali.rice.kim.test.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.type.KimTypeAttributeBo;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.test.BaselineTestCase;

import static org.junit.Assert.*;

/**
 * Unit test for {@link org.kuali.rice.kim.api.group.GroupService}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class GroupUpdateServiceApiCustomTest extends KIMTestCase {

    private static final String NAMESPACE = "KUALI";
    private static final String KIM_ATTRIBUTE_ID = "KS-KRIM-ATTR-DEFN-1003";
    private static final String KIM_ATTRIBUTE_LABEL = "Hold Authorization Org Id";
    private static final String KIM_ATTRIBUTE_NAME = "org.kuali.student.hold.authorization.orgId";
    private static final String KIM_ATTRIBUTE_VALUE = "ORGID-128487550";
    private static final String KIM_ATTRIBUTE_COMPONENT_NAME = "org.kuali.rice.student.bo.KualiStudentKimAttributes";
    private static final String KIM_TYPE_ID = "KS-KRIM-TYP-1011";
    private static final String KIM_TYPE_NAME = "KS Hold Org Authorization Group Type";
    private static final String KIM_TYPE_SERVICE_NAME = "kimGroupTypeService";
    private static final String KIM_TYPE_ATTRIBUTE_ID = "KS-KRIM-TYP-ATTR-1027";
    private static final String GROUP_NAME = "gAwAttrs";

    @Test
    public void testCreateGroupWithCustomKimTypeAttributes() {
        // This test needs to be run in separate test class
        // with @BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
        KimAttribute.Builder kimAttribute = KimAttribute.Builder.create(KIM_ATTRIBUTE_COMPONENT_NAME,
                KIM_ATTRIBUTE_NAME, NAMESPACE);
        kimAttribute.setAttributeLabel(KIM_ATTRIBUTE_LABEL);
        kimAttribute.setId(KIM_ATTRIBUTE_ID);
        kimAttribute.setActive(true);
        KimAttributeBo kimAddrBo = KRADServiceLocator.getDataObjectService().save(KimAttributeBo.from(kimAttribute.build()));

        KimType.Builder kimTypeBuilder = KimType.Builder.create();
        kimTypeBuilder.setId(KIM_TYPE_ID);
        kimTypeBuilder.setName(KIM_TYPE_NAME);
        kimTypeBuilder.setNamespaceCode(NAMESPACE);
        kimTypeBuilder.setServiceName(KIM_TYPE_SERVICE_NAME);
        kimTypeBuilder.setActive(true);
        KimTypeBo kimTypeBo = KRADServiceLocator.getDataObjectService().save(KimTypeBo.from(kimTypeBuilder.build()));

        KimTypeAttribute.Builder kimTypeAttributeBuilder = KimTypeAttribute.Builder.create();
        kimTypeAttributeBuilder.setId(KIM_TYPE_ATTRIBUTE_ID);
        kimTypeAttributeBuilder.setActive(true);
        kimTypeAttributeBuilder.setSortCode("a");
        kimTypeAttributeBuilder.setKimTypeId(KIM_TYPE_ID);
        kimTypeAttributeBuilder.setKimAttribute(kimAttribute);
        KimTypeAttributeBo kimTypeAttrBo = KRADServiceLocator.getDataObjectService().save(KimTypeAttributeBo.from(kimTypeAttributeBuilder.build()));

        kimTypeBuilder = KimType.Builder.create(KimApiServiceLocator.getKimTypeInfoService().getKimType(KIM_TYPE_ID));
        kimTypeAttributeBuilder = KimTypeAttribute.Builder.create(kimTypeAttrBo);
        List<KimTypeAttribute.Builder> attrList = Collections.singletonList(kimTypeAttributeBuilder);
        kimTypeBuilder.setAttributeDefinitions(attrList);

        kimTypeBo = KRADServiceLocator.getDataObjectService().save(KimTypeBo.from(kimTypeBuilder.build()));
        KRADServiceLocator.getDataObjectService().flush(KimTypeBo.class);

        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(KIM_ATTRIBUTE_NAME, KIM_ATTRIBUTE_VALUE);

        Group.Builder groupInfo = Group.Builder.create(NAMESPACE, GROUP_NAME, KIM_TYPE_ID);
        groupInfo.setAttributes(attributes);
        groupInfo.setActive(true);
        groupInfo.setDescription(KIM_ATTRIBUTE_LABEL + " " + KIM_ATTRIBUTE_VALUE);
        Group group = KimApiServiceLocator.getGroupService().createGroup(groupInfo.build());

        Group result = KimApiServiceLocator.getGroupService().getGroupByNamespaceCodeAndName(NAMESPACE, GROUP_NAME);
        assertEquals(groupInfo.isActive(), result.isActive());
        assertTrue(groupInfo.getNamespaceCode().equals(result.getNamespaceCode()));
        assertTrue(groupInfo.getName().equals(result.getName()));
        assertTrue(groupInfo.getKimTypeId().equals(result.getKimTypeId()));
        assertEquals(1, result.getAttributes().size());
        assertTrue(result.getAttributes().get(KIM_ATTRIBUTE_NAME).contains(KIM_ATTRIBUTE_VALUE));
    }
}
