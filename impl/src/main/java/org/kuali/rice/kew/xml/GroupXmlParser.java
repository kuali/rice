/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.kuali.rice.kew.exception.InvalidXmlException;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.util.XmlHelper;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimTypeAttributeImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.xml.sax.SAXException;



/**
 * Parses groups from XML.
 *
 * @see KimGroups
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com) *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupXmlParser implements XmlConstants {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GroupXmlParser.class);
    private static final boolean DEFAULT_ACTIVE_VALUE = true;
    private static final String DEFAULT_GROUP_DESCRIPTION = "";
    private HashMap<String, List<String>> memberGroupIds = new HashMap<String, List<String>>();
    private HashMap<String, List<String>> memberGroupNames = new HashMap<String, List<String>>();
    private HashMap<String, List<String>> memberPrincipalIds = new HashMap<String, List<String>>();
    private AttributeSet groupAttributes = new AttributeSet();

    public List<GroupInfo> parseGroups(InputStream input) throws IOException, InvalidXmlException {
        try {
            Document doc = XmlHelper.trimSAXXml(input);
            Element root = doc.getRootElement();
            return parseGroups(root);
        } catch (JDOMException e) {
            throw new InvalidXmlException("Parse error.", e);
        } catch (SAXException e){
            throw new InvalidXmlException("Parse error.",e);
        } catch(ParserConfigurationException e){
            throw new InvalidXmlException("Parse error.",e);
        }
    }


    /**
     * Parses and saves groups
     * @param element top-level 'data' element which should contain a <groups> child element
     * @return a list of parsed and saved, current, groups;
     * @throws InvalidXmlException
     */
    public List<GroupInfo> parseGroups(Element element) throws InvalidXmlException {
        List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();
        for (Element groupsElement: (List<Element>) element.getChildren(GROUPS, GROUP_NAMESPACE)) {

            for (Element groupElement: (List<Element>) groupsElement.getChildren(GROUP, GROUP_NAMESPACE)) {
                groupInfos.add(parseGroup(groupElement));
            }
        }
        for (GroupInfo groupInfo : groupInfos) {
            GroupService groupService = KIMServiceLocator.getGroupService();

            // check if group already exists
            GroupInfo foundGroup = groupService.getGroupInfoByName(groupInfo.getNamespaceCode(), groupInfo.getGroupName());

            if (foundGroup == null) {
                LOG.error("Group named '" + groupInfo.getGroupName() + "' not found, creating new group named '" + groupInfo.getGroupName() + "'");
                try {
                    GroupInfo newGroupInfo =  groupService.createGroup(groupInfo);

                    String key = newGroupInfo.getNamespaceCode().trim() + ":" + newGroupInfo.getGroupName().trim();
                    //now we should have group Id, so add members to group
                    List<String> groupIds = memberGroupIds.get(key);
                    if (groupIds != null) {
                        for (String groupId : groupIds) {
                            KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroup(groupId);
                            if (group != null) {
                                groupService.addGroupToGroup(group.getGroupId(), newGroupInfo.getGroupId());
                            } else {
                                throw new InvalidXmlException("Group Id "+groupId+" cannot be found.");
                            }
                        }
                    }
                    List<String> groupNames = memberGroupNames.get(key);
                    if (groupNames != null) {
                        for (String groupName : groupNames) {
                            KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroupByName(Utilities.parseGroupNamespaceCode(groupName), Utilities.parseGroupName(groupName));
                            if (group != null) {
                                groupService.addGroupToGroup(group.getGroupId(), newGroupInfo.getGroupId());
                            } else {
                                throw new InvalidXmlException("Group "+groupName+" cannot be found.");
                            }
                        }
                    }
                    List<String> principalIds = memberPrincipalIds.get(key);
                    if (principalIds != null) {
                        for (String principalId : principalIds) {
                            groupService.addPrincipalToGroup(principalId, newGroupInfo.getGroupId());
                        }
                    }
                    KIMServiceLocator.getIdentityManagementService().flushGroupCaches();
                } catch (Exception e) {
                    throw new RuntimeException("Error creating group.", e);
                }
            } else {
                LOG.error("Rule named '" + groupInfo.getGroupName() + "' found, creating a new version");
                try {
                    groupInfo.setGroupId(foundGroup.getGroupId());
                    groupService.updateGroup(foundGroup.getGroupId(), groupInfo);
                } catch (Exception e) {
                    throw new RuntimeException("Error updating group.", e);
                }
            }
        }
        return groupInfos;
    }

    private GroupInfo parseGroup(Element element) throws InvalidXmlException {
        GroupInfo groupInfo = new GroupInfo();
        //GroupService groupService = KIMServiceLocator.getGroupService();
        IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();
        groupInfo.setGroupName(element.getChildText(NAME, GROUP_NAMESPACE));

        if (groupInfo.getGroupName() == null) {
            throw new InvalidXmlException("Group must have a name.");
        }

        String groupNamespace = element.getChildText(NAMESPACE, GROUP_NAMESPACE);
        if (groupNamespace != null) {
            groupInfo.setNamespaceCode(groupNamespace.trim());
        } else {
            throw new InvalidXmlException("Namespace must have a value.");
        }

        String id = element.getChildText(ID, GROUP_NAMESPACE);
        if (id != null) {
            groupInfo.setGroupId(id.trim());
        }

        String description = element.getChildText(DESCRIPTION, GROUP_NAMESPACE);
        if (description != null && !description.trim().equals("")) {
            groupInfo.setGroupDescription(description);
        }

        // Type element and children (namespace and name)
        String typeId = null;
        List<KimTypeAttributeImpl> kimTypeAttributes = new ArrayList<KimTypeAttributeImpl>();
        if (element.getChild(TYPE, GROUP_NAMESPACE) != null) {
            Element typeElement = element.getChild(TYPE, GROUP_NAMESPACE);
            String typeNamespace = typeElement.getChildText(NAMESPACE, GROUP_NAMESPACE);
            String typeName = typeElement.getChildText(NAME, GROUP_NAMESPACE);
            KimTypeImpl kimTypeImpl = KIMServiceLocator.getTypeInternalService().getKimTypeByName(typeNamespace, typeName);
            if (kimTypeImpl != null) {
                groupInfo.setKimTypeId(kimTypeImpl.getKimTypeId());
                kimTypeAttributes = kimTypeImpl.getAttributeDefinitions();
                typeId = kimTypeImpl.getKimTypeId();
            } else  {
                throw new InvalidXmlException("Invalid type name and namespace specified.");
            }
        } else { //set to default type
            KimTypeImpl kimTypeDefault = KIMServiceLocator.getTypeInternalService().getKimTypeByName(KimConstants.KIM_TYPE_DEFAULT_NAMESPACE, KimConstants.KIM_TYPE_DEFAULT_NAME);
            if (kimTypeDefault != null) {
                groupInfo.setKimTypeId(kimTypeDefault.getKimTypeId());
                kimTypeAttributes = kimTypeDefault.getAttributeDefinitions();
            }
        }

        //Active Indicator
        groupInfo.setActive(DEFAULT_ACTIVE_VALUE);
        if (element.getChildText(ACTIVE, GROUP_NAMESPACE) != null) {
            String active = element.getChildText(ACTIVE, GROUP_NAMESPACE).trim();
            if (active.toUpperCase().equals("N") || active.toUpperCase().equals("FALSE")) {
                groupInfo.setActive(false);
            }
        }

        //Get list of attribute keys
        List<String> validAttributeKeys = new ArrayList<String>();
        for (KimTypeAttributeImpl attribute : kimTypeAttributes) {
            validAttributeKeys.add(attribute.getKimAttribute().getAttributeName());
        }
        //Group attributes
        if (element.getChild(ATTRIBUTES, GROUP_NAMESPACE) != null) {
            List<Element> attributes = element.getChild(ATTRIBUTES, GROUP_NAMESPACE).getChildren();
            AttributeSet attributeSet = new AttributeSet();
            for (Element attr : attributes ) {
                String key = attr.getAttributeValue(KEY);
                String value = attr.getAttributeValue(VALUE);
                attributeSet.put(key, value);
                if (!validAttributeKeys.contains(key)) {
                    throw new InvalidXmlException("Invalid attribute specified.");
                }
            }
            if (attributeSet.size() > 0) {
                groupInfo.setAttributes(attributeSet);
            }
        }

        //Group members

        List<Element> members = element.getChild(MEMBERS, GROUP_NAMESPACE).getChildren();
        for (Element member : members) {
            String elementName = member.getName().trim();
            if (elementName.equals(PRINCIPAL_NAME)) {
                String principalName = member.getText().trim();
                KimPrincipal principal = identityManagementService.getPrincipalByPrincipalName(principalName);
                if (principal != null) {
                    addPrincipalToGroup(groupInfo.getNamespaceCode(), groupInfo.getGroupName(), principal.getPrincipalId());
                } else {
                    throw new InvalidXmlException("Principal Name "+principalName+" cannot be found.");
                }
            } else if (elementName.equals(PRINCIPAL_ID)) {
                String xmlPrincipalId = member.getText().trim();
                KimPrincipal principal = identityManagementService.getPrincipal(xmlPrincipalId);
                if (principal != null) {
                    addPrincipalToGroup(groupInfo.getNamespaceCode(), groupInfo.getGroupName(), principal.getPrincipalId());
                } else {
                    throw new InvalidXmlException("Principal Id "+xmlPrincipalId+" cannot be found.");
                }
            // Groups are handled differently since the member group may not be saved yet.  Therefore they need to be validated after the groups are saved.
            } else if (elementName.equals(GROUP_ID)) {
                String xmlGroupId = member.getText().trim();
                addGroupToGroup(groupInfo.getNamespaceCode(), groupInfo.getGroupName(), xmlGroupId);
            } else if (elementName.equals(GROUP_NAME)) {
                String xmlGroupName = member.getChildText(NAME, GROUP_NAMESPACE).trim();
                String xmlGroupNamespace = member.getChildText(NAMESPACE, GROUP_NAMESPACE).trim();
                addGroupNameToGroup(groupInfo.getNamespaceCode(), groupInfo.getGroupName(), xmlGroupNamespace, xmlGroupName);
            } else {
                LOG.error("Unknown member element: " + elementName);
            }


        }

        return groupInfo;

    }

    private void addPrincipalToGroup(String groupNamespace, String groupName, String principalId) {
        String key = groupNamespace.trim() + ":" + groupName.trim();
        List<String> principalIds = memberPrincipalIds.get(key);
        if (principalIds == null) {
            principalIds = new ArrayList<String>();
        }
        principalIds.add(principalId);
        memberPrincipalIds.put(key, principalIds);
    }

    private void addGroupToGroup(String groupNamespace, String groupName, String groupId) {
        String key = groupNamespace.trim() + ":" + groupName.trim();
        List<String> groupIds = memberGroupIds.get(key);
        if (groupIds == null) {
            groupIds = new ArrayList<String>();
        }
        groupIds.add(groupId);
        memberGroupIds.put(key, groupIds);
    }

    private void addGroupNameToGroup(String groupNamespace, String groupName, String memberGroupNamespace, String memberGroupName) {
        String key = groupNamespace.trim() + ":" + groupName.trim();
        List<String> groupNames = memberGroupNames.get(key);
        if (groupNames == null) {
            groupNames = new ArrayList<String>();
        }
        groupNames.add(memberGroupNamespace.trim() + ":" + memberGroupName.trim());
        memberGroupNames.put(key, groupNames);
    }

}
