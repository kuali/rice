/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.dto.EmplIdDTO;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.UuIdDTO;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.bo.user.UserId;
import org.kuali.rice.kns.exception.UserNotFoundException;


/**
 * Used to eliminate kauli user table and delegate user fetches to workflow User service. Wired in the CoreServiceOverride.xml file.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RiceKNSDefaultUserDAOImpl implements UniversalUserDao {

    private static final Logger LOG = Logger.getLogger(RiceKNSDefaultUserDAOImpl.class);

    public UniversalUser getUser(UserId userId) throws UserNotFoundException {

        try {
            if (userId instanceof org.kuali.rice.kns.bo.user.AuthenticationUserId) {
                return convertWorkflowUser(KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdDTO(userId.toString())));
            }
            if (userId instanceof org.kuali.rice.kns.bo.user.PersonPayrollId) {
                return convertWorkflowUser(KEWServiceLocator.getUserService().getWorkflowUser(new EmplIdDTO(userId.toString())));
            }
            if (userId instanceof org.kuali.rice.kns.bo.user.UuId) {
                return convertWorkflowUser(KEWServiceLocator.getUserService().getWorkflowUser(new UuIdDTO(userId.toString())));
            }
        } catch (KEWUserNotFoundException eunfe) {
        	return null;
        }
        catch (Exception e) {
            LOG.error("Exception caught fetching user from workflow.", e);
            throw new UserNotFoundException(e.getMessage());
        }
        throw new UnsupportedOperationException("Id type given to dao that is not supported. " + userId);
    }

    public WorkflowUser getWorkflowUser(org.kuali.rice.kew.user.UserId userId) throws KEWUserNotFoundException {
        return KEWServiceLocator.getUserService().getWorkflowUser(userId);
    }

    public void save(WorkflowUser workflowUser) {
        throw new UnsupportedOperationException("RiceKNSDefaultUserDAOImpl doesn't support saving users");
    }

    public List search(WorkflowUser user, boolean useWildCards) {
        throw new UnsupportedOperationException("RiceKNSDefaultUserDAOImpl doesn't support searching for users");
    }

    private UniversalUser convertWorkflowUser(WorkflowUser user) {
        UniversalUser kUser = new UniversalUser();
        kUser.setPersonPayrollIdentifier(user.getEmplId().getEmplId());
        kUser.setPersonEmailAddress(user.getEmailAddress());
        kUser.setPersonName(user.getGivenName());
        kUser.setPersonUserIdentifier(user.getAuthenticationUserId().getAuthenticationId());
        kUser.setPersonUniversalIdentifier(user.getWorkflowId());
        return kUser;
    }

}
