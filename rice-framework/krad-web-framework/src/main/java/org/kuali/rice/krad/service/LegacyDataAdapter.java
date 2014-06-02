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
package org.kuali.rice.krad.service;

import java.util.List;

import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.document.Document;

/**
 * Adapter that supports "legacy" KNS/KRAD persistence, metadata, and object utility frameworks via runtime
 * argument inspection
 *
 * @deprecated This class is deprecated by default, applications should *never*
 * use this adapter directly
 *
 * @author Kuali Rice Team (rice.collab@kuali.org).
 */
@Deprecated
public interface LegacyDataAdapter extends LegacyDataAppAdapter {

    RelationshipDefinition getDictionaryRelationship(Class<?> c, String attributeName);

    // DocumentService

    /**
     * Finds the Document for the specified class with the given id.
     */
    <T extends Document> T findByDocumentHeaderId(Class<T> documentClass, String id);

    /**
     * Finds the Documents for the specified class with the given list of ids.
     */
    <T extends Document> List<T> findByDocumentHeaderIds(Class<T> documentClass, List<String> ids);

    public static final String CLASS_NOT_PERSISTABLE_OJB_EXCEPTION_CLASS = "org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException";
    public static final String OPTIMISTIC_LOCK_OJB_EXCEPTION_CLASS = "org.apache.ojb.broker.OptimisticLockException";
}