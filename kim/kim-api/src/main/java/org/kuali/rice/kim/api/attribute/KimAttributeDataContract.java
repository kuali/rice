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

package org.kuali.rice.kim.api.attribute;

import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.kim.api.type.KimTypeContract;

/**
 * This is the contract for a KimDataAttribute.  A KimDataAttribute
 * associates a value with a kim attribute.
 */
public interface KimAttributeDataContract extends Identifiable {

    /**
     * The attribute value.  This can be null or blank.
     *
     * @return the attribute value.
     */
	String getAttributeValue();

    /**
     * The kim attribute associated with the kim attribute data.  This can be null if no KimAttributes are associated.
     *
     * @return the kim attribute
     */
	KimAttributeContract getKimAttribute();

    /**
     * The kim type associated with the kim attribute data.  This can be null if no kim types are associated.
     *
     * @return the kim type
     */
    KimTypeContract getKimType();
}
