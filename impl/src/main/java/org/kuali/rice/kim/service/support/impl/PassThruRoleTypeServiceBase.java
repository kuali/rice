/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.service.support.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.KimAttributesTranslator;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.support.KimRoleTypeService;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

public abstract class PassThruRoleTypeServiceBase implements KimRoleTypeService {
	public static final String UNMATCHABLE_QUALIFICATION = "!~!~!~!~!~";

    public abstract AttributeSet convertQualificationForMemberRoles(String namespaceCode, String roleName, AttributeSet qualification);
    
    public AttributeSet convertQualificationAttributesToRequired(AttributeSet qualificationAttributes) {
        return qualificationAttributes;
    }

    public List<RoleMembershipInfo> doRoleQualifiersMatchQualification(AttributeSet qualification, List<RoleMembershipInfo> roleMemberList) {
        return roleMemberList;
    }

    public boolean doesRoleQualifierMatchQualification(AttributeSet qualification, AttributeSet roleQualifier) {
        return true;
    }

    public List<AttributeSet> getAllImpliedQualifications(AttributeSet qualification) {
        List<AttributeSet> result = new ArrayList<AttributeSet>(1);
        result.add(qualification);
        return result;
    }

    public List<AttributeSet> getAllImplyingQualifications(AttributeSet qualification) {
        List<AttributeSet> result = new ArrayList<AttributeSet>(1);
        result.add(qualification);
        return result;
    }

    public List<String> getGroupIdsFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        return null;
    }

    public List<String> getPrincipalIdsFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        return null;
    }

    public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification) {
        return false;
    }

    public boolean isApplicationRoleType() {
        return false;
    }

    public List<String> getAcceptedAttributeNames() {
        return new ArrayList<String>(0);
    }

    public AttributeDefinitionMap getAttributeDefinitions(KimTypeImpl kimType) {
        return null;
    }

    public List<KeyLabelPair> getAttributeValidValues(String attributeName) {
        return new ArrayList<KeyLabelPair>(0);
    }

    public List<KimAttributesTranslator> getKimAttributesTranslators() {
        return new ArrayList<KimAttributesTranslator>(0);
    }

    public String getWorkflowDocumentTypeName() {
        return null;
    }

    public boolean supportsAttributes(List<String> attributeNames) {
        return true;
    }

    public AttributeSet translateInputAttributeSet(AttributeSet inputAttributeSet) {
        return inputAttributeSet;
    }

    public AttributeSet validateAttributes(AttributeSet attributes) {
        return null;
    }

}
