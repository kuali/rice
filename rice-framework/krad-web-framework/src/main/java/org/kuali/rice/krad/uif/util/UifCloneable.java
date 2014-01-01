/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.uif.util;

/**
 * Extends the {@link Cloneable} interface to mark UIF objects that can be cloned, rather than
 * constructed, for faster copying.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @deprecated The {@link #clone} method has been added to {@link Copyable}. This interface is now
 *             redundant, but is still used on a small scale for special handling in
 *             {@link CloneUtils}.
 */
public interface UifCloneable extends Cloneable {

    /**
     * Override {@link Object#clone()} is overriden to assign the public modifier.
     * 
     * {@see Object#clone()}
     */
    Object clone() throws CloneNotSupportedException;
    
}
