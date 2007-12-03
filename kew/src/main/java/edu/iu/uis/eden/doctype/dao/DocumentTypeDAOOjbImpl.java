/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.doctype.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.springmodules.orm.ojb.OjbFactoryUtils;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;

import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeAttribute;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.util.Utilities;

public class DocumentTypeDAOOjbImpl extends PersistenceBrokerDaoSupport implements DocumentTypeDAO {

	public static final Logger LOG = Logger.getLogger(DocumentTypeDAOOjbImpl.class);
	
	public void delete(DocumentType documentType) {
		this.getPersistenceBrokerTemplate().delete(documentType);
	}

	public DocumentType findByDocId(Long docId) {
		Criteria crit = new Criteria();
		crit.addEqualTo("documentTypeId", docId);
		return (DocumentType) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(DocumentType.class, crit));
	}

	public DocumentType findByName(String name) {
		Criteria crit = new Criteria();
		crit.addEqualTo("name", name);
		crit.addEqualTo("currentInd", new Boolean(true));
		DocumentType docType = (DocumentType) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(DocumentType.class, crit));
		return docType;
	}

	public Integer getMaxVersionNumber(String docTypeName) {
		return getMostRecentDocType(docTypeName).getVersion();
	}
	
	public List getChildDocumentTypeIds(Long parentDocumentTypeId) {
		List childrenIds = new ArrayList();
		PersistenceBroker broker = getPersistenceBroker(false);
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = broker.serviceConnectionManager().getConnection();
			st = conn.createStatement();
			rs = st.executeQuery("select DOC_TYP_ID from EN_DOC_TYP_T where DOC_TYP_CUR_IND = 1 and DOC_TYP_PARNT_ID = " + parentDocumentTypeId);
			while (rs.next()) {
				childrenIds.add(new Long(rs.getLong("DOC_TYP_ID")));
			}	
		} catch (Exception e) {
			LOG.error("Error occured fetching children document type ids for document type " + parentDocumentTypeId, e);
			throw new RuntimeException(e);
		} finally {
			try {
				st.close();
			} catch (Exception e) {
				LOG.warn("Failed to close Statement", e);
			}
			
			try {
				rs.close();
			} catch (Exception e) {
				LOG.warn("Failed to close Resultset", e);
			}
			
        	if (broker != null) {
        		try {
        			OjbFactoryUtils.releasePersistenceBroker(broker, this.getPersistenceBrokerTemplate().getPbKey());
        		} catch (Exception e) {
        			LOG.error("Failed closing connection: " + e.getMessage(), e);
        		}
        	}
		}
		return childrenIds;
	}

	public DocumentType getMostRecentDocType(String docTypeName) {
		Criteria crit = new Criteria();
		crit.addEqualTo("name", docTypeName);
		QueryByCriteria query = new QueryByCriteria(DocumentType.class, crit);
		query.addOrderByDescending("version");

		Iterator docTypes = this.getPersistenceBrokerTemplate().getCollectionByQuery(query).iterator();
		while (docTypes.hasNext()) {
			return (DocumentType) docTypes.next();
		}
		return null;
	}

	public void save(DocumentType documentType) {
		this.getPersistenceBrokerTemplate().store(documentType);
	}

	public List findByRouteHeaderId(Long routeHeaderId) {
		Criteria crit = new Criteria();
		crit.addEqualTo("routeHeaderId", routeHeaderId);
		return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(DocumentType.class, crit));
	}

	public Collection find(DocumentType documentType, DocumentType docTypeParent, boolean climbHierarchy) {
		LOG.debug("documentType: "+ documentType);
		LOG.debug("docTypeParent: "+ docTypeParent);
		LOG.debug("climbHierarchy: " + climbHierarchy);

		Criteria crit = new Criteria();
		if (documentType != null && !Utilities.isEmpty(documentType.getLabel())) {
			crit.addLike("UPPER(label)", documentType.getLabel().trim().toUpperCase());
		}
		if (documentType != null && !Utilities.isEmpty(documentType.getName())) {
			String docTypeName = documentType.getName();
			crit.addLike("UPPER(name)", ("%" + docTypeName.trim() + "%").toUpperCase());
		}
		if (documentType != null && documentType.getActiveInd() != null) {
			crit.addEqualTo("activeInd", documentType.getActiveInd());
		}
		if (documentType != null && documentType.getDocumentTypeId() != null) {
			crit.addEqualTo("documentTypeId", documentType.getDocumentTypeId());
		}
		if (docTypeParent != null) {
			if (!"".equals(docTypeParent.getName()) && docTypeParent.getName() != null) {
				Criteria parentCrit = new Criteria();
				//addParentNameOrCriteria(docTypeParent.getName(), parentCrit);
				addParentIdOrCriteria(docTypeParent.getDocumentTypeId(), parentCrit);
				if (climbHierarchy) {
					assembleChildrenCriteria(docTypeParent.getChildrenDocTypes(), parentCrit);
				}
				parentCrit.addEqualTo("currentInd", Boolean.TRUE);
				crit.addAndCriteria(parentCrit);
			}
		} else {
			if (documentType != null && !Utilities.isEmpty(documentType.getName())) {
				DocumentType searchDocumentType = findByName(documentType.getName());
				if ((searchDocumentType != null) && climbHierarchy) {
					LOG.debug("searchDocumentType: "+ searchDocumentType);
					Criteria criteria = new Criteria();
					//addParentNameOrCriteria(searchDocumentType.getName(), criteria);
                    addParentIdOrCriteria(searchDocumentType.getDocumentTypeId(), criteria);
                    assembleChildrenCriteria(searchDocumentType.getChildrenDocTypes(), criteria);
					criteria.addEqualTo("currentInd", Boolean.TRUE);
					crit.addOrCriteria(criteria);
				}
			}
		}
		crit.addEqualTo("currentInd", Boolean.TRUE);
		return this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(DocumentType.class, crit));
	}

    private void addParentIdOrCriteria(Long parentId, Criteria mainCriteria) {
        Criteria parentCriteria = new Criteria();
        parentCriteria.addEqualTo("docTypeParentId", parentId);
        mainCriteria.addOrCriteria(parentCriteria);
    }

	private void assembleChildrenCriteria(Collection childrenDocTypes, Criteria crit) {
		if (childrenDocTypes != null) {
			Iterator childrenDocTypesIter = childrenDocTypes.iterator();
			while (childrenDocTypesIter.hasNext()) {
				DocumentType child = (DocumentType) childrenDocTypesIter.next();
				addParentIdOrCriteria(child.getDocumentTypeId(), crit);
				assembleChildrenCriteria(child.getChildrenDocTypes(), crit);
			}
		}
	}

	public DocumentType getMostRecentDocType(Long documentTypeId) {
		Criteria crit = new Criteria();
		crit.addEqualTo("documentTypeId", documentTypeId);
		QueryByCriteria query = new QueryByCriteria(DocumentType.class, crit);
		query.addOrderByDescending("version");

		Iterator docTypes = this.getPersistenceBrokerTemplate().getCollectionByQuery(query).iterator();
		while (docTypes.hasNext()) {
			return (DocumentType) docTypes.next();
		}
		return null;
	}

	public List findAllCurrentRootDocuments() {
		Criteria crit = new Criteria();
		crit.addIsNull("docTypeParentId");
		crit.addEqualTo("currentInd", Boolean.TRUE);
		return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(DocumentType.class, crit));
	}
    
    public List findAllCurrent() {
        Criteria crit = new Criteria();
        crit.addEqualTo("currentInd", Boolean.TRUE);
        return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(DocumentType.class, crit));
    }
    
    public List findDocumentTypeAttributes(RuleAttribute ruleAttribute) {
    	Criteria crit = new Criteria();
    	crit.addEqualTo("ruleAttributeId", ruleAttribute.getRuleAttributeId());
    	return (List) this.getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(DocumentTypeAttribute.class, crit));
    }
    
    public Long findDocumentTypeIdByDocumentId(Long documentId) {
    	Criteria crit = new Criteria();
    	crit.addEqualTo("routeHeaderId", documentId);
    	ReportQueryByCriteria query = QueryFactory.newReportQuery(DocumentRouteHeaderValue.class, crit);
    	query.setAttributes(new String[] { "documentTypeId" });
    	return (Long)this.getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

}