/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kew.impl.doctype;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.dao.DocumentTypeDAO;
import org.kuali.rice.kew.service.KEWServiceLocator;

/**
 * Reference implementation of the {@link DocumentTypeService}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private static final Logger LOG = Logger.getLogger(DocumentTypeServiceImpl.class);

    private DocumentTypeDAO documentTypeDao;

    @Override
    public String getDocumentTypeIdByName(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new RiceIllegalArgumentException("documentTypeName was null or blank");
        }
        return documentTypeDao.findDocumentTypeIdByName(documentTypeName);
    }

    @Override
    public org.kuali.rice.kew.api.doctype.DocumentType getDocumentTypeById(String documentTypeId) {
        if (StringUtils.isBlank(documentTypeId)) {
            throw new RiceIllegalArgumentException("documentTypeId was null or blank");
        }
        DocumentType documentTypeBo = documentTypeDao.findById(documentTypeId);
        return DocumentType.to(documentTypeBo);
    }

    @Override
    public org.kuali.rice.kew.api.doctype.DocumentType getDocumentTypeByName(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new RiceIllegalArgumentException("documentTypeName was null or blank");
        }
        DocumentType documentTypeBo = documentTypeDao.findByName(documentTypeName);
        return DocumentType.to(documentTypeBo);
    }

    @Override
    public boolean isSuperUser(String principalId, String documentTypeId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Determining super user status [principalId=" + principalId + ", documentTypeId="
                    + documentTypeId + "]");
        }
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId was null or blank");
        }
        if (StringUtils.isBlank(documentTypeId)) {
            throw new RiceIllegalArgumentException("documentTypeId was null or blank");
        }
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findById(documentTypeId);
        boolean isSuperUser = KEWServiceLocator.getDocumentTypePermissionService().canAdministerRouting(principalId,
                documentType);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Super user status is " + isSuperUser + ".");
        }
        return isSuperUser;

    }

    public void setDocumentTypeDao(DocumentTypeDAO documentTypeDao) {
        this.documentTypeDao = documentTypeDao;
    }

}
