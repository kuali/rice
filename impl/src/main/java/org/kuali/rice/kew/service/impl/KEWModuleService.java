/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.doctype.bo.DocumentTypeEBO;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.impl.ModuleServiceBase;
import org.kuali.rice.kew.doctype.bo.DocumentType;

/**
 * The ModuleService for KEW
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KEWModuleService extends ModuleServiceBase {

	protected DocumentTypeService docTypeService = null;

	/**
	 * These are the "primary" keys for the DocTypeService. We are considering both
	 * name and documentTypeId to be unique.
	 *
	 * @see org.kuali.rice.kns.service.impl.ModuleServiceBase#listPrimaryKeyFieldNames(java.lang.Class)
	 */
	@Override
	public List<String> listPrimaryKeyFieldNames(Class businessObjectInterfaceClass) {
		if ( DocumentTypeEBO.class.isAssignableFrom( businessObjectInterfaceClass ) ) {
			List<String> pkFields = new ArrayList<String>( 1 );
			pkFields.add( "name" );
			pkFields.add( "documentTypeId" );
			return pkFields;
		}
		return super.listPrimaryKeyFieldNames(businessObjectInterfaceClass);
	}

	/**
	 * This overridden method calls the DocumentTypeService instead of the underlying
	 * KNS service.  Allows you to search on name and docTypeId
	 *
	 * @see org.kuali.rice.kns.service.impl.ModuleServiceBase#getExternalizableBusinessObject(java.lang.Class, java.util.Map)
	 */
	@Override
	public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(
			Class<T> businessObjectClass, Map<String, Object> fieldValues) {
		if(DocumentTypeEBO.class.isAssignableFrom(businessObjectClass)){
			if ( fieldValues.containsKey( "name" ) ) {
				return (T)getDocumentTypeService().findByName((String)fieldValues.get( "name" ) );
			}else if( fieldValues.containsKey( "documentTypeId" ) ){
				return (T)getDocumentTypeService().findById((Long)fieldValues.get( "documentTypeId" ));
			}else if (fieldValues.containsKey( "id" ) ) {
				return (T)getDocumentTypeService().findById((Long)fieldValues.get( "id" ));
			}

		}

		// otherwise, use the default implementation
		return super.getExternalizableBusinessObject(businessObjectClass, fieldValues);
	}

	/**
	 * This overridden method returns the BusinessObjectEntry for a DocumentType
	 *
	 * Not sure if we need this method. The default might work just fine.
	 *
	 * @see org.kuali.rice.kns.service.impl.ModuleServiceBase#getExternalizableBusinessObjectDictionaryEntry(java.lang.Class)
	 */
	@Override
	public BusinessObjectEntry getExternalizableBusinessObjectDictionaryEntry(
			Class businessObjectInterfaceClass) {

		if(DocumentTypeEBO.class.isAssignableFrom(businessObjectInterfaceClass)){
			return
				KNSServiceLocator.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(DocumentType.class.getCanonicalName());
		}

		// the default
		return super
				.getExternalizableBusinessObjectDictionaryEntry(businessObjectInterfaceClass);
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
}

