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

package org.kuali.rice.core.api.component;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.ImmutableInactivatable;

/**
 * This is the contract for a Component.  This represents functional/logical piece
 * within a rice application or rice ecosystem.
 */
public interface ComponentContract extends Versioned, GloballyUnique, ImmutableInactivatable, Coded {

    /**
     * This is the name value for the component.  It cannot be null or a blank string.
     * @return name
     */
    String getName();

    /**
     * This is the namespace for the component.  It cannot be null or a blank string.
     * <p>
     * It is a way of assigning the component to a logical grouping within a rice application or rice ecosystem.
     * </p>
     *
     * @return namespace code
     */
    String getNamespaceCode();

    /**
     * Whether this is a virtual or derived component.
     * @return virtual
     */
    boolean isVirtual();

    /**
     * This the active flag for the Component.
     *
     * @return active
     */
    boolean isActive();


}
