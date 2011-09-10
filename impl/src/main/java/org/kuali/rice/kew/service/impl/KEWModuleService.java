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
package org.kuali.rice.kew.service.impl;

import org.joda.time.DateTime;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.docsearch.DocumentEbo;
import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.service.impl.ModuleServiceBase;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The ModuleService for KEW
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KEWModuleService extends ModuleServiceBase {

	protected DocumentTypeService docTypeService = null;

	/**
	 * These are the "primary" keys for the DocTypeService. We are considering both
	 * name and documentTypeId to be unique.
	 *
	 * @see org.kuali.rice.krad.service.impl.ModuleServiceBase#listPrimaryKeyFieldNames(java.lang.Class)
	 */
	@Override
	public List<String> listPrimaryKeyFieldNames(Class businessObjectInterfaceClass) {
		if ( DocumentTypeEBO.class.isAssignableFrom( businessObjectInterfaceClass ) ) {
			List<String> pkFields = new ArrayList<String>( 1 );
			pkFields.add( "documentTypeId" );
			return pkFields;
		}else if(DocumentEbo.class.isAssignableFrom( businessObjectInterfaceClass )){
			List<String> pkFields = new ArrayList<String>( 1 );
			pkFields.add( "documentId" );
			return pkFields;
		}
		return super.listPrimaryKeyFieldNames(businessObjectInterfaceClass);
	}

	/**
	 * This overridden method calls the DocumentTypeService instead of the underlying
	 * KNS service.  Allows you to search on name and docTypeId
	 *
	 * @see org.kuali.rice.krad.service.impl.ModuleServiceBase#getExternalizableBusinessObject(java.lang.Class, java.util.Map)
	 */
	@Override
	public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(
			Class<T> businessObjectClass, Map<String, Object> fieldValues) {
		if(DocumentTypeEBO.class.isAssignableFrom(businessObjectClass)){
			if ( fieldValues.containsKey( "name" ) ) {
				return (T)getDocumentTypeService().findByName((String)fieldValues.get( "name" ) );
			}else if( fieldValues.containsKey( "documentTypeId" ) ){
				return (T)getDocumentTypeService().findById(fieldValues.get( "documentTypeId" ).toString());
			}else if (fieldValues.containsKey( "id" ) ) {
				// assume it's a string and convert it to a long.
				return (T)getDocumentTypeService().findById(fieldValues.get( "id" ).toString());
			}

		}else if(DocumentEbo.class.isAssignableFrom( businessObjectClass )){
			if ( fieldValues.containsKey( "documentId" ) ) {
				return (T)createDocumentSearchEbo(KEWServiceLocator.getRouteHeaderService().getRouteHeader(fieldValues.get( "documentId" ).toString()));
			}

		}

		// otherwise, use the default implementation
		return super.getExternalizableBusinessObject(businessObjectClass, fieldValues);
	}

	/**
	 * @return the docTypeService
	 */
	protected DocumentTypeService getDocumentTypeService() {
		if(this.docTypeService == null){
			// the default
			this.docTypeService = KEWServiceLocator.getDocumentTypeService();
		}
		return this.docTypeService;
	}

	/**
	 * @param docTypeService the docTypeService to set
	 */
	public void setDocumentTypeService(DocumentTypeService docTypeService) {
		this.docTypeService = docTypeService;
	}

	private DocumentEbo createDocumentSearchEbo(final DocumentRouteHeaderValue routeHeaderValue){
		return new DocumentEbo(){

            @Override
            public String getApplicationDocumentId() {
                return routeHeaderValue.getApplicationDocumentId();
            }

            @Override
            public DocumentStatus getStatus() {
                return routeHeaderValue.getStatus();
            }

            @Override
            public String getApplicationDocumentStatus() {
                return routeHeaderValue.getApplicationDocumentStatus();
            }

            @Override
            public String getTitle() {
                return routeHeaderValue.getTitle();
            }

            @Override
            public String getDocumentTypeName() {
                return routeHeaderValue.getDocumentTypeName();
            }

            @Override
            public String getInitiatorPrincipalId() {
                return routeHeaderValue.getInitiatorPrincipalId();
            }

            @Override
            public String getDocumentId() {
                return routeHeaderValue.getDocumentId();
            }

            @Override
            public DateTime getDateCreated() {
                return routeHeaderValue.getDateCreated();
            }

            @Override
            public void refresh() {
                // do nothing
            }
            
        };
	}
	/**
	 * This overridden method rewrites the URL.
	 *
	 * @see org.kuali.rice.krad.service.impl.ModuleServiceBase#getExternalizableBusinessObjectInquiryUrl(java.lang.Class, java.util.Map)
	 */
	@Override
	public String getExternalizableBusinessObjectInquiryUrl(
			Class inquiryBusinessObjectClass, Map<String, String[]> parameters) {
		if ( DocumentTypeEBO.class.isAssignableFrom( inquiryBusinessObjectClass ) ) {
			int nonBlank = 0;
			boolean nameFound = false;
			//"name" is the only non-blank property passed in
			for(String key: parameters.keySet()){
				if("name".equals(key) && parameters.get(key) != null){
					nameFound=true;
				}else if(!"name".equals(key) && parameters.get(key) != null){
					nonBlank ++;
				}
			}

			if(nonBlank == 0 && nameFound == true){
				parameters.clear(); // clear out other parameters, including the name pass in
				DocumentTypeEBO dte = (DocumentTypeEBO)getDocumentTypeService().findByName(parameters.get( "name" )[0] );
				String[] strArr = {dte.getDocumentTypeId().toString()};
				parameters.put("documentTypeId", strArr);
			}

		}

		return super.getExternalizableBusinessObjectInquiryUrl(
				inquiryBusinessObjectClass, parameters);
	}
	/**
	 * We want to be able to use name as an alternate key
	 *
	 * @see org.kuali.rice.krad.service.ModuleService#listAlternatePrimaryKeyFieldNames(java.lang.Class)
	 */
	public List<List<String>> listAlternatePrimaryKeyFieldNames(
			Class businessObjectInterfaceClass) {
		if ( DocumentTypeEBO.class.isAssignableFrom( businessObjectInterfaceClass ) ) {
			ArrayList<List<String>> retList = new ArrayList<List<String>>();
			ArrayList<String> keyList = new ArrayList<String>();

			keyList.add("name");
			retList.add(keyList);
			return retList;
		}else{
			return null;
		}

	}
}

