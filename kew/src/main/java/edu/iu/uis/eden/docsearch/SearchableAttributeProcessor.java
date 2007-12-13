/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.docsearch;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;
import edu.iu.uis.eden.util.Utilities;

/**
 * Implementation of {@link SearchableAttributeProcessingService}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SearchableAttributeProcessor implements SearchableAttributeProcessingService {

	private static Logger LOG = Logger.getLogger(SearchableAttribute.class);

	private static final String DONT_INDEX = "doNotExecuteSearchableAttributeIndexing";

	public void indexDocument(Long documentId) {
		indexDocument(documentId, true);
	}

	public void indexDocument(Long documentId, boolean useMostRecentDocType) {
		LOG.debug("indexing document " + documentId + " for document search");
		KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);
		DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		try {
			if (shouldIndex(document)) {
				KEWServiceLocator.getRouteHeaderService().clearRouteHeaderSearchValues(document);
				document.setSearchableAttributeValues(buildSearchableAttributeValues(document.getDocumentType(), document, document.getDocContent(), useMostRecentDocType));
				KEWServiceLocator.getRouteHeaderService().saveRouteHeader(document);
			}
		} catch (Exception e) {
			String errorMsg = "Encountered an error when attempting to index searchable attributes, requeuing.";
			LOG.error(errorMsg, e);
			throw new WorkflowRuntimeException(errorMsg,e);
		}
	}

	private List<SearchableAttributeValue> buildSearchableAttributeValues(DocumentType docType, DocumentRouteHeaderValue document, String docContent, boolean useMostRecentDocType) {
		if (useMostRecentDocType) {
			docType = KEWServiceLocator.getDocumentTypeService().findByName(docType.getName());
		}
		List<SearchableAttributeValue> searchableAttributeValues = new ArrayList<SearchableAttributeValue>();

		for (Iterator iterator = docType.getSearchableAttributes().iterator(); iterator.hasNext();) {
			SearchableAttribute searchableAttribute = (SearchableAttribute) iterator.next();
			List searchStorageValues = searchableAttribute.getSearchStorageValues(docContent);
			if (searchStorageValues != null) {
				for (Iterator iterator2 = searchStorageValues.iterator(); iterator2.hasNext();) {
					SearchableAttributeValue searchableAttributeValue = (SearchableAttributeValue) iterator2.next();
					searchableAttributeValue.setRouteHeader(document);
					searchableAttributeValues.add(searchableAttributeValue);
				}
			}
		}

		return searchableAttributeValues;
	}

	protected boolean shouldIndex(DocumentRouteHeaderValue document) throws XPathExpressionException {
		XPath xpath = XPathHelper.newXPath();
		if (Utilities.isEmpty(document.getDocumentContent().getDocumentContent())) {
		    // returning true since the 'do not index' variable does not exist in the doc content xml since there is no doc content xml
		    return true;
		}
		return !(Boolean)xpath.evaluate("//"+DONT_INDEX, new InputSource(new StringReader(document.getDocumentContent().getDocumentContent())), XPathConstants.BOOLEAN);
	}

}
