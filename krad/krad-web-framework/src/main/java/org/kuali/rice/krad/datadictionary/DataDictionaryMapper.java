/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary;

import org.kuali.rice.krad.uif.view.View;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Maps one Document type to other document Type.
 * 
 * This interface can be used to implement KNS to workflow document type
 * mapping relationships other than one-to-one.
 * 
 * @author mpk35
 *
 */
public interface DataDictionaryMapper {
	/**
	 * This method gets the business object entry for a concrete class
	 * 
	 * @param className
	 * @return
	 */
    @Deprecated
	public BusinessObjectEntry getBusinessObjectEntryForConcreteClass(DataDictionaryIndex index, String className);
	
	/**
     * This method gets the DataOjectEntry (or subclass) for a concrete class
     * 
     * @param className
     * @return the DataObjectEntry for the class or null if not found
     */
    public DataObjectEntry getDataObjectEntryForConcreteClass(DataDictionaryIndex index, String className);
    
    
	/**
	 * @return List of businessObject classnames
	 */
    @Deprecated
	public List<String> getBusinessObjectClassNames(DataDictionaryIndex index);

	/**
	 * @param className
	 * @return BusinessObjectEntry for the named class, or null if none exists
	 */
	@Deprecated
	public BusinessObjectEntry getBusinessObjectEntry(DataDictionaryIndex index, String className );

	/**
     * @param className
     * @return DataObjectEntry for the named class, or null if none exists
     */
    public DataObjectEntry getDataObjectEntry(DataDictionaryIndex index, String className );

	/**
	 * @return Map of (classname, BusinessObjectEntry) pairs
	 */
    @Deprecated
	public Map<String, BusinessObjectEntry> getBusinessObjectEntries(DataDictionaryIndex index);
	
	/**
	 * @param className
	 * @return DataDictionaryEntryBase for the named class, or null if none
	 *         exists
	 */
	public DataDictionaryEntry getDictionaryObjectEntry(DataDictionaryIndex index, String className);
	
	/**
	 * Returns the KNS document entry for the given lookup key.  The documentTypeDDKey is interpreted
	 * successively in the following ways until a mapping is found (or none if found):
	 * <ol>
	 * <li>KEW/workflow document type</li>
	 * <li>business object class name</li>
	 * <li>maintainable class name</li>
	 * </ol>
	 * This mapping is compiled when DataDictionary files are parsed on startup (or demand).  Currently this
	 * means the mapping is static, and one-to-one (one KNS document maps directly to one and only
	 * one key).
	 * 
	 * @param documentTypeDDKey the KEW/workflow document type name
	 * @return the KNS DocumentEntry if it exists
	 */
	public DocumentEntry getDocumentEntry(DataDictionaryIndex index, String documentTypeDDKey);

	/**
	 * Note: only MaintenanceDocuments are indexed by businessObject Class
	 * 
	 * This is a special case that is referenced in one location. Do we need
	 * another map for this stuff??
	 * 
	 * @param businessObjectClass
	 * @return DocumentEntry associated with the given Class, or null if there
	 *         is none
	 */
	public MaintenanceDocumentEntry getMaintenanceDocumentEntryForBusinessObjectClass(DataDictionaryIndex index, Class<?> businessObjectClass);
		
	public Map<String, DocumentEntry> getDocumentEntries(DataDictionaryIndex index);

	public Set<InactivationBlockingMetadata> getAllInactivationBlockingMetadatas(DataDictionaryIndex index, Class<?> blockedClass);
	
	/**
	 * Returns mapped document type based on the given document type.
	 * 
	 * @param documentType
	 * @return new document type or null if given documentType was not found.
	 */
	public String getDocumentTypeName(DataDictionaryIndex index, String documentTypeName);
	
	/**
	 * Returns mapped document type class based on the given document type.
	 * 
	 * @param documentType
	 * @return the class of the mapped document type or null if given documentType was not found.
	 */
	//public Class getDocumentTypeClass(String documentTypeName);
	
	/**
	 * Returns the View entry identified by the given id
	 * 
	 * @param index - the view dictionary index
	 * @param viewId - unique id for view
	 * @return View instance associated with the id
	 */
	public View getViewById(UifDictionaryIndex index, String viewId);
	
	/**
	 * Called to retrieve a <code>View</code> instance that is of the given type
	 * based on the index key
	 * 
	 * @param index - the view dictionary index
	 * @param viewTypeName
	 *            - type name for the view
	 * @param indexKey
	 *            - Map of index key parameters, these are the parameters the
	 *            indexer used to index the view initially and needs to identify
	 *            an unique view instance
	 * @return View instance that matches the given index
	 */
	public View getViewByTypeIndex(UifDictionaryIndex index, String viewTypeName, Map<String, String> indexKey);
	
	/**
	 * Gets all <code>View</code> prototypes configured for the given view type
	 * name
	 * 
	 * @param index - the view dictionary index
	 * @param viewTypeName
	 *            - view type name to retrieve
	 * @return List<View> view prototypes with the given type name, or empty
	 *         list
	 */
	public List<View> getViewsForType(UifDictionaryIndex index, String viewTypeName);
}
