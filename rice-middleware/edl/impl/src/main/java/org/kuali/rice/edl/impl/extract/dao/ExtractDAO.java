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
package org.kuali.rice.edl.impl.extract.dao;

import java.util.List;

import org.kuali.rice.edl.impl.extract.Dump;
import org.kuali.rice.edl.impl.extract.Fields;


public interface ExtractDAO {

    /**
     * Returns a {@link Dump} for the given document id
     * @param documentId the document id
     * @return a {@link Dump}
     */
    public Dump getDumpByDocumentId(String documentId);

    /**
     * Returns all {@link Fields} with the given document id.
     * @param documentId the document id.
     * @return a {@link List} of {@link Fields}
     */
    public List<Fields> getFieldsByDocumentId(String documentId);

    /**
     * Persists the given item to the underlying datasource.
     * @param dump the item to save
     * @return the saved {@link Dump}
     */
    public Dump saveDump(Dump dump);

    /**
     * Removes a {@link Dump} from the underlying datasource for the given document id.
     * @param documentId the document id
     */
    public void deleteDump(String documentId);

    /**
     * Persists the given item to the underlying datasource.
     * @param field the item to save
     * @return the saved {@link Fields}
     */
    public Fields saveField(Fields field);



}
