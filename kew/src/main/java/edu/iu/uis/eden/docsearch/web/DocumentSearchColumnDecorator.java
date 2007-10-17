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
package edu.iu.uis.eden.docsearch.web;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;

import edu.iu.uis.eden.EdenConstants;

/**
 * A ColumnDecorator for columns in Document Search results which adds an extra 
 * non-breaking space if the column is an empty String.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentSearchColumnDecorator implements DisplaytagColumnDecorator {

	public Object decorate(Object columnValue, PageContext pageContext, MediaTypeEnum media) throws DecoratorException {
		if (columnValue == null) {
			return EdenConstants.HTML_NON_BREAKING_SPACE;
		}
		if (columnValue instanceof String && StringUtils.isEmpty(((String)columnValue).trim())) {
			return EdenConstants.HTML_NON_BREAKING_SPACE;
		}
		return columnValue;
	}

}
