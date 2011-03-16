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

package org.kuali.rice.krms.impl.repository;


import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.krms.api.repository.Type;
import org.kuali.rice.krms.api.repository.TypeService;

import java.util.*;

public final class TypeServiceImpl implements TypeService {

    private BusinessObjectService businessObjectService;

    @Override
    public Type getTypeById(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("id is blank");
        }

        TypeBo typeBo = businessObjectService.findByPrimaryKey(TypeBo.class, Collections.singletonMap("id", id));

        return TypeBo.to(typeBo);
    }

    @Override
    public Type getTypeByNameAndNamespace(final String name, final String namespace) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name is blank");
        }
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("namespace", namespace);

        Collection<TypeBo> typeList = businessObjectService.findMatching(TypeBo.class, Collections.unmodifiableMap(map));
        if (typeList == null || typeList.isEmpty()) {
            return null;
        } else if (typeList.size() == 1) {
            return TypeBo.to(typeList.iterator().next());
        } else throw new IllegalStateException("Multiple KRMS types found with same name and namespace");
    }

    @Override
    public List<Type> findAllTypesByNamespace(final String namespace) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("namespace", namespace);
        map.put("active", Boolean.TRUE);

        Collection<TypeBo> typeBos = businessObjectService.findMatching(TypeBo.class, Collections.unmodifiableMap(map));

        return convertListOfBosToImmutables(typeBos);
    }

    @Override
    public List<Type> findAllTypes() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("active", Boolean.TRUE);

        Collection<TypeBo> typeBos = businessObjectService.findMatching(TypeBo.class, Collections.unmodifiableMap(map));
        return convertListOfBosToImmutables(typeBos);
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(final BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Converts a List<CountryBo> to an Unmodifiable List<Country>
     *
     * @param countryBos a mutable List<CountryBo> to made completely immutable.
     * @return An unmodifiable List<Country>
     */
    List<Type> convertListOfBosToImmutables(final Collection<TypeBo> typeBos) {
        ArrayList<Type> types = new ArrayList<Type>();
        for (TypeBo bo : typeBos) {
            Type type = TypeBo.to(bo);
            types.add(type);
        }
        return Collections.unmodifiableList(types);
    }
}
