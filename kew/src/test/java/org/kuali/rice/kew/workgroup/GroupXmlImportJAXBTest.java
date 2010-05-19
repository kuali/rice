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
package org.kuali.rice.kew.workgroup;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.core.config.ConfigLogger;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.config.JAXBConfigImpl;
import org.kuali.rice.core.config.JAXBConfigImpl.ConfigNamespaceURIFilter;
import org.kuali.rice.core.config.xsd.Config;
import org.kuali.rice.core.config.xsd.Param;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.xml.GroupXmlJAXBParser;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupXmlImportJAXBTest extends KEWTestCase {
    private static final Logger LOG = Logger.getLogger(GroupXmlImportJAXBTest.class);
    private static final String INDENT = "  ";

   /**
     *
     * Verify that a workgroup with a bad user in the xml is not going to be put in the db.
     *
     * @throws Exception
     */

    @Test public void testGroupImportXml() throws Exception {
//    	loadXmlFile("GroupXmlImportTest.xml");
    	InputStream xmlFile = getClass().getResourceAsStream("GroupXmlImportJAXBTest.xml");
    	
    	GroupXmlJAXBParser parser = new GroupXmlJAXBParser();
    	GroupInfo groupInfo = parser.parse(xmlFile);

    	assertNotNull(groupInfo);
    	assertTrue(groupInfo.getGroupName().equals("TestUserGroup"));
    	assertTrue(groupInfo.getGroupDescription().equals("Group for test user"));
    	
//        IdentityManagementService identityManagementService = KIMServiceLocator.getIdentityManagementService();
//        //verify that the group was ingested
//        Group group = identityManagementService.getGroupByName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "TestUserGroup");
//
//        assertNotNull(group);
//        List<String> members = identityManagementService.getGroupMemberPrincipalIds(group.getGroupId());
//        List<String> groups = identityManagementService.getMemberGroupIds(group.getGroupId());
//        assertTrue(identityManagementService.isMemberOfGroup(identityManagementService.getPrincipalByPrincipalName("ewestfal").getPrincipalId(), group.getGroupId()));
//        assertTrue(identityManagementService.isMemberOfGroup(identityManagementService.getPrincipalByPrincipalName("rkirkend").getPrincipalId(), group.getGroupId()));
//        assertTrue(identityManagementService.isMemberOfGroup("2015", group.getGroupId()));
//        assertTrue(KIMServiceLocator.getGroupService().isGroupMemberOfGroup(identityManagementService.getGroupByName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "TestWorkgroup").getGroupId(), group.getGroupId()));
    }
    
 
}
