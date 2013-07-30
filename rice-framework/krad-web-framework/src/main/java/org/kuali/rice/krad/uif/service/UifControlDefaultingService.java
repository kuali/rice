/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.service;

import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.uif.control.Control;

/**
 * This service helps build/define default controls for the UIF based on the associated data-level metadata.
 * 
 * It will use the information provided by the krad-data module to attempt to build sensible
 * default controls based on the data type, maximum length, and other attributes available
 * in the ORM-level metadata or provided as annotations on the data object classes. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface UifControlDefaultingService {

    Control deriveControlAttributeFromMetadata( AttributeDefinition attrDef );
}
