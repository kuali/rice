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
package edu.iu.uis.eden.exception;

import edu.iu.uis.eden.doctype.DocumentType;

/**
 * An exception which is thrown when a {@link DocumentType} cannot be found.
 * 
 * @see DocumentType
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentTypeNotFoundException extends WorkflowException {

	private static final long serialVersionUID = -3077097373541610208L;

	public DocumentTypeNotFoundException(String s) {
		super(s);
	}

	public DocumentTypeNotFoundException() {
		super();
	}

}
