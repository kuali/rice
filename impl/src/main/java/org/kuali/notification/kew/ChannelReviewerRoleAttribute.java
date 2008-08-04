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
package org.kuali.notification.kew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;

import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.GenericRoleAttribute;
import edu.iu.uis.eden.routetemplate.QualifiedRoleName;
import edu.iu.uis.eden.routetemplate.Role;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.workgroup.GroupNameId;

/**
 * KEW RoleAttribute implementation that is responsible for encapsulating a list
 * of users and groups which are reviewers for a Notification Channel.
 * This implementation relies on the default XML form implemented by GenericRoleAttribute
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ChannelReviewerRoleAttribute extends GenericRoleAttribute {
    private static final Logger LOG = Logger.getLogger(ChannelReviewerRoleAttribute.class);
    private static final List<Role> SUPPORTED_ROLES;
    
    static {
        Role reviewerRole = new Role(ChannelReviewerRoleAttribute.class, "reviewers", "Reviewers");
        List<Role> tmp = new ArrayList<Role>(1);
        tmp.add(reviewerRole);
        SUPPORTED_ROLES = Collections.unmodifiableList(tmp);
    }

    /**
     * Constructs a ChannelReviewerRoleAttribute.java.
     */
    public ChannelReviewerRoleAttribute() {
        super("channelReviewers");
        LOG.info("CHANNEL REVIEWER ROLE ATTRIBUTE CONSTRUCTOR");
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.GenericRoleAttribute#isMatch(edu.iu.uis.eden.routeheader.DocumentContent, java.util.List)
     */
    @Override
    public boolean isMatch(DocumentContent docContent, List<RuleExtension> ruleExtensions) {
        LOG.info("CHANNEL REVIEWER ROLE ATTRIBUTE IS MATCH");
        return super.isMatch(docContent, ruleExtensions);
    }

    /**
     * @see edu.iu.uis.eden.routetemplate.GenericWorkflowAttribute#getProperties()
     */
    @Override
    public Map<String, String> getProperties() {
        LOG.info("CHANNEL REVIEWER ROLE ATTRIBUTE GETPROPERTIES");
        // intentionally unimplemented...not intending on using this attribute client-side
        return null;
    }

    /**
     * @see edu.iu.uis.eden.plugin.attributes.RoleAttribute#getRoleNames()
     */
    public List<Role> getRoleNames() {
        LOG.info("CHANNEL REVIEWER ROLE ATTRIBUTE CALLED ROLENAMES");
        return SUPPORTED_ROLES;
    }
    
    /**
     * @see edu.iu.uis.eden.routetemplate.GenericRoleAttribute#getQualifiedRoleNames(java.lang.String, edu.iu.uis.eden.routeheader.DocumentContent)
     */
    @Override
    public List<String> getQualifiedRoleNames(String roleName, DocumentContent documentContent) throws EdenUserNotFoundException {
        List<String> qrn = new ArrayList<String>(1);
        qrn.add(roleName);
        return qrn;
    }

    /**
     * This looks at the reviewers list passed through from KEN and then resolves the individuals that need to actually approve 
     * the message.
     * @see edu.iu.uis.eden.routetemplate.GenericRoleAttribute#resolveRecipients(edu.iu.uis.eden.engine.RouteContext, edu.iu.uis.eden.routetemplate.QualifiedRoleName)
     */
    @Override
    protected List<Id> resolveRecipients(RouteContext routeContext, QualifiedRoleName qualifiedRoleName) throws EdenUserNotFoundException {
        LOG.info("CHANNEL REVIEWER ROLE ATTRIBUTE CALLED");
        List<Id> ids = new ArrayList<Id>();

        LOG.info("DOC CONTENT:" + routeContext.getDocumentContent().getDocContent());
        LOG.info("ATTR CONTENT:" + routeContext.getDocumentContent().getAttributeContent());
        DocumentContent dc = routeContext.getDocumentContent();
        List<Map<String, String>> attrs;
        try {
            attrs = content.parseContent(dc.getAttributeContent());
        } catch (XPathExpressionException xpee) {
            throw new WorkflowRuntimeException("Error parsing ChannelReviewer role attribute content", xpee);
        }
        
        if (attrs.size() > 0) {
            Map<String, String> values = attrs.get(0);
            if (values != null) {
                // iterate through all "fields" and accumulate a list of users and groups
                for (Map.Entry<String, String> entry: values.entrySet()) {
                    String name = entry.getKey();
                    String value = entry.getValue();
                    LOG.info("Entry: " + name + "=" + value);
                    Id id;
                    if (name.startsWith("user")) {
                        LOG.info("Adding user: " + value);
                        id = new AuthenticationUserId(value);
                        ids.add(id);
                    } else if (name.startsWith("group")) {
                        LOG.info("Adding group: " + value);
                        id = new GroupNameId(value);
                        ids.add(id);
                    } else {
                        LOG.error("Invalid attribute value: " + name + "=" + value);
                    }
                }
            }
        } else {
            LOG.debug("No attribute content found for ChannelReviewerRoleAttribute");
        }
        
        LOG.info("Returning ids: " + ids.size());
        return ids;
    }
}