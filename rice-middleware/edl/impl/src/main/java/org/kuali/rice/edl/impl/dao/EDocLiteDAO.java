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
package org.kuali.rice.edl.impl.dao;

import java.util.List;

import org.kuali.rice.edl.impl.bo.EDocLiteAssociation;
import org.kuali.rice.edl.impl.bo.EDocLiteDefinition;


public interface EDocLiteDAO {

    /**
     * Persists the given {@link EDocLiteDefinition} to the datasource.
     * @param definition the item to save.
     * @return the saved {@link EDocLiteDefinition}
     */
    public EDocLiteDefinition saveEDocLiteDefinition(EDocLiteDefinition definition);

    /**
     * Persists the given {@link EDocLiteAssociation} to the datasource.
     * @param assoc the {@link EDocLiteAssociation} to save
     * @return the saved {@link EDocLiteDefinition}
     */
    public EDocLiteAssociation saveEDocLiteAssociation(EDocLiteAssociation assoc);

    /**
     * Returns a {@link EDocLiteDefinition} with the given definition name.
     * @param defName the definition name
     * @return a {@link EDocLiteDefinition} with the given definition name.
     */
    public EDocLiteDefinition getEDocLiteDefinition(String defName);

    /**
     * Returns a {@link EDocLiteAssociation} with the associated document type name.
     * @param documentTypeName the document type name
     * @return a {@link EDocLiteAssociation}
     */
    public EDocLiteAssociation getEDocLiteAssociation(String documentTypeName);

    /**
     * Returns a {@link EDocLiteAssociation} for the related association id
     * @param associationId the association id
     * @return a {@link EDocLiteAssociation}
     */
    public EDocLiteAssociation getEDocLiteAssociation(Long associationId);

    /**
     * Returns all active {@link EDocLiteDefinition}.
     * @return all active {@link EDocLiteDefinition}
     */
    public List<String> getEDocLiteDefinitions();

    /**
     * Returns all active {@link EDocLiteAssociation}.
     * @return all active {@link EDocLiteAssociation}
     */
    public List<EDocLiteAssociation> getEDocLiteAssociations();

    /**
     * Returns a collection of {@link EDocLiteAssociation} with similar properties as the given object.
     * @param edocLite
     * @return a {@link List} of {@link EDocLiteAssociation}
     */
    public List<EDocLiteAssociation> search(EDocLiteAssociation edocLite);
}
