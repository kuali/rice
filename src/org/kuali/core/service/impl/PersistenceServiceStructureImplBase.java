/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.core.exceptions.ClassNotPersistableException;

public class PersistenceServiceStructureImplBase {

    private DescriptorRepository descriptorRepository;

    /**
     * Constructs a PersistenceServiceImpl instance.
     */
    public PersistenceServiceStructureImplBase() {
        MetadataManager metadataManager = MetadataManager.getInstance();
        descriptorRepository = metadataManager.getGlobalRepository();
    }

    /**
     * @return DescriptorRepository containing everything OJB knows about persistable classes
     */
    protected DescriptorRepository getDescriptorRepository() {
        return descriptorRepository;
    }


    /**
     * @see org.kuali.core.service.PersistenceMetadataExplorerService#listPrimaryKeyFieldNames(java.lang.Class)
     */
    public List listPrimaryKeyFieldNames(Class clazz) {
        ClassDescriptor classDescriptor = getClassDescriptor(clazz);

        List fieldNames = new ArrayList();

        FieldDescriptor keyDescriptors[] = classDescriptor.getPkFields();

        for (int i = 0; i < keyDescriptors.length; ++i) {
            FieldDescriptor keyDescriptor = keyDescriptors[i];
            fieldNames.add(keyDescriptor.getAttributeName());
        }

        return fieldNames;
    }


    /**
     * @param classDescriptor
     * @return name of the database table associated with given classDescriptor, stripped of its leading schemaName
     */
    protected String getTableName(ClassDescriptor classDescriptor) {
        String schemaName = classDescriptor.getSchema();
        String fullTableName = classDescriptor.getFullTableName();

        String tableName = null;
        if (StringUtils.isNotBlank(schemaName)) {
            tableName = StringUtils.substringAfter(fullTableName, schemaName + ".");
        }
        if (StringUtils.isBlank(tableName)) {
            tableName = fullTableName;
        }

        return tableName;
    }

    /**
     * @param persistableClass
     * @return ClassDescriptor for the given Class
     * @throws IllegalArgumentException if the given Class is null
     * @throws ClassNotPersistableException if the given Class is unknown to OJB
     */
    protected ClassDescriptor getClassDescriptor(Class persistableClass) {
        if (persistableClass == null) {
            throw new IllegalArgumentException("invalid (null) object");
        }

        ClassDescriptor classDescriptor = null;
        DescriptorRepository globalRepository = getDescriptorRepository();
        try {
            classDescriptor = globalRepository.getDescriptorFor(persistableClass);
        }
        catch (ClassNotPersistenceCapableException e) {
            throw new ClassNotPersistableException("class '" + persistableClass.getName() + "' is not persistable", e);
        }

        return classDescriptor;
    }


}
