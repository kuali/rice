/*
 * Copyright 2011 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kuali.rice.krad.uif.authorization;

import java.util.Set;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.krad.web.spring.form.UifFormBase;

/**
 * Configured for a <code>View</code> instance to provide conditional logic
 * based on the current user's authorization
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Authorizer {

    public Set<String> getActionFlags(UifFormBase model, Person user, Set<String> actions);

    public Set<String> getEditModes(UifFormBase model, Person user, Set<String> editModes);

    public Set<String> getSecurePotentiallyHiddenGroupIds();

    public Set<String> getSecurePotentiallyReadOnlyGroupIds();

}
