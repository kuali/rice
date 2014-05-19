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
package org.kuali.rice.krad.datadictionary;

import org.kuali.rice.krad.uif.util.CopyUtils;

/**
 * Marks KRAD framework objects that support deep copying.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Copyable extends Cloneable {

    /**
     * Override {@link Object#clone()} to assign the public modifier.
     * @return {@link Object#clone()}
     * @throws CloneNotSupportedException If {@link Cloneable} is not implemented. This should not
     *         be possible when using this interface.
     * 
     * @see CopyUtils#isUseClone() When cloning is enabled, {@link #clone} is used for shallow copy
     *      operations.
     * @see Cloneable
     * @see Object#clone()
     */
    Object clone() throws CloneNotSupportedException;

}
