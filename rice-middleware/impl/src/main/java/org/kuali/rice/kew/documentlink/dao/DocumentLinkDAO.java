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
package org.kuali.rice.kew.documentlink.dao;

import java.util.List;

import org.kuali.rice.kew.documentlink.DocumentLink;

/**
 * Data Access Object for {@link DocumentLink}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface DocumentLinkDAO {

    /**
     * Get all docs linked to origin doc.
     */
    List<DocumentLink> getLinkedDocumentsByDocId(String docId);

    List<DocumentLink> getOutgoingLinkedDocumentsByDocId(String docId);

    /**
     * save a link for 2 docs
     */
    DocumentLink saveDocumentLink(DocumentLink link);

    /**
     * Delete a link between 2 docs.
     */
    void deleteDocumentLink(DocumentLink link);

    DocumentLink getDocumentLink(String documentLinkId);

}
