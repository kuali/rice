/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kim.api.common.template;

import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

public interface TemplateContract extends Versioned, GloballyUnique, Inactivatable, Identifiable {
    /**
     * The namespace code that this KIM Permission Template belongs too.
     *
     * @return namespaceCode
     */
    String getNamespaceCode();

    /**
     * The name of the KIM Permission Template.
     *
     * @return name
     */
    String getName();

    /**
     * The description of the KIM Permission Template.
     *
     * @return description
     */
	String getDescription();

    /**
     * The KIM Type ID referenced by the KIM Permission Template.
     *
     * @return typeId
     */
	String getKimTypeId();
}
