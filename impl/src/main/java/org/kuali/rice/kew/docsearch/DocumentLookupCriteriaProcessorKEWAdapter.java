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
package org.kuali.rice.kew.docsearch;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kew.bo.lookup.DocumentRouteHeaderValueLookupableHelperServiceImpl;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This is a description of what this class does - chris don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentLookupCriteriaProcessorKEWAdapter implements
		DocumentLookupCriteriaProcessor {
	DocumentSearchCriteriaProcessor criteriaProcessor;
	
	/**
	 * @param criteriaProcessor the criteriaProcessor to set
	 */
	public void setCriteriaProcessor(
			DocumentSearchCriteriaProcessor criteriaProcessor) {
		this.criteriaProcessor = criteriaProcessor;
	}
	/**
	 * @see org.kuali.rice.kew.docsearch.DocumentLookupCriteriaProcessor#getRows(org.kuali.rice.kns.bo.PersistableBusinessObject)
	 */
	public List<Row> getRows(PersistableBusinessObject documentLookupClass, List<Row> knsRows) {
		List<Row> rows = new ArrayList<Row>();
		
		List<Row> preSearchAttRows = new ArrayList<Row>();
		//if the class has not been overridden, just use the standard lookup fields for standard attributes pre
		if(criteriaProcessor.getClass()==org.kuali.rice.kew.docsearch.StandardDocumentSearchCriteriaProcessor.class) {
			preSearchAttRows = knsRows;
		} else {
			preSearchAttRows = standardPreSearchAttRows();
		}
		
		//search atts
		//TODO: search atts here
		
		//post atts
		//TODO: add this after saved search added in

		return knsRows;
	}
	/**
	 * This method ...
	 * 
	 */
	protected List<Row> standardPreSearchAttRows() {
		List<Row> customPreRows = new ArrayList<Row>();
		List<List<StandardDocSearchCriteriaFieldContainer>> fields = criteriaProcessor.getBasicSearchManager().getColumnsPreSearchAttributes();
		for (List<StandardDocSearchCriteriaFieldContainer> list : fields) {
			

			for (StandardDocSearchCriteriaFieldContainer standardDocSearchCriteriaFieldContainer : list) {
				List<Field>knsFields = new ArrayList<Field>();
				Row row = new Row();
				Field field = new Field();
				//TODO: move this code also handle multiple fields per row when KNS enhancement in
				field.setPropertyName(standardDocSearchCriteriaFieldContainer.getFieldKey());
				field.setBusinessObjectClassName(DocumentRouteHeaderValue.class.getName());
				field.setFieldType(standardDocSearchCriteriaFieldContainer.getFields().get(0).getFieldType());
				knsFields.add(field);
				row.setFields(knsFields);
				customPreRows.add(row);
			}
		}
		return customPreRows;
	}

}
