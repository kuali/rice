/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kns.document.authorization;

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.authorization.BusinessObjectAuthorizer;
import org.kuali.rice.krad.document.Document;

import java.util.Set;

/**
 * The DocumentAuthorizer class associated with a given Document is used to
 * dynamically determine what editing mode and what actions are allowed for a
 * given user on a given document instance.
 * 
 * @deprecated Use {@link org.kuali.rice.krad.document.DocumentAuthorizer}.
 */
@Deprecated
public interface DocumentAuthorizer extends BusinessObjectAuthorizer, org.kuali.rice.krad.document.DocumentAuthorizer {
	public Set<String> getDocumentActions(Document document, Person user,
			Set<String> documentActions);

    @Override
	public boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode, String createdBySelfOnly, Person user);
	
	public boolean canViewNoteAttachment(Document document, String attachmentTypeCode, Person user);
}
