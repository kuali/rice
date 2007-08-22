/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.AbstractRoleAttribute;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.routetemplate.Role;
import edu.iu.uis.eden.user.AuthenticationUserId;

/**
 * Current state of affairs
 * 
 * jitrue -> primary recipient
 * natjohns -> delegate (type dictated by rule setup)
 * shenl -> delegate (type dictated by rule setup)
 *
 */
public class BANotificationRoleAttribute extends AbstractRoleAttribute {

    public List getRoleNames() {
        return Arrays.asList(new Role[] { new Role(getClass(), "Notify", "Notify"), new Role(getClass(), "Notify2", "Notify2"), new Role(getClass(), "NotifyDelegate", "NotifyDelegate") });
    }

    public List getQualifiedRoleNames(String roleName, DocumentContent documentContent) throws EdenUserNotFoundException {
        List qualifiedRoleNames = new ArrayList();
        if (roleName.equals("Notify") || roleName.equals("Notify2")) {
            qualifiedRoleNames.add("jitrue");    
        } else throw new RuntimeException("Bad Role " + roleName);        
        return qualifiedRoleNames;
    }

    public ResolvedQualifiedRole resolveQualifiedRole(RouteContext routeContext, String roleName, String qualifiedRole) throws EdenUserNotFoundException {
        if (roleName.equals("Notify") || roleName.equals("Notify2")) {
            return new ResolvedQualifiedRole(roleName, Arrays.asList(new Id[] { new AuthenticationUserId(qualifiedRole) }));
        } else if (roleName.equals("NotifyDelegate")) {
            List recipients = new ArrayList();
            recipients.add(new AuthenticationUserId("natjohns"));
            recipients.add(new AuthenticationUserId("shenl"));
            return new ResolvedQualifiedRole(roleName, recipients);
        }
        throw new RuntimeException("Bad Role " + roleName);
    }

}
