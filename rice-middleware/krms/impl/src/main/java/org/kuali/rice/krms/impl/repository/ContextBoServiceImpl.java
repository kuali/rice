/**
 * Copyright 2005-2018 The Kuali Foundation
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
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krms.api.repository.context.ContextDefinition;

import java.util.HashMap;
import java.util.Map;

import static org.kuali.rice.krms.impl.repository.BusinessObjectServiceMigrationUtils.deleteMatching;
import static org.kuali.rice.krms.impl.repository.BusinessObjectServiceMigrationUtils.findSingleMatching;

/**
 * This is the interface for accessing KRMS repository Context related
 * business objects. 
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ContextBoServiceImpl implements ContextBoService {

    private DataObjectService dataObjectService;

    /**
     * This method will create a {@link ContextDefinition} as described
     * by the parameter passed in.
     *
     * @see org.kuali.rice.krms.impl.repository.ContextBoService#createContext(org.kuali.rice.krms.api.repository.context.ContextDefinition)
     */
    @Override
    public ContextDefinition createContext(ContextDefinition context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }

        final String contextIdKey = context.getId();
        final ContextDefinition existing = getContextByContextId(contextIdKey);

        if (existing != null) {
            throw new IllegalStateException("the context to create already exists: " + context);
        }

        ContextBo bo = dataObjectService.save(ContextBo.from(context));
        return ContextBo.to(bo);
    }

    /**
     * This method updates an existing Context in the repository.
     */
    @Override
    public ContextDefinition updateContext(ContextDefinition context) {
        if (context == null){
            throw new IllegalArgumentException("context is null");
        }

        // must already exist to be able to update
        final String contextIdKey = context.getId();
        final ContextBo existing = dataObjectService.find(ContextBo.class, contextIdKey);

        if (existing == null) {
            throw new IllegalStateException("the context does not exist: " + context);
        }

        final ContextDefinition toUpdate;

        if (!existing.getId().equals(context.getId())){
            // if passed in id does not match existing id, correct it
            final ContextDefinition.Builder builder = ContextDefinition.Builder.create(context);
            builder.setId(existing.getId());
            toUpdate = builder.build();
        } else {
            toUpdate = context;
        }

        // copy all updateable fields to bo
        ContextBo boToUpdate = ContextBo.from(toUpdate);

        // delete any old, existing attributes
        Map<String,String> fields = new HashMap<String,String>(1);
        fields.put("context.id", toUpdate.getId());
        deleteMatching(dataObjectService, ContextAttributeBo.class, fields);

        // update the rule and create new attributes
        final ContextBo updatedData = dataObjectService.save(boToUpdate);

        return ContextBo.to(updatedData);
    }

    @Override
    public ContextDefinition getContextByContextId(String contextId) {
        if (StringUtils.isBlank(contextId)){
            return null;
        }
        ContextBo bo = dataObjectService.find(ContextBo.class, contextId);
        return ContextBo.to(bo);
    }

    @Override
    public ContextDefinition getContextByNameAndNamespace( String name, String namespace ){
        if (StringUtils.isBlank(name)){
            throw new IllegalArgumentException("name is null or blank");
        }
        if (StringUtils.isBlank(namespace)){
            throw new IllegalArgumentException("namespace is null or blank");
        }

        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("namespace", namespace);
        ContextBo bo = findSingleMatching(dataObjectService, ContextBo.class, map);

        return ContextBo.to(bo);
    }

    /**
     * Sets the dataObjectService attribute value.
     *
     * @param dataObjectService The dataObjectService to set.
     */
    public void setDataObjectService(final DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
