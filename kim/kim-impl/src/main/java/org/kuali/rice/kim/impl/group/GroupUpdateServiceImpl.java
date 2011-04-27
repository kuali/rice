/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kim.impl.group;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupUpdateService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;

import javax.jws.WebParam;
import javax.xml.namespace.QName;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jjhanso
 * Date: 4/18/11
 * Time: 2:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroupUpdateServiceImpl extends GroupServiceBase implements GroupUpdateService {
    private static final Logger LOG = Logger.getLogger(GroupUpdateServiceImpl.class);

	private SequenceAccessorService sequenceAccessorService;

    @Override
    public Group createGroup(@WebParam(name = "group") final Group group) throws UnsupportedOperationException {

        saveGroup(group);

        Group newGroup = getGroupByName(group.getNamespaceCode(), group.getName());

        return newGroup;
    }

    @Override
    public Group updateGroup(@WebParam(name = "groupId") final String groupId, @WebParam(name = "group") final Group group) throws UnsupportedOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean addGroupToGroup(@WebParam(name = "childId") final String childId, @WebParam(name = "parentId") final String parentId) throws UnsupportedOperationException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean removeGroupFromGroup(@WebParam(name = "childId") final String childId, @WebParam(name = "parentId") final String parentId) throws UnsupportedOperationException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean addPrincipalToGroup(@WebParam(name = "principalId") final String principalId, @WebParam(name = "groupId") final String groupId) throws UnsupportedOperationException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean removePrincipalFromGroup(@WebParam(name = "principalId") final String principalId, @WebParam(name = "groupId") final String groupId) throws UnsupportedOperationException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeAllMembers(@WebParam(name = "groupId") final String groupId) throws UnsupportedOperationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void saveGroup(Group group) {
		/*if ( group == null ) {
			return null;
		} else if (group.getId() != null) {
			// Get the version of the group that is in the DB
			Group oldGroup = getGroup(group.getId());


		// GroupInternalService handles KEW update duties
		SequenceAccessorService sas = getSequenceAccessorService();
    	if (group.getId() == null) {
    		group.setId(sas.getNextAvailableSequenceNumber(
                    "KRIM_GRP_ID_S", GroupBo.class).toString());
    	}
		KIMServiceLocatorInternal.getGroupInternalService().saveWorkgroup(group);
		getIdentityManagementNotificationService().groupUpdated();*/
	}

     protected SequenceAccessorService getSequenceAccessorService() {
		if ( sequenceAccessorService == null ) {
			sequenceAccessorService = KNSServiceLocator.getSequenceAccessorService();
		}
		return sequenceAccessorService;
	}

    /*protected IdentityManagementNotificationService getIdentityManagementNotificationService() {

        (MessageHelper)GlobalResourceLoader.getService("enMessageHelper").
        return (IdentityManagementNotificationService)KSBServiceLocator.getMessageHelper().getServiceAsynchronously(new QName("KIM", "kimIdentityManagementNotificationService"));
    }*/
}
