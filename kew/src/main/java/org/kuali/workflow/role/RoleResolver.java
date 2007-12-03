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
package org.kuali.workflow.role;

import java.util.List;

import edu.iu.uis.eden.engine.RouteContext;

/**
 * A component which resolves a List of QualifiedRoles into a resolved List for which Action Requests should get generated.
 *
 * The resolve(...) method on this component takes the List of all QualifiedRoles for a given Role and should
 * return a List of only those QualifiedRoles that match the data on the document in the RouteContext.  How the decision
 * as to whether or not a particular QualifiedRole matches is up to the implementor of resolve method.
 *
 * If null, or an empty List is returned, this indicates that no QualifiedRoles matched the document.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RoleResolver {

	public List<QualifiedRole> resolve(RouteContext routeContext, Role role, List<QualifiedRole> allQualifiedRoles);

}
