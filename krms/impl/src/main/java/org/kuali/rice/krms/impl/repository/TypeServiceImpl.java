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
import org.kuali.rice.krms.api.repository.KrmsType;
import org.kuali.rice.krms.api.repository.KrmsTypeService;

import java.util.*;

public final class TypeServiceImpl implements KrmsTypeService {

    private BusinessObjectService businessObjectService;

    @Override
    public KrmsType getTypeById(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("id is blank");
        }

        KrmsTypeBo krmsTypeBo = businessObjectService.findByPrimaryKey(KrmsTypeBo.class, Collections.singletonMap("id", id));

        return KrmsTypeBo.to(krmsTypeBo);
    }

    @Override
    public KrmsType getTypeByNameAndNamespace(final String name, final String namespace) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("name is blank");
        }
        if (StringUtils.isBlank(namespace)) {
            throw new IllegalArgumentException("namespace is blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("namespace", namespace);

        Collection<KrmsTypeBo> typeList = businessObjectService.findMatching(KrmsTypeBo.class, Collections.unmodifiableMap(map));
        if (typeList == null || typeList.isEmpty()) {
            return null;
        } else if (typeList.size() == 1) {
            return KrmsTypeBo.to(typeList.iterator().next());
        } else throw new IllegalStateException("Multiple KRMS types found with same name and namespace");
    }

    @Override
    public List<KrmsType> findAllTypesByNamespace(final String namespace) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("namespace", namespace);
        map.put("active", Boolean.TRUE);

        Collection<KrmsTypeBo> krmsTypeBos = businessObjectService.findMatching(KrmsTypeBo.class, Collections.unmodifiableMap(map));

        return convertListOfBosToImmutables(krmsTypeBos);
    }

    @Override
    public List<KrmsType> findAllTypes() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("active", Boolean.TRUE);

        Collection<KrmsTypeBo> krmsTypeBos = businessObjectService.findMatching(KrmsTypeBo.class, Collections.unmodifiableMap(map));
        return convertListOfBosToImmutables(krmsTypeBos);
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
    List<KrmsType> convertListOfBosToImmutables(final Collection<KrmsTypeBo> krmsTypeBos) {
        ArrayList<KrmsType> krmsTypes = new ArrayList<KrmsType>();
        for (KrmsTypeBo bo : krmsTypeBos) {
            KrmsType krmsType = KrmsTypeBo.to(bo);
            krmsTypes.add(krmsType);
        }
        return Collections.unmodifiableList(krmsTypes);
    }
}
