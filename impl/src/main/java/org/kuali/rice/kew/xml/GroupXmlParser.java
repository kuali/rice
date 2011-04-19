/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.xml;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.kuali.rice.core.util.XmlHelper;
import org.kuali.rice.core.xml.XmlException;
import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.kuali.rice.core.api.impex.xml.XmlConstants.*;


/**
 * Parses groups from XML.
 *
 * @see KimGroups
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupXmlParser {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GroupXmlParser.class);
    private static final boolean DEFAULT_ACTIVE_VALUE = true;
    private static final String DEFAULT_GROUP_DESCRIPTION = "";
    private HashMap<String, List<String>> memberGroupIds = new HashMap<String, List<String>>();
    private HashMap<String, List<String>> memberGroupNames = new HashMap<String, List<String>>();
    private HashMap<String, List<String>> memberPrincipalIds = new HashMap<String, List<String>>();
    private AttributeSet groupAttributes = new AttributeSet();

    public List<GroupInfo> parseGroups(InputStream input) throws IOException, XmlException {
        try {
            Document doc = XmlHelper.trimSAXXml(input);
            Element root = doc.getRootElement();
            return parseGroups(root);
        } catch (JDOMException e) {
            throw new XmlException("Parse error.", e);
        } catch (SAXException e){
            throw new XmlException("Parse error.",e);
        } catch(ParserConfigurationException e){
            throw new XmlException("Parse error.",e);
        }
    }


    /**
     * Parses and saves groups
     * @param element top-level 'data' element which should contain a <groups> child element
     * @return a list of parsed and saved, current, groups;
     * @throws XmlException
     */
    @SuppressWarnings("unchecked")
	public List<GroupInfo> parseGroups(Element element) throws XmlException {
        List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();
        for (Element groupsElement: (List<Element>) element.getChildren(GROUPS, GROUP_NAMESPACE)) {

            for (Element groupElement: (List<Element>) groupsElement.getChildren(GROUP, GROUP_NAMESPACE)) {
                groupInfos.add(parseGroup(groupElement));
            }
        }
        for (GroupInfo groupInfo : groupInfos) {
            IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();

            // check if group already exists
            GroupInfo foundGroup = identityManagementService.getGroupByName(groupInfo.getNamespaceCode(), groupInfo.getGroupName());

            if (foundGroup == null) {
                if ( LOG.isInfoEnabled() ) {
                	LOG.info("Group named '" + groupInfo.getGroupName() + "' not found, creating new group named '" + groupInfo.getGroupName() + "'");
                }
                try {
                    GroupInfo newGroupInfo =  identityManagementService.createGroup(groupInfo);

                    String key = newGroupInfo.getNamespaceCode().trim() + KEWConstants.KIM_GROUP_NAMESPACE_NAME_DELIMITER_CHARACTER + newGroupInfo.getGroupName().trim();
                    addGroupMembers(newGroupInfo, key);
                } catch (Exception e) {
                    throw new RuntimeException("Error creating group with name '" + groupInfo.getGroupName() + "'", e);
                }
            } else {
            	if ( LOG.isInfoEnabled() ) {
            		LOG.info("Group named '" + groupInfo.getGroupName() + "' found, creating a new version");
            	}
                try {
                    groupInfo.setGroupId(foundGroup.getGroupId());
                    identityManagementService.updateGroup(foundGroup.getGroupId(), groupInfo);

                    //delete existing group members and replace with new
                    identityManagementService.removeAllGroupMembers(foundGroup.getGroupId());

                    String key = groupInfo.getNamespaceCode().trim() + KEWConstants.KIM_GROUP_NAMESPACE_NAME_DELIMITER_CHARACTER + groupInfo.getGroupName().trim();
                    addGroupMembers(groupInfo, key);

                } catch (Exception e) {
                    throw new RuntimeException("Error updating group.", e);
                }
            }
        }
        return groupInfos;
    }

    @SuppressWarnings("unchecked")
	private GroupInfo parseGroup(Element element) throws XmlException {
        GroupInfo groupInfo = new GroupInfo();
        IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();
        groupInfo.setGroupName(element.getChildText(NAME, GROUP_NAMESPACE));

        if (groupInfo.getGroupName() == null) {
            throw new XmlException("Group must have a name.");
        }

        String groupNamespace = element.getChildText(NAMESPACE, GROUP_NAMESPACE);
        if (groupNamespace != null) {
            groupInfo.setNamespaceCode(groupNamespace.trim());
        } else {
            throw new XmlException("Namespace must have a value.");
        }

        String id = element.getChildText(ID, GROUP_NAMESPACE);
        if (id != null) {
            groupInfo.setGroupId(id.trim());
        } else {
        	
        }

        String description = element.getChildText(DESCRIPTION, GROUP_NAMESPACE);
        if (description != null && !description.trim().equals("")) {
            groupInfo.setGroupDescription(description);
        }

        // Type element and children (namespace and name)
        String typeId = null;
        List<KimTypeAttribute> kimTypeAttributes = new ArrayList<KimTypeAttribute>();
        if (element.getChild(TYPE, GROUP_NAMESPACE) != null) {
            Element typeElement = element.getChild(TYPE, GROUP_NAMESPACE);
            String typeNamespace = typeElement.getChildText(NAMESPACE, GROUP_NAMESPACE);
            String typeName = typeElement.getChildText(NAME, GROUP_NAMESPACE);
            KimType kimTypeInfo = KimApiServiceLocator.getKimTypeInfoService().findKimTypeByNameAndNamespace(typeNamespace, typeName);
            if (kimTypeInfo != null) {
            	typeId = kimTypeInfo.getId();
                kimTypeAttributes = kimTypeInfo.getAttributeDefinitions();
            } else  {
                throw new XmlException("Invalid type name and namespace specified.");
            }
        } else { //set to default type
            KimType kimTypeDefault = KimApiServiceLocator.getKimTypeInfoService().findKimTypeByNameAndNamespace(KimConstants.KIM_TYPE_DEFAULT_NAMESPACE, KimConstants.KIM_TYPE_DEFAULT_NAME);
            if (kimTypeDefault != null) {
            	typeId = kimTypeDefault.getId();
                kimTypeAttributes = kimTypeDefault.getAttributeDefinitions();
            } else {
            	throw new RuntimeException("Failed to locate the 'Default' group type!  Please ensure that it's in your database.");
            }
        }
        groupInfo.setKimTypeId(typeId);

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
        for (KimTypeAttribute attribute : kimTypeAttributes) {
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
                    throw new XmlException("Invalid attribute specified.");
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
                    throw new XmlException("Principal Name "+principalName+" cannot be found.");
                }
            } else if (elementName.equals(PRINCIPAL_ID)) {
                String xmlPrincipalId = member.getText().trim();
                KimPrincipal principal = identityManagementService.getPrincipal(xmlPrincipalId);
                if (principal != null) {
                    addPrincipalToGroup(groupInfo.getNamespaceCode(), groupInfo.getGroupName(), principal.getPrincipalId());
                } else {
                    throw new XmlException("Principal Id "+xmlPrincipalId+" cannot be found.");
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
        String key = groupNamespace.trim() + KEWConstants.KIM_GROUP_NAMESPACE_NAME_DELIMITER_CHARACTER + groupName.trim();
        List<String> principalIds = memberPrincipalIds.get(key);
        if (principalIds == null) {
            principalIds = new ArrayList<String>();
        }
        principalIds.add(principalId);
        memberPrincipalIds.put(key, principalIds);
    }

    private void addGroupToGroup(String groupNamespace, String groupName, String groupId) {
        String key = groupNamespace.trim() + KEWConstants.KIM_GROUP_NAMESPACE_NAME_DELIMITER_CHARACTER + groupName.trim();
        List<String> groupIds = memberGroupIds.get(key);
        if (groupIds == null) {
            groupIds = new ArrayList<String>();
        }
        groupIds.add(groupId);
        memberGroupIds.put(key, groupIds);
    }

    private void addGroupNameToGroup(String groupNamespace, String groupName, String memberGroupNamespace, String memberGroupName) {
        String key = groupNamespace.trim() + KEWConstants.KIM_GROUP_NAMESPACE_NAME_DELIMITER_CHARACTER + groupName.trim();
        List<String> groupNames = memberGroupNames.get(key);
        if (groupNames == null) {
            groupNames = new ArrayList<String>();
        }
        groupNames.add(memberGroupNamespace.trim() + KEWConstants.KIM_GROUP_NAMESPACE_NAME_DELIMITER_CHARACTER + memberGroupName.trim());
        memberGroupNames.put(key, groupNames);
    }

    private void addGroupMembers(GroupInfo groupInfo, String key) throws XmlException {
        IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();
        List<String> groupIds = memberGroupIds.get(key);
        if (groupIds != null) {
            for (String groupId : groupIds) {
                Group group = identityManagementService.getGroup(groupId);
                if (group != null) {
                    identityManagementService.addGroupToGroup(group.getGroupId(), groupInfo.getGroupId());
                } else {
                    throw new XmlException("Group Id "+groupId+" cannot be found.");
                }
            }
        }
        List<String> groupNames = memberGroupNames.get(key);
        if (groupNames != null) {
            for (String groupName : groupNames) {
                Group group = identityManagementService.getGroupByName(Utilities.parseGroupNamespaceCode(groupName), Utilities.parseGroupName(groupName));
                if (group != null) {
                	identityManagementService.addGroupToGroup(group.getGroupId(), groupInfo.getGroupId());
                } else {
                    throw new XmlException("Group "+groupName+" cannot be found.");
                }
            }
        }
        List<String> principalIds = memberPrincipalIds.get(key);
        if (principalIds != null) {
            for (String principalId : principalIds) {
            	
            	identityManagementService.addPrincipalToGroup(principalId, groupInfo.getGroupId());
            }
        }

    }
}
