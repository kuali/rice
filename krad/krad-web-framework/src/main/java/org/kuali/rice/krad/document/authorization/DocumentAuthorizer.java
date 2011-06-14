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
package org.kuali.rice.krad.document.authorization;

import java.util.Set;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.krad.authorization.BusinessObjectAuthorizer;
import org.kuali.rice.krad.document.Document;

/**
 * The DocumentAuthorizer class associated with a given Document is used to
 * dynamically determine what editing mode and what actions are allowed for a
 * given user on a given document instance.
 * 
 * 
 */
public interface DocumentAuthorizer extends BusinessObjectAuthorizer {
	public Set<String> getDocumentActions(Document document, Person user,
			Set<String> documentActions);

	public boolean canInitiate(String documentTypeName, Person user);

	public boolean canOpen(Document document, Person user);

	public boolean canReceiveAdHoc(Document document, Person user,
			String actionRequestCode);
	
	public boolean canAddNoteAttachment(Document document, String attachmentTypeCode, Person user);
	
	public boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode, String createdBySelfOnly, Person user);
	
	public boolean canViewNoteAttachment(Document document, String attachmentTypeCode, Person user);
	
	public boolean canSendAdHocRequests(Document document, String actionRequestCd, Person user);
}
