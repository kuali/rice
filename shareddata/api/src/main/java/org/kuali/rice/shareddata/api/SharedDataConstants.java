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

package org.kuali.rice.shareddata.api;

import org.kuali.rice.core.api.CoreConstants;

/**
 * <p>SharedDataConstants class.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class SharedDataConstants {
    public static final class Namespaces {
    	public static final String MODULE_NAME = "shareddata";
        public static final String SHAREDDATA_NAMESPACE_PREFIX = CoreConstants.Namespaces.ROOT_NAMESPACE_PREFIX + "/" + MODULE_NAME;
    	/**
    	 * Namespace for the core module which is compatible with Kuali Rice 2.0.x.
    	 */
    	public static final String SHAREDDATA_NAMESPACE_2_0 = SHAREDDATA_NAMESPACE_PREFIX + "/" + CoreConstants.Versions.VERSION_2_0;

        private Namespaces() {
		    throw new UnsupportedOperationException("do not call");
	    }
    }
}
