/**
 * Copyright 2005-2013 The Kuali Foundation
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

import org.kuali.rice.core.api.util.io.SerializationUtils;
import org.kuali.rice.core.framework.persistence.jpa.OrmUtils;
import org.kuali.rice.core.framework.persistence.jpa.criteria.Criteria;
import org.kuali.rice.core.framework.persistence.jpa.criteria.QueryByCriteria;
import org.kuali.rice.kew.documentlink.DocumentLink;
import org.kuali.rice.kew.documentlink.dao.DocumentLinkDAO;
import org.kuali.rice.krad.data.DataObjectService;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * This is a description of what this class does - g1zhang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentLinkDAOJpa
        implements DocumentLinkDAO {

	
    @PersistenceContext(unitName = "kew")
    private EntityManager entityManager;
    private DataObjectService dataObjectService;
    
	/**
	 * double delete all links from orgn doc
	 * 
	 * @see org.kuali.rice.kew.documentlink.dao.DocumentLinkDAO#deleteDocumentLinksByDocId
	 */
	public void deleteDocumentLinksByDocId(String docId) {
		List<DocumentLink> links = getLinkedDocumentsByDocId(docId);
		for(DocumentLink link: links){
			deleteDocumentLink(link);
		}
	}

	/**
	 * double delete a link
	 * 
	 * @see org.kuali.rice.kew.documentlink.dao.DocumentLinkDAO#deleteDocumentLink(org.kuali.rice.kew.documentlink.DocumentLink)
	 */
	public void deleteDocumentLink(DocumentLink link) {
		deleteSingleLinkFromOrgnDoc(link);
		deleteSingleLinkFromOrgnDoc(DocumentLinkDaoUtil.reverseLink((DocumentLink) SerializationUtils.deepCopy(link)));
	}

	/**
	 * get a link from orgn doc
	 * 
	 * @see org.kuali.rice.kew.documentlink.dao.DocumentLinkDAO#getLinkedDocument(org.kuali.rice.kew.documentlink.DocumentLink)
	 */
	public DocumentLink getLinkedDocument(DocumentLink link) {
        Query query = getEntityManager().createNamedQuery("DocumentLink.GetLinkedDocument");
        query.setParameter("orgnDocId", link.getOrgnDocId());
        query.setParameter("destDocId",link.getDestDocId());
		try {
			return (DocumentLink) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * get all links from orgn doc
	 * 
	 * @see org.kuali.rice.kew.documentlink.dao.DocumentLinkDAO#getLinkedDocumentsByDocId(java.lang.String)
	 */
	public List<DocumentLink> getLinkedDocumentsByDocId(String docId) {
        Query query = getEntityManager().createNamedQuery("DocumentLink.GetLinkedDocumentsByDocId");
        query.setParameter("orgnDocId",docId);
        return (List<DocumentLink>)query.getResultList();
	
	}
	
	public List<DocumentLink> getOutgoingLinkedDocumentsByDocId(String docId) {
        Query query = getEntityManager().createNamedQuery("DocumentLink.GetOutgoingLinkedDocumentsByDocId");
        query.setParameter("destDocId",docId);
        return (List<DocumentLink>)query.getResultList();
	}

	/**
	 * add double link
	 * 
	 * @see org.kuali.rice.kew.documentlink.dao.DocumentLinkDAO#saveDocumentLink(org.kuali.rice.kew.documentlink.DocumentLink)
	 */
	public void saveDocumentLink(DocumentLink link) {
		DocumentLink linkedDocument = getLinkedDocument(link);
		if(linkedDocument == null) {
			getDataObjectService().save(link);
		} else {
			link.setDocLinkId(linkedDocument.getDocLinkId());
		}
//		//if we want a 2-way linked pair
		DocumentLink rLink = DocumentLinkDaoUtil.reverseLink((DocumentLink)SerializationUtils.deepCopy(link));
		if(getLinkedDocument(rLink) == null) {
			getDataObjectService().save(rLink);
		}

	}
	
	private void deleteSingleLinkFromOrgnDoc(DocumentLink link){
		DocumentLink cur = getLinkedDocument(link);
		getDataObjectService().delete(cur);
	}

	@Override
	public DocumentLink getDocumentLink(Long documentLinkId) {
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
