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
package mocks;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.DocumentTypeVO;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.postprocessor.PostProcessor;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.user.WorkflowUser;

public class MockDocumentTypeServiceImpl implements DocumentTypeService {

    public DocumentType setDocumentTypeVersion(DocumentType documentType, boolean currentInd) {
        return null;
    }
    public DocumentType getMostRecentDocType(String docTypeName) {
        return null;
    }
    public boolean isLockedForRouting(DocumentType documentType) {
        return false;
    }
    private Map documentsById = new HashMap();
    private Map documentsByName = new HashMap();
    private Map postProcessors = new HashMap();

    public void makeCurrent(List documentTypes) {
        throw new UnsupportedOperationException("not yet implmeneted");
    }
    
    public void addDocumentType(DocumentType documentType, PostProcessor postProcessor) {
        documentsById.put(documentType.getDocumentTypeId(), documentType);
        documentsByName.put(documentType.getName(), documentType);
        postProcessors.put(documentType.getDocumentTypeId(), postProcessor);
    }

    public DocumentType findByDocumentId(Long documentId) {
		throw new UnsupportedOperationException("not yet implemented");
	}
    
	public DocumentTypeVO getDocumentTypeVOById(Long documentTypeId) {
        DocumentType docType = findById(documentTypeId);
        DocumentTypeVO docTypeVO = new DocumentTypeVO();
        docTypeVO.setDocTypeParentId(docType.getDocTypeParentId());
        docTypeVO.setDocTypeDescription(docType.getDescription());
        docTypeVO.setDocTypeHandlerUrl(docType.getDocHandlerUrl());
        docTypeVO.setDocTypeId(docType.getDocumentTypeId());
        docTypeVO.setDocTypeLabel(docType.getLabel());
        docTypeVO.setName(docType.getName());
        docTypeVO.setDocTypeVersion(docType.getVersion());
        if (docType.getCurrentInd().booleanValue()) {
            docTypeVO.setDocTypeCurrentInd(EdenConstants.ACTIVE_CD);
        } else {
            docTypeVO.setDocTypeCurrentInd(EdenConstants.INACTIVE_CD);
        }
        docTypeVO.setPostProcessorName(docType.getPostProcessorName());
        docTypeVO.setDocTypeJndiFactoryClass(null);
        docTypeVO.setDocTypeActiveInd(docType.getActiveInd().booleanValue());
        
        if (docType.getParentDocType() != null) {
            docTypeVO.setDocTypeActiveInherited(true);
        } else {
            docTypeVO.setDocTypeActiveInherited(false);
        }

        docTypeVO.setDocTypeDefaultApprovePolicy(docType.getDefaultApprovePolicy().getPolicyValue().booleanValue());
        docTypeVO.setDocTypePreApprovalPolicy(docType.getPreApprovePolicy().getPolicyValue().booleanValue());
        return docTypeVO;
    }

    public Integer getMaxVersionNumber(String name){
        return new Integer(0);
    }
    public DocumentType findById(Long documentTypeId) {
        return (DocumentType) documentsById.get(documentTypeId);
    }

    public DocumentType findByName(String name) {
        return (DocumentType) documentsByName.get(name);
    }

    public void versionAndSave(DocumentType documentType) {
        addDocumentType(documentType, new MockPostProcessor(true));
    }

    public PostProcessor getPostProcessor(Long documentTypeId) {
        return (PostProcessor) postProcessors.get(documentTypeId);
    }

    public Collection findRouteLevels(Long documentTypeId) {
        return (Collection) ((DocumentType)documentsById.get(documentTypeId)).getRouteLevels();
    }

    public Collection find(DocumentType documentType, String docGroupName, boolean climbHiearchy) {
        throw new UnsupportedOperationException("not implemented in MockDocumentTypeServiceImpl");
    }

    public void delete(DocumentType documentType) {
        documentsById.remove(documentType.getDocumentTypeId());
        documentsByName.remove(documentType.getName());
    }

    public DocumentType route(DocumentType documentType, WorkflowUser user, String annotation) {
        return new DocumentType();
    }    
    
    public List findByRouteHeaderId(Long routeHeaderId) {
        throw new UnsupportedOperationException("not implemented in MockDocumentTypeServiceImpl");
    }
    public void makeCurrent(Long routeHeaderId) {
        throw new UnsupportedOperationException("not implemented in MockDocumentTypeServiceImpl");
    }
    
    public DocumentType blanketApprove(Long routeHeaderId, DocumentType documentType, WorkflowUser user, String annotation) throws Exception {
        return null;
    }
    public List findAllCurrentRootDocuments() {
        return null;
    }
    public DocumentType getMostRecentDocType(Long documentTypeId) {
        return null;
    }
    public DocumentType route(Long routeHeaderId, DocumentType documentType, WorkflowUser user, String annotation) throws Exception {

        return null;
    }
    /* (non-Javadoc)
     * @see edu.iu.uis.eden.doctype.DocumentTypeService#getDocumentTypeVO(java.lang.Long)
     */
    public DocumentTypeVO getDocumentTypeVO(Long documentTypeId) {
        return null;
    }
    /* (non-Javadoc)
     * @see edu.iu.uis.eden.doctype.DocumentTypeService#getDocumentTypeVO(java.lang.String)
     */
    public DocumentTypeVO getDocumentTypeVO(String documentTypeName) {
        return null;
    }
    /* (non-Javadoc)
     * @see edu.iu.uis.eden.doctype.DocumentTypeService#getRootDocumentType(edu.iu.uis.eden.doctype.DocumentType)
     */
    public DocumentType findRootDocumentType(DocumentType docType) {
        return null;
    }
    public void loadXml(InputStream inputStream, WorkflowUser user) {
        throw new UnsupportedOperationException("Mock document type service can't load xml");
    }
    public Element export(ExportDataSet dataSet) {
        return null;
    }
    public List findAllCurrent() {
        return null;
    }
	public List getChildDocumentTypes(DocumentType documentType) {
		return null;
	}
	public DocumentType findByNameIgnoreCache(Long documentTypeId) {
		return null;
	}
	public void save(DocumentType documentType) {
		
	}
	public void clearCacheForAttributeUpdate(RuleAttribute ruleAttribute) {
		
	}
	public Integer getDocumentTypeCount() {
		return null;
	}
    
}