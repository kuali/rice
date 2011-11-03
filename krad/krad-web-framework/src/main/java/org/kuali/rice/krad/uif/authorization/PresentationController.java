/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.authorization;

import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.Set;

/**
 * Configured for a <code>View</code> instance to provide conditional logic
 * based on any variable (view configuration, system parameters, ...) that does
 * not depend on the current user
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface PresentationController {

    public Set<String> getActionFlags(UifFormBase model);

    public Set<String> getEditModes(UifFormBase model);

    public Set<String> getConditionallyHiddenPropertyNames(UifFormBase model);

    public Set<String> getConditionallyHiddenGroupIds(UifFormBase model);

    public Set<String> getConditionallyReadOnlyPropertyNames(UifFormBase model);

    public Set<String> getConditionallyReadOnlyGroupIds(UifFormBase model);

    public Set<String> getConditionallyRequiredPropertyNames(UifFormBase model);

}
