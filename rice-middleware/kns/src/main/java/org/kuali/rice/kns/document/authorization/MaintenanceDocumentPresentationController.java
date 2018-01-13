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

import org.kuali.rice.kns.bo.authorization.InquiryOrMaintenanceDocumentPresentationController;
import org.kuali.rice.kns.document.MaintenanceDocument;

import java.util.Set;

/**
 * @deprecated Use {@link org.kuali.rice.krad.maintenance.MaintenanceDocumentPresentationController}.
 */
@Deprecated
public interface MaintenanceDocumentPresentationController extends InquiryOrMaintenanceDocumentPresentationController,
        org.kuali.rice.krad.maintenance.MaintenanceDocumentPresentationController {

    @Override
	public boolean canCreate(Class boClass);

	public Set<String> getConditionallyReadOnlyPropertyNames(
			MaintenanceDocument document);

	public Set<String> getConditionallyReadOnlySectionIds(
			MaintenanceDocument document);

	public Set<String> getConditionallyRequiredPropertyNames(
			MaintenanceDocument document);
}
