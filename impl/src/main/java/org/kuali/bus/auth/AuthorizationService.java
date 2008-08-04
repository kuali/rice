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
package org.kuali.bus.auth;

import javax.servlet.http.HttpServletRequest;

/**
 * A simple authorization mechinism which can be used to determine whether or not the authenticated user
 * is an administrator and therefore has the permissions to view the KSB.  The KSB itself has no
 * concept of identity available to it, so the application which is using the bus needs to have some
 * mechanism by which to determine identity at the time that the {@link #isAdministrator(HttpServletRequest)}
 * method is invoked (i.e. via a statically available ThreadLocal or from the HttpServletRequest).
 *
 * <p>This interface will most likely be deprecated in a future version of Rice and replaced with a more
 * sophisticated authorization implementation.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface AuthorizationService {

    public boolean isAdministrator(HttpServletRequest request);

}
