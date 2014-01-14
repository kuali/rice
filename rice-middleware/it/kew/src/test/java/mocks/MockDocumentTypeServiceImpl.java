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
package mocks;

import org.jdom.Element;
import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MockDocumentTypeServiceImpl implements DocumentTypeService {

    private Map<String, DocumentType> documentsById = new HashMap<String, DocumentType>();
    private Map<String, DocumentType> documentsByName = new HashMap<String, DocumentType>();

    @Override
    public DocumentType findByDocumentId(String documentId) {
		throw new UnsupportedOperationException("not yet implemented");
	}

    @Override
    public DocumentType findById(String documentTypeId) {
        return documentsById.get(documentTypeId);
    }

    @Override
    public DocumentType findByName(String name) {
        return documentsByName.get(name);
    }

    @Override
    public DocumentType findByNameCaseInsensitive(String name) {
        return documentsByName.get(name);
    }

    @Override
    public DocumentType versionAndSave(DocumentType documentType) {
        addDocumentType(documentType);
        return documentType;
    }

    @Override
    public Collection<DocumentType> find(DocumentType documentType, String docGroupName, boolean climbHiearchy) {
        throw new UnsupportedOperationException("not implemented in MockDocumentTypeServiceImpl");
    }

    @Override
    public List<DocumentType> findAllCurrentRootDocuments() {
        return null;
    }

    @Override
    public DocumentType findRootDocumentType(DocumentType docType) {
        return null;
    }

    @Override
    public void loadXml(InputStream inputStream, String principalId) {
        throw new UnsupportedOperationException("Mock document type service can't load xml");
    }

    @Override
    public Element export(ExportDataSet dataSet) {
        return null;
    }

	@Override
	public boolean supportPrettyPrint() {
		return true;
	}

    @Override
	public List<DocumentType> findAllCurrent() {
        return null;
    }

    @Override
	public List<DocumentType> getChildDocumentTypes(String documentTypeId) {
		return null;
	}

    @Override
	public DocumentType save(DocumentType documentType) {
       return null;
	}

    @Override
    public List<DocumentType> findPreviousInstances(String documentTypeName) {
        return null;
    }

    private void addDocumentType(DocumentType documentType) {
        documentsById.put(documentType.getDocumentTypeId(), documentType);
        documentsByName.put(documentType.getName(), documentType);
    }

}
