/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.framework.docsearch;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Collection;

/**
 * This class contains all the information needed for document search, validation and indexing. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlRootElement(name = DocumentSearchContext.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DocumentSearchContext.Constants.TYPE_NAME, propOrder = {
    DocumentSearchContext.Elements.DOCUMENT,
    DocumentSearchContext.Elements.DOCUMENT_CONTENT,
    DocumentSearchContext.Elements.DOCUMENT_TYPE_NAME,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DocumentSearchContext extends AbstractDataTransferObject {

    @XmlElement(name = Elements.DOCUMENT, required = false)
	private final Document document;

    @XmlElement(name = Elements.DOCUMENT_CONTENT, required = false)
	private final DocumentContent documentContent;

    @XmlElement(name = Elements.DOCUMENT_TYPE_NAME, required = false)
    private final String documentTypeName;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private DocumentSearchContext() {
        this.document = null;
        this.documentContent = null;
        this.documentTypeName = null;
    }

    private DocumentSearchContext(Document document, DocumentContent documentContent, String documentTypeName) {
        this.document = document;
        this.documentContent = documentContent;
        this.documentTypeName = documentTypeName;
    }

    public static DocumentSearchContext createFullContext(Document document, DocumentContent documentContent, String documentTypeName) {
        if (document == null) {
            throw new IllegalArgumentException("document was null");
        }
        if (documentContent == null) {
            throw new IllegalArgumentException("documentContent was null");
        }
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("documentTypeName was null or blank");
        }
        return new DocumentSearchContext(document, documentContent, documentTypeName);
    }

    public static DocumentSearchContext createDocumentTypeNameContext(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("documentTypeName was null or blank");
        }
        return new DocumentSearchContext(null, null, documentTypeName);
    }

    public Document getDocument() {
        return document;
    }

    public DocumentContent getDocumentContent() {
        return documentContent;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "documentSearchContext";
        final static String TYPE_NAME = "DocumentSearchContextType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] { CoreConstants.CommonElements.FUTURE_ELEMENTS };
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String DOCUMENT = "document";
        final static String DOCUMENT_CONTENT = "documentContent";
        final static String DOCUMENT_TYPE_NAME = "documentTypeName";
    }
	
}
