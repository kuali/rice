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

package org.kuali.rice.kim.api.type;

import java.util.Collection;

public interface KimTypeInfoService {

    /**
     * Gets a {@link KimType} from a kim type id.
     * <p/>
     * <p>
     * This method will return null if the kim type does not exist.
     * </p>
     *
     * @param id the id to retrieve the kim type by. cannot be null.
     * @return a {@link KimType} or null
     * @throws IllegalArgumentException if the id is null
     */
    KimType getKimType(String id);

    /**
     * Gets a {@link KimType} from a kim type name and namespace code.
     * <p/>
     * <p>
     * This method will return null if the kim type does not exist.
     * </p>
     * <p/>
     * <p>
     * This method will only return active kim types.
     * </p>
     *
     * @param namespaceCode the namespaceCode to retrieve the kim type by. cannot be null.
     * @param name          the name to retrieve the kim type by. cannot be null.
     * @return a {@link KimType} or null
     * @throws IllegalArgumentException if the namespaceCode or name is null
     * @throws IllegalStateException    if multiple active results are found for a namespaceCode and name
     */
    KimType findKimTypeByNameAndNamespace(String namespaceCode, String name);

    /**
     * Gets all the {@link KimType KimTypes}.
     * <p/>
     * <p>
     * This method will always return an <b>immutable</b> Collection
     * even when no values exist.
     * </p>
     * <p/>
     * <p>
     * This method will only return active kim types.
     * </p>
     *
     * @return an immutable collection of kim types
     */
    Collection<KimType> findAllKimTypes();
}
