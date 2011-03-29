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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.control.TextControl;
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.field.GroupField;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TableToolsHelper {

	public static String constructTableColumnOptions(boolean isSortable, Component component){
		String colOptions = "null";
		
		if (!isSortable){
			colOptions = "{ \"" + UifConstants.TableToolsKeys.SORTABLE + "\" : false } ";
		}else if (component instanceof AttributeField){
			AttributeField field = (AttributeField)component;
			
			if ( field.getControl() instanceof TextControl && StringUtils.equals(field.getDescription(),"Travel Account Number")){
				colOptions = "{ \"" + UifConstants.TableToolsKeys.SORT_DATA_TYPE + "\" : \"" + UifConstants.TableToolsValues.DOM_TEXT + "\" , \"" + UifConstants.TableToolsKeys.SORT_TYPE + "\" : \"" + UifConstants.TableToolsValues.NUMERIC + "\" } ";
			}else if ( field.getControl() instanceof TextControl && ((TextControl)field.getControl()).getDatePicker() != null){
				colOptions = "{ \"" + UifConstants.TableToolsKeys.SORT_DATA_TYPE + "\" : \"" + UifConstants.TableToolsValues.DOM_TEXT + "\" , \"" + UifConstants.TableToolsKeys.SORT_TYPE + "\" : \"" + UifConstants.TableToolsValues.DATE + "\" } ";
			}else if ( field.getControl() instanceof TextControl){
				colOptions = "{ \"" + UifConstants.TableToolsKeys.SORT_DATA_TYPE + "\" : \"" + UifConstants.TableToolsValues.DOM_TEXT + "\" } ";
			}
		}else if (component instanceof GroupField){
			colOptions = "{ \"" + UifConstants.TableToolsKeys.SORT_DATA_TYPE + "\" : \"" + UifConstants.TableToolsValues.DOM_TEXT + "\" } ";
		}
		
		return colOptions;
	}
}
