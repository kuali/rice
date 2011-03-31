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
package org.kuali.rice.kns.uif.helper;

import java.util.Date;

import org.apache.commons.lang.ClassUtils;
import org.kuali.rice.kns.uif.UifConstants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TableToolsHelper {

	public static String constructTableColumnOptions(boolean isSortable, Class dataTypeClass){
		
		String colOptions = "null";
		
		if (!isSortable || dataTypeClass == null){
			colOptions = "{ \"" + UifConstants.TableToolsKeys.SORTABLE + "\" : false } ";
		}else{
			if (ClassUtils.isAssignable(dataTypeClass, String.class)){
				colOptions = "{ \"" + UifConstants.TableToolsKeys.SORT_DATA_TYPE + "\" : \"" + UifConstants.TableToolsValues.DOM_TEXT + "\" } ";
			}else if (ClassUtils.isAssignable(dataTypeClass, Date.class)){
				colOptions = "{ \"" + UifConstants.TableToolsKeys.SORT_DATA_TYPE + "\" : \"" + UifConstants.TableToolsValues.DOM_TEXT + "\" , \"" + UifConstants.TableToolsKeys.SORT_TYPE + "\" : \"" + UifConstants.TableToolsValues.DATE + "\" } ";				
			}else if (ClassUtils.isAssignable(dataTypeClass, Number.class)){
				colOptions = "{ \"" + UifConstants.TableToolsKeys.SORT_DATA_TYPE + "\" : \"" + UifConstants.TableToolsValues.DOM_TEXT + "\" , \"" + UifConstants.TableToolsKeys.SORT_TYPE + "\" : \"" + UifConstants.TableToolsValues.NUMERIC + "\" } ";
			}
		}
		
		return colOptions;
	}
}
