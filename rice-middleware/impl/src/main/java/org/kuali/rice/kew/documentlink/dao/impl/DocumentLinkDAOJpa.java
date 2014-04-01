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
package org.kuali.rice.kew.documentlink.dao.impl;

import org.kuali.rice.kew.documentlink.DocumentLink;
import org.kuali.rice.kew.documentlink.dao.DocumentLinkDAO;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * A JPA-based implementation of the {@link DocumentLinkDAO}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentLinkDAOJpa implements DocumentLinkDAO {

    private EntityManager entityManager;
    private DataObjectService dataObjectService;

    @Override
    public void deleteDocumentLink(DocumentLink link) {
        getDataObjectService().delete(link);
        // see if a reverse link exists or not
        DocumentLink reverseLink = getLinkedDocument(link.getDestDocId(), link.getOrgnDocId());
        if (reverseLink != null) {
            getDataObjectService().delete(reverseLink);
        }
    }

    @Override
    public List<DocumentLink> getLinkedDocumentsByDocId(String docId) {
        TypedQuery<DocumentLink> query =
                getEntityManager().createNamedQuery("DocumentLink.GetLinkedDocumentsByDocId", DocumentLink.class);
        query.setParameter("orgnDocId",docId);
        return query.getResultList();

    }

    @Override
    public List<DocumentLink> getOutgoingLinkedDocumentsByDocId(String docId) {
        TypedQuery<DocumentLink> query =
                getEntityManager().createNamedQuery("DocumentLink.GetOutgoingLinkedDocumentsByDocId", DocumentLink.class);
        query.setParameter("destDocId",docId);
        return query.getResultList();
    }

    @Override
    public DocumentLink saveDocumentLink(DocumentLink link) {
        link = saveIfNotExists(link);
        // create the 2-way linked pair
        saveIfNotExists(createReverseLink(link));
        getDataObjectService().flush(DocumentLink.class);
        return link;
    }

    protected DocumentLink saveIfNotExists(DocumentLink link) {
        // if an existing link already exists for this, we pretty much just ignore the request to save since it's
        // already there
        DocumentLink existingLink = getLinkedDocument(link.getOrgnDocId(), link.getDestDocId());
        if (existingLink == null) {
            link = getDataObjectService().save(link);
        } else {
            link = existingLink;
        }
        return link;
    }

    protected DocumentLink getLinkedDocument(String orgnDocId, String destDocId) {
        TypedQuery<DocumentLink> query =
                getEntityManager().createNamedQuery("DocumentLink.GetLinkedDocument", DocumentLink.class);
        query.setParameter("orgnDocId", orgnDocId);
        query.setParameter("destDocId", destDocId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    private DocumentLink createReverseLink(DocumentLink link) {
        DocumentLink reverseLink = new DocumentLink();
        reverseLink.setOrgnDocId(link.getDestDocId());
        reverseLink.setDestDocId(link.getOrgnDocId());
        return reverseLink;
    }

    @Override
    public DocumentLink getDocumentLink(String documentLinkId) {
        return getDataObjectService().find(DocumentLink.class,documentLinkId);
    }


    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }


    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
